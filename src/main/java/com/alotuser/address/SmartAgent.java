package com.alotuser.address;

import java.io.Serializable;
import java.net.URL;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import com.alotuser.address.assets.Address;
import com.alotuser.address.assets.AddressInfo;
import com.alotuser.address.assets.UserInfo;
import com.alotuser.address.core.SmartParse;
import com.alotuser.address.data.AddressDataLoader;
import com.alotuser.address.data.LocalAddressDataLoader;
import com.alotuser.address.data.UrlAddressDataLoader;

/**
 * 地址解析入口工具类 SmartAgent
 * 内置全局URL维度地址数据缓存，相同URL资源仅加载一次，避免重复IO/网络请求
 * 支持多种数据源：本地默认json、自定义文件URL、远程HTTP URL、自定义加载器实现
 *
 * @author I6view
 */
public class SmartAgent implements Serializable {

    private static final long serialVersionUID = 1L;

    /** SmartAgent 静态池 */
    private static final ConcurrentHashMap<String, UrlAddressDataLoader> LOADER_POOL = new ConcurrentHashMap<>();

    /**
     * 全局地址数据缓存池
     * key：URL完整字符串，解决多次new URL对象equals对比损耗问题
     * value：解析完成的地址列表
     * ConcurrentHashMap 保证多线程并发安全
     */
    private static final ConcurrentHashMap<String, List<Address>> URL_DATA_CACHE = new ConcurrentHashMap<>();

    /** 待解析的原始地址文本 */
    private String userAddressString;

    /** 地址解析处理器，内置带缓存逻辑的数据源加载器 */
    private SmartParse smartParse;

    /** 全局默认地址数据加载器，DCL双重检查锁单例 */
    private static volatile AddressDataLoader globalAddressDataLoader = null;

    // ===================== 全局加载器配置 API =====================
    /**
     * 全局统一设置自定义地址数据源加载器
     * 全局生效：所有无参构造/无URL参数创建的SmartAgent均复用该加载器
     *
     * <p>使用示例：
     * <pre>{@code
     * // 自定义固定内存数据
     * List<Address> memoryAddressData = loadFromDb();
     * SmartAgent.setGlobalAddressDataLoader(new AddressDataLoader() {
     *     @Override
     *     public List<Address> loadData() {
     *         return memoryAddressData;
     *     }
     * });
     *
     * }</pre>
     *
     * @param customLoader 自定义数据源加载实现（支持本地文件、远程URL、数据库、内存静态数据等）
     * @throws IllegalArgumentException 入参为null时抛出非法参数异常
     */
    public static void setGlobalAddressDataLoader(AddressDataLoader customLoader) {
        if (customLoader == null) {
            throw new IllegalArgumentException("AddressDataLoader 不能为null");
        }
        globalAddressDataLoader = customLoader;
    }

    /**
     * 获取全局默认地址数据加载器
     * 未手动设置时，自动初始化 LocalAddressDataLoader（读取resources/areaData.json）
     * @return 全局共享的地址数据加载器
     */
    public static AddressDataLoader getGlobalAddressDataLoader() {
        if (globalAddressDataLoader == null) {
            synchronized (SmartAgent.class) {
                if (globalAddressDataLoader == null) {
                    globalAddressDataLoader = new LocalAddressDataLoader();
                }
            }
        }
        return globalAddressDataLoader;
    }

    // ===================== 构造器重载 =====================
    /**
     * 使用全局默认加载器创建地址解析实例
     * @param userAddressString 待解析的原始地址字符串
     */
    public SmartAgent(String userAddressString) {
        this(userAddressString, getGlobalAddressDataLoader());
    }

    /**
     * 自定义数据源加载器创建解析实例（当前实例独立使用该加载器）
     * @param userAddressString 待解析原始地址
     * @param customLoader 自定义地址数据加载器
     * @throws IllegalArgumentException 加载器为null时抛出
     */
    public SmartAgent(String userAddressString, AddressDataLoader customLoader) {
        if (customLoader == null) {
            throw new IllegalArgumentException("AddressDataLoader 不能为空");
        }
        this.userAddressString = userAddressString;
        // 包装缓存逻辑，传入解析器
        this.smartParse = new SmartParse(wrapLoaderWithCache(customLoader));
    }

    /**
     * 通过URL对象指定数据源创建解析实例
     * @param userAddressString 待解析原始地址
     * @param dataUrl 资源URL（本地文件/远程http地址）
     */
    public SmartAgent(String userAddressString, URL dataUrl) {
        this(userAddressString, getCachedUrlLoader(dataUrl));
    }

    /**
     * 通过URL字符串指定数据源创建解析实例
     * @param userAddressString 待解析原始地址
     * @param urlStr 资源地址字符串（file/http/https）
     */
    public SmartAgent(String userAddressString, String urlStr) {
        this(userAddressString, getCachedUrlLoader(urlStr));
    }

    // ===================== 缓存包装核心方法 =====================
    private static UrlAddressDataLoader getCachedUrlLoader(String urlStr) {
        return LOADER_POOL.computeIfAbsent(urlStr, k -> new UrlAddressDataLoader(k));
    }

    private static UrlAddressDataLoader getCachedUrlLoader(URL url) {
        String key = url.toString();
        return LOADER_POOL.computeIfAbsent(key, k -> new UrlAddressDataLoader(url));
    }
    /**
     * 为原始加载器包装缓存逻辑增强
     * 仅对UrlAddressDataLoader开启URL缓存，其他自定义加载器直接原生加载不缓存
     * @param loader 原始数据源加载器
     * @return 包装缓存能力的代理加载器
     */
    private AddressDataLoader wrapLoaderWithCache(AddressDataLoader loader) {
        return new AddressDataLoader() {
            /**
             * 走缓存读取地址数据
             */
            @Override
            public List<Address> loadData() {
                URL url;
                // 仅URL类型加载器启用缓存
                if (loader instanceof UrlAddressDataLoader) {
                    url = ((UrlAddressDataLoader) loader).getUrl();
                } else {
                    // 非URL加载器，直接读取不缓存
                    return loader.loadData();
                }
                // 使用URL字符串作为缓存key，规避多次new URL对象匹配失效问题
                String cacheKey = url.toString();
                // 缓存存在直接返回，不存在则加载并存入缓存
                return URL_DATA_CACHE.computeIfAbsent(cacheKey, k -> loader.loadData());
            }

            /**
             * 直接调用原始加载器的URL重载方法，不参与全局缓存
             */
            @Override
            public List<Address> loadData(URL url) {
                return loader.loadData(url);
            }
        };
    }

    // ===================== 对外解析业务方法 =====================
    /**
     * 解析地址文本，获取省市区三级地址信息
     * @return 地址拆分信息 AddressInfo
     */
    public AddressInfo getAddressInfo() {
        return smartParse.parseAddressInfo(userAddressString);
    }

    /**
     * 解析地址文本，提取姓名、手机号、详细地址等用户信息
     * @return 用户信息 UserInfo
     */
    public UserInfo getUserInfo() {
        return smartParse.parseUserInfo(userAddressString);
    }

    // ===================== 静态工厂重载，快速创建实例 =====================
    /**
     * 快速创建解析实例，使用全局默认数据源
     * @param userAddressString 待解析地址
     * @return SmartAgent解析对象
     */
    public static SmartAgent parseUserAddressString(String userAddressString) {
        return new SmartAgent(userAddressString);
    }

    /**
     * 快速创建解析实例，自定义数据源加载器
     * @param userAddressString 待解析地址
     * @param loader 自定义加载器
     * @return SmartAgent解析对象
     */
    public static SmartAgent parseUserAddressString(String userAddressString, AddressDataLoader loader) {
        return new SmartAgent(userAddressString, loader);
    }

    /**
     * 快速创建解析实例，传入URL对象作为数据源
     * @param userAddressString 待解析地址
     * @param dataUrl 资源URL
     * @return SmartAgent解析对象
     */
    public static SmartAgent parseUserAddressString(String userAddressString, URL dataUrl) {
        return new SmartAgent(userAddressString, dataUrl);
    }

    /**
     * 快速创建解析实例，传入URL字符串作为数据源
     * @param userAddressString 待解析地址
     * @param urlStr 资源地址字符串
     * @return SmartAgent解析对象
     */
    public static SmartAgent parseUserAddressString(String userAddressString, String urlStr) {
        return new SmartAgent(userAddressString, urlStr);
    }

    // ===================== 缓存操作工具方法 =====================
    /**
     * 清空全局所有URL地址缓存
     * 适用于数据源文件更新、需要全量刷新场景
     */
    public static void clearAllAddressCache() {
        URL_DATA_CACHE.clear();
    }

    /**
     * 删除指定URL对应的缓存，下次使用该URL会重新加载数据
     * @param url 目标资源URL对象
     */
    public static void removeAddressCache(URL url) {
        if (url != null) {
            URL_DATA_CACHE.remove(url.toString());
        }
    }

    /**
     * 根据URL字符串删除对应缓存
     * @param urlStr 目标资源URL字符串
     */
    public static void removeAddressCache(String urlStr) {
        if (urlStr != null) {
            URL_DATA_CACHE.remove(urlStr);
        }
    }

}