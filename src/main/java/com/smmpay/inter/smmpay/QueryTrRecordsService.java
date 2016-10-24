package com.smmpay.inter.smmpay;

import java.util.Map;

import com.smmpay.inter.dto.res.PagReturnDTO;

/**
 * 查询指定账户交易记录
 * @author 
 *
 */
public interface QueryTrRecordsService {

	PagReturnDTO queryTrRecords(Map<String,String> map);
}
