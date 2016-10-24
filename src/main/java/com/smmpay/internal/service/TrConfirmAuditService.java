package com.smmpay.internal.service;


import com.smmpay.inter.dto.res.TrConfirmAuditDTO;

public interface TrConfirmAuditService{
  public int insertTrConfirmAudit(TrConfirmAuditDTO auditDTO) throws Exception;
}
