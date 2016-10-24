package com.smmpay.payment;

import java.util.Map;
/**
 * 解冻支付
 * @author zengshihua
 *
 */
public interface ThawPayService {

	/**
	 * 后台审核付款流程(后台交易审核完成，点击解冻支付)
	 */
	public Map<String, Object> autoTthawPayHandle(Map<String, Object> paramMap) throws Exception;
	
	/**
	 * 手动解冻支付（自动解冻支付失败、后台手动重发，对应“确认转账”动作）
	 */
	public Map<String, Object> manualThawPayHandle(Map<String, Object> paramMap) throws Exception;
	
	public Map<String, Object> manualTransferHandle(Map<String, Object> paramMap) throws Exception;
}
