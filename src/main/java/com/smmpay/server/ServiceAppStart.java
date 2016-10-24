package com.smmpay.server;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import org.apache.log4j.Logger;
import org.springframework.context.support.ClassPathXmlApplicationContext;


public class ServiceAppStart {
	 private static Logger log = Logger.getLogger(ServiceAppStart.class);
	 public static Map<String,String> keyMap = new HashMap<String,String>();
	 
	 static {
		 getProperties("AccessAuthory.properties");
	 }
	 
	 public static Properties getProperties(String fileName){
			InputStream is = ClassLoader.getSystemResourceAsStream(fileName);
			Properties pro = new Properties();
			try{
				pro.load(is);
				for(Map.Entry<Object, Object> env:pro.entrySet()){
					keyMap.put(String.valueOf(env.getKey()), String.valueOf(env.getValue()));
				    log.info("keyMap:"+env.getKey()+" & "+env.getValue());
				}
			}catch(Exception e){
				e.printStackTrace();
			}
			return pro;
	 }
	 
	 public static void main(String[] args) throws Exception {  
//		 SpringApplication app = new SpringApplication(ServiceAppStart.class);
//			//app.setWebEnvironment(true);
//			app.setShowBanner(false);
//			Set<Object> set = new HashSet<Object>();
//			set.add("classpath:service-application.xml");
//			set.add("classpath:oc-service-context.xml");
//			app.setSources(set);
//			app.run(args);
	        ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext(new String[] {"spring-context.xml","service-application.xml"});  
	        context.start();  
	      
	        System.in.read();  
	    }  
	
}
