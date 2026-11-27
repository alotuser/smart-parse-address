# smart-parse-address
## 智能解析收货地址

### 应用背景：
	可以解析出文本中的收货人姓名、联系方式、邮编和详细地址。并且可以将地址拆分出省市区（自治区、旗、盟等），方便电商和物流等项目应用。
### Maven项目的pom.xml的dependencies中加入以下内容:
``` xml
<dependency>
    <groupId>com.github.alotuser</groupId>
    <artifactId>smart-parse-address</artifactId>
    <version>1.8.1</version>
</dependency>
```
### demo代码演示
``` java
String textA1 = "收货人: 李丽 手机号码: 13166666666 所在地区: 上海市黄浦区 详细地址: 南苏州路333号中国东方航空公司\n";
UserInfo userInfoA1 = SmartAgent.parseUserAddressString(textA1).getUserInfo();
System.out.println(JSONUtil.toJsonStr(userInfoA1));

String textA2 = "收货人: 李丽 手机号码: 13166666666 地址: 上海市黄浦区 详细地址: 南苏州路333号中国东方航空公司";
UserInfo userInfoA2 = SmartAgent.parseUserAddressString(textA2).getUserInfo();
System.out.println(JSONUtil.toJsonStr(userInfoA2));

String textA3 = "姓名: 李丽 电话: 13166666666 地址: 上海市黄浦区 详细地址: 南苏州路333号中国东方航空公司";
UserInfo userInfoA3 = SmartAgent.parseUserAddressString(textA3).getUserInfo();
System.out.println(JSONUtil.toJsonStr(userInfoA3));

String textB1 = "李丽13166666666上海市黄浦区南苏州路333号中国东方航空公司";
UserInfo userInfoB1 = SmartAgent.parseUserAddressString(textB1).getUserInfo();
System.out.println(JSONUtil.toJsonStr(userInfoB1));

String textB2 = "上海市黄浦区南苏州路333号中国东方航空公司13166666666李丽";
UserInfo userInfoB2 = SmartAgent.parseUserAddressString(textB2).getUserInfo();
System.out.println(JSONUtil.toJsonStr(userInfoB2));

String textC1 = "上海市黄浦区南苏州路333号中国东方航空公司";
UserInfo userInfoC1 = SmartAgent.parseUserAddressString(textC1).getUserInfo();
System.out.println(JSONUtil.toJsonStr(userInfoC1));
```
### 地址数据源切换
	默认读取 classpath 下的 resources/areaData.json，无需任何配置。数据放在本地其他文件或远程服务上时，解析前先调用 SmartAgent.setGlobalAddressDataLoader(AddressDataLoader) 全局指定数据源，之后所有 new SmartAgent(...) / SmartAgent.parseUserAddressString(...) 都复用该加载器，数据只在首次加载一次。
``` java
// 本地指定文件：写 file: URL，Windows 绝对路径、相对路径都可用（反斜杠在 Java 字符串里要转义成 \\）
// 远程数据源直接写 http/https 地址；不设置则默认读 classpath 下的 areaData.json
String url = "file:D:\\tg\\areaData.json";
SmartAgent.setGlobalAddressDataLoader(new UrlAddressDataLoader(url));

// 手上的纯路径（不带 file:）先转成 URL 再传
String localUrl = new File("D:\\tg\\areaData.json").toURI().toString();

// 远程服务
String remoteUrl = "http://127.0.0.1:8080/areaData.json";
SmartAgent.setGlobalAddressDataLoader(new UrlAddressDataLoader(remoteUrl));

// 数据库、内存等自定义数据源，实现 AddressDataLoader 即可
SmartAgent.setGlobalAddressDataLoader(new AddressDataLoader() {
    @Override
    public List<Address> loadData() {
        return queryAreaTreeFromDb();
    }
});
```
	几点约定：一是设置只影响之后创建的 SmartAgent，需在解析前调用；二是入参为 null 会抛 IllegalArgumentException("AddressDataLoader 不能为null")；三是数据文件内容更新后，需先 SmartAgent.clearAllAddressCache() 再重新 setGlobalAddressDataLoader(...)，否则拿到的还是缓存里的旧数据；四是可随时用 SmartAgent.getGlobalAddressDataLoader() 查看当前生效的加载器。

#### 全局与单次（实例级）两种用法
	全局方式适合整个应用共用一份地址库；如果不同业务要用不同数据源，也可以在创建实例时单独指定，只影响当前这一个实例。

| 用法 | 数据源 | 生效范围 |
| --- | --- | --- |
| SmartAgent.parseUserAddressString(text) / new SmartAgent(text) | 全局加载器（默认 classpath 下的 areaData.json） | 所有实例 |
| SmartAgent.setGlobalAddressDataLoader(loader) | 任意 AddressDataLoader | 设置之后创建的所有实例 |
| new SmartAgent(text, AddressDataLoader loader) | 自定义加载器（数据库、内存、文件等） | 当前实例 |
| new SmartAgent(text, URL dataUrl) | file/http/https 形式的 URL | 当前实例 |
| new SmartAgent(text, String urlStr) | 同上，内部按 URL 解析（纯路径需先转 URL） | 当前实例 |
| SmartAgent.parseUserAddressString(text, loader / dataUrl / urlStr) | 同上三种，静态工厂形式 | 当前实例 |

``` java
String text = "收货人: 李丽 手机号码: 13166666666 所在地区: 上海市黄浦区 详细地址: 南苏州路333号中国东方航空公司";

// 1. 构造器 + URL 对象
UserInfo u1 = new SmartAgent(text, new URL("file:/D:/tg/areaData.json")).getUserInfo();

// 2. 构造器 + URL 字符串（纯路径 "D:\\tg\\areaData.json" 不被接受，需先 new File(path).toURI().toString()）
UserInfo u2 = new SmartAgent(text, "file:/D:/tg/areaData.json").getUserInfo();
UserInfo u2b = new SmartAgent(text, new File("D:\\tg\\areaData.json").toURI().toString()).getUserInfo();

// 3. 构造器 + 自定义加载器
UserInfo u3 = new SmartAgent(text, new UrlAddressDataLoader("file:D:\\tg\\areaData.json")).getUserInfo();

// 4. 静态工厂的三种重载
UserInfo u4 = SmartAgent.parseUserAddressString(text, new URL("file:/D:/tg/areaData.json")).getUserInfo();
UserInfo u5 = SmartAgent.parseUserAddressString(text, "file:/D:/tg/areaData.json").getUserInfo();
UserInfo u6 = SmartAgent.parseUserAddressString(text, new UrlAddressDataLoader("file:/D:/tg/areaData.json")).getUserInfo();
```
	实例级指定 URL 时也会走全局 URL 缓存：同一个 URL 无论创建多少个 SmartAgent，数据只加载一次（缓存按 URL 字符串共享）。实例级指定的数据源不影响 SmartAgent.getGlobalAddressDataLoader()，两者可以同时使用。

### 地区树形同步工具
	https://github.com/alotuser/generate-area-data
