package com.smmpay.process.enumDef;

/**
 * 
 * 解冻类型
 * 
 * @author zengshihua
 *
 */
public enum UNfreezeType {

	F0("0", "强制解冻"),
	F1("1", "解冻支付");

	private String code;
	private String message;

	private UNfreezeType(String code, String message) {
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
