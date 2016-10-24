package com.smmpay.respository.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.smmpay.respository.model.base.ResCommon;

/**
 * 商户附属账户交易明细查询
 * 
 * 响应报文
 * 
 * @author zengshihua
 * 
 */
public class ResDlstrndt extends ResCommon implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -401966836612129229L;

	/**
	 * 返回记录条数
	 */
	private Integer returnRecords;
	
	/**
	 * ROW
	 */
	private List<ResDlstrndtRow> list=new ArrayList<ResDlstrndtRow>();
	
	
	public Integer getReturnRecords() {
		return returnRecords;
	}

	public void setReturnRecords(Integer returnRecords) {
		this.returnRecords = returnRecords;
	}

	public List<ResDlstrndtRow> getList() {
		return list;
	}

	public void setList(List<ResDlstrndtRow> list) {
		this.list = list;
	}
	
	

	@Override
	public String toString() {
		return "ResDlstrndt [list=" + list + "]";
	}

}
