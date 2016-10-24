package com.smmpay.internal.service.impl;

import com.smmpay.common.ResErrorCode;
import com.smmpay.inter.dto.req.PlatformDebitsDTO;
import com.smmpay.inter.dto.res.ReturnDTO;
import com.smmpay.internal.service.DebitsService;
import com.smmpay.process.enumDef.ReqResult;
import com.smmpay.respository.dao.TrCashApplyMapper;
import com.smmpay.respository.dao.TrCashApplyRecordMapper;
import com.smmpay.respository.model.ReqDlfcsout;
import com.smmpay.respository.model.TrCashApply;
import com.smmpay.respository.model.TrCashApplyRecord;
import com.smmpay.respository.model.base.ResCommon;
import com.smmpay.util.CPayUtils;
import com.smmpay.util.ClientIdUtils;
import com.smmpay.util.DateUtil;
import com.smmpay.util.XMLUtils;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.InvocationTargetException;
import java.math.RoundingMode;
import java.util.Date;

/**
 * Created by tangshulei on 2015/12/4.
 */
@Service("debitsService")
@Transactional
public class DebitsServiceImpl implements DebitsService{

    private Logger logger = Logger.getLogger(DebitsServiceImpl.class);

    @Autowired
    private TrCashApplyRecordMapper trCashApplyRecordMapper;
    @Autowired
    private TrCashApplyMapper trCashApplyMapper;

    /**
     *
     * 调用中信出金接口
     * @param platformDebitsDTO
     * @param returnDTO
     * @return
     */
    @Transactional(propagation = Propagation.REQUIRED,rollbackFor = Exception.class)
    public ReturnDTO debits(PlatformDebitsDTO platformDebitsDTO, ReturnDTO returnDTO){
        /**
         * 生成交易记录
         */
        String applyTime= DateUtil.doFormatDate(new Date(), "yyyy-MM-dd HH:mm:ss");
        String clientID= ClientIdUtils.CreateClientId();
//			TrRecord cj = new TrRecord();
        TrCashApplyRecord cj = new TrCashApplyRecord();
        cj.setApplyTime(DateUtil.doFormatDate(new Date(), "yyyy-MM-dd HH:mm:ss"));
        TrCashApply trCashApply = new TrCashApply();

        /**
         * 如改出金已有流水记录 则返回失败
         */
        int i = trCashApplyRecordMapper.selectByCashApplyId(platformDebitsDTO.getCashApplyId());
        if(i >= 1){
            trCashApply.setApplyStatus(2);
            trCashApply.setId(platformDebitsDTO.getCashApplyId());
            trCashApplyMapper.updateByPrimaryKeySelective(trCashApply);
            returnDTO.setSuccess(false);
            returnDTO.setStatus(ResErrorCode.RETURN_EXCE_CODE);
            returnDTO.setStatus(ResErrorCode.RETURN_EXCE_MSG_1);
            return returnDTO;
        }

        ReqDlfcsout req=new ReqDlfcsout();
        try {
            BeanUtils.copyProperties(req, platformDebitsDTO);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        req.setPreFlg("0");//预约标志char(1) 0：非预约1：预约
        req.setClientID(clientID);
        req.setTranAmt(req.getTranAmt().setScale(2, RoundingMode.HALF_UP));

        String sendXML = XMLUtils.beanToXML(req);
        /**
         * 请求中信接口
         */
        logger.info("请求中信接口，平台出金.入参:" + sendXML);
        String resultXML = CPayUtils.postRequest(sendXML);
        /**
         * 将响应结果转换成实体类
         */
        logger.info("将响应结果转换成实体类.入参:" + resultXML);
        ResCommon resCommon=(ResCommon) XMLUtils.xmlToBean(resultXML,ResCommon.class,null);
        logger.info("将响应结果转换成实体类.出参:"+resCommon);


        if(resCommon.getStatus().equals(ReqResult.AAAAAAE.getCode())
                ||resCommon.getStatus().equals(ReqResult.AAAAAAA.getCode())){

            returnDTO.setMsg(ResErrorCode.RETURN_SUCCESS_CODE);
            returnDTO.setStatus(ResErrorCode.RETURN_SUCCESS_MSG);
//				cj.setTrApplyStatus(0);
            cj.setApplyStatus(1);
        }else{
            cj.setApplyStatus(3);
//            TrCashApply trCashApply = new TrCashApply();
//						trCashApplyMapper.selectByPrimaryKey(platformDebitsDTO.getCashApplyId());
            trCashApply.setApplyStatus(2);
            trCashApply.setId(platformDebitsDTO.getCashApplyId());
            trCashApplyMapper.updateByPrimaryKeySelective(trCashApply);
//				cj.setTrApplyStatus(2);
//				cj.setPayChannelTrTime(DateUtil.doFormatDate(new Date(), "yyyy-MM-dd HH:mm:ss"));
            cj.setReplayMsg(resCommon.getStatusText());
            cj.setReplayTime(DateUtil.doFormatDate(new Date(), "yyyy-MM-dd HH:mm:ss"));
            returnDTO.setMsg(resCommon.getStatusText());
            returnDTO.setStatus(ResErrorCode.RETURN_EXCE_CODE);

            //throw new Exception();
        }
        cj.setCashApplyId(platformDebitsDTO.getCashApplyId());
        cj.setCashBankId(platformDebitsDTO.getCashBankId());
        cj.setCashMoney(platformDebitsDTO.getTranAmt());
//			cj.setTrMoney(platformDebitsDTO.getTranAmt());

//			cj.setTrApplyTime(applyTime);
//			cj.setTrType(2);
//			cj.setTrCharge(new BigDecimal(0.00));//手续费
//			cj.setUserMoney(new BigDecimal(0.00));//本次执行后账户余额
        cj.setUserId(platformDebitsDTO.getUserId());
//			cj.setUserPayChannelId(platformDebitsDTO.getUserPayChannelId());
        cj.setUserCompanyName(platformDebitsDTO.getUserCompanyName());
        cj.setPayChannelId(platformDebitsDTO.getUserPayChannelId());
        cj.setClientId(clientID);
        cj.setCashBankName(platformDebitsDTO.getRecvBankNm() != null ? platformDebitsDTO.getRecvBankNm() : "");

        cj.setCashBankNo(platformDebitsDTO.getRecvAccNo() != null?platformDebitsDTO.getRecvAccNo() : "");
        cj.setCashBankCnaps(platformDebitsDTO.getRecvTgfi());
        cj.setUserUseMoney(platformDebitsDTO.getUserUseMoney());
        cj.setUserFreezeMoney(platformDebitsDTO.getUserFreezeMoney());
        cj.setPayChannelUserAccount(platformDebitsDTO.getPayChannelUserAccount());
        cj.setUserEmail(platformDebitsDTO.getUserEmail());
        cj.setCounterFee(platformDebitsDTO.getCounterFee());
        cj.setCashType(platformDebitsDTO.getCashType());
        Integer buyCount = trCashApplyRecordMapper.insertSelective(cj);
        return returnDTO;
    }
}
