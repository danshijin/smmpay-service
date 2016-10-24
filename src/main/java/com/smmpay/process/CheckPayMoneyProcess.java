package com.smmpay.process;

import com.smmpay.process.enumDef.PaymentLog;
import com.smmpay.respository.dao.SPaymentLogMapper;
import com.smmpay.respository.model.SPaymentLog;
import com.smmpay.util.SPaymentLogUtils;
import org.apache.log4j.Logger;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
/**
 * 
 * 检查余额是否足够
 * 
 * @author zengshihua
 *
 */
public class CheckPayMoneyProcess extends StepProcessor {

	
	private Logger logger=Logger.getLogger(CheckPayMoneyProcess.class);
	
	private SPaymentLogMapper pLogMapper;
	
	protected CheckPayMoneyProcess() {
		super();
	}

	protected CheckPayMoneyProcess(Map<String, Object> paramMap) {
		super(paramMap);
	}

	protected CheckPayMoneyProcess(Map<String, Object> paramMap,
			StepProcessor nextProcessor) {
		super(paramMap, nextProcessor);
	}

	public static CheckPayMoneyProcess getInstanceWithParam(SPaymentLogMapper pLogMapper) {

		Map<String, Object> paramMap = buildParam(null);
		CheckPayMoneyProcess process = new CheckPayMoneyProcess(paramMap);
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
			logger.info("开始检查买方账户余额是否足够."+resultParamMap);
			
			String msg="",result="失败";
			SPaymentLog pLog=new SPaymentLog();
			pLog.setPaymentId(Integer.valueOf(String.valueOf(resultParamMap.get("paymentId"))));
			pLog.setOperationName(PaymentLog.P1.getMessage());
			
			if(resultParamMap.containsKey("userMoney")){
				BigDecimal userMoney=new BigDecimal(String.valueOf(resultParamMap.get("userMoney")));//可用余额
				BigDecimal dealMoney=new BigDecimal(String.valueOf(resultParamMap.get("dealMoney")));//交易金额
				if(userMoney.compareTo(dealMoney)==1||userMoney.compareTo(dealMoney)==0){
					resultMap.put("resultCode", "success");
					resultMap.put("resultMesg", "调用成功");
				}else{
					resultMap.put("resultCode", "exce");
					resultMap.put("resultMesg", "可用余额不足,本步骤操作结束.");
					
					msg="可用余额不足";
					pLog.setOpetationDesc(msg);
					pLog.setOperationResult(result);
					SPaymentLogUtils.write(pLog, pLogMapper);
					
					
				}
				
			}else{
				resultMap.put("resultCode", "exce");
				resultMap.put("resultMesg", "缺少userMoney参数");
			}
			
		} catch (Exception e) {
			resultMap.put("resultCode", "exce");
			resultMap.put("resultMesg", "检查余额是否足够发生异常");
			logger.error("检查余额是否足够发生异常"+e.getMessage());
			e.printStackTrace();
		}
		logger.info("结束检检查余额是否足够发生异常."+resultMap);
		return resultMap;
	}
}
