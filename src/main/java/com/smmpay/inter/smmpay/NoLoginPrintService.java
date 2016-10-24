package com.smmpay.inter.smmpay;

import java.util.Map;

import com.smmpay.inter.dto.res.ReturnDTO;

public interface NoLoginPrintService {
	
	/**
	 * 非登录打印明细查询
	 * @param map
	 * @return
	 */
	public ReturnDTO queryTrRecords(Map<String,String> map);
	
	
}
