package com.smmpay.service.smmpay;

import java.util.Date;
import java.util.Map;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.smmpay.common.ResErrorCode;
import com.smmpay.inter.dto.req.CancelAccountDTO;
import com.smmpay.inter.dto.res.ReturnDTO;
import com.smmpay.inter.smmpay.CancelAccountService;
import com.smmpay.process.QueryBalanceProcess;
import com.smmpay.process.enumDef.ReqResult;
import com.smmpay.respository.dao.SBankLogMapper;
import com.smmpay.respository.model.ReqDlolcacc;
import com.smmpay.respository.model.ResDlolcacc;
import com.smmpay.respository.model.SBankLog;
import com.smmpay.service.base.BaseService;
import com.smmpay.util.CPayUtils;
import com.smmpay.util.DateUtil;
import com.smmpay.util.XMLUtils;

@Service("cancelAccountService")
public class CancelAccountServiceImpl  implements  CancelAccountService {

	private Logger logger = Logger.getLogger(QueryBalanceProcess.class);
	
	@Autowired
	private SBankLogMapper bankLogMapper;

	public ReturnDTO cancelAccount(Map<String, String> map) {

		ReturnDTO returnDTO = new ReturnDTO();
		CancelAccountDTO account = new CancelAccountDTO();
		try {

			BeanUtils.populate(account, map);
			/**
			 * 判断参数
			 */
			if (StringUtils.isBlank(account.getSubAccNo())) {
				// 参数错误，直接返回
				returnDTO.setMsg(ResErrorCode.CANCELACCOUNT_ERROR_MSG_1);
				returnDTO.setStatus(ResErrorCode.VALIDPARAM_ERROR_CODE);
				returnDTO.setSuccess(false);
				return returnDTO;
			}


			ReqDlolcacc req = new ReqDlolcacc();
			req.setSubAccNo(account.getSubAccNo());

			String sendXML = XMLUtils.beanToXML(req);
			/**
			 * 请求中信接口
			 */
			logger.info("在线销户.入参:" + sendXML);
			String resultXML = CPayUtils.postRequest(sendXML);
			
			/**
			 * 将响应结果转换成实体类
			 */
			logger.info("在线销户.出参:" + resultXML);
			ResDlolcacc res = (ResDlolcacc) XMLUtils.xmlToBean(resultXML, ResDlolcacc.class, null);

			/**
			 * 记录日志
			 */
			SBankLog bankLog=new SBankLog();
			bankLog.setRequestParam(sendXML);
			bankLog.setRequestInterface("在线销户");
			bankLog.setApplyTime(DateUtil.doFormatDate(new Date(), "yyyy-MM-dd HH:mm:ss"));
			bankLog.setPayChannelId(1001);
			bankLog.setReplyText(resultXML);
			
			if (res.getStatus().equals(ReqResult.AAAAAAA.getCode())) {
				bankLog.setReplyStatus(0);
				returnDTO.setMsg(res.getStatusText());
				returnDTO.setStatus(ResErrorCode.RETURN_SUCCESS_CODE);
				
			}else{
				bankLog.setReplyStatus(1);
				returnDTO.setMsg(res.getStatusText());
				returnDTO.setStatus(ResErrorCode.RETURN_EXCE_CODE);
			}
			
			/**
			 * 保存请求日志
			 */
			bankLogMapper.insertSelective(bankLog);
			
			

		} catch (Exception e) {
			e.printStackTrace();
		} 

		return returnDTO;
	}

}
