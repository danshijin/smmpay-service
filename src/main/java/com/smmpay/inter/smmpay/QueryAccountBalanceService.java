package com.smmpay.inter.smmpay;

import java.util.Map;

import com.smmpay.inter.dto.res.ReturnDTO;

/**
 *  查询指定账户余额
 * @author 
 *
 */
public interface QueryAccountBalanceService {

	ReturnDTO queryAccountBalance(Map<String,String> map);
}
