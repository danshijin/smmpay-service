package com.smmpay.inter.dto.req;

import java.io.Serializable;

/**
 * Created by tangshulei on 2015/11/5.
 */
public class UserAccountDTO implements Serializable{

    private static final long serialVersionUID = 382060932279824182L;
    private String userId;
    /**用户名**/
    private String userName;
    /**密码**/
    private String password;
    /**商城账号**/
    private String mallUserName;
    /**营业执照编号 **/
    private String certificateNo;
    /** 营业执照图片**/
    private String certificateUrl;
    /**	公司名称 **/
    private String companyName;
    /**公司地址**/
    private String companyAddr;
    /**联系人 **/
    private String contactName;
    /**联系人电话 **/
    private String phone;
    /**联系人手机 **/
    private String mobilePhone;
    /**邮编 **/
    private String postCode;
    /**开户所属银行 **/
    private String bankTypeId;
    /**开户行所在省 **/
    private String provinceId;
    /**开户行所在市 **/
    private String cityId;
    /**支行名称 **/
    private String bankId;
    /**开户账号 **/
    private String bankAccountNo;
    /**法人身份图片 **/
    private String idCardUrl;
    /**税务登记图片 **/
    private String registerCertificateUrl;

    private String registerIp;
    /**时间戳 **/
    private String date;

    private String authorizeUrl;

    public String getAuthorizeUrl() {
        return authorizeUrl;
    }

    public void setAuthorizeUrl(String authorizeUrl) {
        this.authorizeUrl = authorizeUrl;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getRegisterIp() {
        return registerIp;
    }

    public void setRegisterIp(String registerIp) {
        this.registerIp = registerIp;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getMallUserName() {
        return mallUserName;
    }

    public void setMallUserName(String mallUserName) {
        this.mallUserName = mallUserName;
    }

    public String getCertificateNo() {
        return certificateNo;
    }

    public void setCertificateNo(String certificateNo) {
        this.certificateNo = certificateNo;
    }

    public String getCertificateUrl() {
        return certificateUrl;
    }

    public void setCertificateUrl(String certificateUrl) {
        this.certificateUrl = certificateUrl;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getCompanyAddr() {
        return companyAddr;
    }

    public void setCompanyAddr(String companyAddr) {
        this.companyAddr = companyAddr;
    }

    public String getContactName() {
        return contactName;
    }

    public void setContactName(String contactName) {
        this.contactName = contactName;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getMobilePhone() {
        return mobilePhone;
    }

    public void setMobilePhone(String mobilePhone) {
        this.mobilePhone = mobilePhone;
    }

    public String getPostCode() {
        return postCode;
    }

    public void setPostCode(String postCode) {
        this.postCode = postCode;
    }

    public String getBankTypeId() {
        return bankTypeId;
    }

    public void setBankTypeId(String bankTypeId) {
        this.bankTypeId = bankTypeId;
    }

    public String getProvinceId() {
        return provinceId;
    }

    public void setProvinceId(String provinceId) {
        this.provinceId = provinceId;
    }

    public String getCityId() {
        return cityId;
    }

    public void setCityId(String cityId) {
        this.cityId = cityId;
    }

    public String getBankId() {
        return bankId;
    }

    public void setBankId(String bankId) {
        this.bankId = bankId;
    }

    public String getBankAccountNo() {
        return bankAccountNo;
    }

    public void setBankAccountNo(String bankAccountNo) {
        this.bankAccountNo = bankAccountNo;
    }

    public String getIdCardUrl() {
        return idCardUrl;
    }

    public void setIdCardUrl(String idCardUrl) {
        this.idCardUrl = idCardUrl;
    }

    public String getRegisterCertificateUrl() {
        return registerCertificateUrl;
    }

    public void setRegisterCertificateUrl(String registerCertificateUrl) {
        this.registerCertificateUrl = registerCertificateUrl;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
