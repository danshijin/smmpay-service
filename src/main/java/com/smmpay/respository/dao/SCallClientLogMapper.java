package com.smmpay.respository.dao;

import java.util.List;

import com.smmpay.respository.model.SCallClientLog;

public interface SCallClientLogMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(SCallClientLog record);

    int insertSelective(SCallClientLog record);

    SCallClientLog selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(SCallClientLog record);

    int updateByPrimaryKey(SCallClientLog record);
    
    List<SCallClientLog> findAllListByType(SCallClientLog log);
}