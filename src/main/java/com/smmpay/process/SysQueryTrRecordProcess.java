package com.smmpay.process;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;

import com.smmpay.respository.dao.TrRecordMapper;
import com.smmpay.respository.dao.UserAccountMapper;
import com.smmpay.respository.model.TrRecord;
import com.smmpay.respository.model.UserAccount;
import com.smmpay.util.DateUtil;
/**
 * 
 *系统每日结算 
 *
 */
public class SysQueryTrRecordProcess extends StepProcessor {

	
	private TrRecordMapper trRecordMapper;
	
	private UserAccountMapper userAccountMapper;
	
	private Logger logger=Logger.getLogger(SysQueryTrRecordProcess.class);
	
	
	
	protected SysQueryTrRecordProcess() {
		super();
	}

	protected SysQueryTrRecordProcess(Map<String, Object> paramMap) {
		super(paramMap);
	}

	protected SysQueryTrRecordProcess(Map<String, Object> paramMap,
			StepProcessor nextProcessor) {
		super(paramMap, nextProcessor);
	}

	public static SysQueryTrRecordProcess getInstanceWithParam(Map<String, Object> queryParam,TrRecordMapper trMapper,UserAccountMapper uaMapper) {

		Map<String, Object> paramMap = buildParam(queryParam);
		
		SysQueryTrRecordProcess process = new SysQueryTrRecordProcess(paramMap);
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
			/*List<TrRecord> trRecordList=trRecordMapper.queryLastTrRecord(resultParamMap);
			*//**
			 * 有入金记录
			 *//*
			if(trRecordList.size()>0){
				
				TrRecord trRecord=trRecordList.get(0);
				//最后入金记录时间
				String startDate=DateUtil.doSFormatDate2(trRecord.getPayChannelTrTime(), "yyyyMMdd");
				resultMap.put("startDate", startDate);
				resultMap.put("resultCode", "success");
				resultMap.put("resultMesg", "调用成功");
				logger.error("最后入金记录时间"+startDate);
				
			}else{
				
				*//**
				 * 没有交易记录，这种情况发生在初始交易时还没有同步入金记录
				 * 取注册时间作为开始时间
				 *//*
				UserAccount userAccount=userAccountMapper.selectByPrimaryKey(Integer.valueOf((String) resultParamMap.get("userId")));
				if(userAccount!=null){
					String startDate=DateUtil.doSFormatDate2(userAccount.getRegisterTime(), "yyyyMMdd");
					resultMap.put("startDate", "20160202");
					resultMap.put("resultCode", "success");
					resultMap.put("resultMesg", "调用成功");
					logger.info("取注册时间作为开始时间"+startDate);
					
				}else{
					resultMap.put("resultCode", "exce");
					resultMap.put("resultMesg", "没有该注册用户");
				}
			}*/
			
			String startDate=String.valueOf(resultParamMap.get("startDate"));
			resultMap.put("startDate", startDate);
			//系统当前时间
			String endDate=DateUtil.doFormatDate(new Date(), "yyyyMMdd");
			resultMap.put("endDate", endDate);
			resultMap.put("resultCode", "success");
			resultMap.put("resultMesg", "调用成功");
			
		} catch (Exception e) {
			resultMap.put("resultCode", "exce");
			resultMap.put("resultMesg", "获取最后入金记录时间发生异常");
			logger.error("获取最后入金记录时间发生异常"+e.getMessage());
		}
		logger.info("获取最后入金记录时间返回参数"+resultMap);
		return resultMap;
	}

}
