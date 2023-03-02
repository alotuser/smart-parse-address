package com.alotuser.address.assets;

import java.io.Serializable;
import java.util.List;

/**
 * 地区 Address
 * @author I6view
 *
 */
public class Address {
	/**
	 * id
	 */
	private String id;
	/**
	 * 地区编码
	 */
	private String code;
	/**
	 * 地区名称
	 */
	private String name;
	/**
	 * 父节点
	 */
	private Serializable parentId;
	/**
	 * 地区子节点
	 */
	private List<Address> children;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public Serializable getParentId() {
		return parentId;
	}

	public void setParentId(Serializable parentId) {
		this.parentId = parentId;
	}

	public List<Address> getChildren() {
		return children;
	}

	public void setChildren(List<Address> children) {
		this.children = children;
	}

}
