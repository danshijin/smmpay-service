package com.smmpay.process;

import java.math.RoundingMode;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.smmpay.internal.service.IDatabaseEncrypt;
import com.smmpay.process.enumDef.PaymentSatus;
import com.smmpay.process.enumDef.ReqResult;
import com.smmpay.process.enumDef.TranType;
import com.smmpay.respository.dao.SBankLogMapper;
import com.smmpay.respository.dao.TrFreezeRecordMapper;
import com.smmpay.respository.dao.TrPaymentRecordMapper;
import com.smmpay.respository.dao.TrRecordMapper;
import com.smmpay.respository.dao.TrUnfreezeRecordMapper;
import com.smmpay.respository.dao.UserAccountMapper;
import com.smmpay.respository.dao.UserPayChannelMapper;
import com.smmpay.respository.model.ReqDlmdetrn;
import com.smmpay.respository.model.SBankLog;
import com.smmpay.respository.model.TrFreezeRecord;
import com.smmpay.respository.model.TrPaymentRecord;
import com.smmpay.respository.model.TrUnfreezeRecord;
import com.smmpay.respository.model.UserPayChannel;
import com.smmpay.respository.model.base.ResCommon;
import com.smmpay.util.CPayUtils;
import com.smmpay.util.ClientIdUtils;
import com.smmpay.util.DateUtil;
import com.smmpay.util.XMLUtils;
/**
 * 
 * 手动解冻支付处理
 * 
 */
public class ManualThawPayProcess extends StepProcessor {

	private TrFreezeRecordMapper freezeRecordMapper;
	
	private TrPaymentRecord paymentRecord;
	
	private UserPayChannelMapper userPayChannelMapper;
	
	private TrUnfreezeRecordMapper trUnfreezeRecordMapper;
	
	private TrPaymentRecordMapper trPaymentRecordMapper;
	
	private SBankLogMapper bankLogMapper;

	private IDatabaseEncrypt iDatabaseEncrypt;
	
	private Logger logger=Logger.getLogger(ManualThawPayProcess.class);
	
	
	
	protected ManualThawPayProcess() {
		super();
	}

	protected ManualThawPayProcess(Map<String, Object> paramMap) {
		super(paramMap);
	}

	protected ManualThawPayProcess(Map<String, Object> paramMap,
			StepProcessor nextProcessor) {
		super(paramMap, nextProcessor);
	}

	public static ManualThawPayProcess getInstanceWithParam(Map<String, Object> queryParam,
			TrFreezeRecordMapper freezeRecordMapper,
			TrPaymentRecord paymentRecord,
			UserPayChannelMapper userPayChannelMapper,
			TrUnfreezeRecordMapper trUnfreezeRecordMapper,
			TrPaymentRecordMapper trPaymentRecordMapper,
			SBankLogMapper bankLogMapper,
			IDatabaseEncrypt iDatabaseEncrypt) {

		Map<String, Object> paramMap = buildParam(queryParam);
		
		ManualThawPayProcess process = new ManualThawPayProcess(paramMap);
		process.freezeRecordMapper=freezeRecordMapper;
		process.paymentRecord=paymentRecord;
		process.userPayChannelMapper=userPayChannelMapper;
		process.trUnfreezeRecordMapper=trUnfreezeRecordMapper;
		process.trPaymentRecordMapper=trPaymentRecordMapper;
		process.bankLogMapper=bankLogMapper;
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
	 * @throws Exception 
	 */
	@Override
	protected Map<String, Object> action(Map<String, Object> resultParamMap) throws Exception {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		
			logger.info("开始获取支付记录的最后一条冻结记录,入参："+resultParamMap);
			List<TrFreezeRecord> fRecordList = freezeRecordMapper.lastFreezeRecord(resultParamMap);
			if(fRecordList!=null&&fRecordList.size()>0){
			/**
			 * 生成clientID
			 */
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
			unFRecord.setUnfreezeNote("手动解冻支付");
			
			Integer unFCount=trUnfreezeRecordMapper.insertSelective(unFRecord);
			
			if(unFCount>0){
				
				// 添加数据验证字段
				logger.info("解冻记录表，手动解冻添加verifyCode");
				iDatabaseEncrypt.addUnFreezeVerifyCode(unFRecord.getId());
				
				//更新支付记录状态为已冻结
				paymentRecord.setPaymentStatus(PaymentSatus.PS10.getCode());//已冻结
				Integer uPCount=trPaymentRecordMapper.updatePaymentStatus(paymentRecord);
				
				if(uPCount>0){
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
					reqDl.setMemo(buyerPayChanne.getUserAccountNo()+"解冻支付到"+sellerPayChanne.getUserAccountNo());//摘要
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
					if(resCommon!=null&&resCommon.getStatus().equals(ReqResult.AAAAAAE.getCode())){//表示中信主机接受命令成功
						resultMap.put("resultCode", "success");
						resultMap.put("resultMesg", resCommon.getStatusText());
						bankLog.setReplyStatus(0);
					}else{
						//
						resultMap.put("resultCode", "exce");
						resultMap.put("resultMesg", resCommon.getStatusText());
						logger.error("错误原因:"+resCommon.getStatusText());
						bankLog.setReplyStatus(1);
						throw new Exception();
					}
					try {
						
						/**保存请求日志*/
						bankLogMapper.insertSelective(bankLog);
						
					} catch (Exception e) {
						e.printStackTrace();
					}
					
					
				}else{
					resultMap.put("resultCode", "exce");
					resultMap.put("resultMesg", "支付记录状态失败.");
					logger.error("支付记录状态失败.");
					throw new Exception();
				}
				
				}else{
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
			}
		
		logger.info("手动解冻支付处理返回参数"+resultMap);
		return resultMap;
	}

}
