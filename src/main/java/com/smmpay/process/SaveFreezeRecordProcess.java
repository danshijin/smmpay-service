package com.smmpay.process;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import com.smmpay.internal.service.IDatabaseEncrypt;
import com.smmpay.process.enumDef.PaymentLog;
import com.smmpay.process.enumDef.PaymentSatus;
import com.smmpay.process.enumDef.ReqResult;
import com.smmpay.process.enumDef.TranType;
import com.smmpay.respository.dao.SBankLogMapper;
import com.smmpay.respository.dao.SPaymentLogMapper;
import com.smmpay.respository.dao.TrFreezeRecordMapper;
import com.smmpay.respository.dao.TrPaymentRecordMapper;
import com.smmpay.respository.model.ReqDlmdetrn;
import com.smmpay.respository.model.SBankLog;
import com.smmpay.respository.model.SPaymentLog;
import com.smmpay.respository.model.TrFreezeRecord;
import com.smmpay.respository.model.TrPaymentRecord;
import com.smmpay.respository.model.base.ResCommon;
import com.smmpay.util.CPayUtils;
import com.smmpay.util.ClientIdUtils;
import com.smmpay.util.DateUtil;
import com.smmpay.util.SPaymentLogUtils;
import com.smmpay.util.XMLUtils;
/**
 * 
 * 生成资金冻结记录
 * 
 * @author zengshihua
 *
 */
public class SaveFreezeRecordProcess extends StepProcessor {

	
	private Logger logger=Logger.getLogger(SaveFreezeRecordProcess.class);
	
	private TrFreezeRecordMapper freezeRecordMapper;
	
	private TrPaymentRecordMapper paymentRecordMapper;
	
	private SBankLogMapper bankLogMapper;
	
	private SPaymentLogMapper pLogMapper;
	
	private IDatabaseEncrypt iDatabaseEncrypt;
	
	protected SaveFreezeRecordProcess() {
		super();
	}

	protected SaveFreezeRecordProcess(Map<String, Object> paramMap) {
		super(paramMap);
	}

	protected SaveFreezeRecordProcess(Map<String, Object> paramMap,
			StepProcessor nextProcessor) {
		super(paramMap, nextProcessor);
	}

	public static SaveFreezeRecordProcess getInstanceWithParam(TrFreezeRecordMapper freezeRecordMapper,
			TrPaymentRecordMapper paymentRecordMapper,
			SBankLogMapper bankLogMapper,
			SPaymentLogMapper pLogMapper,
			IDatabaseEncrypt iDatabaseEncrypt) {

		Map<String, Object> paramMap = buildParam(null);
		
		SaveFreezeRecordProcess process = new SaveFreezeRecordProcess(paramMap);
		process.freezeRecordMapper=freezeRecordMapper;
		process.paymentRecordMapper=paymentRecordMapper;
		process.bankLogMapper=bankLogMapper;
		process.pLogMapper=pLogMapper;
		process.iDatabaseEncrypt = iDatabaseEncrypt;
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
	 * 
	 * 调用中信冻结指令失败不回滚冻结记录
	 * 
	 * 轮询冻结结果除了成功，其他都是失败，转为人工处理
	 */
	@Override
	protected Map<String, Object> action(Map<String, Object> resultParamMap) {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		try {

			logger.info("开始生成冻结记录并调用中信冻结指令."+resultParamMap);
			String msg="",result="失败";
			SPaymentLog pLog=new SPaymentLog();
			pLog.setPaymentId(Integer.valueOf(String.valueOf(resultParamMap.get("paymentId"))));
			pLog.setOperationName(PaymentLog.P2.getMessage());
			
			TrFreezeRecord freezeRecord= buildFreezeRecord(resultParamMap);
			Integer  freezeId = freezeRecordMapper.insert(freezeRecord);
			
			// 添加验证字段
			iDatabaseEncrypt.addFreezeVerifyCode(freezeRecord.getFreezeId());  
			
			logger.info("生成资金冻结记录成功.count="+freezeId);
			if(freezeRecord.getFreezeId()==null){
				resultMap.put("resultCode", "exce");
				resultMap.put("resultMesg", "生成资金冻结记录失败.");
				
				msg="生成资金冻结记录失败.";
				pLog.setOpetationDesc(msg);
				pLog.setOperationResult(result);
				SPaymentLogUtils.write(pLog, pLogMapper);
				
				return resultMap;
			}
			
			/**
			 * 修改支付记录为状态为冻结中
			 */
			
			TrPaymentRecord paymentRecord = new TrPaymentRecord(); 
			paymentRecord.setPaymentStatus(Integer.valueOf(PaymentSatus.PS9.getCode()));//冻结中
			paymentRecord.setPaymentId(Integer.valueOf(String.valueOf(resultParamMap.get("paymentId"))));
			//paymentRecord.setFreezeTime(DateUtil.doFormatDate(new Date(), "yyyy-MM-dd HH:mm:ss"));
			Integer psCount = paymentRecordMapper.updatePaymentStatus(paymentRecord);
			
			
			logger.info("修改支付记录为冻结中.psCount="+psCount);
			if(psCount<=0){
				resultMap.put("resultCode", "exce");
				resultMap.put("resultMesg", "修改支付记录为冻结中失败.");
				return resultMap;
				//throw new Exception();
			}
			
			resultParamMap.put("clientID", freezeRecord.getFreezeClientId());
			String sendXML=XMLUtils.beanToXML(buildReqParam(resultParamMap));
			
			/**避免发生ED11308错误 接口调用频繁*/
			if(resultParamMap.containsKey("job")&&"Y".equals(resultParamMap.get("job"))){
				Thread.sleep(13000);
			}
			
			/**
			 * 请求中信冻结资金接口
			 */
			logger.info("请求中信接口，获取交易明细.入参:"+sendXML);
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
			bankLog.setRequestInterface("信强制转账-解冻支付BH接口 ");
			bankLog.setApplyTime(DateUtil.doFormatDate(new Date(), "yyyy-MM-dd HH:mm:ss"));
			bankLog.setPayChannelId(Integer.valueOf(String.valueOf(resultParamMap.get("payChannelId"))));
			bankLog.setReplyText(resultXML);
			
			
			if(resCommon !=null && resCommon.getStatus().equals(ReqResult.AAAAAAE.getCode())
					||resCommon.getStatus().equals(ReqResult.AAAAAAA.getCode())){
				
				bankLog.setReplyStatus(0);
				resultMap.put("frSaveCode", "succ");
				resultMap.put("resultCode", "success");
				resultMap.put("resultMesg", "调用中信冻结支付指令成功.");
				logger.info("调用中信冻结支付指令成功.");
				result="成功";
				msg=resCommon.getStatusText();
			}else{
				bankLog.setReplyStatus(1);
				resultMap.put("resultCode", "exce");
				resultMap.put("resultMesg", resCommon.getStatusText());
				msg=resCommon.getStatusText();
			}
			
			/**
			 * 保存请求日志
			 */
			bankLogMapper.insertSelective(bankLog);
			
			/**
			 * 步骤3，请求冻结买方货款
			 */
			pLog.setOpetationDesc(msg);
			pLog.setOperationResult(result);
			SPaymentLogUtils.write(pLog, pLogMapper);
			
			
		} catch (Exception e) {
			resultMap.put("resultCode", "exce");
			resultMap.put("resultMesg", "生成资金冻结记录/调用中信冻结资金发生异常");
			logger.error("生成资金冻结记录/调用中信冻结资金发生异常"+e.getMessage());
			e.printStackTrace();
		}
		logger.info("生成资金冻结记录/调用中信冻结资金结束."+resultMap);
		return resultMap;
	}
	
	protected TrFreezeRecord buildFreezeRecord(Map<String, Object> paramMap) {
		TrFreezeRecord fr = new TrFreezeRecord();
		if(paramMap!=null){
			
			if(paramMap.containsKey("userId")
					&&StringUtils.isNoneBlank((String)paramMap.get("userId"))){
				fr.setUserId(Integer.valueOf((String)paramMap.get("userId")));
			}else{
				return null;
			}
			if(paramMap.containsKey("subAccNm")
					&&StringUtils.isNoneBlank((String)paramMap.get("subAccNm"))){
				fr.setUserName((String)paramMap.get("subAccNm"));
			}else{
				return null;
			}
			if(paramMap.containsKey("dealMoney")
					&&paramMap.get("dealMoney")!=null){
				fr.setFreezeMoney(new BigDecimal(String.valueOf(paramMap.get("dealMoney"))));//
			}else{
				return null;
			}
			if(paramMap.containsKey("payChannelId")
					&&String.valueOf(paramMap.get("payChannelId"))!=null){
				fr.setPayChannelId(Integer.parseInt(String.valueOf(paramMap.get("payChannelId"))));
			}else{
				return null;
			}
			if(paramMap.containsKey("userPayChannelId")
					&&String.valueOf(paramMap.get("userPayChannelId"))!=null){
				fr.setUserPayChannelId(Integer.parseInt(String.valueOf(paramMap.get("userPayChannelId"))));
			}else{
				return null;
			}
			if(paramMap.containsKey("paymentId")&&
					Integer.valueOf(String.valueOf(paramMap.get("paymentId")))!=null){
				fr.setPaymentId(Integer.valueOf(String.valueOf(paramMap.get("paymentId"))));
			}else{
				return null;
			}
			
			fr.setFreezeStatus(0);
			fr.setApplyTime(DateUtil.doFormatDate(new Date(), "yyyy-MM-dd HH:mm:ss"));
			fr.setFreezeClientId(ClientIdUtils.CreateClientId());
			
			return fr;
		}else{
			return null;
		}
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
				req.setTranAmt(((BigDecimal)paramMap.get("dealMoney")).setScale(2, RoundingMode.HALF_UP));
			}else{
				return null;
			}
			req.setClientID(String.valueOf(paramMap.get("clientID")));
			req.setTranType(TranType.BR.getCode());
			//req.setMemo(memo);
			req.setTranFlag("0");
			
			return req;
		}else{
			return null;
		}
	}
	public static void main(String[] args) {
		System.out.println(new BigDecimal(0.010).setScale(2,RoundingMode.UNNECESSARY));
	}
}
