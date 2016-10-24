package com.smmpay.payment.impl;

import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.smmpay.common.ResErrorCode;
import com.smmpay.internal.service.IDatabaseEncrypt;
import com.smmpay.payment.ThawPayService;
import com.smmpay.process.HandleTransferInstProcess;
import com.smmpay.process.ManualThawPayProcess;
import com.smmpay.process.StepProcessor;
import com.smmpay.process.ThawPayProcess;
import com.smmpay.respository.dao.DaPayChannelMapper;
import com.smmpay.respository.dao.SBankLogMapper;
import com.smmpay.respository.dao.SPaymentLogMapper;
import com.smmpay.respository.dao.TrConfirmAuditMapper;
import com.smmpay.respository.dao.TrFreezeRecordMapper;
import com.smmpay.respository.dao.TrPaymentRecordMapper;
import com.smmpay.respository.dao.TrRecordMapper;
import com.smmpay.respository.dao.TrTransferRecordMapper;
import com.smmpay.respository.dao.TrUnfreezeRecordMapper;
import com.smmpay.respository.dao.UserAccountMapper;
import com.smmpay.respository.dao.UserMoneyExceptionMapper;
import com.smmpay.respository.dao.UserPayChannelMapper;
import com.smmpay.respository.model.DaPayChannel;
import com.smmpay.respository.model.TrConfirmAudit;
import com.smmpay.respository.model.TrPaymentRecord;
import com.smmpay.respository.model.TrTransferRecord;

/**
 * 后台审核付款流程(后台交易审核完成，点击解冻支付)
 * 
 */
@Service("thawPayService")
public class ThawPayServiceImpl implements ThawPayService {

	private Logger logger=Logger.getLogger(this.getClass());
	
	@Autowired
	private TrPaymentRecordMapper trPaymentRecordMapper;
	
	@Autowired
	private UserMoneyExceptionMapper userMoneyExceptionMapper;
	
	@Autowired
	private TrRecordMapper trRecordMapper;
	
	@Autowired
	private UserAccountMapper userAccountMapper;
	
	@Autowired
	private UserPayChannelMapper userPayChannelMapper;
	
	@Autowired
	private TrFreezeRecordMapper freezeRecordMapper;
	
	@Autowired
	private TrConfirmAuditMapper confirmAuditMapper;
	
	@Autowired
	private TrUnfreezeRecordMapper trUnfreezeRecordMapper;
	
	@Autowired
	private SBankLogMapper bankLogMapper;
	
	@Autowired
	private SPaymentLogMapper pLogMapper;
	
	@Autowired
	private DaPayChannelMapper channelDao;
	
	@Autowired
	private TrTransferRecordMapper transferMapper;
	
	@Autowired
	private IDatabaseEncrypt iDatabaseEncrypt;
	
	private StepProcessor fristProcessor;
	private StepProcessor nextProcessor;
	
	
	

	/**
	 * 入参：支付记录(paymentId)
	 *     
	 */
	@Transactional(rollbackFor = Exception.class)
	public Map<String, Object> autoTthawPayHandle(Map<String, Object> paramMap) throws Exception {
		logger.info("执行解冻支付流程>>>>>>>>,入参:"+paramMap);
		Map<String, Object> result=null;
		try {
			
			//获取支付记录
			TrPaymentRecord paymentRecord = trPaymentRecordMapper.selectByPrimaryKey(Integer.valueOf(String.valueOf(paramMap.get("paymentId"))));
			ThawPayProcess fristProcess=ThawPayProcess.getInstanceWithParam(paramMap, freezeRecordMapper, 
					paymentRecord, userPayChannelMapper, trUnfreezeRecordMapper,trPaymentRecordMapper,bankLogMapper,pLogMapper, iDatabaseEncrypt);
			result=fristProcess.process(null);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		logger.info("执行解冻支付流程>>>>>>>>,出参:"+result);
		return result;
	}
	/**
	 * 支付记录(paymentId)
	 *     
	 */
	protected TrConfirmAudit buildConfirmAudit(Map<String, Object> paramMap) {
		
		TrConfirmAudit conAudit = new TrConfirmAudit();
		conAudit.setAuditUserId(Integer.valueOf(String.valueOf(paramMap.get("auditUserId"))));
		conAudit.setAuditStatus(1);//通过
		conAudit.setAuditTime(String.valueOf(paramMap.get("auditTime")));
		conAudit.setConfirmId(Integer.valueOf(String.valueOf(paramMap.get("confirmId"))));
		return conAudit;
		
	}
	
	/**
	 * 手动解冻支付
	 * 
	 * 支付记录(paymentId)
	 *     
	 */
	@Transactional(rollbackFor = Exception.class)
	public Map<String, Object> manualThawPayHandle(Map<String, Object> paramMap) throws Exception {
		logger.info("执行手动解冻支付流程>>>>>>>>,入参:"+paramMap);
		Map<String, Object> result=null;
		try {
			/**
			 * 获取支付记录
			 */
			TrPaymentRecord paymentRecord = trPaymentRecordMapper.selectByPrimaryKey(Integer.valueOf(String.valueOf(paramMap.get("paymentId"))));
			/**
			 * 执行手动解冻支付流程
			 */
			ManualThawPayProcess first=ManualThawPayProcess.getInstanceWithParam(paramMap, freezeRecordMapper, paymentRecord, 
					userPayChannelMapper, trUnfreezeRecordMapper, trPaymentRecordMapper,bankLogMapper, iDatabaseEncrypt);
			result=first.process(null);
			logger.info("执行手动解冻支付流程>>>>>>>>,出参:"+result);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}
	
	/**
	 * 手动转账
	 * 
	 * 支付记录(paymentId)
	 *     
	 */
	@Transactional(rollbackFor = Exception.class)
	public Map<String, Object> manualTransferHandle(Map<String, Object> paramMap) throws Exception {
		logger.info("执行手动转账流程>>>>>>>>,入参:"+paramMap);
		Map<String, Object> result=null;
		try {
			/**
			 * 获取支付记录
			 */
			DaPayChannel orderChannel = channelDao.selectDefault();
			paramMap.put("payChannelId", orderChannel.getPayChanneId());
			TrPaymentRecord paymentRecord = trPaymentRecordMapper.selectByPrimaryKey(Integer.valueOf(String.valueOf(paramMap.get("paymentId"))));
			TrTransferRecord transferRecord = transferMapper.queryTrRecordByFailure(paramMap);
			paramMap.put("clientID", transferRecord.getClientId());
			
			/**
			 * 执行手动转账流程
			 */
			HandleTransferInstProcess first = HandleTransferInstProcess.getInstanceWithParamByMapParam(userPayChannelMapper, bankLogMapper, paymentRecord, paramMap);
			result=first.process(null);
			if("success".equals(result.get("resultCode"))){
				transferRecord.setStatus(0);
				transferRecord.setDesc(result.get("resultMesg") == null?"":String.valueOf(result.get("resultMesg")));
				transferMapper.updateByPrimaryKey(transferRecord);
			}
			logger.info("执行手动转账流程>>>>>>>>,出参:"+result);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}
}
