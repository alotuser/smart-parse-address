package com.alotuser.address;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.regex.Pattern;

import com.alotuser.address.assets.Address;
import com.alotuser.address.assets.AddressInfo;
import com.alotuser.address.assets.MatchAddress;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.lang.RegexPool;
import cn.hutool.core.util.ReUtil;
import cn.hutool.core.util.StrUtil;
/**
 * 匹配地址类
 * @author I6view
 *
 */
public class SmartMatch {

	
	/**
	 * 前缀字符特殊处理，匹配时候会自动处理掉符合正则的文字
	 */
	private Pattern pattern = Pattern.compile("^[省市区县州街道镇乡特别行政自治]+");

	/**
	 * 匹配手机号码
	 *
	 * @param  text    地址信息
	 * @return String
	 */
	public static String matchMobile(String text) {
		String mobile = ReUtil.getGroup0(RegexPool.MOBILE, text);
		if (StrUtil.isNotEmpty(mobile)) {
			return mobile;
		}

		mobile = ReUtil.getGroup0(RegexPool.TEL, text);
		if (StrUtil.isNotEmpty(mobile)) {
			return mobile;
		}

		mobile = ReUtil.getGroup0(RegexPool.TEL_400_800, text);
		if (StrUtil.isNotEmpty(mobile)) {
			return mobile;
		}
		mobile = ReUtil.getGroup0(RegexPool.MOBILE_HK, text);
		if (StrUtil.isNotEmpty(mobile)) {
			return mobile;
		}
		mobile = ReUtil.getGroup0(RegexPool.MOBILE_TW, text);
		if (StrUtil.isNotEmpty(mobile)) {
			return mobile;
		}
		return ReUtil.getGroup0(RegexPool.MOBILE_MO, text);
	}

	/**
	 * filterStr
	 * @param text 地址信息
	 * @return
	 */
	public static String filterStr(String text) {
		text = ReUtil.replaceAll(text, "[`~!@#$^&*=|{}':;',.<>/?~！@#￥……&*——|‘；：”“’。，、？-]", " ");
		return text.replace("\r", "").replace("\n", "");
	}
	
	
	/**
	 * 匹配地址
	 *
	 * @param addressList 地址列表
	 * @param text        匹配的地址信息
	 * @param level       匹配级别。从0开始，可以选择只匹配到第几级，为null则忽略
	 */
	AddressInfo matchAddress(List<Address> addressList, String text, Integer level) {

		if (StrUtil.isBlank(text)) {
			return null;
		}
		AddressInfo info = new AddressInfo();
		// 清除特殊字符
		text = ReUtil.replaceAll(text, "[^\u4e00-\u9fa5A-Za-z0-9-]", "");

		String address = text;

		String matchAddressStr = "";
		List<MatchAddress> matchProvince = new ArrayList<>();
		for (int endIndex = 0; endIndex < text.length(); endIndex++) {
			matchAddressStr = StrUtil.subWithLength(text, 0, endIndex + 2);
			for (Address province : addressList) {
				if (province.getName().contains(matchAddressStr)) {
					matchProvince.add(new MatchAddress(province, null, null, null, matchAddressStr));
				}
			}
		}

		if (!matchProvince.isEmpty()) {
			MatchAddress matchAddress = getBestMatch(matchProvince);
			setMatchAddressInfo(info, matchAddress);
			text = text.replaceFirst(matchAddress.getMatchValue(), "");
			text = ReUtil.replaceFirst(pattern, text, "");
		}
		if (level != null && level == 0) {
			setAddress(matchProvince, address, text, info);
			return info;
		}

		// 市查找
		List<MatchAddress> matchCity = new ArrayList<>(); // 粗略匹配上的市
		for (int endIndex = 0; endIndex < text.length(); endIndex++) {
			matchAddressStr = StrUtil.subWithLength(text, 0, endIndex + 2);
			for (Address province : addressList) {
				if (province.getChildren() == null) {
					continue;
				}
				if (info.getProvince() == null || province.getName().equals(info.getProvince())) {
					for (Address city : province.getChildren()) {
						if (city.getName().contains(matchAddressStr)) {
							matchCity.add(new MatchAddress(province, city, null, null, matchAddressStr));
						}
					}
				}
			}
		}
		if (!matchCity.isEmpty()) {
			MatchAddress matchAddress = getBestMatch(matchCity);
			setMatchAddressInfo(info, matchAddress);
			text = text.replaceFirst(matchAddress.getMatchValue(), "");
			// 如果是市开头的，去掉
			text = ReUtil.replaceFirst(pattern, text, "");
		}

		if (level != null && level == 1) {
			setAddress(matchProvince, address, text, info);
			return info;
		}

		// 区县查找
		List<MatchAddress> matchCounty = new ArrayList<>(); // 粗略匹配上的区县
		for (int endIndex = 0; endIndex < text.length(); endIndex++) {
			matchAddressStr = StrUtil.subWithLength(text, 0, endIndex + 2);

			for (Address province : addressList) {
				if (province.getChildren() == null) {
					continue;
				}
				if (info.getProvince() != null && !info.getProvince().equals(province.getName())) {
					continue;
				}
				for (Address city : province.getChildren()) {// 市
					if (CollUtil.isEmpty(city.getChildren())) {
						continue;
					}
					if (info.getCity() != null && !info.getCity().equals(city.getName())) {
						continue;
					}
					for (Address county : city.getChildren()) { // 区
						if (county.getName().contains(matchAddressStr)) {
							matchCounty.add(new MatchAddress(province, city, county, null, matchAddressStr));
						}
					}
				}
			}
		}
		if (!matchCounty.isEmpty()) {
			MatchAddress matchAddress = getBestMatch(matchCounty);
			setMatchAddressInfo(info, matchAddress);
			text = text.replaceFirst(matchAddress.getMatchValue(), "");
			text = ReUtil.replaceFirst(pattern, text, "");
		}

		if (level != null && level == 2) {
			setAddress(matchProvince, address, text, info);
			return info;
		}

		// 街道查找
		List<MatchAddress> matchStreet = new ArrayList<>(); // 粗略匹配上的街道查
		for (int endIndex = 0; endIndex < text.length(); endIndex++) {
			matchAddressStr = StrUtil.subWithLength(text, 0, endIndex + 2);

			for (Address province : addressList) {
				if (province.getChildren() == null) {
					continue;
				}
				if (info.getProvince() != null && !info.getProvince().equals(province.getName())) {
					continue;
				}
				for (Address city : province.getChildren()) {// 市
					if (city.getChildren() == null) {
						continue;
					}
					if (info.getCity() != null && !info.getCity().equals(city.getName())) {
						continue;
					}
					for (Address county : city.getChildren()) { // 区
						if (county.getChildren() == null) {
							continue;
						}
						if (info.getCounty() != null && !info.getCounty().equals(county.getName())) {
							continue;
						}
						for (Address street : county.getChildren()) { // 街道
							if (street.getName().contains(matchAddressStr)) {
								matchStreet.add(new MatchAddress(province, city, county, street, matchAddressStr));
							}
						}
					}
				}
			}
		}
		if (!matchStreet.isEmpty()) {
			MatchAddress matchAddress = getBestMatch(matchStreet);
			setMatchAddressInfo(info, matchAddress);
			text = text.replaceFirst(matchAddress.getMatchValue(), "");
			text = ReUtil.replaceFirst(pattern, text, "");
		}
		setAddress(matchStreet, address, text, info);
		return info;
	}
	/**
	 * set Address
	 * @param matchList matchList
	 * @param address address
	 * @param text text
	 * @param info AddressInfo
	 */
	private void setAddress(List<MatchAddress> matchList, String address, String text, AddressInfo info) {
		if (matchList.isEmpty() || !address.equals(text)) {
			info.setAddress(text);
		}
	}
	/**
	 * 获取最优匹配
	 * @param matchAddressList matchAddressList
	 * @return
	 */
	private MatchAddress getBestMatch(List<MatchAddress> matchAddressList) {
		return Collections.max(matchAddressList, Comparator.comparingInt(o -> o.getMatchValue().length()));
	}
	/**
	 * set Match Info
	 * @param info
	 * @param matchAddress
	 */
	protected void setMatchAddressInfo(AddressInfo info, MatchAddress matchAddress) {
		info.setProvince(matchAddress.getProvince());
		info.setProvinceCode(matchAddress.getProvinceCode());
		info.setCity(matchAddress.getCity());
		info.setCityCode(matchAddress.getCityCode());
		info.setCounty(matchAddress.getCounty());
		info.setCountyCode(matchAddress.getCountyCode());
		info.setStreet(matchAddress.getStreet());
		info.setStreetCode(matchAddress.getStreetCode());
		info.setAreaId(matchAddress.getAreaId());
	}


	public Pattern getPattern() {
		return pattern;
	}

	public void setPattern(Pattern pattern) {
		this.pattern = pattern;
	}
}
