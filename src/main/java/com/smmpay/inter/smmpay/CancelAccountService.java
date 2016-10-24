package com.smmpay.inter.smmpay;

import java.util.Map;

import com.smmpay.inter.dto.res.ReturnDTO;

/**
 * 在线销户
 * @author zengshihua
 *
 */
public interface CancelAccountService {

	ReturnDTO cancelAccount(Map<String,String> map);
}
