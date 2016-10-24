package com.smmpay.respository.model;

import java.io.Serializable;

import com.smmpay.respository.model.base.ReqCommon;

/**
 * 商户附属账户交易明细查询
 * 
 * 请求报文
 * 
 * @author zengshihua
 * 
 */
public class ReqDlstrndt extends ReqCommon implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -401966836612129229L;
	
	/**
	 * 请求地址
	 */
	private final String action="DLSTRNDT";
	
	
	/**
	 * 附属账号
	 */
	private String subAccNo;
	
	/**
	 * <!--查询类型 varchar(1) 1：查询待调账交易明细 空：查询全部交易明细-->
	 */
	private String queryType;
	
	/**
	 * 起始日期
	 */
	private String startDate;
	
	/**
	 * 终止日期
	 */
	private String endDate;
	
	/**
	 * 交易类型
	 */
	private String tranType;
	
	/**
	 * 起始记录号
	 */
	private String startRecord;
	
	/**
	 * 请求记录条数 最大为10
	 */
	private String pageNumber;

	
	/*public ReqDlstrndt() {
		
		this.action = "DLSTRNDT";
		this.userName = "YSWTEST";
		this.accountNo = "7313510182600037822";
		this.subAccNo = "3110210003611006568";
		this.queryType = "";
		this.startDate = "20160209";
		this.endDate = "20160212";
		
	}*/

	public String getAction() {
		return action;
	}

	public String getSubAccNo() {
		return subAccNo;
	}

	public void setSubAccNo(String subAccNo) {
		this.subAccNo = subAccNo;
	}

	public String getQueryType() {
		return queryType;
	}

	public void setQueryType(String queryType) {
		this.queryType = queryType;
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

	public String getTranType() {
		return tranType;
	}

	public void setTranType(String tranType) {
		this.tranType = tranType;
	}

	public String getStartRecord() {
		return startRecord;
	}

	public void setStartRecord(String startRecord) {
		this.startRecord = startRecord;
	}

	public String getPageNumber() {
		return pageNumber;
	}

	public void setPageNumber(String pageNumber) {
		this.pageNumber = pageNumber;
	}
	

}
