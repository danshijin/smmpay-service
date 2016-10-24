package com.smmpay.util;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by tangshulei on 2015/11/9.
 */
public class StringUtils {

    public static boolean isNumeric(String str){
        Pattern pattern = Pattern.compile("[0-9]*");
        Matcher isNum = pattern.matcher(str);
        if( !isNum.matches() ){
            return false;
        }
        return true;
    }

    public static boolean isDecimal(String str){
        Pattern pattern = Pattern.compile("([0-9]*)(\\.[0-9]*)?");
        Matcher isNum = pattern.matcher(str);
        if( !isNum.matches() ){
            return false;
        }
        return true;
    }


    /**
     * 获取0-i之间的 小数位数为j的随机数字
     * @param i
     * @param j
     * @return
     */
    public static BigDecimal getNumber(int i, int j){
        Random random = new Random();
        Float f = random.nextFloat();
        if(f == 0){
            f = random.nextFloat();
        }
        BigDecimal decimal = new BigDecimal(f * i);
        decimal = decimal.setScale(j, BigDecimal.ROUND_HALF_UP);
        return decimal;
    }

    public static void main(String[] args){
        String abc = "11";
        System.out.print(isDecimal(abc));
    }

    public static boolean validDate(String str, String pattern){
        SimpleDateFormat sdf = new SimpleDateFormat(pattern);
        try{
            sdf.setLenient(false);
            sdf.parse(str);
        }catch(Exception e){
            e.printStackTrace();
            return false;
        }
        return true;
    }
}
