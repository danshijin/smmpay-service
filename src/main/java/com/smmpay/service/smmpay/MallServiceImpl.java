package com.smmpay.service.smmpay;


import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.smmpay.inter.dto.res.ReturnDTO;
import com.smmpay.inter.dto.res.TrConfirmAuditDTO;
import com.smmpay.inter.dto.res.TrPaymentRecordDTO;
import com.smmpay.inter.smmpay.MallService;
import com.smmpay.internal.service.TOrderService;
import com.smmpay.internal.service.TrConfirmAuditService;

public class MallServiceImpl implements MallService {

	private Logger log = Logger.getLogger(this.getClass());
	@Autowired
	TrConfirmAuditService confirmService;
	@Autowired
	TOrderService orderService;
	
	public ReturnDTO callTrConfirmAudit(Map<String,String> map) {
		ReturnDTO dto = new ReturnDTO();
		TrConfirmAuditDTO confirmDTO = new TrConfirmAuditDTO();
		try{
			BeanUtils.populate(confirmDTO, map);
			
		}catch(Exception e){
			e.printStackTrace();
		}
		log.info("paymentId:"+confirmDTO.getPaymentId());
		log.info("mallUserName:"+confirmDTO.getMallUserName());
		//判断payment对应的支付是否存在
		TrPaymentRecordDTO recordDTO = orderService.findPaymentRecord(confirmDTO.getPaymentId());
		if(recordDTO == null ){
			dto = new ReturnDTO("000002",false,"支付记录不存在");
		}
		if(!recordDTO.getBuyerMallUserName().equals(confirmDTO.getMallUserName())){
			dto = new ReturnDTO("000003",false,"支付记录买卖家不匹配");
		}
		
		try{
			confirmDTO.setApplyTime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
			int rows = confirmService.insertTrConfirmAudit(confirmDTO);
			if(rows == 0){
				 dto = new ReturnDTO("000004",false,"重复提交或处理失败");
			}
		}catch(Exception e){
			e.printStackTrace();
			log.error(e);
		    dto = new ReturnDTO("000001",false,"数据处理失败");
		}
		
		return dto;
	}

	public ReturnDTO checkPayCodeFromMall(Map<String, String> map) {
		TrPaymentRecordDTO paymentDTO = new TrPaymentRecordDTO();
		ReturnDTO dto = new ReturnDTO();
		try{
			BeanUtils.populate(paymentDTO, map);
		}catch(Exception e){
			e.printStackTrace();
		}
		TrPaymentRecordDTO recordDTO = orderService.findPaymentRecord(paymentDTO.getPaymentId());
		log.info(recordDTO.getBuyerMallUserName() +"" + paymentDTO.getBuyerMallUserName());
		if(recordDTO != null){
			if(!recordDTO.getBuyerMallUserName().equals(paymentDTO.getBuyerMallUserName())){
				dto = new ReturnDTO("0000001",false,"商城用户与支付买家不匹配");
			}
			if(recordDTO.getPaymentCode() != null && !recordDTO.getPaymentCode().equals(paymentDTO.getPaymentCode())){
				dto = new ReturnDTO("0000002",false,"支付码与系统不匹配");
			}
		}else{
			dto = new ReturnDTO("0000003",false,"支付记录不存在");
		}
		return dto;
	}
 
	public ReturnDTO getMarketCode(Map<String, String> map) {
		return orderService.getMarketStatus();
		
	}
}
