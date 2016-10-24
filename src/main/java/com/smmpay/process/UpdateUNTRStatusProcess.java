package com.smmpay.process;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.smmpay.process.enumDef.ReqResult;
import com.smmpay.process.enumDef.ReqType;
import com.smmpay.respository.dao.TrFreezeRecordMapper;
import com.smmpay.respository.dao.TrUnfreezeRecordMapper;
import com.smmpay.respository.dao.UserMoneyExceptionMapper;
import com.smmpay.respository.dao.UserPayChannelMapper;
import com.smmpay.respository.model.ReqDlcidstt;
import com.smmpay.respository.model.TrFreezeRecord;
import com.smmpay.respository.model.TrPaymentRecord;
import com.smmpay.respository.model.TrUnfreezeRecord;
import com.smmpay.respository.model.UserMoneyException;
import com.smmpay.respository.model.base.ResCommon;
import com.smmpay.util.CPayUtils;
import com.smmpay.util.XMLUtils;
/**
 * 
 * 修改冻结记录冻结状态
 * 
 * @author zengshihua
 *
 */
public class UpdateUNTRStatusProcess extends StepProcessor {

	
	private Logger logger=Logger.getLogger(UpdateUNTRStatusProcess.class);
	
	private TrUnfreezeRecordMapper trUnfreezeRecordMapper;
	
	private TrUnfreezeRecord trUnfreezeRecord;
	
	
	protected UpdateUNTRStatusProcess() {
		super();
	}

	protected UpdateUNTRStatusProcess(Map<String, Object> paramMap) {
		super(paramMap);
	}

	protected UpdateUNTRStatusProcess(Map<String, Object> paramMap,
			StepProcessor nextProcessor) {
		super(paramMap, nextProcessor);
	}

	public static UpdateUNTRStatusProcess getInstanceWithParam(TrUnfreezeRecordMapper trUnfreezeRecordMapper,
			TrUnfreezeRecord trUnfreezeRecord) {

		//Map<String, Object> paramMap = buildParam(null);
		
		UpdateUNTRStatusProcess process = new UpdateUNTRStatusProcess(null);
		process.trUnfreezeRecordMapper=trUnfreezeRecordMapper;
		process.trUnfreezeRecord=trUnfreezeRecord;
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
			
			Integer count=trUnfreezeRecordMapper.updateUNFreezeStatus(trUnfreezeRecord);
			if(count>0){
				resultMap.put("resultCode", "success");
				resultMap.put("resultMesg", "成功");
			}else{
				throw new Exception();
			}
			
		} catch (Exception e) {
			resultMap.put("resultCode", "exce");
			resultMap.put("resultMesg", "修改冻结记录状态发生异常.");
			logger.error("修改冻结记录状态生异常.",e);
		}
		
		logger.info("结束修改冻结记录状态："+resultMap);
		return resultMap;
	}

}
