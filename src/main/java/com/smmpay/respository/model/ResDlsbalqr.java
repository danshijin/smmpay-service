package com.smmpay.respository.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.smmpay.respository.model.base.ResCommon;

/**
 * 商户附属账户余额查询 
 * 
 * 说明：商户查询附属账户体系内附属账户的余额信息。 
 * 
 * 响应报文
 * 
 * @author zengshihua
 * 
 */
public class ResDlsbalqr extends ResCommon implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4234030995444549796L;

	/**
	 * 余额明细
	 */
	public List<ResDlsbalqrRow> list = new ArrayList<ResDlsbalqrRow>();


	public List<ResDlsbalqrRow> getList() {
		return list;
	}

	public void setList(List<ResDlsbalqrRow> list) {
		this.list = list;
	}

}
