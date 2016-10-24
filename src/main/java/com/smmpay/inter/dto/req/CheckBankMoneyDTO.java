package com.smmpay.inter.dto.req;

import java.io.Serializable;

/**
 * Created by tangshulei on 2015/11/10.
 */
public class CheckBankMoneyDTO implements Serializable{

    private static final long serialVersionUID = -2319935946493294796L;
    private String userId;
    private String userName;
    private String money;
    private String bindId;

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

    public String getMoney() {
        return money;
    }

    public void setMoney(String money) {
        this.money = money;
    }

    public String getBindId() {
        return bindId;
    }

    public void setBindId(String bindId) {
        this.bindId = bindId;
    }
}
