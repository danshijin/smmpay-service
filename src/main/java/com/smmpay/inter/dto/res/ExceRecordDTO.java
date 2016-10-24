package com.smmpay.inter.dto.res;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 异常记录
 * 
 * @author zengshihua
 * 
 */
public class ExceRecordDTO implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * 序号
	 */
	private Integer rowNum;
	/**
	 * 企业名称
	 */
	private String userAccountName;
	/**
	 * 附属账号
	 */
	private String userAccountNo;
	/**
	 * 用户支付渠道ID
	 */
	private Integer userPayChannelId;
	/**
	 * 系统帐户余额
	 */
	private BigDecimal userMoney;
	/**
	 * 银行实际帐户余额
	 */
	private BigDecimal userSJAMT;
	
	/**
	 * userId
	 */
	private Integer userId;
	
	
	public Integer getRowNum() {
		return rowNum;
	}
	public void setRowNum(Integer rowNum) {
		this.rowNum = rowNum;
	}
	public String getUserAccountName() {
		return userAccountName;
	}
	public void setUserAccountName(String userAccountName) {
		this.userAccountName = userAccountName;
	}
	public String getUserAccountNo() {
		return userAccountNo;
	}
	public void setUserAccountNo(String userAccountNo) {
		this.userAccountNo = userAccountNo;
	}
	public BigDecimal getUserMoney() {
		return userMoney;
	}
	public void setUserMoney(BigDecimal userMoney) {
		this.userMoney = userMoney;
	}
	public BigDecimal getUserSJAMT() {
		return userSJAMT;
	}
	public void setUserSJAMT(BigDecimal userSJAMT) {
		this.userSJAMT = userSJAMT;
	}
	public Integer getUserPayChannelId() {
		return userPayChannelId;
	}
	public void setUserPayChannelId(Integer userPayChannelId) {
		this.userPayChannelId = userPayChannelId;
	}
	public Integer getUserId() {
		return userId;
	}
	public void setUserId(Integer userId) {
		this.userId = userId;
	}

	
}
