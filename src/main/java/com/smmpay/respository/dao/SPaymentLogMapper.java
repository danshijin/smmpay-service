package com.smmpay.respository.dao;

import com.smmpay.respository.model.SPaymentLog;

public interface SPaymentLogMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(SPaymentLog record);

    int insertSelective(SPaymentLog record);

    SPaymentLog selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(SPaymentLog record);

    int updateByPrimaryKey(SPaymentLog record);
}