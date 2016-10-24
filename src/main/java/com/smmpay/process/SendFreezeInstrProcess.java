package com.smmpay.process;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import com.smmpay.process.enumDef.ReqResult;
import com.smmpay.process.enumDef.TranType;
import com.smmpay.respository.dao.SBankLogMapper;
import com.smmpay.respository.model.ReqDlmdetrn;
import com.smmpay.respository.model.SBankLog;
import com.smmpay.respository.model.base.ResCommon;
import com.smmpay.util.CPayUtils;
import com.smmpay.util.DateUtil;
import com.smmpay.util.XMLUtils;
/**
 * 
 * 发送支付冻结指令
 * 
 * @author zengshihua
 *
 */
public class SendFreezeInstrProcess extends StepProcessor {
	
	private SBankLogMapper bankLogMapper;
	
	private Logger logger=Logger.getLogger(SendFreezeInstrProcess.class);
	
	protected SendFreezeInstrProcess() {
		super();
	}

	protected SendFreezeInstrProcess(Map<String, Object> paramMap) {
		super(paramMap);
	}

	protected SendFreezeInstrProcess(Map<String, Object> paramMap,
			StepProcessor nextProcessor) {
		super(paramMap, nextProcessor);
	}

	public static SendFreezeInstrProcess getInstanceWithParam(SBankLogMapper bankLogMapper) {

		Map<String, Object> paramMap = buildParam(null);
		
		SendFreezeInstrProcess process = new SendFreezeInstrProcess(paramMap);
		process.bankLogMapper=bankLogMapper;
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
	 * 实际处理逻辑
	 * 
	 */
	@Override
	protected Map<String, Object> action(Map<String, Object> resultParamMap) {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		try {


			String sendXML=XMLUtils.beanToXML(buildReqParam(resultParamMap));
			/**
			 * 请求中信冻结资金接口
			 */
			logger.info("请求中信冻结资金接口.入参:"+sendXML);
			String resultXML=CPayUtils.postRequest(sendXML);
			/**
			 * 将响应结果转换成实体类
			 */
			logger.info("将响应结果转换成实体类.入参:"+resultXML);
			ResCommon resCommon=(ResCommon) XMLUtils.xmlToBean(resultXML,ResCommon.class,null);
			logger.info("将响应结果转换成实体类.出参:"+resCommon);
			
			/**
			 * 记录日志
			 */
			
			SBankLog bankLog=new SBankLog();
			bankLog.setRequestParam(sendXML);
			bankLog.setRequestInterface("支付冻结 ");
			bankLog.setApplyTime(DateUtil.doFormatDate(new Date(), "yyyy-MM-dd HH:mm:ss"));
			bankLog.setPayChannelId(Integer.valueOf(String.valueOf(resultParamMap.get("payChannelId"))));
			bankLog.setReplyText(resultXML);
			
			
			if(resCommon.getStatus().equals(ReqResult.AAAAAAE.getCode())
					||resCommon.getStatus().equals(ReqResult.AAAAAAA.getCode())){
				bankLog.setReplyStatus(0);
				resultMap.put("frSaveCode", "succ");
				resultMap.put("resultCode", "success");
				resultMap.put("resultMesg", "调用中信冻结支付指令成功.");
				logger.info("调用中信冻结支付指令成功.");
			}else{
				bankLog.setReplyStatus(0);
				resultMap.put("resultCode", "exce");
				resultMap.put("resultMesg", resCommon.getStatusText());
			}
			
			/**
			 * 保存请求日志
			 */
			bankLogMapper.insertSelective(bankLog);
			
		} catch (Exception e) {
			resultMap.put("resultCode", "exce");
			resultMap.put("resultMesg", "调用中信冻结资金发生异常");
			logger.error("调用中信冻结资金发生异常"+e.getMessage());
		}
		logger.info("调用中信冻结资金结束."+resultMap);
		return resultMap;
	}
	
	protected ReqDlmdetrn buildReqParam(Map<String, Object> paramMap) {
		ReqDlmdetrn req = new ReqDlmdetrn();
		if(paramMap!=null){
			
			if(paramMap.containsKey("subAccNo")
					&&StringUtils.isNoneBlank((String)paramMap.get("subAccNo"))){
				req.setPayAccNo((String)paramMap.get("subAccNo"));
			}else{
				return null;
			}
			if(paramMap.containsKey("dealMoney")
					&&(BigDecimal)paramMap.get("dealMoney")!=null){
				req.setTranAmt((BigDecimal)paramMap.get("dealMoney"));
			}else{
				return null;
			}
			req.setClientID(String.valueOf(paramMap.get("clientID")));
			req.setTranType(TranType.BR.getCode());
			req.setTranFlag("0");
			
			return req;
		}else{
			return null;
		}
	}
}
