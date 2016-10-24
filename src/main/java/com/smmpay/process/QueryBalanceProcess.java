package com.smmpay.process;

import com.smmpay.process.enumDef.OperateType;
import com.smmpay.process.enumDef.PaymentLog;
import com.smmpay.process.enumDef.ReqResult;
import com.smmpay.respository.dao.SPaymentLogMapper;
import com.smmpay.respository.dao.TrPaymentRecordMapper;
import com.smmpay.respository.dao.UserMoneyExceptionMapper;
import com.smmpay.respository.dao.UserPayChannelMapper;
import com.smmpay.respository.model.*;
import com.smmpay.util.CPayUtils;
import com.smmpay.util.DateUtil;
import com.smmpay.util.SPaymentLogUtils;
import com.smmpay.util.XMLUtils;
import org.apache.log4j.Logger;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
/**
 * 
 * 调用中信接口查询余额
 * 
 * @author zengshihua
 *
 */
public class QueryBalanceProcess extends StepProcessor {

	
	
	private UserPayChannelMapper userPayChannelMapper;
	
	private UserMoneyExceptionMapper userMoneyExceptionMapper;
	
	private TrPaymentRecordMapper trPaymentRecordMapper;
	
	private SPaymentLogMapper pLogMapper;
	
	private String type;
	
	private Logger logger=Logger.getLogger(QueryBalanceProcess.class);
	
	
	
	protected QueryBalanceProcess() {
		super();
	}

	protected QueryBalanceProcess(Map<String, Object> paramMap) {
		super(paramMap);
	}

	protected QueryBalanceProcess(Map<String, Object> paramMap,
			StepProcessor nextProcessor) {
		super(paramMap, nextProcessor);
	}

	public static QueryBalanceProcess getInstanceWithParam(UserPayChannelMapper userPayChannelMapper,
			UserMoneyExceptionMapper userMoneyExceptionMapper,
			TrPaymentRecordMapper trPaymentRecordMapper,
			SPaymentLogMapper pLogMapper) {

		Map<String, Object> paramMap = buildParam(null);
		
		QueryBalanceProcess process = new QueryBalanceProcess(paramMap);
		process.userPayChannelMapper=userPayChannelMapper;
		process.userMoneyExceptionMapper=userMoneyExceptionMapper;
		process.trPaymentRecordMapper=trPaymentRecordMapper;
		process.pLogMapper=pLogMapper;
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
	 * 组装参数返回
	 */
	@Override
	protected Map<String, Object> action(Map<String, Object> resultParamMap) {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		try {
			
			logger.info(">>查询余额,操作类型:>>"+resultParamMap.get("operateType"));
			
			String msg="",result="失败";
			String operateType=String.valueOf(resultParamMap.get("operateType"));
			SPaymentLog pLog=new SPaymentLog();
			
			
			logger.info("开始执行余额查询/余额比对/异常记录操作,入参："+resultParamMap);
			String sendXML=XMLUtils.beanToXML(buildReqParam(resultParamMap));
			
			/**避免发生ED11308错误 接口调用频繁*/
			if(resultParamMap.containsKey("job")&&"Y".equals(resultParamMap.get("job"))){
				Thread.sleep(13000);
			}
			
			/**
			 * 请求中信接口，获取交易明细
			 */
			logger.info("请求中信接口，获取商户附属账户余额查询.入参:"+sendXML);
			String resultXML=CPayUtils.postRequest(sendXML);
			/**
			 * 将响应结果转换成实体类
			 */
			logger.info("将响应结果转换成实体类.入参:"+resultXML);
			ResDlsbalqr resDlsbalqr=(ResDlsbalqr) XMLUtils.xmlToBean(resultXML,ResDlsbalqr.class,ResDlsbalqrRow.class);
			logger.info("将响应结果转换成实体类.出参:"+resDlsbalqr);
			
			/**
			 * 查询支付通道余额
			 * 
			 */
			UserPayChannel userPayChannel=userPayChannelMapper.queryUserMoney(resultParamMap);
			
			if(resDlsbalqr!=null&&ReqResult.AAAAAAA.getCode().equals(resDlsbalqr.getStatus())
					&&resDlsbalqr.getList().size()>0){
				
				ResDlsbalqrRow resDlsbalqrRow=resDlsbalqr.getList().get(0);
				//取可用余额+smm冻结金额作比较			
				if(resDlsbalqrRow.getKYAMT().compareTo(userPayChannel.getUseMoney().add(userPayChannel.getSmmFreezeMoney()))==0){
					/**
					 * 无异常、返回余额
					 */
					resultMap.put("handleCode", OperateType.O01.getCode());//处理代码
					resultMap.put("handleMesg", OperateType.O01.getMessage());
					resultMap.put("resultCode", "success");
					resultMap.put("resultMesg", "余额比对无异常");
					
					
				}else{
					
					resultMap.put("handleCode", OperateType.O02.getCode());//处理代码
					resultMap.put("handleMesg", OperateType.O02.getMessage());
					resultMap.put("resultCode", "exce");//有异常、结束操作
					resultMap.put("resultMesg", "余额比对异常,请先处理.");
					
					/**
					 * 执行步骤日志
					 */
					msg="余额比对异常,请先处理.";
					if(!operateType.equals(OperateType.O2.getCode())){//查询余额不记录异常
						
						logger.error("余额比对异常，往账户异常表增加一条记录.");
						/**
						 * 向异常表新增一条记录
						 */
						int count=userMoneyExceptionMapper.insert(buildMoneyException(resDlsbalqrRow,userPayChannel,resultParamMap));
					}
					
					if(resultParamMap.containsKey("paymentId")&&resultParamMap.get("paymentId")!=null){
						
						pLog.setPaymentId(Integer.valueOf(String.valueOf(resultParamMap.get("paymentId"))));
						pLog.setOperationName(PaymentLog.P1.getMessage());
						pLog.setOpetationDesc(msg);
						pLog.setOperationResult(result);
						SPaymentLogUtils.write(pLog, pLogMapper);
						logger.error("将支付记录设置为手动处理:"+resultParamMap.get("paymentId"));
						/**
						 * 将支付记录设置为手动处理
						 */
						trPaymentRecordMapper.updateHandleByKey((Integer)resultParamMap.get("paymentId"));
					}
					
				}
				
				resultMap.put("userMoney", userPayChannel.getUseMoney());//可用余额
				resultMap.put("freezeMoney", userPayChannel.getFreezeMoney());//冻结资金
				resultMap.put("totalMoney", userPayChannel.getUseMoney().add(userPayChannel.getFreezeMoney()));//总金额资金
				
			}else if(resDlsbalqr!=null&& resDlsbalqr.getStatus().equals(ReqResult.ED11308.getCode())){
				
				/**
				 * 接口调用频繁
				 */
				logger.info(ReqResult.ED11308.getMessage());
				resultMap.put("resultCode", "exce");
				resultMap.put("resultMesg", ReqResult.ED11308.getMessage());
				resultMap.put("resCode", ReqResult.ED11308.getCode());
				
			}else{
				logger.info(resDlsbalqr.getStatusText());
				resultMap.put("resultCode", "exce");
				resultMap.put("resultMesg", resDlsbalqr.getStatusText());
				/**
				 * 执行步骤日志
				 */
				msg="请求中信失败";
				
				if(!operateType.equals(OperateType.O2.getCode())){//查询余额不记录异常
					pLog.setPaymentId(Integer.valueOf(String.valueOf(resultParamMap.get("paymentId"))));
					pLog.setOperationName(PaymentLog.P1.getMessage());
					pLog.setOpetationDesc(msg);
					pLog.setOperationResult(result);
					SPaymentLogUtils.write(pLog, pLogMapper);
				}
			}
			
			
			
		} catch (Exception e) {
			resultMap.put("resultCode", "exce");
			resultMap.put("resultMesg", "获取商户附属账户余额查询发生异常");
			logger.error("获取商户附属账户余额查询"+e.getMessage());
		}
		
		logger.info("余额比对结束."+resultMap);
		return resultMap;
	}
	/**
	 * 组装请求参数
	 * @param resultParamMap
	 * @return
	 */
	public ReqDlsbalqr buildReqParam(Map<String, Object> resultParamMap){
		
		ReqDlsbalqr req=new ReqDlsbalqr();
		req.setSubAccNo((String)resultParamMap.get("subAccNo"));
		
		return req;
	}
	/**
	 * 组装异常记录参数
	 * @param resultParamMap
	 * @return
	 */
	public UserMoneyException buildMoneyException(ResDlsbalqrRow resDlsbalqrRow,
			UserPayChannel userPayChannel, Map<String, Object> resultParamMap){
		
		UserMoneyException ume=new UserMoneyException();
		ume.setCreateTime(DateUtil.doFormatDate(new Date(), "yyyy-MM-dd HH:mm:ss"));
		ume.setUserId(Integer.valueOf((String)resultParamMap.get("userId")));
		ume.setUserCompanyName((String)resultParamMap.get("subAccNm"));//账号名称
		ume.setOperate(Integer.valueOf(String.valueOf(resultParamMap.get("operateType")!=null?resultParamMap.get("operateType"):"")));//操作类型
		ume.setPayChannelId((Integer)resultParamMap.get("payChannelId"));
		ume.setUserPayChannelAccount((String)resultParamMap.get("subAccNo"));//附属账号
		ume.setUserPayChannelMoney(resDlsbalqrRow.getKYAMT());//用户在中信的余额
		ume.setUserMoney(userPayChannel.getUseMoney());//用户在支付系统中的余额
		ume.setAuditStatus(0);//未处理
		if(resultParamMap.containsKey("paymentId")
				&&resultParamMap.get("paymentId")!=null){
			ume.setPaymentId(Integer.valueOf(String.valueOf(resultParamMap.get("paymentId"))));
		}
		
		return ume;
	}

}
