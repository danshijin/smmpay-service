package com.smmpay.respository.model;

import java.math.BigDecimal;

public class TrRecord implements java.io.Serializable{
    private Integer trId;

    private BigDecimal trMoney;

    private Integer trType;

    private String trApplyTime;

    private Integer trApplyStatus;

    private BigDecimal trCharge;

    private String trWaterId;

    private BigDecimal userMoney;

    private Integer userId;

    private Integer userPayChannelId;

    private String userCompanyName;

    private Integer oppositUserId;

    private Integer oppositUserPayChannelId;

    private String oppositCompanyName;

    private String outcomeBankName;

    private String outcomeBankAccountNo;

    private String outcomeBankCnaps;

    private String printCheckCode;

    private Integer payChannelId;

    private String note;

    private String replayTime;

    private String payChannelTrTime;

    private String payChannelTrNo;
    
    private BigDecimal userFreezeMoney;

    public Integer getTrId() {
        return trId;
    }

    public void setTrId(Integer trId) {
        this.trId = trId;
    }

    public BigDecimal getTrMoney() {
        return trMoney;
    }

    public void setTrMoney(BigDecimal trMoney) {
        this.trMoney = trMoney;
    }

    public Integer getTrType() {
        return trType;
    }

    public void setTrType(Integer trType) {
        this.trType = trType;
    }

    public String getTrApplyTime() {
        return trApplyTime;
    }

    public void setTrApplyTime(String trApplyTime) {
        this.trApplyTime = trApplyTime == null ? null : trApplyTime.trim();
    }

    public Integer getTrApplyStatus() {
        return trApplyStatus;
    }

    public void setTrApplyStatus(Integer trApplyStatus) {
        this.trApplyStatus = trApplyStatus;
    }

    public BigDecimal getTrCharge() {
        return trCharge;
    }

    public void setTrCharge(BigDecimal trCharge) {
        this.trCharge = trCharge;
    }

    public String getTrWaterId() {
        return trWaterId;
    }

    public void setTrWaterId(String trWaterId) {
        this.trWaterId = trWaterId == null ? null : trWaterId.trim();
    }

    public BigDecimal getUserMoney() {
        return userMoney;
    }

    public void setUserMoney(BigDecimal userMoney) {
        this.userMoney = userMoney;
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
        this.userCompanyName = userCompanyName == null ? null : userCompanyName.trim();
    }

    public Integer getOppositUserId() {
        return oppositUserId;
    }

    public void setOppositUserId(Integer oppositUserId) {
        this.oppositUserId = oppositUserId;
    }

    public Integer getOppositUserPayChannelId() {
        return oppositUserPayChannelId;
    }

    public void setOppositUserPayChannelId(Integer oppositUserPayChannelId) {
        this.oppositUserPayChannelId = oppositUserPayChannelId;
    }

    public String getOppositCompanyName() {
        return oppositCompanyName;
    }

    public void setOppositCompanyName(String oppositCompanyName) {
        this.oppositCompanyName = oppositCompanyName == null ? null : oppositCompanyName.trim();
    }

    public String getOutcomeBankName() {
        return outcomeBankName;
    }

    public void setOutcomeBankName(String outcomeBankName) {
        this.outcomeBankName = outcomeBankName == null ? null : outcomeBankName.trim();
    }

    public String getOutcomeBankAccountNo() {
        return outcomeBankAccountNo;
    }

    public void setOutcomeBankAccountNo(String outcomeBankAccountNo) {
        this.outcomeBankAccountNo = outcomeBankAccountNo == null ? null : outcomeBankAccountNo.trim();
    }

    public String getOutcomeBankCnaps() {
        return outcomeBankCnaps;
    }

    public void setOutcomeBankCnaps(String outcomeBankCnaps) {
        this.outcomeBankCnaps = outcomeBankCnaps == null ? null : outcomeBankCnaps.trim();
    }

    public String getPrintCheckCode() {
        return printCheckCode;
    }

    public void setPrintCheckCode(String printCheckCode) {
        this.printCheckCode = printCheckCode == null ? null : printCheckCode.trim();
    }

    public Integer getPayChannelId() {
        return payChannelId;
    }

    public void setPayChannelId(Integer payChannelId) {
        this.payChannelId = payChannelId;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note == null ? null : note.trim();
    }

    public String getReplayTime() {
        return replayTime;
    }

    public void setReplayTime(String replayTime) {
        this.replayTime = replayTime == null ? null : replayTime.trim();
    }

    public String getPayChannelTrTime() {
        return payChannelTrTime;
    }

    public void setPayChannelTrTime(String payChannelTrTime) {
        this.payChannelTrTime = payChannelTrTime == null ? null : payChannelTrTime.trim();
    }

    public String getPayChannelTrNo() {
        return payChannelTrNo;
    }

    public void setPayChannelTrNo(String payChannelTrNo) {
        this.payChannelTrNo = payChannelTrNo == null ? null : payChannelTrNo.trim();
    }

	public BigDecimal getUserFreezeMoney() {
		return userFreezeMoney;
	}

	public void setUserFreezeMoney(BigDecimal userFreezeMoney) {
		this.userFreezeMoney = userFreezeMoney;
	}
    
    
    
}