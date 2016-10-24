package com.smmpay.process;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import com.smmpay.respository.dao.TrTransferRecordMapper;
import com.smmpay.respository.model.TrPaymentRecord;
import com.smmpay.respository.model.TrTransferRecord;
import com.smmpay.util.Base64;
import com.smmpay.util.ClientIdUtils;
import com.smmpay.util.DateUtil;
import com.smmpay.util.DesCrypt;

/**
 * 
 * 生成转账记录，状态“请求中”
 * 
 * @author zengshihua
 * 
 */
public class CreateTransferProcess extends StepProcessor {

	private Logger logger = Logger.getLogger(CreateTransferProcess.class);

	private TrTransferRecordMapper transferRecordMapper;
	
	private TrPaymentRecord paymentRecord;


	protected CreateTransferProcess() {
		super();
	}

	protected CreateTransferProcess(Map<String, Object> paramMap) {
		super(paramMap);
	}

	protected CreateTransferProcess(Map<String, Object> paramMap,
			StepProcessor nextProcessor) {
		super(paramMap, nextProcessor);
	}

	public static CreateTransferProcess getInstanceWithParam(
			TrTransferRecordMapper transferRecordMapper,
			TrPaymentRecord paymentRecord) {

		Map<String, Object> paramMap = buildParam(null);

		CreateTransferProcess process = new CreateTransferProcess(paramMap);
		process.transferRecordMapper = transferRecordMapper;
		process.paymentRecord = paymentRecord;
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
			String applyTime = DateUtil.doFormatDate(new Date(),"yyyy-MM-dd HH:mm:ss");
			
			String clientID=ClientIdUtils.CreateClientId();
			TrTransferRecord transferRecord = new TrTransferRecord();
			transferRecord.setClientId(clientID);
			transferRecord.setPaymentId(Integer.parseInt(String.valueOf(resultParamMap.get("newPaymentId"))));
			transferRecord.setPayChannelId(paymentRecord.getPayChannelId());
			transferRecord.setOutUserId(Integer.valueOf(paymentRecord.getSellerUserId()));
			transferRecord.setOutUserChannelId(paymentRecord.getSellerPayChannelId());
			transferRecord.setOutUserCompanyName(paymentRecord.getSellerCompanyName());
			transferRecord.setInUserChannelId(paymentRecord.getBuyerPayChannelId());
			transferRecord.setInUserCompanyName(paymentRecord.getBuyerCompanyName());
			transferRecord.setInUserId(Integer.valueOf(paymentRecord.getBuyerUserId()));
			transferRecord.setApplyTime(applyTime);
			transferRecord.setStatus(0);
			logger.info("msg"+paymentRecord.getPaymentId()+" "+paymentRecord.getPayChannelId()+" "+paymentRecord.getSellerUserId()+
					" "+paymentRecord.getSellerPayChannelId()+" "+paymentRecord.getSellerCompanyName()+" "+paymentRecord.getBuyerPayChannelId()
					+" "+paymentRecord.getBuyerCompanyName()+" "+paymentRecord.getBuyerUserId());
			logger.info("生成转账记录入参:"+transferRecord.toString());
			logger.info("生成转账记录入参:"+transferRecord.toString());

			String columns = clientID + String.valueOf(resultParamMap.get("newPaymentId")) + paymentRecord.getSellerUserId() + paymentRecord.getBuyerUserId() + applyTime;
			byte[] desColumns = DesCrypt.encryptMode(columns.getBytes("utf-8"));
			String columns_str = Base64.getBase64(desColumns);
			transferRecord.setVerifyCode(columns_str);
			
			Integer count=transferRecordMapper.insertSelective(transferRecord);
			
			if (count > 0) {
				resultMap.put("resultCode", "success");
				resultMap.put("resultMesg", "生成转账记录成功.");
				resultMap.put("transferId", transferRecord.getId());
			} else {
				resultMap.put("resultCode", "exce");
				resultMap.put("resultMesg", "生成转账记录失败.");
			}
			resultMap.put("clientID", clientID);
			resultMap.put("payChannelId", paymentRecord.getPayChannelId());
			
		} catch (Exception e) {
			e.printStackTrace();
			logger.info("生成转账记录发生异常."+e.getMessage());
			throw e;
		}
		return resultMap;
	}
}
