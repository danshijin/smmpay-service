package com.smmpay.payment;

import java.util.Map;

import com.smmpay.inter.dto.res.PagReturnDTO;

public interface Payment {

	/**
	 * 余额查询
	 * @param paramMap
	 * 			入参：用户支付ID、支付渠道ID、附属账号、附属账户名称 、operateType操作类型
	 * @return
	 */
	public Map<String, Object> queryPayment(Map<String, Object> paramMap) throws Exception;
	
	
	/**
	 * 支付
	 * 
	 * 入参：用户支付ID(userId)/支付记录ID(paymentId)/交易金额(dealMoney)/余额比对异常后是否设置手动处理标识(handleFlag Y:需要,N:不需要)
	 * 
	 * 出参：返回代码(resultCode)、返回信息(resultMesg)、
	 * 比对处理代码(handleCode)、比对处理信息(handleMesg)、
	 * 可用余额(userMoney)、冻结余额(freezeMoney)
	 * 账户余额(totalMoney)
	 */
	public Map<String, Object> payment(Map<String, Object> paramMap) throws Exception;
	
	/**
	 * 非登录打印明细查询
	 * @param map
	 * @return
	 */
	public PagReturnDTO queryTrRecords(Map<String,String> map);
	
}
