package com.smmpay.respository.model;

import java.math.BigDecimal;

public class TrCashApply {
    public static final int CASH_TYPE_1 = 1;

    public static final int REPLAY_STATUS_0 = 0;
    public static final int REPLAY_STATUS_1 = 1;
    public static final int REPLAY_STATUS_2 = 2;

    public static final int APPLY_STATUS_0 = 0;
    public static final int APPLY_STATUS_1 = 1;
    public static final int APPLY_STATUS_2 = 2;

    private Integer id;

    private Integer cashBankId;

    private BigDecimal counterFee;

    private BigDecimal cashMoney;

    private String cashBankNo;

    private String cashBankName;

    private String cashBankCnaps;

    private Integer cashType;

    private Integer userId;

    private String userEmail;

    private String userCompanyName;

    private BigDecimal userUseMoney;

    private BigDecimal userFreezeMoney;

    private String applyTime;

    private String replayTime;

    private Integer applyStatus;

    private Integer replayStatus;

    private Integer replayUserId;

    private Integer payChannelId;

    private String payChannelUserAccount;

    private String applyRemark;
    
    private String cashNo;

    public String getApplyRemark() {
        return applyRemark;
    }

    public void setApplyRemark(String applyRemark) {
        this.applyRemark = applyRemark;
    }

    public Integer getApplyStatus() {
        return applyStatus;
    }

    public void setApplyStatus(Integer applyStatus) {
        this.applyStatus = applyStatus;
    }

    public BigDecimal getCounterFee() {
        return counterFee;
    }

    public void setCounterFee(BigDecimal counterFee) {
        this.counterFee = counterFee;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getCashBankId() {
        return cashBankId;
    }

    public void setCashBankId(Integer cashBankId) {
        this.cashBankId = cashBankId;
    }

    public BigDecimal getCashMoney() {
        return cashMoney;
    }

    public void setCashMoney(BigDecimal cashMoney) {
        this.cashMoney = cashMoney;
    }

    public String getCashBankNo() {
        return cashBankNo;
    }

    public void setCashBankNo(String cashBankNo) {
        this.cashBankNo = cashBankNo == null ? null : cashBankNo.trim();
    }

    public String getCashBankName() {
        return cashBankName;
    }

    public void setCashBankName(String cashBankName) {
        this.cashBankName = cashBankName == null ? null : cashBankName.trim();
    }

    public String getCashBankCnaps() {
        return cashBankCnaps;
    }

    public void setCashBankCnaps(String cashBankCnaps) {
        this.cashBankCnaps = cashBankCnaps == null ? null : cashBankCnaps.trim();
    }

    public Integer getCashType() {
        return cashType;
    }

    public void setCashType(Integer cashType) {
        this.cashType = cashType;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail == null ? null : userEmail.trim();
    }

    public String getUserCompanyName() {
        return userCompanyName;
    }

    public void setUserCompanyName(String userCompanyName) {
        this.userCompanyName = userCompanyName == null ? null : userCompanyName.trim();
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

    public String getApplyTime() {
        return applyTime;
    }

    public void setApplyTime(String applyTime) {
        this.applyTime = applyTime == null ? null : applyTime.trim();
    }

    public String getReplayTime() {
        return replayTime;
    }

    public void setReplayTime(String replayTime) {
        this.replayTime = replayTime == null ? null : replayTime.trim();
    }

    public Integer getReplayStatus() {
        return replayStatus;
    }

    public void setReplayStatus(Integer replayStatus) {
        this.replayStatus = replayStatus;
    }

    public Integer getReplayUserId() {
        return replayUserId;
    }

    public void setReplayUserId(Integer replayUserId) {
        this.replayUserId = replayUserId;
    }

    public Integer getPayChannelId() {
        return payChannelId;
    }

    public void setPayChannelId(Integer payChannelId) {
        this.payChannelId = payChannelId;
    }

    public String getPayChannelUserAccount() {
        return payChannelUserAccount;
    }

    public void setPayChannelUserAccount(String payChannelUserAccount) {
        this.payChannelUserAccount = payChannelUserAccount == null ? null : payChannelUserAccount.trim();
    }

	public String getCashNo() {
		return cashNo;
	}

	public void setCashNo(String cashNo) {
		this.cashNo = cashNo;
	}
}