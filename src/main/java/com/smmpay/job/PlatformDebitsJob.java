package com.smmpay.job;

import java.util.Date;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.smmpay.payment.PlatformDebitsResultService;
import com.smmpay.util.DateUtil;

/**
 * 轮询出金结果
 * @author zengshihua
 *
 */
public class PlatformDebitsJob {
	
	private Logger log = Logger.getLogger(this.getClass());

	@Autowired
	private PlatformDebitsResultService platformDebitsResultService;
	
	
	protected void execute() {
		try {

			long startTime = System.currentTimeMillis();
			log.info("[" + DateUtil.doFormatDate(new Date(), "yyyy-MM-dd HH:mm:ss")+ "],开始执行轮询出金结果处理流程");
			platformDebitsResultService.queryRecordType3();
			long endTime = System.currentTimeMillis();
			log.info("["+ DateUtil.doFormatDate(new Date(), "yyyy-MM-dd HH:mm:ss")+ "],结束执行轮询轮询出金结果处理流程,耗时：" + (endTime - startTime) / 1000+ "秒.");

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
