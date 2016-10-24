package com.smmpay.respository.model;

import java.io.Serializable;

import com.smmpay.respository.model.base.ReqCommon3;

/**
 * 
 * 交易状态查询 
 * 请求报文
 * 
 * @author zengshihua
 *
 */
public class ReqDlcidstt extends ReqCommon3 implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	/**
	 * 请求码
	 */
	private final String  action="DLCIDSTT";
	
	/**
	 * 登录名
	 */
	//private String  userName;
	
	/**
	 * 客户流水号
	 */
	private String  clientID;
	
	/**
	 * 	原请求代码char(8)，可空，若客户能保证各交易类型的流水号唯一，则可空，否则需上送原请求代码。  
	 * 	资金初始化：DLFNDINI 
	 * 	调账入款：DLTRSFIN 
	 * 	错账调回：DLWFDRTN 
	 * 	入金：DLFONDIN 
	 * 	出金：DLFNDOUT 
	 * 	保证金退还：DLGTYRTN 
	 * 	转账：DLSUBTRN 
	 * 	强制转账：DLMDETRN
	 */
	private String  type;
	
	/*public ReqDlcidstt(){
		
		action="DLCIDSTT";
		userName="YSWTEST";
		clientID="20004";
		type="DLMDETRN";
		
	}*/
	
	public String getAction() {
		return action;
	}
	
	/*public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}*/
	public String getClientID() {
		return clientID;
	}
	public void setClientID(String clientID) {
		this.clientID = clientID;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	
}
