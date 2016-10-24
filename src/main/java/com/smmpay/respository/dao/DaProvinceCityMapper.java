package com.smmpay.respository.dao;

import java.util.List;

import com.smmpay.respository.model.DaProvinceCity;

public interface DaProvinceCityMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(DaProvinceCity record);

    int insertSelective(DaProvinceCity record);

    DaProvinceCity selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(DaProvinceCity record);

    int updateByPrimaryKey(DaProvinceCity record);
    /**
     * 查询所有的省
     * @return
     */
    List<DaProvinceCity> selectProvinceAll();
    /**
     * 查询所有的市
     * @return
     */
    List<DaProvinceCity> selectCityAll(Integer parentId);
}