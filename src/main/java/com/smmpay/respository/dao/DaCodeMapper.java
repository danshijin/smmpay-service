package com.smmpay.respository.dao;

import java.util.List;

import com.smmpay.respository.model.DaCode;

public interface DaCodeMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(DaCode record);

    int insertSelective(DaCode record);

    DaCode selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(DaCode record);

    int updateByPrimaryKey(DaCode record);
    /**
     * 查询所有的主行
     * @return
     */
    List<DaCode> queryAllBank();

    DaCode selectDaCodeByCode(Integer code);
    
}