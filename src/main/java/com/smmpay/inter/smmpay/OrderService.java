package com.smmpay.inter.smmpay;

import java.util.Map;

import com.smmpay.inter.dto.res.ReturnDTO;

public interface OrderService {
	//插入支付订单
	public ReturnDTO insertPayOrder(Map<String, String> map);

}
