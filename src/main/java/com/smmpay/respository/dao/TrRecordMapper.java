package com.smmpay.respository.dao;

import java.util.List;
import java.util.Map;

import com.smmpay.respository.model.TrRecord;

public interface TrRecordMapper {
    int deleteByPrimaryKey(Integer trId);

    int insert(TrRecord record);

    int insertSelective(TrRecord record);

    TrRecord selectByPrimaryKey(Integer trId);

    int updateByPrimaryKeySelective(TrRecord record);

    int updateByPrimaryKey(TrRecord record);
    /**
     * 
     *获取最新入金记录时间
     *
     */
	public List<TrRecord> queryLastTrRecord(Map<String, Object> paramMap);
	
	/**
     * 
     *根据柜员交易号是否存在
     *
     */
	public List<TrRecord> queryTrRecordByTrNo(String payChannelTrNo);
	
	/**
	 * 保存入金记录
	 * @param records
	 * @return
	 */
	public int insertList(List<TrRecord> records);
	
	
	/**
     * 
     *查询所有出金请求中记录
     *
     */
	public List<TrRecord> queryTrRecordType3();
	
	/**
	 * 是否有请求中的出金记录
	 * @return
	 */
	public Integer  queryApplyStatus(Map<String, Object> paramMap);
	
	
}