package com.alotuser.address.assets;

import cn.hutool.core.util.StrUtil;

/**
 * 地址信息
 * 
 * @author I6view
 */
public class AddressInfo {

	/**
	 * id
	 */
	private String areaId;

	/**
	 * 省
	 */
	private String province;

	private String provinceCode;

	/**
	 * 市
	 */
	private String city;

	private String cityCode;

	/**
	 * 区
	 */
	private String county;

	private String countyCode;

	/**
	 * 街道
	 */
	private String street;

	private String streetCode;

	/**
	 * 详细地址
	 */
	private String address;

	public boolean isEmpty() {
		return StrUtil.isAllEmpty(provinceCode, cityCode, countyCode, streetCode) || areaId == null;
	}

	public String getProvince() {
		return province;
	}

	public void setProvince(String province) {
		this.province = province;
	}

	public String getProvinceCode() {
		return provinceCode;
	}

	public void setProvinceCode(String provinceCode) {
		this.provinceCode = provinceCode;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getCityCode() {
		return cityCode;
	}

	public void setCityCode(String cityCode) {
		this.cityCode = cityCode;
	}

	public String getCounty() {
		return county;
	}

	public void setCounty(String county) {
		this.county = county;
	}

	public String getCountyCode() {
		return countyCode;
	}

	public void setCountyCode(String countyCode) {
		this.countyCode = countyCode;
	}

	public String getStreet() {
		return street;
	}

	public void setStreet(String street) {
		this.street = street;
	}

	public String getStreetCode() {
		return streetCode;
	}

	public void setStreetCode(String streetCode) {
		this.streetCode = streetCode;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getAreaId() {
		return areaId;
	}

	public void setAreaId(String areaId) {
		this.areaId = areaId;
	}

	public void setAddressInfo(AddressInfo addressInfo) {
		if (null != addressInfo.getAddress())
			this.setAddress(addressInfo.getAddress());
		if (null != addressInfo.getAreaId())
			this.setAreaId(addressInfo.getAreaId());
		if (null != addressInfo.getCity())
			this.setCity(addressInfo.getCity());
		if (null != addressInfo.getCityCode())
			this.setCityCode(addressInfo.getCityCode());
		if (null != addressInfo.getCounty())
			this.setCounty(addressInfo.getCounty());
		if (null != addressInfo.getCountyCode())
			this.setCountyCode(addressInfo.getCountyCode());
		if (null != addressInfo.getProvince())
			this.setProvince(addressInfo.getProvince());
		if (null != addressInfo.getProvinceCode())
			this.setProvinceCode(addressInfo.getProvinceCode());
		if (null != addressInfo.getStreet())
			this.setStreet(addressInfo.getStreet());
		if (null != addressInfo.getStreetCode())
			this.setStreetCode(addressInfo.getStreetCode());
	}

}
