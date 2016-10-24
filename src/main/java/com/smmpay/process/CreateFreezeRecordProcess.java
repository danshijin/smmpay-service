package com.smmpay.process;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import com.smmpay.internal.service.IDatabaseEncrypt;
import com.smmpay.respository.dao.TrFreezeRecordMapper;
import com.smmpay.respository.model.TrFreezeRecord;
import com.smmpay.respository.model.TrPaymentRecord;
import com.smmpay.util.ClientIdUtils;
import com.smmpay.util.DateUtil;
/**
 * 
 * 生成新的资金冻结记录
 * 
 * @author zengshihua
 *
 */
public class CreateFreezeRecordProcess extends StepProcessor {

	
	private Logger logger=Logger.getLogger(CreateFreezeRecordProcess.class);
	
	private TrFreezeRecordMapper freezeRecordMapper;
	
	private TrPaymentRecord paymentRecord;
	
    private IDatabaseEncrypt iDatabaseEncrypt;
	
	protected CreateFreezeRecordProcess() {
		super();
	}

	protected CreateFreezeRecordProcess(Map<String, Object> paramMap) {
		super(paramMap);
	}

	protected CreateFreezeRecordProcess(Map<String, Object> paramMap,
			StepProcessor nextProcessor) {
		super(paramMap, nextProcessor);
	}

	public static CreateFreezeRecordProcess getInstanceWithParam(TrFreezeRecordMapper freezeRecordMapper,
			TrPaymentRecord paymentRecord, IDatabaseEncrypt iDatabaseEncrypt) {

		Map<String, Object> paramMap = buildParam(null);
		
		CreateFreezeRecordProcess process = new CreateFreezeRecordProcess(paramMap);
		process.freezeRecordMapper=freezeRecordMapper;
		process.paymentRecord=paymentRecord;
		process.iDatabaseEncrypt=iDatabaseEncrypt;
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
	 */
	@Override
	protected Map<String, Object> action(Map<String, Object> resultParamMap) {
		
		Map<String, Object> resultMap = new HashMap<String, Object>();
		try {
			/**
			 * 生成clientID
			 */
			String clientID=ClientIdUtils.CreateClientId();
			TrFreezeRecord fr = new TrFreezeRecord();
			fr.setFreezeMoney(paymentRecord.getDealMoney());
			fr.setApplyTime(DateUtil.doFormatDate(new Date(), "yyyy-MM-dd HH:mm:ss"));
			fr.setFreezeStatus(0);
			fr.setFreezeNote("手动支付冻结");
			fr.setUserId(Integer.valueOf(paymentRecord.getBuyerUserId()));
			fr.setUserName(paymentRecord.getBuyerCompanyName());
			fr.setPayChannelId(paymentRecord.getPayChannelId());
			fr.setUserPayChannelId(paymentRecord.getBuyerPayChannelId());
			fr.setFreezeClientId(clientID);
			fr.setPaymentId(paymentRecord.getPaymentId());
			
			Integer insertResult=freezeRecordMapper.insertSelective(fr);
			if(insertResult>0){
				// 添加验证字段
				logger.info("冻结记录表，手动冻结添加verifyCode");
				iDatabaseEncrypt.addFreezeVerifyCode(fr.getFreezeId());
				
				resultMap.put("resultCode", "success");
				resultMap.put("clientID", clientID);
				resultMap.put("resultMesg", "生成新的冻结成功");
				logger.info("生成新的冻结成功"+insertResult);
			}else{
				resultMap.put("resultCode", "exce");
				resultMap.put("resultMesg", "生成新的冻结记录失败");
				logger.error("生成新的冻结记录失败"+insertResult);
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
		}
		
		return resultMap;
	}
	
	
}
