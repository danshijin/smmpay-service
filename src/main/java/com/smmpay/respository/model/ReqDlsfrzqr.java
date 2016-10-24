package com.smmpay.respository.model;

import java.io.Serializable;

import com.smmpay.respository.model.base.ReqCommon;

/**
 * 
 * 商户附属账户冻结信息查询
 * 
 * 请求报文
 * 
 * @author zengshihua
 * 
 */
public class ReqDlsfrzqr extends ReqCommon implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	/**
	 * 请求地址
	 */
	private final String action="DLSFRZQR";
	
	/**
	 * 登录名
	 */
	//private String userName;
	
	/**
	 * 主体账号
	 */
	//private String accountNo;
	
	/**
	 * 附属账号
	 */
	private String subAccNo;
	
	/**
	 * 起始日期
	 */
	private String startDate;
	
	/**
	 * 终止日期
	 */
	private String endDate;

	
	/*public ReqDlsfrzqr() {
		super();
		this.action = "DLSFRZQR";
		this.userName = "YSWTEST";
		this.accountNo = "7313510182600037822";
		this.subAccNo = "3110210003611006562";
		this.startDate = "20160210";
		this.endDate = "20160211";
	}*/

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

	public String getStartDate() {
		return startDate;
	}

	public void setStartDate(String startDate) {
		this.startDate = startDate;
	}

	public String getEndDate() {
		return endDate;
	}

	public void setEndDate(String endDate) {
		this.endDate = endDate;
	}

}
