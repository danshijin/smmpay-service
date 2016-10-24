package com.smmpay.respository.dao;

import com.smmpay.respository.model.DaPayChannel;


public interface DaPayChannelMapper {
    int deleteByPrimaryKey(Integer payChanneId);

    int insert(DaPayChannel record);

    int insertSelective(DaPayChannel record);

    DaPayChannel selectByPrimaryKey(Integer payChanneId);

    int updateByPrimaryKeySelective(DaPayChannel record);

    int updateByPrimaryKey(DaPayChannel record);
    
    //获取默认渠道
    DaPayChannel selectDefault();
}