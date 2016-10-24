package com.smmpay.internal.service;

import java.util.Map;

import com.smmpay.inter.dto.res.ReturnDTO;
import com.smmpay.inter.dto.res.TrPaymentRecordDTO;

public interface TOrderService {
	//插入支付订单
	public ReturnDTO insertPayOrder(Map<String, String> map);

	public TrPaymentRecordDTO findPaymentRecord(Integer primaryKey);
	
	public ReturnDTO getMarketStatus();
}
