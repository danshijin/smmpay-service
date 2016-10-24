package com.smmpay.respository.model;

import java.io.Serializable;

import com.smmpay.respository.model.base.ReqCommon2;

/**
 * 在线销户
 * 
 * @author zengshihua
 *
 */
public class ReqDlolcacc extends ReqCommon2 implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8811917063694220228L;


	/**
	 * 请求码
	 */
	private final String action="DLOLCACC";

	
	/**
	 * 附属账号
	 */
	private String subAccNo;


	public String getSubAccNo() {
		return subAccNo;
	}


	public void setSubAccNo(String subAccNo) {
		this.subAccNo = subAccNo;
	}


	public String getAction() {
		return action;
	}
	
}
