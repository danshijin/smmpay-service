package com.smmpay.process;

import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
import com.smmpay.respository.dao.TrUnfreezeRecordMapper;
import com.smmpay.respository.dao.UserPayChannelMapper;
import com.smmpay.respository.model.ReqDlmdetrn;
import com.smmpay.respository.model.SBankLog;
import com.smmpay.respository.model.SPaymentLog;
import com.smmpay.respository.model.TrFreezeRecord;
import com.smmpay.respository.model.TrPaymentRecord;
import com.smmpay.respository.model.TrUnfreezeRecord;
import com.smmpay.respository.model.UserPayChannel;
import com.smmpay.respository.model.base.ResCommon;
import com.smmpay.util.CPayUtils;
import com.smmpay.util.ClientIdUtils;
import com.smmpay.util.DatabaseEncryptUtil;
import com.smmpay.util.DateUtil;
import com.smmpay.util.SPaymentLogUtils;
import com.smmpay.util.XMLUtils;
/**
 * 
 * 首先获取支付记录的最后一条冻结记录
 * 
 * 然后发送冻结指令
 * 
 */
public class ThawPayProcess extends StepProcessor {

	private TrFreezeRecordMapper freezeRecordMapper;
	
	private TrPaymentRecord paymentRecord;
	
	private UserPayChannelMapper userPayChannelMapper;
	
	private TrUnfreezeRecordMapper trUnfreezeRecordMapper;
	
	private TrPaymentRecordMapper paymentRecordMapper;
	
	private SBankLogMapper bankLogMapper;
	
	private SPaymentLogMapper pLogMapper;
	
	private IDatabaseEncrypt iDatabaseEncrypt;
	
	private Logger logger=Logger.getLogger(ThawPayProcess.class);
	
	protected ThawPayProcess() {
		super();
	}

	protected ThawPayProcess(Map<String, Object> paramMap) {
		super(paramMap);
	}

	protected ThawPayProcess(Map<String, Object> paramMap,
			StepProcessor nextProcessor) {
		super(paramMap, nextProcessor);
	}

	public static ThawPayProcess getInstanceWithParam(Map<String, Object> queryParam,
			TrFreezeRecordMapper freezeRecordMapper,
			TrPaymentRecord paymentRecord,
			UserPayChannelMapper userPayChannelMapper,
			TrUnfreezeRecordMapper trUnfreezeRecordMapper,
			TrPaymentRecordMapper paymentRecordMapper,
			SBankLogMapper bankLogMapper,
			SPaymentLogMapper pLogMapper,
			IDatabaseEncrypt iDatabaseEncrypt) {

		Map<String, Object> paramMap = buildParam(queryParam);
		
		ThawPayProcess process = new ThawPayProcess(paramMap);
		process.freezeRecordMapper=freezeRecordMapper;
		process.paymentRecord=paymentRecord;
		process.userPayChannelMapper=userPayChannelMapper;
		process.trUnfreezeRecordMapper=trUnfreezeRecordMapper;
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
		if(!param.isEmpty()){
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
		String msg="",result="失败";
		SPaymentLog pLog=new SPaymentLog();
		pLog.setPaymentId(paymentRecord.getPaymentId());
		pLog.setOperationName(PaymentLog.P5.getMessage());
		
		try {
			logger.info("开始获取支付记录的最后一条冻结记录,入参："+resultParamMap);
			
			
			List<TrFreezeRecord> fRecordList = freezeRecordMapper.lastFreezeRecord(resultParamMap);
			if(fRecordList!=null&&fRecordList.size()>0){
				
				String clientID=ClientIdUtils.CreateClientId();
				/**
				 * 解冻支付参数
				 */
				TrFreezeRecord freezeRecord = fRecordList.get(0);
				
				UserPayChannel buyerPayChanne=userPayChannelMapper.selectByPrimaryKey(paymentRecord.getBuyerPayChannelId());//买方/付款方
				UserPayChannel sellerPayChanne=userPayChannelMapper.selectByPrimaryKey(paymentRecord.getSellerPayChannelId());//卖方/收款方
				
				/**
				 * 生成解冻记录
				 * 
				 */
				TrUnfreezeRecord unFRecord = new TrUnfreezeRecord();
				unFRecord.setFreezeId(freezeRecord.getFreezeId());
				unFRecord.setUnfreezeApplyTime(DateUtil.doFormatDate(new Date(),"yyyy-MM-dd HH:mm:ss"));
				unFRecord.setUnfreezeStatus(0);
				unFRecord.setRecevieUserId(freezeRecord.getUserId());
				unFRecord.setRecevieUserName(sellerPayChanne.getUserAccountName());
				unFRecord.setRecevieUserPayChannelId(paymentRecord.getSellerPayChannelId());
				unFRecord.setPayChannelId(paymentRecord.getPayChannelId());
				unFRecord.setUnfreezeClientId(clientID);
				Integer unFCount=trUnfreezeRecordMapper.insertSelective(unFRecord); // 更改后， unFCount 代表插入后的主键id
				iDatabaseEncrypt.addUnFreezeVerifyCode(unFRecord.getId());
				/**
				 * 更新支付记录状态“付款中”
				 */
				TrPaymentRecord payRec= new TrPaymentRecord(); 
				payRec.setPaymentStatus(Integer.valueOf(PaymentSatus.PS10.getCode()));//付款中
				payRec.setPaymentId(paymentRecord.getPaymentId());
				Integer psCount = paymentRecordMapper.updatePaymentStatus(payRec);
				logger.info("更新支付记录状态 付款中  psCount:"+psCount);
				
				if(unFCount>0&&psCount>0){
					/**
					 * 请求中信参数
					 */
					ReqDlmdetrn reqDl=new ReqDlmdetrn();
					reqDl.setClientID(clientID);
					reqDl.setPayAccNo(buyerPayChanne.getUserAccountNo());//付款账号
					reqDl.setTranType(TranType.BH.getCode());
					reqDl.setRecvAccNo(sellerPayChanne.getUserAccountNo());//收款账号
					reqDl.setRecvAccNm(sellerPayChanne.getUserAccountName());//收款账户名称
					reqDl.setTranAmt(freezeRecord.getFreezeMoney().setScale(2, RoundingMode.HALF_UP));//交易金额
					reqDl.setFreezeNo(freezeRecord.getFreezeNo());
					reqDl.setMemo(buyerPayChanne.getUserAccountNo()+">>>>"+sellerPayChanne.getUserAccountNo());//摘要
					reqDl.setTranFlag("0");//转账时效标识
					
					/**
					 * 请求中信强制转账-解冻支付BH接口
					 */
					String sendXML=XMLUtils.beanToXML(reqDl);
					
					logger.info("请求中信强制转账-解冻支付BH.入参:"+sendXML);
					String resultXML=CPayUtils.postRequest(sendXML);
					/**
					 * 记录日志
					 */
					
					SBankLog bankLog=new SBankLog();
					bankLog.setRequestParam(sendXML);
					bankLog.setRequestInterface("信强制转账-解冻支付BH接口 ");
					bankLog.setApplyTime(DateUtil.doFormatDate(new Date(), "yyyy-MM-dd HH:mm:ss"));
					bankLog.setPayChannelId(paymentRecord.getPayChannelId());
					bankLog.setReplyText(resultXML);
					/**
					 * 将响应结果转换成实体类
					 */
					logger.info("将响应结果转换成实体类.入参:"+resultXML);
					ResCommon resCommon=(ResCommon) XMLUtils.xmlToBean(resultXML,ResCommon.class,null);
					logger.info("将响应结果转换成实体类.出参:"+resCommon);
					if(resCommon!=null&&resCommon.getStatus().equals(ReqResult.AAAAAAE.getCode())){//表示中信主机接收命令成功
						resultMap.put("resultCode", "success");
						resultMap.put("resultMesg", resCommon.getStatusText());
						bankLog.setReplyStatus(0);
						msg=resCommon.getStatusText();
						result="成功";
					}else{
						//
						resultMap.put("resultCode", "exce");
						resultMap.put("resultMesg", resCommon.getStatusText());
						logger.error(resCommon.getStatusText());
						bankLog.setReplyStatus(1);
						msg=resCommon.getStatusText();
						result="失败";
						throw new Exception();
					}
					
					/**
					 * 保存请求日志
					 */
					bankLogMapper.insertSelective(bankLog);
					
				}else{
					//没有对应冻结记录，请检查
					resultMap.put("resultCode", "exce");
					resultMap.put("resultMesg", "生成解冻记录失败.");
					logger.error("生成解冻记录失败.");
					throw new Exception();
				}
				
			}else{
				//没有对应冻结记录，请检查
				resultMap.put("resultCode", "exce");
				resultMap.put("resultMesg", "没有对应冻结记录，请检查");
				logger.error("没有对应冻结记录，请检查");
				throw new Exception();
			}
			
		} catch (Exception e) {
			resultMap.put("resultCode", "exce");
			resultMap.put("resultMesg", "(后台审核)解冻支付发生异常");
			logger.error("(后台审核)解冻支付发生异常发生异常"+e.getMessage());
		}finally{
			pLog.setOpetationDesc(msg);
			pLog.setOperationResult(result);
			SPaymentLogUtils.write(pLog, pLogMapper);
		}
		logger.info("(后台审核)解冻支付返回参数"+resultMap);
		return resultMap;
	}
	

}
