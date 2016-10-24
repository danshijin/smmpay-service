package com.smmpay.util;

import com.smmpay.respository.dao.SPaymentLogMapper;
import com.smmpay.respository.model.SPaymentLog;

public class SPaymentLogUtils {

	public static void write (SPaymentLog pLog,SPaymentLogMapper pLogMapper) {
		pLog.setCreateTime(DateUtil.getFormatDate());
		pLog.setOperationTime(DateUtil.getFormatDate());
		pLogMapper.insertSelective(pLog);
	}
}
