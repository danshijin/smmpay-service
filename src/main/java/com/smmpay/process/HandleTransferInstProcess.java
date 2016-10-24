package com.smmpay.process;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import com.smmpay.process.enumDef.ReqResult;
import com.smmpay.process.enumDef.TranType;
import com.smmpay.respository.dao.SBankLogMapper;
import com.smmpay.respository.dao.SCallClientLogMapper;
import com.smmpay.respository.dao.TrTransferRecordMapper;
import com.smmpay.respository.dao.UserPayChannelMapper;
import com.smmpay.respository.model.ReqDlmdetrn;
import com.smmpay.respository.model.SBankLog;
import com.smmpay.respository.model.TrPaymentRecord;
import com.smmpay.respository.model.UserPayChannel;
import com.smmpay.respository.model.base.ResCommon;
import com.smmpay.util.CPayUtils;
import com.smmpay.util.DateUtil;
import com.smmpay.util.XMLUtils;

/**
 * 
 * 针对新的支付记录，执行“转账”指令
 * 
 * @author zengshihua
 * 
 */
public class HandleTransferInstProcess extends StepProcessor {

	private Logger logger = Logger.getLogger(HandleTransferInstProcess.class);

	private UserPayChannelMapper userPayChannelMapper;
	
	private SBankLogMapper bankLogMapper;
	
	private TrPaymentRecord paymentRecord ;

	protected HandleTransferInstProcess() {
		super();
	}

	protected HandleTransferInstProcess(Map<String, Object> paramMap) {
		super(paramMap);
	}

	protected HandleTransferInstProcess(Map<String, Object> paramMap,
			StepProcessor nextProcessor) {
		super(paramMap, nextProcessor);
	}

	public static HandleTransferInstProcess getInstanceWithParam(UserPayChannelMapper userPayChannelMapper,
			SBankLogMapper bankLogMapper,
			TrPaymentRecord paymentRecord  ) {

		// Map<String, Object> paramMap = buildParam(param);
		HandleTransferInstProcess process = new HandleTransferInstProcess(null);
		process.userPayChannelMapper=userPayChannelMapper;
		process.bankLogMapper=bankLogMapper;
		process.paymentRecord=paymentRecord;
		return process;
	}

	public static HandleTransferInstProcess getInstanceWithParamByMapParam(UserPayChannelMapper userPayChannelMapper,
			SBankLogMapper bankLogMapper,
			TrPaymentRecord paymentRecord, Map<String, Object> param) {

	    Map<String, Object> paramMap = buildParam(param);
		HandleTransferInstProcess process = new HandleTransferInstProcess(paramMap);
		process.userPayChannelMapper=userPayChannelMapper;
		process.bankLogMapper=bankLogMapper;
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
	 * 实际处理逻辑
	 * @throws Exception 
	 */
	@Override
	protected Map<String, Object> action(Map<String, Object> resultParamMap) throws Exception {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		try {
			
			UserPayChannel payUserPayChannel=userPayChannelMapper.selectByPrimaryKey(paymentRecord.getSellerPayChannelId());
			UserPayChannel recvUserPayChannel=userPayChannelMapper.selectByPrimaryKey(paymentRecord.getBuyerPayChannelId());
			logger.info("isTransfer:"+resultParamMap.get("isTransfer"));
			if(resultParamMap.get("isTransfer") != null && String.valueOf(resultParamMap.get("isTransfer")).equals("1")){
				payUserPayChannel=userPayChannelMapper.selectByPrimaryKey(paymentRecord.getBuyerPayChannelId());
			    recvUserPayChannel=userPayChannelMapper.selectByPrimaryKey(paymentRecord.getSellerPayChannelId());
			}
			logger.info("paymentId:"+paymentRecord.getPaymentId());
			logger.info("SellerPayChannelId:"+paymentRecord.getSellerPayChannelId());
			logger.info("BuyerPayChannelId:"+paymentRecord.getBuyerPayChannelId());
			
			logger.info("recvUserPayChannel:"+recvUserPayChannel.getUserAccountName());
			
			ReqDlmdetrn reqDl = new ReqDlmdetrn();
			reqDl.setPayAccNo(payUserPayChannel.getUserAccountNo());
			reqDl.setTranType(TranType.BF.getCode());
			reqDl.setRecvAccNo(recvUserPayChannel.getUserAccountNo());
			reqDl.setRecvAccNm(recvUserPayChannel.getUserAccountName());
			reqDl.setTranAmt(paymentRecord.getDealMoney().setScale(2, BigDecimal.ROUND_DOWN));
			//reqDl.setTranType("0");
			reqDl.setClientID(String.valueOf(resultParamMap.get("clientID")));//clientID
			
			
			String sendXML=XMLUtils.beanToXML(reqDl);
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
			
			/**
			 * 记录日志
			 */
			
			SBankLog bankLog=new SBankLog();
			bankLog.setRequestParam(sendXML);
			bankLog.setRequestInterface("转账 ");
			bankLog.setApplyTime(DateUtil.doFormatDate(new Date(), "yyyy-MM-dd HH:mm:ss"));
			bankLog.setPayChannelId(Integer.valueOf(String.valueOf(resultParamMap.get("payChannelId"))));
			bankLog.setReplyText(resultXML);
			
			
			if(resCommon.getStatus().equals(ReqResult.AAAAAAE.getCode())
					||resCommon.getStatus().equals(ReqResult.AAAAAAA.getCode())){
				bankLog.setReplyStatus(0);
				resultMap.put("resultCode", "success");
				resultMap.put("resultMesg", ".");
				logger.info("转账指令发送成功.");
			}else{
				bankLog.setReplyStatus(1);
				resultMap.put("resultCode", "exce");
				resultMap.put("resultMesg", resCommon.getStatusText());
				
				resultMap.put("resultCodeTransfer", "failure");
				logger.info("转账指令发送失败.");
				//resultMap.put("resultMesg", resCommon.getStatusText());
			}
			/**
			 * 保存请求日志
			 */
			bankLogMapper.insertSelective(bankLog);
			
		} catch (Exception e) {
			resultMap.put("resultCode", "exce");
			resultMap.put("resultMesg", "转账指令发送发生异常.");
			logger.error("转账指令发送发生异常."+e.getMessage());
			throw e;
		}
		logger.info("转账指令发送结束:"+resultMap);
		return resultMap;
	}
}
