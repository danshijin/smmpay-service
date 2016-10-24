package com.smmpay.payment.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.smmpay.internal.service.IDatabaseEncrypt;
import com.smmpay.payment.FreezePayResultService;
import com.smmpay.process.HandleFreezeRecordProcess;
import com.smmpay.process.QueryTRStatusProcess;
import com.smmpay.process.StepProcessor;
import com.smmpay.process.UpdatePRStatusProcess;
import com.smmpay.process.UpdateTRStatusProcess;
import com.smmpay.process.enumDef.PaymentLog;
import com.smmpay.process.enumDef.ReqType;
import com.smmpay.respository.dao.SBankLogMapper;
import com.smmpay.respository.dao.SCallClientLogMapper;
import com.smmpay.respository.dao.SPaymentLogMapper;
import com.smmpay.respository.dao.TrFreezeRecordMapper;
import com.smmpay.respository.dao.TrPaymentRecordMapper;
import com.smmpay.respository.dao.UserAccountMapper;
import com.smmpay.respository.dao.UserPayChannelMapper;
import com.smmpay.respository.model.SPaymentLog;
import com.smmpay.respository.model.TrFreezeRecord;
import com.smmpay.respository.model.UserPayChannel;
import com.smmpay.util.DateUtil;
import com.smmpay.util.SPaymentLogUtils;

/**
 * 轮询待支付冻结结果
 * 
 * @author zengshihua
 * 
 */
@Service
public class FreezePayResultServiceImpl implements FreezePayResultService {
	
	private Logger logger=Logger.getLogger(FreezePayResultServiceImpl.class);
	
	@Autowired
	private UserAccountMapper userAccountMapper;
	
	@Autowired
	private UserPayChannelMapper userPayChannelMapper;
	
	@Autowired
	private TrFreezeRecordMapper trFreezeRecordMapper;
	
	@Autowired
	private TrPaymentRecordMapper trPaymentRecordMapper;
	
	@Autowired
	private SCallClientLogMapper sCallClientLogMapper;
	@Autowired
	private SBankLogMapper bankLogMapper;
	
	@Autowired
	private SPaymentLogMapper pLogMapper;
	
	@Autowired
	private IDatabaseEncrypt iDatabaseEncrypt;
	
	private StepProcessor frist;
	
	private StepProcessor next;

	public synchronized void  freezePayResult() throws Exception {

		List<TrFreezeRecord> frList = trFreezeRecordMapper.queryFreezeRecordList(null);
		if (frList == null || frList.size() == 0) {
			return;
		}
		/**
		 * 开始处理
		 */
		logger.info("开始处理轮询待支付冻结结果.共需处理:"+frList.size());
		for (TrFreezeRecord trFreezeRecord : frList) {
			startHandle(trFreezeRecord);
		}

	}

	@Transactional(rollbackFor = Exception.class)
	public void startHandle(TrFreezeRecord trFreezeRecord) throws Exception {
		try {
			// 验证存储数据真实性
			if(!iDatabaseEncrypt.validFreezeVerifyCode(trFreezeRecord.getFreezeId())){
				logger.info("支付冻结结果，freezeId" +trFreezeRecord.getFreezeId()+"， verifyCode验证不通过");
				return;
			}
			
			Map<String, Object> param = new HashMap<String, Object>();
			param.put("clientID", trFreezeRecord.getFreezeClientId());
			param.put("type", ReqType.DLMDETRN.getCode());
			param.put("job", "Y");//在查询时睡眠标记
			QueryTRStatusProcess fristProcess = QueryTRStatusProcess.getInstanceWithParam(param);//查询交易状态
			Map<String, Object> fristResultMap=fristProcess.process(null);
			
			logger.info("查询交易状态fristResultMap:"+fristResultMap.get("resultCode"));
			
			if(fristResultMap.get("resultCode").equals("success")){
				/**
				 * 交易成功
				 */
				HandleFreezeRecordProcess succProcess=HandleFreezeRecordProcess.getInstanceWithParam(bulidQueyParam(trFreezeRecord),trFreezeRecordMapper,
						trPaymentRecordMapper,userAccountMapper,userPayChannelMapper,sCallClientLogMapper,trFreezeRecord,bankLogMapper);
				
				Map<String, Object> resultMap=succProcess.process(null);
				if (resultMap != null && resultMap.get("resultCode").equals("success")) {
					//
					if(resultMap.containsKey("freezeNoFlag")&&
							resultMap.get("freezeNoFlag")!=null){
						String msg=resultMap.get("freezeNoFlag")!=null?"冻结编号"+resultMap.get("freezeNoFlag"):"";
						writeLog(msg,"成功",trFreezeRecord.getPaymentId());
					}
					
				}
			}else if(fristResultMap.get("resultCode").equals("break")){
				logger.info("交易状态还处于未知中，不作任何处理");
			}else{
				
				/**
				 * 交易失败
				 */
				trFreezeRecord.setFreezeNote(fristResultMap.get("resultMesg")!=null?String.valueOf(fristResultMap.get("resultMesg")):"");
				trFreezeRecord.setReplyTime(DateUtil.getFormatDate());
				UpdateTRStatusProcess faileProcess=UpdateTRStatusProcess.getInstanceWithParam(trFreezeRecordMapper, trFreezeRecord);//修改冻结记录状态
				
				frist=faileProcess;
				next=faileProcess;
				
				next.next(UpdatePRStatusProcess.getInstanceWithParam(trPaymentRecordMapper,sCallClientLogMapper, trFreezeRecord,null,"BR"));//修改交易支付记录状态
				
				Map<String, Object> result=frist.process(null);
				
				
				writeLog("冻结失败","失败",trFreezeRecord.getPaymentId());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	/**
	 * 根据userId取得付款方账号
	 * 作为第二步操作的入参
	 * @param paramMap
	 * @return
	 */
	protected Map<String, Object> bulidQueyParam(TrFreezeRecord trFreezeRecord) {

		Map<String, Object> paramMap=new HashMap<String, Object>();
		paramMap.put("userId", trFreezeRecord.getUserId());
		paramMap.put("paymentId", trFreezeRecord.getPaymentId());
		paramMap.put("job", "Y");//在查询时睡眠标记
		
		List<UserPayChannel> userPayChannelList=userPayChannelMapper.queryPayChannelByUserId(paramMap);
		if(userPayChannelList!=null&&userPayChannelList.size()>0){
			/**
			 * 现在只针对中信支付通道做处理
			 */
			paramMap.put("payChannelId", userPayChannelList.get(0).getPayChannelId());
			paramMap.put("subAccNo", userPayChannelList.get(0).getUserAccountNo());
			paramMap.put("subAccNm", userPayChannelList.get(0).getUserAccountName());
			paramMap.put("userPayChannelId", userPayChannelList.get(0).getUserPayChannelId());
			
		}
		return paramMap;
	}
	
	/**
	 * 步骤4、买方冻结结果
	 * @param msg
	 * @param result
	 * @param paymentId
	 */
	public void writeLog (String msg,String result, Integer paymentId) {
		SPaymentLog pLog=new SPaymentLog();
		pLog.setPaymentId(paymentId);
		pLog.setOperationName(PaymentLog.P3.getMessage());
		pLog.setOpetationDesc(msg);
		pLog.setOperationResult(result);
		SPaymentLogUtils.write(pLog, pLogMapper);
	}
	
}
