package com.alotuser.address.data;

import java.net.URL;
import java.util.List;

import com.alotuser.address.assets.Address;
import com.alotuser.address.util.JsonUtil;

/**
 * 地址数据加载器
 * 
 * @author I6view
 */
public interface AddressDataLoader {

	final String AREAPATH = "areaData.json";

	/**
	 * 加载地址数据
	 * 
	 * @return Address List
	 */
	List<Address> loadData();

	/**
	 * 自定义URL加载地址数据
	 * 
	 * @param url json资源url
	 * @return 地址列表
	 */
	default List<Address> loadData(URL url) {
		return JsonUtil.parseArray(url, Address.class);
	}

}
