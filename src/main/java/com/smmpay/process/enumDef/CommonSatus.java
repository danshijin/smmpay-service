package com.smmpay.process.enumDef;
/**
 * 
 * @author zengshihua
 *
 */
public enum CommonSatus {
	
	CS0(0, "请求中"),
	CS1(1, "成功"),
	CS2(2, "失败");

	private Integer code;
	private String message;

	private CommonSatus(Integer code, String message) {
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
