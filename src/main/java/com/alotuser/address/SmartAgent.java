package com.alotuser.address;

import java.io.Serializable;

import com.alotuser.address.assets.AddressInfo;
import com.alotuser.address.assets.UserInfo;
import com.alotuser.address.data.AddressDataLoader;
import com.alotuser.address.data.LocalDataAddressDataLoader;
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
	
	private static volatile AddressDataLoader addressDataLoader=null;
	
	public SmartAgent(String userAddressString) {
		this.userAddressString=userAddressString;
		if(addressDataLoader == null) {
            synchronized (SmartAgent.class) {
                if(addressDataLoader == null) {
                	addressDataLoader = new LocalDataAddressDataLoader();
                }
            }
        }
		smartParse = new SmartParse(addressDataLoader);
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
