package com.smmpay.service.smmpay;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;

import com.smmpay.common.ResErrorCode;
import com.smmpay.inter.dto.req.QueryTrRecordsDTO;
import com.smmpay.inter.dto.res.PagReturnDTO;
import com.smmpay.inter.dto.res.ResQueryTrRecordsDTO;
import com.smmpay.inter.dto.res.ReturnDTO;
import com.smmpay.inter.smmpay.QueryTrRecordsService;
import com.smmpay.process.enumDef.ReqResult;
import com.smmpay.respository.model.ReqDlstrndt;
import com.smmpay.respository.model.ResDlbregst;
import com.smmpay.respository.model.ResDlstrndt;
import com.smmpay.respository.model.ResDlstrndtRow;
import com.smmpay.service.base.BaseService;
import com.smmpay.util.CPayUtils;
import com.smmpay.util.XMLUtils;

@Service("queryTrRecordsService")
public class QueryTrRecordsServiceImpl extends BaseService implements
		QueryTrRecordsService {

	private Logger logger = Logger.getLogger(QueryTrRecordsServiceImpl.class);

	public PagReturnDTO queryTrRecords(Map<String, String> map) {

		PagReturnDTO returnDTO = new PagReturnDTO();
		QueryTrRecordsDTO queryTrRecordsDTO = new QueryTrRecordsDTO();
		try {

			BeanUtils.populate(queryTrRecordsDTO, map);
			/**
			 * 判断参数
			 */
			if (StringUtils.isBlank(queryTrRecordsDTO.getSubAccNo()) 
					||StringUtils.isBlank(queryTrRecordsDTO.getStartRecord())
					||StringUtils.isBlank(queryTrRecordsDTO.getPageNumber())
					||StringUtils.isBlank(queryTrRecordsDTO.getStartDate())
					||StringUtils.isBlank(queryTrRecordsDTO.getEndDate())) {
				// 参数错误，直接返回
				returnDTO.setMsg(ResErrorCode.VALIDPARAM_ERROR_MSG);
				returnDTO.setStatus(ResErrorCode.VALIDPARAM_ERROR_CODE);
				returnDTO.setSuccess(false);
				return returnDTO;
			}

			returnDTO.setMsg(ResErrorCode.RETURN_EXCE_MSG);
			returnDTO.setStatus(ResErrorCode.RETURN_EXCE_CODE);

			ReqDlstrndt req = new ReqDlstrndt();
			
			BeanUtils.copyProperties(req, queryTrRecordsDTO);
			
			String sendXML = XMLUtils.beanToXML(req);
			/**
			 * 请求中信接口
			 */
			logger.info("请求中信接口，指定附属账户的交易记录.入参:" + sendXML);
			String resultXML = CPayUtils.postRequest(sendXML);
			/**
			 * 将响应结果转换成实体类
			 */
			logger.info("将响应结果转换成实体类.入参:" + resultXML);
			ResDlstrndt resDlsbalqr = (ResDlstrndt) XMLUtils.xmlToBean(resultXML, ResDlstrndt.class, ResDlstrndtRow.class);
			logger.info("将响应结果转换成实体类.出参:" + resDlsbalqr);

			if (resDlsbalqr != null&&resDlsbalqr.getStatus().equals(ReqResult.AAAAAAA.getCode())) {
				
				List<ResDlstrndtRow> resDRow=resDlsbalqr.getList();
				List<ResQueryTrRecordsDTO> resqtRecordsDTOList=new ArrayList<ResQueryTrRecordsDTO>();
				
				for (ResDlstrndtRow resDlstrndtRow : resDRow) {
					ResQueryTrRecordsDTO resTrRecDTO = new ResQueryTrRecordsDTO();
					BeanUtils.copyProperties(resTrRecDTO, resDlstrndtRow);
					resqtRecordsDTOList.add(resTrRecDTO);
				}
				returnDTO.setReturnRecords(resDlsbalqr.getReturnRecords());
				returnDTO.setData(resqtRecordsDTOList);
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
