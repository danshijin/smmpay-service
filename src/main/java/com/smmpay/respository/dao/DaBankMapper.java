package com.smmpay.respository.dao;

import java.util.List;

import com.smmpay.respository.model.DaBank;

public interface DaBankMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(DaBank record);

    int insertSelective(DaBank record);

    DaBank selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(DaBank record);

    int updateByPrimaryKey(DaBank record);
    
    List<DaBank> getBankByCity(DaBank daBank);
}