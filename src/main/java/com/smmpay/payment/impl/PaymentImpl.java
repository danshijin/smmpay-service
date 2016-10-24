package com.smmpay.payment.impl;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import com.smmpay.inter.dto.res.ResQueryTradingRecordDTO;
import com.smmpay.internal.service.IDatabaseEncrypt;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.smmpay.common.ResErrorCode;
import com.smmpay.inter.dto.req.NoLoginQueryTrRecordsDTO;
import com.smmpay.inter.dto.res.PagReturnDTO;
import com.smmpay.inter.dto.res.ResQueryTrRecordsDTO;
import com.smmpay.payment.Payment;
import com.smmpay.process.CheckPayMoneyProcess;
import com.smmpay.process.QueryBalanceProcess;
import com.smmpay.process.QueryBuyerAccountProcess;
import com.smmpay.process.QueryFreezeRecordProcess;
import com.smmpay.process.QueryTrRecordProcess;
import com.smmpay.process.SaveFreezeRecordProcess;
import com.smmpay.process.StepProcessor;
import com.smmpay.process.SynCreditRecordProcess;
import com.smmpay.process.enumDef.OperateType;
import com.smmpay.process.enumDef.ReqResult;
import com.smmpay.respository.dao.SBankLogMapper;
import com.smmpay.respository.dao.SPaymentLogMapper;
import com.smmpay.respository.dao.TrCashApplyRecordMapper;
import com.smmpay.respository.dao.TrFreezeRecordMapper;
import com.smmpay.respository.dao.TrPaymentRecordMapper;
import com.smmpay.respository.dao.TrRecordMapper;
import com.smmpay.respository.dao.TrTransferRecordMapper;
import com.smmpay.respository.dao.TrUnfreezeRecordMapper;
import com.smmpay.respository.dao.UserAccountMapper;
import com.smmpay.respository.dao.UserMoneyExceptionMapper;
import com.smmpay.respository.dao.UserPayChannelMapper;
import com.smmpay.respository.model.ReqDlptdtqy;
import com.smmpay.respository.model.ReqDlstrndt;
import com.smmpay.respository.model.ResDlptdtqy;
import com.smmpay.respository.model.ResDlptdtqyRow;
import com.smmpay.respository.model.ResDlstrndt;
import com.smmpay.respository.model.ResDlstrndtRow;
import com.smmpay.respository.model.TrPaymentRecord;
import com.smmpay.respository.model.UserPayChannel;
import com.smmpay.service.smmpay.QueryTrRecordsServiceImpl;
import com.smmpay.util.CPayUtils;
import com.smmpay.util.XMLUtils;

/**
 * 
 * @author zengshihua
 *
 */
@Service
public class PaymentImpl implements Payment{
	
	private Logger logger = Logger.getLogger(QueryTrRecordsServiceImpl.class);
	
	@Resource
	private TrRecordMapper trRecordMapper;
	@Resource
	private UserAccountMapper userAccountMapper;
	@Resource
	private UserPayChannelMapper userPayChannelMapper;
	@Resource
	private UserMoneyExceptionMapper userMoneyExceptionMapper;
	@Autowired
	private TrFreezeRecordMapper freezeRecordMapper;
	@Autowired
	private TrPaymentRecordMapper trPaymentRecordMapper;
	@Autowired
	private SBankLogMapper bankLogMapper;
	@Autowired
	private TrUnfreezeRecordMapper trUnfreezeRecordMapper;
	@Autowired
	private TrTransferRecordMapper trTransferRecordMapper;
	@Autowired
	private TrCashApplyRecordMapper trCashApplyRecordMapper;
	@Autowired
	private SPaymentLogMapper pLogMapper;
	@Autowired
	private IDatabaseEncrypt iDatabaseEncrypt;
	
	
	/**
	 * 查询余额并同步入金记录以及比对余额
	 * 
	 * 入参：用户支付ID(userId)
	 * 
	 * 出参：返回代码(resultCode)、返回信息(resultMesg)、
	 * 比对处理代码(handleCode)、比对处理信息(handleMesg)、
	 * 可用余额(userMoney)、冻结余额(freezeMoney)
	 * 账户余额(totalMoney)
	 */
	@Transactional(rollbackFor = Exception.class)
	public synchronized Map<String, Object> queryPayment(Map<String, Object> paramMap) throws Exception {
		
		Map<String, Object> result=null;
		if(qpCheckParam(paramMap)){
			try {
				List<UserPayChannel> userPayChannelList=userPayChannelMapper.queryPayChannelByUserId(paramMap);
				if(userPayChannelList!=null&&userPayChannelList.size()>0){
					/**
					 * 现在只针对中信支付通道做处理
					 */
					paramMap.put("payChannelId", userPayChannelList.get(0).getPayChannelId());
					paramMap.put("subAccNo", userPayChannelList.get(0).getUserAccountNo());
					paramMap.put("subAccNm", userPayChannelList.get(0).getUserAccountName());
					paramMap.put("userPayChannelId", userPayChannelList.get(0).getUserPayChannelId());
					QueryTrRecordProcess first=QueryTrRecordProcess.getInstanceWithParam(paramMap,trRecordMapper,userAccountMapper);//获取最后入金记录时间
					result=query(first,first);
				}
				
			} catch (Exception e) {
				e.printStackTrace();
				throw e;
			}
		}else{
			//请传入参数
		}
		
		return result;
	}
	
	public Map<String, Object> query(StepProcessor frstProcessor,
			StepProcessor nextProcessor) throws Exception {
		
		nextProcessor.next(SynCreditRecordProcess.getInstanceWithParam(trRecordMapper,userAccountMapper,userPayChannelMapper))//同步入金记录
		 			 .next(QueryBalanceProcess.getInstanceWithParam(userPayChannelMapper,userMoneyExceptionMapper,trPaymentRecordMapper,pLogMapper));//查询余额
		
		return frstProcessor.process(null);
	}
	
	/**
	 * 支付
	 * 
	 * 入参：用户支付ID(userId)/支付记录ID(paymentId)/交易金额(dealMoney)/余额比对异常后是否设置手动处理标识(handleFlag Y:需要,N:不需要)
	 * 
	 * 出参：返回代码(resultCode)、返回信息(resultMesg)、
	 * 比对处理代码(handleCode)、比对处理信息(handleMesg)、
	 * 可用余额(userMoney)、冻结余额(freezeMoney)
	 * 账户余额(totalMoney)
	 */
	@Transactional(rollbackFor = Exception.class)
	public synchronized Map<String, Object>  payment(Map<String, Object> paramMap) throws Exception {
		
		Map<String, Object> result=new HashMap<String, Object>();
		
		if(payParam(paramMap)){
			try {
				
				QueryBuyerAccountProcess fristProcess=QueryBuyerAccountProcess.getInstanceWithParam(userMoneyExceptionMapper, paramMap,pLogMapper);//判断账户是否有异常
					result=startProcess(fristProcess,fristProcess,paramMap);
					
			} catch (Exception e) {
				result.put("resultCode", "exce");
				result.put("resultMesg", "支付流程发生异常.");
				e.printStackTrace();
				throw e;
			}
		}else{
			result.put("resultCode", "fail");
			result.put("resultMesg", "请检查参数.");
		}
		
		return result;
	}	
	protected Map<String, Object> startProcess(StepProcessor frstProcessor,
			StepProcessor nextProcessor,Map<String, Object> paramMap) throws Exception {
		
		nextProcessor.next(QueryTrRecordProcess.getInstanceWithParam(bulidQueyParam(paramMap),
							trRecordMapper,
							userAccountMapper))//获取最最后同步记录
					.next(SynCreditRecordProcess.getInstanceWithParam(trRecordMapper,
							userAccountMapper,
							userPayChannelMapper))//同步入金记录
					.next(QueryFreezeRecordProcess.getInstanceWithParam(freezeRecordMapper,
							trUnfreezeRecordMapper,
							trRecordMapper,trTransferRecordMapper,
							trCashApplyRecordMapper,pLogMapper))//检查账户是否存在请求中的记录
					.next(QueryBalanceProcess.getInstanceWithParam(userPayChannelMapper,
							userMoneyExceptionMapper,
							trPaymentRecordMapper,
							pLogMapper))//查询以及比对余额
					.next(CheckPayMoneyProcess.getInstanceWithParam(pLogMapper))//检查余额是否足够
					.next(SaveFreezeRecordProcess.getInstanceWithParam(freezeRecordMapper,
							trPaymentRecordMapper,
							bankLogMapper,
							pLogMapper,
							iDatabaseEncrypt));//生成资金冻结记录，状态请求中(包含发送冻结指令至中信)
		
		return frstProcessor.process(null);
	}
	/**
	 * 根据userId取得付款方账号
	 * 作为第二步操作的入参
	 * @param paramMap
	 * @return
	 */
	protected Map<String, Object> bulidQueyParam(Map<String, Object> paramMap) {
		
		List<UserPayChannel> userPayChannelList=userPayChannelMapper.queryPayChannelByUserId(paramMap);
		if(userPayChannelList!=null&&userPayChannelList.size()>0){
			/**
			 * 现在只针对中信支付通道做处理
			 */
			paramMap.put("payChannelId", userPayChannelList.get(0).getPayChannelId());
			paramMap.put("subAccNo", userPayChannelList.get(0).getUserAccountNo());
			paramMap.put("subAccNm", userPayChannelList.get(0).getUserAccountName());
			paramMap.put("userPayChannelId", userPayChannelList.get(0).getUserPayChannelId());
		}
		TrPaymentRecord paymentRecord = trPaymentRecordMapper.selectByPrimaryKey(Integer.valueOf(String.valueOf(paramMap.get("paymentId"))));
		paramMap.put("sellerUserId", paymentRecord.getSellerUserId());
		paramMap.put("operateType", OperateType.O1.getCode());
		return paramMap;
	}

	/**
	 * 
	 * 校验入参
	 * 
	 */
	public boolean qpCheckParam(Map<String, Object> paramMap){
		if(paramMap!=null){
			/*if(!paramMap.containsKey("subAccNo")||
					!StringUtils.isNotBlank((String)paramMap.get("subAccNo"))){
				return false;
			}
			if(!paramMap.containsKey("subAccNm")||
					!StringUtils.isNotBlank((String)paramMap.get("subAccNm"))){
				return false;
			}*/
			if(!paramMap.containsKey("userId")||
					!StringUtils.isNotBlank((String)paramMap.get("userId"))){
				return false;
			}
			/*if(!paramMap.containsKey("payChannelId")||
					!StringUtils.isNotBlank((String)paramMap.get("payChannelId"))){
				return false;
			}*/
			
			return true;
		}else{
			
			return false;
		}
		
	}
	/**
	 * 
	 * 校验入参
	 * 
	 */
	public boolean payParam(Map<String, Object> paramMap){
		if(paramMap!=null){
			if(!paramMap.containsKey("userId")||
					!StringUtils.isNotBlank(String.valueOf(paramMap.get("userId")))){
				return false;
			}
			if(!paramMap.containsKey("paymentId")||
					!StringUtils.isNotBlank(String.valueOf(paramMap.get("paymentId")))){
				return false;
			}
			if(!paramMap.containsKey("dealMoney")||
					!StringUtils.isNotBlank(String.valueOf(paramMap.get("dealMoney")))){
				return false;
			}
			
			return true;
		}else{
			
			return false;
		}
		
	}
	/**
	 * 非登录打印明细
	 */
	public  PagReturnDTO queryTrRecords(Map<String, String> map) {
		PagReturnDTO returnDTO = new PagReturnDTO();
		NoLoginQueryTrRecordsDTO queryTrRecordsDTO = new NoLoginQueryTrRecordsDTO();
		try {

			BeanUtils.populate(queryTrRecordsDTO, map);
			/**
			 * 判断参数
			 */
			if (StringUtils.isBlank(queryTrRecordsDTO.getSubAccNo())
					||StringUtils.isBlank(queryTrRecordsDTO.getStartDate())
					||StringUtils.isBlank(queryTrRecordsDTO.getEndDate())) {
				// 参数错误，直接返回
				returnDTO.setMsg(ResErrorCode.VALIDPARAM_ERROR_MSG);
				returnDTO.setStatus(ResErrorCode.VALIDPARAM_ERROR_CODE);
				returnDTO.setSuccess(false);
				return returnDTO;
			}

			returnDTO.setMsg(ResErrorCode.RETURN_EXCE_MSG);
			returnDTO.setStatus(ResErrorCode.RETURN_EXCE_CODE);

			ReqDlptdtqy req = new ReqDlptdtqy();
			
			BeanUtils.copyProperties(req, queryTrRecordsDTO);
			//req.setStartRecord("1");
			//req.setPageNumber("10");
			
			String sendXML = XMLUtils.beanToXML(req);
			/**
			 * 请求中信接口
			 */
			logger.info("请求中信接口，指定附属账户的交易记录.入参:" + sendXML);
			String resultXML = CPayUtils.postRequest(sendXML);
			/**
			 * 将响应结果转换成实体类
			 */
			logger.info("将响应结果转换成实体类.入参:" + resultXML);
			ResDlptdtqy resDl = (ResDlptdtqy) XMLUtils.xmlToBean(resultXML, ResDlptdtqy.class, ResDlptdtqyRow.class);
			logger.info("将响应结果转换成实体类.出参:" + resDl);

			if (resDl != null&&resDl.getStatus().equals(ReqResult.AAAAAAA.getCode())) {
				
				List<ResDlptdtqyRow> resDRow=resDl.getList();
				List<ResQueryTradingRecordDTO> resqtRecordsDTOList=new ArrayList<ResQueryTradingRecordDTO>();

				for (ResDlptdtqyRow resDlptdtqyRow : resDRow) {
					ResQueryTradingRecordDTO resTrRecDTO = new ResQueryTradingRecordDTO();
					BeanUtils.copyProperties(resTrRecDTO, resDlptdtqyRow);
					resqtRecordsDTOList.add(resTrRecDTO);
				}
				returnDTO.setReturnRecords(resDl.getReturnRecords());
				returnDTO.setData(resqtRecordsDTOList);
				returnDTO.setMsg(ResErrorCode.RETURN_SUCCESS_MSG);
				returnDTO.setStatus(ResErrorCode.RETURN_SUCCESS_CODE);
			}else if(resDl != null&&resDl.getStatus().equals(ReqResult.ES88363.getCode())){
				/**
				 * 主机返回账户明细信息列表为空
				 */
				returnDTO.setMsg(ReqResult.ES88363.getMessage());
				returnDTO.setStatus(ReqResult.ES88363.getCode());
			}
			else if(resDl!=null&& resDl.getStatus().equals(ReqResult.ED11308.getCode())){
				
				/**
				 * ED11308错误
				 */
				logger.info(ReqResult.ED11308.getMessage());
				returnDTO.setMsg(ReqResult.ED11308.getMessage());
				returnDTO.setStatus(ReqResult.ED11308.getCode());
			}
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}
		return returnDTO;
	}
	
}
