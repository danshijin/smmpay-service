package com.smmpay.respository.dao;

import com.smmpay.respository.model.UserBindBank;

import java.util.List;

public interface UserBindBankMapper {
    int deleteByPrimaryKey(Integer bindId);

    int insert(UserBindBank record);

    int insertSelective(UserBindBank record);

    UserBindBank selectByPrimaryKey(Integer bindId);

    int updateByPrimaryKeySelective(UserBindBank record);

    int updateByPrimaryKey(UserBindBank record);

    Integer countUserBindBank(UserBindBank record);

    Integer countUserBindBankForList(UserBindBank record);

    Integer countUserBindBankAll(UserBindBank record);

    int updateCheckMoney(Integer bindId);

    Integer countUserBindBankByStatus(Integer userId);

    List<UserBindBank> selectUserBindBankByUserId(UserBindBank userBindBank);
}