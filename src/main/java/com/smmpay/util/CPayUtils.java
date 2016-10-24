package com.smmpay.util;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;

import org.dom4j.Document;
import org.dom4j.io.SAXReader;

/**
 * 发送报文至前置程序工具类
 * 
 * @author zengshihua
 * 
 */
public class CPayUtils {

	@SuppressWarnings("unused")
	public static String postRequest(String sendXml) {
		
		Document document = null;
		try {
			String url =PropertiesUtil.getValue("URL");
			URL sendUrl = new URL(url.trim());
			URLConnection connection = sendUrl.openConnection();

			connection.setConnectTimeout(60000);
			connection.setReadTimeout(60000);
			connection.setDoOutput(true);
			// 发送报文
			OutputStreamWriter out = new OutputStreamWriter(connection.getOutputStream(), "GBK");
			out.write(sendXml);

			out.flush();
			out.close();

			// 得到服务器的回应：
			InputStream is = connection.getInputStream();

			InputStreamReader isr = new InputStreamReader(is, "GBK");

			SAXReader saxReader = new SAXReader();
			document = saxReader.read(isr);
			return document.asXML();
			
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
}