package com.smmpay.inter.smmpay;

import java.util.Map;

import com.smmpay.inter.dto.res.ReturnDTO;

/**
 * 每日结算
 * 
 * @author zengshihua
 *
 */
public interface CallEveryDaySettlementService {

	public ReturnDTO settlement(Map<String, Object> paramMap)throws Exception;

}
