package com.smmpay.respository.dao;

import com.smmpay.respository.model.FnlRoleRelation;

public interface FnlRoleRelationMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(FnlRoleRelation record);

    int insertSelective(FnlRoleRelation record);

    FnlRoleRelation selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(FnlRoleRelation record);

    int updateByPrimaryKey(FnlRoleRelation record);
}