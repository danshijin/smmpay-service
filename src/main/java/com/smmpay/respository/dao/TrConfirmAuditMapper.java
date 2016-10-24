package com.smmpay.respository.dao;

import com.smmpay.respository.model.TrConfirmAudit;

public interface TrConfirmAuditMapper {
    int deleteByPrimaryKey(Integer confirmId);

    int insert(TrConfirmAudit record);

    int insertSelective(TrConfirmAudit record);

    TrConfirmAudit selectByPrimaryKey(Integer confirmId);

    int updateByPrimaryKeySelective(TrConfirmAudit record);

    int updateByPrimaryKey(TrConfirmAudit record);
    
    TrConfirmAudit selectBySelective(TrConfirmAudit record);
}