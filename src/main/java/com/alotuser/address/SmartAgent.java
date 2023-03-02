package com.alotuser.address;

import java.io.Serializable;

import com.alotuser.address.assets.AddressInfo;
import com.alotuser.address.assets.UserInfo;
/**
 * SmartAgent
 * @author I6view
 *
 */
public class SmartAgent implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -6937633457650992278L;

	private String userAddressString;

	private SmartParse smartParse;
	
	public SmartAgent(String userAddressString) {
		this.userAddressString=userAddressString;
		smartParse = new SmartParse();
	}
	/**
	 * addressInfo
	 * @return addressInfo
	 */
	public AddressInfo getAddressInfo() {
		return smartParse.parseAddressInfo(userAddressString);
	}
	/**
	 * userInfo
	 * @return userInfo
	 */
	public UserInfo getUserInfo() {
		return smartParse.parseUserInfo(userAddressString);
	}
	/**
	 * SmartAgent
	 * @param userAddressString userAddressString
	 * @return SmartAgent
	 */
	public static SmartAgent parseUserAddressString(String userAddressString) {		
		return new SmartAgent(userAddressString);
	}
	
}
