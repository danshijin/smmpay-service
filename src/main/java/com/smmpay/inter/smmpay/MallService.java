package com.smmpay.inter.smmpay;


import java.util.Map;

import com.smmpay.inter.dto.res.ReturnDTO;

public interface MallService{
	
  public ReturnDTO callTrConfirmAudit(Map<String,String> map);
  
  public ReturnDTO checkPayCodeFromMall(Map<String,String> map);
  
}
