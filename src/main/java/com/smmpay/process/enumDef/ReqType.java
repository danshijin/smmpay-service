package com.smmpay.process.enumDef;

/**
 * 
 * 请求返回结果
 * 
 * @author zengshihua
 *
 */
public enum ReqType {

	DLMDETRN("DLMDETRN", "强制转账"),
	DLFCSOUT("DLFCSOUT", "平台出金");

	private String code;
	private String message;

	private ReqType(String code, String message) {
		this.code = code;
		this.message = message;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

}
