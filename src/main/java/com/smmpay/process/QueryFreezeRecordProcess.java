package com.smmpay.process;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import com.smmpay.process.enumDef.PaymentLog;
import com.smmpay.respository.dao.SPaymentLogMapper;
import com.smmpay.respository.dao.TrCashApplyRecordMapper;
import com.smmpay.respository.dao.TrFreezeRecordMapper;
import com.smmpay.respository.dao.TrRecordMapper;
import com.smmpay.respository.dao.TrTransferRecordMapper;
import com.smmpay.respository.dao.TrUnfreezeRecordMapper;
import com.smmpay.respository.model.SPaymentLog;
import com.smmpay.respository.model.TrCashApplyRecord;
import com.smmpay.util.SPaymentLogUtils;
/**
 * 
 * 检查账户是否存在"请求中"的冻结记录
 * 
 * @author zengshihua
 *
 */
public class QueryFreezeRecordProcess extends StepProcessor {

	
	private Logger logger=Logger.getLogger(QueryFreezeRecordProcess.class);
	
	private TrFreezeRecordMapper freezeRecordMapper;
	
	private TrUnfreezeRecordMapper trUnfreezeRecordMapper;
	
	private TrRecordMapper trRecordMapper;
	
	private TrTransferRecordMapper trTransferRecordMapper;
	
	private TrCashApplyRecordMapper trCashApplyRecordMapper;
	
	private SPaymentLogMapper pLogMapper;
	
	
	protected QueryFreezeRecordProcess() {
		super();
	}

	protected QueryFreezeRecordProcess(Map<String, Object> paramMap) {
		super(paramMap);
	}

	protected QueryFreezeRecordProcess(Map<String, Object> paramMap,
			StepProcessor nextProcessor) {
		super(paramMap, nextProcessor);
	}

	public static QueryFreezeRecordProcess getInstanceWithParam(TrFreezeRecordMapper freezeRecordMapper,
			TrUnfreezeRecordMapper trUnfreezeRecordMapper,
			TrRecordMapper trRecordMapper,
			TrTransferRecordMapper trTransferRecordMapper,
			TrCashApplyRecordMapper trCashApplyRecordMapper,
			SPaymentLogMapper pLogMapper) {

		Map<String, Object> paramMap = buildParam(null);
		
		QueryFreezeRecordProcess process = new QueryFreezeRecordProcess(paramMap);
		process.freezeRecordMapper=freezeRecordMapper;
		process.trUnfreezeRecordMapper=trUnfreezeRecordMapper;
		process.trRecordMapper=trRecordMapper;
		process.trTransferRecordMapper=trTransferRecordMapper;
		process.trCashApplyRecordMapper=trCashApplyRecordMapper;
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
			String msg="",result="失败";
			
			SPaymentLog pLog=new SPaymentLog();
			pLog.setPaymentId(Integer.valueOf(String.valueOf(resultParamMap.get("paymentId"))));
			pLog.setOperationName(PaymentLog.P1.getMessage());
			
			logger.info("开始检查买方账户是否有异常记录."+resultParamMap);
			/**
			 * 检查买方账户是否有“请求中”的冻结记录
			 */
			Integer count=freezeRecordMapper.queryFreezeRecordStatus(resultParamMap);
			
			
			if(count!=null&&count>0){
				resultMap.put("resultCode", "exce");
				resultMap.put("resultMesg", "账户存在请求中的冻结记录,本步骤结束.");
				logger.error("账户存在请求中的冻结记录,本步骤结束.resultParamMap："+resultParamMap);
				msg="账户存在请求中的冻结记录";
			}else{
				
				/**
				 * 解冻记录
				 *recevieUserId
				 */
				Integer unCount=trUnfreezeRecordMapper.queryUNFreezeStatus(resultParamMap);
				if(unCount!=null&&unCount>0){
					resultMap.put("resultCode", "exce");
					resultMap.put("resultMesg", "账户存在请求中的解冻记录,本步骤结束.");
					logger.error("账户存在请求中的解冻记录,本步骤结束.resultParamMap："+resultParamMap);
					msg="账户存在请求中的解冻记录,本步骤结束";
				}else{
					/**
					 * 出金申请记录
					 */
					//Integer reCount = trRecordMapper.queryApplyStatus(resultParamMap);
					TrCashApplyRecord car=new TrCashApplyRecord();
					car.setApplyStatus(1);
					car.setUserId(Integer.valueOf(String.valueOf(resultParamMap.get("userId"))));
					Integer reCount=trCashApplyRecordMapper.queryCashApplyRecordByStatus(car);
					if(reCount!=null&&reCount>0){
						resultMap.put("resultCode", "exce");
						resultMap.put("resultMesg", "账户存在请求中的出金记录,本步骤结束.");
						logger.error("账户存在请求中的出金记录,本步骤结束.");
						msg="账户存在请求中的出金记录";
					}else{
						
						/**
						 * 转账记录
						 */
						Integer tfCount =trTransferRecordMapper.queryStatus(resultParamMap);
						
						if(tfCount!=null&&tfCount>0){
							resultMap.put("resultCode", "exce");
							resultMap.put("resultMesg", "账户存在请求中的转账记录,本步骤结束.");
							logger.error("账户存在请求中的转账记录,本步骤结束.resultParamMap："+resultParamMap);
							msg="账户存在请求中的转账记录";
						}else{
							resultMap.put("resultCode", "success");
							resultMap.put("resultMesg", "调用成功");
							msg="账户无异常";
							result="成功";
						}
					}
					
				}
				
			}
			/**
			 * 步骤2,检查账户是否有异常
			 */
			pLog.setOpetationDesc(msg);
			pLog.setOperationResult(result);
			SPaymentLogUtils.write(pLog, pLogMapper);
			
		} catch (Exception e) {
			resultMap.put("resultCode", "exce");
			resultMap.put("resultMesg", "检查账户是否存在请求中的冻结记录");
			logger.error("检查账户是否存在请求中的冻结记录"+e.getMessage());
			e.printStackTrace();
		}
		logger.info("结束检查账户是否存在请求中的冻结记录."+resultMap);
		return resultMap;
	}

}
