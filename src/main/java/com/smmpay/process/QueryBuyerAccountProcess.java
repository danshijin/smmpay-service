package com.smmpay.process;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.smmpay.process.enumDef.PaymentLog;
import com.smmpay.respository.dao.SPaymentLogMapper;
import com.smmpay.respository.dao.UserMoneyExceptionMapper;
import com.smmpay.respository.dao.UserPayChannelMapper;
import com.smmpay.respository.model.SPaymentLog;
import com.smmpay.respository.model.TrPaymentRecord;
import com.smmpay.respository.model.UserMoneyException;
import com.smmpay.util.SPaymentLogUtils;

public class QueryBuyerAccountProcess extends StepProcessor {

	
	private Logger logger=Logger.getLogger(QueryBuyerAccountProcess.class);
	
	private TrPaymentRecord paymentRecord;
	
	private UserMoneyExceptionMapper userMoneyExceptionMapper;
	
	private SPaymentLogMapper pLogMapper;
	
	
	protected QueryBuyerAccountProcess() {
		super();
	}

	protected QueryBuyerAccountProcess(Map<String, Object> paramMap) {
		super(paramMap);
	}

	protected QueryBuyerAccountProcess(Map<String, Object> paramMap,
			StepProcessor nextProcessor) {
		super(paramMap, nextProcessor);
	}

	public static QueryBuyerAccountProcess getInstanceWithParam(UserMoneyExceptionMapper userMoneyExceptionMapper,
			Map<String, Object> param,
			SPaymentLogMapper pLogMapper) {

		Map<String, Object> paramMap = buildParam(param);
		
		QueryBuyerAccountProcess process = new QueryBuyerAccountProcess(paramMap);
		process.userMoneyExceptionMapper=userMoneyExceptionMapper;
		process.pLogMapper=pLogMapper;
		return process;
	}

	@SuppressWarnings("unused")
	private static Map<String, Object> buildParam(Map<String, Object> param) {
		// 构建输入参数
		Map<String, Object> processParamMap = new HashMap<String, Object>();
		if(param!=null){
			processParamMap.putAll(param);
		}
		return processParamMap;

	}
	/**
	 * 组装参数返回
	 */
	@Override
	protected Map<String, Object> action(Map<String, Object> resultParamMap) {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		try {
			SPaymentLog pLog=new SPaymentLog();
			logger.error("检查买方账户是否有异常记录paymentId:"+resultParamMap.get("paymentId"));
			pLog.setPaymentId(Integer.valueOf(String.valueOf(resultParamMap.get("paymentId"))));
			pLog.setOperationName(PaymentLog.P1.getMessage());
			
			
			logger.info("开始检查买方账户是否有异常记录."+resultParamMap);
			List<UserMoneyException> umEList=userMoneyExceptionMapper.queryUMExceByUserId(Integer.valueOf(String.valueOf(resultParamMap.get("userId"))));
			if(umEList!=null&&umEList.size()>0){
				resultMap.put("resultCode", "exce"); 
				resultMap.put("resultMesg", "买方账户存在异常记录，请先处理");
				/**
				 * 步骤日志 ，检查账户是否有异常记录
				 */
				pLog.setOpetationDesc("买方账户存在异常记录，请先处理");
				pLog.setOperationResult("失败");
				SPaymentLogUtils.write(pLog, pLogMapper);
				
			}else{
				resultMap.put("resultCode", "success");
				resultMap.put("resultMesg", "调用成功");
			}
			
		} catch (Exception e) {
			resultMap.put("resultCode", "exce");
			resultMap.put("resultMesg", "检查买方账户是否有异常记录发生异常");
			logger.error("检查买方账户是否有异常记录发生异常",e);
			e.printStackTrace();
		}
		logger.info("结束检查买方账户是否有异常记录."+resultMap);
		return resultMap;
	}

}
