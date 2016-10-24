package com.smmpay.payment.impl;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.smmpay.internal.service.IDatabaseEncrypt;
import com.smmpay.payment.ManualFreezeService;
import com.smmpay.process.CheckPayMoneyProcess;
import com.smmpay.process.CreateFreezeRecordProcess;
import com.smmpay.process.QueryBalanceProcess;
import com.smmpay.process.QueryFreezeRecordProcess;
import com.smmpay.process.QueryTrRecordProcess;
import com.smmpay.process.SendFreezeInstrProcess;
import com.smmpay.process.StepProcessor;
import com.smmpay.process.SynCreditRecordProcess;
import com.smmpay.process.UpdatePRStatus2Process;
import com.smmpay.process.enumDef.OperateType;
import com.smmpay.respository.dao.SBankLogMapper;
import com.smmpay.respository.dao.SPaymentLogMapper;
import com.smmpay.respository.dao.TrCashApplyRecordMapper;
import com.smmpay.respository.dao.TrConfirmAuditMapper;
import com.smmpay.respository.dao.TrFreezeRecordMapper;
import com.smmpay.respository.dao.TrPaymentRecordMapper;
import com.smmpay.respository.dao.TrRecordMapper;
import com.smmpay.respository.dao.TrTransferRecordMapper;
import com.smmpay.respository.dao.TrUnfreezeRecordMapper;
import com.smmpay.respository.dao.UserAccountMapper;
import com.smmpay.respository.dao.UserMoneyExceptionMapper;
import com.smmpay.respository.dao.UserPayChannelMapper;
import com.smmpay.respository.model.TrPaymentRecord;
import com.smmpay.respository.model.UserPayChannel;

/**
 *  支付冻结手动处理(自动冻结失败,后台重发支付冻结)
 * 
 */
@Service("manualFreezeService")
public class ManualFreezeServiceImpl implements ManualFreezeService {

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
	private TrTransferRecordMapper trTransferRecordMapper;
	@Autowired
	private TrCashApplyRecordMapper trCashApplyRecordMapper;
	@Autowired
	private SPaymentLogMapper pLogMapper;
	@Autowired
	private IDatabaseEncrypt iDatabaseEncrypt;
	
	private StepProcessor fristProcessor;
	private StepProcessor nextProcessor;
	
	/**
	 * 入参:支付记录(paymentId)
	 */
	@Transactional(rollbackFor = Exception.class)
	public Map<String, Object> manualFreezeHandle(Map<String, Object> paramMap) throws Exception {
		Map<String, Object> result = null;
		try {
			
			/**
			 * 获取支付记录
			 */
			TrPaymentRecord paymentRecord = trPaymentRecordMapper.selectByPrimaryKey(Integer.valueOf(String.valueOf(paramMap.get("paymentId"))));
			
			QueryTrRecordProcess first=QueryTrRecordProcess.getInstanceWithParam(bulidQueyParam(paymentRecord),trRecordMapper,userAccountMapper);//获取最后同步记录时间
			fristProcessor=first;
			nextProcessor=first;
			nextProcessor.next(SynCreditRecordProcess.getInstanceWithParam(trRecordMapper,userAccountMapper,userPayChannelMapper))//同步入金记录
						 .next(QueryFreezeRecordProcess.getInstanceWithParam(freezeRecordMapper,trUnfreezeRecordMapper,trRecordMapper,trTransferRecordMapper,trCashApplyRecordMapper,pLogMapper))//检查账户是否存在请求中的冻结记录
						 .next(QueryBalanceProcess.getInstanceWithParam(userPayChannelMapper,userMoneyExceptionMapper,trPaymentRecordMapper,pLogMapper))//查询以及比对余额
						 .next(CheckPayMoneyProcess.getInstanceWithParam(pLogMapper))//检查余额是否足够
						 .next(CreateFreezeRecordProcess.getInstanceWithParam(freezeRecordMapper, paymentRecord, iDatabaseEncrypt))//生成新的冻结记录
						 .next(UpdatePRStatus2Process.getInstanceWithParam(trPaymentRecordMapper, paymentRecord))//更新支付记录为待付款
						 .next(SendFreezeInstrProcess.getInstanceWithParam(bankLogMapper));//发送支付冻结指令至中信;
			result = fristProcessor.process(null);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return result;
	}
	
	/**
	 * 根据userId取得付款方账号
	 * 作为第二步操作的入参
	 * @param paramMap
	 * @return
	 */
	protected Map<String, Object> bulidQueyParam(TrPaymentRecord paymentRecord) {
		
		Map<String, Object> paramMap=new HashMap<String, Object>();
		paramMap.put("userId", paymentRecord.getBuyerUserId());
		paramMap.put("paymentId", paymentRecord.getPaymentId());
		paramMap.put("dealMoney", paymentRecord.getDealMoney().setScale(2, BigDecimal.ROUND_DOWN));
		paramMap.put("operateType", OperateType.O1.getCode());
		
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
		return paramMap;
	}
	
}
