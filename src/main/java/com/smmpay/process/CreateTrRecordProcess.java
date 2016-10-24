package com.smmpay.process;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import com.smmpay.respository.dao.TrRecordMapper;
import com.smmpay.respository.dao.UserAccountMapper;
import com.smmpay.respository.model.TrPaymentRecord;
import com.smmpay.respository.model.TrRecord;
import com.smmpay.respository.model.UserAccount;
import com.smmpay.util.DateUtil;

/**
 * 
 * 生成买卖双方交易记录
 * 
 * @author zengshihua
 * 
 */
public class CreateTrRecordProcess extends StepProcessor {

	private Logger logger = Logger.getLogger(CreateTrRecordProcess.class);


	private TrPaymentRecord paymentRecord;

	private TrRecordMapper recordMapper;
	
	private UserAccountMapper userAccountMapper;


	protected CreateTrRecordProcess() {
		super();
	}

	protected CreateTrRecordProcess(Map<String, Object> paramMap) {
		super(paramMap);
	}

	protected CreateTrRecordProcess(Map<String, Object> paramMap,
			StepProcessor nextProcessor) {
		super(paramMap, nextProcessor);
	}

	public static CreateTrRecordProcess getInstanceWithParam(
			TrRecordMapper recordMapper,
			TrPaymentRecord paymentRecord,
			UserAccountMapper userAccountMapper) {

		Map<String, Object> paramMap = buildParam(null);

		CreateTrRecordProcess process = new CreateTrRecordProcess(paramMap);
		process.recordMapper = recordMapper;
		process.paymentRecord = paymentRecord;
		process.userAccountMapper=userAccountMapper;
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
	@SuppressWarnings("finally")
	@Override
	protected Map<String, Object> action(Map<String, Object> resultParamMap) throws Exception {

		Map<String, Object> resultMap = new HashMap<String, Object>();
		try {
			String applyTime = DateUtil.doFormatDate(new Date(),
					"yyyy-MM-dd HH:mm:ss");
			
			UserAccount buyUserAccount = userAccountMapper.selectByPrimaryKey(Integer.valueOf(paymentRecord.getBuyerUserId()));
			UserAccount sellerUserAccount = userAccountMapper.selectByPrimaryKey(Integer.valueOf(paymentRecord.getSellerUserId()));
			
			// 买方记录
			TrRecord buy = new TrRecord();
			buy.setTrMoney(paymentRecord.getDealMoney());
			buy.setTrType(1);
			buy.setTrApplyTime(applyTime);
			buy.setTrApplyStatus(1);
			buy.setTrCharge(new BigDecimal(0.00));// 交易手续费
			buy.setUserId(Integer.valueOf(paymentRecord.getBuyerUserId()));
			buy.setUserPayChannelId(paymentRecord.getBuyerPayChannelId());
			buy.setUserCompanyName(paymentRecord.getBuyerCompanyName());
			buy.setOppositUserId(Integer.valueOf(paymentRecord.getSellerUserId()));
			buy.setOppositCompanyName(paymentRecord.getSellerCompanyName());
			buy.setOppositUserPayChannelId(paymentRecord.getSellerPayChannelId());
			buy.setPayChannelId(paymentRecord.getPayChannelId());
			buy.setUserMoney(buyUserAccount.getUseMoney());//本交易执行后用户账户可用余额
			buy.setNote("购买"+paymentRecord.getProductName());
			logger.info("卖方"+paymentRecord.getSellerUserId());
			logger.info("卖方"+sellerUserAccount.getCompanyName());
			logger.info("卖方"+sellerUserAccount.getUseMoney());
			buy.setUserFreezeMoney(buyUserAccount.getFreezeMoney().subtract(paymentRecord.getDealMoney()));//本交易执行成功后用户账户冻结金额
			// buy.setNote(note);
			// 卖方记录
			TrRecord opposit = new TrRecord();
			opposit.setTrMoney(paymentRecord.getDealMoney());
			opposit.setTrType(0);
			opposit.setTrApplyTime(applyTime);
			opposit.setTrApplyStatus(1);
			opposit.setTrCharge(new BigDecimal(0.00));
			opposit.setUserId(Integer.valueOf(paymentRecord.getSellerUserId()));
			opposit.setUserPayChannelId(paymentRecord.getSellerPayChannelId());
			opposit.setUserCompanyName(paymentRecord.getSellerCompanyName());
			opposit.setOppositUserId(Integer.valueOf(paymentRecord.getBuyerUserId()));
			opposit.setOppositCompanyName(paymentRecord.getBuyerCompanyName());
			opposit.setOppositUserPayChannelId(paymentRecord.getBuyerPayChannelId());
			opposit.setPayChannelId(paymentRecord.getPayChannelId());
			opposit.setNote("出售"+paymentRecord.getProductName());
			logger.error("卖方useMoney"+sellerUserAccount.getUseMoney().add(paymentRecord.getDealMoney()));
			opposit.setUserMoney(sellerUserAccount.getUseMoney().add(paymentRecord.getDealMoney()));//本交易执行后用户账户可用余额
			opposit.setUserFreezeMoney(sellerUserAccount.getFreezeMoney());//本交易执行成功后用户账户冻结金额
			
			Integer buyCount = recordMapper.insertSelective(buy);
			Integer oppCount = recordMapper.insertSelective(opposit);
			
			if (buyCount > 0&&oppCount>0) {
				
				resultMap.put("resultCode", "success");
				resultMap.put("resultMesg", "成功");
				
			} else {
				resultMap.put("resultCode", "exce");
				resultMap.put("resultMesg", "生成买卖方交易记录失败.");
				throw new Exception();
			}
		} catch (Exception e) {
			//e.printStackTrace();
			resultMap.put("resultCode", "exce");
			resultMap.put("resultMesg", "生成买卖方交易记录失败.");
			e.printStackTrace();
			logger.info("生成买卖双方交易记录发生异常."+e.getMessage());
			logger.error("生成买卖双方交易记录发生异常."+e.getStackTrace());
			throw e;
		}
		logger.info("生成买卖双方交易记录,出参:"+resultMap);
		return resultMap;
	}
}
