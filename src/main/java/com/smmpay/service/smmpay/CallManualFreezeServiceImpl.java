package com.smmpay.service.smmpay;

import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.smmpay.common.ResErrorCode;
import com.smmpay.inter.dto.res.ReturnDTO;
import com.smmpay.inter.smmpay.CallManualFreezeService;
import com.smmpay.payment.ManualFreezeService;
import com.smmpay.payment.ThawPayService;

/**
 *  支付冻结手动处理(自动冻结失败,后台重发支付冻结)
 * 
 */
@Service("callManualFreezeService")
public class CallManualFreezeServiceImpl implements CallManualFreezeService {

	private Logger logger=Logger.getLogger(this.getClass());
	
	@Autowired
	private ManualFreezeService manualFreezeService;
	
	@Autowired
	private ThawPayService thawPayService;

	public ReturnDTO manualFreezeHandle(Map<String, Object> paramMap) throws Exception {
		
		logger.info("--------开始调用支付冻结手动处理-------入参："+paramMap);
		/**
		 * 验证参数
		 */
		ReturnDTO returnDTO = new ReturnDTO();
		
		if(paramMap==null||!paramMap.containsKey("paymentId")){
			returnDTO.setMsg(ResErrorCode.VALIDPARAM_ERROR_MSG);
			returnDTO.setStatus(ResErrorCode.VALIDPARAM_ERROR_CODE);
			return returnDTO;
		}
		
		try {
			
			Map<String, Object> result=manualFreezeService.manualFreezeHandle(paramMap);
			
			if("success".equals(result.get("resultCode"))){
				returnDTO.setMsg(String.valueOf(result.get("resultMesg")));
				returnDTO.setStatus(ResErrorCode.RETURN_SUCCESS_CODE);
			}else{
				returnDTO.setMsg(String.valueOf(result.get("resultMesg")));
				returnDTO.setStatus(ResErrorCode.VALIDPARAM_ERROR_CODE);
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return returnDTO;
	}
	
	public ReturnDTO manualTransferHandle(Map<String, Object> paramMap) throws Exception{

		logger.info("--------开始调用资金解冻平台转账流程-------入参："+paramMap);
		/**
		 * 验证参数
		 */
		ReturnDTO returnDTO = new ReturnDTO();
		
		if(paramMap==null||!paramMap.containsKey("paymentId")){
			returnDTO.setMsg(ResErrorCode.VALIDPARAM_ERROR_MSG);
			returnDTO.setStatus(ResErrorCode.VALIDPARAM_ERROR_CODE);
			return returnDTO;
		}
		
		try {
			paramMap.put("isTransfer", "1");
			Map<String, Object> result=thawPayService.manualTransferHandle(paramMap);
			
			if("success".equals(result.get("resultCode"))){
				returnDTO.setMsg(String.valueOf(result.get("resultMesg")));
				returnDTO.setStatus(ResErrorCode.RETURN_SUCCESS_CODE);
			}else{
				returnDTO.setMsg(String.valueOf(result.get("resultMesg")));
				returnDTO.setStatus(ResErrorCode.VALIDPARAM_ERROR_CODE);
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return returnDTO;
	}
}
