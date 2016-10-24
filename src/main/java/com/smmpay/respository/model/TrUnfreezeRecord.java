package com.smmpay.respository.model;

import java.math.BigDecimal;

public class TrUnfreezeRecord {
    private Integer id;

    private Integer freezeId;

    private String unfreezeApplyTime;

    private String unfreezeReplyTime;

    private Integer unfreezeStatus;

    private Integer recevieUserId;

    private String recevieUserName;

    private Integer recevieUserPayChannelId;

    private String unfreezeNote;

    private Integer payChannelId;

    private BigDecimal unfreezeUserMoney;

    private BigDecimal unfreezeUserFreezeMoney;

    private String unfreezeClientId;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getFreezeId() {
        return freezeId;
    }

    public void setFreezeId(Integer freezeId) {
        this.freezeId = freezeId;
    }

    public String getUnfreezeApplyTime() {
        return unfreezeApplyTime;
    }

    public void setUnfreezeApplyTime(String unfreezeApplyTime) {
        this.unfreezeApplyTime = unfreezeApplyTime == null ? null : unfreezeApplyTime.trim();
    }

    public String getUnfreezeReplyTime() {
        return unfreezeReplyTime;
    }

    public void setUnfreezeReplyTime(String unfreezeReplyTime) {
        this.unfreezeReplyTime = unfreezeReplyTime == null ? null : unfreezeReplyTime.trim();
    }

    public Integer getUnfreezeStatus() {
        return unfreezeStatus;
    }

    public void setUnfreezeStatus(Integer unfreezeStatus) {
        this.unfreezeStatus = unfreezeStatus;
    }

    public Integer getRecevieUserId() {
        return recevieUserId;
    }

    public void setRecevieUserId(Integer recevieUserId) {
        this.recevieUserId = recevieUserId;
    }

    public String getRecevieUserName() {
        return recevieUserName;
    }

    public void setRecevieUserName(String recevieUserName) {
        this.recevieUserName = recevieUserName == null ? null : recevieUserName.trim();
    }

    public Integer getRecevieUserPayChannelId() {
        return recevieUserPayChannelId;
    }

    public void setRecevieUserPayChannelId(Integer recevieUserPayChannelId) {
        this.recevieUserPayChannelId = recevieUserPayChannelId;
    }

    public String getUnfreezeNote() {
        return unfreezeNote;
    }

    public void setUnfreezeNote(String unfreezeNote) {
        this.unfreezeNote = unfreezeNote == null ? null : unfreezeNote.trim();
    }

    public Integer getPayChannelId() {
        return payChannelId;
    }

    public void setPayChannelId(Integer payChannelId) {
        this.payChannelId = payChannelId;
    }

    public BigDecimal getUnfreezeUserMoney() {
        return unfreezeUserMoney;
    }

    public void setUnfreezeUserMoney(BigDecimal unfreezeUserMoney) {
        this.unfreezeUserMoney = unfreezeUserMoney;
    }

    public BigDecimal getUnfreezeUserFreezeMoney() {
        return unfreezeUserFreezeMoney;
    }

    public void setUnfreezeUserFreezeMoney(BigDecimal unfreezeUserFreezeMoney) {
        this.unfreezeUserFreezeMoney = unfreezeUserFreezeMoney;
    }

    public String getUnfreezeClientId() {
        return unfreezeClientId;
    }

    public void setUnfreezeClientId(String unfreezeClientId) {
        this.unfreezeClientId = unfreezeClientId == null ? null : unfreezeClientId.trim();
    }
}