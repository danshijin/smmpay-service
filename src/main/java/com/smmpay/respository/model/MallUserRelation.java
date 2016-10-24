package com.smmpay.respository.model;

public class MallUserRelation {
    private Integer id;

    private String mallUserAccount;

    private String email;

    private String code;

    private String createTime;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getMallUserAccount() {
        return mallUserAccount;
    }

    public void setMallUserAccount(String mallUserAccount) {
        this.mallUserAccount = mallUserAccount == null ? null : mallUserAccount.trim();
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email == null ? null : email.trim();
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code == null ? null : code.trim();
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime == null ? null : createTime.trim();
    }
}