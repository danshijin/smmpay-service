package com.smmpay.respository.dao;

import com.smmpay.inter.dto.req.TradingRecordDTO;
import java.util.List;
import java.util.Map;

import com.smmpay.respository.model.TrPaymentRecord;

import java.util.List;

public interface TrPaymentRecordMapper {
    int deleteByPrimaryKey(Integer paymentId);

    int insert(TrPaymentRecord record);

    int insertSelective(TrPaymentRecord record);

    TrPaymentRecord selectByPrimaryKey(Integer paymentId);

    int updateByPrimaryKeySelective(TrPaymentRecord record);

    int updateByPrimaryKey(TrPaymentRecord record);

    List<TrPaymentRecord> selectForList(TradingRecordDTO tradingRecordDTO);

    int countForList(TradingRecordDTO tradingRecordDTO);
    
    List<TrPaymentRecord> queryPaymentRecord(Map<String, Object> paramMap);
    
    
    int updateHandleByKey(Integer paymentId);
    
    
    Integer updatePaymentStatus(TrPaymentRecord record);
    
    int insertNew(TrPaymentRecord record);
    
    List<TrPaymentRecord> selectBySelective(TrPaymentRecord record);
    
    String getPaymentCode(Map<String,Integer> map);
    
    int getMarketCode();
    
    TrPaymentRecord selectByPaymentNo(String paymentNo);
    
    List<TrPaymentRecord> selectByParam(TrPaymentRecord record);
    
}