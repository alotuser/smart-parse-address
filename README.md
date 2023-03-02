# smart-parse-address
## 智能解析收货地址

### 应用背景：
	可以解析出文本中的收货人姓名、联系方式、邮编和详细地址。并且可以将地址拆分出省市区（自治区、旗、盟等），方便电商和物流等项目应用。
### Maven项目的pom.xml的dependencies中加入以下内容:
``` xml
<dependency>
    <groupId>com.github.alotuser</groupId>
    <artifactId>smart-parse-address</artifactId>
    <version>1.0.0</version>
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


