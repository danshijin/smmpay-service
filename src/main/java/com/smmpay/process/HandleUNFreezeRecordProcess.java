package com.smmpay.process;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.smmpay.process.enumDef.PayResult;
import com.smmpay.process.enumDef.PayType;
import com.smmpay.process.enumDef.ReqResult;
import com.smmpay.process.enumDef.ReqType;
import com.smmpay.respository.dao.SCallClientLogMapper;
import com.smmpay.respository.dao.TrFreezeRecordMapper;
import com.smmpay.respository.dao.TrPaymentRecordMapper;
import com.smmpay.respository.dao.UserAccountMapper;
import com.smmpay.respository.dao.UserMoneyExceptionMapper;
import com.smmpay.respository.dao.UserPayChannelMapper;
import com.smmpay.respository.model.ReqDlcidstt;
import com.smmpay.respository.model.ReqDlsfrzqr;
import com.smmpay.respository.model.ResDlsfrzqr;
import com.smmpay.respository.model.ResDlsfrzqrRow;
import com.smmpay.respository.model.SCallClientLog;
import com.smmpay.respository.model.TrFreezeRecord;
import com.smmpay.respository.model.TrPaymentRecord;
import com.smmpay.respository.model.UserAccount;
import com.smmpay.respository.model.UserMoneyException;
import com.smmpay.respository.model.base.ResCommon;
import com.smmpay.util.CPayUtils;
import com.smmpay.util.DateUtil;
import com.smmpay.util.XMLUtils;

/**
 * 
 * 解冻结果流程处理
 * 
 * @author zengshihua
 * 
 */
public class HandleUNFreezeRecordProcess extends StepProcessor {

	private Logger logger = Logger.getLogger(HandleUNFreezeRecordProcess.class);

	private TrFreezeRecordMapper trFreezeRecordMapper;
	
	private TrPaymentRecordMapper trPaymentRecordMapper;
	
	private UserPayChannelMapper userPayChannelMapper;
	
	private UserAccountMapper userAccountMapper;
	
	private SCallClientLogMapper sCallClientLogMapper;
	
	private TrFreezeRecord trFreezeRecord;

	protected HandleUNFreezeRecordProcess() {
		super();
	}

	protected HandleUNFreezeRecordProcess(Map<String, Object> paramMap) {
		super(paramMap);
	}

	protected HandleUNFreezeRecordProcess(Map<String, Object> paramMap,
			StepProcessor nextProcessor) {
		super(paramMap, nextProcessor);
	}

	public static HandleUNFreezeRecordProcess getInstanceWithParam(Map<String, Object> param,
			TrFreezeRecordMapper trFreezeRecordMapper,TrPaymentRecordMapper trPaymentRecordMapper,
			UserAccountMapper userAccountMapper, UserPayChannelMapper userPayChannelMapper,
			SCallClientLogMapper sCallClientLogMapper,TrFreezeRecord trFreezeRecord) {

		 Map<String, Object> paramMap = buildParam(param);
		HandleUNFreezeRecordProcess process = new HandleUNFreezeRecordProcess(paramMap);
		process.trFreezeRecord = trFreezeRecord;
		process.trFreezeRecordMapper=trFreezeRecordMapper;
		process.trPaymentRecordMapper=trPaymentRecordMapper;
		process.userAccountMapper=userAccountMapper;
		process.userPayChannelMapper=userPayChannelMapper;
		process.sCallClientLogMapper=sCallClientLogMapper;
		return process;
	}

	@SuppressWarnings("unused")
	private static Map<String, Object> buildParam(Map<String, Object> param) {
		// 构建输入参数
		Map<String, Object> processParamMap = new HashMap<String, Object>();
		if (param != null) {
			processParamMap.putAll(param);
		}
		return processParamMap;

	}

	/**
	 * 组装参数返回
	 * @throws Exception 
	 */
	@Override
	protected Map<String, Object> action(Map<String, Object> resultParamMap) throws Exception {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		try {
			
			/**
			 * 计算查询日期
			 */
			Date beginDate = new Date();
			Calendar date = Calendar.getInstance();
			date.setTime(beginDate);
			date.set(Calendar.DATE, date.get(Calendar.DATE) - 1);
			
			//String startDate=DateUtil.doFormatDate(beginDate, "yyyyMMdd");
			//String endDate=DateUtil.doFormatDate(date.getTime(), "yyyyMMdd");
			String startDate="20160225";
			String endDate="20160225";
            
			ReqDlsfrzqr reqDl=new ReqDlsfrzqr();
			reqDl.setSubAccNo(String.valueOf(resultParamMap.get("subAccNo")));
			reqDl.setStartDate(startDate);
			reqDl.setEndDate(endDate);
			
			
			String sendXML=XMLUtils.beanToXML(reqDl);
			/**
			 * 请求中信冻结资金接口
			 */
			logger.info("请求中信接口，获取交易明细.入参:"+sendXML);
			String resultXML=CPayUtils.postRequest(sendXML);
			/**
			 * 将响应结果转换成实体类
			 */
			logger.info("将响应结果转换成实体类.入参:"+resultXML);
			ResDlsfrzqr res=(ResDlsfrzqr) XMLUtils.xmlToBean(resultXML,ResDlsfrzqr.class,ResDlsfrzqrRow.class);
			logger.info("将响应结果转换成实体类.出参:"+res);
			
			if(res!=null&&res.getStatus().equals(ReqResult.AAAAAAA.getCode())){
				List<ResDlsfrzqrRow> resDlsRows = res.getList();
				String subAccNo=String.valueOf(resultParamMap.get("subAccNo"));
				for(int i=0;i<resDlsRows.size();i++){
					//判断是否有匹配记录
					ResDlsfrzqrRow resDlsRow = resDlsRows.get(i);
					//如果附属账号相等并且金额相等
					if(resDlsRow.getSubAccNo().equals(subAccNo)
							&&resDlsRow.getDJAMT().compareTo(trFreezeRecord.getFreezeMoney())==0){
						//如果有匹配记录，判断该冻结编号是否已经存在数据库
						String djCode = resDlsRow.getDJCODE();
						resultParamMap.put("freezeNo", djCode);
						Integer count = trFreezeRecordMapper.queryByFreezeNo(resultParamMap);
						if(count<=0){
							
							/**
							 * 获取账户余额
							 */
							UserAccount userAccount=userAccountMapper.selectByPrimaryKey(trFreezeRecord.getUserId());
							/**
							 * 冻结成功后处理
							 */
							//修改冻结记录状态
							logger.info("修改冻结记录状态:djCode="+djCode);
							trFreezeRecord.setFreezeNo(djCode);//冻结编号
							trFreezeRecord.setFreezeUserMoney(userAccount.getUseMoney().subtract(trFreezeRecord.getFreezeMoney()));//冻结成功后余额
							trFreezeRecord.setFreezeUserFreezeMoney(userAccount.getFreezeMoney().add(trFreezeRecord.getFreezeMoney()));//冻结成功后金额
							trFreezeRecord.setReplyTime(DateUtil.doFormatDate(new Date(), "yyyy-MM-dd HH:mm:ss"));
							trFreezeRecord.setFreezeStatus(1);
							Integer backfill=trFreezeRecordMapper.updateFreezeNo(trFreezeRecord);
							logger.info("修改冻结记录状态结果："+backfill);
							if(backfill<0){
								throw new Exception();
							}
							//修改支付记录状态
							logger.info("修改支付记录状态");
							TrPaymentRecord pr=new TrPaymentRecord();
							pr.setPaymentId(trFreezeRecord.getPaymentId());
							pr.setPaymentStatus(1);
							Integer paymentCount=trPaymentRecordMapper.updatePaymentStatus(pr);
							if(paymentCount<0){
								throw new Exception();
							}
							logger.info("修改支付记录状态结果:"+paymentCount);
							
							Map<String, Object> updateMap=new HashMap<String, Object>();
							updateMap.put("userId", trFreezeRecord.getUserId());
							updateMap.put("changeMoney", trFreezeRecord.getFreezeMoney());
							updateMap.put("userPayChannelId", trFreezeRecord.getUserPayChannelId());
							updateMap.put("userAccountNo",resDlsRow.getSubAccNo());
							//修改账户余额
							Integer upa=userAccountMapper.updateUserMoneyByUserId2(updateMap);
							if(upa<0){
								throw new Exception();
							}
							/**
							 * 修改支付通道可用余额
							 */
							logger.info("修改支付通道可用余额.入参:"+updateMap);
							int countPayChanne= userPayChannelMapper.updateUserMoneyByUserIdPCId2(updateMap);
							logger.info("修改支付通道可用余额.出参:"+countPayChanne);
							if(countPayChanne<0){
								throw new Exception();
							}
							/**
							 * 生成商城结果呼叫日志记录。状态“未呼叫成功”
							 * 
							 */
							SCallClientLog ccl=new SCallClientLog();
							ccl.setCreateTime(DateUtil.doFormatDate(new Date(), "yyyy-MM-dd HH:mm:ss"));
							ccl.setPayType(Integer.valueOf(PayType.P0.getCode()));
							ccl.setPayResult(Integer.valueOf(PayResult.P0.getCode()));
							ccl.setPaymentRecordId(trFreezeRecord.getPaymentId());
							ccl.setIsSuccess(0);
							Integer callLogCount=sCallClientLogMapper.insertSelective(ccl);
							if(callLogCount<0){
								throw new Exception();
							}
							logger.info(trFreezeRecord.getFreezeId()+"处理成功.");
							
							
						}else{
							//数据库存在冻结编号
							logger.info("数据库存在冻结编号.");
						}
					}
					
				}
			}
			resultMap.put("resultCode", "success");
			resultMap.put("resultMesg", "处理完成.");
			
		} catch (Exception e) {
			resultMap.put("resultCode", "exce");
			resultMap.put("resultMesg", "查询交易状态发生异常.");
			logger.error("查询交易状态发生异常."+e.getMessage());
			throw e;
		}
		logger.info("结束查询交易状态："+resultMap);
		return resultMap;
	}
}
