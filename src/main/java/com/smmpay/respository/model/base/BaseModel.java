package com.smmpay.respository.model.base;

public class BaseModel implements java.io.Serializable{

	private static final long serialVersionUID = 1335083614493974948L;
	//用户登录
	private String userName;
	//主体账号
	private String accountNo;
	//action名称
	private String actionName;
	public BaseModel(){}
    public BaseModel(String actionName,String userName,String accountNo){
    	this.userName = userName;
    	this.accountNo = accountNo;
    }
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public String getAccountNo() {
		return accountNo;
	}
	public void setAccountNo(String accountNo) {
		this.accountNo = accountNo;
	}
	public String getActionName() {
		return actionName;
	}
	public void setActionName(String actionName) {
		this.actionName = actionName;
	}
	
	
}
