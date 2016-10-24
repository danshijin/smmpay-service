package com.smmpay.respository.model;

import java.io.Serializable;
import java.math.BigDecimal;

import com.smmpay.respository.model.base.ReqCommon;

/**
 * 强制转账
 * 
 * 请求报文
 * 
 * @author zengshihua
 * 
 */
public class ReqDlmdetrn extends ReqCommon implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7784725725853546421L;

	/**
	 * 请求代码
	 */
	private final String action="DLMDETRN";
	/**
	 * 登录名
	 */
	//private String userName;
	
	/**
	 * 客户流水号
	 */
	private String clientID;
	
	/**
	 * 主体账号
	 */
	//private String accountNo;
	
	/**
	 * 付款账号
	 */
	private String payAccNo;
	
	/**
	 * 转账类型varchar(2) ，BF：转账；BG：解冻；BH：解冻支付；BR：冻结；BS：支付冻结
	 */
	private String tranType;
	
	/**
	 * 收款账号varchar(19)，当转账类型为“冻结”时可空，其他类型必输
	 */
	private String recvAccNo;
	
	/**
	 * 收款账户名称varchar(60) ，当转账类型为“冻结”时可空，其他类型必输
	 */
	private String recvAccNm;
	
	/**
	 * 交易金额
	 */
	private BigDecimal tranAmt;
	
	/**
	 * 冻结编号
	 */
	private String freezeNo;
	
	/**
	 * 摘要
	 */
	private String memo;
	
	/**
	 * 转账时效标识char (1)，0：异步交易；1：同步交易
	 */
	private String tranFlag;

	/*public ReqDlmdetrn(String clientID,double tranAmt) {

		this.action = "DLMDETRN";
		this.userName = "YSWTEST";
		this.clientID = clientID;
		this.accountNo = "7313510182600037822";
		this.payAccNo = "3110210003611006562";
		this.tranType = "BF";
		this.recvAccNo = "3110210003611006568";
		this.recvAccNm = "上海有色网测试附属账户456";
		this.tranAmt = tranAmt;
		this.freezeNo = "";
		this.memo = "强制冻结测试";
	}*/

	public String getAction() {
		return action;
	}

	/*public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}*/

	public String getClientID() {
		return clientID;
	}

	public void setClientID(String clientID) {
		this.clientID = clientID;
	}

	/*public String getAccountNo() {
		return accountNo;
	}

	public void setAccountNo(String accountNo) {
		this.accountNo = accountNo;
	}*/

	public String getPayAccNo() {
		return payAccNo;
	}

	public void setPayAccNo(String payAccNo) {
		this.payAccNo = payAccNo;
	}

	public String getTranType() {
		return tranType;
	}

	public void setTranType(String tranType) {
		this.tranType = tranType;
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

	public String getFreezeNo() {
		return freezeNo;
	}

	public void setFreezeNo(String freezeNo) {
		this.freezeNo = freezeNo;
	}

	public String getMemo() {
		return memo;
	}

	public void setMemo(String memo) {
		this.memo = memo;
	}

	public String getTranFlag() {
		return tranFlag;
	}

	public void setTranFlag(String tranFlag) {
		this.tranFlag = tranFlag;
	}

}
