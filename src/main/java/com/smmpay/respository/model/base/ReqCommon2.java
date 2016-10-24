package com.smmpay.respository.model.base;

import java.io.Serializable;

import com.smmpay.util.PropertiesUtil;

/**
 * 公共  请求报文
 * @author zengshihua
 *
 */
public class ReqCommon2 implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1661823384852565759L;
		
	/**
	 * 登录名
	 */
	private String userName;
	
	/**
	 * 主体账号
	 */
	private String mainAccNo;
	
	/**
	 * 主体账号
	 */
	//private String accountNo="7313510182600037822";
	
	public ReqCommon2(){
		this.setUserName(PropertiesUtil.getValue("userName"));
		this.setMainAccNo(PropertiesUtil.getValue("accountNo"));
	}
	
	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	/*public String getAccountNo() {
		return accountNo;
	}

	public void setAccountNo(String accountNo) {
		this.accountNo = accountNo;
	}*/

	public String getMainAccNo() {
		return mainAccNo;
	}

	public void setMainAccNo(String mainAccNo) {
		this.mainAccNo = mainAccNo;
	}

}
