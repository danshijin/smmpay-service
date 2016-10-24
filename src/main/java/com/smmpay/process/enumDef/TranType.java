package com.smmpay.process.enumDef;

/**
 * 
 * 交易类型
 * 
 * @author zengshihua
 *
 */
public enum TranType {

	T23("23", "普通外部转账"),
	T1("D", "转出"),
	T2("C", "转入"),
	BF("BF", "转账"),
	BG("BG", "解冻"),
	BH("BH", "解冻支付"),
	BR("BR", "冻结"),
	BS("BS", "支付冻结");

	private String code;
	private String message;

	private TranType(String code, String message) {
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
