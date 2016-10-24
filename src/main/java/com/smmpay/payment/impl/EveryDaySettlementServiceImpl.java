package com.smmpay.payment.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.smmpay.inter.dto.res.ExceRecordDTO;
import com.smmpay.payment.EveryDaySettlementService;
import com.smmpay.process.StepProcessor;
import com.smmpay.process.SysQueryBalanceProcess;
import com.smmpay.process.SysQueryTrRecordProcess;
import com.smmpay.process.SysSynCreditRecordProcess;
import com.smmpay.process.enumDef.OperateType;
import com.smmpay.respository.dao.SBankLogMapper;
import com.smmpay.respository.dao.TrConfirmAuditMapper;
import com.smmpay.respository.dao.TrFreezeRecordMapper;
import com.smmpay.respository.dao.TrPaymentRecordMapper;
import com.smmpay.respository.dao.TrRecordMapper;
import com.smmpay.respository.dao.TrUnfreezeRecordMapper;
import com.smmpay.respository.dao.UserAccountMapper;
import com.smmpay.respository.dao.UserMoneyExceptionMapper;
import com.smmpay.respository.dao.UserPayChannelMapper;
import com.smmpay.respository.model.UserPayChannel;

/**
 * 每日结算
 * 
 */
@Service("everyDaySettlementService")
public class EveryDaySettlementServiceImpl implements EveryDaySettlementService {

	private Logger logger=Logger.getLogger(this.getClass());
	
	@Autowired
	private TrPaymentRecordMapper trPaymentRecordMapper;
	@Autowired
	private UserMoneyExceptionMapper userMoneyExceptionMapper;
	@Autowired
	private TrRecordMapper trRecordMapper;
	@Autowired
	private UserAccountMapper userAccountMapper;
	@Autowired
	private UserPayChannelMapper userPayChannelMapper;
	@Autowired
	private TrFreezeRecordMapper freezeRecordMapper;
	@Autowired
	private TrConfirmAuditMapper confirmAuditMapper;
	@Autowired
	private TrUnfreezeRecordMapper trUnfreezeRecordMapper;
	@Autowired
	private SBankLogMapper bankLogMapper;
	
	private StepProcessor fristProcessor;
	private StepProcessor nextProcessor;
	
	/**
	 * 入参:结算日期startDate(yyyyMMdd)
	 * @throws Exception 
	 * 
	 */
	public Map<String, Object> settlement(Map<String, Object> paramMap) throws Exception {
		Map<String, Object> resultMap=new HashMap<String, Object>();
		//获取所有平台附属账户
		List<UserPayChannel> upcList=userPayChannelMapper.selectAll(null);
		int synCount=0;//同步记录
		int skipCount=0;//跳过记录
		int count=1;
		BigDecimal sysMoney=new BigDecimal(0.00);//账户在有色网的实际余额
		BigDecimal sysSJAMT=new BigDecimal(0.00);//账户在中信银行的实际余额
		List<ExceRecordDTO> exceList = new ArrayList<ExceRecordDTO>();
		try {
			
			for(int i=0;i<upcList.size();i++){
				/**
				 * 现在只针对中信支付通道做处理
				 */
				paramMap.put("userId", upcList.get(i).getUserId());
				paramMap.put("payChannelId", upcList.get(i).getPayChannelId());
				paramMap.put("subAccNo", upcList.get(i).getUserAccountNo());
				paramMap.put("subAccNm", upcList.get(i).getUserAccountName());
				paramMap.put("userPayChannelId", upcList.get(i).getUserPayChannelId());
				SysQueryTrRecordProcess first=SysQueryTrRecordProcess.getInstanceWithParam(paramMap,trRecordMapper,userAccountMapper);//获取最后入金记录时间
				fristProcessor=first;
				nextProcessor=first;
				nextProcessor.next(SysSynCreditRecordProcess.getInstanceWithParam(trRecordMapper, userAccountMapper, userPayChannelMapper,bankLogMapper))//同步入金记录
							 .next(SysQueryBalanceProcess.getInstanceWithParam(userPayChannelMapper, userMoneyExceptionMapper, bankLogMapper));//比对余额
				
				Map<String, Object> result=fristProcessor.process(null);
				//累加共入金记录数
				if(result.containsKey("synCount")&&result.get("synCount")!=null){
					synCount+=Integer.valueOf(String.valueOf(result.get("synCount")));
				}
				//累加已有入金记录
				if(result.containsKey("skipCount")&&result.get("skipCount")!=null){
					skipCount+=Integer.valueOf(String.valueOf(result.get("skipCount")));
				}
				//累加附属账户实际余额
				if(result.containsKey("totalMoney")&&result.get("totalMoney")!=null){
					sysMoney=new BigDecimal(sysMoney.add(new BigDecimal(String.valueOf(result.get("totalMoney")))).doubleValue()).setScale(2,BigDecimal.ROUND_HALF_UP);
				}
				//累加账户在中信银行的实际余额
				if(result.containsKey("sjAmt")&&result.get("sjAmt")!=null){
					sysSJAMT=new BigDecimal(sysSJAMT.add(new BigDecimal(String.valueOf(result.get("sjAmt")))).doubleValue()).setScale(2,BigDecimal.ROUND_HALF_UP);
					//sysSJAMT+=new BigDecimal(String.valueOf(result.get("sjAmt"))).doubleValue();
				}
				//如果余额比对异常
				if(result.containsKey("handleCode")&&result.get("handleCode").equals(OperateType.O02.getCode())){
					count++;
					ExceRecordDTO exceRecordDTO=new ExceRecordDTO();
					exceRecordDTO.setRowNum(count);
					exceRecordDTO.setUserMoney(new BigDecimal(String.valueOf(result.get("totalMoney"))));
					exceRecordDTO.setUserSJAMT(new BigDecimal(String.valueOf(result.get("sjAmt"))));
					exceRecordDTO.setUserAccountNo(upcList.get(i).getUserAccountNo());
					exceRecordDTO.setUserAccountName(upcList.get(i).getUserAccountName());
					exceRecordDTO.setUserPayChannelId(upcList.get(i).getUserPayChannelId());
					exceRecordDTO.setUserId(upcList.get(i).getUserId());
					exceList.add(exceRecordDTO);
				}
				
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		resultMap.put("upcList", upcList.size());//共需处理总数
		resultMap.put("synCount", synCount);//同步入金记录
		resultMap.put("skipCount", skipCount);//跳过记录
		resultMap.put("sysMoney", sysMoney);//平台在系统总余额
		resultMap.put("sysSJAMT", sysSJAMT);//平台在中信总余额
		resultMap.put("exceList", exceList);//异常账户
		return resultMap;
	}
}
