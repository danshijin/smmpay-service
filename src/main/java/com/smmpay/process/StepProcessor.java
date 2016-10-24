package com.smmpay.process;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;


public abstract class StepProcessor {
	
	public Map<String, Object> paramMap = null;
	
	public StepProcessor nextProcessor;

	protected StepProcessor() {
		super();
	}

	protected StepProcessor(Map<String, Object> paramMap) {
		super();
		this.paramMap = paramMap;
	}

	protected StepProcessor(Map<String, Object> paramMap, StepProcessor nextProcessor) {
		super();
		this.paramMap = paramMap;
		this.nextProcessor = nextProcessor;

	}

	/**
	 * @param nextProcessor 下一个操作
	 * @return
	 */
	public StepProcessor next(StepProcessor nextProcessor) {
		this.nextProcessor = nextProcessor;
		return nextProcessor;
	}
	
	/**
	 * 业务步骤实际操作内容，各步骤必须实现该方法。
	 * @param  resultParamMap 前序步骤累计操作结果 ，结合本步骤基本参数作为本步骤操作输入参数
	 * @return  包含以下值：
	 * 			业务操作成功resultCode(succ),会继续调用下一步操作(如果有下一步);
	 * 			否则为业务操作失败：resultCode(exce|fail),errorCode,resultMesg，会继续调用fail方法并终止process操作(链).
	 */
	protected abstract Map<String, Object> action(Map<String, Object> resultParamMap) throws Exception;
	
	/**
	 * 
	 * @param result 需要包含以下三个值
	 * 				resultCode
	 * 				errorCode
	 * 				resultMesg
	 * 
	 * @return  需要包含以下三个值
	 * 				resultCode
	 * 				errorCode
	 * 				resultMesg
	 */
	protected  Map<String, Object> failed(Map<String, Object> result){
		Map<String, Object> resultMap=new HashMap<String, Object>();
		
		resultMap.put("resultCode", "exce");
		if(result==null || result.isEmpty()){
			resultMap.put("errorCode", "SYS_EXCEPTION");
			resultMap.put("resultMesg", "请求失败");
		}else{
			resultMap.putAll(result);
			String errorCode=(String) result.get("errorCode");
			String resultMesg=(String) result.get("resultMesg");
			resultMap.put("errorCode", StringUtils.isNotBlank(errorCode)?errorCode:"SYS_EXCEPTION");
			resultMap.put("resultMesg", StringUtils.isNotBlank(resultMesg)?resultMesg:"请求失败");
			
		}
		
		return resultMap;
	}
	
	/**
	 * 
	 * @param resultParamMap  前面步骤操作结果
	 * @return
	 * @throws Exception 
	 */
	public final Map<String, Object> process(Map<String, Object> resultParamMap) throws Exception {
		
		// 开始操作.resultParamMap

		Map<String, Object> mergeParamMap = mergeMap(resultParamMap, paramMap);
		if (mergeParamMap != null) {
			// 移除上一步操作结果代码
			mergeParamMap.remove("resultCode");
			mergeParamMap.remove("resultMesg");
		}

		Map<String, Object> resultMap = action(mergeParamMap);
		// 操作结束.resultMap
		Map<String, Object> mergeResultMap = mergeMap(mergeParamMap, resultMap);

		if (resultMap != null && resultMap.get("resultCode").equals("success")) {
			// 操作成功，返回数据 mergeResultMap
			if (nextProcessor != null) {
				Map<String, Object> nextResultMap = nextProcessor.process(mergeResultMap);
				return nextResultMap;
			} else {
				
				return mergeResultMap;
			}
			
		}else if(resultMap != null && resultMap.get("resultCode").equals("break")){
			//跳出操作，并没有异常
			return mergeResultMap;
			
		}else {
			//失败，结束操作链
			return failed(mergeResultMap);
		}
		
	}
	
	/**
	 * 合并基本参数和上一步操作结果作为本步骤的输入参数
	 * @param resultParamMap
	 * @param paramMap
	 * @return
	 */
	@SuppressWarnings("unused")
	public Map<String, Object> mergeMap(Map<String, Object> resultParamMap,Map<String, Object> paramMap) {
		
		if(resultParamMap!=null){
			if(paramMap!=null){
				resultParamMap.putAll(paramMap);
			}
			return resultParamMap;
		}
		if(paramMap!=null){
			if(resultParamMap!=null){
				paramMap.putAll(resultParamMap);
			}
			return paramMap;
		}
		return null;
		
	}
}
