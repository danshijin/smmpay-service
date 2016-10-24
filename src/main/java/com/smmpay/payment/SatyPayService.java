package com.smmpay.payment;

import java.util.Map;

public interface SatyPayService {

	/**
	 * 余额查询
	 */
	public Map<String, Object> satyPayHandle() throws Exception;
	
}
