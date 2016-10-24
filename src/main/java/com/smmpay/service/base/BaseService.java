package com.smmpay.service.base;

import java.io.File;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.beans.BeanUtils;

import com.alibaba.fastjson.JSONObject;
import com.smmpay.inter.dto.ChBuyPoolDTO;
import com.smmpay.respository.model.ChBuyPool;
import com.smmpay.server.ServiceAppStart;
import com.smmpay.util.Base64;
import com.smmpay.util.CertificateCoder;
import com.smmpay.util.Constant;
import com.smmpay.util.DesCrypt;
import com.smmpay.util.MD5;

public class BaseService {

	private Logger log = Logger.getLogger(this.getClass());
	public <T> T copy(Object s,Class<T> t) throws Exception{
		try{
			T tn = t.newInstance();
			BeanUtils.copyProperties(s, tn);
			return tn;
		}catch(Exception e){
			e.printStackTrace();
		}
		return null;
	}
	/**
	 * 获得公私钥证书地-
	 */
	public String getCertificatePath(String fileName){
		try{
			
			String filePath = this.getClass().getResource("/").getPath() + File.separator + fileName;
			return filePath;
		}catch(Exception e){
			e.printStackTrace();
		}
		return "";
	}
	
	/**
	 * 验证token是否过期
	 */
	public boolean isTokenValid(String key,String id,String expireTime){
		//if(ServiceAppStart.keyMap.get(key) != null){
			if(ServiceAppStart.keyMap.containsValue(key) && ServiceAppStart.keyMap.containsValue(id)){
				//
				for(Map.Entry<String, String> env:ServiceAppStart.keyMap.entrySet()){
					if(env.getValue().equals(key)){
						Constant.CER_PATH = env.getKey().replace("secretKey_", "").trim();
						break;
					}
				}
				return true;
			}
		//}
		return false;
	}
	
	/**
	 * 所有的验证处理
	 */
	public JSONObject validParam(Map<String,String> map) throws Exception{
		try{
			String token = map.get("token");
			String[] token_obj = token.split("&");
			//验证访问权限是否过期
			if(isTokenValid(token_obj[0], token_obj[1], token_obj[2])){
				String dKey = map.get("dKey");
				String jsonStr = map.get("jsonStr");
				String md5key = map.get("MD5Str");
				String sign = map.get("sign");
			    //验证MD5
				String md5DesCrypt = MD5.md5(dKey+jsonStr);
				if((md5DesCrypt.equals(md5key))){
					byte[] dKeyDecrypt = CertificateCoder.decryptByPublicKey(Base64.getFromBase64(dKey),getCertificatePath("smm_pay_"+Constant.CER_PATH+".cer"));
					DesCrypt.desStr = new String(dKeyDecrypt);
					log.info("desStr:"+DesCrypt.desStr);
					byte[] jsonStrDecrypt = DesCrypt.decryptMode(Base64.getFromBase64(jsonStr));
					String jsonStrDe = new String(jsonStrDecrypt);
					log.info("jsonStrDecrypt:"+jsonStrDe);
					
					JSONObject jsonData = JSONObject.parseObject(jsonStrDe);
					JSONObject json = jsonData.getJSONArray("data").getJSONObject(0);
					String verify_sign = "";
					for(String key : json.keySet()){
						verify_sign += json.get(key);
					}
					boolean isSign = CertificateCoder.verify((verify_sign).getBytes("utf-8"), sign, getCertificatePath("smm_pay_"+Constant.CER_PATH+".cer"));
					log.info("veryfySign:"+isSign);
					//验证签名
					if(isSign){
						return json;
					}
				}
			}
		}catch(Exception e){
			e.printStackTrace();
			throw e;
		}
		return null;
	}
}
