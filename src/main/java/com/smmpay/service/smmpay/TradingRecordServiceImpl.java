package com.smmpay.service.smmpay;

import com.smmpay.common.ResErrorCode;
import com.smmpay.inter.dto.req.TradingRecordDTO;
import com.smmpay.inter.dto.res.PagReturnDTO;
import com.smmpay.inter.dto.res.ResPayDetail;
import com.smmpay.inter.dto.res.ResPaymentRecordDTO;
import com.smmpay.inter.dto.res.ReturnDTO;
import com.smmpay.inter.smmpay.TradingRecordService;
import com.smmpay.inter.smmpay.UserAccountService;
import com.smmpay.internal.service.TRService;
import com.smmpay.internal.service.UAService;
import com.smmpay.internal.service.ValidDTO;
import com.smmpay.payment.Payment;
import com.smmpay.respository.model.TrPaymentRecord;
import com.smmpay.respository.model.UserPayChannel;
import com.smmpay.util.StringUtils;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by tangshulei on 2015/11/10.
 */
@Service("tradingRecordService")
public class TradingRecordServiceImpl implements TradingRecordService {

    Logger log = Logger.getLogger(this.getClass());

    @Autowired
    ValidDTO validDTO;

    @Autowired
    TRService trService;

    @Autowired
    Payment payment;

    @Autowired
    UAService uaService;

    /**
     * 支付记录查询
     *
     * @param map
     * @return
     */
    public ReturnDTO getPaymentRecord(Map<String, String> map) {
        ReturnDTO returnDTO = new ReturnDTO();
        TradingRecordDTO tradingRecordDTO = new TradingRecordDTO();
        try {
            BeanUtils.populate(tradingRecordDTO, map);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }

        /**
         * 判断参数
         */
        String msg = validDTO.validGetPaymentRecord(tradingRecordDTO);
        if (msg != null) {
            //参数错误，直接返回
            validDTO.returnError(ResErrorCode.VALIDPARAM_ERROR_CODE, msg, returnDTO);
            return returnDTO;
        }
        /**验证userID userName **/
        returnDTO = validDTO.validUserIdAndUserName(tradingRecordDTO.getUserId(), tradingRecordDTO.getUserName());
        if (!returnDTO.isSuccess()) {
            return returnDTO;
        }

        /**设置时间，分页 **/
        if(tradingRecordDTO.getStartDate() != null && !"".equals(tradingRecordDTO.getStartDate())) {
            tradingRecordDTO.setStartDate(tradingRecordDTO.getStartDate() + " 00:00:00");
        }
        tradingRecordDTO.setEndDate(tradingRecordDTO.getEndDate() + " 23:59:59");
        int pageSize = Integer.valueOf(tradingRecordDTO.getPageSize());
        int page = Integer.valueOf(tradingRecordDTO.getPage());
        if (page <= 0) {
            page = 1;
        }
        if (pageSize <= 0) {
            pageSize = 10;
        }
        tradingRecordDTO.setStartValue((page - 1) * pageSize);
        tradingRecordDTO.setPageSizeValue(pageSize);
        List<TrPaymentRecord> list = new ArrayList<TrPaymentRecord>();
        List<ResPaymentRecordDTO> resList = new ArrayList<ResPaymentRecordDTO>();
        int countSize = 0;
        try {
            list = trService.getTrPaymentRecordList(tradingRecordDTO);
            countSize = trService.countTrPaymentRecordList(tradingRecordDTO);
        } catch (Exception e) {
            log.error(ResErrorCode.GETPAYRECORD_ERROR_CODE, e);
            validDTO.returnError(ResErrorCode.GETPAYRECORD_ERROR_CODE, ResErrorCode.GETPAYRECORD_ERROR_MSG, returnDTO);
        }


        /**组装返回类 **/
        for (TrPaymentRecord trPaymentRecord : list) {
            ResPaymentRecordDTO resTradingRecordDTO = new ResPaymentRecordDTO();
            validDTO.copyResTradingRecordDTO(trPaymentRecord, resTradingRecordDTO);
            if (trPaymentRecord.getBuyerUserId().equals(tradingRecordDTO.getUserId())) {
                resTradingRecordDTO.setSellOrBuy(1);
                resTradingRecordDTO.setOpposit(trPaymentRecord.getSellerCompanyName());
            } else {
                resTradingRecordDTO.setSellOrBuy(2);
                resTradingRecordDTO.setOpposit(trPaymentRecord.getBuyerCompanyName());
            }
            resList.add(resTradingRecordDTO);
        }
        Map<String, Object> resMap = new HashMap<String, Object>();
        resMap.put("count", countSize);
        resMap.put("list", resList);
        returnDTO.setData(resMap);
        return returnDTO;
    }

    /**
     * 支付详情查询
     *
     * @param map
     * @return
     */
    public ReturnDTO getPaymentDetail(Map<String, String> map) {
        String userId = map.get("userId");
        String userName = map.get("userName");
        String paymentNo = map.get("paymentNo");
        /**验证userID userName **/
        ReturnDTO returnDTO = validDTO.validUserIdAndUserName(userId, userName);
        if (!returnDTO.isSuccess()) {
            return returnDTO;
        }
        /**验证参数 **/
        if (paymentNo == null || "".equals(paymentNo)) {
            returnDTO = validDTO.returnError(ResErrorCode.VALIDPARAM_ERROR_CODE, ResErrorCode.GETPAYDETAIL_ERROR_MSG_1, returnDTO);
            return returnDTO;
        }
       
        TrPaymentRecord trPaymentRecord = null;
        /**查询数据 **/
        try {
            trPaymentRecord = trService.getTrPaymentRecordDetailByNo(paymentNo);
        } catch (Exception e) {
            log.error(ResErrorCode.GETPAYDETAIL_ERROR_MSG);
            return validDTO.returnError(ResErrorCode.GETPAYDETAIL_ERROR_CODE, ResErrorCode.GETPAYDETAIL_ERROR_MSG, returnDTO);
        }
        if (trPaymentRecord == null) {
            return validDTO.returnError(ResErrorCode.GETPAYDETAIL_ERROR_NO_CODE, ResErrorCode.GETPAYDETAIL_ERROR_NO_MSG, returnDTO);
        }

        /**用户是否属于当前交易 **/
        if (!userId.equals(trPaymentRecord.getSellerUserId()) && !userId.equals(trPaymentRecord.getBuyerUserId())) {
            return validDTO.returnError(ResErrorCode.GETPAYDETAIL_ERROR_NO_AUTH_CODE, ResErrorCode.GETPAYDETAIL_ERROR_NO_AUTH_MSG, returnDTO);
        }

        /**封装返回数据 **/
        ResPayDetail resPayDetail = new ResPayDetail();
        validDTO.copyResPayDetail(trPaymentRecord, resPayDetail);
        if (userId.equals(trPaymentRecord.getSellerUserId())) {
            resPayDetail.setSellOrBuy(2);
            resPayDetail.setOppositCompanyName(trPaymentRecord.getBuyerCompanyName());
            int userPayChannelId = trPaymentRecord.getBuyerPayChannelId();
            UserPayChannel userPayChannel = trService.getUserPayChannelDetail(userPayChannelId);
            if (userPayChannel != null) {
                resPayDetail.setOppositPayChannelAccount(userPayChannel.getUserAccountNo());
            }
        } else {
            resPayDetail.setSellOrBuy(1);
            resPayDetail.setOppositCompanyName(trPaymentRecord.getSellerCompanyName());
            int userPayChannelId = trPaymentRecord.getSellerPayChannelId();
            UserPayChannel userPayChannel = trService.getUserPayChannelDetail(userPayChannelId);
            if (userPayChannel != null) {
                resPayDetail.setOppositPayChannelAccount(userPayChannel.getUserAccountNo());
            }
        }
        returnDTO.setData(resPayDetail);
        return returnDTO;
    }

    public ReturnDTO closePayment(Map<String,String> map){
        String userId = map.get("userId");
        String userName = map.get("userName");
        String paymentId = map.get("paymentId");
        /**验证userID userName **/
        ReturnDTO returnDTO = validDTO.validUserIdAndUserName(userId, userName);
        if (!returnDTO.isSuccess()) {
            return returnDTO;
        }
        /**验证参数 **/
        if (paymentId == null || "".equals(paymentId)) {
            returnDTO = validDTO.returnError(ResErrorCode.VALIDPARAM_ERROR_CODE, ResErrorCode.GETPAYDETAIL_ERROR_MSG_1, returnDTO);
            return returnDTO;
        }
        if (!StringUtils.isNumeric(paymentId)) {
            returnDTO = validDTO.returnError(ResErrorCode.VALIDPARAM_ERROR_CODE, ResErrorCode.GETPAYDETAIL_ERROR_MSG_2, returnDTO);
            return returnDTO;
        }
        TrPaymentRecord trPaymentRecord = null;
        /**查询数据 **/
        try {
            trPaymentRecord = trService.getTrPaymentRecordDetail(Integer.valueOf(paymentId));
        } catch (Exception e) {
            log.error(ResErrorCode.GETPAYDETAIL_ERROR_MSG);
            return validDTO.returnError(ResErrorCode.GETPAYDETAIL_ERROR_CODE, ResErrorCode.GETPAYDETAIL_ERROR_MSG, returnDTO);
        }
        if (trPaymentRecord == null) {
            return validDTO.returnError(ResErrorCode.GETPAYDETAIL_ERROR_NO_CODE, ResErrorCode.GETPAYDETAIL_ERROR_NO_MSG, returnDTO);
        }

        /**用户是否属于当前交易 **/
        if (!userId.equals(trPaymentRecord.getSellerUserId()) && !userId.equals(trPaymentRecord.getBuyerUserId())) {
            return validDTO.returnError(ResErrorCode.GETPAYDETAIL_ERROR_NO_AUTH_CODE, ResErrorCode.GETPAYDETAIL_ERROR_NO_AUTH_MSG, returnDTO);
        }

        if(TrPaymentRecord.PAYMENT_STATUS_0 != trPaymentRecord.getPaymentStatus()){
            return validDTO.returnError(ResErrorCode.CLOSEPAYDETAIL_ERROR_CODE, ResErrorCode.CLOSEPAYDETAIL_ERROR_MSG, returnDTO);
        }

        /**关闭交易 **/
        try {
            trPaymentRecord.setPaymentStatus(TrPaymentRecord.PAYMENT_STATUS_3);
            trService.closeTrPaymentRecordDetail(trPaymentRecord);
        }catch (Exception e){
            log.error(ResErrorCode.CLOSEPAYDETAIL_ERROR_CLOSE_MSG, e);
            return validDTO.returnError(ResErrorCode.CLOSEPAYDETAIL_ERROR_CLOSE_CODE,ResErrorCode.CLOSEPAYDETAIL_ERROR_CLOSE_MSG,returnDTO);
        }
        return returnDTO;
    }

    /**
     * 交易记录查询
     *
     * @param map
     * @return
     */
    public ReturnDTO getTradingRecord(Map<String, String> map) {
        String userId = map.get("userId");
        String userName = map.get("userName");
        String startDate = map.get("startDate");
        String endDate = map.get("endDate");
        String page = map.get("page");
        String pageSize = map.get("pageSize");

        /**验证userID userName **/
        ReturnDTO returnDTO = validDTO.validUserIdAndUserName(userId, userName);
        if (!returnDTO.isSuccess()) {
            return returnDTO;
        }

        /**
         * 判断参数
         */
        String msg = validDTO.validPage(startDate,endDate,page,pageSize);
        if (msg != null) {
            //参数错误，直接返回
            validDTO.returnError(ResErrorCode.VALIDPARAM_ERROR_CODE, msg, returnDTO);
            return returnDTO;
        }
        UserPayChannel userPayChannel = new UserPayChannel();
        userPayChannel.setUserId(Integer.valueOf(userId));
        userPayChannel.setPayChannelId(10001);
        //获取用户账户信息
        userPayChannel = uaService.getUserPayChannel(userPayChannel);
        if(userPayChannel == null){
            return validDTO.returnError(ResErrorCode.GETTRADINGRECORD_ERROR_CODE,ResErrorCode.GETTRADINGRECORD_ERROR_MSG,returnDTO);
        }
        //获取交易记录
        Map<String,String> paramMap = new HashMap<String,String>();
        paramMap.put("subAccNo",userPayChannel.getUserAccountNo());
        startDate = startDate.replaceAll("-","");
        paramMap.put("startDate",startDate);
        endDate = endDate.replaceAll("-","");
        paramMap.put("endDate",endDate);
        paramMap.put("tranType","1");
        int pageInt = Integer.valueOf(page);
        int pageSizeInt = Integer.valueOf(pageSize);
        if(pageInt < 1){
            pageInt = 1;
        }
        if(pageSizeInt < 1 || pageSizeInt > 10){
            pageSizeInt = 10;
        }
        int startData = (pageInt - 1) * pageSizeInt + 1;
        paramMap.put("startRecord",startData + "");
        paramMap.put("pageNumber",pageSizeInt + "");
        PagReturnDTO pagReturnDTO = payment.queryTrRecords(paramMap);
        //封装返回数据
        if(pagReturnDTO.isSuccess()){
            Map<String,Object> resMap = new HashMap<String, Object>();
            resMap.put("count", pagReturnDTO.getReturnRecords());
            resMap.put("list", pagReturnDTO.getData());
            returnDTO.setData(resMap);
        }else{
            returnDTO.setSuccess(false);
            returnDTO.setMsg(pagReturnDTO.getMsg());
            returnDTO.setStatus(pagReturnDTO.getStatus());
        }

        return returnDTO;
    }
}
