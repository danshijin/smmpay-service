package com.smmpay.respository.model;

import java.util.Date;

public class FnlLog {
    private Integer id;

    private Integer fnlaccount;

    private Integer type;

    private String content;

    private Date createdat;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getFnlaccount() {
        return fnlaccount;
    }

    public void setFnlaccount(Integer fnlaccount) {
        this.fnlaccount = fnlaccount;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content == null ? null : content.trim();
    }

    public Date getCreatedat() {
        return createdat;
    }

    public void setCreatedat(Date createdat) {
        this.createdat = createdat;
    }
}