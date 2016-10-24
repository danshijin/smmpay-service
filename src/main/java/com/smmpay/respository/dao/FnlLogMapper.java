package com.smmpay.respository.dao;

import com.smmpay.respository.model.FnlLog;

public interface FnlLogMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(FnlLog record);

    int insertSelective(FnlLog record);

    FnlLog selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(FnlLog record);

    int updateByPrimaryKey(FnlLog record);
}