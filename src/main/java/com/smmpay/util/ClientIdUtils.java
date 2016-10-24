package com.smmpay.util;

import java.util.Date;
/**
 * 
 * 生成不重复ClientId
 * 
 * @author zengshihua
 *
 */
public class ClientIdUtils {

	public static String CreateClientId() {
		
		return DateUtil.doFormatDate(new Date(), "yyyyMMddHHmmssSSS");
		
	}
	
}
