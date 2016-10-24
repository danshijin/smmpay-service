package com.smmpay.service.smmpay;

import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.smmpay.common.ResErrorCode;
import com.smmpay.inter.dto.res.ReturnDTO;
import com.smmpay.inter.smmpay.CallThawPayService;
import com.smmpay.payment.ThawPayService;

/**
 * 后台审核付款流程(后台交易审核完成，点击解冻支付)
 * 
 */
@Service("callThawPayService")
public class CallThawPayServiceImpl implements CallThawPayService {

	private Logger logger=Logger.getLogger(this.getClass());
	
	@Autowired
	private ThawPayService thawPayService;

	public ReturnDTO autoTthawPayHandle(Map<String, Object> paramMap) throws Exception {
		
		logger.info("--------解冻支付-------入参："+paramMap);
		
		/**
		 * 验证参数
		 */
		ReturnDTO returnDTO = new ReturnDTO();
		
		try {
			String msg=validParam(paramMap);
			if(msg!=null){
				returnDTO.setStatus(ResErrorCode.RETURN_EXCE_CODE);
				returnDTO.setMsg(msg);
				return returnDTO;
			}
			Map<String, Object> result=thawPayService.autoTthawPayHandle(paramMap);
			
			if(result!=null&&result.containsKey("resultCode")
					&&"success".equals(result.get("resultCode"))){
				returnDTO.setStatus(ResErrorCode.RETURN_SUCCESS_CODE);
				returnDTO.setMsg(ResErrorCode.RETURN_SUCCESS_MSG);
			}else{
				returnDTO.setStatus(ResErrorCode.RETURN_EXCE_CODE);
				returnDTO.setMsg(String.valueOf(result.get("resultMesg")));
			}
			
		} catch (Exception e) {
			returnDTO.setStatus(ResErrorCode.RETURN_EXCE_CODE);
			returnDTO.setMsg(ResErrorCode.RETURN_EXCE_MSG);
			e.printStackTrace();
		}
		
		return returnDTO;
	}

	public ReturnDTO manualThawPayHandle(Map<String, Object> paramMap) throws Exception {
		logger.info("--------手动解冻支付-------入参："+paramMap);
		/**
		 * 验证参数
		 */
		ReturnDTO returnDTO = new ReturnDTO();
		try {
			
			String msg=validParam(paramMap);
			if(msg!=null){
				returnDTO.setStatus(ResErrorCode.RETURN_EXCE_CODE);
				returnDTO.setMsg(msg);
				return returnDTO;
			}
			
			Map<String, Object> result=thawPayService.manualThawPayHandle(paramMap);
			
			if(result!=null&&result.containsKey("resultCode")
					&&"success".equals(result.get("resultCode"))){
				returnDTO.setStatus(ResErrorCode.RETURN_SUCCESS_CODE);
				returnDTO.setMsg(ResErrorCode.RETURN_SUCCESS_MSG);
			}else{
				returnDTO.setStatus(ResErrorCode.RETURN_EXCE_CODE);
				returnDTO.setMsg(result.get("resultMesg")!=null?String.valueOf(result.get("resultMesg")):"");
			}
			
		} catch (Exception e) {
			returnDTO.setMsg(ResErrorCode.RETURN_EXCE_MSG);
			returnDTO.setStatus(ResErrorCode.RETURN_EXCE_CODE);
			e.printStackTrace();
		}
		return returnDTO;
	}
	
	/**
	 * 手动转账
	 */
	public ReturnDTO manualTransferHandle(Map<String,Object> paramMap) throws Exception{
		
		logger.info("--------手动转账-------入参："+paramMap);
		/**
		 * 验证参数
		 */
		ReturnDTO returnDTO = new ReturnDTO();
		try {
			
			String msg=validParam(paramMap);
			if(msg!=null){
				returnDTO.setStatus(ResErrorCode.RETURN_EXCE_CODE);
				returnDTO.setMsg(msg);
				return returnDTO;
			}
		
			Map<String, Object> result=thawPayService.manualTransferHandle(paramMap);
			logger.info("执行手动转账流程>>>>>>>>,出参:"+result);
			if(result!=null&&result.containsKey("resultCode")
					&&"success".equals(result.get("resultCode"))){
				returnDTO.setStatus(ResErrorCode.RETURN_SUCCESS_CODE);
				returnDTO.setMsg(ResErrorCode.RETURN_SUCCESS_MSG);
			}else{
				returnDTO.setStatus(ResErrorCode.RETURN_EXCE_CODE);
				returnDTO.setMsg(result.get("resultMesg")!=null?String.valueOf(result.get("resultMesg")):"");
			}
			
		} catch (Exception e) {
			returnDTO.setMsg(ResErrorCode.RETURN_EXCE_MSG);
			returnDTO.setStatus(ResErrorCode.RETURN_EXCE_CODE);
			e.printStackTrace();
		}
		return returnDTO;
	}
	/**
	 * 入参：审核人(auditUserId)，审核时间(auditTime)，支付记录(paymentId)
	 *     
	 */
	public String validParam(Map<String, Object> param){
		if(param==null){
			return "参数为空";
		}
		/*if(param.containsKey("auditUserId")||"".equals(param.get("auditUserId"))){
			return "auditUserId不能为空";
		}
		if(param.containsKey("auditTime")||"".equals(param.get("auditTime"))){
			return "auditTime不能为空";
		}*/
		if(!param.containsKey("paymentId")||"".equals(param.get("paymentId"))){
			return "paymentId不能为空";
		}
		/*if(param.containsKey("confirmId")||"".equals(param.get("confirmId"))){
			return "confirmId不能为空";
		}*/
		return null;
	}
}
