package com.smmpay.respository.dao;

import java.util.List;

import com.smmpay.respository.model.TrCashApplyRecord;

public interface TrCashApplyRecordMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(TrCashApplyRecord record);

    int insertSelective(TrCashApplyRecord record);

    TrCashApplyRecord selectByPrimaryKey(Integer id);

    List<TrCashApplyRecord> selectByStatus(Integer status);

    int updateByPrimaryKeySelective(TrCashApplyRecord record);

    int updateByPrimaryKey(TrCashApplyRecord record);

    int selectByCashApplyId(Integer cashApplyId);
    
    Integer queryCashApplyRecordByStatus(TrCashApplyRecord record);

    Integer countRecordByUserIdAndStatus(Integer userId);
}