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
import com.smmpay.respository.model.TrFreezeRecord;
import com.smmpay.respository.model.TrPaymentRecord;
import com.smmpay.respository.model.UserMoneyException;
import com.smmpay.respository.model.base.ResCommon;
import com.smmpay.util.CPayUtils;
import com.smmpay.util.XMLUtils;
/**
 * 
 * 修改冻结记录状态
 * 
 * @author zengshihua
 *
 */
public class UpdateTRStatusProcess extends StepProcessor {

	
	private Logger logger=Logger.getLogger(UpdateTRStatusProcess.class);
	
	private TrFreezeRecordMapper freezeRecordMapper;
	
	private TrFreezeRecord trFreezeRecord;
	
	
	protected UpdateTRStatusProcess() {
		super();
	}

	protected UpdateTRStatusProcess(Map<String, Object> paramMap) {
		super(paramMap);
	}

	protected UpdateTRStatusProcess(Map<String, Object> paramMap,
			StepProcessor nextProcessor) {
		super(paramMap, nextProcessor);
	}

	public static UpdateTRStatusProcess getInstanceWithParam(TrFreezeRecordMapper freezeRecordMapper,
			TrFreezeRecord trFreezeRecord) {

		//Map<String, Object> paramMap = buildParam(null);
		
		UpdateTRStatusProcess process = new UpdateTRStatusProcess(null);
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
	 * 组装参数返回
	 */
	@Override
	protected Map<String, Object> action(Map<String, Object> resultParamMap) {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		resultMap.put("resultCode", "exce");
		resultMap.put("resultMesg", "失败");
		try {
			trFreezeRecord.setFreezeStatus(2);//失败
			Integer count=freezeRecordMapper.updateFreezeStatus(trFreezeRecord);
			if(count>0){
				resultMap.put("resultCode", "success");
				resultMap.put("resultMesg", "成功");
			}else{
				throw new Exception();
			}
			
		} catch (Exception e) {
			resultMap.put("resultCode", "exce");
			resultMap.put("resultMesg", "修改冻结记录状态发生异常.");
			logger.error("修改冻结记录状态生异常."+e.getMessage());
		}
		
		logger.info("结束修改冻结记录状态："+resultMap);
		return resultMap;
	}

}
