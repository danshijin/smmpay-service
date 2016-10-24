package com.smmpay.process;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;

import com.smmpay.process.enumDef.ReqResult;
import com.smmpay.process.enumDef.TranType;
import com.smmpay.respository.dao.SBankLogMapper;
import com.smmpay.respository.dao.TrRecordMapper;
import com.smmpay.respository.dao.UserAccountMapper;
import com.smmpay.respository.dao.UserPayChannelMapper;
import com.smmpay.respository.model.ReqDlstrndt;
import com.smmpay.respository.model.ResDlstrndt;
import com.smmpay.respository.model.ResDlstrndtRow;
import com.smmpay.respository.model.SBankLog;
import com.smmpay.respository.model.TrRecord;
import com.smmpay.util.CPayUtils;
import com.smmpay.util.DateUtil;
import com.smmpay.util.XMLUtils;
/**
 * 系统每日结算
 *
 */
public class SysSynCreditRecordProcess extends StepProcessor {

	
	private Logger logger=Logger.getLogger(SysSynCreditRecordProcess.class);
	
	private TrRecordMapper trRecordMapper;
	
	private UserAccountMapper userAccountMapper;
	
	private UserPayChannelMapper userPayChannelMapper;
	
	private SBankLogMapper bankLogMapper;
	
	
	protected SysSynCreditRecordProcess() {
		super();
	}

	protected SysSynCreditRecordProcess(Map<String, Object> paramMap) {
		super(paramMap);
	}

	protected SysSynCreditRecordProcess(Map<String, Object> paramMap,
			StepProcessor nextProcessor) {
		super(paramMap, nextProcessor);
	}

	public static SysSynCreditRecordProcess getInstanceWithParam(TrRecordMapper trRecordMapper,
			UserAccountMapper userAccountMapper,UserPayChannelMapper userPayChannelMapper,
			SBankLogMapper bankLogMapper) {

		Map<String, Object> paramMap = buildParam();
		SysSynCreditRecordProcess process = new SysSynCreditRecordProcess(paramMap);
		process.trRecordMapper=trRecordMapper;
		process.userAccountMapper=userAccountMapper;
		process.userPayChannelMapper=userPayChannelMapper;
		process.bankLogMapper=bankLogMapper;
		return process;
	}

	@SuppressWarnings("unused")
	private static Map<String, Object> buildParam() {
		// 构建输入参数
		Map<String, Object> processParamMap = new HashMap<String, Object>();
		return processParamMap;

	}

	
	
	@Override
	protected Map<String, Object> action(Map<String, Object> resultParamMap){
		logger.info("开始同步入金记录.入参:"+resultParamMap);
		Map<String, Object> resultMap = new HashMap<String, Object>();
		
		try {
			/**
			 * 组装交易明细查询参数
			 */
			ReqDlstrndt reqDlstrndt=new ReqDlstrndt();
			reqDlstrndt.setStartDate((String)resultParamMap.get("startDate"));
			reqDlstrndt.setEndDate((String)resultParamMap.get("endDate"));
			reqDlstrndt.setSubAccNo((String)resultParamMap.get("subAccNo"));
			reqDlstrndt.setTranType(TranType.T23.getCode());//只查询入金记录
			String sendXML=XMLUtils.beanToXML(reqDlstrndt);
			/**
			 * 请求中信接口，获取交易明细
			 */
			logger.info("请求中信接口，获取交易明细.入参:"+sendXML);
			String resultXML=CPayUtils.postRequest(sendXML);
			/**
			 * 将响应结果转换成实体类
			 */
			logger.info("将响应结果转换成实体类.入参:"+resultXML);
			ResDlstrndt resDlstrndt=(ResDlstrndt) XMLUtils.xmlToBean(resultXML,ResDlstrndt.class,ResDlstrndtRow.class);
			logger.info("将响应结果转换成实体类.出参:"+resDlstrndt);
			
			/**
			 * 记录日志
			 */
			
			SBankLog bankLog=new SBankLog();
			bankLog.setRequestParam(sendXML);
			bankLog.setRequestInterface(reqDlstrndt.getAction());
			bankLog.setApplyTime(DateUtil.doFormatDate(new Date(), "yyyy-MM-dd HH:mm:ss"));
			bankLog.setPayChannelId(Integer.valueOf(String.valueOf(resultParamMap.get("payChannelId"))));
			bankLog.setReplyText(resultXML);
			/**
			 * 判断是否请求中信成功
			 */
			if(resDlstrndt!=null&& resDlstrndt.getStatus().equals(ReqResult.AAAAAAA.getCode())){
				List<ResDlstrndtRow> resDRs = resDlstrndt.getList();
				List<ResDlstrndtRow> goldenList = new ArrayList<ResDlstrndtRow>();
				BigDecimal changeMoney=new BigDecimal(0.00);//计算变动金额
				bankLog.setReplyStatus(0);
				/**
				 * 判断获取的是否是入金
				 */
				int count=0;
				for(int i=0;i<resDRs.size();i++){
					//(交易类型=23&&交易序号=2)入金
					if(resDRs.get(i).getTRANTYPE().equals(TranType.T23.getCode())
							&&resDRs.get(i).getCDFG().equals(TranType.T2.getCode())){
						
						//判断交易记录是否已同步过此笔交易
						List<TrRecord> tr=trRecordMapper.queryTrRecordByTrNo(resDRs.get(i).getHOSTFLW());
						if(tr.size()==0){
							goldenList.add(resDRs.get(i));
							changeMoney=changeMoney.add(resDRs.get(i).getTRANAMT());
						}
						count++;
					}
				}
				
				logger.info("请求中信成功.入金记录:"+goldenList);
				if(goldenList.size()>0){
					/**
					 * 组装保存入金记录参数
					 */
					List<TrRecord> trRecordList=buildTrRecord(goldenList,resultParamMap);
					if(trRecordList.size()>0){
						/**
						 * 保存入金记录
						 */
						logger.info("保存入金记录.入参:"+trRecordList);
						trRecordMapper.insertList(trRecordList);
						
						/**
						 * 修改用户账户余额
						 */
						logger.info("修改用户账户余额.入参:"+changeMoney);
						resultParamMap.put("changeMoney", changeMoney);
						int countAccount= userAccountMapper.updateUserMoneyByUserId(resultParamMap);
						logger.info("修改用户账户余额.出参:"+countAccount);
						/**
						 * 修改支付通道可用余额
						 */
						logger.info("修改支付通道可用余额.入参:"+changeMoney);
						int countPayChanne= userPayChannelMapper.updateUserMoneyByUserIdPCId(resultParamMap);
						logger.info("修改支付通道可用余额.出参:"+countPayChanne);
						
						
						resultMap.put("resultCode", "success");
					}
					
					resultMap.put("synCount", goldenList.size());//同步记录
					resultMap.put("skipCount", goldenList.size()-count);//跳过记录
					
				}else if(resDlstrndt!=null
						&& resDlstrndt.getStatus().equals(ReqResult.PBRA001.getCode())){
					/**
					 * 没有符合条件的记录
					 */
					logger.info(ReqResult.PBRA001.getMessage());
					resultMap.put("resultCode", "success");
					resultMap.put("resultMesg", ReqResult.PBRA001.getMessage());
					
				}else{
					bankLog.setReplyStatus(1);
					/**
					 * 没有入金记录
					 */
					logger.info("没有入金记录.");
					resultMap.put("resultCode", "success");
					resultMap.put("resultMesg", "没有入金记录");
					
				}
				
			}else if(resDlstrndt!=null&& resDlstrndt.getStatus().equals(ReqResult.PBRA001.getCode())){
				/**
				 * 没有入金记录
				 */
				logger.info(ReqResult.PBRA001.getMessage());
				resultMap.put("resultCode", "success");
				resultMap.put("resultMesg", ReqResult.PBRA001.getMessage());
				
			}else{
				
				logger.info("请求中信失败，结束操作");
				resultMap.put("resultCode","exce");
				resultMap.put("resultMesg", "没有入金记录");
				
			}
			/**
			 * 保存请求日志
			 */
			bankLogMapper.insertSelective(bankLog);
			
		} catch (Exception e) {
			resultMap.put("resultCode","exce");
			logger.error("同步入金记录发送异常."+e.getMessage());
		}
		
		return resultMap;
	}

	/**
	 * 交易记录参数
	 */
	public List<TrRecord> buildTrRecord(List<ResDlstrndtRow> goldenList,Map<String, Object> resultParamMap){
		List<TrRecord> trRecordList=new ArrayList<TrRecord>();
		TrRecord tr=null;
		try {
			for(int i=0;i<goldenList.size();i++){
				tr=new TrRecord();
				//交易金额
				tr.setTrMoney(goldenList.get(i).getTRANAMT());
				//交易类型(23&&1=入金)
				tr.setTrType(3);
				//交易请求类型
				tr.setTrApplyTime(DateUtil.doFormatDate(new Date(), "yyyy-MM-dd HH:mm:ss"));
				//交易请求状态
				tr.setTrApplyStatus(1);
				//交易手续费
				tr.setTrCharge(goldenList.get(i).getXTSFAM());
				//交易流水号（20为数字+字母）
				//tr.setTrWaterId();
				//本交易执行后用户账户余额
				tr.setUserMoney(goldenList.get(i).getACCBAL());
				//支付用户ID
				tr.setUserId(Integer.valueOf(String.valueOf(resultParamMap.get("userId"))));
				//附属账号
				tr.setUserPayChannelId((Integer) resultParamMap.get("userPayChannelId"));
				//用户公司名称
				tr.setUserCompanyName((String)resultParamMap.get("subAccNm"));
				//出金银行名称
				tr.setOutcomeBankName(goldenList.get(i).getOPPBRANCHNAME());
				//出金银行卡号
				tr.setOutcomeBankAccountNo(goldenList.get(i).getOPPACCNO());
				//出金银行支行联行号
				tr.setOutcomeBankCnaps(goldenList.get(i).getOPPBANKNO());
				//tr.setPrintCheckCode();
				//支付渠道ID
				tr.setPayChannelId((Integer)resultParamMap.get("payChannelId"));
				//反馈时间
				tr.setReplayTime(DateUtil.doFormatDate(new Date(), "yyyy-MM-dd HH:mm:ss"));
				//摘要
				tr.setNote(goldenList.get(i).getRESUME());
				//支付渠道反馈的交易时间
				tr.setPayChannelTrTime(goldenList.get(i).getTRANDATE()+" "+goldenList.get(i).getTRANTIME());
				//支付渠道返回的柜员交易号
				tr.setPayChannelTrNo(goldenList.get(i).getHOSTFLW());
				
				trRecordList.add(tr);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return trRecordList;
	}

}


