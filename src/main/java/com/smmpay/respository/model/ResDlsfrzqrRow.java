package com.smmpay.respository.model;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 
 * 商户附属账户冻结信息查询
 * 
 * 响应报文
 * 
 * @author zengshihua
 * 
 */
public class ResDlsfrzqrRow implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	/**
	 * 附属账号varchar(19)
	 */
	private String subAccNo;

	/**
	 * 冻结类型varchar(2)
	 */
	private String DJTYPE;
	
	/**
	 * 冻结编号varchar(22)
	 */
	private String DJCODE;
	/**
	 * 冻结日期char(8)
	 */
	private String DJDATE;
	
	/**
	 * 冻结时间char(6)
	 */
	private String DJTIME;
	
	/**
	 * 冻结柜员交易号varchar(14)
	 */
	private String DJOPR;
	
	/**
	 * 冻结金额decimal(15,2)
	 */
	private BigDecimal  DJAMT;
	
	/**
	 * 解冻日期char(8)
	 */
	private String JDDATE;
	
	/**
	 * 解冻时间char(6)
	 */
	private String JDTIME;
	
	/**
	 * 解冻柜员交易号varchar(14)
	 */
	private String JDOPR;
	
	/**
	 * 受理原因varchar(60)
	 */
	private String REASON;

	
	public String getSubAccNo() {
		return subAccNo;
	}

	public void setSubAccNo(String subAccNo) {
		this.subAccNo = subAccNo;
	}

	public String getDJTYPE() {
		return DJTYPE;
	}

	public void setDJTYPE(String dJTYPE) {
		DJTYPE = dJTYPE;
	}

	public String getDJCODE() {
		return DJCODE;
	}

	public void setDJCODE(String dJCODE) {
		DJCODE = dJCODE;
	}

	public String getDJDATE() {
		return DJDATE;
	}

	public void setDJDATE(String dJDATE) {
		DJDATE = dJDATE;
	}

	public String getDJTIME() {
		return DJTIME;
	}

	public void setDJTIME(String dJTIME) {
		DJTIME = dJTIME;
	}

	public String getDJOPR() {
		return DJOPR;
	}

	public void setDJOPR(String dJOPR) {
		DJOPR = dJOPR;
	}

	public BigDecimal getDJAMT() {
		return DJAMT;
	}

	public void setDJAMT(BigDecimal dJAMT) {
		DJAMT = dJAMT;
	}

	public String getJDDATE() {
		return JDDATE;
	}

	public void setJDDATE(String jDDATE) {
		JDDATE = jDDATE;
	}

	public String getJDTIME() {
		return JDTIME;
	}

	public void setJDTIME(String jDTIME) {
		JDTIME = jDTIME;
	}

	public String getJDOPR() {
		return JDOPR;
	}

	public void setJDOPR(String jDOPR) {
		JDOPR = jDOPR;
	}

	public String getREASON() {
		return REASON;
	}

	public void setREASON(String rEASON) {
		REASON = rEASON;
	}

}
