package com.smmpay.util;
 

import java.io.FileInputStream; 
import java.io.FileNotFoundException; 
import java.io.FileOutputStream; 
import java.io.IOException; 
import java.io.InputStream;
import java.util.Properties; 

/**
 *  
 * @author zengshihua
 *
 */
public class PropertiesUtil 
{ 
    public static Properties propertie; 
    public static PropertiesUtil pUtil = new PropertiesUtil();
    
    public PropertiesUtil() 
    { 
    	if(propertie == null){
    		propertie = new Properties(); 
            try { 
            	InputStream is = ClassLoader.getSystemResourceAsStream("ysw.properties");
            	propertie.load(is);
            } catch (FileNotFoundException ex) { 
                ex.printStackTrace(); 
            } catch (IOException ex) { 
                ex.printStackTrace(); 
            } 
    	}
    }
    
    public static String getValue(String key) 
    { 
    	if(propertie.containsKey(key)){ 
            String value = propertie.getProperty(key);
            return value; 
        } 
        else 
            return "";
    }
    
    public static String getValue(String fileName, String key) 
    { 
        try { 
            String value = ""; 
            FileInputStream inputFile = new FileInputStream(fileName); 
            propertie.load(inputFile); 
            inputFile.close(); 
            if(propertie.containsKey(key)){ 
                value = propertie.getProperty(key); 
                return value; 
            }else 
                return value; 
        } catch (FileNotFoundException e) { 
            e.printStackTrace(); 
            return ""; 
        } catch (IOException e) { 
            e.printStackTrace(); 
            return ""; 
        } catch (Exception ex) { 
            ex.printStackTrace(); 
            return ""; 
        } 
    }
    
   
    public static void clear() 
    { 
        propertie.clear(); 
    }
    
    public static void setValue(String key, String value) 
    { 
        propertie.setProperty(key, value); 
    }
    
    public static void saveFile(String fileName, String description) 
    { 
        try { 
        	FileOutputStream outputFile = new FileOutputStream(fileName); 
            propertie.store(outputFile, description); 
            outputFile.close(); 
        } catch (FileNotFoundException e) { 
            e.printStackTrace(); 
        } catch (IOException ioe){ 
            ioe.printStackTrace(); 
        } 
    }
    public static void main(String[] args) {
    	System.out.println(PropertiesUtil.getValue("STAY.PAY"));
	}
}