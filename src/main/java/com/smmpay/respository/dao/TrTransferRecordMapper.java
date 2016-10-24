package com.smmpay.respository.dao;

import java.util.List;
import java.util.Map;

import com.smmpay.respository.model.TrTransferRecord;

public interface TrTransferRecordMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(TrTransferRecord record);

    int insertSelective(TrTransferRecord record);

    TrTransferRecord selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(TrTransferRecord record);

    int updateByPrimaryKey(TrTransferRecord record);
    
    Integer queryStatus(Map<String, Object> paramMap);
    
    List<TrTransferRecord> queryList();
    
    TrTransferRecord queryTrRecordByFailure(Map<String, Object> paramMap);
}