package com.smmpay.service.smmpay;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.smmpay.inter.dto.res.ReturnDTO;
import com.smmpay.inter.smmpay.OrderService;
import com.smmpay.internal.service.TOrderService;

@Service("orderService")
public class OrderServiceImpl implements OrderService {

	@Autowired
	private TOrderService torderService;
	/**
	 * 插入支付订单
	 */
	public ReturnDTO insertPayOrder(Map<String, String> map){
		
		return torderService.insertPayOrder(map);
	}

}
