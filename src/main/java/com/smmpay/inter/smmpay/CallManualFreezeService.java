package com.smmpay.inter.smmpay;

import java.util.Map;

import com.smmpay.inter.dto.res.ReturnDTO;
/**
 * 支付冻结手动处理(自动冻结失败,后台重发支付冻结)
 * @author zengshihua
 *
 */
public interface CallManualFreezeService {

	/**
	 * 支付冻结手动处理
	 */
	public ReturnDTO manualFreezeHandle(Map<String, Object> paramMap) throws Exception;
	
	/**
	 * 手动转账
	 * @param paramMap
	 * @return
	 * @throws Exception
	 */
	public ReturnDTO manualTransferHandle(Map<String, Object> paramMap) throws Exception;

}
