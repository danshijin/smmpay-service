package com.smmpay.payment.impl;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.smmpay.payment.PlatformDebitsResultService;
import com.smmpay.process.QueryTRStatusProcess;
import com.smmpay.process.enumDef.ReqResult;
import com.smmpay.process.enumDef.ReqType;
import com.smmpay.process.enumDef.TranType;
import com.smmpay.respository.dao.TrCashApplyMapper;
import com.smmpay.respository.dao.TrCashApplyRecordMapper;
import com.smmpay.respository.dao.TrRecordMapper;
import com.smmpay.respository.dao.UserAccountMapper;
import com.smmpay.respository.dao.UserPayChannelMapper;
import com.smmpay.respository.model.ReqDlstrndt;
import com.smmpay.respository.model.ResDlstrndt;
import com.smmpay.respository.model.ResDlstrndtRow;
import com.smmpay.respository.model.TrCashApply;
import com.smmpay.respository.model.TrCashApplyRecord;
import com.smmpay.respository.model.TrRecord;
import com.smmpay.respository.model.UserAccount;
import com.smmpay.respository.model.UserPayChannel;
import com.smmpay.util.CPayUtils;
import com.smmpay.util.DateUtil;
import com.smmpay.util.XMLUtils;

/**
 * 平台出金结果
 * 
 */
@Service("platformDebitsResultService")
public class PlatformDebitsResultServiceImpl implements PlatformDebitsResultService {

	private Logger logger=Logger.getLogger(this.getClass());
	
	
	@Autowired
	private TrRecordMapper trRecordMapper;
	@Autowired
	private UserAccountMapper userAccountMapper;
	@Autowired
	private UserPayChannelMapper userPayChannelMapper;
	@Autowired
	private TrCashApplyRecordMapper trCashApplyRecordMapper;
	@Autowired
	private TrCashApplyMapper trCashApplyMapper;
	
	
	public synchronized void queryRecordType3() throws Exception {
//		List<TrRecord> recordList = trRecordMapper.queryTrRecordType3();
		List<TrCashApplyRecord> recordList = trCashApplyRecordMapper.selectByStatus(1);
		if(recordList==null||recordList.size()==0){
			logger.info("没有待处理的出金记录.");
			return;
		}
		
		for (TrCashApplyRecord trRecord : recordList) {
			handle(trRecord);
		}
		
	}
	@Transactional(rollbackFor = Exception.class)
	protected void handle(TrCashApplyRecord trRecord) throws Exception {
		
		try {
			
			Map<String, Object> param = new HashMap<String, Object>();
//			param.put("clientID", trRecord.getTrWaterId());
			param.put("clientID", trRecord.getClientId());
			param.put("type", ReqType.DLFCSOUT.getCode());
			QueryTRStatusProcess fristProcess = QueryTRStatusProcess.getInstanceWithParam(param);//查询交易状态
			Map<String, Object> fristResultMap=fristProcess.process(null);
			
			if(fristResultMap.get("resultCode").equals("success")){

				BigDecimal fee = trRecord.getCounterFee();
				//获取手续费
				BigDecimal xtsfam=getCharge(trRecord);

				BigDecimal feeSub = xtsfam.subtract(fee);

				if(xtsfam.compareTo(new BigDecimal(-1))==0){
					logger.info(trRecord.getId()+"没有匹配的到手续费,跳出循环.");
					return;
				}

//				TrCashApply tca = trCashApplyMapper.selectByPrimaryKey(trRecord.getCashApplyId());


				UserAccount userAccount = userAccountMapper.selectByPrimaryKey(trRecord.getUserId());
				
				//更新交易记录状态、反馈时间
				//新增交易记录 更新出金记录
				TrCashApplyRecord trCashApplyRecord = new TrCashApplyRecord();
				trCashApplyRecord.setApplyStatus(2);
				trCashApplyRecord.setReplayTime(DateUtil.doFormatDate(new Date(), "yyyy-MM-dd HH:mm:ss"));
				trCashApplyRecord.setId(trRecord.getId());
				trCashApplyRecord.setUserUseMoney(userAccount.getUseMoney());
				trCashApplyRecord.setCounterFee(xtsfam);
//				trCashApplyRecord.setUserFreezeMoney(userAccount.getFreezeMoney().subtract(trRecord.getCashMoney().add(xtsfam)));

				TrCashApply trCashApply = new TrCashApply();
				trCashApply.setApplyStatus(1);
				trCashApply.setId(trRecord.getCashApplyId());
				trCashApply.setCounterFee(xtsfam);
				trCashApplyMapper.updateByPrimaryKeySelective(trCashApply);

//				TrRecord record = new TrRecord();
//				record.setTrApplyStatus(1);
//				record.setTrId(trRecord.getTrId());
//				record.setPayChannelTrTime(DateUtil.doFormatDate(new Date(), "yyyy-MM-dd HH:mm:ss"));
//				record.setUserMoney(userAccount.getUseMoney().subtract(trRecord.getTrMoney().add(xtsfam)));
//				record.setUserFreezeMoney(userAccount.getFreezeMoney());
//				Integer rCount=trRecordMapper.updateByPrimaryKeySelective(record);
				Integer rCount = trCashApplyRecordMapper.updateByPrimaryKeySelective(trCashApplyRecord);
				if(rCount>0){
					TrRecord record = new TrRecord();
					record.setTrApplyStatus(1);
					record.setTrId(trRecord.getId());
					record.setPayChannelTrTime(DateUtil.doFormatDate(new Date(), "yyyy-MM-dd HH:mm:ss"));
					record.setUserMoney(userAccount.getUseMoney());
//					record.setUserFreezeMoney(userAccount.getFreezeMoney().subtract(trRecord.getCashMoney().add(xtsfam)));
					record.setTrMoney(trRecord.getCashMoney());
					record.setTrType(2);
					record.setTrApplyTime(DateUtil.doFormatDate(new Date(), "yyyy-MM-dd HH:mm:ss"));
					record.setTrCharge(xtsfam);
					record.setTrWaterId(trRecord.getClientId());
					record.setUserId(trRecord.getUserId());
					record.setUserPayChannelId(trRecord.getPayChannelId());
					record.setUserCompanyName(trRecord.getUserCompanyName());
					record.setReplayTime(DateUtil.doFormatDate(new Date(), "yyyy-MM-dd HH:mm:ss"));
					record.setNote("出金");
					Integer buyCount = trRecordMapper.insertSelective(record);

					TrRecord record1 = new TrRecord();
					record1.setTrApplyStatus(1);
					record1.setTrId(trRecord.getId());
					record1.setPayChannelTrTime(DateUtil.doFormatDate(new Date(), "yyyy-MM-dd HH:mm:ss"));
					record1.setUserMoney(userAccount.getUseMoney());
//					record.setUserFreezeMoney(userAccount.getFreezeMoney().subtract(trRecord.getCashMoney().add(xtsfam)));
					record1.setTrMoney(xtsfam);
					record1.setTrType(2);
					record1.setTrApplyTime(DateUtil.doFormatDate(new Date(), "yyyy-MM-dd HH:mm:ss"));
					record1.setTrCharge(xtsfam);
					record1.setTrWaterId(trRecord.getClientId());
					record1.setUserId(trRecord.getUserId());
					record1.setUserPayChannelId(trRecord.getPayChannelId());
					record1.setUserCompanyName(trRecord.getUserCompanyName());
					record1.setReplayTime(DateUtil.doFormatDate(new Date(), "yyyy-MM-dd HH:mm:ss"));
					record1.setNote("出金手续费");
					buyCount = trRecordMapper.insertSelective(record1);
//

					//更新账户余额
					Map<String, Object> userAccountMap=new HashMap<String, Object>();
//					userAccountMap.put("changeMoney", trRecord.getTrMoney().add(xtsfam));//加上手续费
					userAccountMap.put("changeMoney", feeSub);//加上手续费
					userAccountMap.put("userId", trRecord.getUserId());
					Integer uCount=userAccountMapper.updateUserMoneyByUserId4(userAccountMap);
					//更新支付通道余额
					Map<String, Object> userPayChannelMap=new HashMap<String, Object>();
//					userPayChannelMap.put("userPayChannelId", trRecord.getUserPayChannelId());
//					userPayChannelMap.put("changeMoney", trRecord.getTrMoney().add(xtsfam));//加上手续费
					userPayChannelMap.put("userPayChannelId", trRecord.getPayChannelId());
					userPayChannelMap.put("changeMoney", trRecord.getCashMoney().add(fee));//加上手续费
					userPayChannelMap.put("xtsfam", feeSub);//加上手续费
					Integer pCount=userPayChannelMapper.updateUserMoneyByUserIdPCId4(userPayChannelMap);
					
					if(uCount>0&&pCount>0){
//					if(pCount > 0){
						logger.info("更新支付通道余额、更新账户余额成功,TrId="+trRecord.getId());
					}else{
						logger.info("更新支付通道余额、更新账户余额失败,TrId="+trRecord.getId());
						throw new Exception();
					}
					
				}else{
					logger.info("更新交易记录状态、反馈时间失败,TrId="+trRecord.getId());
				}
				
			}else if(fristResultMap.get("resultCode").equals("break")){
				logger.info("交易状态还处于未知中，不作任何处理"+trRecord.getId());
			}else{
				//更新交易记录为失败
				TrRecord record = new TrRecord();
				record.setTrApplyStatus(2);
//				record.setTrId(trRecord.getTrId());
				record.setTrId(trRecord.getId());
				record.setPayChannelTrTime(DateUtil.doFormatDate(new Date(), "yyyy-MM-dd HH:mm:ss"));
				record.setNote(fristResultMap.get("statusText")!=null?String.valueOf(fristResultMap.get("statusText")):"");
				Integer rCount=trRecordMapper.updateByPrimaryKeySelective(record);
				if(rCount>0){
					logger.info("更新交易记录为失败--处理成功,TrId="+trRecord.getId());
				}else{
					logger.info("更新交易记录为失败--处理失败,TrId="+trRecord.getId());
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 获取手续费
	 * @param trRecord
	 * @return
	 * @throws InterruptedException
	 */
	protected BigDecimal getCharge(TrCashApplyRecord trRecord) throws InterruptedException {
		
		BigDecimal returnBig=new BigDecimal(-1);
		
//		UserPayChannel userPayChannel=userPayChannelMapper.selectByPrimaryKey(trRecord.getUserPayChannelId());
		UserPayChannel userPayChannel=userPayChannelMapper.selectByPrimaryKey(trRecord.getPayChannelId());
		Thread.sleep(13000);
		
		/**
		 * 组装交易明细查询参数
		 */
		
		Calendar c = Calendar.getInstance();
	    SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
	    c.add(Calendar.DATE, -2);
		String startDate=sdf.format(c.getTime());
		String endDate=DateUtil.doFormatDate(new Date(), "yyyyMMdd");
		
		ReqDlstrndt reqDlstrndt=new ReqDlstrndt();
		reqDlstrndt.setStartDate(startDate);
		reqDlstrndt.setEndDate(endDate);
		reqDlstrndt.setSubAccNo(userPayChannel.getUserAccountNo());
		reqDlstrndt.setTranType(TranType.T23.getCode());//只查询出金记录
		String sendXML=XMLUtils.beanToXML(reqDlstrndt);
		
		
		/**
		 * 请求中信接口，获取交易明细
		 */
		logger.info("取交易明细.获取手续费，入参:"+sendXML);
		String resultXML=CPayUtils.postRequest(sendXML);
		/**
		 * 将响应结果转换成实体类
		 */
		logger.info("取交易明细.获取手续费，出参:"+resultXML);
		ResDlstrndt resDlstrndt=(ResDlstrndt) XMLUtils.xmlToBean(resultXML,ResDlstrndt.class,ResDlstrndtRow.class);
		/**
		 * 判断是否请求中信成功
		 */
		
		if(resDlstrndt!=null&& resDlstrndt.getStatus().equals(ReqResult.AAAAAAA.getCode())){
			if(resDlstrndt.getList()!=null&&resDlstrndt.getList().size()>0){
				List<ResDlstrndtRow> resDRs = resDlstrndt.getList();
				
				for (ResDlstrndtRow resDlstrndtRow : resDRs) {
					
					if(TranType.T23.getCode().equals(resDlstrndtRow.getTRANTYPE())//外部转账
							&&TranType.T1.getCode().equals(resDlstrndtRow.getCDFG())//转出
//							&&trRecord.getOutcomeBankAccountNo().equals(resDlstrndtRow.getOPPACCNO())//出金银行卡号相等
//							&&(trRecord.getTrMoney().compareTo(resDlstrndtRow.getTRANAMT())==0)){//金额相等
							&&trRecord.getCashBankNo().equals(resDlstrndtRow.getOPPACCNO())
							&&trRecord.getCashMoney().compareTo(resDlstrndtRow.getTRANAMT()) == 0){

						returnBig=resDlstrndtRow.getXTSFAM();//返回手续费
						logger.info(trRecord.getId()+"手续费为:"+returnBig);
					}
				}
				
				if(returnBig.compareTo(new BigDecimal(-1))==0){
					/**没有匹配记录*/
					logger.info("没有匹配记录");	
				}
				
			}else{
				/**没有查找到记录*/
				logger.info("取交易明细.获取手续费没有查询到记录 ："+resDlstrndt.getStatusText());
			}
			
			
		}else{
			/**请求中信接口失败*/
			logger.info("取交易明细.获取手续费失败 ："+resDlstrndt.getStatusText());
			
		}
		
		return returnBig;
		
	}
}
