package com.smmpay.process;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.log4j.Logger;

import com.smmpay.respository.dao.TrPaymentRecordMapper;
import com.smmpay.respository.dao.UserAccountMapper;
import com.smmpay.respository.model.TrPaymentRecord;
import com.smmpay.respository.model.UserAccount;

/**
 * 
 * 复制新支付记录，买方为有色网退款账户、卖方为解冻账户。支付状态“待退款”
 * 
 * @author zengshihua
 * 
 */
public class CopyPaymentRecordProcess extends StepProcessor {

	private Logger logger = Logger.getLogger(CopyPaymentRecordProcess.class);

	private TrPaymentRecordMapper paymentRecordMapper;

	private TrPaymentRecord paymentRecord;

	private TrPaymentRecordMapper orderDao;
	
	private UserAccountMapper userDao;


	protected CopyPaymentRecordProcess() {
		super();
	}

	protected CopyPaymentRecordProcess(Map<String, Object> paramMap) {
		super(paramMap);
	}

	protected CopyPaymentRecordProcess(Map<String, Object> paramMap,
			StepProcessor nextProcessor) {
		super(paramMap, nextProcessor);
	}

	public static CopyPaymentRecordProcess getInstanceWithParam(TrPaymentRecordMapper paymentRecordMapper,
			TrPaymentRecord paymentRecord, TrPaymentRecordMapper orderDao, UserAccountMapper userDao) {

		Map<String, Object> paramMap = buildParam(null);

		CopyPaymentRecordProcess process = new CopyPaymentRecordProcess(paramMap);
		process.paymentRecordMapper=paymentRecordMapper;
		process.paymentRecord = paymentRecord;
		process.orderDao = orderDao;
		process.userDao = userDao;
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
			TrPaymentRecord newPaymentRecord=new TrPaymentRecord();
			
			BeanUtils.copyProperties(newPaymentRecord, paymentRecord);
			newPaymentRecord.setPaymentStatus(11);
			newPaymentRecord.setBuyerCompanyName(paymentRecord.getSellerCompanyName());
			newPaymentRecord.setBuyerMallUserName(paymentRecord.getSellerMallUserName());
			newPaymentRecord.setBuyerPayChannelId(paymentRecord.getSellerPayChannelId());
			newPaymentRecord.setBuyerUserId(paymentRecord.getSellerUserId());
			newPaymentRecord.setSellerCompanyName(paymentRecord.getBuyerCompanyName());
			newPaymentRecord.setSellerMallUserName(paymentRecord.getBuyerMallUserName());
			newPaymentRecord.setSellerPayChannelId(paymentRecord.getBuyerPayChannelId());
			newPaymentRecord.setSellerUserId(paymentRecord.getBuyerUserId());
			newPaymentRecord.setPaymentId(null);
			newPaymentRecord.setPaymentNo(getPaymentNoFromStoredProcedure(newPaymentRecord));
			logger.info("复制支付记录入参:"+newPaymentRecord.toString());
			Integer pCount=paymentRecordMapper.insert(newPaymentRecord);
			if(pCount>0){
				resultMap.put("resultCode", "success");
				resultMap.put("newPaymentId", newPaymentRecord.getPaymentId());
				resultMap.put("resultMesg", "复制成功");
			}else{
				resultMap.put("resultCode", "exce");
				resultMap.put("resultMesg", "复制失败");
			}
			
			
		} catch (Exception e) {
			resultMap.put("resultCode", "exce");
			resultMap.put("resultMesg", "复制失败");
			e.printStackTrace();
			logger.info("复制失败支付记录发生异常."+e.getMessage());
			throw e;
		}
		return resultMap;
	}
	
	private  String getPaymentNoFromStoredProcedure(TrPaymentRecord newPR){
		Map<String,Integer> mapParam = new HashMap<String,Integer>();
		// 获取买家公司信息
		UserAccount buyer = new UserAccount();
		buyer.setMallUserName(newPR.getBuyerMallUserName());
		buyer = userDao.findAccountByMallUserName(buyer);
		logger.info("买家："+buyer.getUserId());
		// 增加退补款判断
		mapParam.put("codeType", 0);
		mapParam.put("userId", buyer.getUserId());
		mapParam.put("source", 0);
		String paymentNo = orderDao.getPaymentCode(mapParam);
		logger.info("paymentNo:"+paymentNo);
		if(paymentNo ==null || "".equals(paymentNo)){
			paymentNo = null;
		}
		return paymentNo;
	}
}
