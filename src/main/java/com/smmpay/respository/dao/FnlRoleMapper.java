package com.smmpay.respository.dao;

import com.smmpay.respository.model.FnlRole;

public interface FnlRoleMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(FnlRole record);

    int insertSelective(FnlRole record);

    FnlRole selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(FnlRole record);

    int updateByPrimaryKey(FnlRole record);
}