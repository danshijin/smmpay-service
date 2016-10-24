package com.smmpay.service.smmpay;

import java.lang.reflect.InvocationTargetException;
import java.util.Map;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;

import com.smmpay.common.ResErrorCode;
import com.smmpay.inter.dto.req.OpenAccountDTO;
import com.smmpay.inter.dto.res.ResOpenAccountDTO;
import com.smmpay.inter.dto.res.ReturnDTO;
import com.smmpay.inter.smmpay.OpenAccountService;
import com.smmpay.process.QueryBalanceProcess;
import com.smmpay.respository.model.ReqDlbregst;
import com.smmpay.respository.model.ResDlbregst;
import com.smmpay.service.base.BaseService;
import com.smmpay.util.CPayUtils;
import com.smmpay.util.XMLUtils;

@Service("openAccountService")
public class OpenAccountServiceImpl implements OpenAccountService {

	private Logger logger = Logger.getLogger(QueryBalanceProcess.class);

	public ReturnDTO openAccount(Map<String, String> map) {

		ReturnDTO returnDTO = new ReturnDTO();
		OpenAccountDTO oPenAccount = new OpenAccountDTO();
		try {

			BeanUtils.populate(oPenAccount, map);
			/**
			 * 判断参数
			 */
			if (StringUtils.isBlank(oPenAccount.getSubAccNm())) {
				// 参数错误，直接返回
				returnDTO.setMsg(ResErrorCode.OPENACCOUNT_ERROR_MSG_1);
				returnDTO.setStatus(ResErrorCode.VALIDPARAM_ERROR_CODE);
				returnDTO.setSuccess(false);
				return returnDTO;
			}

			returnDTO.setMsg(ResErrorCode.RETURN_EXCE_MSG);
			returnDTO.setStatus(ResErrorCode.RETURN_EXCE_CODE);

			ReqDlbregst req = new ReqDlbregst();
			req.setSubAccNm(oPenAccount.getSubAccNm());

			String sendXML = XMLUtils.beanToXML(req);
			/**
			 * 请求中信接口
			 */
			logger.info("请求中信接口，附属账户预签约.入参:" + sendXML);
			String resultXML = CPayUtils.postRequest(sendXML);
			/**
			 * 将响应结果转换成实体类
			 */
			logger.info("将响应结果转换成实体类.入参:" + resultXML);
			ResDlbregst resDlsbalqr = (ResDlbregst) XMLUtils.xmlToBean(resultXML, ResDlbregst.class, null);
			logger.info("将响应结果转换成实体类.出参:" + resDlsbalqr);

			if (resDlsbalqr != null) {
				
				ResOpenAccountDTO resOpenAccount = new ResOpenAccountDTO();
				BeanUtils.copyProperties(resOpenAccount, resDlsbalqr);
				returnDTO.setData(resOpenAccount);
				returnDTO.setMsg(ResErrorCode.RETURN_SUCCESS_CODE);
				returnDTO.setStatus(ResErrorCode.RETURN_SUCCESS_MSG);
			}

		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}

		return returnDTO;
	}

}
