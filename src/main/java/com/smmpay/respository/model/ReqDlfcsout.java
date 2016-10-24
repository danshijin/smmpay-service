package com.smmpay.respository.model;


import java.io.Serializable;
import java.math.BigDecimal;

import com.smmpay.respository.model.base.ReqCommon3;

/**
 * 平台出金
 * 请求报文
 * @author zengshihua
 *
 */
public class ReqDlfcsout extends ReqCommon3 implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1661823384852565759L;
	
	/**
	 * 请求码
	 */
	private final String action="DLFCSOUT";
	
	/**
	 * 主体账号
	 */
	private String clientID;
	/**
	 * 付款账号
	 */
	private String accountNo;
	
	/**
	 * 收款账号
	 */
	private String recvAccNo;
	
	/**
	 * 收款账户名称
	 */
	private String recvAccNm;
	
	/**
	 *  交易金额 
	 */
	private BigDecimal tranAmt;
	/**
	 * 中信标识char(1) 0：本行；1： 他行
	 */
	private String sameBank;
	/**
	 * 收款账户开户行支付联行号 
	 */
	private String recvTgfi;
	/**
	 * 收款账户开户行名
	 */
	private String recvBankNm;
	/**
	 * 摘要
	 */
	private String memo;
	/**
	 * 预约标志char(1) 0：非预约1：预约
	 */
	private String preFlg;
	/**
	 *预约日期char(8) 格式：YYYYMMDD 预约时非空
	 */
	private String preDate;

	/**
	 * 预约时间格式：hhmmss 预约时非空，只限100000、120000、140000、160000四个时间点
	 */
	private String preTime;
	
//	public ReqDlfcsout() {
//		
//		this.action = "DLFCSOUT";
//		this.userName = "YSWTEST";
//		this.clientID = "10002456";
//		this.accountNo = "3110210003611006568";
//		this.recvAccNo = "7313210182600003949";
//		this.recvAccNm = "中信10047764";
//		this.tranAmt = "100";
//		this.sameBank = "0";
//		
//	}

	public String getAction() {
		return action;
	}

	public String getClientID() {
		return clientID;
	}

	public void setClientID(String clientID) {
		this.clientID = clientID;
	}

	public String getAccountNo() {
		return accountNo;
	}

	public void setAccountNo(String accountNo) {
		this.accountNo = accountNo;
	}

	public String getRecvAccNo() {
		return recvAccNo;
	}

	public void setRecvAccNo(String recvAccNo) {
		this.recvAccNo = recvAccNo;
	}

	public String getRecvAccNm() {
		return recvAccNm;
	}

	public void setRecvAccNm(String recvAccNm) {
		this.recvAccNm = recvAccNm;
	}
	public BigDecimal getTranAmt() {
		return tranAmt;
	}

	public void setTranAmt(BigDecimal tranAmt) {
		this.tranAmt = tranAmt;
	}

	public String getSameBank() {
		return sameBank;
	}

	public void setSameBank(String sameBank) {
		this.sameBank = sameBank;
	}

	public String getRecvTgfi() {
		return recvTgfi;
	}

	public void setRecvTgfi(String recvTgfi) {
		this.recvTgfi = recvTgfi;
	}

	public String getRecvBankNm() {
		return recvBankNm;
	}

	public void setRecvBankNm(String recvBankNm) {
		this.recvBankNm = recvBankNm;
	}

	public String getMemo() {
		return memo;
	}

	public void setMemo(String memo) {
		this.memo = memo;
	}

	public String getPreFlg() {
		return preFlg;
	}

	public void setPreFlg(String preFlg) {
		this.preFlg = preFlg;
	}

	public String getPreDate() {
		return preDate;
	}

	public void setPreDate(String preDate) {
		this.preDate = preDate;
	}

	public String getPreTime() {
		return preTime;
	}

	public void setPreTime(String preTime) {
		this.preTime = preTime;
	}
	
	
	

}
