package com.smmpay.respository.dao;

import com.smmpay.respository.model.SBankLog;

public interface SBankLogMapper {
    int deleteByPrimaryKey(Integer logId);

    int insert(SBankLog record);

    int insertSelective(SBankLog record);

    SBankLog selectByPrimaryKey(Integer logId);

    int updateByPrimaryKeySelective(SBankLog record);

    int updateByPrimaryKey(SBankLog record);
}