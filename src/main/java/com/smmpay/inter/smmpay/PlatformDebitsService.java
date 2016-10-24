package com.smmpay.inter.smmpay;

import java.util.Map;

import com.smmpay.inter.dto.res.ReturnDTO;

/**
 * 查询指定账户交易记录
 * @author 
 *
 */
public interface PlatformDebitsService {

	ReturnDTO platDebits(Map<String,String> map);

	ReturnDTO platformDebits(Map<String,String> map)throws Exception;
}
