package com.smmpay.respository.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.smmpay.respository.model.base.ResCommon;

/**
 * 在线销户
 * 
 * @author zengshihua
 * 
 */
public class ResDlolcacc extends ResCommon implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7053389727760081987L;

	/**
	 * 附属账号
	 */
	private String subAccNo;
	
	/**
	 * 附属账户名
	 */
	private String subAccNm;

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
