package com.smmpay.inter.dto.res;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import com.smmpay.respository.model.ResDlstrndtRow;

public class ResPlatformDebitsDTO implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = -7784725725853546421L;

	/**
	 * 交易状态
	 */
	private String status;
	/**
	 * 交易状态信息
	 */
	private String statusText;
	
	
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getStatusText() {
		return statusText;
	}
	public void setStatusText(String statusText) {
		this.statusText = statusText;
	}
}
