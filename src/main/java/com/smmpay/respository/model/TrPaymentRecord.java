package com.smmpay.respository.model;

import java.math.BigDecimal;

public class TrPaymentRecord implements java.io.Serializable{
    public static String PAYMENT_STATUS_MSG_0 = "待付款";
    public static String PAYMENT_STATUS_MSG_1 = "已冻结";
    public static String PAYMENT_STATUS_MSG_2 = "已完成";
    public static String PAYMENT_STATUS_MSG_3 = "关闭";

    public static final int PAYMENT_STATUS_0 = 0;
    public static final int PAYMENT_STATUS_1 = 1;
    public static final int PAYMENT_STATUS_2 = 2;
    public static final int PAYMENT_STATUS_3 = 3;

    private Integer paymentId;

    private String paymentNo;
    private BigDecimal dealMoney;

    private String dealType;
    
    private Integer payType;

    private String buyerCompanyName;

    private String buyerMallUserName;

    private Integer buyerPayChannelId;

    private String buyerUserId;

    private String sellerCompanyName;

    private String sellerMallUserName;

    private Integer sellerPayChannelId;

    private String sellerUserId;

    private String mallOrderId;

    private String productName;

    private BigDecimal productNum;

    private String productNumUnit;

    private String productDetail;

    private String paymentCode;

    private Integer payChannelId;

    private Integer isHandler;

    private String freezeTime;

    private String doneTime;

    private String createTime;

    private String paymentTime;

    private String invoice;
    
    private String notifyUrl;

    private String paymentType;

    private String settlementType;

    private Integer paymentStatus;

    private String orderCreateTime;

    private String verifyCode;//数据加密验证
    
    
    
    public String getVerifyCode() {
		return verifyCode;
	}

	public void setVerifyCode(String verifyCode) {
		this.verifyCode = verifyCode;
	}

	public String getNotifyUrl() {
		return notifyUrl;
	}

	public void setNotifyUrl(String notifyUrl) {
		this.notifyUrl = notifyUrl;
	}

	public Integer getPayType() {
		return payType;
	}

	public void setPayType(Integer payType) {
		this.payType = payType;
	}

	public Integer getPaymentId() {
        return paymentId;
    }

    public void setPaymentId(Integer paymentId) {
        this.paymentId = paymentId;
    }

    public BigDecimal getDealMoney() {
        return dealMoney;
    }

    public void setDealMoney(BigDecimal dealMoney) {
        this.dealMoney = dealMoney;
    }

    public String getDealType() {
        return dealType;
    }

    public void setDealType(String dealType) {
        this.dealType = dealType == null ? null : dealType.trim();
    }

    public String getBuyerCompanyName() {
        return buyerCompanyName;
    }

    public void setBuyerCompanyName(String buyerCompanyName) {
        this.buyerCompanyName = buyerCompanyName == null ? null : buyerCompanyName.trim();
    }

    public String getBuyerMallUserName() {
        return buyerMallUserName;
    }

    public void setBuyerMallUserName(String buyerMallUserName) {
        this.buyerMallUserName = buyerMallUserName == null ? null : buyerMallUserName.trim();
    }

    public Integer getBuyerPayChannelId() {
        return buyerPayChannelId;
    }

    public void setBuyerPayChannelId(Integer buyerPayChannelId) {
        this.buyerPayChannelId = buyerPayChannelId;
    }

    public String getBuyerUserId() {
        return buyerUserId;
    }

    public void setBuyerUserId(String buyerUserId) {
        this.buyerUserId = buyerUserId == null ? null : buyerUserId.trim();
    }

    public String getSellerCompanyName() {
        return sellerCompanyName;
    }

    public void setSellerCompanyName(String sellerCompanyName) {
        this.sellerCompanyName = sellerCompanyName == null ? null : sellerCompanyName.trim();
    }

    public String getSellerMallUserName() {
        return sellerMallUserName;
    }

    public void setSellerMallUserName(String sellerMallUserName) {
        this.sellerMallUserName = sellerMallUserName == null ? null : sellerMallUserName.trim();
    }

    public Integer getSellerPayChannelId() {
        return sellerPayChannelId;
    }

    public void setSellerPayChannelId(Integer sellerPayChannelId) {
        this.sellerPayChannelId = sellerPayChannelId;
    }

    public String getSellerUserId() {
        return sellerUserId;
    }

    public void setSellerUserId(String sellerUserId) {
        this.sellerUserId = sellerUserId == null ? null : sellerUserId.trim();
    }

    public String getMallOrderId() {
        return mallOrderId;
    }

    public void setMallOrderId(String mallOrderId) {
        this.mallOrderId = mallOrderId == null ? null : mallOrderId.trim();
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName == null ? null : productName.trim();
    }

    public BigDecimal getProductNum() {
        return productNum;
    }

    public void setProductNum(BigDecimal productNum) {
        this.productNum = productNum;
    }

    public String getProductNumUnit() {
        return productNumUnit;
    }

    public void setProductNumUnit(String productNumUnit) {
        this.productNumUnit = productNumUnit == null ? null : productNumUnit.trim();
    }

    public String getProductDetail() {
        return productDetail;
    }

    public void setProductDetail(String productDetail) {
        this.productDetail = productDetail == null ? null : productDetail.trim();
    }

    public String getPaymentCode() {
        return paymentCode;
    }

    public void setPaymentCode(String paymentCode) {
        this.paymentCode = paymentCode == null ? null : paymentCode.trim();
    }

    public Integer getPayChannelId() {
        return payChannelId;
    }

    public void setPayChannelId(Integer payChannelId) {
        this.payChannelId = payChannelId;
    }

    public Integer getIsHandler() {
        return isHandler;
    }

    public void setIsHandler(Integer isHandler) {
        this.isHandler = isHandler;
    }

    public String getFreezeTime() {
        return freezeTime;
    }

    public void setFreezeTime(String freezeTime) {
        this.freezeTime = freezeTime == null ? null : freezeTime.trim();
    }

    public String getDoneTime() {
        return doneTime;
    }

    public void setDoneTime(String doneTime) {
        this.doneTime = doneTime == null ? null : doneTime.trim();
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime == null ? null : createTime.trim();
    }

    public String getPaymentTime() {
        return paymentTime;
    }

    public void setPaymentTime(String paymentTime) {
        this.paymentTime = paymentTime == null ? null : paymentTime.trim();
    }

    public String getInvoice() {
        return invoice;
    }

    public void setInvoice(String invoice) {
        this.invoice = invoice == null ? null : invoice.trim();
    }

    public String getPaymentType() {
        return paymentType;
    }

    public void setPaymentType(String paymentType) {
        this.paymentType = paymentType == null ? null : paymentType.trim();
    }

    public String getSettlementType() {
        return settlementType;
    }

    public void setSettlementType(String settlementType) {
        this.settlementType = settlementType == null ? null : settlementType.trim();
    }

    public Integer getPaymentStatus() {
        return paymentStatus;
    }

    public void setPaymentStatus(Integer paymentStatus) {
        this.paymentStatus = paymentStatus;
    }

    public String getOrderCreateTime() {
        return orderCreateTime;
    }

    public void setOrderCreateTime(String orderCreateTime) {
        this.orderCreateTime = orderCreateTime == null ? null : orderCreateTime.trim();
    }

	public String getPaymentNo() {
		return paymentNo;
	}

	public void setPaymentNo(String paymentNo) {
		this.paymentNo = paymentNo;
	}

	@Override
	public String toString() {
		return "TrPaymentRecord [paymentId=" + paymentId + ", paymentNo="
				+ paymentNo + ", dealMoney=" + dealMoney + ", dealType="
				+ dealType + ", payType=" + payType + ", buyerCompanyName="
				+ buyerCompanyName + ", buyerMallUserName=" + buyerMallUserName
				+ ", buyerPayChannelId=" + buyerPayChannelId + ", buyerUserId="
				+ buyerUserId + ", sellerCompanyName=" + sellerCompanyName
				+ ", sellerMallUserName=" + sellerMallUserName
				+ ", sellerPayChannelId=" + sellerPayChannelId
				+ ", sellerUserId=" + sellerUserId + ", mallOrderId="
				+ mallOrderId + ", productName=" + productName
				+ ", productNum=" + productNum + ", productNumUnit="
				+ productNumUnit + ", productDetail=" + productDetail
				+ ", paymentCode=" + paymentCode + ", payChannelId="
				+ payChannelId + ", isHandler=" + isHandler + ", freezeTime="
				+ freezeTime + ", doneTime=" + doneTime + ", createTime="
				+ createTime + ", paymentTime=" + paymentTime + ", invoice="
				+ invoice + ", notifyUrl=" + notifyUrl + ", paymentType="
				+ paymentType + ", settlementType=" + settlementType
				+ ", paymentStatus=" + paymentStatus + ", orderCreateTime="
				+ orderCreateTime + "]";
	}
    
    
}