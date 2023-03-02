package com.alotuser.address.data;

import java.util.List;

import com.alotuser.address.assets.Address;

/**
 * 地址数据加载器
 * @author I6view
 */
public interface AddressDataLoader {

	/**
	 * 加载地址数据
	 * @return
	 */
    List<Address> loadData();

}
