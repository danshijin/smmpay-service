package com.smmpay.inter.dto.res;

import java.io.Serializable;

/**
 * 
 * @author zengshihua
 *
 */
public class PagReturnDTO implements Serializable{

    private static final long serialVersionUID = 6042238835149095913L;
    private String status = "000000";
    private boolean success = true;
    private String msg="处理成功";
    private Object data;
    private Integer returnRecords;

    public PagReturnDTO(){
    	
    }
    public PagReturnDTO(String status,boolean success,String msg){
    	this.status = status;
    	this.success = success;
    	this.msg = msg;
    }
    public PagReturnDTO(String status,boolean success,String msg,Object data){
    	this(status,success,msg);
    	this.data = data;
    	
    }
    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }
	public Integer getReturnRecords() {
		return returnRecords;
	}
	public void setReturnRecords(Integer returnRecords) {
		this.returnRecords = returnRecords;
	}
    
    
}
