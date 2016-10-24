package com.smmpay.respository.model;

import java.util.Date;

public class TrConfirmAudit {
    private Integer confirmId;

    private Integer paymentId;

    private String applyTime;

    private String mallAuditTime;

    private String mallAuditStatus;

    private String mallAuditReason;

    private Integer auditUserId;

    private String auditTime;

    private Integer auditStatus;

    public Integer getConfirmId() {
        return confirmId;
    }

    public void setConfirmId(Integer confirmId) {
        this.confirmId = confirmId;
    }

    public Integer getPaymentId() {
        return paymentId;
    }

    public void setPaymentId(Integer paymentId) {
        this.paymentId = paymentId;
    }

    

    public String getMallAuditStatus() {
        return mallAuditStatus;
    }

    public void setMallAuditStatus(String mallAuditStatus) {
        this.mallAuditStatus = mallAuditStatus == null ? null : mallAuditStatus.trim();
    }

    public String getMallAuditReason() {
        return mallAuditReason;
    }

    public void setMallAuditReason(String mallAuditReason) {
        this.mallAuditReason = mallAuditReason == null ? null : mallAuditReason.trim();
    }

    public Integer getAuditUserId() {
        return auditUserId;
    }

    public void setAuditUserId(Integer auditUserId) {
        this.auditUserId = auditUserId;
    }

   
    public String getApplyTime() {
		return applyTime;
	}

	public void setApplyTime(String applyTime) {
		this.applyTime = applyTime;
	}

	public String getMallAuditTime() {
		return mallAuditTime;
	}

	public void setMallAuditTime(String mallAuditTime) {
		this.mallAuditTime = mallAuditTime;
	}

	public String getAuditTime() {
		return auditTime;
	}

	public void setAuditTime(String auditTime) {
		this.auditTime = auditTime;
	}

	public Integer getAuditStatus() {
        return auditStatus;
    }

    public void setAuditStatus(Integer auditStatus) {
        this.auditStatus = auditStatus;
    }
}