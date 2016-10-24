package com.smmpay.respository.dao;

import java.util.List;

import com.smmpay.respository.model.UserMoneyException;

public interface UserMoneyExceptionMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(UserMoneyException record);

    int insertSelective(UserMoneyException record);

    UserMoneyException selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(UserMoneyException record);

    int updateByPrimaryKey(UserMoneyException record);
    
    List<UserMoneyException> queryUMExceByUserId(Integer id);
    
}