package com.smmpay.respository.model.base;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 
 * 公共 响应报文
 * 
 * @author zengshihua
 * 
 */
public class ResCommon implements Serializable {

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
