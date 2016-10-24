package com.smmpay.respository.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.smmpay.respository.model.base.ResCommon;

/**
 * 
 * 商户附属账户冻结信息查询
 * 
 * 响应报文
 * 
 * @author zengshihua
 * 
 */
public class ResDlsfrzqr extends ResCommon implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	
	private List<ResDlsfrzqrRow> list=new ArrayList<ResDlsfrzqrRow>();
	

	public List<ResDlsfrzqrRow> getList() {
		return list;
	}

	public void setList(List<ResDlsfrzqrRow> list) {
		this.list = list;
	}
	
	

}
