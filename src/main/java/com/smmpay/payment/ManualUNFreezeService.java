package com.smmpay.payment;

import java.util.Map;

import com.smmpay.inter.dto.res.ReturnDTO;
/**
 * 手动解冻
 * @author zengshihua
 *
 */
public interface ManualUNFreezeService {

	/**
	 * 手动解冻
	 */
	public ReturnDTO manualUNFreezeHandle(Map<String, Object> paramMap) throws Exception;

	/**
	 * 是否有补价订单
	 */
	public ReturnDTO whetherBuJiaOrder(Map<String, Object> paramMap) throws Exception;
}
