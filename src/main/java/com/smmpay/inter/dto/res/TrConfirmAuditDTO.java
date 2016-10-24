package com.smmpay.inter.dto.res;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by wanghao on 2015/11/12.
 */
public class TrConfirmAuditDTO implements Serializable{

	private static final long serialVersionUID = 4442581563050899318L;

	private Integer confirmId;

    private Integer paymentId;

    private String applyTime;

    private String mallAuditTime;

    private String mallAuditStatus;

    private String mallAuditReason;

    private Integer auditUserId;

    private String auditTime;

    private Integer auditStatus;
    
    private String mallUserName;
    
    private String payCode;

	public String getPayCode() {
		return payCode;
	}

	public void setPayCode(String payCode) {
		this.payCode = payCode;
	}

	public String getMallUserName() {
		return mallUserName;
	}

	public void setMallUserName(String mallUserName) {
		this.mallUserName = mallUserName;
	}

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

	public String getMallAuditStatus() {
		return mallAuditStatus;
	}

	public void setMallAuditStatus(String mallAuditStatus) {
		this.mallAuditStatus = mallAuditStatus;
	}

	public String getMallAuditReason() {
		return mallAuditReason;
	}

	public void setMallAuditReason(String mallAuditReason) {
		this.mallAuditReason = mallAuditReason;
	}

	public Integer getAuditUserId() {
		return auditUserId;
	}

	public void setAuditUserId(Integer auditUserId) {
		this.auditUserId = auditUserId;
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
