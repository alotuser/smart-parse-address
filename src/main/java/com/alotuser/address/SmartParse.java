package com.alotuser.address;

import java.util.Comparator;
import java.util.List;

import com.alotuser.address.assets.Address;
import com.alotuser.address.assets.AddressInfo;
import com.alotuser.address.assets.UserInfo;
import com.alotuser.address.data.AddressDataLoader;
import com.alotuser.address.data.LocalDataAddressDataLoader;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.text.StrSplitter;
import cn.hutool.core.util.CharUtil;
import cn.hutool.core.util.StrUtil;

public class SmartParse {

	private final AddressDataLoader addressDataLoader;

	private SmartMatch smartMatch;

	/**
	 * 默认加载本地地址库
	 */
	public SmartParse() {
		smartMatch = new SmartMatch();
		this.addressDataLoader = new LocalDataAddressDataLoader();
	}
	/**
	 * 自定义加载地址库
	 * @param addressDataLoader 地址数据加载器
	 */
	public SmartParse(AddressDataLoader addressDataLoader) {
		smartMatch = new SmartMatch();
		this.addressDataLoader = addressDataLoader;
	}
	/**
	 * 解析用户地址信息
	 * @param  text 	地址信息
	 * @return UserInfo
	 */
	public UserInfo parseUserInfo(String text) {
		return parseUserInfo(text, null);
	}

	 
	/**
	 *  解析用户地址信息
	 * @param text 地址信息
	 * @param level level
	 * @return 用户地址信息
	 */
	public UserInfo parseUserInfo(String text, Integer level) {
		if (StrUtil.isBlank(text)) {
			return null;
		}

		text = text.replace(" 详细地址: ", "");
		StringBuilder matchText = new StringBuilder();
		// 先根据空白符分割，如果空白符分割包含：号，则默认取最后那一段
		for (String str : StrSplitter.splitByRegex(text, "[\\n\\r]", 0, true, true)) {
			str = str.replace(": ", ":");
			if (StrUtil.isBlank(str)) {
				continue;
			}
			for (String s : StrSplitter.splitByRegex(str, "，| ", 0, true, true)) {
				List<String> strings = StrSplitter.splitByRegex(s, "[:：]", 0, true, true);
				if (CollUtil.isEmpty(strings)) {
					continue;
				}
				if (strings.size() == 2) {
					matchText.append(strings.get(1));
				} else {
					matchText.append(strings.get(0));
				}
				matchText.append(" ");
			}
		}
		text = matchText.toString();
		UserInfo userInfo = new UserInfo();
		String mobile = SmartMatch.matchMobile(text);
		List<String> nameAddress = null;
		
		//text = SmartMatch.filterStr(text);
		if (StrUtil.isNotEmpty(mobile)) {
			userInfo.setMobile(mobile);
			nameAddress = StrUtil.split(StrUtil.removeAll(text, CharUtil.SPACE), mobile);
		}else {
			nameAddress=ListUtil.of(text);
		}
		
		List<Address> addressList = addressDataLoader.loadData();
		if (nameAddress != null) {
			ComparatorList comparatorList=new ComparatorList(nameAddress);
			String name = comparatorList.getMin();
			String address = comparatorList.getMax();
			AddressInfo addressInfo = smartMatch.matchAddress(addressList, address, level);
			if(nameAddress.size()!=1|| addressInfo.isEmpty()) 
				userInfo.setName(name);
			if(addressInfo.isEmpty())
				addressInfo.setAddress(null);
			
			userInfo.setAddressInfo(addressInfo);
		}

		return userInfo;
	}

	/**
	 * 解析地址
	 * 
	 * @param text 地址信息
	 * @return 地址信息
	 */
	public AddressInfo parseAddressInfo(String text) {
		return parseAddressInfo(text, null);
	}

	/**
	 * 解析地址
	 * 
	 * @param text  地址信息
	 * @param level 匹配级别。从0开始，可以选择只匹配到第几级，为null则忽略
	 * @return      地址信息
	 */
	public AddressInfo parseAddressInfo(String text, Integer level) {

		if (StrUtil.isBlank(text)) {
			return null;
		}

		List<Address> addressList = addressDataLoader.loadData();
		AddressInfo addressInfo = new AddressInfo();

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
	 * 比较器集合
	 * @author I6view
	 *
	 */
	class ComparatorList{
		
		private List<String> list;

		public ComparatorList(List<String> list) {
			super();
			this.list = list;
		}
		private final Comparator<String> lengthComparator = new Comparator<String>() {
			@Override
			public int compare(String o1, String o2) {
				return StrUtil.length(o1) - StrUtil.length(o2);
			}
		};
		public String getMax() {
			return list.stream().max(lengthComparator).get();
		}
		public String getMin() {
			return list.stream().min(lengthComparator).get();
		}
	}
	
	
}
