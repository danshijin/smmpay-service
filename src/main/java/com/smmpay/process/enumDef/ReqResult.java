package com.smmpay.process.enumDef;

/**
 * 
 * 请求返回结果
 * 
 * @author zengshihua
 *
 */
public enum ReqResult {

	AAAAAAE("AAAAAAE", "已提交银行处理,请稍后使用交易状态查询交易进行查询"),
	PBRA001("PBRA001", "没有符合条件的记录"),
	AAAAAAA("AAAAAAA", "交易成功"),
	AAAAAAC("AAAAAAC", "预约成功"),
	ED02083("ED02083", "输入的客户流水号无制单信息,请检查输入项"),
	ED11308("ED11308", "接口调用太频繁"),
	ES88363("ES88363", "主机返回账户明细信息列表为空");

	private String code;
	private String message;

	private ReqResult(String code, String message) {
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
