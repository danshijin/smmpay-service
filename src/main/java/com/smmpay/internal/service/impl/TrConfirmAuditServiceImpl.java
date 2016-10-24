package com.smmpay.internal.service.impl;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.smmpay.inter.dto.res.TrConfirmAuditDTO;
import com.smmpay.internal.service.TrConfirmAuditService;
import com.smmpay.respository.dao.TrConfirmAuditMapper;
import com.smmpay.respository.model.TrConfirmAudit;
import com.smmpay.service.base.BaseService;

/**
 * Created by wanghao on 2015/11/11.
 */
@Service("confirmService")
@Transactional
public class TrConfirmAuditServiceImpl extends BaseService implements TrConfirmAuditService {

	private Logger log = Logger.getLogger(this.getClass());
    @Autowired
    TrConfirmAuditMapper trConfirmAuditMapper;

    
    @Transactional(rollbackFor = Exception.class)
    public int insertTrConfirmAudit(TrConfirmAuditDTO auditDTO) throws Exception{
    	try{
    		TrConfirmAudit audit = copy(auditDTO,TrConfirmAudit.class);
    		TrConfirmAudit auditRepeat = new TrConfirmAudit();
    		log.info("auditRepeat paymentId"+auditRepeat.getPaymentId());
    		auditRepeat.setPaymentId(audit.getPaymentId());
    		auditRepeat.setAuditStatus(0);
    		auditRepeat = trConfirmAuditMapper.selectBySelective(auditRepeat);
    		if(auditRepeat == null){
    			int rows = trConfirmAuditMapper.insertSelective(audit);
        		log.info("insert rows:"+rows);
        		return rows;
    		}
    	}catch(Exception e){
    		e.printStackTrace();
    		log.error(e);
    		throw e;
    	}
    	return 0;
    }
}
