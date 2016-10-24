package com.smmpay.respository.dao;

import com.smmpay.respository.model.MallUserRelation;

public interface MallUserRelationMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(MallUserRelation record);

    int insertSelective(MallUserRelation record);

    MallUserRelation selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(MallUserRelation record);

    int updateByPrimaryKey(MallUserRelation record);
}