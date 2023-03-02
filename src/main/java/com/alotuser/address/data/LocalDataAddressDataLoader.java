package com.alotuser.address.data;

import java.net.URL;
import java.nio.charset.Charset;
import java.util.List;

import com.alotuser.address.assets.Address;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.resource.ResourceUtil;
import cn.hutool.json.JSONUtil;

/**
 * 加载本地地址数据->resources/areaData.json
 * @author I6view
 *
 */
public class LocalDataAddressDataLoader implements AddressDataLoader {

    private final List<Address> addressList;

    public LocalDataAddressDataLoader() {
        URL url = ResourceUtil.getResource("areaData.json");
        String jsonData = FileUtil.readString(url, Charset.defaultCharset());
        this.addressList = JSONUtil.parseArray(jsonData).toList(Address.class);
    }

    @Override
    public List<Address> loadData() {
        return addressList;
    }

}
