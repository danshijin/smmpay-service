package com.smmpay.process;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import com.smmpay.respository.dao.TrPaymentRecordMapper;
import com.smmpay.respository.model.TrPaymentRecord;

/**
 * 
 * 修改支付记录状态
 * 
 * @author zengshihua
 * 
 */
public class UpdatePRStatus2Process extends StepProcessor {

	private Logger logger = Logger.getLogger(UpdatePRStatus2Process.class);

	private TrPaymentRecordMapper paymentRecordMapper;
	private TrPaymentRecord paymentRecord;

	protected UpdatePRStatus2Process() {
		super();
	}

	protected UpdatePRStatus2Process(Map<String, Object> paramMap) {
		super(paramMap);
	}

	protected UpdatePRStatus2Process(Map<String, Object> paramMap,
			StepProcessor nextProcessor) {
		super(paramMap, nextProcessor);
	}

	public static UpdatePRStatus2Process getInstanceWithParam(
			TrPaymentRecordMapper paymentRecordMapper,
			TrPaymentRecord paymentRecord) {

		// Map<String, Object> paramMap = buildParam(null);
		UpdatePRStatus2Process process = new UpdatePRStatus2Process(null);
		process.paymentRecordMapper = paymentRecordMapper;
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
	 * 组装参数返回
	 */
	@Override
	protected Map<String, Object> action(Map<String, Object> resultParamMap) {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		Integer uPCount = paymentRecordMapper.updatePaymentStatus(paymentRecord);
		if (uPCount > 0) {
			resultMap.put("resultCode", "success");
			resultMap.put("resultMesg", "修改支付记录状态成功.");
			logger.info("修改支付记录状态成功.");
		} else {
			resultMap.put("resultCode", "exce");
			resultMap.put("resultMesg", "修改支付记录状态失败.");
			logger.info("修改支付记录状态失败.");
		}
		return resultMap;
	}

}
