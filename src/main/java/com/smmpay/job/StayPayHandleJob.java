package com.smmpay.job;

import java.util.Date;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.smmpay.payment.FreezePayResultService;
import com.smmpay.payment.PlatformDebitsResultService;
import com.smmpay.payment.SatyPayService;
import com.smmpay.payment.TransferResultService;
import com.smmpay.payment.UNFreezePayResultService;
import com.smmpay.util.DateUtil;


/**
 * 轮询待支付记录处理流程
 * 
 * @author zengshihua
 *
 */
public class StayPayHandleJob {
	
	private Logger logger=Logger.getLogger(this.getClass());
	
	@Autowired
	private SatyPayService satyPayService;
	
	@Autowired
	private FreezePayResultService fprService;
	
	@Autowired
	private UNFreezePayResultService unFreezePayResultService;
	
	@Autowired
	private PlatformDebitsResultService platformDebitsResultService;
	
	@Autowired
	private TransferResultService transferResultService;
	
	protected void execute() {
		try {
			/**
			 * 轮询待支付
			 */
			long startTime=System.currentTimeMillis();
			logger.info("["+DateUtil.doFormatDate(new Date(), "yyyy-MM-dd HH:mm:ss")+"],开始执行轮询待支付记录处理流程");
			satyPayService.satyPayHandle();
			long endTime=System.currentTimeMillis();
			logger.info("["+DateUtil.doFormatDate(new Date(), "yyyy-MM-dd HH:mm:ss")+"],结束执行轮询待支付记录处理流程,耗时："+(endTime-startTime)/1000+"秒.");
			
			/**
			 * 冻结结果
			 */
			long startTime2=System.currentTimeMillis();
			logger.info("["+DateUtil.doFormatDate(new Date(), "yyyy-MM-dd HH:mm:ss")+"],开始执行支付冻结结果流程");
			fprService.freezePayResult();
			long endTime2=System.currentTimeMillis();
			logger.info("["+DateUtil.doFormatDate(new Date(), "yyyy-MM-dd HH:mm:ss")+"],结束执行支付冻结结果流程,耗时："+(endTime2-startTime2)/1000+"秒.");
			
			/**
			 * 解冻结果
			 */
			long startTime3=System.currentTimeMillis();
			logger.info("["+DateUtil.doFormatDate(new Date(), "yyyy-MM-dd HH:mm:ss")+"],开始执行解冻指令结果流程");
			unFreezePayResultService.unfreezePayResult();
			long endTime3=System.currentTimeMillis();
			logger.info("["+DateUtil.doFormatDate(new Date(), "yyyy-MM-dd HH:mm:ss")+"],结束执行解冻指令结果流程,耗时："+(endTime3-startTime3)/1000+"秒.");
			
			/**
			 * 平台出金结果
			 */
			long startTime4 = System.currentTimeMillis();
			logger.info("[" + DateUtil.doFormatDate(new Date(), "yyyy-MM-dd HH:mm:ss")+ "],开始执行轮询出金结果处理流程");
			platformDebitsResultService.queryRecordType3();
			long endTime4 = System.currentTimeMillis();
			logger.info("["+ DateUtil.doFormatDate(new Date(), "yyyy-MM-dd HH:mm:ss")+ "],结束执行轮询轮询出金结果处理流程,耗时：" + (endTime4 - startTime4) / 1000+ "秒.");
		
			/**
			 * 转账结果
			 */
			long startTime5 = System.currentTimeMillis();
			logger.info("[" + DateUtil.doFormatDate(new Date(), "yyyy-MM-dd HH:mm:ss")+ "],开始执行轮询转账结果处理流程");
			transferResultService.TransferResult();
			long endTime5 = System.currentTimeMillis();
			logger.info("["+ DateUtil.doFormatDate(new Date(), "yyyy-MM-dd HH:mm:ss")+ "],结束执行轮询转账结果处理流程,耗时：" + (endTime5 - startTime5) / 1000+ "秒.");
		
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
