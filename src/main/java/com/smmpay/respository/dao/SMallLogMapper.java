package com.smmpay.respository.dao;

import com.smmpay.respository.model.SMallLog;

public interface SMallLogMapper {
    int deleteByPrimaryKey(Integer logId);

    int insert(SMallLog record);

    int insertSelective(SMallLog record);

    SMallLog selectByPrimaryKey(Integer logId);

    int updateByPrimaryKeySelective(SMallLog record);

    int updateByPrimaryKey(SMallLog record);
}