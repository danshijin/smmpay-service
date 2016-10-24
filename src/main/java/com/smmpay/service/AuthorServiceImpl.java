package com.smmpay.service;

import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONObject;
import com.smmpay.inter.AuthorService;
import com.smmpay.inter.dto.res.ReturnDTO;
import com.smmpay.internal.service.TOrderService;
import com.smmpay.server.ServiceAppStart;
import com.smmpay.service.base.BaseService;

@Service("authorService")
public class AuthorServiceImpl extends BaseService implements AuthorService{

	@Autowired
	TOrderService orderService;
	public String getToken(String key,String id) {
		Map<String,String> map = new LinkedHashMap<String,String>();
	    String isValid = "0";
	    String expireTime = "";
		//if(ServiceAppStart.keyMap.get(key) != null){
			if(ServiceAppStart.keyMap.containsValue(key) && ServiceAppStart.keyMap.containsValue(id)){
				//系统判断有无令牌记录，没有则创建新的令牌，有则返回原有信
				//数据库查询开--------
				
				
				//数据库查询结----------
				
				isValid = "1";
				expireTime = String.valueOf(new Date().getTime());			
			}
		//}
		map.put("uuid", String.valueOf((int)(Math.random() * 1000000000)));
		map.put("accessToken", "access_token");
		map.put("isValid", isValid);
		map.put("expireTime", expireTime);
		return JSONObject.toJSONString(map);
	}
	

	public boolean isInvalid() {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean isOpen(){
		ReturnDTO dto = orderService.getMarketStatus();
		if(dto.getStatus().equals("000000")) return true;
		return false;
	}
	public static void main(String args[]){
		System.out.println((int)(Math.random() * 1000000000));
	}
	
}
