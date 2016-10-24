package com.smmpay.service.smmpay;

import com.smmpay.common.ResErrorCode;
import com.smmpay.inter.dto.res.PagReturnDTO;
import com.smmpay.inter.dto.res.ReturnDTO;
import com.smmpay.inter.smmpay.NoLoginPrintService;
import com.smmpay.payment.Payment;
import com.smmpay.util.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * 
 * @author zengshihua
 *
 */
@Service("noLoginPrintService")
public class NoLoginPrintServiceImpl implements NoLoginPrintService {

    Logger log = Logger.getLogger(this.getClass());
    
    public static String DATE_FORMAT = "yyyyMMdd";

    @Autowired
    Payment payment;


    /**
     * 交易记录查询
     *
     * @param map
     * @return
     */
    public ReturnDTO queryTrRecords(Map<String, String> map) {

    	String subAccNo = map.get("subAccNo");
        String startDate = map.get("startDate");
        String endDate = map.get("endDate");
        String page = map.get("page");
        /*String pageSize = map.get("pageSize");*/
        ReturnDTO returnDTO = new ReturnDTO();

        /**
         * 判断参数
         */
        String msg = validPage(subAccNo,startDate,endDate);
        if (msg != null) {
            //参数错误，直接返回
        	returnDTO.setMsg(ResErrorCode.VALIDPARAM_ERROR_MSG);
			returnDTO.setStatus(ResErrorCode.VALIDPARAM_ERROR_CODE);
            return returnDTO;
        }

        //获取交易记录
        Map<String,String> paramMap = new HashMap<String,String>();
        paramMap.put("subAccNo",subAccNo);
        startDate = startDate.replaceAll("-","");
        paramMap.put("startDate",startDate);
        endDate = endDate.replaceAll("-","");
        paramMap.put("endDate",endDate);
        //paramMap.put("tranType","1");
        /*int pageInt = Integer.valueOf(page);
        int pageSizeInt = Integer.valueOf(pageSize);
        if(pageInt < 1){
            pageInt = 1;
        }
        if(pageSizeInt < 1 || pageSizeInt > 10){
            pageSizeInt = 10;
        }
        int startData = (pageInt - 1) * pageSizeInt + 1;*/
        paramMap.put("startRecord",page + "");
        paramMap.put("pageNumber",10 + "");
        PagReturnDTO pagReturnDTO = payment.queryTrRecords(paramMap);
        //封装返回数据
        if(pagReturnDTO.isSuccess()){
            Map<String,Object> resMap = new HashMap<String, Object>();
            resMap.put("count", pagReturnDTO.getReturnRecords());
            resMap.put("list", pagReturnDTO.getData());
            returnDTO.setData(resMap);
        }else{
            returnDTO.setSuccess(false);
            returnDTO.setMsg(pagReturnDTO.getMsg());
            returnDTO.setStatus(pagReturnDTO.getStatus());
        }

        return returnDTO;
    }
    
    public String validPage(String subAccNo,String startDate, String endDate){
    	if(subAccNo == null || "".equals(subAccNo)){
            return ResErrorCode.GETPAYRECORD_ERROR_MSG_13;
        }
        if(startDate == null || "".equals(startDate)){
            return ResErrorCode.GETPAYRECORD_ERROR_MSG_5;
        }
        if(!StringUtils.validDate(startDate, DATE_FORMAT)){
            return ResErrorCode.GETPAYRECORD_ERROR_MSG_7;
        }
        if(endDate == null || "".equals(endDate)){
            return ResErrorCode.GETPAYRECORD_ERROR_MSG_6;
        }
        if(!StringUtils.validDate(endDate, DATE_FORMAT)){
            return ResErrorCode.GETPAYRECORD_ERROR_MSG_8;
        }
        
        return null;
    }
}
