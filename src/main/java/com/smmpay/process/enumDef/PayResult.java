package com.smmpay.process.enumDef;

/**
 * 
 * 支付结果
 * 
 * @author zengshihua
 *
 */
public enum PayResult {

	P0("0", "成功"),
	P1("1", "失败"),
	P2("2", "未知");

	private String code;
	private String message;

	private PayResult(String code, String message) {
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
