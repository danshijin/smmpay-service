package com.smmpay.respository.model.base;

import java.io.Serializable;

import com.smmpay.util.PropertiesUtil;

/**
 * 公共  请求报文
 * @author zengshihua
 *
 */
public class ReqCommon3 implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1661823384852565759L;
		
	/**
	 * 登录名
	 */
	private String userName;
	
	public ReqCommon3(){
		this.setUserName(PropertiesUtil.getValue("userName"));
	}
	
	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	

}
