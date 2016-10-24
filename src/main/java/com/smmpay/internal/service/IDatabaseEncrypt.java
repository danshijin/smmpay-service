package com.smmpay.internal.service;

/**
* @author  zhaoyutao
* @version 2015年12月22日 上午11:05:17
* 类说明
*/

public interface IDatabaseEncrypt {


	/** 解冻记录表添加加密字段
	 * @param unFRecord
	 */
	void addUnFreezeVerifyCode(Integer unFRecordId);

	/** 冻结记录表添加加密字段
	 * @param fRecord
	 */
	void addFreezeVerifyCode(Integer fRecordId);

	/** 出金申请表添加加密字段
	 * @param trCashApply
	 */
	void addCashApplyVerifyCode(Integer trCashApplyId);

	boolean validFreezeVerifyCode(int freezeId);

	boolean validUnFreezeVerifyCode(int id);

}
