package com.smmpay.respository.dao;

import org.apache.ibatis.annotations.Param;

import com.smmpay.respository.model.TrCashApply;

public interface TrCashApplyMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(TrCashApply record);

    int insertSelective(TrCashApply record);

    TrCashApply selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(TrCashApply record);

    int updateByPrimaryKey(TrCashApply record);
    
    int addVerifyCode(@Param(value="id") int id, @Param(value="verifyCode") String verifyCode);
    
    String getVerify(int id);
}