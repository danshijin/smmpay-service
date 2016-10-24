package com.smmpay.internal.service;

import com.smmpay.common.ResErrorCode;
import com.smmpay.inter.dto.req.*;
import com.smmpay.inter.dto.res.ResPayDetail;
import com.smmpay.inter.dto.res.ResPaymentRecordDTO;
import com.smmpay.inter.dto.res.ResUserBindBank;
import com.smmpay.inter.dto.res.ReturnDTO;
import com.smmpay.respository.model.TrPaymentRecord;
import com.smmpay.respository.model.UserAccount;
import com.smmpay.respository.model.UserBindBank;
import com.smmpay.util.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

import static com.smmpay.respository.model.TrPaymentRecord.*;

/**
 * Created by tangshulei on 2015/11/6.
 */
@Service("validDTO")
public class ValidDTO {

    public static String DATE_FORMAT = "YYYY-MM-dd";

    @Autowired
    UAService uaService;

    public BigDecimal getFee(BigDecimal total){
        BigDecimal fee_1 = new BigDecimal(5.50);
        BigDecimal fee_2 = new BigDecimal(10.50);
        BigDecimal fee_3 = new BigDecimal(15.50);
        BigDecimal fee_4 = new BigDecimal(20.50);
        BigDecimal fee_5 = new BigDecimal(0.5);
        BigDecimal fee;
        BigDecimal fee_6 = new BigDecimal(0.00002);
        BigDecimal one = new BigDecimal(10000);
        BigDecimal ten = new BigDecimal(100000);
        BigDecimal five = new BigDecimal(500000);
        BigDecimal oneTen = new BigDecimal(1000000);
        if(total.compareTo(one) <= 0){
            return fee_1;
        }
        if(total.compareTo(one) == 1 && total.compareTo(ten) <= 0){
            return fee_2;
        }
        if(total.compareTo(ten) == 1 && total.compareTo(five) <= 0){
            return fee_3;
        }
        if(total.compareTo(five) == 1 && total.compareTo(oneTen) <= 0){
            return fee_4;
        }
        if(total.compareTo(oneTen) == 1){
            fee = fee_6.multiply(total).add(fee_5);
            fee = fee.setScale(2, BigDecimal.ROUND_HALF_UP);
            return fee;
        }
        return fee_1;
    }

    public static void main(String[] args){
        ValidDTO v = new ValidDTO();
        BigDecimal a = new BigDecimal(1000001);
        System.out.println(v.getFee(a));
    }

    /**
     * 注册DTO验证
     * @param userAccountDTO
     * @return
     */
    public String validRegister(UserAccountDTO userAccountDTO){
        if(userAccountDTO.getUserName() == null || "".equals(userAccountDTO.getUserName())){
            return ResErrorCode.USERACCOUNT_ERROR_MSG_1;
        }
        if(userAccountDTO.getPassword() == null || "".equals(userAccountDTO.getPassword())){
            return ResErrorCode.USERACCOUNT_ERROR_MSG_2;
        }
        if(userAccountDTO.getMallUserName() == null || "".equals(userAccountDTO.getMallUserName())){
            return ResErrorCode.USERACCOUNT_ERROR_MSG_3;
        }
        if(userAccountDTO.getCertificateNo() == null || "".equals(userAccountDTO.getCertificateNo())){
            return ResErrorCode.USERACCOUNT_ERROR_MSG_4;
        }
        if(userAccountDTO.getCertificateUrl() == null || "".equals(userAccountDTO.getCertificateUrl())){
            return ResErrorCode.USERACCOUNT_ERROR_MSG_5;
        }
        if(userAccountDTO.getCompanyName() == null || "".equals(userAccountDTO.getCompanyName())){
            return ResErrorCode.USERACCOUNT_ERROR_MSG_6;
        }if(userAccountDTO.getCompanyAddr() == null || "".equals(userAccountDTO.getCompanyAddr())){
            return ResErrorCode.USERACCOUNT_ERROR_MSG_7;
        }if(userAccountDTO.getContactName() == null || "".equals(userAccountDTO.getContactName())){
            return ResErrorCode.USERACCOUNT_ERROR_MSG_8;
        }if(userAccountDTO.getPhone() == null || "".equals(userAccountDTO.getPhone())){
            return ResErrorCode.USERACCOUNT_ERROR_MSG_9;
        }
        if(userAccountDTO.getMobilePhone() == null || "".equals(userAccountDTO.getMobilePhone())){
            return ResErrorCode.USERACCOUNT_ERROR_MSG_10;
        }if(userAccountDTO.getPostCode() == null || "".equals(userAccountDTO.getPostCode())){
            return ResErrorCode.USERACCOUNT_ERROR_MSG_11;
        }
        if(userAccountDTO.getBankTypeId() == null || "".equals(userAccountDTO.getBankTypeId())){
            return ResErrorCode.USERACCOUNT_ERROR_MSG_12;
        }
        if(userAccountDTO.getProvinceId() == null || "".equals(userAccountDTO.getProvinceId())){
            return ResErrorCode.USERACCOUNT_ERROR_MSG_13;
        }
        if(!StringUtils.isNumeric(userAccountDTO.getProvinceId())){
            return ResErrorCode.USERACCOUNT_ERROR_MSG_21;
        }
        if(userAccountDTO.getCityId() == null || "".equals(userAccountDTO.getCityId())){
            return ResErrorCode.USERACCOUNT_ERROR_MSG_14;
        }

        if(!StringUtils.isNumeric(userAccountDTO.getCityId())){
            return ResErrorCode.USERACCOUNT_ERROR_MSG_22;
        }
        if(userAccountDTO.getBankId() == null || "".equals(userAccountDTO.getBankId())){
            return ResErrorCode.USERACCOUNT_ERROR_MSG_15;
        }
        if(!StringUtils.isNumeric(userAccountDTO.getBankId())){
            return ResErrorCode.USERACCOUNT_ERROR_MSG_24;
        }
        if(userAccountDTO.getBankAccountNo() == null || "".equals(userAccountDTO.getBankAccountNo())){
            return ResErrorCode.USERACCOUNT_ERROR_MSG_16;
        }
        if(userAccountDTO.getIdCardUrl() == null || "".equals(userAccountDTO.getIdCardUrl())){
            return ResErrorCode.USERACCOUNT_ERROR_MSG_17;
        }
        if(userAccountDTO.getRegisterCertificateUrl() == null || "".equals(userAccountDTO.getRegisterCertificateUrl())){
            return ResErrorCode.USERACCOUNT_ERROR_MSG_18;
        }
        if(userAccountDTO.getDate() == null || "".equals(userAccountDTO.getDate())){
            return ResErrorCode.USERACCOUNT_ERROR_MSG_19;
        }
        if(userAccountDTO.getRegisterIp() == null || "".equals(userAccountDTO.getRegisterIp())){
            return ResErrorCode.USERACCOUNT_ERROR_MSG_20;
        }
        if(userAccountDTO.getAuthorizeUrl() == null || "".equals(userAccountDTO.getAuthorizeUrl())){
            return ResErrorCode.USERACCOUNT_ERROR_MSG_26;
        }
        return null;
    }

    /**
     * 查询用户是否存在DTO验证
     * @param checkUserDTO
     * @return
     */
    public String validCheckUser(CheckUserDTO checkUserDTO){
        if(checkUserDTO.getCheckType() == null || "".equals(checkUserDTO.getCheckType())){
            return ResErrorCode.CHECKUSER_ERROR_MSG_1;
        }
        if(!CheckUserDTO.CUDTO_CHECKTYPE_1.equals(checkUserDTO.getCheckType()) && !CheckUserDTO.CUDTO_CHECKTYPE_2.equals(checkUserDTO.getCheckType())){
            return ResErrorCode.CHECKUSER_ERROR_MSG_4;
        }
        if(CheckUserDTO.CUDTO_CHECKTYPE_1.equals(checkUserDTO.getCheckType())){
            if(checkUserDTO.getUserName() == null || "".equals(checkUserDTO.getUserName())){
                return ResErrorCode.CHECKUSER_ERROR_MSG_2;
            }
        }
        if(CheckUserDTO.CUDTO_CHECKTYPE_2.equals(checkUserDTO.getCheckType())){
            if(checkUserDTO.getMallUserName() == null || "".equals(checkUserDTO.getMallUserName())){
                return ResErrorCode.CHECKUSER_ERROR_MSG_3;
            }
        }

        return null;
    }

    /**
     * 修改密码DTO验证
     * @param updateUserPasswordDTO
     */
    public String updatePassword(UpdateUserPasswordDTO updateUserPasswordDTO){
        if(updateUserPasswordDTO.getUserId() == null || "".equals(updateUserPasswordDTO.getUserId())){
            return ResErrorCode.UPDATEPASSWORD_ERROR_MSG_1;
        }
        if(!StringUtils.isNumeric(updateUserPasswordDTO.getUserId())){
            return ResErrorCode.UPDATEPASSWORD_ERROR_MSG_5;
        }
        if(updateUserPasswordDTO.getUserName() == null || "".equals(updateUserPasswordDTO.getUserName())){
            return ResErrorCode.UPDATEPASSWORD_ERROR_MSG_2;
        }
        if(updateUserPasswordDTO.getOldPassword() == null || "".equals(updateUserPasswordDTO.getOldPassword())){
            return ResErrorCode.UPDATEPASSWORD_ERROR_MSG_3;
        }
        if(updateUserPasswordDTO.getNewPassword() == null || "".equals(updateUserPasswordDTO.getNewPassword())){
            return ResErrorCode.UPDATEPASSWORD_ERROR_MSG_4;
        }
        return null;
    }

    public  String  validUserIdAndName(String userId, String userName){
        if(userId == null || "".equals(userId)){
            return ResErrorCode.USERID_ERROR_MSG_1;
        }
        if(!StringUtils.isNumeric(userId)){
            return ResErrorCode.USERID_ERROR_MSG_2;
        }
        if(userName == null || "".equals(userName)){
            return ResErrorCode.USERNAME_ERROR_MSG_1;
        }
        return null;
    }

    /**
     * 驗證绑定银行卡
     * @param userAccountDTO
     * @return
     */
    public String validBindBank(UserAccountDTO userAccountDTO){
        if(userAccountDTO.getUserId() == null || "".equals(userAccountDTO.getUserId())){
            return ResErrorCode.USERID_ERROR_MSG_1;
        }
        if(!StringUtils.isNumeric(userAccountDTO.getUserId())){
            return ResErrorCode.USERID_ERROR_MSG_2;
        }
        if(userAccountDTO.getUserName() == null || "".equals(userAccountDTO.getUserName())){
            return ResErrorCode.USERACCOUNT_ERROR_MSG_1;
        }
        if(userAccountDTO.getBankTypeId() == null || "".equals(userAccountDTO.getBankTypeId())){
            return ResErrorCode.USERACCOUNT_ERROR_MSG_12;
        }
        if(!StringUtils.isNumeric(userAccountDTO.getBankTypeId())){
            return ResErrorCode.USERACCOUNT_ERROR_MSG_23;
        }
        if(userAccountDTO.getProvinceId() == null || "".equals(userAccountDTO.getProvinceId())){
            return ResErrorCode.USERACCOUNT_ERROR_MSG_13;
        }
        if(!StringUtils.isNumeric(userAccountDTO.getProvinceId())){
            return ResErrorCode.USERACCOUNT_ERROR_MSG_21;
        }
        if(userAccountDTO.getCityId() == null || "".equals(userAccountDTO.getCityId())){
            return ResErrorCode.USERACCOUNT_ERROR_MSG_14;
        }

        if(!StringUtils.isNumeric(userAccountDTO.getCityId())){
            return ResErrorCode.USERACCOUNT_ERROR_MSG_22;
        }
        if(userAccountDTO.getBankId() == null || "".equals(userAccountDTO.getBankId())){
            return ResErrorCode.USERACCOUNT_ERROR_MSG_15;
        }
        if(!StringUtils.isNumeric(userAccountDTO.getBankId())){
            return ResErrorCode.USERACCOUNT_ERROR_MSG_24;
        }
        if(userAccountDTO.getBankAccountNo() == null || "".equals(userAccountDTO.getBankAccountNo())){
            return ResErrorCode.USERACCOUNT_ERROR_MSG_16;
        }
        return null;
    }

    /**
     * 验证绑卡金额
     * @param checkBankMoneyDTO
     */
    public String validBankMoney(CheckBankMoneyDTO checkBankMoneyDTO){
        if(checkBankMoneyDTO.getUserId() == null || "".equals(checkBankMoneyDTO.getUserId())){
            return ResErrorCode.USERID_ERROR_MSG_1;
        }
        if(!StringUtils.isNumeric(checkBankMoneyDTO.getUserId())){
            return ResErrorCode.USERID_ERROR_MSG_2;
        }
        if(checkBankMoneyDTO.getUserName() == null || "".equals(checkBankMoneyDTO.getUserName())){
            return ResErrorCode.USERNAME_ERROR_MSG_1;
        }
        if(checkBankMoneyDTO.getBindId() == null || "".equals(checkBankMoneyDTO.getBindId())){
            return ResErrorCode.BANDID_ERROR_MSG_1;
        }
        if(!StringUtils.isNumeric(checkBankMoneyDTO.getBindId())){
            return ResErrorCode.BANDID_ERROR_MSG_2;
        }
        if(checkBankMoneyDTO.getMoney() == null || "".equals(checkBankMoneyDTO.getMoney())){
            return ResErrorCode.BANDMONEY_ERROR_MSG_1;
        }
        if(!StringUtils.isDecimal(checkBankMoneyDTO.getMoney())){
            return ResErrorCode.BANDMONEY_ERROR_MSG_2;
        }

        return null;
    }

    public String validGetPaymentRecord(TradingRecordDTO tradingRecordDTO){
        if(tradingRecordDTO.getUserId() == null || "".equals(tradingRecordDTO.getUserId())){
            return ResErrorCode.USERID_ERROR_MSG_1;
        }
        if(!StringUtils.isNumeric(tradingRecordDTO.getUserId())){
            return ResErrorCode.USERID_ERROR_MSG_2;
        }
        if(tradingRecordDTO.getUserName() == null || "".equals(tradingRecordDTO.getUserName())){
            return ResErrorCode.USERNAME_ERROR_MSG_1;
        }
        if(tradingRecordDTO.getIsBuy() == null || "".equals(tradingRecordDTO.getIsBuy())){
            return ResErrorCode.GETPAYRECORD_ERROR_MSG_1;
        }
        if(!StringUtils.isNumeric(tradingRecordDTO.getIsBuy())){
            return ResErrorCode.GETPAYRECORD_ERROR_MSG_2;
        }
        if(tradingRecordDTO.getPaymentStatus() == null || "".equals(tradingRecordDTO.getPaymentStatus())){
            return ResErrorCode.GETPAYRECORD_ERROR_MSG_3;
        }
        if(!StringUtils.isNumeric(tradingRecordDTO.getPaymentStatus())){
            return ResErrorCode.GETPAYRECORD_ERROR_MSG_4;
        }
//        if(tradingRecordDTO.getStartDate() == null || "".equals(tradingRecordDTO.getStartDate())){
//            return ResErrorCode.GETPAYRECORD_ERROR_MSG_5;
//        }
        if(tradingRecordDTO.getStartDate() != null && !"".equals(tradingRecordDTO.getStartDate())) {
            if (!StringUtils.validDate(tradingRecordDTO.getStartDate(), DATE_FORMAT)) {
                return ResErrorCode.GETPAYRECORD_ERROR_MSG_7;
            }
        }
        if(tradingRecordDTO.getEndDate() == null || "".equals(tradingRecordDTO.getEndDate())){
            return ResErrorCode.GETPAYRECORD_ERROR_MSG_6;
        }
        if(!StringUtils.validDate(tradingRecordDTO.getEndDate(), DATE_FORMAT)){
            return ResErrorCode.GETPAYRECORD_ERROR_MSG_8;
        }
        if(tradingRecordDTO.getPage() == null || "".equals(tradingRecordDTO.getPage())){
            return ResErrorCode.GETPAYRECORD_ERROR_MSG_9;
        }
        if(!StringUtils.isNumeric(tradingRecordDTO.getPage())){
            return ResErrorCode.GETPAYRECORD_ERROR_MSG_10;
        }
        if(tradingRecordDTO.getPageSize() == null || "".equals(tradingRecordDTO.getPageSize())){
            return ResErrorCode.GETPAYRECORD_ERROR_MSG_11;
        }
        if(!StringUtils.isNumeric(tradingRecordDTO.getPageSize())){
            return ResErrorCode.GETPAYRECORD_ERROR_MSG_12;
        }
        return null;
    }

    public String validPage(String startDate, String endDate, String page, String pageSize){
        if(startDate == null || "".equals(startDate)){
            return ResErrorCode.GETPAYRECORD_ERROR_MSG_5;
        }
        if(!StringUtils.validDate(startDate, DATE_FORMAT)){
            return ResErrorCode.GETPAYRECORD_ERROR_MSG_7;
        }
        if(endDate == null || "".equals(endDate)){
            return ResErrorCode.GETPAYRECORD_ERROR_MSG_6;
        }
        if(!StringUtils.validDate(endDate, DATE_FORMAT)){
            return ResErrorCode.GETPAYRECORD_ERROR_MSG_8;
        }
        if(page == null || "".equals(page)){
            return ResErrorCode.GETPAYRECORD_ERROR_MSG_9;
        }
        if(!StringUtils.isNumeric(page)){
            return ResErrorCode.GETPAYRECORD_ERROR_MSG_10;
        }
        if(pageSize == null || "".equals(pageSize)){
            return ResErrorCode.GETPAYRECORD_ERROR_MSG_11;
        }
        if(!StringUtils.isNumeric(pageSize)){
            return ResErrorCode.GETPAYRECORD_ERROR_MSG_12;
        }
        return null;
    }

    public String validGetCash(GetCashDTO getCashDTO){
        if(getCashDTO.getCashBankId() == null || "".equals(getCashDTO.getCashBankId())){
            return ResErrorCode.GETCASH_ERROR_MSG_1;
        }
        if(!StringUtils.isNumeric(getCashDTO.getCashBankId())){
            return ResErrorCode.GETCASH_ERROR_MSG_2;
        }
        if(getCashDTO.getCashMoney() == null || "".equals(getCashDTO.getCashMoney())){
            return ResErrorCode.GETCASH_ERROR_MSG_3;
        }
        if(!StringUtils.isDecimal(getCashDTO.getCashMoney())){
            return ResErrorCode.GETCASH_ERROR_MSG_4;
        }
        if(getCashDTO.getCashType() == null || "".equals(getCashDTO.getCashType())){
            return ResErrorCode.GETCASH_ERROR_MSG_5;
        }
        if(!StringUtils.isNumeric(getCashDTO.getCashType())){
            return ResErrorCode.GETCASH_ERROR_MSG_6;
        }
        if(getCashDTO.getPayChannelId() == null || "".equals(getCashDTO.getPayChannelId())){
            return ResErrorCode.GETCASH_ERROR_MSG_7;
        }
        if(!StringUtils.isNumeric(getCashDTO.getPayChannelId())){
            return ResErrorCode.GETCASH_ERROR_MSG_8;
        }
        return null;
    }

    public void copyUserAccount(UserAccount userAccount, UserAccountDTO userAccountDTO){
        userAccount.setUserName(userAccountDTO.getUserName());
        userAccount.setPassword(userAccountDTO.getPassword());
        userAccount.setCertificateNo(userAccountDTO.getCertificateNo());
        userAccount.setCertificateUrl(userAccountDTO.getCertificateUrl());
        userAccount.setCompanyAddr(userAccountDTO.getCompanyAddr());
        userAccount.setCompanyName(userAccountDTO.getCompanyName());
        userAccount.setCompanyPostCode(userAccountDTO.getPostCode());
        userAccount.setContactName(userAccountDTO.getContactName());
        userAccount.setIdCardUrl(userAccountDTO.getIdCardUrl());
        userAccount.setMallUserName(userAccountDTO.getMallUserName());
        userAccount.setMobilePhone(userAccountDTO.getMobilePhone());
        userAccount.setPhone(userAccountDTO.getPhone());
        userAccount.setRegisteryCertificateUrl(userAccountDTO.getRegisterCertificateUrl());
        userAccount.setRegisterIp(userAccountDTO.getRegisterIp());
        userAccount.setAuthorizeUrl(userAccountDTO.getAuthorizeUrl());
    }

    public void copyUserBindBank(UserBindBank userBindBank,UserAccountDTO userAccountDTO){
        userBindBank.setBankId(Integer.valueOf(userAccountDTO.getBankId()));
        userBindBank.setBankTypeId(Integer.valueOf(userAccountDTO.getBankTypeId()));
        userBindBank.setBankAccountNo(userAccountDTO.getBankAccountNo());
        userBindBank.setBankAreaProvince(Integer.valueOf(userAccountDTO.getProvinceId()));
        userBindBank.setBankAreaCity(Integer.valueOf(userAccountDTO.getCityId()));
    }

    public void copyResUserBindBank(UserBindBank userBindBank, ResUserBindBank resUserBindBank){
        resUserBindBank.setAuditStatus(userBindBank.getAuditStatus() + "");
//        switch (userBindBank.getAuditStatus()) {
//            case UserBindBank.UB_AUDIT_STATUS_0:
//                    resUserBindBank.setAuditStatus(UserBindBank.UB_AUDIT_STATUS_STR_0) ;
//            case UserBindBank.UB_AUDIT_STATUS_1:
//                resUserBindBank.setAuditStatus(UserBindBank.UB_AUDIT_STATUS_STR_1) ;
//            case UserBindBank.UB_AUDIT_STATUS_2:
//                resUserBindBank.setAuditStatus(UserBindBank.UB_AUDIT_STATUS_STR_2) ;
//        }
        resUserBindBank.setIsPayment(userBindBank.getIsPayment() + "");
//        switch (userBindBank.getIsPayment()){
//            case UserBindBank.UB_IS_PAYMENT_0:
//                resUserBindBank.setIsPayment(UserBindBank.UB_IS_PAYMENT_STR_0);
//            case UserBindBank.UB_IS_PAYMENT_1:
//                resUserBindBank.setIsPayment(UserBindBank.UB_IS_PAYMENT_STR_1);
//        }
        resUserBindBank.setBankAccountNo(userBindBank.getBankAccountNo());
        resUserBindBank.setBankName(userBindBank.getBankName());
        resUserBindBank.setBindBank(userBindBank.getBankType());
        resUserBindBank.setBindId(userBindBank.getBindId() + "");
        resUserBindBank.setCity(userBindBank.getBankAreaCity() + "");
        resUserBindBank.setProvince(userBindBank.getBankAreaProvince() + "");
        resUserBindBank.setBindTypeId(userBindBank.getBankTypeId() + "");
        resUserBindBank.setBindTime(userBindBank.getBindTime());
    }

    public void copyResTradingRecordDTO(TrPaymentRecord trPaymentRecord, ResPaymentRecordDTO resTradingRecordDTO){
        resTradingRecordDTO.setPaymentId(trPaymentRecord.getPaymentId());
        resTradingRecordDTO.setMallOrderId(trPaymentRecord.getMallOrderId());
        resTradingRecordDTO.setApplyTime(trPaymentRecord.getCreateTime());
        resTradingRecordDTO.setProductName(trPaymentRecord.getProductName());
        resTradingRecordDTO.setProductDetail(trPaymentRecord.getProductDetail());
        resTradingRecordDTO.setDealMoney(trPaymentRecord.getDealMoney());
        resTradingRecordDTO.setPaymentNo(trPaymentRecord.getPaymentNo());
        resTradingRecordDTO.setPaymentStatus(trPaymentRecord.getPaymentStatus() + "");
//        switch(trPaymentRecord.getPaymentStatus()){
//            case TrPaymentRecord.PAYMENT_STATUS_0:
//                resTradingRecordDTO.setPaymentStatus(PAYMENT_STATUS_MSG_0);
//            case TrPaymentRecord.PAYMENT_STATUS_1:
//                resTradingRecordDTO.setPaymentStatus(PAYMENT_STATUS_MSG_1);
//            case TrPaymentRecord.PAYMENT_STATUS_2:
//                resTradingRecordDTO.setPaymentStatus(PAYMENT_STATUS_MSG_2);
//            case TrPaymentRecord.PAYMENT_STATUS_3:
//                resTradingRecordDTO.setPaymentStatus(PAYMENT_STATUS_MSG_3);
//        }

    }

    public void copyResPayDetail(TrPaymentRecord trPaymentRecord, ResPayDetail resPayDetail){
        resPayDetail.setPaymentId(trPaymentRecord.getPaymentId());
        resPayDetail.setProductName(trPaymentRecord.getProductName());
        resPayDetail.setProductDetail(trPaymentRecord.getProductDetail());
        resPayDetail.setProductNum(trPaymentRecord.getProductNum());
        resPayDetail.setDealMoney(trPaymentRecord.getDealMoney());
        resPayDetail.setCreateTime(trPaymentRecord.getCreateTime());
        resPayDetail.setSettlementType(trPaymentRecord.getSettlementType());
        resPayDetail.setDealType(trPaymentRecord.getDealType());
        resPayDetail.setInvoice(trPaymentRecord.getInvoice());
        resPayDetail.setPaymentType(trPaymentRecord.getPaymentType());
        resPayDetail.setPaymentTime(trPaymentRecord.getPaymentTime());
        resPayDetail.setPaymentCode(trPaymentRecord.getPaymentCode());
        resPayDetail.setMallOrderId(trPaymentRecord.getMallOrderId());
        resPayDetail.setPaymentStatus(trPaymentRecord.getPaymentStatus());
        resPayDetail.setDoneTime(trPaymentRecord.getDoneTime());
        resPayDetail.setFreezeTime(trPaymentRecord.getFreezeTime());
        resPayDetail.setOrderCreateTime(trPaymentRecord.getOrderCreateTime());
        resPayDetail.setPaymentNo(trPaymentRecord.getPaymentNo());
        resPayDetail.setPayType(trPaymentRecord.getPayType());
//        update zhenghao  2015-12-24
        resPayDetail.setBuyerUserId(trPaymentRecord.getBuyerUserId());
        resPayDetail.setSellerUserId(trPaymentRecord.getSellerUserId());
    }

    public ReturnDTO returnError(String code, String message, ReturnDTO returnDTO){
        returnDTO.setStatus(code);
        returnDTO.setMsg(message);
        returnDTO.setSuccess(false);
        return returnDTO;
    }

    public ReturnDTO validUserIdAndUserName(String userId, String userName){
        ReturnDTO returnDTO = new ReturnDTO();
        /**
         * 判断参数
         */
        String msg = validUserIdAndName(userId, userName);
        if(msg != null){
            //参数错误，直接返回
            returnDTO = returnError(ResErrorCode.VALIDPARAM_ERROR_CODE, msg, returnDTO);
            return returnDTO;
        }

        UserAccount userAccountDTO = new UserAccount();
        userAccountDTO.setUserId(Integer.valueOf(userId));
        userAccountDTO.setUserName(userName);
        /**验证用户名 id **/
        boolean isValid = uaService.validUserByUserIdAndUserName(userAccountDTO);
        if(!isValid){
            returnDTO = returnError(ResErrorCode.LOGIN_ERROR_VALID_CODE, ResErrorCode.LOGIN_ERROR_VALID_MSG, returnDTO);
            return returnDTO;
        }

        return returnDTO;
    }

}
