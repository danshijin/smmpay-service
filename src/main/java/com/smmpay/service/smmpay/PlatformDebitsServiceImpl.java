package com.smmpay.service.smmpay;
import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.smmpay.inter.dto.req.GetCashDTO;
import com.smmpay.internal.service.DebitsService;
import com.smmpay.internal.service.UAService;
import com.smmpay.internal.service.ValidDTO;
import com.smmpay.payment.Payment;
import com.smmpay.respository.dao.TrCashApplyMapper;
import com.smmpay.respository.dao.TrCashApplyRecordMapper;
import com.smmpay.respository.dao.UserPayChannelMapper;
import com.smmpay.respository.model.TrCashApply;
import com.smmpay.respository.model.TrCashApplyRecord;
import com.smmpay.respository.model.UserPayChannel;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.smmpay.common.ResErrorCode;
import com.smmpay.inter.dto.req.PlatformDebitsDTO;
import com.smmpay.inter.dto.res.ReturnDTO;
import com.smmpay.inter.smmpay.PlatformDebitsService;
import com.smmpay.process.enumDef.ReqResult;
import com.smmpay.respository.dao.TrRecordMapper;
import com.smmpay.respository.model.ReqDlfcsout;
import com.smmpay.respository.model.base.ResCommon;
import com.smmpay.service.base.BaseService;
import com.smmpay.util.CPayUtils;
import com.smmpay.util.ClientIdUtils;
import com.smmpay.util.DateUtil;
import com.smmpay.util.XMLUtils;

/**
 * 平台出金
 * @author zengshihua
 *
 */
@Service("platformDebitsService")
public class PlatformDebitsServiceImpl extends BaseService implements PlatformDebitsService {
	
	@Autowired
	private TrRecordMapper recordMapper;
	@Autowired
	private TrCashApplyRecordMapper trCashApplyRecordMapper;
	@Autowired
	private TrCashApplyMapper trCashApplyMapper;
	@Autowired
	private UAService uaService;
	@Autowired
	private ValidDTO validDTO;
	@Autowired
	private Payment payment;
	@Autowired
	private DebitsService debitsService;
	@Autowired
	private UserPayChannelMapper userPayChannelMapper;

	private Logger logger = Logger.getLogger(PlatformDebitsServiceImpl.class);

	public ReturnDTO platDebits(Map<String,String> map){
		ReturnDTO returnDTO = new ReturnDTO();
		GetCashDTO getCashDTO = new GetCashDTO();
		try {
			BeanUtils.populate(getCashDTO, map);
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}

		/**
		 * 判断参数
		 */
		String msg = validDTO.validGetCash(getCashDTO);
		if(msg != null){
			//参数错误，直接返回
			returnDTO = validDTO.returnError(ResErrorCode.VALIDPARAM_ERROR_CODE, msg, returnDTO);
			return returnDTO;
		}
		/**验证userID userName **/
		returnDTO = validDTO.validUserIdAndUserName(getCashDTO.getUserId(), getCashDTO.getUserName());
		if(!returnDTO.isSuccess()){
			return returnDTO;
		}

		int count = trCashApplyRecordMapper.countRecordByUserIdAndStatus(Integer.valueOf(getCashDTO.getUserId()));
		if(count > 0){
			return validDTO.returnError(ResErrorCode.GETCASH_ERROR_RECORD_CODE, ResErrorCode.GETCASH_ERROR_RECORD_MSG, returnDTO);
		}

		Map<String,Object> payMap = new HashMap<String, Object>();
		Map<String,Object> resultMap = new HashMap<String, Object>();
		payMap.put("userId", getCashDTO.getUserId());
		payMap.put("operateType", 4);
		try {
			resultMap = payment.queryPayment(payMap);
		} catch (Exception e) {
			e.printStackTrace();
			validDTO.returnError(ResErrorCode.GETUSERACCOUNT_ERROR_CODE, ResErrorCode.GETUSERACCOUNT_ERROR_MSG, returnDTO);
			return returnDTO;
		}

		if(resultMap == null  || !"success".equals(resultMap.get("resultCode"))){
			String resMsg = ResErrorCode.GETUSERACCOUNT_ERROR_MSG;
			if(resultMap != null && resultMap.get("resultMesg") != null
					&& resultMap.get("resultMesg").toString() != null
					&& !"".equals(resultMap.get("resultMesg").toString())){
				resMsg = resultMap.get("resultMesg").toString();
			}
			validDTO.returnError(ResErrorCode.GETUSERACCOUNT_ERROR_CODE, resMsg, returnDTO);
			return returnDTO;
		}

		try {
			returnDTO = uaService.getCashForPlat(getCashDTO, returnDTO);
		}catch (Exception e){
			logger.error(ResErrorCode.GETCASH_ERROR_MSG, e);
			return validDTO.returnError(ResErrorCode.GETCASH_ERROR_CODE, ResErrorCode.GETCASH_ERROR_MSG, returnDTO);
		}

		return returnDTO;
	}

	public ReturnDTO platformDebits(Map<String, String> map)  {
		
		ReturnDTO returnDTO = new ReturnDTO();
		PlatformDebitsDTO platformDebitsDTO=new PlatformDebitsDTO();
		try {
			
			BeanUtils.populate(platformDebitsDTO, map);
			/**
			 * 判断参数
			 */
			returnDTO.setMsg(ResErrorCode.VALIDPARAM_ERROR_MSG);
			returnDTO.setStatus(ResErrorCode.VALIDPARAM_ERROR_CODE);
			if(StringUtils.isBlank(platformDebitsDTO.getAccountNo())
					|| StringUtils.isBlank(platformDebitsDTO.getRecvAccNo())
					|| StringUtils.isBlank(platformDebitsDTO.getRecvAccNm())
					|| platformDebitsDTO.getTranAmt()==null 
					|| StringUtils.isBlank(platformDebitsDTO.getSameBank())
					|| platformDebitsDTO.getCashApplyId() == null
					|| platformDebitsDTO.getCashBankId() == null
					|| platformDebitsDTO.getUserPayChannelId() == null
					|| platformDebitsDTO.getUserId() == null
					){
				// 参数错误，直接返回
				returnDTO.setSuccess(false);
				return returnDTO;
			}
			//SameBank=1 收款账户为他行，则收款账户开户行支付联行号与收款账户开户行名至少一项不为空
			if(platformDebitsDTO.getSameBank().equals("1")){
				if(StringUtils.isBlank(platformDebitsDTO.getRecvTgfi())){
					if(StringUtils.isBlank(platformDebitsDTO.getRecvBankNm())){
					// 参数不符合，直接返回
					returnDTO.setSuccess(false);
					return returnDTO;
					}
				}
			}

			returnDTO.setMsg(ResErrorCode.RETURN_EXCE_MSG);
			returnDTO.setStatus(ResErrorCode.RETURN_EXCE_CODE);

//			int i = trCashApplyRecordMapper.selectByCashApplyId(platformDebitsDTO.getCashApplyId());
//			if(i >= 1){
//				returnDTO.setSuccess(false);
//				return returnDTO;
//			}

			returnDTO = debitsService.debits(platformDebitsDTO, returnDTO);

//			/**
//			 * 生成交易记录
//			 */
//			String applyTime=DateUtil.doFormatDate(new Date(), "yyyy-MM-dd HH:mm:ss");
//			String clientID=ClientIdUtils.CreateClientId();
////			TrRecord cj = new TrRecord();
//			TrCashApplyRecord cj = new TrCashApplyRecord();
//			cj.setApplyTime(DateUtil.doFormatDate(new Date(), "yyyy-MM-dd HH:mm:ss"));
//
//			ReqDlfcsout req=new ReqDlfcsout();
//			BeanUtils.copyProperties(req, platformDebitsDTO);
//			req.setPreFlg("0");//预约标志char(1) 0：非预约1：预约
//			req.setClientID(clientID);
//			req.setTranAmt(req.getTranAmt().setScale(2, RoundingMode.HALF_UP));
//
//			String sendXML = XMLUtils.beanToXML(req);
//			/**
//			 * 请求中信接口
//			 */
//			logger.info("请求中信接口，平台出金.入参:" + sendXML);
//			String resultXML = CPayUtils.postRequest(sendXML);
//			/**
//			 * 将响应结果转换成实体类
//			 */
//			logger.info("将响应结果转换成实体类.入参:" + resultXML);
//			ResCommon resCommon=(ResCommon) XMLUtils.xmlToBean(resultXML,ResCommon.class,null);
//			logger.info("将响应结果转换成实体类.出参:"+resCommon);
//
//
//			if(resCommon.getStatus().equals(ReqResult.AAAAAAE.getCode())
//					||resCommon.getStatus().equals(ReqResult.AAAAAAA.getCode())){
//
//				returnDTO.setMsg(ResErrorCode.RETURN_SUCCESS_CODE);
//				returnDTO.setStatus(ResErrorCode.RETURN_SUCCESS_MSG);
////				cj.setTrApplyStatus(0);
//				cj.setApplyStatus(1);
//			}else{
//				cj.setApplyStatus(3);
//				TrCashApply trCashApply = new TrCashApply();
////						trCashApplyMapper.selectByPrimaryKey(platformDebitsDTO.getCashApplyId());
//				trCashApply.setApplyStatus(2);
//				trCashApply.setId(platformDebitsDTO.getCashApplyId());
//				trCashApplyMapper.updateByPrimaryKeySelective(trCashApply);
////				cj.setTrApplyStatus(2);
////				cj.setPayChannelTrTime(DateUtil.doFormatDate(new Date(), "yyyy-MM-dd HH:mm:ss"));
//				cj.setReplayMsg(resCommon.getStatusText());
//				cj.setReplayTime(DateUtil.doFormatDate(new Date(), "yyyy-MM-dd HH:mm:ss"));
//				returnDTO.setMsg(resCommon.getStatusText());
//				returnDTO.setStatus(ResErrorCode.RETURN_EXCE_CODE);
//
//				//throw new Exception();
//			}
//			cj.setCashApplyId(platformDebitsDTO.getCashApplyId());
//			cj.setCashBankId(platformDebitsDTO.getCashBankId());
//			cj.setCashMoney(platformDebitsDTO.getTranAmt());
////			cj.setTrMoney(platformDebitsDTO.getTranAmt());
//
////			cj.setTrApplyTime(applyTime);
////			cj.setTrType(2);
////			cj.setTrCharge(new BigDecimal(0.00));//手续费
////			cj.setUserMoney(new BigDecimal(0.00));//本次执行后账户余额
//			cj.setUserId(platformDebitsDTO.getUserId());
////			cj.setUserPayChannelId(platformDebitsDTO.getUserPayChannelId());
//			cj.setUserCompanyName(platformDebitsDTO.getUserCompanyName());
//			cj.setPayChannelId(platformDebitsDTO.getUserPayChannelId());
//			cj.setClientId(clientID);
//			cj.setCashBankName(platformDebitsDTO.getRecvBankNm() != null ? platformDebitsDTO.getRecvBankNm() : "");
//
//			cj.setCashBankNo(platformDebitsDTO.getRecvAccNo() != null?platformDebitsDTO.getRecvAccNo() : "");
//			cj.setCashBankCnaps(platformDebitsDTO.getRecvTgfi());
//			cj.setUserUseMoney(platformDebitsDTO.getUserUseMoney());
//			cj.setUserFreezeMoney(platformDebitsDTO.getUserFreezeMoney());
//			cj.setPayChannelUserAccount(platformDebitsDTO.getPayChannelUserAccount());
//			cj.setUserEmail(platformDebitsDTO.getUserEmail());
//			cj.setCounterFee(platformDebitsDTO.getCounterFee());
//			cj.setCashType(platformDebitsDTO.getCashType());
//
//			Integer buyCount = trCashApplyRecordMapper.insertSelective(cj);
			
			
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			logger.error("出金接口：",e);
			return validDTO.returnError(ResErrorCode.GETCASH_ERROR_CODE, ResErrorCode.GETCASH_ERROR_MSG, returnDTO);
		} catch (InvocationTargetException e) {
			// TODO Auto-generated catch block
			logger.error("出金接口：",e);
			e.printStackTrace();
			return validDTO.returnError(ResErrorCode.GETCASH_ERROR_CODE, ResErrorCode.GETCASH_ERROR_MSG, returnDTO);
		}
		return returnDTO;
	}
}
