package com.smmpay.inter.dto.req;

import java.io.Serializable;

/**
 * Created by tangshulei on 2015/11/13.
 */
public class GetCashDTO implements Serializable {

    private String userId;

    private String userName;

    private String cashType;

    private String cashBankId;

    private String cashMoney;

    private String payChannelId = "10001";

    public String getPayChannelId() {
        return payChannelId;
    }

    public void setPayChannelId(String payChannelId) {
        this.payChannelId = payChannelId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getCashType() {
        return cashType;
    }

    public void setCashType(String cashType) {
        this.cashType = cashType;
    }

    public String getCashBankId() {
        return cashBankId;
    }

    public void setCashBankId(String cashBankId) {
        this.cashBankId = cashBankId;
    }

    public String getCashMoney() {
        return cashMoney;
    }

    public void setCashMoney(String cashMoney) {
        this.cashMoney = cashMoney;
    }
}
