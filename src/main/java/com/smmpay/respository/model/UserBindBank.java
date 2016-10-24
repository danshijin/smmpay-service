package com.smmpay.respository.model;

import java.math.BigDecimal;

public class UserBindBank {
    public static final int UB_IS_PAYMENT_0 = 0; //未划款
    public static final int UB_IS_PAYMENT_1 = 1; //已划款
    public static final String UB_IS_PAYMENT_STR_0 = "未划款";
    public static final String UB_IS_PAYMENT_STR_1 = "已划款";

    public static final int UB_AUDIT_STATUS_0 = 0; //待审核
    public static final int UB_AUDIT_STATUS_1 = 1; //通过
    public static final int UB_AUDIT_STATUS_2 = 2; //关闭
    public static final String UB_AUDIT_STATUS_STR_0 = "待审核"; //待审核
    public static final String UB_AUDIT_STATUS_STR_1 = "通过";
    public static final String UB_AUDIT_STATUS_STR_2 = "关闭";

    private Integer bindId;

    private Integer userId;

    private Integer bankId;

    private String bankName;

    private BigDecimal drawMoney;

    private String bankType;

    private int bankTypeId;

    private String bankAccountNo;

    private Integer bankAreaProvince;

    private String bankProvinceName;

    private Integer bankAreaCity;

    private String bankCityName;

    private String cnaps;

    private Integer isPayment;

    private String bindTime;

    private Integer auditStatus;

    private String createTime;

    public Integer getBankId() {
        return bankId;
    }

    public void setBankId(Integer bankId) {
        this.bankId = bankId;
    }

    public String getBankProvinceName() {
        return bankProvinceName;
    }

    public void setBankProvinceName(String bankProvinceName) {
        this.bankProvinceName = bankProvinceName;
    }

    public String getBankCityName() {
        return bankCityName;
    }

    public void setBankCityName(String bankCityName) {
        this.bankCityName = bankCityName;
    }

    public int getBankTypeId() {
        return bankTypeId;
    }

    public void setBankTypeId(int bankTypeId) {
        this.bankTypeId = bankTypeId;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public BigDecimal getDrawMoney() {
        return drawMoney;
    }

    public void setDrawMoney(BigDecimal drawMoney) {
        this.drawMoney = drawMoney;
    }

    public Integer getBindId() {
        return bindId;
    }

    public void setBindId(Integer bindId) {
        this.bindId = bindId;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getBankName() {
        return bankName;
    }

    public void setBankName(String bankName) {
        this.bankName = bankName == null ? null : bankName.trim();
    }

    public String getBankType() {
        return bankType;
    }

    public void setBankType(String bankType) {
        this.bankType = bankType == null ? null : bankType.trim();
    }

    public String getBankAccountNo() {
        return bankAccountNo;
    }

    public void setBankAccountNo(String bankAccountNo) {
        this.bankAccountNo = bankAccountNo == null ? null : bankAccountNo.trim();
    }

    public Integer getBankAreaProvince() {
        return bankAreaProvince;
    }

    public void setBankAreaProvince(Integer bankAreaProvince) {
        this.bankAreaProvince = bankAreaProvince;
    }

    public Integer getBankAreaCity() {
        return bankAreaCity;
    }

    public void setBankAreaCity(Integer bankAreaCity) {
        this.bankAreaCity = bankAreaCity;
    }

    public String getCnaps() {
        return cnaps;
    }

    public void setCnaps(String cnaps) {
        this.cnaps = cnaps == null ? null : cnaps.trim();
    }

    public Integer getIsPayment() {
        return isPayment;
    }

    public void setIsPayment(Integer isPayment) {
        this.isPayment = isPayment;
    }

    public String getBindTime() {
        return bindTime;
    }

    public void setBindTime(String bindTime) {
        this.bindTime = bindTime == null ? null : bindTime.trim();
    }

    public Integer getAuditStatus() {
        return auditStatus;
    }

    public void setAuditStatus(Integer auditStatus) {
        this.auditStatus = auditStatus;
    }
}