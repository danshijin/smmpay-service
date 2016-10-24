package com.smmpay.respository.model;

import java.math.BigDecimal;

public class SCheckBankRecord {

    public static final int CHECK_RESULT_0 = 0;
    public static final int CHECK_RESULT_1 = 1;

    private Integer id;

    private Integer userId;

    private String bankNo;

    private String bankName;

    private Integer userBandId;

    private BigDecimal inputMoney;

    private String checkTime;

    private Integer checkResult;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getBankNo() {
        return bankNo;
    }

    public void setBankNo(String bankNo) {
        this.bankNo = bankNo == null ? null : bankNo.trim();
    }

    public String getBankName() {
        return bankName;
    }

    public void setBankName(String bankName) {
        this.bankName = bankName == null ? null : bankName.trim();
    }

    public Integer getUserBandId() {
        return userBandId;
    }

    public void setUserBandId(Integer userBandId) {
        this.userBandId = userBandId;
    }

    public BigDecimal getInputMoney() {
        return inputMoney;
    }

    public void setInputMoney(BigDecimal inputMoney) {
        this.inputMoney = inputMoney;
    }

    public String getCheckTime() {
        return checkTime;
    }

    public void setCheckTime(String checkTime) {
        this.checkTime = checkTime == null ? null : checkTime.trim();
    }

    public Integer getCheckResult() {
        return checkResult;
    }

    public void setCheckResult(Integer checkResult) {
        this.checkResult = checkResult;
    }
}