package com.smmpay.job;

import java.util.Date;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.smmpay.payment.FreezePayResultService;
import com.smmpay.payment.SatyPayService;
import com.smmpay.util.DateUtil;


/**
 * 轮询待支付冻结结果
 * 
 * @author zengshihua
 *
 */
public class FreezePayResultHandleJob {
	
	private Logger logger=Logger.getLogger(this.getClass());
	
	@Autowired
	private FreezePayResultService fprService;
	
	protected void execute() {
		try {
			//long startTime=System.currentTimeMillis();
			//logger.info("["+DateUtil.doFormatDate(new Date(), "yyyy-MM-dd HH:mm:ss")+"],开始执行支付冻结结果流程");
			//fprService.freezePayResult();
			//long endTime=System.currentTimeMillis();
			//logger.info("["+DateUtil.doFormatDate(new Date(), "yyyy-MM-dd HH:mm:ss")+"],结束执行支付冻结结果流程,耗时："+(endTime-startTime)/1000+"秒.");
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
