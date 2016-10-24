package com.smmpay.process;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import com.smmpay.process.enumDef.PayResult;
import com.smmpay.process.enumDef.PayType;
import com.smmpay.respository.dao.SCallClientLogMapper;
import com.smmpay.respository.dao.TrFreezeRecordMapper;
import com.smmpay.respository.dao.TrPaymentRecordMapper;
import com.smmpay.respository.model.SCallClientLog;
import com.smmpay.respository.model.TrFreezeRecord;
import com.smmpay.respository.model.TrPaymentRecord;
import com.smmpay.respository.model.TrUnfreezeRecord;
import com.smmpay.util.DateUtil;

/**
 * 
 * 修改支付记录状态
 * 
 * @author zengshihua
 * 
 */
public class UpdatePRStatusProcess extends StepProcessor {

	private Logger logger = Logger.getLogger(UpdatePRStatusProcess.class);

	private TrPaymentRecordMapper paymentRecordMapper;
	private SCallClientLogMapper sCallClientLogMapper;
	private TrFreezeRecord trFreezeRecord;
	private TrUnfreezeRecord trUnfreezeRecord;
	private TrFreezeRecordMapper trFreezeRecordMapper;
	private String type;

	protected UpdatePRStatusProcess() {
		super();
	}

	protected UpdatePRStatusProcess(Map<String, Object> paramMap) {
		super(paramMap);
	}

	protected UpdatePRStatusProcess(Map<String, Object> paramMap,
			StepProcessor nextProcessor) {
		super(paramMap, nextProcessor);
	}

	public static UpdatePRStatusProcess getInstanceWithParam(
			TrPaymentRecordMapper paymentRecordMapper,
			SCallClientLogMapper sCallClientLogMapper,
			TrFreezeRecord trFreezeRecord,
			TrUnfreezeRecord trUnfreezeRecord,
			String type) {

		// Map<String, Object> paramMap = buildParam(null);

		UpdatePRStatusProcess process = new UpdatePRStatusProcess(null);
		process.paymentRecordMapper = paymentRecordMapper;
		process.trFreezeRecord = trFreezeRecord;
		process.trUnfreezeRecord = trUnfreezeRecord;
		process.sCallClientLogMapper=sCallClientLogMapper;
		process.type = type;
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
		resultMap.put("resultCode", "exce");
		resultMap.put("resultMesg", "失败");
		
		logger.error("操作类型:" + type);
		
		try {

			TrPaymentRecord pr = new TrPaymentRecord();
			

			if (type.equals("BR")) {
				pr.setPaymentId(trFreezeRecord.getPaymentId());
				pr.setPaymentStatus(4);// 冻结支付失败
				Integer count = payBR(pr);
				if (count > 0) {
					resultMap.put("resultCode", "success");
					resultMap.put("resultMesg", "成功");
				} else {
					throw new Exception();
				}

			} else if (type.equals("BH")) {
				//解冻失败
				TrFreezeRecord tfr=trFreezeRecordMapper.selectByPrimaryKey(trUnfreezeRecord.getFreezeId());
				pr.setPaymentId(tfr.getPaymentId());
				logger.error("支付记录ID." + tfr.getPaymentId());
				payBH(pr, trUnfreezeRecord);
			} else {

			}

		} catch (Exception e) {
			
			resultMap.put("resultCode", "exce");
			resultMap.put("resultMesg", "修改支付记录状态发生异常.");
			logger.error("修改支付记录状态生异常." + e.getMessage());
		}

		logger.info("结束修改支付记录状态：出参" + resultMap);
		return resultMap;
	}

	public Integer payBR(TrPaymentRecord pr) {

		Integer count = paymentRecordMapper.updatePaymentStatus(pr);
		if (count > 0) {

			/**
			 * 生成商城结果呼叫日志记录。状态“未呼叫成功”
			 * 
			 */
			SCallClientLog ccl = new SCallClientLog();
			ccl.setCallTime(DateUtil.doFormatDate(new Date(),"yyyy-MM-dd HH:mm:ss"));
			ccl.setPayType(Integer.valueOf(PayType.P0.getCode()));
			ccl.setPayResult(Integer.valueOf(PayResult.P1.getCode()));
			ccl.setPaymentRecordId(trFreezeRecord.getPaymentId());
			ccl.setIsSuccess(0);
			sCallClientLogMapper.insertSelective(ccl);

			return count;

		} else {

			return -1;
		}
	}
	
	public Integer payBH(TrPaymentRecord pr, TrUnfreezeRecord trUnfreezeRecord) {

		logger.info("解冻支付-失败-生成呼叫商城日志.");
		pr.setPaymentStatus(5);
		Integer count = paymentRecordMapper.updatePaymentStatus(pr);
		/**
		 * 生成商城结果呼叫日志记录。状态“未呼叫成功”
		 * 
		 */
		SCallClientLog ccl = new SCallClientLog();
		ccl.setCallTime(DateUtil.doFormatDate(new Date(), "yyyy-MM-dd HH:mm:ss"));
		ccl.setPayType(Integer.valueOf(PayType.P1.getCode()));
		ccl.setPayResult(Integer.valueOf(PayResult.P1.getCode()));
		ccl.setPaymentRecordId(trUnfreezeRecord.getFreezeId());//冻结记录编号
		ccl.setIsSuccess(0);
		sCallClientLogMapper.insertSelective(ccl);
		
		return count;
	}
}
