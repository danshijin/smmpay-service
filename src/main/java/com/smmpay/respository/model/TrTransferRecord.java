package com.smmpay.respository.model;

public class TrTransferRecord {
    private Integer id;

    private String clientId;

    private Integer paymentId;

    private Integer payChannelId;

    private Integer outUserId;

    private String outUserCompanyName;

    private Integer outUserChannelId;

    private Integer inUserId;

    private String inUserCompanyName;

    private Integer inUserChannelId;

    private String applyTime;

    private String responseTime;

    private Integer status;

    private String desc;
    
    private String verifyCode;
    
    
    public String getVerifyCode() {
		return verifyCode;
	}

	public void setVerifyCode(String verifyCode) {
		this.verifyCode = verifyCode;
	}

	public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}

	public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId == null ? null : clientId.trim();
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

    public Integer getOutUserId() {
        return outUserId;
    }

    public void setOutUserId(Integer outUserId) {
        this.outUserId = outUserId;
    }

    public String getOutUserCompanyName() {
        return outUserCompanyName;
    }

    public void setOutUserCompanyName(String outUserCompanyName) {
        this.outUserCompanyName = outUserCompanyName == null ? null : outUserCompanyName.trim();
    }

    public Integer getOutUserChannelId() {
        return outUserChannelId;
    }

    public void setOutUserChannelId(Integer outUserChannelId) {
        this.outUserChannelId = outUserChannelId;
    }

    public Integer getInUserId() {
        return inUserId;
    }

    public void setInUserId(Integer inUserId) {
        this.inUserId = inUserId;
    }

    public String getInUserCompanyName() {
        return inUserCompanyName;
    }

    public void setInUserCompanyName(String inUserCompanyName) {
        this.inUserCompanyName = inUserCompanyName == null ? null : inUserCompanyName.trim();
    }

    public Integer getInUserChannelId() {
        return inUserChannelId;
    }

    public void setInUserChannelId(Integer inUserChannelId) {
        this.inUserChannelId = inUserChannelId;
    }

    public String getApplyTime() {
        return applyTime;
    }

    public void setApplyTime(String applyTime) {
        this.applyTime = applyTime == null ? null : applyTime.trim();
    }

    public String getResponseTime() {
        return responseTime;
    }

    public void setResponseTime(String responseTime) {
        this.responseTime = responseTime == null ? null : responseTime.trim();
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }
}