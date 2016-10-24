package com.smmpay.process;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import com.smmpay.process.enumDef.OperateType;
import com.smmpay.process.enumDef.ReqResult;
import com.smmpay.respository.dao.SBankLogMapper;
import com.smmpay.respository.dao.TrPaymentRecordMapper;
import com.smmpay.respository.dao.UserMoneyExceptionMapper;
import com.smmpay.respository.dao.UserPayChannelMapper;
import com.smmpay.respository.model.ReqDlsbalqr;
import com.smmpay.respository.model.ResDlsbalqr;
import com.smmpay.respository.model.ResDlsbalqrRow;
import com.smmpay.respository.model.SBankLog;
import com.smmpay.respository.model.TrPaymentRecord;
import com.smmpay.respository.model.UserMoneyException;
import com.smmpay.respository.model.UserPayChannel;
import com.smmpay.util.CPayUtils;
import com.smmpay.util.DateUtil;
import com.smmpay.util.XMLUtils;
/**
 * 系统每日结算-比对余额-
 * 1、记录比对错误数据
 * 2、记录大账户下面附属账户总余额
 * 3、记录大账户在中信的总余额
 */
public class SysQueryBalanceProcess extends StepProcessor {

	
	
	private UserPayChannelMapper userPayChannelMapper;
	
	private UserMoneyExceptionMapper userMoneyExceptionMapper;
	
	private SBankLogMapper bankLogMapper;
	
	
	private Logger logger=Logger.getLogger(SysQueryBalanceProcess.class);
	
	
	
	protected SysQueryBalanceProcess() {
		super();
	}

	protected SysQueryBalanceProcess(Map<String, Object> paramMap) {
		super(paramMap);
	}

	protected SysQueryBalanceProcess(Map<String, Object> paramMap,
			StepProcessor nextProcessor) {
		super(paramMap, nextProcessor);
	}

	public static SysQueryBalanceProcess getInstanceWithParam(UserPayChannelMapper userPayChannelMapper,
			UserMoneyExceptionMapper userMoneyExceptionMapper,
			SBankLogMapper bankLogMapper) {

		Map<String, Object> paramMap = buildParam(null);
		
		SysQueryBalanceProcess process = new SysQueryBalanceProcess(paramMap);
		process.userPayChannelMapper=userPayChannelMapper;
		process.userMoneyExceptionMapper=userMoneyExceptionMapper;
		process.bankLogMapper=bankLogMapper;
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
			 * 商户附属账户余额查询 
			 */
			logger.info("开始执行余额查询/余额比对/异常记录操作,入参："+resultParamMap);
			String sendXML=XMLUtils.beanToXML(buildReqParam(resultParamMap));
			
			logger.info("请求中信接口，获取商户附属账户余额查询.入参:"+sendXML);
			String resultXML=CPayUtils.postRequest(sendXML);
			
			logger.info("将响应结果转换成实体类.入参:"+resultXML);
			ResDlsbalqr resDlsbalqr=(ResDlsbalqr) XMLUtils.xmlToBean(resultXML,ResDlsbalqr.class,ResDlsbalqrRow.class);
			logger.info("将响应结果转换成实体类.出参:"+resDlsbalqr);
			/**
			 * 记录日志
			 */
			
			SBankLog bankLog=new SBankLog();
			bankLog.setRequestParam(sendXML);
			bankLog.setRequestInterface("商户附属账户余额查询 ");
			bankLog.setApplyTime(DateUtil.doFormatDate(new Date(), "yyyy-MM-dd HH:mm:ss"));
			bankLog.setPayChannelId(Integer.valueOf(String.valueOf(resultParamMap.get("payChannelId"))));
			bankLog.setReplyText(resultXML);
			
			/**
			 * 查询支付通道余额
			 * 
			 */
			UserPayChannel userPayChannel=userPayChannelMapper.queryUserMoney(resultParamMap);
			
			if(resDlsbalqr.getStatus().equals(ReqResult.AAAAAAA.getCode())){
				bankLog.setReplyStatus(0);
				ResDlsbalqrRow resDlsbalqrRow=resDlsbalqr.getList().get(0);
				//取实际余额作比较
				if(resDlsbalqrRow.getSJAMT().compareTo(userPayChannel.getUseMoney().add(userPayChannel.getFreezeMoney()))==0){//可用余额+冻结金额=实际余额
					/**
					 * 无异常、返回余额
					 */
					resultMap.put("handleCode", OperateType.O01.getCode());//处理代码
					resultMap.put("handleMesg", OperateType.O01.getMessage());
					
					
				}else{
					/**
					 * 向异常表新增一条记录
					 */
					logger.error("余额比对异常，往账户异常表增加一条记录.");
					int count=userMoneyExceptionMapper.insert(buildMoneyException(resDlsbalqrRow,userPayChannel,resultParamMap));
					resultMap.put("handleCode", OperateType.O02.getCode());//处理代码
					resultMap.put("handleMesg", OperateType.O02.getMessage());
				}
				
				resultMap.put("userMoney", userPayChannel.getUseMoney());//可用余额
				resultMap.put("freezeMoney", userPayChannel.getFreezeMoney());//冻结资金
				resultMap.put("totalMoney", userPayChannel.getUseMoney().add(userPayChannel.getFreezeMoney()));//总金额资金
				resultMap.put("sjAmt", resDlsbalqrRow.getSJAMT());//用户在中信实际余额
				
				resultMap.put("resultCode", "success");
				resultMap.put("resultMesg", "调用成功.");
				
			}else{
				bankLog.setReplyStatus(1);
				resultMap.put("resultCode", "exce");
				resultMap.put("resultMesg", "请求中信失败");
				
			}
			/**
			 * 保存请求日志
			 */
			bankLogMapper.insertSelective(bankLog);
			
		} catch (Exception e) {
			resultMap.put("resultCode", "exce");
			resultMap.put("resultMesg", "获取商户附属账户余额查询发生异常");
			logger.error("获取商户附属账户余额查询"+e.getMessage());
			
			e.printStackTrace();
		}
		
		logger.info("获取商户附属账户余额查询："+resultMap);
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
		ume.setUserId(Integer.valueOf(String.valueOf(resultParamMap.get("userId"))));
		ume.setUserCompanyName((String)resultParamMap.get("subAccNm"));//账号名称
		ume.setOperate(Integer.valueOf(OperateType.O2.getCode()));//余额查询异常
		ume.setPayChannelId((Integer)resultParamMap.get("payChannelId"));
		ume.setUserPayChannelAccount((String)resultParamMap.get("subAccNo"));//附属账号
		ume.setUserPayChannelMoney(resDlsbalqrRow.getKYAMT());//用户在中信的余额
		ume.setUserMoney(userPayChannel.getUseMoney());//用户在支付系统中的余额
		ume.setAuditStatus(0);//未处理
		return ume;
	}

}
