package com.smmpay.respository.dao;

import com.smmpay.respository.model.SCheckBankRecord;

public interface SCheckBankRecordMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(SCheckBankRecord record);

    int insertSelective(SCheckBankRecord record);

    SCheckBankRecord selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(SCheckBankRecord record);

    int updateByPrimaryKey(SCheckBankRecord record);

    int selectByBindBankId(SCheckBankRecord record);
}