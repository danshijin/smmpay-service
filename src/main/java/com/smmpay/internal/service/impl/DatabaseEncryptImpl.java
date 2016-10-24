package com.smmpay.internal.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.smmpay.internal.service.IDatabaseEncrypt;
import com.smmpay.respository.dao.TrCashApplyMapper;
import com.smmpay.respository.dao.TrFreezeRecordMapper;
import com.smmpay.respository.dao.TrUnfreezeRecordMapper;
import com.smmpay.respository.model.TrCashApply;
import com.smmpay.respository.model.TrFreezeRecord;
import com.smmpay.respository.model.TrUnfreezeRecord;
import com.smmpay.util.DatabaseEncryptUtil;

/**
* @author  zhaoyutao
* @version 2015年12月22日 上午11:05:37
* 数据库关键数据 行加密
*/
@Service
public class DatabaseEncryptImpl implements IDatabaseEncrypt {
	
	private Logger logger = Logger.getLogger(DatabaseEncryptImpl.class);
	
	@Autowired
	private TrUnfreezeRecordMapper trUnfreezeRecordMapper;
	
	@Autowired
	private TrFreezeRecordMapper trFreezeRecordMapper;
	
	@Autowired
    private TrCashApplyMapper trCashApplyMapper;
	
	public void addUnFreezeVerifyCode(Integer unFRecordId){
		
		TrUnfreezeRecord unFRecord = trUnfreezeRecordMapper.selectByPrimaryKey(unFRecordId);
		List<Object> paramList = new ArrayList<Object>();
		paramList.add(unFRecord.getFreezeId());
		paramList.add(unFRecord.getUnfreezeClientId());
		paramList.add(unFRecord.getUnfreezeApplyTime());
		logger.info("解冻记录表添加 verifyCode，id：" + unFRecordId + "，待加密字符串数组为：" + paramList.toString());
		String databaseCipher = DatabaseEncryptUtil.encrypt(paramList);
		logger.info("解冻记录表，verifyCode：" + databaseCipher);
		trUnfreezeRecordMapper.addVeriyCode(unFRecordId, databaseCipher);
		logger.info("添加成功");
	}
	
	public boolean validUnFreezeVerifyCode(int id){
		TrUnfreezeRecord record = trUnfreezeRecordMapper.selectByPrimaryKey(id);
		List<Object> paramList = new ArrayList<Object>();
		paramList.add(record.getFreezeId());
		paramList.add(record.getUnfreezeClientId());
		paramList.add(record.getUnfreezeApplyTime());
		logger.info("解冻记录表比对 verifyCode，id：" + id + "，待验证字符串数组为：" + paramList.toString());
		String calculateCipher = DatabaseEncryptUtil.encrypt(paramList);
		String databaseCipher = trUnfreezeRecordMapper.getVerify(id);
		logger.info("calculateCipher："+ calculateCipher +"， databaseCipher：" + databaseCipher);
		return DatabaseEncryptUtil.compareCiphertext(calculateCipher, databaseCipher);
	}
	
	public void addFreezeVerifyCode(Integer fRecordId){
		TrFreezeRecord fRecord = trFreezeRecordMapper.getMd5Params(fRecordId);
		List<Object> paramList = new ArrayList<Object>();
		paramList.add(fRecord.getPaymentId());
		paramList.add(fRecord.getFreezeMoney());
		paramList.add(fRecord.getFreezeClientId());
		paramList.add(fRecord.getApplyTime());
		logger.info("冻结记录表添加 verifyCode，freezeId：" + fRecordId + "，待加密字符串数组为：" + paramList.toString());
		String databaseCipher = DatabaseEncryptUtil.encrypt(paramList);
		logger.info("冻结记录表，verifyCode：" + databaseCipher);
		trFreezeRecordMapper.addVerifyCode(fRecordId, databaseCipher);
		logger.info("tr_freeze_record 添加成功");
	}
	
	public boolean validFreezeVerifyCode(int freezeId){
		TrFreezeRecord record = trFreezeRecordMapper.getMd5Params(freezeId);
		List<Object> paramList = new ArrayList<Object>();
		paramList.add(record.getPaymentId());
		paramList.add(record.getFreezeMoney());
		paramList.add(record.getFreezeClientId());
		paramList.add(record.getApplyTime());
		logger.info("冻结记录表比对 verifyCode，freezeId：" + freezeId + "，待验证字符串数组为：" + paramList.toString());
		String calculateCipher = DatabaseEncryptUtil.encrypt(paramList);
		String databaseCipher = trFreezeRecordMapper.getVerify(freezeId);
		logger.info("calculateCipher："+ calculateCipher +"， databaseCipher：" + databaseCipher);
		return DatabaseEncryptUtil.compareCiphertext(calculateCipher, databaseCipher);
	}

	public void addCashApplyVerifyCode(Integer trCashApplyId){
		TrCashApply trCashApply = trCashApplyMapper.selectByPrimaryKey(trCashApplyId);
		List<Object> paramList = new ArrayList<Object>();
		paramList.add(trCashApply.getUserId());
		paramList.add(trCashApply.getCashMoney());
		paramList.add(trCashApply.getApplyTime());
		logger.info("出金申请表添加 verifyCode，id：" + trCashApplyId + "，待加密字符串数组为：" + paramList.toString());
		String databaseCipher = DatabaseEncryptUtil.encrypt(paramList);
		logger.info("出金申请表，verifyCode：" + databaseCipher);
		trCashApplyMapper.addVerifyCode(trCashApplyId, databaseCipher);
		logger.info("添加成功");
	}
}
