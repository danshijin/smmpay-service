package com.smmpay.util;

import org.springframework.beans.factory.annotation.Value;

/**
 * 常用变量
 * @author wanghao
 *
 */
public class Constant {

	public static String CER_PATH="";

	public static String MALL_HOST="http://huidumall.smm.cn/";

	//商城支付成功接口
	public static String PAY_NOTIFY_RESULT_ADDR = MALL_HOST+"Payment/paymentCallback";
	//解冻支付成功接口
	public static String Fin_NOTIFY_RESULT_ADDR = MALL_HOST+"Interface/changeProvider";
}
