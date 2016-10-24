package com.smmpay.job;

import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import com.alibaba.fastjson.JSONObject;
import com.smmpay.respository.dao.SCallClientLogMapper;
import com.smmpay.respository.dao.SMallLogMapper;
import com.smmpay.respository.dao.TrPaymentRecordMapper;
import com.smmpay.respository.dao.UserAccountMapper;
import com.smmpay.respository.dao.UserPayChannelMapper;
import com.smmpay.respository.model.SCallClientLog;
import com.smmpay.respository.model.SMallLog;
import com.smmpay.respository.model.TrPaymentRecord;
import com.smmpay.respository.model.UserAccount;
import com.smmpay.respository.model.UserPayChannel;
import com.smmpay.util.HttpClient;
import com.smmpay.util.UtilMail;

/**
 * 轮询支付冻结、解冻支付结果通知商城 2015-11-16
 * 
 * @author wanghao
 *
 */

public class PayFreezeResultNotifyJob {

	private Logger log = Logger.getLogger(this.getClass());
	@Autowired
	SCallClientLogMapper callClientLogMapper;
	@Autowired
	TrPaymentRecordMapper paymentMapper;
	@Autowired
	UserAccountMapper accountMapper;
	@Autowired
	UserPayChannelMapper userPayChannelMapper;

	@Autowired
	SMallLogMapper mallLogMapper;

	@Value("#{common['MALL_PAY_NOTIFY_RESULT_ADDR']}")
	private String MALL_PAY_NOTIFY_RESULT_ADDR;
	
	@Value("#{common['FRONT.HOST']}")
	private String FRONTHOST;
	
	@SuppressWarnings("unused")
	private void processNotifyResultToMall() {
		// 查询成功的支付冻结记录开始
		SCallClientLog clientLog = new SCallClientLog();
		clientLog.setPayType(0);
		clientLog.setPayResult(0);
		clientLog.setIsSuccess(0);
		log.info("-----------------查询支付冻结记录开始-----------------------");
		List<SCallClientLog> listLog = callClientLogMapper.findAllListByType(clientLog);
		log.info("查询到支付冻结结果记录数: " + listLog.size() + "条");
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		if (listLog != null && listLog.size() > 0) {
			for (SCallClientLog scl : listLog) {
				Map<String, String> mapParam = new LinkedHashMap<String, String>();
				if (scl.getPaymentRecordId() != null) {
					log.info("---------------支付编号" + scl.getPaymentRecordId() + "开始调用--------------");
					final TrPaymentRecord paymentRecord = paymentMapper.selectByPrimaryKey(scl.getPaymentRecordId());

					scl.setCallTime(sdf.format(new Date()));

					if (paymentRecord != null && paymentRecord.getMallOrderId() != null) {
						mapParam.put("code", "0");						
						Map<String, String> mapParamData = new LinkedHashMap<String, String>();
						mapParamData.put("mallOrderId", paymentRecord.getMallOrderId());
						mapParamData.put("paymentId", paymentRecord.getPaymentId().toString());
						mapParamData.put("status", "1");
						mapParam.put("data", JSONObject.toJSONString(mapParamData));
						
						// mapParam.put("orderno",
						// String.valueOf(paymentRecord.getMallOrderId()));
						
						
						try {
							String notifyUrl = scl.getNotifyUrl();
							if (notifyUrl == null || "".equals(notifyUrl)) {
								notifyUrl = this.MALL_PAY_NOTIFY_RESULT_ADDR;
							}
							log.info("请求参数:"+JSONObject.toJSONString(mapParam));
							
							String result = HttpClient.getResultFromMall(notifyUrl, mapParam,null);
							log.info("---------------调用结果: " + result + "--------------");
							JSONObject json = null;
							try{
								 json = JSONObject.parseObject(result);
							}catch(Exception e){
								e.printStackTrace();
								log.error("json 转换异常:"+e);
							}
							
							if (StringUtils.isNotBlank(result)) {//null
								
							    json = JSONObject.parseObject(result);
								if (json.get("errno") != null && json.getInteger("errno") == 0) {
									log.info("---------------支付编号" + scl.getPaymentRecordId() + "调用成功--------------");
									scl.setIsSuccess(1);
									
									// 发送邮件
									if(paymentRecord.getPayType() == 0) { // 正常冻结
										log.info("payType : " + paymentRecord.getPayType());
										sendEmail(paymentRecord, "MailTemplete.html", "付款成功", Boolean.TRUE);
									} else if (paymentRecord.getPayType() == 1) {  // 补款冻结
										log.info("payType : " + paymentRecord.getPayType());
										sendEmail(paymentRecord, "imbalanceFreezeMail.html", "差额付款成功", Boolean.TRUE);
									}else if (paymentRecord.getPayType() == 2) {  // 退款冻结
										log.info("payType : " + paymentRecord.getPayType());
										sendEmail(paymentRecord, "refundImbalanceFreezeMail.html", "退款付款成功", Boolean.TRUE);
									}
									
								} else {
									log.info("---------------支付编号" + scl.getPaymentRecordId() + "调用失败:"
											+ json.getString("msg"));
									scl.setIsSuccess(2);
								}

								// 呼叫商城log
								SMallLog mallLog = new SMallLog();
								mallLog.setRequestInterface(notifyUrl);
								mallLog.setRequestParam(JSONObject.toJSONString(mapParam));
								mallLog.setApplyTime(sdf.format(new Date()));
								mallLog.setReplayStatus(json.getInteger("errno"));
								mallLog.setReplyText(result);
								scl.setResponseText(result);
								mallLogMapper.insert(mallLog);
							}
						} catch (Exception e) {
							e.printStackTrace();
							scl.setIsSuccess(0);
							log.error(e);
							// log.info(e.getMessage());
						}
					} else
						scl.setIsSuccess(2);
				} else {
					log.info("ID" + scl.getId() + "的编号数据异常");
					scl.setIsSuccess(0);
				}
				scl.setResponseTime(sdf.format(new Date()));
				callClientLogMapper.updateByPrimaryKey(scl);

			}
		}
		log.info("-----------------查询支付冻结记录完成-----------------------");
		// 查询成功的支付冻结记录结束

		// 查询解冻支付记录通知商城开始
		SCallClientLog clientLogPay = new SCallClientLog();
		clientLogPay.setPayType(1);
		clientLogPay.setPayResult(0);
		clientLogPay.setIsSuccess(0);
		log.info("-----------------查询解冻支付记录开始-----------------------");
		List<SCallClientLog> listLogPay = callClientLogMapper.findAllListByType(clientLogPay);
		log.info("查询到解冻支付结果记录数: " + listLog.size() + "条");
		// SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		if (listLogPay != null && listLogPay.size() > 0) {
			for (SCallClientLog scl : listLogPay) {
				if (scl.getPaymentRecordId() != null) {
					log.info("---------------支付编号" + scl.getPaymentRecordId() + "开始调用--------------");
					final TrPaymentRecord paymentRecord = paymentMapper.selectByPrimaryKey(scl.getPaymentRecordId());

					scl.setCallTime(sdf.format(new Date()));
					if (paymentRecord != null && paymentRecord.getMallOrderId() != null) {
						Map<String, String> mapParam = new LinkedHashMap<String, String>();
						mapParam.put("code", "1");
						Map<String, String> mapParamData = new LinkedHashMap<String, String>();
						mapParamData.put("mallOrderId", paymentRecord.getMallOrderId());
						mapParamData.put("paymentId", paymentRecord.getPaymentId().toString());
						mapParamData.put("status", "1");
						mapParam.put("data", JSONObject.toJSONString(mapParamData));
						//mapParam.put("paycode", String.valueOf(paymentRecord.getPaymentCode()));
						//mapParam.put("orderno", String.valueOf(paymentRecord.getMallOrderId()));
						try {
							String notifyUrl = scl.getNotifyUrl();
							if (notifyUrl == null || "".equals(notifyUrl)) {
								notifyUrl = this.MALL_PAY_NOTIFY_RESULT_ADDR;
								//notifyUrl=this.MALL_fin_NOTIFY_RESULT_ADDR;
							}
							log.info("请求参数:"+JSONObject.toJSONString(mapParam));
							String result = HttpClient.getResultFromMall(notifyUrl, mapParam,null);
							log.info("---------------调用结果" + result + "--------------");
							if (result != null) {
								JSONObject json = JSONObject.parseObject(result);
								if (json.get("errno") != null && json.getInteger("errno") == 0) {
									log.info("---------------支付编号" + scl.getPaymentRecordId() + "调用成功--------------");
									scl.setIsSuccess(1);
									
									//paymentRecord.getPayType()   0  发两封给买卖双方  1 发一个补款邮件给买卖双方
									
									// 发送邮件
									log.info("---------------支付编号" + scl.getPaymentRecordId() +"发送邮件");
									try{
										if(paymentRecord.getPayType() == 0) { // 正常冻结
											sendEmail(paymentRecord, "sellerGetMoneyMail.html", "收到货款", Boolean.FALSE); //卖家
											sendEmail(paymentRecord, "buyerPaidMail.html", "支付货款", Boolean.TRUE); //买家
										} else if (paymentRecord.getPayType() == 1) {  // 买家补款解冻
											sendEmail(paymentRecord, "imSellerGetMoneyMail.html", "收到货款", Boolean.FALSE); //卖家
											sendEmail(paymentRecord, "imBuyerPaidMail.html", "支付货款", Boolean.TRUE); //买家
										}else if (paymentRecord.getPayType() == 2) {  // 卖家退款解冻
											sendEmail(paymentRecord, "imBuyerGetMoneyMail.html", "收到货款", Boolean.FALSE); //买家
											sendEmail(paymentRecord, "imSellerPaidMail.html", "支付货款", Boolean.TRUE); //卖家
										}
									}catch(Exception e){
										e.printStackTrace();
										log.error(e);
									}
									
									
								} else {
									log.info("---------------支付编号" + scl.getPaymentRecordId() + "调用失败:"
											+ json.getString("msg"));
									scl.setIsSuccess(2);
								}
								
								// 呼叫商城log
								SMallLog mallLog = new SMallLog();
								mallLog.setRequestInterface(notifyUrl);
								mallLog.setRequestParam(JSONObject.toJSONString(mapParam));
								mallLog.setApplyTime(sdf.format(new Date()));
								mallLog.setReplayStatus(json.getInteger("errno") == 1 ? 0 : 1);
								mallLog.setReplyText(result);
								scl.setResponseText(result);
								mallLogMapper.insert(mallLog);
							}
						} catch (Exception e) {
							e.printStackTrace();
							scl.setIsSuccess(0);
							log.error(e);
							// log.info(e.getMessage());
						}
					} else
						scl.setIsSuccess(2);
				} else {
					log.info("ID" + scl.getId() + "的编号数据异常");
					scl.setIsSuccess(0);
				}
				scl.setResponseTime(sdf.format(new Date()));
				callClientLogMapper.updateByPrimaryKey(scl);
			}
		}
		log.info("-----------------查询解冻支付记录完成-----------------------");
		// 查询解冻支付记录通知商城结束
	}

	
	/*@Test
	public void test(){
		TrPaymentRecord paymentRecord = paymentMapper.selectByPrimaryKey(449);
		
		sendEmail(paymentRecord, "MailTemplete.html", "付款成功",  Boolean.TRUE);
		sendEmail(paymentRecord, "imbalanceFreezeMail.html", "差额付款成功", Boolean.FALSE);
		
		sendEmail(paymentRecord, "sellerGetMoneyMail.html", "收到货款", Boolean.FALSE); //卖家
		sendEmail(paymentRecord, "buyerPaidMail.html", "支付货款", Boolean.TRUE); //买家
		
		sendEmail(paymentRecord, "imSellerGetMoneyMail.html", "收到货款", Boolean.FALSE); //卖家
		sendEmail(paymentRecord, "imBuyerPaidMail.html", "支付货款", Boolean.TRUE); //买家
		
	}*/
	
	/** 发送冻结成功和支付成功邮件
	 * @param paymentRecord  getPayType() 0，正常冻结 1，补款冻结
	 * @param templateHtmlName
	 * @param emailSubject
	 * @param isBuyer
	 */
	public void sendEmail(final TrPaymentRecord paymentRecord, final String templateHtmlName, final String emailSubject, final boolean isBuyer) {
		
		Map<String, Object> userInfoMap = null;
		// 买方信息获取
		userInfoMap =  getUserAccountAndPayChannelByPaymentRecord(paymentRecord.getBuyerMallUserName(), paymentRecord.getBuyerPayChannelId());
		final UserAccount buyerAccount = (UserAccount) userInfoMap.get("account");
		final UserPayChannel buyerPayChannel = (UserPayChannel) userInfoMap.get("payChannel");
		// 卖方信息获取
		userInfoMap =  getUserAccountAndPayChannelByPaymentRecord(paymentRecord.getSellerMallUserName(), paymentRecord.getSellerPayChannelId());
		final UserAccount sellerAccount = (UserAccount) userInfoMap.get("account");
		final UserPayChannel sellerPayChannel = (UserPayChannel) userInfoMap.get("payChannel");
		
		final String receiveUser;
		
		// 收件人判断
		if(isBuyer){
			receiveUser = buyerAccount.getUserName();
		} else {
			receiveUser = sellerAccount.getUserName();
		}
		log.error("开始准备邮件："+ emailSubject +"("+ templateHtmlName + ")，" + paymentRecord.getPaymentId() +"，收件人："+ receiveUser + "(" + (isBuyer ? "买家":"卖家")+")");
		// 异步发送邮件
		Thread th = new Thread(new Runnable() {
			public void run() {
				try {
					log.info("邮件开始发送");
					InputStream is =null;
					is = ClassLoader.getSystemResourceAsStream("emailTemlate/" + templateHtmlName);
					byte[] readByte = new byte[is.available()];
					is.read(readByte);

					String mailContent = new String(readByte, "utf-8");
					log.info("email--------------" + mailContent);
					
					mailContent = mailContent.replace("$buyerName", buyerAccount.getContactName());   // 买方收件人姓名
					mailContent = mailContent.replace("$sellerName", sellerAccount.getContactName());   // 卖方收件人姓名
					
					mailContent = mailContent.replace("$buyerAccNo", buyerPayChannel.getUserAccountNo());// 买方账号
					mailContent = mailContent.replace("$sellerAccNo", sellerPayChannel.getUserAccountNo());// 卖方账号
					
					mailContent = mailContent.replace("$buyerCompanyName", String.valueOf(paymentRecord.getBuyerCompanyName())); // 买方公司名
					mailContent = mailContent.replace("$sellerCompanyName", String.valueOf(paymentRecord.getSellerCompanyName())); //卖方公司名
					
					mailContent = mailContent.replace("$paymentId", paymentRecord.getPaymentNo().toString()); // 交易编号
					mailContent = mailContent.replace("$dealMoney", paymentRecord.getDealMoney().toString()); // 交易金额
					mailContent = mailContent.replace("$payCode", paymentRecord.getPaymentCode().toString()); // 唯一支付码
					mailContent = mailContent.replace("$paymentType", paymentRecord.getPaymentType()); // 付款方式
					mailContent = mailContent.replace("$createTime", paymentRecord.getCreateTime()); // 付款时间
					mailContent = mailContent.replace("$productName", paymentRecord.getProductName()); // 商品名称
					mailContent = mailContent.replace("$freezeClientId", String.valueOf(paymentRecord.getPaymentId()));// 资金流水号
					mailContent = mailContent.replace("$productDetail", paymentRecord.getProductDetail());  // 商品详情
					mailContent = mailContent.replace("$productNum", String.valueOf(paymentRecord.getProductNum()) + String.valueOf(paymentRecord.getProductNumUnit())); // 商品数量
					mailContent = mailContent.replace("$dealMoney", paymentRecord.getDealMoney().toString()); // 交易金额
					mailContent = mailContent.replace("$orderCreateTime", paymentRecord.getOrderCreateTime()); // 采购时间
					mailContent = mailContent.replace("$settlementType", paymentRecord.getSettlementType()); // 交收方式
					mailContent = mailContent.replace("$dealType", paymentRecord.getDealType());  // 交易类型
					mailContent = mailContent.replace("$invoice", paymentRecord.getInvoice());  // 发票说明
					mailContent = mailContent.replace("$mallOrderId", String.valueOf(paymentRecord.getMallOrderId())); // 商城订单编号
					mailContent = mailContent.replace("$FRONTHOST", FRONTHOST);  // 支付前台登录网址

					log.info("email--------------" + mailContent);
					UtilMail se = new UtilMail(false);
					se.doSendHtmlEmail(emailSubject, mailContent, receiveUser);
					log.info("邮件发送完成");
				} catch (Exception e) {
					e.printStackTrace();
					log.error("发送"+ emailSubject +"("+ templateHtmlName + ")出现问题，" + paymentRecord.getPaymentId() +"，收件人："+ receiveUser + "(" + (isBuyer ? "买家":"卖家") +")，" +e);
				}
			}
		});
		th.start();
	}
	
	// UserAccount buyerAccount, UserAccount sellerAccount, UserPayChannel buyerPayChannel, UserPayChannel sellerPayChannel
	private Map<String, Object> getUserAccountAndPayChannelByPaymentRecord(String mallUserName, Integer payChannelId){
		
		// 获取用户账户
		UserAccount tempAccount = new UserAccount();
		tempAccount.setMallUserName(mallUserName);
		UserAccount account = accountMapper.findAccountByMallUserName(tempAccount);
		
		// 取得用户账号渠道信息
		UserPayChannel payChannel = userPayChannelMapper.selectByPrimaryKey(payChannelId);
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("account", account);
		map.put("payChannel", payChannel);
		return map;
	}
}
