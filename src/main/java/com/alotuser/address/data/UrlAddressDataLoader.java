package com.alotuser.address.data;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import com.alotuser.address.assets.Address;

import cn.alotus.core.lang.Assert;

/**
 * 从URL加载地址数据
 * 
 * @author I6view
 *
 */
public class UrlAddressDataLoader implements AddressDataLoader {

	private final URL url;
	private List<Address> addressList;

	public UrlAddressDataLoader(String urlStr) {
		Assert.notBlank(urlStr, "URL不能为空");

		try {
			this.url = new URL(urlStr);
		} catch (MalformedURLException e) {
			throw new IllegalArgumentException("非法URL：" + urlStr, e);
		}
	}

	public UrlAddressDataLoader(URL url) {
		this.url = url;
		this.addressList = loadData(url);
	}

	@Override
	public List<Address> loadData() {
		if (addressList == null) {
			this.addressList = loadData(this.url);
		}
		return addressList;
	}

	public URL getUrl() {
		return url;
	}

}
