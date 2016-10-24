package com.smmpay.util;

import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.InputStreamSource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Properties;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.internet.MimeUtility;

public class UtilMailWithAttach {
	
    private MimeMessage message;
    private Session session;
    private Transport transport;

    private String mailHost = "";
    private String sender_username = "";
    private String sender_password = "";

    private Properties properties = new Properties();

    /*
     * 初始化方法
     */
    public UtilMailWithAttach(boolean debug) {
//        InputStream in = UtilMailWithAttach.class.getResourceAsStream("MailServer.properties");
        try {
            InputStream in = ClassLoader.getSystemResourceAsStream("MailServer.properties");
            properties.load(in);
            this.mailHost = properties.getProperty("mail.smtp.host");
            this.sender_username = properties.getProperty("mail.sender.username");
            this.sender_password = properties.getProperty("mail.sender.password");
        } catch (IOException e) {
            e.printStackTrace();
        }

        session = Session.getInstance(properties);
        session.setDebug(debug);// 开启后有调试信息
        message = new MimeMessage(session);
    }

    public void sendPicMail(String subject, String htmlText, String receiveUser){
        try {
            JavaMailSender mailSender = new JavaMailSenderImpl();

            ((JavaMailSenderImpl)mailSender).setHost(this.mailHost);
            ((JavaMailSenderImpl)mailSender).setUsername(this.sender_username);
            ((JavaMailSenderImpl)mailSender).setPassword(this.sender_password);

            //配置文件，用于实例化java.mail.session
            Properties pro = System.getProperties();

            //登录SMTP服务器,需要获得授权，网易163邮箱新近注册的邮箱均不能授权。
            //测试 sohu 的邮箱可以获得授权
            pro.put("mail.smtp.auth", "true");
            pro.put("mail.smtp.socketFactory.port", "25");
            pro.put("mail.smtp.socketFactory.fallback", "false");

            ((JavaMailSenderImpl)mailSender).setJavaMailProperties(pro);

            MimeMessage mailMessage = mailSender.createMimeMessage();
            //false 是mulitpart类型 、true html格式
            MimeMessageHelper messageHelper = new MimeMessageHelper(mailMessage, true, "utf-8");

            messageHelper.setFrom("上海有色网<no-replay@smm.cn>");
            messageHelper.setTo(receiveUser);

            messageHelper.setSubject(subject);
            // true 表示启动HTML格式的邮件
            messageHelper.setText(htmlText, true);

            URL url = ClassLoader.getSystemResource("index-icon_1.png");

            final InputStream is = ClassLoader.getSystemResourceAsStream("index-icon_1.png");
            InputStreamSource iss = new InputStreamSource() {
                public InputStream getInputStream() throws IOException {
                    return is;
                }
            };
            messageHelper.addInline("IMG0", iss, null);//跟cid一致

//            FileSystemResource img = new FileSystemResource(new File(url.toString()));
//
//            messageHelper.addInline("IMG0", img);//跟cid一致
//
//            url = ClassLoader.getSystemResource("index-icon_2.png");
//
//            FileSystemResource img1 = new FileSystemResource(new File(url.toString()));
//
//            messageHelper.addInline("IMG1", img1);//跟cid一致
//
//            url = ClassLoader.getSystemResource("index-icon_3.png");
//
//            FileSystemResource img2 = new FileSystemResource(new File(url.toString()));
//
//            messageHelper.addInline("IMG2", img2);//跟cid一致

            mailSender.send(mailMessage);
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    /**
     * 发送邮件
     * 
     * @param subject
     *            邮件主题
     * @param sendHtml
     *            邮件内容
     * @param receiveUser
     *            收件人地址
     * @param attachment
     *            附件
     */
    public void doSendHtmlEmail(String subject, String sendHtml, 
    							String receiveUser, File attachment) {
        try {
            // 发件人
            InternetAddress from = new InternetAddress(sender_username);
            message.setFrom(from);

            // 收件人
            InternetAddress to = new InternetAddress(receiveUser);
            message.setRecipient(Message.RecipientType.TO, to);

            // 邮件主题
            message.setSubject(subject);

            // 向multipart对象中添加邮件的各个部分内容，包括文本内容和附件
            Multipart multipart = new MimeMultipart();
            
            // 添加邮件正文
            BodyPart contentPart = new MimeBodyPart();
            contentPart.setContent(sendHtml, "text/html;charset=UTF-8");
            multipart.addBodyPart(contentPart);
            
            // 添加附件的内容
            if (attachment != null) {
                BodyPart attachmentBodyPart = new MimeBodyPart();
                DataSource source = new FileDataSource(attachment);
                attachmentBodyPart.setDataHandler(new DataHandler(source));
                
                // 网上流传的解决文件名乱码的方法，其实用MimeUtility.encodeWord就可以很方便的搞定
                // 这里很重要，通过下面的Base64编码的转换可以保证你的中文附件标题名在发送时不会变成乱码
                //sun.misc.BASE64Encoder enc = new sun.misc.BASE64Encoder();
                //messageBodyPart.setFileName("=?GBK?B?" + enc.encode(attachment.getName().getBytes()) + "?=");
                
                //MimeUtility.encodeWord可以避免文件名乱码
                attachmentBodyPart.setFileName(MimeUtility.encodeWord(attachment.getName()));
                multipart.addBodyPart(attachmentBodyPart);
            }

            //4.设置邮件图片1
            MimeBodyPart image1 = new MimeBodyPart();
            image1.setDataHandler(new DataHandler(new FileDataSource("index-icon_1.png")));  //javamail jaf
            image1.setContentID("IMG0");
            //4.设置邮件图片1
            MimeBodyPart image2 = new MimeBodyPart();
            image2.setDataHandler(new DataHandler(new FileDataSource("index-icon_2.png")));  //javamail jaf
            image2.setContentID("IMG1");
            //4.设置邮件图片1
            MimeBodyPart image3 = new MimeBodyPart();
            image3.setDataHandler(new DataHandler(new FileDataSource("index-icon_3.png")));  //javamail jaf
            image3.setContentID("IMG2");
            multipart.addBodyPart(image1);
            multipart.addBodyPart(image2);
            multipart.addBodyPart(image3);
            
            // 将multipart对象放到message中
            message.setContent(multipart);
            // 保存邮件
            message.saveChanges();

            transport = session.getTransport("smtp");
            // smtp验证，就是你用来发邮件的邮箱用户名密码
            transport.connect(mailHost, sender_username, sender_password);
            // 发送
            transport.sendMessage(message, message.getAllRecipients());

            System.out.println("send success!");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (transport != null) {
                try {
                    transport.close();
                } catch (MessagingException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static void main(String[] args) {
//        UtilMailWithAttach se = new UtilMailWithAttach(true);
//       
//        try {
//			URL url = new URL("http://localhost:8088/hj/zdzshikuang.doc");
//			File desFile=new File("d:/zdzfile.doc");
//			FileUtils.copyURLToFile(url, desFile); 
//			if(desFile.exists()){
//				se.doSendHtmlEmail("邮件主题", "邮件内容", "mars.hua@lbschina.com.cn", desFile);
//			}
//		} catch (MalformedURLException e) {
//			e.printStackTrace();
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
       
    }
}
