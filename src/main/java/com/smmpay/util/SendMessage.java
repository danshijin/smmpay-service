package com.smmpay.util;

import java.io.InputStream;
import java.security.Security;
import java.util.Date;
import java.util.Properties;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class SendMessage {
	 private static Properties serverProps; 
	    
	    private static final String SSL_FACTORY = "javax.net.ssl.SSLSocketFactory"; 
	    private static final String SMTP_HOST = "smtp.163.com"; // smtp 服务器地址 
	    private static final int SMTP_PORT = 465; // smtp 服务器端口 
	    
	    private static final String FROM_ADDR = "woohoo1987@163.com"; // 邮件的发送者地址 
	    private static final String USERNAME = "woohoo1987@163.com"; // 用户名 
	    private static final String PASSWORD = "wh@xp19878787"; // 密码 
	    
	    static { 
	        serverProps = new Properties(); 
	        serverProps.setProperty("mail.smtp.host", SMTP_HOST); 
	        serverProps.setProperty("mail.smtp.socketFactory.class", SSL_FACTORY);
	        serverProps.setProperty("mail.smtp.socketFactory.fallback", "false"); 
	        serverProps.setProperty("mail.smtp.port", String.valueOf(SMTP_PORT)); 
	        serverProps.setProperty("mail.smtp.socketFactory.port", String.valueOf(SMTP_PORT)); 
	        serverProps.put("mail.smtp.auth", "true"); 
	    } 

	    /**
	     * 文本类型的发送
	     * @param email
	     * @param subject
	     * @param text
	     * @throws MessagingException
	     */
		public static void send(String email, String subject, String  text) throws MessagingException {
			Security.addProvider(new com.sun.net.ssl.internal.ssl.Provider());
			Session session = Session.getDefaultInstance(serverProps, new Authenticator() {

				@Override
				protected PasswordAuthentication getPasswordAuthentication() {
					return new PasswordAuthentication(USERNAME, PASSWORD);
				}
			});
			Message msg = new MimeMessage(session);
			msg.setFrom(new InternetAddress(FROM_ADDR));
			msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse(email, false));
			msg.setContent(text, "text/html;charset=gb2312");
			msg.setSubject(subject);
			msg.setSentDate(new Date());
			Transport.send(msg);
		}
		
		/**
		 * html类型的发送
		 * @param email
		 * @param subject
		 * @param html
		 * @throws MessagingException
		 */
		public static void sendHtml(String email,String subject,String html)throws MessagingException {
			Security.addProvider(new com.sun.net.ssl.internal.ssl.Provider());
			Session session = Session.getDefaultInstance(serverProps, new Authenticator() {


	    /**
	      * @param args
	      * @throws Exception 
	      */
//	     public static void main(String[] args) throws Exception {
//	         
////	    	 Security.addProvider(new com.sun.net.ssl.internal.ssl.Provider()); 
////	    	 Session session = Session.getDefaultInstance(serverProps, new Authenticator() { 
////
////	                @Override 
////	                protected PasswordAuthentication getPasswordAuthentication() { 
////	                    return new PasswordAuthentication(USERNAME, PASSWORD); 
////	                } 
////	        }); 
////	         Message msg = new MimeMessage(session); 
////	         msg.setFrom(new InternetAddress(FROM_ADDR)); 
////	         msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse("woohoo1987@163.com", false)); 
////	         msg.setSubject("dfdf"); 
////	         msg.setText("fdfdf"); 
////	         msg.setSentDate(new Date()); 
////	         Transport.send(msg); 
//	    	 InputStream is = ClassLoader.getSystemResourceAsStream("MailTemplete.html");
//		        byte[] readByte = new byte[is.available()];
//			    is.read(readByte);
//			    String mailContent = new String(readByte,"utf-8");
//			    SendMessage.send("woohoo1987@163.com", "付款成功", mailContent);
//	     }

			@Override
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(USERNAME, PASSWORD);
				}
			});
			
			
			Message msg = new MimeMessage(session);
			msg.setFrom(new InternetAddress(FROM_ADDR));
			msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse(email, false));
			msg.setSubject(subject);
			msg.setContent(html.toString(), "text/html;charset=utf-8");
			msg.setSentDate(new Date());
			Transport.send(msg);
			
			
		}
		
		

//	    /**
//	      * @param args
//	      * @throws Exception 
//	      */
//	     public static void main(String[] args) throws Exception {
//	         
//	    	 Security.addProvider(new com.sun.net.ssl.internal.ssl.Provider()); 
//	    	 Session session = Session.getDefaultInstance(serverProps, new Authenticator() { 
//
//	                @Override 
//	                protected PasswordAuthentication getPasswordAuthentication() { 
//	                    return new PasswordAuthentication(USERNAME, PASSWORD); 
//	                } 
//	        }); 
//	         Message msg = new MimeMessage(session); 
//	         msg.setFrom(new InternetAddress(FROM_ADDR)); 
//	         msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse("mr.youxs@foxmail.com", false)); 
//	         msg.setSubject("dfdf"); 
//	         msg.setContent("<a href='http://mail.qq.com'>33333</a>".toString(), "text/html;charset=utf-8");
//	         msg.setSentDate(new Date()); 
//	         Transport.send(msg); 
//	     }
		
		
		public static void main(String[] args) {
			try {
				InputStream is = ClassLoader
						.getSystemResourceAsStream("MailTemplete.html");
				byte[] readByte = new byte[is.available()];
				is.read(readByte);
				String mailContent = new String(readByte, "utf-8");
				mailContent =  mailContent.replace("$paymentId","1");
				mailContent =  mailContent.replace("$dealMoney","100");
				mailContent =  mailContent.replace("$payCode","10000");
				mailContent =  mailContent.replace("$sellerCompanyName", "775778510@qq.com");
				mailContent =  mailContent.replace("$paymentType","在线支付");
				mailContent =  mailContent.replace("$sellerAccNo","mr.youxs@qq.com");//对方账号
				mailContent =  mailContent.replace("$createTime", "2015-11-18");
				mailContent =  mailContent.replace("$productName", "铜交易");
				mailContent =  mailContent.replace("$freezeClientId", "22");//资金流水号
				mailContent =  mailContent.replace("$productDetail","这笔很重要");
				mailContent =  mailContent.replace("$productNum","29");
				mailContent =  mailContent.replace("$dealMoney", "20000");
				mailContent =  mailContent.replace("$orderCreateTime", "2015-11-18 22:10:22");
				mailContent =  mailContent.replace("$settlementType", "会员");
				mailContent =  mailContent.replace("$dealType", "测试");
				mailContent =  mailContent.replace("$invoice", "223424");
				mailContent =  mailContent.replace("$paymentId","22");
				    SendMessage.sendHtml("mr.youxs@foxmail.com","付款成功", mailContent);
			} catch (Exception e) {
				// TODO: handle exception
			}
			
	}

}
