package com.smmpay.service.smmpay;

import java.util.List;
import java.util.Map;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;

import com.smmpay.common.ResErrorCode;
import com.smmpay.inter.dto.req.QueryAccountBalanceDTO;
import com.smmpay.inter.dto.res.ResQueryAccountBalanceDTO;
import com.smmpay.inter.dto.res.ReturnDTO;
import com.smmpay.inter.smmpay.QueryAccountBalanceService;
import com.smmpay.process.enumDef.ReqResult;
import com.smmpay.respository.model.ReqDlsbalqr;
import com.smmpay.respository.model.ResDlsbalqr;
import com.smmpay.respository.model.ResDlsbalqrRow;
import com.smmpay.service.base.BaseService;
import com.smmpay.util.CPayUtils;
import com.smmpay.util.XMLUtils;

@Service("QueryAccountBalanceService")
public class QueryAccountBalanceServiceImpl extends BaseService implements
		QueryAccountBalanceService {

	private Logger logger = Logger.getLogger(QueryAccountBalanceServiceImpl.class);
	
	public ReturnDTO queryAccountBalance(Map<String, String> map) {
		ReturnDTO returnDTO = new ReturnDTO();
		QueryAccountBalanceDTO balanceDTO=new QueryAccountBalanceDTO();
		try {
			BeanUtils.populate(balanceDTO, map);
			/**
			 * 判断参数
			 */
			if (StringUtils.isBlank(balanceDTO.getSubAccNo())) {
				// 参数错误，直接返回
				returnDTO.setMsg(ResErrorCode.OPENACCOUNT_ERROR_MSG_1);
				returnDTO.setStatus(ResErrorCode.VALIDPARAM_ERROR_CODE);
				returnDTO.setSuccess(false);
				return returnDTO;
			}
			returnDTO.setMsg(ResErrorCode.RETURN_EXCE_MSG);
			returnDTO.setStatus(ResErrorCode.RETURN_EXCE_CODE);
			
			ReqDlsbalqr req = new ReqDlsbalqr();
			req.setSubAccNo(balanceDTO.getSubAccNo());
			String sendXML = XMLUtils.beanToXML(req);
			/**
			 * 请求中信接口
			 */
			logger.info("请求中信接口，附属账户余额查询.入参:" + sendXML);
			String resultXML = CPayUtils.postRequest(sendXML);
			/**
			 * 将响应结果转换成实体类
			 */
			logger.info("将响应结果转换成实体类.入参:" + resultXML);
			ResDlsbalqr resDlsbalqr=(ResDlsbalqr)XMLUtils.xmlToBean(resultXML, ResDlsbalqr.class, ResDlsbalqrRow.class);
			if (resDlsbalqr.getStatus().equals(ReqResult.AAAAAAA.getCode())) {
				List<ResDlsbalqrRow> resDRow=resDlsbalqr.getList();
				ResQueryAccountBalanceDTO resBanlanceDTO=new ResQueryAccountBalanceDTO();
				BeanUtils.copyProperties(resBanlanceDTO, resDRow.get(0));
				returnDTO.setData(resBanlanceDTO);
				returnDTO.setMsg(ResErrorCode.RETURN_SUCCESS_CODE);
				returnDTO.setStatus(ResErrorCode.RETURN_SUCCESS_MSG);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} 
		return returnDTO;
	}

}
