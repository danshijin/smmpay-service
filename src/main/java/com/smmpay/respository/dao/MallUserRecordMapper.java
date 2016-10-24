package com.smmpay.respository.dao;

import com.smmpay.respository.model.MallUserRecord;

public interface MallUserRecordMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(MallUserRecord record);

    int insertSelective(MallUserRecord record);

    MallUserRecord selectByPrimaryKey(Integer id);

    MallUserRecord selectByCode(String code);

    int updateByPrimaryKeySelective(MallUserRecord record);

    int updateByPrimaryKey(MallUserRecord record);
}