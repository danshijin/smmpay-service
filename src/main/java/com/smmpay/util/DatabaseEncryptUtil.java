package com.smmpay.util;

import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

/**
* @author  zhaoyutao
* @version 2015年12月21日 下午2:20:46
* 数据库中数据添加加密验证字段
*/

public class DatabaseEncryptUtil {
	
	private static Logger log = Logger.getLogger(DatabaseEncryptUtil.class);
	/** 对参数进行加密后返回
	 * @param paramList
	 * @return
	 */
	public static String encrypt(List<Object> paramList){
		String cipherText = "";
		DecimalFormat df=new java.text.DecimalFormat("0.00");
		try {
			String plainText = "";
			for(Object param : paramList){
				String str = "";
				if(param instanceof BigDecimal){
					BigDecimal temp = (BigDecimal) param;
					str = df.format(temp);
				} else {
					str = String.valueOf(param);
				}
				log.info(plainText);
				plainText += str;
			}
			log.info("plainText:"+plainText);
			log.info("DESkEY: "+DesCrypt.desStr);
			
			cipherText =  Base64.getBase64(DesCrypt.encryptMode(plainText.toString().getBytes("utf-8")));
			
			log.info("cipherText:"+cipherText);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return cipherText;
	}
	/** 
	 * @param calculate 通过计算获取的密文
	 * @param database 数据库中存储的密文
	 * @return true 相等， false databaseCipher为空或者不相等
	 */
	public static boolean compareCiphertext(String calculateCipher, String databaseCipher){
		return StringUtils.isNotBlank(databaseCipher) && calculateCipher.equals(databaseCipher);
	}
	
	
}
