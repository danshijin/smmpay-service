package com.smmpay.respository.model;

public class DaPayChannel {
    private Integer payChanneId;

    private String platAccountNo;

    private String channelName;

    private String createTime;

    private Integer status;

    public Integer getPayChanneId() {
        return payChanneId;
    }

    public void setPayChanneId(Integer payChanneId) {
        this.payChanneId = payChanneId;
    }

    public String getPlatAccountNo() {
        return platAccountNo;
    }

    public void setPlatAccountNo(String platAccountNo) {
        this.platAccountNo = platAccountNo == null ? null : platAccountNo.trim();
    }

    public String getChannelName() {
        return channelName;
    }

    public void setChannelName(String channelName) {
        this.channelName = channelName == null ? null : channelName.trim();
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime == null ? null : createTime.trim();
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }
}