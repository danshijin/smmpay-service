package com.smmpay.util;

import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

@SuppressWarnings("restriction")
public class Base64 {  
    // 加密  
	public static String getBase64(byte[] b) {  
       // byte[] b = null;  
        String s = null;  
       /// try {  
            //b = str.getBytes("utf-8");  
        //} catch (UnsupportedEncodingException e) {  
          //  e.printStackTrace();  
       // }  
        if (b != null) {  
            s = new BASE64Encoder().encodeBuffer(b).replace("\n", "").replace("\r", "").replace("\r\n", "");  
        }  
        return s;  
    }  
  
    // 解密  
	public static byte[] getFromBase64(String s) {  
        byte[] b = null;
        if (s != null) {  
          
			BASE64Decoder decoder = new BASE64Decoder();  
            try {  
                b = decoder.decodeBuffer(s);  
               // result = new String(b, "utf-8");  
            } catch (Exception e) {  
                e.printStackTrace();  
            }  
        }  
        return b;  
    }  
}  