package com.smmpay.inter.dto.req;

import java.io.Serializable;
import java.math.BigDecimal;

public class PlatformDebitsDTO implements Serializable{

	private static final long serialVersionUID = 3635548571243802313L;
	/**
	 * 用户在支付渠道ID
	 */
	
	private Integer payChannelId;
	
	/**
	 * 用户ID
	 */
	private Integer userId;

	/**
	 * 出金记录id
	 */
	private Integer cashApplyId;
	/**
	 * 出金银行卡id
	 */
	private Integer cashBankId;
	
	/**
	 * 用户在支付渠道ID
	 */
	
	private Integer userPayChannelId;

	/**
	 * 支付渠道会员账号
	 */
	private String payChannelUserAccount;
	
	/**
	 * 用户账户账号名称
	 */
	private String userCompanyName;

	/**
	 * 用户账户email
	 */
	private String userEmail;
	/**
	 * 用户账户账号可用余额
	 */
	private BigDecimal userUseMoney;
	/**
	 * 用户账户账号冻结余额
	 */
	private BigDecimal userFreezeMoney;
	
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
	 *  手续费
	 */
	private BigDecimal counterFee;

	/**
	 *  出金方式
	 */
	private Integer cashType;

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

	public Integer getCashBankId() {
		return cashBankId;
	}

	public void setCashBankId(Integer cashBankId) {
		this.cashBankId = cashBankId;
	}

	public Integer getCashApplyId() {
		return cashApplyId;
	}

	public void setCashApplyId(Integer cashApplyId) {
		this.cashApplyId = cashApplyId;
	}

	public String getPayChannelUserAccount() {
		return payChannelUserAccount;
	}

	public void setPayChannelUserAccount(String payChannelUserAccount) {
		this.payChannelUserAccount = payChannelUserAccount;
	}

	public String getUserEmail() {
		return userEmail;
	}

	public void setUserEmail(String userEmail) {
		this.userEmail = userEmail;
	}

	public BigDecimal getUserUseMoney() {
		return userUseMoney;
	}

	public void setUserUseMoney(BigDecimal userUseMoney) {
		this.userUseMoney = userUseMoney;
	}

	public BigDecimal getUserFreezeMoney() {
		return userFreezeMoney;
	}

	public void setUserFreezeMoney(BigDecimal userFreezeMoney) {
		this.userFreezeMoney = userFreezeMoney;
	}

	public BigDecimal getCounterFee() {
		return counterFee;
	}

	public void setCounterFee(BigDecimal counterFee) {
		this.counterFee = counterFee;
	}

	public Integer getCashType() {
		return cashType;
	}

	public void setCashType(Integer cashType) {
		this.cashType = cashType;
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
	public Integer getUserId() {
		return userId;
	}
	public void setUserId(Integer userId) {
		this.userId = userId;
	}
	public Integer getUserPayChannelId() {
		return userPayChannelId;
	}
	public void setUserPayChannelId(Integer userPayChannelId) {
		this.userPayChannelId = userPayChannelId;
	}
	public String getUserCompanyName() {
		return userCompanyName;
	}
	public void setUserCompanyName(String userCompanyName) {
		this.userCompanyName = userCompanyName;
	}
	public Integer getPayChannelId() {
		return payChannelId;
	}
	public void setPayChannelId(Integer payChannelId) {
		this.payChannelId = payChannelId;
	}
	
	
}
