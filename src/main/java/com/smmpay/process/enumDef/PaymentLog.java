package com.smmpay.process.enumDef;

/**
 * 
 * 支付类型
 * 
 * @author zengshihua
 *
 */
public enum PaymentLog {

	P0("P0", "创建交易记录"),
	P1("P1", "检查账户异常"),
	P2("P2", "请求冻结买方货款"),
	P3("P3", "买方贷款冻结"),
	P4("P4", "买方支付请求审核"),
	P5("P5", "请求解冻支付"),
	P6("P6", "解冻支付完成");

	private String code;
	private String message;

	private PaymentLog(String code, String message) {
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
