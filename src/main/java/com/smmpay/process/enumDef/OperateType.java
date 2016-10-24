package com.smmpay.process.enumDef;

/**
 * 
 * 异常操作类型
 * 
 * @author zengshihua
 *
 */
public enum OperateType {

	O1("1", "支付冻结"),
	O2("2", "查询余额"),
	O3("3", "每日结算"),
	O4("4", "出金"),
	O01("01", "比对余额正常"),
	O02("02", "比对余额异常常"),
	O03("03", "每日结算-比对余额异常");

	private String code;
	private String message;

	private OperateType(String code, String message) {
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
