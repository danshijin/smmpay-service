package com.smmpay.payment.impl;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.fastjson.JSONObject;
import com.smmpay.payment.TransferResultService;
import com.smmpay.process.QueryTRStatusProcess;
import com.smmpay.process.enumDef.ReqType;
import com.smmpay.respository.dao.DaPayChannelMapper;
import com.smmpay.respository.dao.TrPaymentRecordMapper;
import com.smmpay.respository.dao.TrRecordMapper;
import com.smmpay.respository.dao.TrTransferRecordMapper;
import com.smmpay.respository.dao.UserAccountMapper;
import com.smmpay.respository.dao.UserPayChannelMapper;
import com.smmpay.respository.model.DaPayChannel;
import com.smmpay.respository.model.TrPaymentRecord;
import com.smmpay.respository.model.TrRecord;
import com.smmpay.respository.model.TrTransferRecord;
import com.smmpay.respository.model.UserAccount;
import com.smmpay.respository.model.UserPayChannel;
import com.smmpay.util.Base64;
import com.smmpay.util.DateUtil;
import com.smmpay.util.DesCrypt;
/**
 * 2015-12-14
 * @author wanghao
 *
 */
@Service("transferResultServiceImpl")
public class TransferResultServiceImpl implements TransferResultService{
	private Logger log = Logger.getLogger(this.getClass());
	@Autowired
	private UserAccountMapper userAccountMapper;
	@Autowired
	private TrPaymentRecordMapper trPaymentRecordMapper;
	@Autowired
	private TrRecordMapper recordMapper;
	@Autowired
	private DaPayChannelMapper channelDao;
	@Autowired
	private TrTransferRecordMapper transRecordMapper;
	@Autowired
	private UserPayChannelMapper userChannelDao;
	
	@Transactional(rollbackFor = Exception.class)
	public void TransferResult() throws Exception{
		log.info("-------------------开始查询转账记录-------------------------");
		List<TrTransferRecord> listTransfer =transRecordMapper.queryList();
		log.info("-------------------查询转账记录------------------------- "+listTransfer.size());
		for(TrTransferRecord record:listTransfer){
			String columns = record.getClientId().toString() + record.getPaymentId().toString() + record.getOutUserId().toString() + record.getInUserId().toString() + record.getApplyTime();
			byte[] desColumns = DesCrypt.encryptMode(columns.getBytes("utf-8"));
			String columns_str = Base64.getBase64(desColumns);
			log.info("columns:"+columns);
			log.info("columns_str:"+columns_str);
			log.info("record     :"+record.getVerifyCode());
			log.info("ff:"+columns_str.equals(record.getVerifyCode()));
			if(columns_str.equals(record.getVerifyCode())){//校验字段合法性
				Map<String, Object> param = new HashMap<String, Object>();
				param.put("clientID", record.getClientId());
				param.put("type", ReqType.DLMDETRN.getCode());
				QueryTRStatusProcess fristProcess = QueryTRStatusProcess.getInstanceWithParam(param);//查询交易状态
				try {
					Map<String, Object> fristResultMap=fristProcess.process(null);
					
					//接收信息
					String msg=fristResultMap.get("statusText")!=null?String.valueOf(fristResultMap.get("statusText")):"";
					log.info("-------------------转账中信返回: "+JSONObject.toJSONString(fristResultMap));
					//如果成功
					if(fristResultMap.get("resultCode").equals("success")){
						log.info("-------------------转账中信返回成功处理: "+fristResultMap.get("resultCode"));
						//更改转账记录为成功
						record.setStatus(1);
						record.setResponseTime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
						record.setDesc(msg);
						transRecordMapper.updateByPrimaryKey(record);
						
						//更改支付记录状态
						TrPaymentRecord trPaymentRecord = trPaymentRecordMapper.selectByPrimaryKey(record.getPaymentId());
						trPaymentRecord.setPaymentStatus(2);
						trPaymentRecordMapper.updateByPrimaryKey(trPaymentRecord);
						
						TrPaymentRecord pr = new TrPaymentRecord();
						pr.setSellerUserId(trPaymentRecord.getBuyerUserId());
						pr.setBuyerUserId(trPaymentRecord.getSellerUserId());
						pr.setPaymentNo(trPaymentRecord.getPaymentNo());
						List<TrPaymentRecord> prList = trPaymentRecordMapper.selectByParam(pr);
						if(prList != null && prList.size() != 0){
							pr = prList.get(0);
							pr.setPaymentStatus(7);
							trPaymentRecordMapper.updateByPrimaryKey(pr);
						}
						
						
						UserAccount userAccount = userAccountMapper.selectByPrimaryKey(Integer.parseInt(trPaymentRecord.getSellerUserId()));
						
						
						//生成交易记录
						// 获取订单支付渠道
						DaPayChannel orderChannel = channelDao.selectDefault();
						
						TrRecord trRecordBuy = new TrRecord();
						
						// 获取买家渠道信息
						UserPayChannel buyerChannel = new UserPayChannel();
						buyerChannel.setUserId(userAccount.getUserId());
						buyerChannel.setPayChannelId(orderChannel.getPayChanneId());
						buyerChannel = userChannelDao
								.queryPayChannelByUserIdAndOrderChannel(buyerChannel);
						trRecordBuy.setTrApplyStatus(1);
						trRecordBuy.setUserFreezeMoney(userAccount.getFreezeMoney());
						trRecordBuy.setPayChannelTrTime(DateUtil.doFormatDate(new Date(), "yyyy-MM-dd HH:mm:ss"));
						trRecordBuy.setUserMoney(userAccount.getUseMoney().subtract(trPaymentRecord.getDealMoney()));

						trRecordBuy.setTrMoney(trPaymentRecord.getDealMoney());
						trRecordBuy.setTrType(0);
						trRecordBuy.setTrApplyTime(DateUtil.doFormatDate(new Date(), "yyyy-MM-dd HH:mm:ss"));
					
						trRecordBuy.setTrWaterId(record.getClientId());
						trRecordBuy.setUserId(userAccount.getUserId());
						trRecordBuy.setUserPayChannelId(buyerChannel.getPayChannelId());
						trRecordBuy.setUserCompanyName(trPaymentRecord.getSellerCompanyName());
						trRecordBuy.setReplayTime(DateUtil.doFormatDate(new Date(), "yyyy-MM-dd HH:mm:ss"));
						
						UserAccount userAccountSeller = userAccountMapper.selectByPrimaryKey(Integer.parseInt(trPaymentRecord.getBuyerUserId()));
						// 获取买家渠道信息
						UserPayChannel sellerChannel = new UserPayChannel();
						sellerChannel.setUserId(userAccountSeller.getUserId());
						sellerChannel.setPayChannelId(orderChannel.getPayChanneId());
						sellerChannel = userChannelDao
								.queryPayChannelByUserIdAndOrderChannel(sellerChannel);
						trRecordBuy.setOppositUserId(userAccountSeller.getUserId());
						trRecordBuy.setOppositUserPayChannelId(sellerChannel.getPayChannelId());
						trRecordBuy.setOppositCompanyName(trPaymentRecord.getBuyerCompanyName());
						recordMapper.insertSelective(trRecordBuy);
						
						TrRecord trRecordSeller = new TrRecord();
						trRecordSeller.setTrApplyStatus(1);
						trRecordSeller.setUserFreezeMoney(userAccountSeller.getFreezeMoney());
						trRecordSeller.setPayChannelTrTime(DateUtil.doFormatDate(new Date(), "yyyy-MM-dd HH:mm:ss"));
						trRecordSeller.setUserMoney(userAccountSeller.getUseMoney().subtract(trPaymentRecord.getDealMoney()));

						trRecordSeller.setTrMoney(trPaymentRecord.getDealMoney());
						trRecordSeller.setTrType(1);
						trRecordSeller.setTrApplyTime(DateUtil.doFormatDate(new Date(), "yyyy-MM-dd HH:mm:ss"));
					
						trRecordSeller.setTrWaterId(record.getClientId());
						trRecordSeller.setUserId(userAccountSeller.getUserId());
						trRecordSeller.setUserPayChannelId(sellerChannel.getPayChannelId());
						trRecordSeller.setUserCompanyName(trPaymentRecord.getBuyerCompanyName());
						trRecordSeller.setReplayTime(DateUtil.doFormatDate(new Date(), "yyyy-MM-dd HH:mm:ss"));
						
					
						trRecordSeller.setOppositUserId(userAccount.getUserId());
						trRecordSeller.setOppositUserPayChannelId(buyerChannel.getPayChannelId());
						trRecordSeller.setOppositCompanyName(trPaymentRecord.getSellerCompanyName());
						recordMapper.insertSelective(trRecordSeller);
						
						//更新用户余额
						userAccount.setUseMoney(userAccount.getUseMoney().add(trPaymentRecord.getDealMoney()));
						userAccountMapper.updateByPrimaryKey(userAccount);
						
						userAccountSeller.setUseMoney(userAccount.getUseMoney().subtract(trPaymentRecord.getDealMoney()));
						userAccountMapper.updateByPrimaryKey(userAccountSeller);
						
						
						buyerChannel.setUseMoney(buyerChannel.getUseMoney().add(trPaymentRecord.getDealMoney()));
						sellerChannel.setUseMoney(sellerChannel.getUseMoney().subtract(trPaymentRecord.getDealMoney()));
						userChannelDao.updateByPrimaryKey(buyerChannel);
						userChannelDao.updateByPrimaryKey(sellerChannel);
						
					}else if(fristResultMap.get("resultCode").equals("exce")){
						//更改转账记录为成功
						log.info("--------------------------中信返回记录失败处理: "+JSONObject.toJSONString(fristResultMap));
						record.setStatus(2);
						record.setResponseTime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
						record.setDesc(msg);
						transRecordMapper.updateByPrimaryKey(record);
					}
				} catch (Exception e) {
					e.printStackTrace();
					log.error("处理异常"+e);
					throw e;
				}
			}else{
					log.info("--------------------------加密校验失败,数据被篡改:支付编号 "+record.getPaymentId());
					record.setStatus(0);
					record.setResponseTime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
					transRecordMapper.updateByPrimaryKey(record);
			}
			
		}
		log.info("-------------------结束查询转账记录-------------------------");
		
	}

	
}
