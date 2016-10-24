package com.smmpay.process;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.smmpay.process.enumDef.ReqResult;
import com.smmpay.process.enumDef.ReqType;
import com.smmpay.respository.dao.TrFreezeRecordMapper;
import com.smmpay.respository.model.ReqDlcidstt;
import com.smmpay.respository.model.ResDlcidstt;
import com.smmpay.respository.model.TrFreezeRecord;
import com.smmpay.respository.model.base.ResCommonRow;
import com.smmpay.util.CPayUtils;
import com.smmpay.util.XMLUtils;
/**
 * 
 * 查询交易状态
 * 
 * @author zengshihua
 *
 */
public class QueryTRStatusProcess extends StepProcessor {

	
	private Logger logger=Logger.getLogger(QueryTRStatusProcess.class);
	
	protected QueryTRStatusProcess() {
		super();
	}

	protected QueryTRStatusProcess(Map<String, Object> paramMap) {
		super(paramMap);
	}

	protected QueryTRStatusProcess(Map<String, Object> paramMap,
			StepProcessor nextProcessor) {
		super(paramMap, nextProcessor);
	}

	public static QueryTRStatusProcess getInstanceWithParam(Map<String, Object> param) {

		Map<String, Object> paramMap = buildParam(param);
		
		QueryTRStatusProcess process = new QueryTRStatusProcess(paramMap);
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
	 * 查询交易状态
	 */
	@Override
	protected Map<String, Object> action(Map<String, Object> resultParamMap) {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		try {
			
			ReqDlcidstt reqDL=new ReqDlcidstt(); 
			reqDL.setClientID(String.valueOf(resultParamMap.get("clientID")));
			reqDL.setType(String.valueOf(resultParamMap.get("type")));
			String sendXML=XMLUtils.beanToXML(reqDL);
			
			/**避免发生ED11308错误 接口调用频繁*/
			if(resultParamMap.containsKey("job")&&"Y".equals(resultParamMap.get("job"))){
				Thread.sleep(13000);
			}
			
			/**
			 * 请求中信交易状态查询接口
			 */
			logger.info("请求中信接口，获取交易状态.入参:"+sendXML);
			String resultXML=CPayUtils.postRequest(sendXML);
			/**
			 * 将响应结果转换成实体类
			 */
			logger.info("将响应结果转换成实体类.入参:"+resultXML);
			ResDlcidstt resCommon=(ResDlcidstt) XMLUtils.xmlToBean(resultXML,ResDlcidstt.class,ResCommonRow.class);
			logger.info("将响应结果转换成实体类.出参:"+resCommon);
			
			if(resCommon!=null&&resCommon.getStatus().equals(ReqResult.AAAAAAA.getCode())){
				List<ResCommonRow> resc= resCommon.getList();
				//stt: 0 成功 1 失败 2未知 3审核拒绝 4 用户撤销
				if(resc.get(0).getStatus().equals(ReqResult.AAAAAAA.getCode())
						&&resc.get(0).getStt().equals("0")){
					resultMap.put("resultCode", "success");
					resultMap.put("resultMesg", resc.get(0).getStatusText()!=null?resc.get(0).getStatusText():"");
					resultMap.put("statusText", resc.get(0).getStatusText()!=null?resc.get(0).getStatusText():"");
					
				}else if(resc.get(0).getStatus().equals(ReqResult.AAAAAAC.getCode())
						&&resc.get(0).getStt().equals("2")){
					//跳出当前操作
					resultMap.put("resultCode", "break");
					resultMap.put("resultMesg", resc.get(0).getStatusText()!=null?resc.get(0).getStatusText():"");
					resultMap.put("statusText", resc.get(0).getStatusText()!=null?resc.get(0).getStatusText():"");
					
				}else if(resc.get(0).getStatus().equals(ReqResult.AAAAAAA.getCode())
						&&resc.get(0).getStt().equals("1")){
					//作失败处理
					resultMap.put("resultCode", "exce");
					resultMap.put("resultMesg", resc.get(0).getStatusText()!=null?resc.get(0).getStatusText():"");
					resultMap.put("statusText", resc.get(0).getStatusText()!=null?resc.get(0).getStatusText():"");
					
				}else if(resc.get(0).getStatus().equals(ReqResult.ED02083.getCode())){//ED02083
					//输入的客户流水号无制单信息,请检查输入项
					resultMap.put("resultCode", "exce");
					resultMap.put("resultMesg", resCommon.getStatusText()!=null?resCommon.getStatusText():"");
					resultMap.put("statusText", resCommon.getStatusText()!=null?resCommon.getStatusText():"");
				}else{
					//除成功、失败、未知以外状态
					resultMap.put("resultCode", "break");
					resultMap.put("resultMesg", resc.get(0).getStatusText()!=null?resc.get(0).getStatusText():"");
					resultMap.put("statusText", resc.get(0).getStatusText()!=null?resc.get(0).getStatusText():"");
				}
			}else{
				//作失败处理
				//resultMap.put("resultCode", "exce");
				resultMap.put("resultCode", "break");
				resultMap.put("resultMesg", resCommon.getStatusText()!=null?resCommon.getStatusText():"");
			}
			
		} catch (Exception e) {
			//resultMap.put("resultCode", "exce");
			resultMap.put("resultCode", "break");
			resultMap.put("resultMesg", "查询交易状态发生异常.");
			logger.error("查询交易状态发生异常."+e.getMessage());
		}
		
		logger.info("结束查询交易状态："+resultMap);
		return resultMap;
	}
}
