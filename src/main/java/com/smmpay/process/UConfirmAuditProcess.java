package com.smmpay.process;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import com.smmpay.respository.dao.TrConfirmAuditMapper;
import com.smmpay.respository.model.TrConfirmAudit;

/**
 * 
 * 更新交易付款申请状态为“已通过”，
 * 
 * 记录审核时间、审核人、审核状态
 * 
 * @author zengshihua
 * 
 */
public class UConfirmAuditProcess extends StepProcessor {

	private Logger logger = Logger.getLogger(UConfirmAuditProcess.class);

	private TrConfirmAuditMapper confirmAuditMapper;
	private TrConfirmAudit conAudit;

	protected UConfirmAuditProcess() {
		super();
	}

	protected UConfirmAuditProcess(Map<String, Object> paramMap) {
		super(paramMap);
	}

	protected UConfirmAuditProcess(Map<String, Object> paramMap,
			StepProcessor nextProcessor) {
		super(paramMap, nextProcessor);
	}

	public static UConfirmAuditProcess getInstanceWithParam(TrConfirmAuditMapper confirmAuditMapper, 
			TrConfirmAudit conAudit,
			Map<String, Object> param) {

		Map<String, Object> paramMap = buildParam(param);

		UConfirmAuditProcess process = new UConfirmAuditProcess(paramMap);
		process.confirmAuditMapper = confirmAuditMapper;
		process.conAudit=conAudit;
		return process;
	}

	@SuppressWarnings("unused")
	private static Map<String, Object> buildParam(Map<String, Object> param) {
		// 构建输入参数
		Map<String, Object> processParamMap = new HashMap<String, Object>();
		if (param != null) {
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

		Integer uPCount = confirmAuditMapper.updateByPrimaryKeySelective(conAudit);
		if (uPCount > 0) {
			resultMap.put("resultCode", "success");
			resultMap.put("resultMesg", "修改支付申请状态记录成功");
			logger.error("修改支付申请记录成功."+uPCount);
		} else {
			resultMap.put("resultCode", "exce");
			resultMap.put("resultMesg", "修改支付申请状态记录失败"+uPCount);
		}
		
		logger.info("结束修改支付申请状态：出参" + resultMap);
		return resultMap;
	}

}
