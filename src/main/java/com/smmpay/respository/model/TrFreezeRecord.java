package com.smmpay.respository.model;

import java.math.BigDecimal;

public class TrFreezeRecord {
	
    private Integer freezeId;

    private String freezeNo;

    private BigDecimal freezeMoney;

    private String applyTime;

    private String replyTime;

    private Integer freezeStatus;

    private String freezeNote;

    private Integer userId;

    private String userName;

    private Integer paymentId;
    
    private Integer payChannelId;

    private Integer userPayChannelId;
    
    private Integer unfreezeType;
    
    private BigDecimal freezeUserMoney;
    
    private BigDecimal freezeUserFreezeMoney;
  
    private String freezeClientId;

    public BigDecimal getFreezeUserFreezeMoney() {
		return freezeUserFreezeMoney;
	}

	public void setFreezeUserFreezeMoney(BigDecimal freezeUserFreezeMoney) {
		this.freezeUserFreezeMoney = freezeUserFreezeMoney;
	}

    public Integer getFreezeId() {
        return freezeId;
    }

    public void setFreezeId(Integer freezeId) {
        this.freezeId = freezeId;
    }

    public String getFreezeNo() {
        return freezeNo;
    }

    public void setFreezeNo(String freezeNo) {
        this.freezeNo = freezeNo == null ? null : freezeNo.trim();
    }

    public BigDecimal getFreezeMoney() {
        return freezeMoney;
    }

    public void setFreezeMoney(BigDecimal freezeMoney) {
        this.freezeMoney = freezeMoney;
    }

    public String getApplyTime() {
        return applyTime;
    }

    public void setApplyTime(String applyTime) {
        this.applyTime = applyTime == null ? null : applyTime.trim();
    }

    public String getReplyTime() {
        return replyTime;
    }

    public void setReplyTime(String replyTime) {
        this.replyTime = replyTime == null ? null : replyTime.trim();
    }

    public Integer getFreezeStatus() {
        return freezeStatus;
    }

    public void setFreezeStatus(Integer freezeStatus) {
        this.freezeStatus = freezeStatus;
    }

    public String getFreezeNote() {
        return freezeNote;
    }

    public void setFreezeNote(String freezeNote) {
        this.freezeNote = freezeNote == null ? null : freezeNote.trim();
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName == null ? null : userName.trim();
    }

    public Integer getPaymentId() {
        return paymentId;
    }

    public void setPaymentId(Integer paymentId) {
        this.paymentId = paymentId;
    }

    public Integer getPayChannelId() {
        return payChannelId;
    }

    public void setPayChannelId(Integer payChannelId) {
        this.payChannelId = payChannelId;
    }

    public Integer getUserPayChannelId() {
        return userPayChannelId;
    }

    public void setUserPayChannelId(Integer userPayChannelId) {
        this.userPayChannelId = userPayChannelId;
    }

	public Integer getUnfreezeType() {
		return unfreezeType;
	}

	public void setUnfreezeType(Integer unfreezeType) {
		this.unfreezeType = unfreezeType;
	}

	public BigDecimal getFreezeUserMoney() {
		return freezeUserMoney;
	}

	public void setFreezeUserMoney(BigDecimal freezeUserMoney) {
		this.freezeUserMoney = freezeUserMoney;
	}

	public String getFreezeClientId() {
		return freezeClientId;
	}

	public void setFreezeClientId(String freezeClientId) {
		this.freezeClientId = freezeClientId;
	}

}