package com.smmpay.process;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.smmpay.respository.dao.TrRecordMapper;
import com.smmpay.respository.dao.UserAccountMapper;
import com.smmpay.respository.model.TrRecord;
import com.smmpay.util.DateUtil;
/**
 * 

 * 查询(支付系统库)指定账户入金记录、入金退款记录
 * 
 * 入参：用户支付ID(userId)、支付渠道ID(payChannelId)、附属账号(subAccNo)、附属账户名称(subAccNm)
 * 
 * 出参： 该账户最后入金记录或入金退款记录的银行记录时间
 * 
 * @author zengshihua
 *
 */
public class QueryTrRecordProcess extends StepProcessor {

	
	private TrRecordMapper trRecordMapper;
	
	private UserAccountMapper userAccountMapper;
	
	private Logger logger=Logger.getLogger(QueryTrRecordProcess.class);
	
	
	
	protected QueryTrRecordProcess() {
		super();
	}

	protected QueryTrRecordProcess(Map<String, Object> paramMap) {
		super(paramMap);
	}

	protected QueryTrRecordProcess(Map<String, Object> paramMap,
			StepProcessor nextProcessor) {
		super(paramMap, nextProcessor);
	}

	public static QueryTrRecordProcess getInstanceWithParam(Map<String, Object> queryParam,TrRecordMapper trMapper,UserAccountMapper uaMapper) {

		Map<String, Object> paramMap = buildParam(queryParam);
		
		QueryTrRecordProcess process = new QueryTrRecordProcess(paramMap);
		process.trRecordMapper=trMapper;
		process.userAccountMapper=uaMapper;
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
		try {
			logger.info("开始获取最后入金记录时间,入参："+resultParamMap);
			List<TrRecord> trRecordList=trRecordMapper.queryLastTrRecord(resultParamMap);
			/**
			 * 有入金记录
			 */
			if(trRecordList.size()>0){
				
				TrRecord trRecord=trRecordList.get(0);
				//最后入金记录时间
				String startDate=DateUtil.doSFormatDate2(trRecord.getPayChannelTrTime(), "yyyyMMdd");
				resultMap.put("startDate", startDate);
				resultMap.put("resultCode", "success");
				resultMap.put("resultMesg", "调用成功");
				logger.error("最后入金记录时间"+startDate);
				
			}else{
				
				Calendar c = Calendar.getInstance();
			    SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
			    c.add(Calendar.MONTH, -2);
				//String startDate=DateUtil.doSFormatDate2(userAccount.getRegisterTime(), "yyyyMMdd");
				resultMap.put("startDate", sdf.format(c.getTime()));
				resultMap.put("resultCode", "success");
				resultMap.put("resultMesg", "调用成功");
				logger.info("取注册时间作为开始时间"+sdf.format(c.getTime()));
				/**
				 * 没有交易记录，这种情况发生在初始交易时还没有同步入金记录
				 * 取注册时间作为开始时间
				 */
				/*UserAccount userAccount=userAccountMapper.selectByPrimaryKey(Integer.valueOf((String) resultParamMap.get("userId")));
				if(userAccount!=null){
					String startDate=DateUtil.doSFormatDate2(userAccount.getRegisterTime(), "yyyyMMdd");
					resultMap.put("startDate", startDate);
					resultMap.put("resultCode", "success");
					resultMap.put("resultMesg", "调用成功");
					logger.info("取注册时间作为开始时间"+startDate);
					
				}else{
					resultMap.put("resultCode", "exce");
					resultMap.put("resultMesg", "没有该注册用户");
				}*/
			}
			//系统当前时间
			String endDate=DateUtil.doFormatDate(new Date(), "yyyyMMdd");
			resultMap.put("endDate", endDate);
			
			
		} catch (Exception e) {
			resultMap.put("resultCode", "exce");
			resultMap.put("resultMesg", "获取最后入金记录时间发生异常");
			logger.error("获取最后入金记录时间发生异常"+e.getMessage());
		}
		logger.info("获取最后入金记录时间返回参数"+resultMap);
		return resultMap;
	}
}
