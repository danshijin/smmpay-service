package com.smmpay.payment;

import java.util.Map;

/**
 * 每日结算
 * 
 * @author zengshihua
 *
 */
public interface EveryDaySettlementService {

	public Map<String, Object> settlement(Map<String, Object> paramMap)throws Exception;

}
