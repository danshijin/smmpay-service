package com.smmpay.inter.dto.req;

import java.io.Serializable;

public class QueryTrRecordsDTO implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = -401966836612129229L;	
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
