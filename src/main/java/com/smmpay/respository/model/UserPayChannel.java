package com.smmpay.respository.model;

import java.io.Serializable;
import java.math.BigDecimal;

public class UserPayChannel implements Serializable{
	
	
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public static final int UPC_ACCOUNT_STATUS_0 = 0; //正常
    public static final int UPC_ACCOUNT_STATUS_1 = 1; //关闭


    private Integer userPayChannelId;

    private Integer userId;

    private Integer payChannelId;

    private String userAccountNo;

    private String userAccountName;

    private Integer userAccountStatus;

    private BigDecimal freezeMoney;

    private BigDecimal useMoney;

    private BigDecimal smmFreezeMoney;

    private String createTime;

    public BigDecimal getSmmFreezeMoney() {
        return smmFreezeMoney;
    }

    public void setSmmFreezeMoney(BigDecimal smmFreezeMoney) {
        this.smmFreezeMoney = smmFreezeMoney;
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

    public Integer getPayChannelId() {
        return payChannelId;
    }

    public void setPayChannelId(Integer payChannelId) {
        this.payChannelId = payChannelId;
    }

    public String getUserAccountNo() {
        return userAccountNo;
    }

    public void setUserAccountNo(String userAccountNo) {
        this.userAccountNo = userAccountNo == null ? null : userAccountNo.trim();
    }

    public String getUserAccountName() {
        return userAccountName;
    }

    public void setUserAccountName(String userAccountName) {
        this.userAccountName = userAccountName == null ? null : userAccountName.trim();
    }

    public Integer getUserAccountStatus() {
        return userAccountStatus;
    }

    public void setUserAccountStatus(Integer userAccountStatus) {
        this.userAccountStatus = userAccountStatus;
    }

    public BigDecimal getFreezeMoney() {
        return freezeMoney;
    }

    public void setFreezeMoney(BigDecimal freezeMoney) {
        this.freezeMoney = freezeMoney;
    }

    public BigDecimal getUseMoney() {
        return useMoney;
    }

    public void setUseMoney(BigDecimal useMoney) {
        this.useMoney = useMoney;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime == null ? null : createTime.trim();
    }
}