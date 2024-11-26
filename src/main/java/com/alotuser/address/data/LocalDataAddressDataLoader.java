package com.alotuser.address.data;

import java.util.List;

import com.alotuser.address.assets.Address;
import com.alotuser.address.util.JsonUtil;

import cn.alotus.core.io.resource.ResourceUtil;
 

/**
 * 加载本地地址数据->resources/areaData.json
 * @author I6view
 *
 */
public class LocalDataAddressDataLoader implements AddressDataLoader {

    private final List<Address> addressList;

    public LocalDataAddressDataLoader() {
        this.addressList = JsonUtil.parseArray(ResourceUtil.getResource("areaData.json"),Address.class);
    }

    @Override
    public List<Address> loadData() {
        return addressList;
    }

}
