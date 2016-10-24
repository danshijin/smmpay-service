package com.smmpay.inter.dto.res;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import com.smmpay.respository.model.ResDlsbalqrRow;

public class ResQueryAccountBalanceDTO implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4234030995444549796L;

	/**
	 * 附属账号
	 */
	public String subAccNo;
	/**
	 * 附属账户名称
	 */
	public String SUBACCNM;
	/**
	 * 透支额度
	 */
	public BigDecimal TZAMT;
	/**
	 * 实体账户可用资金
	 */
	public BigDecimal XSACVL;
	/**
	 * 可用余额
	 */
	public BigDecimal KYAMT;
	/**
	 * 实际余额
	 */
	public BigDecimal SJAMT;
	/**
	 * 冻结金额
	 */
	public BigDecimal DJAMT;

	public String getSubAccNo() {
		return subAccNo;
	}

	public void setSubAccNo(String subAccNo) {
		this.subAccNo = subAccNo;
	}

	public String getSUBACCNM() {
		return SUBACCNM;
	}

	public void setSUBACCNM(String sUBACCNM) {
		SUBACCNM = sUBACCNM;
	}

	public BigDecimal getTZAMT() {
		return TZAMT;
	}

	public void setTZAMT(BigDecimal tZAMT) {
		TZAMT = tZAMT;
	}

	public BigDecimal getXSACVL() {
		return XSACVL;
	}

	public void setXSACVL(BigDecimal xSACVL) {
		XSACVL = xSACVL;
	}

	public BigDecimal getKYAMT() {
		return KYAMT;
	}

	public void setKYAMT(BigDecimal kYAMT) {
		KYAMT = kYAMT;
	}

	public BigDecimal getSJAMT() {
		return SJAMT;
	}

	public void setSJAMT(BigDecimal sJAMT) {
		SJAMT = sJAMT;
	}

	public BigDecimal getDJAMT() {
		return DJAMT;
	}

	public void setDJAMT(BigDecimal dJAMT) {
		DJAMT = dJAMT;
	}
}
