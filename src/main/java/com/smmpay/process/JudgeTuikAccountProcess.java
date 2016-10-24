package com.smmpay.process;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import com.smmpay.respository.dao.TrPaymentRecordMapper;
import com.smmpay.respository.dao.UserPayChannelMapper;
import com.smmpay.respository.model.TrPaymentRecord;
import com.smmpay.respository.model.UserPayChannel;

/**
 * 
 * 判断账户是否为有色网退款账户
 * 
 */
public class JudgeTuikAccountProcess extends StepProcessor {

	private Logger logger = Logger.getLogger(JudgeTuikAccountProcess.class);

	
	private UserPayChannelMapper userPayChannelMapper;
	
	private TrPaymentRecord paymentRecord;
	

	protected JudgeTuikAccountProcess() {
		super();
	}

	protected JudgeTuikAccountProcess(Map<String, Object> paramMap) {
		super(paramMap);
	}

	protected JudgeTuikAccountProcess(Map<String, Object> paramMap,
			StepProcessor nextProcessor) {
		super(paramMap, nextProcessor);
	}

	public static JudgeTuikAccountProcess getInstanceWithParam(UserPayChannelMapper userPayChannelMapper,
			TrPaymentRecord paymentRecord,
			Map<String, Object> param) {
		 Map<String, Object> paramMap = buildParam(param);
		JudgeTuikAccountProcess process = new JudgeTuikAccountProcess(paramMap);
		process.userPayChannelMapper=userPayChannelMapper;
		process.paymentRecord=paymentRecord;
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
			UserPayChannel userPayChannel=userPayChannelMapper.selectByPrimaryKey(paymentRecord.getSellerPayChannelId());
			if(userPayChannel.getUserAccountNo().equals(String.valueOf(resultParamMap.get("YSWTKZH")))){
				resultMap.put("accountFlag", "Y");
				resultMap.put("resultMesg", "Y");
			}else{
				resultMap.put("accountFlag", "N");
				resultMap.put("resultMesg", "N");
			}
			resultMap.put("resultCode", "success");
		} catch (Exception e) {
			resultMap.put("resultCode", "exce");
			resultMap.put("resultMesg", "判断收款户是否为有色网退款账户发生异常");
			logger.error("判断收款户是否为有色网退款账户发生异常",e);
			
		}
		
		return resultMap;
	}

}
