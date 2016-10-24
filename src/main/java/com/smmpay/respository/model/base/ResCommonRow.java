package com.smmpay.respository.model.base;

import java.io.Serializable;

/**
 * 
 * 公共 响应报文
 * 
 * @author zengshihua
 * 
 */
public class ResCommonRow implements Serializable {

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
	
	/**
	 * 状态标志 char(1) 0 成功 1 失败 2未知 3审核拒绝 4 用户撤销--
	 */
	
	private String stt;

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

	public String getStt() {
		return stt;
	}

	public void setStt(String stt) {
		this.stt = stt;
	}
}
