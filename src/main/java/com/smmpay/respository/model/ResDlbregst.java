package com.smmpay.respository.model;

import java.io.Serializable;

/**
 * 附属账户预签约
 * 响应报文
 * @author zengshihua
 *
 */
public class ResDlbregst implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1661823384852565759L;
	
	/**
	 * 交易状态
	 */
	private String status;
	
	/**
	 * 交易状态信息
	 */
	private String statusText;
	
	/**
	 * 附属账号
	 */
	private String subAccNo;
	
	/**
	 * 附属账户名称
	 */
	private String subAccNm;

	
	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getStatusText() {
		return statusText;
	}

	public void setStatusText(String statusText) {
		this.statusText = statusText;
	}

	public String getSubAccNo() {
		return subAccNo;
	}

	public void setSubAccNo(String subAccNo) {
		this.subAccNo = subAccNo;
	}

	public String getSubAccNm() {
		return subAccNm;
	}

	public void setSubAccNm(String subAccNm) {
		this.subAccNm = subAccNm;
	}
	
}
