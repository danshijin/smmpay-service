package com.smmpay.inter.dto.req;

import java.io.Serializable;

@SuppressWarnings("serial")
public class CancelAccountDTO implements Serializable{

	/**
	 * 附属账号
	 */
	private String subAccNo;

	public String getSubAccNo() {
		return subAccNo;
	}

	public void setSubAccNo(String subAccNo) {
		this.subAccNo = subAccNo;
	}
}
