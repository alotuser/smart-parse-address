package com.alotuser.address.data;

import java.util.List;

import com.alotuser.address.assets.Address;

import cn.alotus.core.io.resource.ResourceUtil;

/**
 * 加载本地地址数据->resources/areaData.json
 * 
 * @author I6view
 *
 */
public class LocalAddressDataLoader implements AddressDataLoader {

	private List<Address> addressList;
	
	public LocalAddressDataLoader() {
		this.addressList = loadData(ResourceUtil.getResource(AREAPATH));
	}

	@Override
	public List<Address> loadData() {
		return addressList;
	}

}
