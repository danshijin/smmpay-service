package com.smmpay.respository.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.smmpay.respository.model.base.ResCommonRow;

/**
 * 
 * 交易状态查询
 *  响应报文
 * 
 * @author zengshihua
 *
 */
public class ResDlcidstt implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	/**
	 * 状态标志  char(1) 0 成功 1 失败 2未知 3审核拒绝 4 用户撤销
	 */
	private String  stt;
	
	/**
	 * 状态代码
	 */
	private String  status;
	
	/**
	 * 状态信息
	 */
	private String  statusText;

	
	private List<ResCommonRow> list=new ArrayList<ResCommonRow>();
	
	
	 
	public String getStt() {
		return stt;
	}

	public void setStt(String stt) {
		this.stt = stt;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getStatusText() {
		return statusText;
	}

	public void setStatusText(String statusText) {
		this.statusText = statusText;
	}

	public List<ResCommonRow> getList() {
		return list;
	}

	public void setList(List<ResCommonRow> list) {
		this.list = list;
	}
	
}
