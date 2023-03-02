package com.alotuser.address;

import com.alotuser.address.assets.UserInfo;

import cn.hutool.json.JSONUtil;

class SmartParseTest {

	public static void main(String[] args) {
		SmartParse smartParse = new SmartParse();

		String textA1 = "收货人: 李丽 手机号码: 13166666666 所在地区: 上海市黄浦区 详细地址: 南苏州路333号中国东方航空公司\n";
		UserInfo userInfoA1 = smartParse.parseUserInfo(textA1);
		System.out.println(JSONUtil.toJsonStr(userInfoA1));

		String textA2 = "收货人: 李丽 手机号码: 13166666666 地址: 上海市黄浦区 详细地址: 南苏州路333号中国东方航空公司";
		UserInfo userInfoA2 = smartParse.parseUserInfo(textA2);
		System.out.println(JSONUtil.toJsonStr(userInfoA2));

		String textA3 = "姓名: 李丽 电话: 13166666666 地址: 上海市黄浦区 详细地址: 南苏州路333号中国东方航空公司";
		UserInfo userInfoA3 = smartParse.parseUserInfo(textA3);
		System.out.println(JSONUtil.toJsonStr(userInfoA3));

		String textB1 = "李丽13166666666上海市黄浦区南苏州路333号中国东方航空公司";
		UserInfo userInfoB1 = smartParse.parseUserInfo(textB1);
		System.out.println(JSONUtil.toJsonStr(userInfoB1));

		String textB2 = "上海市黄浦区南苏州路333号中国东方航空公司13166666666李丽";
		UserInfo userInfoB2 = smartParse.parseUserInfo(textB2);
		System.out.println(JSONUtil.toJsonStr(userInfoB2));

		String textC1 = "上海市黄浦区南苏州路333号中国东方航空公司";
		UserInfo userInfoC1 = smartParse.parseUserInfo(textC1);
		System.out.println(JSONUtil.toJsonStr(userInfoC1));

	}

}
