package com.smmpay.respository.model;

import java.math.BigDecimal;

public class UserMoneyException {
    private Integer id;

    private String createTime;

    private Integer userId;

    private String userCompanyName;

    private Integer operate;

    private Integer paymentId;

    private Integer payChannelId;

    private String userPayChannelAccount;

    private BigDecimal userPayChannelMoney;

    private BigDecimal userMoney;

    private Integer auditUserId;

    private String auditTime;

    private Integer auditStatus;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime == null ? null : createTime.trim();
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getUserCompanyName() {
        return userCompanyName;
    }

    public void setUserCompanyName(String userCompanyName) {
        this.userCompanyName = userCompanyName == null ? null : userCompanyName.trim();
    }

    public Integer getOperate() {
        return operate;
    }

    public void setOperate(Integer operate) {
        this.operate = operate;
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

    public String getUserPayChannelAccount() {
        return userPayChannelAccount;
    }

    public void setUserPayChannelAccount(String userPayChannelAccount) {
        this.userPayChannelAccount = userPayChannelAccount == null ? null : userPayChannelAccount.trim();
    }

    public BigDecimal getUserPayChannelMoney() {
        return userPayChannelMoney;
    }

    public void setUserPayChannelMoney(BigDecimal userPayChannelMoney) {
        this.userPayChannelMoney = userPayChannelMoney;
    }

    public BigDecimal getUserMoney() {
        return userMoney;
    }

    public void setUserMoney(BigDecimal userMoney) {
        this.userMoney = userMoney;
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
        this.auditTime = auditTime == null ? null : auditTime.trim();
    }

    public Integer getAuditStatus() {
        return auditStatus;
    }

    public void setAuditStatus(Integer auditStatus) {
        this.auditStatus = auditStatus;
    }
}