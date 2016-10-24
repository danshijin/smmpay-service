package com.smmpay.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.Map;

import org.apache.log4j.Logger;

import com.alibaba.fastjson.JSONObject;

public class HttpClient {

	private static Logger log = Logger.getLogger(HttpClient.class);
	
	/**
	 * 根据url得到http链接
	 * @param url
	 * @return
	 */
	protected static HttpURLConnection getConnection(String url){
		try{
			URL u = new URL(url);
			HttpURLConnection conn = (HttpURLConnection)u.openConnection();
			return conn;
		}catch(IOException e){
			e.printStackTrace();
		}
		return null;
	}
	/**
	 * 参数写入输入流
	 * @param mapParam
	 * @param conn
	 * @throws Exception
	 */
	protected static void writeParam(Map<String,String> mapParam,String jsonStr,URLConnection conn) throws Exception{
		conn.setDoInput(true);
		conn.setDoOutput(true);
		conn.setConnectTimeout(60000);
		conn.connect();
		OutputStream os = conn.getOutputStream();
		
		//拼装参数
		StringBuffer sb = new StringBuffer("");
		if(mapParam != null){
			int startIndex = 0;
			if(mapParam != null && mapParam.entrySet().size() > 0){
				for(Map.Entry<String, String> env : mapParam.entrySet()){
					if(startIndex == 0)  sb.append(env.getKey()+"="+env.getValue());
					else sb.append("&"+env.getKey()+"="+env.getValue());
					startIndex++;
				}
			}
		}
		
		if(jsonStr != null && !"".equals(jsonStr)){
			sb.append(jsonStr);
		}
		log.info("jsonStr :"+sb.toString());
		os.write(sb.toString().getBytes("utf-8"));
		os.close();
		os.flush();
	}
	
	/**
	 * 输出流得到结果
	 * @param conn
	 * @return
	 * @throws Exception
	 */
	protected static String getResult(URLConnection conn) throws Exception{
		try{
			InputStream is = conn.getInputStream();
			byte[] readByte = new byte[is.available()];
			
		    is.read(readByte);
		    return new String(readByte,"utf-8");
		}catch(Exception e){
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * 
	 * @param url
	 * @param mapParam
	 * @return
	 * @throws Exception
	 */
	public static String getResultFromMall(String url,Map<String,String> mapParam,String jsonStr) throws Exception{
		HttpURLConnection conn = getConnection(url);
		writeParam(mapParam, jsonStr,conn);
		return getResult(conn);
	}
	
	public static void main(String args[]){
		try{
			String str2 ="mallUserName=test01&paymentId=139&payCode=02bgnl26&auditStatus=2&auditTime=2015-11-17 15:10:02&date=1447744202&md5=448e67a80a0a5bbfc3e92928d648ac6c";
//			//String str3= "";
	//		String str3 = "mallUserName=woohoo1987@163.com&realName=fffff&email=872565143@qq.com&date=1447748795&md5=4f1b7ce78c9b99dd08b8c0fc5f9f77eb";
//			
			//String str4="dealMoney=0.01&dealType=直接购买&payType=0&mallOrderId=D52070022476&orderCreateTime=2015-12-02&buyerMallUserName=tanlifan&sellerMallUserName=smmxiaoxia&productName=1#电解铜&productNum=500&productNumUnit=吨&productDetail=1#电解铜500&invoice=货票同行&paymentType=安全支付&settlementType=买方自提&date=1449046462&md5=a98e5597de3535ec66bb6918a9d27e1d";
//			String str5 = "mallUserName=test01&date=1447748795&md5=a5fd3bc1edafcc95291064d6771dbb7c";
			//{"mallUserName":"maijiasunyuwei","paymentId":"316","payCode":"71jwfk90","mallAuditStatus":"1","mallAuditTime":"2015-12-07 15:33:04","mallAuditReason":"","date":"2016-03-21 15:33:22"}
			String str6 = "{\"code\":\"1\",\"data\":{\"mallOrderId\":\"D370800710002484\",\"paymentId\":\"316\",\"status\":\"1\"}}";
			URL url = new URL("http://testmall.smm.cn/Payment/apienter");
			HttpURLConnection conn =(HttpURLConnection)url.openConnection();
			conn.setDoInput(true);
			conn.setDoOutput(true);
			conn.connect();
			OutputStream os = conn.getOutputStream();
			//mapParamData.put("mallOrderId", paymentRecord.getMallOrderId());
			//mapParamData.put("paymentId", paymentRecord.getPaymentId().toString());
			//mapParamData.put("status", "1");
			//mapParam.put("data", JSONObject.toJSONString(mapParamData));
			log.info("str6:"+str6);
			os.write(str6.getBytes("utf-8"));
			os.close();
			os.flush();
			
			String result = getResult(conn);
			log.info("result:"+result);
		//	log.info("result:"+MD5.md5("woohoo1987@163.comfffff872565143@qq.com1447748795"));
			//log.info(MD5.md5("0.01直接购买0D520700224762015-12-02tanlifansmmxiaoxia1#电解铜500吨1#电解铜500货票同行安全支付买方自提1449046462"));
			//log.info(MD5.md5("test0113902bgnl2622015-11-17 15:10:021447744202"));
			//log.info(MD5.md5("maijiasunyuwei31671jwfk9012015-12-07 15:33:042016-03-21 15:33:22"));
//			
//		
			
			
		}catch(Exception e){
			e.printStackTrace();
		}
	}
}
