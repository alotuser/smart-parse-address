package com.alotuser.address;

import com.alotuser.address.assets.UserInfo;
import com.alotuser.address.util.JsonUtil;


class SmartParseTest {

	public static void main(String[] args) {
		
		String textA1 = "收货人: 李丽 手机号码: 13166666666 所在地区: 上海市黄浦区 详细地址: 南苏州路333号中国东方航空公司\n";
		UserInfo userInfoA1 = SmartAgent.parseUserAddressString(textA1).getUserInfo();
		System.out.println(JsonUtil.toJsonStr(userInfoA1));

		String textA2 = "收货人: 李丽 手机号码: 13166666666 地址: 上海市黄浦区 详细地址: 南苏州路333号中国东方航空公司";
		UserInfo userInfoA2 = SmartAgent.parseUserAddressString(textA2).getUserInfo();
		System.out.println(JsonUtil.toJsonStr(userInfoA2));

		String textA3 = "姓名: 李丽 电话: 13166666666 地址: 上海市黄浦区 详细地址: 南苏州路333号中国东方航空公司";
		UserInfo userInfoA3 = SmartAgent.parseUserAddressString(textA3).getUserInfo();
		System.out.println(JsonUtil.toJsonStr(userInfoA3));

		String textB1 = "李丽13166666666上海市黄浦区南苏州路333号中国东方航空公司";
		UserInfo userInfoB1 = SmartAgent.parseUserAddressString(textB1).getUserInfo();
		System.out.println(JsonUtil.toJsonStr(userInfoB1));

		String textB2 = "上海市黄浦区南苏州路333号中国东方航空公司13166666666李丽";
		UserInfo userInfoB2 = SmartAgent.parseUserAddressString(textB2).getUserInfo();
		System.out.println(JsonUtil.toJsonStr(userInfoB2));

		String textC1 = "上海市黄浦区南苏州路333号中国东方航空公司";
		UserInfo userInfoC1 = SmartAgent.parseUserAddressString(textC1).getUserInfo();
		System.out.println(JsonUtil.toJsonStr(userInfoC1));

	}

}
