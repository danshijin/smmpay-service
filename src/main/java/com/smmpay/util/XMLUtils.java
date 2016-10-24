package com.smmpay.util;

import java.io.StringWriter;
import java.io.Writer;

import org.apache.log4j.Logger;

import com.smmpay.process.SynCreditRecordProcess;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.CompactWriter;
import com.thoughtworks.xstream.io.xml.DomDriver;

/**
 * 实体与XML互换
 * 
 * @author zengshihua
 * 
 */
public class XMLUtils {
	
	private static Logger logger=Logger.getLogger(XMLUtils.class);
	
	/**
	 * 实体转换成XML
	 * 
	 * @param bean
	 * @return
	 */
	public static String beanToXML(Object bean) {
		
		XStream xstream = new XStream(new DomDriver());
		xstream.alias("stream", bean.getClass());
		Writer writer = new StringWriter();
		xstream.marshal(bean, new CompactWriter(writer));
		String xml = "<?xml version=\"1.0\" encoding=\"GBK\"?>" + writer.toString();
		return xml;
	}

	/**
	 * XML转换成实体
	 * 
	 * @param bean
	 * @return
	 */
	public static Object xmlToBean(String xml,Class stream,Class row) {

		XStream xstream = new XStream(new DomDriver());
		xstream.alias("stream", stream);
		if(row!=null){
			xstream.alias("row", row);
		}
		Object bean=null;
		try {
			bean=xstream.fromXML(xml);
		} catch (Exception e) {
			logger.error("XML转换成实体，异常："+e.getMessage());
			e.printStackTrace();
		} 
		return bean;
	}
}
