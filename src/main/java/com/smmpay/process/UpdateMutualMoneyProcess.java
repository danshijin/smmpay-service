package com.smmpay.process;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.smmpay.respository.dao.TrPaymentRecordMapper;
import com.smmpay.respository.dao.UserAccountMapper;
import com.smmpay.respository.dao.UserPayChannelMapper;
import com.smmpay.respository.model.TrPaymentRecord;

/**
 * 
 * 买方冻结金额-
 * 卖方可用余额+
 * @author zengshihua
 * 
 */
public class UpdateMutualMoneyProcess extends StepProcessor {

	private Logger logger = Logger.getLogger(UpdateMutualMoneyProcess.class);

	private TrPaymentRecordMapper paymentRecordMapper;
	
	private UserAccountMapper userAccountMapper;
	
	private UserPayChannelMapper userPayChannelMapper;
	
	private TrPaymentRecord paymentRecord;

	protected UpdateMutualMoneyProcess() {
		super();
	}

	protected UpdateMutualMoneyProcess(Map<String, Object> paramMap) {
		super(paramMap);
	}

	protected UpdateMutualMoneyProcess(Map<String, Object> paramMap,
			StepProcessor nextProcessor) {
		super(paramMap, nextProcessor);
	}

	public static UpdateMutualMoneyProcess getInstanceWithParam(
			TrPaymentRecordMapper paymentRecordMapper,
			UserAccountMapper userAccountMapper,
			UserPayChannelMapper userPayChannelMapper,
			TrPaymentRecord paymentRecord) {

		// Map<String, Object> paramMap = buildParam(null);
		UpdateMutualMoneyProcess process = new UpdateMutualMoneyProcess(null);
		process.paymentRecordMapper = paymentRecordMapper;
		process.paymentRecord = paymentRecord;
		process.userAccountMapper=userAccountMapper;
		process.userPayChannelMapper=userPayChannelMapper;
		return process;
	}

	@SuppressWarnings("unused")
	private static Map<String, Object> buildParam(Map<String, Object> param) {
		// 构建输入参数
		Map<String, Object> processParamMap = new HashMap<String, Object>();
		if (param != null) {
			processParamMap.putAll(param);
		}
		return processParamMap;

	}

	/**
	 * 实际操作逻辑
	 */
	@Override
	protected Map<String, Object> action(Map<String, Object> resultParamMap) {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		try {
			
			/**
			 * 买方冻结金额-
			 */
			Map<String, Object> buyParam=new HashMap<String, Object>();
			buyParam.put("freezeMoney", paymentRecord.getDealMoney());
			buyParam.put("userId", paymentRecord.getBuyerUserId());
			buyParam.put("userPayChannelId", paymentRecord.getBuyerPayChannelId());
			/**
			 * 更新账户冻结资金
			 */
			Integer acount=userAccountMapper.updateFreezeMoneyByUserId2(buyParam);
			logger.info("买方冻结金额- 更新账户冻结资金."+acount);
			/**
			 * 更新支付通道冻结资金
			 */
			Integer pcount=userPayChannelMapper.updateFreezeMoneyByUserIdPCId2(buyParam);
			logger.info("买方冻结金额- 更新支付通道冻结资金."+pcount);
			
			/**
			 * 卖方可用余额+
			 */
			Map<String, Object> selletParam=new HashMap<String, Object>();
			selletParam.put("changeMoney",paymentRecord.getDealMoney());
			selletParam.put("userId", paymentRecord.getSellerUserId());
			selletParam.put("userPayChannelId", paymentRecord.getSellerPayChannelId());
			/**
			 * 卖方可用余额+ 账户余额
			 */
			int countAccount= userAccountMapper.updateUserMoneyByUserId(selletParam);
			logger.info("卖方可用余额+ 账户余额."+countAccount);
			
			/**
			 * 卖方可用余额+ 支付通道余额.
			 */
			int countPayChanne= userPayChannelMapper.updateUserMoneyByUserIdPCId(selletParam);
			logger.info("卖方可用余额+ 支付通道余额."+countPayChanne);
			
			resultMap.put("resultCode", "success");
			resultMap.put("resultMesg", "买方冻结金额- 卖方可用余额+.");
			logger.info("买方冻结金额- 卖方可用余额+.成功.");
			
			
		} catch (Exception e) {
			
			resultMap.put("resultCode", "exce");
			resultMap.put("resultMesg", "买方冻结金额- 卖方可用余额+.失败.");
			logger.error("买方冻结金额- 卖方可用余额+.失败."+e.getMessage());
		}
		
		return resultMap;
	}

}
