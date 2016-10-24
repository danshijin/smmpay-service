package com.smmpay.inter.smmpay;

import java.util.Map;

import com.smmpay.inter.dto.res.ReturnDTO;
/**
 * 解冻支付
 * @author zengshihua
 *
 */
public interface CallThawPayService {

	/**
	 * 后台审核付款流程(后台交易审核完成，点击解冻支付)
	 */
	public ReturnDTO autoTthawPayHandle(Map<String, Object> paramMap) throws Exception;
	
	/**
	 * 手动解冻支付（自动解冻支付失败、后台手动重发，对应“确认转账”动作）
	 */
	public ReturnDTO manualThawPayHandle(Map<String, Object> paramMap) throws Exception;
}
