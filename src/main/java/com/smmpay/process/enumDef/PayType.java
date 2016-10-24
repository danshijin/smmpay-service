package com.smmpay.process.enumDef;

/**
 * 
 * 支付类型
 * 
 * @author zengshihua
 *
 */
public enum PayType {

	P0("0", "支付冻结"),
	P1("1", "解冻付款"),
	P2("2", "强制解冻");

	private String code;
	private String message;

	private PayType(String code, String message) {
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
