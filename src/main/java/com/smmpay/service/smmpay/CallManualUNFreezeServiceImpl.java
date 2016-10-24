package com.smmpay.service.smmpay;

import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.smmpay.common.ResErrorCode;
import com.smmpay.inter.dto.res.ReturnDTO;
import com.smmpay.inter.smmpay.CallManualFreezeService;
import com.smmpay.inter.smmpay.CallManualUNFreezeService;
import com.smmpay.payment.ManualUNFreezeService;

/**
 *  手动解冻
 * 
 */
@Service("callManualUNFreezeService")
public class CallManualUNFreezeServiceImpl implements CallManualUNFreezeService {
	
	private Logger logger=Logger.getLogger(this.getClass());
	
	@Autowired
	private ManualUNFreezeService manualUNFreezeService;
	
	public ReturnDTO manualUNFreezeHandle(Map<String, Object> paramMap) throws Exception {
		
		logger.info("--------手动解冻-------入参："+paramMap);
		
		ReturnDTO returnDTO = new ReturnDTO();
		
		/**
		 * 验证参数
		 */
		if(paramMap==null||!paramMap.containsKey("paymentId")){
			
			returnDTO.setMsg(ResErrorCode.VALIDPARAM_ERROR_MSG);
			returnDTO.setStatus(ResErrorCode.VALIDPARAM_ERROR_CODE);
			return returnDTO;
		}
		
		returnDTO=manualUNFreezeService.manualUNFreezeHandle(paramMap);
		
		return returnDTO;
	}

	/**
	 * 是否有补价订单
	 */
	public ReturnDTO whetherBuJiaOrder(Map<String, Object> paramMap) throws Exception {
		logger.info("--------是否有补价订单-------入参："+paramMap);
		ReturnDTO returnDTO = new ReturnDTO();
		if(paramMap==null||!paramMap.containsKey("paymentId")){
			returnDTO.setMsg(ResErrorCode.VALIDPARAM_ERROR_MSG);
			returnDTO.setStatus(ResErrorCode.VALIDPARAM_ERROR_CODE);
			return returnDTO;
		}
		returnDTO=manualUNFreezeService.whetherBuJiaOrder(paramMap);
		return returnDTO;
	}	
}