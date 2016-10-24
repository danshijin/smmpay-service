package com.smmpay.service.smmpay;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONObject;
import com.smmpay.common.ResErrorCode;
import com.smmpay.inter.dto.res.DaBankDTO;
import com.smmpay.inter.dto.res.DaCodeDTO;
import com.smmpay.inter.dto.res.DaProvinceCityDTO;
import com.smmpay.inter.dto.res.ReturnDTO;
import com.smmpay.inter.smmpay.BankService;
import com.smmpay.internal.service.ValidDTO;
import com.smmpay.respository.dao.DaBankMapper;
import com.smmpay.respository.dao.DaCodeMapper;
import com.smmpay.respository.dao.DaProvinceCityMapper;
import com.smmpay.respository.model.DaBank;
import com.smmpay.respository.model.DaCode;
import com.smmpay.respository.model.DaProvinceCity;
import com.smmpay.service.base.BaseService;

@Service("bankService")
public class BankServiceImpl extends BaseService implements BankService {
	Logger log = Logger.getLogger(BankServiceImpl.class);
	@Autowired
	private DaProvinceCityMapper provinceCityDao;
	@Autowired
	private DaBankMapper bankDao;
	@Autowired
	private DaCodeMapper codeDao;
	@Autowired
	private ValidDTO validDTO;

	public ReturnDTO getAllBank(Map<String, String> map) {
		// TODO Auto-generated method stub
		ReturnDTO returnDTO = new ReturnDTO();
		List<DaCode> bankList = codeDao.queryAllBank();
		List<DaCodeDTO> bankdtoList = new ArrayList<DaCodeDTO>();
		for (DaCode daCode : bankList) {
			DaCodeDTO bankDto = new DaCodeDTO();
			bankDto.setCode(daCode.getCode());
			bankDto.setCodeName(daCode.getCodeName());
			bankdtoList.add(bankDto);
		}
		returnDTO.setData(bankdtoList);
		returnDTO.setMsg(ResErrorCode.RETURN_SUCCESS_MSG);
		returnDTO.setStatus(ResErrorCode.RETURN_SUCCESS_CODE);
		return returnDTO;
	}

	public ReturnDTO getAllProvince(Map<String, String> map) {
		// TODO Auto-generated method stub
		ReturnDTO returnDTO = new ReturnDTO();
		List<DaProvinceCity> pclist = provinceCityDao.selectProvinceAll();
		List<DaProvinceCityDTO> pcDtoList = new ArrayList<DaProvinceCityDTO>();
		for (DaProvinceCity daProvinceCity : pclist) {
			DaProvinceCityDTO dto = new DaProvinceCityDTO();
			dto.setId(daProvinceCity.getId());
			dto.setParentId(daProvinceCity.getParentId());
			dto.setLayer(daProvinceCity.getLayer());
			dto.setAreaName(daProvinceCity.getAreaName());
			pcDtoList.add(dto);
		}
		returnDTO.setData(pcDtoList);
		returnDTO.setMsg(ResErrorCode.RETURN_SUCCESS_MSG);
		returnDTO.setStatus(ResErrorCode.RETURN_SUCCESS_CODE);
		
		return returnDTO;
	}

	public ReturnDTO getCitys(Map<String, String> map) {
		// TODO Auto-generated method stub
		ReturnDTO returnDTO = new ReturnDTO();
		DaProvinceCityDTO daProvinceCityDTO = new DaProvinceCityDTO();
		try {
			BeanUtils.populate(daProvinceCityDTO, map);
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if (daProvinceCityDTO.getParentId() != null) {
			List<DaProvinceCity> pclist = provinceCityDao
					.selectCityAll(daProvinceCityDTO.getParentId());
			log.info(pclist.size());
			List<DaProvinceCityDTO> pcdtoList = new ArrayList<DaProvinceCityDTO>();
			for (DaProvinceCity daProvinceCity : pclist) {
				DaProvinceCityDTO provinceCityDTO = new DaProvinceCityDTO();
				provinceCityDTO.setId(daProvinceCity.getId());
				provinceCityDTO.setAreaName(daProvinceCity.getAreaName());
				pcdtoList.add(provinceCityDTO);
			//	log.info(daProvinceCityDTO.getId());
				//log.info(daProvinceCityDTO.getAreaName());
			}
			
			//log.info(pcdtoList.get(0).getId()+pcdtoList.get(0).getAreaName());
			//log.info(JSONObject.toJSONString(pcdtoList));
			returnDTO.setData(pcdtoList);
			returnDTO.setMsg(ResErrorCode.RETURN_SUCCESS_MSG);
			returnDTO.setStatus(ResErrorCode.RETURN_SUCCESS_CODE);
			return returnDTO;
		} else {
			returnDTO.setMsg(ResErrorCode.VALIDPARAM_ERROR_MSG);
			returnDTO.setStatus(ResErrorCode.VALIDPARAM_ERROR_CODE);
			return returnDTO;
		}
	}

	public ReturnDTO getBankByCity(Map<String, String> map) {
		// TODO Auto-generated method stub
		ReturnDTO returnDTO = new ReturnDTO();
		DaBankDTO bankDTO = new DaBankDTO();
		DaBank daBank = new DaBank();
		try {
			BeanUtils.populate(bankDTO, map);
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if (bankDTO.getCityId() != null && bankDTO.getBankKind() != null) {
			daBank.setCityId(bankDTO.getCityId());
			daBank.setBankKind(bankDTO.getBankKind());
			List<DaBank> bankList = bankDao.getBankByCity(daBank);
			List<DaBankDTO> bankDtoList = new ArrayList<DaBankDTO>();
			for (DaBank daBank2 : bankList) {
				DaBankDTO bankDto = new DaBankDTO();
				bankDto.setId(daBank2.getId());
				bankDto.setShortName(daBank2.getShortName());
				bankDto.setCnaps(daBank2.getCnaps());
				bankDto.setEngName(daBank2.getEngName());
				bankDtoList.add(bankDto);
			}
			returnDTO.setData(bankDtoList);
			returnDTO.setMsg(ResErrorCode.RETURN_SUCCESS_MSG);
			returnDTO.setStatus(ResErrorCode.RETURN_SUCCESS_CODE);
			return returnDTO;
		} else {
			returnDTO.setMsg(ResErrorCode.VALIDPARAM_ERROR_MSG);
			returnDTO.setStatus(ResErrorCode.VALIDPARAM_ERROR_CODE);
			return returnDTO;
		}
	}

	public ReturnDTO getBankByCityLike(Map<String, String> map) {
		// TODO Auto-generated method stub
		ReturnDTO returnDTO = new ReturnDTO();
		DaBankDTO bankDTO = new DaBankDTO();
		DaBank daBank = new DaBank();
		try {
			BeanUtils.populate(bankDTO, map);
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		if (bankDTO.getCityId() != null && bankDTO.getBankKind() != null
				&& bankDTO.getShortName() != null) {
			daBank.setCityId(bankDTO.getCityId());
			daBank.setBankKind(bankDTO.getBankKind());
			daBank.setShortName(bankDTO.getShortName());
			
			List<DaBank> bankList = bankDao.getBankByCity(daBank);
			List<DaBankDTO> bankDtoList = new ArrayList<DaBankDTO>();
			for (DaBank daBank2 : bankList) {
				DaBankDTO bankDto = new DaBankDTO();
				bankDto.setId(daBank2.getId());
				bankDto.setShortName(daBank2.getShortName());
				bankDto.setCnaps(daBank2.getCnaps());
				bankDto.setEngName(daBank2.getEngName());
				bankDtoList.add(bankDto);
			}
			returnDTO.setData(bankDtoList);
			returnDTO.setMsg(ResErrorCode.RETURN_SUCCESS_MSG);
			returnDTO.setStatus(ResErrorCode.RETURN_SUCCESS_CODE);
			return returnDTO;
		} else {
			returnDTO.setMsg(ResErrorCode.VALIDPARAM_ERROR_MSG);
			returnDTO.setStatus(ResErrorCode.VALIDPARAM_ERROR_CODE);
			return returnDTO;
		}
	}

}
