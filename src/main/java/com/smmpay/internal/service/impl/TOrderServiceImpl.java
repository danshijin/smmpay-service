package com.smmpay.internal.service.impl;

import java.lang.reflect.InvocationTargetException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.smmpay.common.ResErrorCode;
import com.smmpay.inter.dto.res.ReturnDTO;
import com.smmpay.inter.dto.res.TrPaymentRecordDTO;
import com.smmpay.internal.service.TOrderService;
import com.smmpay.job.PayFreezeResultNotifyJob;
import com.smmpay.payment.Payment;
import com.smmpay.respository.dao.DaPayChannelMapper;
import com.smmpay.respository.dao.SPaymentLogMapper;
import com.smmpay.respository.dao.TrPaymentRecordMapper;
import com.smmpay.respository.dao.UserAccountMapper;
import com.smmpay.respository.dao.UserPayChannelMapper;
import com.smmpay.respository.model.DaPayChannel;
import com.smmpay.respository.model.SPaymentLog;
import com.smmpay.respository.model.TrPaymentRecord;
import com.smmpay.respository.model.UserAccount;
import com.smmpay.respository.model.UserPayChannel;
import com.smmpay.service.base.BaseService;
import com.smmpay.util.Base64;
import com.smmpay.util.DatabaseEncryptUtil;
import com.smmpay.util.DesCrypt;
import com.smmpay.util.SPaymentLogUtils;

@Service("TorderService")
public class TOrderServiceImpl extends BaseService implements TOrderService {

	private Logger log = Logger.getLogger(this.getClass());
	@Autowired
	private TrPaymentRecordMapper orderDao;
	@Autowired
	private UserAccountMapper userDao;
	@Autowired
	private DaPayChannelMapper channelDao;
	@Autowired
	private UserPayChannelMapper userChannelDao;
	@Autowired
	private Payment paymentService;
	@Autowired
	private SPaymentLogMapper pLogMapper;

	@Autowired
	private PayFreezeResultNotifyJob emailJob;


	/**
	 * 插入支付订单
	 */
	@Transactional(rollbackFor = Exception.class)
	public ReturnDTO insertPayOrder(Map<String, String> map) {
		TrPaymentRecordDTO trPaymentRecordDTO = new TrPaymentRecordDTO();
		// TODO Auto-generated method stub
		try {
			log.info("传入的map:"+JSON.toJSONString(map));
			BeanUtils.populate(trPaymentRecordDTO, map);
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		log.info("买家："+trPaymentRecordDTO.getBuyerMallUserName());
		log.info("卖家："+trPaymentRecordDTO.getSellerMallUserName());
		
		/**
		 * 退补款记录开始---20151202
		 */
		synchronized(this){
			TrPaymentRecord recordCheck = new TrPaymentRecord();
			recordCheck.setMallOrderId(trPaymentRecordDTO.getMallOrderId());
		    recordCheck.setPayType(0);
			List<TrPaymentRecord> listRecord = orderDao.selectBySelective(recordCheck);
	        if(trPaymentRecordDTO.getPayType() != 0){//支付记录为商城退补款订单5282224.43
	        	if(listRecord == null || listRecord.size() == 0){
	    			return new ReturnDTO("000006",false,"原始支付记录不存在");
	    		}
	        	recordCheck.setPayType(trPaymentRecordDTO.getPayType());
	        	listRecord = orderDao.selectBySelective(recordCheck);
	        	if(listRecord != null && listRecord.size() > 0){
	        		return new ReturnDTO("000008",false,"此订单已经有退补款订单");
	        	}
			}else{
				if(listRecord != null && listRecord.size() > 0){
	    			return new ReturnDTO("000007",false,"此订单已经有支付记录");
	    		}
			}
	        /**
			 * 退补款记录结束---20151202---同步重复插入
			 */
			
				ReturnDTO returnDTO = new ReturnDTO();
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				// 获取买家公司信息
				UserAccount buyer = new UserAccount();
				
				buyer.setMallUserName(trPaymentRecordDTO.getBuyerMallUserName());
				buyer = userDao.findAccountByMallUserName(buyer);
	
				log.info("买家："+buyer.getUserId());
				// 获取卖家公司信息
				UserAccount seller = new UserAccount();
				seller.setMallUserName(trPaymentRecordDTO.getSellerMallUserName());
				seller = userDao.findAccountByMallUserName(seller);
				// 获取订单支付渠道
				DaPayChannel orderChannel = channelDao.selectDefault();
				// 获取买家渠道信息
				UserPayChannel buyerChannel = new UserPayChannel();
				buyerChannel.setUserId(buyer.getUserId());
				buyerChannel.setPayChannelId(orderChannel.getPayChanneId());
				buyerChannel = userChannelDao
						.queryPayChannelByUserIdAndOrderChannel(buyerChannel);
				// 获取卖家渠道信息
				UserPayChannel sellerChannel = new UserPayChannel();
				sellerChannel.setUserId(seller.getUserId());
				log.info("卖家："+seller.getUserId());
				sellerChannel.setPayChannelId(orderChannel.getPayChanneId());
				sellerChannel = userChannelDao
						.queryPayChannelByUserIdAndOrderChannel(sellerChannel);
				// 插入支付订单
				TrPaymentRecord record = new TrPaymentRecord();
				record.setDealMoney(trPaymentRecordDTO.getDealMoney());// 交易金额
				record.setDealType(trPaymentRecordDTO.getDealType());// 交易类型
				
				record.setPayType(trPaymentRecordDTO.getPayType());//退补款记录---20151202
				Map<String,Integer> mapParam = new HashMap<String,Integer>();
				
				// 增加退补款判断
				if(trPaymentRecordDTO.getPayType() != 0) {
					mapParam.put("codeType", 3);
				} else {
					mapParam.put("codeType", 0);
				}

				mapParam.put("userId", buyer.getUserId());
				mapParam.put("source", 0);
				String paymentNo = orderDao.getPaymentCode(mapParam);
				log.info("paymentNo:"+paymentNo);
				
				if(paymentNo !=null && !"".equals(paymentNo)){
					record.setPaymentNo(paymentNo);
				}
				record.setNotifyUrl(trPaymentRecordDTO.getNotifyUrl());
				record.setBuyerCompanyName(buyer.getCompanyName());// 买家公司名称
				record.setBuyerMallUserName(buyer.getMallUserName());// 买家商城用户名
				record.setBuyerPayChannelId(buyerChannel.getUserPayChannelId());// 买家用户支付渠道id
				record.setBuyerUserId(buyer.getUserId().toString());// 买家支付用户ID
	
				record.setSellerCompanyName(seller.getCompanyName());// 卖家公司名称
				record.setSellerMallUserName(seller.getMallUserName());// 卖家商城用户名
				record.setSellerPayChannelId(sellerChannel.getUserPayChannelId());// 卖家用户支付渠道id
				record.setSellerUserId(seller.getUserId().toString());// 卖家支付用户ID
	
				record.setMallOrderId(trPaymentRecordDTO.getMallOrderId());// 商城订单号
				record.setProductName(trPaymentRecordDTO.getProductName());// 商品名称
				record.setProductNum(trPaymentRecordDTO.getProductNum());// 商品数量
				record.setProductNumUnit(trPaymentRecordDTO.getProductNumUnit());// 商品数量单位
				record.setProductDetail(trPaymentRecordDTO.getProductDetail());// 商品明细
				record.setPaymentCode(trPaymentRecordDTO.getPaymentCode());// 支付码
				record.setPayChannelId(orderChannel.getPayChanneId());// 支付渠道ID
				record.setCreateTime(sdf.format(new Date()));// 交易记录创建时间
				record.setInvoice(trPaymentRecordDTO.getInvoice());// 发票说明
				record.setPaymentType(trPaymentRecordDTO.getPaymentType());// 付款方式
				record.setSettlementType(trPaymentRecordDTO.getSettlementType());// 交收方式
				record.setOrderCreateTime(trPaymentRecordDTO.getOrderCreateTime());// 订单创建时间
				record.setIsHandler(0);// 是否手动处理
				record.setPaymentStatus(0);// 支付状态
				record.setPaymentCode(getRandNum(2)+getRandStr(4)+getRandNum(2));
				
				if (orderDao.insertNew(record) > 0) {
					TrPaymentRecord paymentRecord = orderDao.selectByPrimaryKey(record.getPaymentId());
				
					try{
						
						List<Object> paramList = new ArrayList<Object>();
						paramList.add(paymentRecord.getBuyerUserId());
						paramList.add(buyer.getMallUserName());
						paramList.add(paymentRecord.getSellerUserId());
						
						paramList.add(seller.getMallUserName());
						paramList.add(paymentRecord.getMallOrderId());
						paramList.add(paymentRecord.getDealMoney());
						
						paramList.add(paymentRecord.getOrderCreateTime());
					
						log.info("支付ID:"+record.getPaymentId()+"" + paramList.toString());
						String databaseCipher = DatabaseEncryptUtil.encrypt(paramList);
						TrPaymentRecord trecord = new TrPaymentRecord();
						trecord.setPaymentId(record.getPaymentId());
						trecord.setVerifyCode(databaseCipher);
						orderDao.updateByPrimaryKeySelective(trecord);
					}catch(Exception e){
						e.printStackTrace();
					}
					
					
					//如果是买家补款,发送邮件提醒买家入金
					if(record.getPayType() == 0){
						emailJob.sendEmail(record, "applyPaymentMail.html", "订单入金", Boolean.TRUE);
					}else if(record.getPayType() == 1){
						emailJob.sendEmail(record, "goldenRemindMail.html", "差额入金", Boolean.TRUE);
					}else if(record.getPayType() == 2){
						emailJob.sendEmail(record, "refundGoldenRemindMail.html", "退款入金", Boolean.TRUE);
					}
					
					//插入创建交易记录日志start
					SPaymentLog pLog = new SPaymentLog();
					pLog.setPaymentId(record.getPaymentId());
					pLog.setOperationName("创建交易记录");
					pLog.setCreateTime(sdf.format(new Date()));
					pLog.setOperationResult("成功");
					pLog.setOpetationDesc("商城订单编号"+record.getMallOrderId()+",交易金额："+record.getDealMoney()+"元");
					pLog.setCreateTime(sdf.format(new Date()));
					new SPaymentLogUtils().write(pLog, pLogMapper);
					//插入创建交易记录日志end
					
					returnDTO.setMsg(ResErrorCode.RETURN_SUCCESS_MSG);
					returnDTO.setStatus(ResErrorCode.RETURN_SUCCESS_CODE);
					Map<String, Object> paramMap = new HashMap<String, Object>();
					paramMap.put("paymentId", record.getPaymentId());
					paramMap.put("userId", record.getBuyerUserId());
					paramMap.put("dealMoney", record.getDealMoney());
					Map<String, Object> pmap = null;
					Map<String, String> dataMap = new HashMap<String, String>();
					try {
						//调用银企直联
						pmap = paymentService.payment(paramMap);
						log.info("pmap"+JSONObject.toJSONString(pmap));
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						log.error("调用支付接口异常");
						returnDTO.setMsg(ResErrorCode.RETURN_EXCE_MSG);
						returnDTO.setStatus(ResErrorCode.RETURN_EXCE_CODE);
					}
					dataMap.put("paymentId", String.valueOf(record.getPaymentId()));
					log.info("paymentNo:"+record.getPaymentNo());
					dataMap.put("paymentNo", record.getPaymentNo());
					if (pmap.get("resultCode").equals("success")) {
						dataMap.put("freezeStatus", "冻结中");
						returnDTO.setMsg(ResErrorCode.RETURN_SUCCESS_MSG);
						returnDTO.setStatus(ResErrorCode.RETURN_SUCCESS_CODE);
						
						
					}else{
						returnDTO.setMsg(ResErrorCode.RETURN_EXCE_MSG);
						returnDTO.setStatus(ResErrorCode.RETURN_EXCE_CODE);
					}
					returnDTO.setData(dataMap);
				} else {
					//插入创建交易记录日志start
					SPaymentLog pLog = new SPaymentLog();
					pLog.setPaymentId(record.getPaymentId());
					pLog.setOperationName("创建交易记录");
					pLog.setCreateTime(sdf.format(new Date()));
					pLog.setOperationResult("失败");
					pLog.setOpetationDesc("商城订单编号"+record.getMallOrderId()+",交易金额："+record.getDealMoney()+"元");
					pLog.setCreateTime(sdf.format(new Date()));
					new SPaymentLogUtils().write(pLog, pLogMapper);
					//插入创建交易记录日志end
					returnDTO.setMsg(ResErrorCode.RETURN_EXCE_MSG);
					returnDTO.setStatus(ResErrorCode.RETURN_EXCE_CODE);
				}
				
				return returnDTO;
		}
		
	}
	
	
	public static String getRandStr(int charCount) {
		String charValue = "";
		for (int i = 0; i < charCount; i++){
		char c = (char) (randomInt(0,26)+'a');
		charValue += String.valueOf(c);
		}
		return charValue;
	}
	
	public static String getRandNum(int charCount) {
		String charValue = "";
		for (int i = 0; i < charCount; i++){
		char c = (char) (randomInt(0,10)+'0');
		charValue += String.valueOf(c);
		}
		return charValue;
	}
	public static int randomInt(int from, int to){
		Random r = new Random();
		return from + r.nextInt(to - from);
	}
	
	public TrPaymentRecordDTO findPaymentRecord(Integer primaryKey) {
		TrPaymentRecord record = orderDao.selectByPrimaryKey(primaryKey);
		TrPaymentRecordDTO recordDTO = null;
		try{
			recordDTO = copy(record, TrPaymentRecordDTO.class);
		}catch(Exception e){
			e.printStackTrace();
		}
		return recordDTO;
	}


	public ReturnDTO getMarketStatus() {
		int code = orderDao.getMarketCode();
		if(code == 0){
			return new ReturnDTO(ResErrorCode.DECRYPTPARAM_ERROR_CODE,false,ResErrorCode.DECRYPTPARAM_ERROR_MSG);
		}else{
			return new ReturnDTO(ResErrorCode.RETURN_SUCCESS_CODE,true,ResErrorCode.RETURN_SUCCESS_MSG);
		}
	}
	
	
	
}
