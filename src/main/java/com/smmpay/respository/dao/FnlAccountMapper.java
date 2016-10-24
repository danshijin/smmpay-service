package com.smmpay.respository.dao;

import com.smmpay.respository.model.FnlAccount;

public interface FnlAccountMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(FnlAccount record);

    int insertSelective(FnlAccount record);

    FnlAccount selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(FnlAccount record);

    int updateByPrimaryKey(FnlAccount record);
}