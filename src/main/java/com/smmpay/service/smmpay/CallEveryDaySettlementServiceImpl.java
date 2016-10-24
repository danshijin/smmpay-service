package com.smmpay.service.smmpay;

import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.smmpay.common.ResErrorCode;
import com.smmpay.inter.dto.res.ReturnDTO;
import com.smmpay.inter.smmpay.CallEveryDaySettlementService;
import com.smmpay.payment.EveryDaySettlementService;

/**
 * 每日结算
 * 
 * @author zengshihua
 *
 */
@Service("callEveryDaySettlementService")
public class CallEveryDaySettlementServiceImpl implements CallEveryDaySettlementService{
	
	private Logger logger=Logger.getLogger(this.getClass());
	
	@Autowired
	private EveryDaySettlementService everyDaySettlementService;
	
	public ReturnDTO settlement(Map<String, Object> paramMap) throws Exception {
		
		logger.info("--------每日结算-------入参："+paramMap);
		/**
		 * 验证参数
		 */
		ReturnDTO returnDTO = new ReturnDTO();
		String msg=validParam(paramMap);
		if(msg!=null){
			returnDTO.setStatus(ResErrorCode.RETURN_EXCE_CODE);
			returnDTO.setMsg(msg);
			return returnDTO;
		}
		
		Map<String, Object> result=everyDaySettlementService.settlement(paramMap);
		
		//if(result.containsKey("resultCode")&&"success".equals(result.get("resultCode"))){
		returnDTO.setStatus(ResErrorCode.RETURN_SUCCESS_CODE);
		returnDTO.setMsg(ResErrorCode.RETURN_SUCCESS_MSG);
		returnDTO.setData(result);
		
		return returnDTO;
	}
	/**
	 * 入参:结算日期startDate(yyyyMMdd)
	 * @throws Exception 
	 * 
	 */
	public String validParam(Map<String, Object> param){
		if(param==null){
			return "参数为空";
		}
		if(!param.containsKey("startDate")||"".equals(param.get("startDate"))){
			return "startDate不能为空";
		}
		
		return null;
	}

}
