package com.smmpay.payment.impl;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.smmpay.internal.service.IDatabaseEncrypt;
import com.smmpay.payment.UNFreezePayResultService;
import com.smmpay.process.CopyPaymentRecordProcess;
import com.smmpay.process.CreateTrRecordProcess;
import com.smmpay.process.CreateTransferProcess;
import com.smmpay.process.HandleTransferInstProcess;
import com.smmpay.process.JudgeTuikAccountProcess;
import com.smmpay.process.QueryTRStatusProcess;
import com.smmpay.process.StepProcessor;
import com.smmpay.process.UpdateMutualMoneyProcess;
import com.smmpay.process.UpdatePRStatus2Process;
import com.smmpay.process.UpdatePRStatusProcess;
import com.smmpay.process.UpdateUNTRStatusProcess;
import com.smmpay.process.enumDef.PayResult;
import com.smmpay.process.enumDef.PayType;
import com.smmpay.process.enumDef.PaymentLog;
import com.smmpay.process.enumDef.ReqType;
import com.smmpay.respository.dao.SBankLogMapper;
import com.smmpay.respository.dao.SCallClientLogMapper;
import com.smmpay.respository.dao.SPaymentLogMapper;
import com.smmpay.respository.dao.TrFreezeRecordMapper;
import com.smmpay.respository.dao.TrPaymentRecordMapper;
import com.smmpay.respository.dao.TrRecordMapper;
import com.smmpay.respository.dao.TrTransferRecordMapper;
import com.smmpay.respository.dao.TrUnfreezeRecordMapper;
import com.smmpay.respository.dao.UserAccountMapper;
import com.smmpay.respository.dao.UserPayChannelMapper;
import com.smmpay.respository.model.SCallClientLog;
import com.smmpay.respository.model.SPaymentLog;
import com.smmpay.respository.model.TrFreezeRecord;
import com.smmpay.respository.model.TrPaymentRecord;
import com.smmpay.respository.model.TrTransferRecord;
import com.smmpay.respository.model.TrUnfreezeRecord;
import com.smmpay.util.DateUtil;
import com.smmpay.util.SPaymentLogUtils;

/**
 * 
 * 轮询解冻指令结果
 * 
 * @author zengshihua
 * 
 */
@Service
public class UNFreezePayResultServiceImpl implements UNFreezePayResultService {
	
	private Logger logger=Logger.getLogger(UNFreezePayResultServiceImpl.class);
	
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
	private TrRecordMapper recordMapper;
	
	@Autowired
	private SBankLogMapper bankLogMapper;
	
	@Autowired
	private TrUnfreezeRecordMapper trUnfreezeRecordMapper;
	
	@Autowired
	private TrTransferRecordMapper transferRecordMapper;
	
	@Autowired
	private SPaymentLogMapper pLogMapper;
	
	@Autowired
	private IDatabaseEncrypt iDatabaseEncrypt;
	
	@Autowired
	private TrPaymentRecordMapper orderDao;
	
	@Autowired
	private UserAccountMapper userDao;
	
	/**
	 *有色网退款专用账户
	 */
	@Value("#{ysw['YSWTKZH']}")
	private String YSWTKZH;
	
	private StepProcessor frist;
	
	private StepProcessor next;
	
	private StepProcessor faileFrist;
	
	private StepProcessor faileNext;

	public synchronized void unfreezePayResult() throws Exception {
		/**
		 * 获取所有请求中的解冻记录
		 */
		List<TrUnfreezeRecord> frList = trUnfreezeRecordMapper.queryUnfreezeRecordAll(null);
		
		
		if (frList == null || frList.size() == 0) {
			
			return;
		}
		/**
		 * 开始处理
		 */
		logger.info("开始处理解冻结果.共需处理:"+frList.size());
		for (TrUnfreezeRecord trUnfreezeRecord : frList) {
			startHandle(trUnfreezeRecord);
		}

	}

	@Transactional(rollbackFor = Exception.class)
	public void startHandle(TrUnfreezeRecord trUnfreezeRecord) throws Exception {
		try {
			// 验证存储数据真实性
			if(!iDatabaseEncrypt.validUnFreezeVerifyCode(trUnfreezeRecord.getId())){
				logger.info("支付冻结结果，freezeId" +trUnfreezeRecord.getFreezeId()+"， verifyCode验证不通过");
				return;
			}
			
			Map<String, Object> param = new HashMap<String, Object>();
			param.put("clientID", trUnfreezeRecord.getUnfreezeClientId());
			param.put("YSWTKZH", YSWTKZH);
			param.put("type", ReqType.DLMDETRN.getCode());
			QueryTRStatusProcess fristProcess = QueryTRStatusProcess.getInstanceWithParam(param);//查询交易状态
			Map<String, Object> fristResultMap=fristProcess.process(null);
			//更新解冻记录状态“成功”,反馈时间
			TrFreezeRecord tfr=trFreezeRecordMapper.selectByPrimaryKey(trUnfreezeRecord.getFreezeId());//冻结记录
			
			String msg=fristResultMap.get("statusText")!=null?String.valueOf(fristResultMap.get("statusText")):"";
			
			if(fristResultMap.get("resultCode").equals("success")){
				
				trUnfreezeRecord.setUnfreezeStatus(1);
				trUnfreezeRecord.setUnfreezeReplyTime(DateUtil.doFormatDate(new Date(), "yyyy-MM-dd HH:mm:ss"));
				trUnfreezeRecord.setUnfreezeNote(fristResultMap.get("resultMesg")!=null?String.valueOf(fristResultMap.get("resultMesg")):"");//解冻备注
				UpdateUNTRStatusProcess untProcess=UpdateUNTRStatusProcess.getInstanceWithParam(trUnfreezeRecordMapper, trUnfreezeRecord);//更新解冻记录状态“成功”，记录反馈时间
				
				frist=untProcess;
				next=untProcess;
				
				TrPaymentRecord paymentRecord = new TrPaymentRecord();
				paymentRecord.setPaymentId(tfr.getPaymentId());
				paymentRecord.setPaymentStatus(2);
				paymentRecord.setDoneTime(DateUtil.doFormatDate(new Date(), "yyyy-MM-dd HH:mm:ss"));
				TrPaymentRecord pr=trPaymentRecordMapper.selectByPrimaryKey(tfr.getPaymentId());//支付记录
				
				next.next(UpdatePRStatus2Process.getInstanceWithParam(trPaymentRecordMapper, paymentRecord))//更新支付记录,状态“已完成”
					.next(CreateTrRecordProcess.getInstanceWithParam(recordMapper, pr,userAccountMapper))//生成买卖双方交易记录
					.next(UpdateMutualMoneyProcess.getInstanceWithParam(trPaymentRecordMapper,userAccountMapper,userPayChannelMapper,pr))//更新买卖双方账户金额、冻结金额
					.next(JudgeTuikAccountProcess.getInstanceWithParam(userPayChannelMapper, pr,param));//判断收款户是否为有色网退款账户
				
				logger.info("frist:"+frist);
				Map<String, Object> result = frist.process(null);
				logger.info("frist>>result:"+result);
				if(result.get("accountFlag").equals("Y")){
					//是 
					CopyPaymentRecordProcess cprProcess=CopyPaymentRecordProcess.getInstanceWithParam(trPaymentRecordMapper, pr, orderDao, userDao);//复制新支付记录，买方为有色网退款账户、卖方为解冻账户。支付状态“待退款”
					cprProcess.next(CreateTransferProcess.getInstanceWithParam(transferRecordMapper, pr))//生成转账记录
					.next(HandleTransferInstProcess.getInstanceWithParam(userPayChannelMapper, bankLogMapper,pr));//针对新的支付记录，执行“转账”指令
					Map<String, Object> result2 = cprProcess.process(null);//resultMap.put("resultCodeTransfer", "failure");
					logger.info("result2:"+result2);
					logger.info("transfer:"+result2.get("resultCodeTransfer"));
					if(result2.get("resultCodeTransfer") !=null && result2.get("resultCodeTransfer").toString().equals("failure")){
						String transferId = String.valueOf(result2.get("transferId"));
						TrTransferRecord record = transferRecordMapper.selectByPrimaryKey(Integer.parseInt(transferId));
						record.setStatus(2);
						record.setDesc(result2.get("resultMesg") == null?"":String.valueOf(result2.get("resultMesg")));
						transferRecordMapper.updateByPrimaryKeySelective(record);
						
						
						int pid = Integer.parseInt(String.valueOf(result2.get("newPaymentId")));
						TrPaymentRecord prd=trPaymentRecordMapper.selectByPrimaryKey(pid);//支付记录
						prd.setPaymentStatus(8);
						trPaymentRecordMapper.updateByPrimaryKeySelective(prd);
					}
					
					
				}else if(result.get("accountFlag").equals("N")){
					/**
					 * 生成商城结果呼叫日志记录。状态“未呼叫成功”
					 * 
					 */
					SCallClientLog ccl=new SCallClientLog();
					ccl.setCreateTime(DateUtil.doFormatDate(new Date(), "yyyy-MM-dd HH:mm:ss"));
					ccl.setPayType(Integer.valueOf(PayType.P1.getCode()));
					ccl.setPayResult(Integer.valueOf(PayResult.P0.getCode()));
					ccl.setPaymentRecordId(tfr.getPaymentId());
					ccl.setIsSuccess(0);
					ccl.setNotifyUrl(pr.getNotifyUrl());//商城呼叫地址
					Integer callLogCount=sCallClientLogMapper.insertSelective(ccl);
					logger.info("生成商城结果呼叫日志记录。callLogCount="+callLogCount);
					
					/**
					 * 步骤日志 ,第六步:检查解冻支付结果
					 */
					writeLog(msg,"成功",tfr.getPaymentId());
					
				}else{
					//不存在流程
					logger.info("不存在流程.");
				}
				
			}else if(fristResultMap.get("resultCode").equals("break")){
				
				/**
				 * 交易预约中
				 */
				logger.info("解冻交易状态还处于未知中，不作任何处理");
				
			}else{
				
				/**
				 * 交易失败
				 */
				trUnfreezeRecord.setUnfreezeStatus(2);
				trUnfreezeRecord.setUnfreezeReplyTime(DateUtil.doFormatDate(new Date(), "yyyy-MM-dd HH:mm:ss"));
				trUnfreezeRecord.setUnfreezeNote(fristResultMap.get("resultMesg")!=null?String.valueOf(fristResultMap.get("resultMesg")):"");//解冻备注
				UpdateUNTRStatusProcess faileProcess=UpdateUNTRStatusProcess.getInstanceWithParam(trUnfreezeRecordMapper, trUnfreezeRecord);//更新解冻记录状态“失败”，记录反馈时间/备注失败原因
				faileNext=faileProcess;
				faileFrist=faileProcess;
				faileNext.next(UpdatePRStatusProcess.getInstanceWithParam(trPaymentRecordMapper,sCallClientLogMapper, null,trUnfreezeRecord,"BH"));//更新支付记录，状态为“解冻支付失败”
				
				faileFrist.process(null);
				writeLog(msg,"失败",tfr.getPaymentId());
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
		}
	}
	
	/**
	 * 步骤7 解冻支付完成
	 * @param msg
	 * @param result
	 * @param paymentId
	 */
	public void writeLog (String msg,String result, Integer paymentId) {
		SPaymentLog pLog=new SPaymentLog();
		pLog.setPaymentId(paymentId);
		pLog.setOperationName(PaymentLog.P6.getMessage());
		pLog.setOpetationDesc(msg);
		pLog.setOperationResult(result);
		SPaymentLogUtils.write(pLog, pLogMapper);
	}
	
	
}
