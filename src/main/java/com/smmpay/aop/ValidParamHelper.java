package com.smmpay.aop;

import java.io.File;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.smmpay.common.ResErrorCode;
import com.smmpay.inter.dto.res.ReturnDTO;
import com.smmpay.server.ServiceAppStart;
import com.smmpay.util.Base64;
import com.smmpay.util.CertificateCoder;
import com.smmpay.util.Constant;
import com.smmpay.util.DesCrypt;
import com.smmpay.util.MD5;


/**
 * Created by tangshulei on 2015/11/5.
 */
@Component
@Aspect
public class ValidParamHelper{

    private Logger log = Logger.getLogger(this.getClass());

    @Pointcut("execution(public * com.smmpay.service.smmpay.*.*(..))")
    public void sleeppoint(){
    }

    @Around("sleeppoint()")
    public Object process(ProceedingJoinPoint point) throws Throwable {
        System.out.println("around-------------");
        Object[] args = point.getArgs();
        Map<String, String> map = null;
        if (args != null && args.length > 0 && args[0] instanceof java.util.Map) {
            map = (Map<String, String>) args[0];
        }
        Map<String,String> jsonMap = null;
        try {
        	if(map != null) jsonMap = validParam(map);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if(jsonMap == null || jsonMap.size() <= 0){
            ReturnDTO returnDTO = new ReturnDTO();
            returnDTO.setMsg(ResErrorCode.DECRYPTPARAM_ERROR_MSG);
            returnDTO.setSuccess(false);
            returnDTO.setStatus(ResErrorCode.DECRYPTPARAM_ERROR_CODE);
            return returnDTO;
        }
        if(jsonMap != null )  args[0] = jsonMap;
        Object returnValue = point.proceed(args);
        return returnValue;
    }

    @Before("sleeppoint()")
    public void beforeSleep(){
       // System.out.println("睡觉前要脱衣服!");
    }

    @AfterReturning("sleeppoint()")
    public void afterSleep(){
      //  System.out.println("睡醒了要穿衣服！");
    }

    /**
     * 所有的验证处理
     */
    public Map<String,String> validParam(Map<String,String> map) throws Exception {
        try {
            String token = map.get("token");
            String[] token_obj = token.split("&");
            //验证访问权限是否过期
            if (isTokenValid(token_obj[0], token_obj[1], token_obj[2])) {
                String dKey = map.get("dKey");
                String jsonStr = map.get("jsonStr");
                String md5key = map.get("MD5Str");
                String sign = map.get("sign");
                //验证MD5
                String md5DesCrypt = MD5.md5(dKey + jsonStr);
                if ((md5DesCrypt.equals(md5key))) {
                	log.info("smm_pay_" + Constant.CER_PATH + ".cer");
                    byte[] dKeyDecrypt = CertificateCoder.decryptByPublicKey(Base64.getFromBase64(dKey), "smm_pay_" + Constant.CER_PATH + ".cer");
                   
                    DesCrypt.desStr = new String(dKeyDecrypt);
                    log.info("desStr:" + DesCrypt.desStr);
                    byte[] jsonStrDecrypt = DesCrypt.decryptMode(Base64.getFromBase64(jsonStr));
                    String jsonStrDe = new String(jsonStrDecrypt,"utf-8");
                    log.info("jsonStrDecrypt:" + jsonStrDe);
                    
                    ObjectMapper mapper = new ObjectMapper();
                    LinkedHashMap mapParam = mapper.readValue(jsonStrDe, LinkedHashMap.class);

                    Map<String,String> json =((List<Map<String,String>>)mapParam.get("data")).get(0);
                    String verify_sign = "";
                    for (String key : json.keySet()) {
                        verify_sign += json.get(key);
                    }
                    log.info("sign"+verify_sign);
                    boolean isSign = CertificateCoder.verify((verify_sign).getBytes("utf-8"), sign, "smm_pay_" + Constant.CER_PATH + ".cer");
                    log.info("veryfySign:" + isSign);
                    //验证签名
                    if (isSign) {
                    	return json;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
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
}
