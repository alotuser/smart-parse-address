package com.alotuser.address.core;

import java.util.Comparator;
import java.util.List;

import com.alotuser.address.assets.Address;
import com.alotuser.address.assets.AddressInfo;
import com.alotuser.address.assets.UserInfo;
import com.alotuser.address.data.AddressDataLoader;
import com.alotuser.address.data.LocalAddressDataLoader;

import cn.alotus.core.collection.CollUtil;
import cn.alotus.core.collection.ListUtil;
import cn.alotus.core.text.StrSplitter;
import cn.alotus.core.util.CharUtil;
import cn.alotus.core.util.StrUtil;

/**
 * 地址解析入口类
 * 封装手机号提取、姓名拆分、省市区街道匹配完整流程
 * 支持自定义地址数据源加载器，可指定地址匹配层级
 * @author I6view
 */
public class SmartParse {

    /** 地址数据加载器，提供全量省市区街道基础数据 */
    private final AddressDataLoader addressDataLoader;

    /** 地址层级匹配核心处理器 */
    private SmartMatch smartMatch;

    /**
     * 无参构造，默认使用本地JSON地址库加载器
     */
    public SmartParse() {
        smartMatch = new SmartMatch();
        this.addressDataLoader = new LocalAddressDataLoader();
    }

    /**
     * 自定义地址数据源构造器
     * @param addressDataLoader 自定义地址数据加载实现（本地/远程/内存等）
     */
    public SmartParse(AddressDataLoader addressDataLoader) {
        smartMatch = new SmartMatch();
        this.addressDataLoader = addressDataLoader;
    }

    /**
     * 解析文本，提取用户完整信息（姓名+手机号+省市区详细地址）
     * 默认匹配完整四级地址（省/市/区县/街道）
     * @param text 原始收件地址文本
     * @return 封装姓名、手机号、地址层级的UserInfo实体
     */
    public UserInfo parseUserInfo(String text) {
        return parseUserInfo(text, null);
    }

    /**
     * 解析文本，提取用户完整信息，可限制地址匹配层级
     * @param text 原始收件地址文本
     * @param level 地址匹配层级 0=仅省 1=省市 2=省市区 null=完整四级
     * @return 封装姓名、手机号、地址层级的UserInfo实体
     */
    public UserInfo parseUserInfo(String text, Integer level) {
        // 空文本直接返回
        if (StrUtil.isBlank(text)) {
            return null;
        }

        // 移除固定冗余前缀文字
        text = text.replace(" 详细地址: ", StrUtil.EMPTY);
        StringBuilder matchText = new StringBuilder();

        // 按换行、回车切割多行文本
        for (String str : StrSplitter.splitByRegex(text, "[\\n\\r]", 0, true, true)) {
            // 统一 "： " 分隔符格式
            str = str.replace(": ", ":");
            if (StrUtil.isBlank(str)) {
                continue;
            }
            // 按中文逗号、空格再次拆分片段
            for (String s : StrSplitter.splitByRegex(str, "，| ", 0, true, true)) {
                // 按冒号/全角冒号拆分键值对
                List<String> strings = StrSplitter.splitByRegex(s, "[:：]", 0, true, true);
                if (CollUtil.isEmpty(strings)) {
                    continue;
                }
                // 键值对取后半段地址内容，无冒号则取整段
                if (strings.size() == 2) {
                    matchText.append(strings.get(1));
                } else {
                    matchText.append(strings.get(0));
                }
                matchText.append(" ");
            }
        }
        // 拼接清洗后的待匹配文本
        text = matchText.toString();
        UserInfo userInfo = new UserInfo();

        // 提取手机号、固话、港澳台号码
        String mobile = SmartMatch.matchMobile(text);
        List<String> nameAddress = null;

        // 存在手机号：分割文本，前半为姓名，后半为地址
        if (StrUtil.isNotEmpty(mobile)) {
            userInfo.setMobile(mobile);
            nameAddress = StrUtil.split(StrUtil.removeAll(text, CharUtil.SPACE), mobile);
        } else {
            // 无手机号，整段作为地址候选
            nameAddress = ListUtil.of(text);
        }

        // 加载全量地址库数据
        List<Address> addressList = addressDataLoader.loadData();
        if (nameAddress != null) {
            ComparatorList comparatorList = new ComparatorList(nameAddress);
            // 最短文本=姓名，最长文本=详细地址
            String name = comparatorList.getMin();
            String address = comparatorList.getMax();
            // 匹配省市区街道层级信息
            AddressInfo addressInfo = smartMatch.matchAddress(addressList, address, level);

            // 多段文本 或 未匹配到地址时填充姓名
            if (nameAddress.size() != 1 || addressInfo.isEmpty()) {
                userInfo.setName(name);
            }
            // 无匹配地址时清空详细地址字段
            if (addressInfo.isEmpty()) {
                addressInfo.setAddress(null);
            }
            userInfo.setAddressInfo(addressInfo);
        }

        return userInfo;
    }

    /**
     * 仅解析省市区街道地址信息，默认完整四级匹配
     * @param text 原始地址文本
     * @return 省/市/区县/街道拆分实体
     */
    public AddressInfo parseAddressInfo(String text) {
        return parseAddressInfo(text, null);
    }

    /**
     * 仅解析省市区街道地址信息，可限制匹配层级
     * @param text 原始地址文本
     * @param level 匹配级别。从0开始，可以选择只匹配到第几级，为null则忽略
     * @return 省/市/区县/街道拆分实体
     */
    public AddressInfo parseAddressInfo(String text, Integer level) {
        if (StrUtil.isBlank(text)) {
            return null;
        }

        List<Address> addressList = addressDataLoader.loadData();
        AddressInfo addressInfo = new AddressInfo();

        // 空格拆分多段地址，逐段匹配合并结果
        List<String> split = StrUtil.split(text, " ");
        for (String str : split) {
            AddressInfo info = smartMatch.matchAddress(addressList, str, level);
            if (info != null && !info.isEmpty()) {
                addressInfo.setAddressInfo(info);
            }
        }
        return addressInfo;
    }

    /**
     * 字符串长度比较工具内部类
     * 用于区分短文本(姓名)、长文本(详细地址)
     * @author I6view
     */
    class ComparatorList {
        /** 待比较文本集合 */
        private List<String> list;

        public ComparatorList(List<String> list) {
            this.list = list;
        }

        /** 按字符串长度升序比较器 */
        private final Comparator<String> lengthComparator = new Comparator<String>() {
            @Override
            public int compare(String o1, String o2) {
                return StrUtil.length(o1) - StrUtil.length(o2);
            }
        };

        /**
         * 获取集合内最长字符串（详细地址）
         * @return 最长文本
         */
        public String getMax() {
            return list.stream().max(lengthComparator).get();
        }

        /**
         * 获取集合内最短字符串（姓名）
         * @return 最短文本
         */
        public String getMin() {
            return list.stream().min(lengthComparator).get();
        }
    }
}