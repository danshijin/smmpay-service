package com.smmpay.process;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.smmpay.process.enumDef.ReqResult;
import com.smmpay.process.enumDef.ReqType;
import com.smmpay.respository.dao.TrFreezeRecordMapper;
import com.smmpay.respository.dao.UserMoneyExceptionMapper;
import com.smmpay.respository.dao.UserPayChannelMapper;
import com.smmpay.respository.model.ReqDlcidstt;
import com.smmpay.respository.model.ResDlcidstt;
import com.smmpay.respository.model.TrFreezeRecord;
import com.smmpay.respository.model.TrPaymentRecord;
import com.smmpay.respository.model.UserMoneyException;
import com.smmpay.respository.model.base.ResCommon;
import com.smmpay.respository.model.base.ResCommonRow;
import com.smmpay.util.CPayUtils;
import com.smmpay.util.XMLUtils;
/**
 * 
 * 查询解冻交易状态
 * 
 * @author zengshihua
 *
 */
public class QueryUNTRStatusProcess extends StepProcessor {

	
	private Logger logger=Logger.getLogger(QueryUNTRStatusProcess.class);
	
	private TrFreezeRecordMapper freezeRecordMapper;
	private TrFreezeRecord trFreezeRecord;
	
	
	protected QueryUNTRStatusProcess() {
		super();
	}

	protected QueryUNTRStatusProcess(Map<String, Object> paramMap) {
		super(paramMap);
	}

	protected QueryUNTRStatusProcess(Map<String, Object> paramMap,
			StepProcessor nextProcessor) {
		super(paramMap, nextProcessor);
	}

	public static QueryUNTRStatusProcess getInstanceWithParam(TrFreezeRecordMapper freezeRecordMapper,TrFreezeRecord trFreezeRecord) {

		//Map<String, Object> paramMap = buildParam(null);
		
		QueryUNTRStatusProcess process = new QueryUNTRStatusProcess(null);
		process.freezeRecordMapper=freezeRecordMapper;
		process.trFreezeRecord=trFreezeRecord;
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
	 * 查询解冻交易状态
	 */
	@Override
	protected Map<String, Object> action(Map<String, Object> resultParamMap) {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		try {
			
			ReqDlcidstt reqDL=new ReqDlcidstt(); 
			//reqDL.setClientID(trFreezeRecord.getUnfreezeClientId());
			reqDL.setType(ReqType.DLMDETRN.getCode());
			String sendXML=XMLUtils.beanToXML(reqDL);
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
				}else if(resc.get(0).getStatus().equals(ReqResult.AAAAAAC.getCode())
						&&resc.get(0).getStt().equals("2")){
					//跳出当前操作
					resultMap.put("resultCode", "break");
					resultMap.put("resultMesg", resc.get(0).getStatusText()!=null?resc.get(0).getStatusText():"");
					
				}else{
					//作失败处理
					resultMap.put("resultCode", "exce");
					resultMap.put("resultMesg", resc.get(0).getStatusText()!=null?resc.get(0).getStatusText():"");
				}
			}else{
				//作失败处理
				resultMap.put("resultCode", "exce");
				resultMap.put("resultMesg", resCommon.getStatusText()!=null?resCommon.getStatusText():"");
			}
			
		} catch (Exception e) {
			resultMap.put("resultCode", "exce");
			resultMap.put("resultMesg", "查询解冻交易状态发生异常.");
			logger.error("查询解冻交易状态发生异常."+e.getMessage());
		}
		logger.info("结束查询解冻交易状态："+resultMap);
		return resultMap;
	}

}
