package com.smmpay.payment;

import java.util.Map;
/**
 * 支付冻结手动处理(自动冻结失败,后台重发支付冻结)
 * @author zengshihua
 *
 */
public interface ManualFreezeService {

	/**
	 * 支付冻结手动处理
	 */
	public Map<String, Object> manualFreezeHandle(Map<String, Object> paramMap) throws Exception;


}
