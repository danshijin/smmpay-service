package com.smmpay.respository.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.smmpay.respository.model.base.ResCommon;

/**
 * 非登录打印明细查询
 * 
 * 响应报文
 * 
 * @author zengshihua
 * 
 */
public class ResDlptdtqy extends ResCommon implements Serializable {

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
	private List<ResDlptdtqyRow> list=new ArrayList<ResDlptdtqyRow>();
	
	
	public Integer getReturnRecords() {
		return returnRecords;
	}

	public void setReturnRecords(Integer returnRecords) {
		this.returnRecords = returnRecords;
	}

	public List<ResDlptdtqyRow> getList() {
		return list;
	}

	public void setList(List<ResDlptdtqyRow> list) {
		this.list = list;
	}
	
	

	@Override
	public String toString() {
		return "ResDlstrndt [list=" + list + "]";
	}

}
