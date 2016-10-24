package com.smmpay.payment.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.smmpay.internal.service.IDatabaseEncrypt;
import com.smmpay.payment.SatyPayService;
import com.smmpay.process.CheckPayMoneyProcess;
import com.smmpay.process.QueryBalanceProcess;
import com.smmpay.process.QueryBuyerAccountProcess;
import com.smmpay.process.QueryFreezeRecordProcess;
import com.smmpay.process.QueryTrRecordProcess;
import com.smmpay.process.SaveFreezeRecordProcess;
import com.smmpay.process.StepProcessor;
import com.smmpay.process.SynCreditRecordProcess;
import com.smmpay.process.enumDef.OperateType;
import com.smmpay.process.enumDef.PaymentSatus;
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
import com.smmpay.respository.model.TrPaymentRecord;
import com.smmpay.respository.model.UserPayChannel;

/**
 * 
 * @author zengshihua
 * 
 */
@Service
public class SatyPayServiceImpl implements SatyPayService {

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
	private TrUnfreezeRecordMapper trUnfreezeRecordMapper;
	@Autowired
	private TrTransferRecordMapper trTransferRecordMapper;
	@Autowired
	private TrCashApplyRecordMapper trCashApplyRecordMapper;
	@Autowired
	private SBankLogMapper bankLogMapper;
	@Autowired
	private SPaymentLogMapper pLogMapper;
	@Autowired
	private IDatabaseEncrypt iDatabaseEncrypt;

	
	public synchronized Map<String, Object> satyPayHandle() throws Exception {
		
		/**
		 * 查询待支付记录
		 */
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("paymentStatus", PaymentSatus.PS0.getCode());
		paramMap.put("isHandler", 0);
		List<TrPaymentRecord> paymentRecords = trPaymentRecordMapper.queryPaymentRecord(paramMap);
		
		logger.info("共需处理待支付记录:"+paymentRecords.size());
		/**
		 * 处理待支付记录
		 */
		if (paymentRecords != null && paymentRecords.size() > 0) {
			QueryBuyerAccountProcess fristProcess=null;
			for (int i = 0; i < paymentRecords.size(); i++) {
				TrPaymentRecord paymentRecord = paymentRecords.get(i);
				Map<String, Object> param=new HashMap<String, Object>();
				param.put("userId", Integer.valueOf(paymentRecord.getBuyerUserId()));
				param.put("paymentId", paymentRecord.getPaymentId());
				fristProcess=QueryBuyerAccountProcess.getInstanceWithParam(userMoneyExceptionMapper, param,pLogMapper);//判断账户是否有异常
				startProcess(fristProcess,fristProcess,paymentRecord);
			}
		}
		return null;
	}
	
	@Transactional(rollbackFor = Exception.class)
	protected void  startProcess(StepProcessor frstProcessor,
				StepProcessor nextProcessor,TrPaymentRecord paymentRecord) throws Exception {
		try {
			
			nextProcessor.next(QueryTrRecordProcess.getInstanceWithParam(bulidQueyParam(paymentRecord),trRecordMapper,userAccountMapper))//获取最后同步记录时间
						.next(SynCreditRecordProcess.getInstanceWithParam(trRecordMapper,userAccountMapper,userPayChannelMapper))//同步入金记录
						.next(QueryFreezeRecordProcess.getInstanceWithParam(freezeRecordMapper,trUnfreezeRecordMapper,trRecordMapper,trTransferRecordMapper,trCashApplyRecordMapper,pLogMapper))//检查账户是否存在请求中的冻结记录
						.next(QueryBalanceProcess.getInstanceWithParam(userPayChannelMapper,userMoneyExceptionMapper,trPaymentRecordMapper,pLogMapper))//查询以及比对余额
						.next(CheckPayMoneyProcess.getInstanceWithParam(pLogMapper))//检查余额是否足够
						.next(SaveFreezeRecordProcess.getInstanceWithParam(freezeRecordMapper,trPaymentRecordMapper,bankLogMapper,pLogMapper, iDatabaseEncrypt));//生成资金冻结记录，状态请求中(包含发送冻结指令至中信)
			frstProcessor.process(null);
		} catch (Exception e) {
			logger.error("轮询待支付发生错误:",e);
			e.printStackTrace();
		}
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
		paramMap.put("sellerUserId", paymentRecord.getSellerUserId());
		paramMap.put("paymentId", paymentRecord.getPaymentId());
		paramMap.put("dealMoney", paymentRecord.getDealMoney());
		paramMap.put("operateType", OperateType.O1.getCode());
		
		paramMap.put("job", "Y");//在查询时睡眠标记
		
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
