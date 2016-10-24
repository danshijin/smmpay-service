package com.smmpay.process.enumDef;
/**
 * 
 * @author zengshihua
 *
 */
public enum PaymentSatus {
	//支付状态 0 待付款 1 已冻结 2 已完成 3 已关闭 4,冻结失败，5，解冻支付失败，6，待退款 7，已退款 8，退款失败,9,冻结中，10，付款中，11，退款中',
	
	PS0(0, "待付款"),
	PS1(1, "已冻结 "),
	PS2(2, "已完成"),
	PS3(3, "已关闭"),
	PS4(4, "冻结失败"),
	PS5(5, "解冻支付失败"),
	PS6(6, "待退款"),
	PS7(7, "已退款 "),
	PS8(8, "退款失败"),
	PS9(9, "冻结中"),
	PS10(10, "付款中"),
	PS11(11, "退款中");

	private Integer code;
	private String message;

	private PaymentSatus(Integer code, String message) {
		this.code = code;
		this.message = message;
	}

	public Integer getCode() {
		return code;
	}

	public void setCode(Integer code) {
		this.code = code;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}
}
