package com.smmpay.job;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.smmpay.payment.UNFreezePayResultService;


/**
 * 轮询解冻指令结果
 * 
 * @author zengshihua
 *
 */
public class UNFreezePayResultHandleJob {
	
	private Logger logger=Logger.getLogger(this.getClass());
	
	@Autowired
	private UNFreezePayResultService unFreezePayResultService;
	
	protected void execute() {
		try {
			//long startTime=System.currentTimeMillis();
			//logger.info("["+DateUtil.doFormatDate(new Date(), "yyyy-MM-dd HH:mm:ss")+"],开始执行解冻指令结果流程");
			//unFreezePayResultService.unfreezePayResult();
			//long endTime=System.currentTimeMillis();
			//logger.info("["+DateUtil.doFormatDate(new Date(), "yyyy-MM-dd HH:mm:ss")+"],结束执行解冻指令结果流程,耗时："+(endTime-startTime)/1000+"秒.");
			
		} catch (Exception e) {
			
			e.printStackTrace();
		}
	}
}
