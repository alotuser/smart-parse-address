package com.alotuser.address.assets;

/**
 * 匹配地址
 * 
 * @author I6view
 *
 */
public class MatchAddress {

	public MatchAddress(Address province, Address city, Address county, Address street, String matchValue) {
		if (province != null) {
			this.province = province.getName();
			this.provinceCode = province.getCode();
			this.areaId = province.getId();
		}
		if (city != null) {
			this.city = city.getName();
			this.cityCode = city.getCode();
			this.areaId = city.getId();
		}
		if (county != null) {
			this.county = county.getName();
			this.countyCode = county.getCode();
			this.areaId = county.getId();
		}
		if (street != null) {
			this.street = street.getName();
			this.streetCode = street.getCode();
			this.areaId = street.getId();
		}
		this.matchValue = matchValue;
	}

	
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

	private String matchValue;

	private String areaId;

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

	public String getMatchValue() {
		return matchValue;
	}

	public void setMatchValue(String matchValue) {
		this.matchValue = matchValue;
	}

	public String getAreaId() {
		return areaId;
	}

	public void setAreaId(String areaId) {
		this.areaId = areaId;
	}

}
