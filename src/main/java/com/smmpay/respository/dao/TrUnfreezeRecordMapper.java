package com.smmpay.respository.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.smmpay.respository.model.TrUnfreezeRecord;

public interface TrUnfreezeRecordMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(TrUnfreezeRecord record);

    int insertSelective(TrUnfreezeRecord record);

    TrUnfreezeRecord selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(TrUnfreezeRecord record);

    int updateByPrimaryKey(TrUnfreezeRecord record);
    
    List<TrUnfreezeRecord> queryUnfreezeRecordAll(Map<String, Object> param);
    
    /**
     * 修改解冻状态
     * @param record
     * @return
     */
    Integer updateUNFreezeStatus(TrUnfreezeRecord trUnfreezeRecord);
    
    
    Integer queryUNFreezeStatus(Map<String, Object> param);
    
    int addVeriyCode(@Param(value="id") int id, @Param(value="verifyCode") String verifyCode);
    
    String getVerify(int id);
}