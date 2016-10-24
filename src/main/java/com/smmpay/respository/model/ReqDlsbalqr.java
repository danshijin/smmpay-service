package com.smmpay.respository.model;

import java.io.Serializable;

import com.smmpay.respository.model.base.ReqCommon;

/**
 * 商户附属账户余额查询
 * 说明：商户查询附属账户体系内附属账户的余额信息。
 * 请求报文
 * @author zengshihua
 *
 */
public class ReqDlsbalqr extends ReqCommon implements Serializable {

	
	private static final long serialVersionUID = 4234030995444549796L;
	
	/*
	 * 请求地址
	 */
	public final String action="DLSBALQR";
	
	/*
	 * 登录名
	 */
	//public String userName;
	
	/*
	 * 主体账号
	 */
	//public String  accountNo;
	
	/*
	 * 附属账号
	 */
	public String subAccNo;

	
	public String getAction() {
		return action;
	}

	/*public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getAccountNo() {
		return accountNo;
	}

	public void setAccountNo(String accountNo) {
		this.accountNo = accountNo;
	}*/

	public String getSubAccNo() {
		return subAccNo;
	}

	public void setSubAccNo(String subAccNo) {
		this.subAccNo = subAccNo;
	}
	
	
}
