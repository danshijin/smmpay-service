package com.smmpay.payment.impl;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.fastjson.JSONObject;
import com.smmpay.common.ResErrorCode;
import com.smmpay.inter.dto.res.ReturnDTO;
import com.smmpay.internal.service.IDatabaseEncrypt;
import com.smmpay.internal.service.ValidDTO;
import com.smmpay.payment.ManualUNFreezeService;
import com.smmpay.process.enumDef.CommonSatus;
import com.smmpay.process.enumDef.PaymentSatus;
import com.smmpay.process.enumDef.ReqResult;
import com.smmpay.process.enumDef.TranType;
import com.smmpay.respository.dao.SBankLogMapper;
import com.smmpay.respository.dao.TrFreezeRecordMapper;
import com.smmpay.respository.dao.TrPaymentRecordMapper;
import com.smmpay.respository.dao.TrUnfreezeRecordMapper;
import com.smmpay.respository.dao.UserAccountMapper;
import com.smmpay.respository.dao.UserPayChannelMapper;
import com.smmpay.respository.model.ReqDlmdetrn;
import com.smmpay.respository.model.SBankLog;
import com.smmpay.respository.model.TrFreezeRecord;
import com.smmpay.respository.model.TrPaymentRecord;
import com.smmpay.respository.model.TrUnfreezeRecord;
import com.smmpay.respository.model.UserAccount;
import com.smmpay.respository.model.UserPayChannel;
import com.smmpay.respository.model.base.ResCommon;
import com.smmpay.util.CPayUtils;
import com.smmpay.util.ClientIdUtils;
import com.smmpay.util.DateUtil;
import com.smmpay.util.XMLUtils;

/**
 *  手动解冻
 * 
 */
@Service("manualUNFreezeService")
public class ManualUNFreezeServiceImpl implements ManualUNFreezeService {

	private Logger logger=Logger.getLogger(this.getClass());
	
	@Autowired
	TrPaymentRecordMapper trPaymentRecordMapper;
	
	@Autowired
	UserPayChannelMapper userPayChannelMapper;
	
	@Autowired
	TrFreezeRecordMapper freezeRecordMapper;
	
	@Autowired
	TrUnfreezeRecordMapper trUnfreezeRecordMapper;
	
	@Autowired
	SBankLogMapper bankLogMapper;
	
	@Autowired
    ValidDTO validDTO;
	
	@Autowired
	private TrPaymentRecordMapper orderDao;
	
	@Autowired
	private UserAccountMapper userDao;
	
	@Autowired
	private IDatabaseEncrypt iDatabaseEncrypt;
	
	@Value("#{ysw['YSWTKZH.userPayChannelId']}")
	String userPayChannelId;
	
	@Value("#{ysw['YSWTKZH.userId']}")
	String userId;

	/**
	 * 支付记录ID:paymentId 
	 */
	@Transactional(rollbackFor = Exception.class)
	public synchronized ReturnDTO manualUNFreezeHandle(Map<String, Object> paramMap)throws Exception {
		
		ReturnDTO returnDTO = new ReturnDTO();
		
		try {
			
			/** 获取支付记录*/
			Integer paymentId = Integer.valueOf(String.valueOf(paramMap.get("paymentId")));
			TrPaymentRecord paymentRecord = trPaymentRecordMapper.selectByPrimaryKey(paymentId);
			System.out.println("paymentId:"+paymentId+" "+JSONObject.toJSONString(paymentRecord));
			if(paymentRecord==null){
				
				return validDTO.returnError(ResErrorCode.UNFREEZR_ERROR_CODE_1, ResErrorCode.UNFREEZR_ERROR_MSG_1, returnDTO);
			}
			
			//是否有补价单子-------解冻之前判断是否有补价单子--开始
			Map<String, Object> buJiaMap=new HashMap<String, Object>();
			buJiaMap.put("paymentId",paymentId);
			returnDTO=this.whetherBuJiaOrder(buJiaMap);

			if(!returnDTO.isSuccess()) return returnDTO;



//			if(paymentRecord.getPayType() == 0){
//				TrPaymentRecord paymentRecordExten = new TrPaymentRecord();
//				paymentRecordExten.setMallOrderId(paymentRecord.getMallOrderId());
//				paymentRecordExten.setPayType(1);
//				List<TrPaymentRecord> listExten = trPaymentRecordMapper.selectBySelective(paymentRecordExten);
//				if(listExten != null && listExten.size() > 0){
//					paymentRecordExten = listExten.get(0);
//					Map mapParam = new HashMap();
//					mapParam.put("paymentId", paymentRecordExten.getPaymentId());
//					TrFreezeRecord fRecord = freezeRecordMapper.queryByPaymentId(mapParam);
//
//					Map<String,String> map = new LinkedHashMap<String,String>();
//					if(fRecord != null){
//						returnDTO.setStatus(ResErrorCode.UNFREEZR_ERROR_CODE_9);
//						returnDTO.setMsg(ResErrorCode.UNFREEZR_ERROR_MSG_9);
//						returnDTO.setSuccess(false);
//
//					}else{//如果没冻结就把补价单子关闭
//						mapParam.put("freezeStatus", 0);
//						fRecord = freezeRecordMapper.queryFreezeRecordByStatus(mapParam);
//						if(fRecord != null){//如果不为空，查看冻结结果
//							returnDTO.setStatus(ResErrorCode.UNFREEZR_ERROR_CODE_10);
//							returnDTO.setMsg(ResErrorCode.UNFREEZR_ERROR_MSG_10);
//							returnDTO.setSuccess(false);
//							returnDTO.setData(paymentRecordExten);
//						}else{
//							returnDTO.setStatus(ResErrorCode.UNFREEZR_ERROR_CODE_11);
//							returnDTO.setMsg(ResErrorCode.UNFREEZR_ERROR_MSG_11);
//							returnDTO.setSuccess(false);
//						}
//
//					}
//					map.put("paymentNo", paymentRecordExten.getPaymentNo());
//					returnDTO.setData(paymentRecordExten);
//					return returnDTO;
//				}
//			}
//
			//是否有补价单子-------解冻之前判断是否有补价单子--结束
			
			/** 获取原冻结记录*/
			TrFreezeRecord freezeRecord = freezeRecordMapper.queryByPaymentId(paramMap);
			if(freezeRecord==null){
				return validDTO.returnError(ResErrorCode.UNFREEZR_ERROR_CODE_2, ResErrorCode.UNFREEZR_ERROR_MSG_2, returnDTO);
			}
			
			/** 更新对应支付记录状态为“已关闭”*/
			paymentRecord.setPaymentStatus(PaymentSatus.PS3.getCode());
			Integer pCount=trPaymentRecordMapper.updatePaymentStatus(paymentRecord);
			if(pCount>0){
				/**
				 * 复制支付记录。卖方为“有色网退款账户”，支付状态“已冻结”
				 */
				UserPayChannel yswPayChannel=userPayChannelMapper.selectByPrimaryKey(Integer.valueOf(String.valueOf(userPayChannelId)));//有色网退款账户/收款方

				//如果有色网退款账户没有配置，抛出异常
				if(yswPayChannel==null) throw new Exception("有色网退款专用账户未开通，请联系管理员");

				UserPayChannel buyPayChannel=userPayChannelMapper.selectByPrimaryKey(paymentRecord.getBuyerPayChannelId());//付款方
				System.out.println("paymentId:"+paymentId+" "+JSONObject.toJSONString(paymentRecord));
				TrPaymentRecord newPR = new TrPaymentRecord();
				try{
					BeanUtils.copyProperties(paymentRecord,newPR);
				}catch(Exception e){
					e.printStackTrace();
				}
				
				System.out.println("paymentId:"+paymentId+" "+JSONObject.toJSONString(newPR));
				System.out.println("dealType:"+paymentRecord.getDealType());
				newPR.setPaymentStatus(PaymentSatus.PS1.getCode());
				newPR.setSellerCompanyName(yswPayChannel.getUserAccountName());
				newPR.setSellerUserId(String.valueOf(yswPayChannel.getUserId()));
				newPR.setSellerPayChannelId(yswPayChannel.getUserPayChannelId());
				newPR.setPaymentId(null);
				newPR.setPaymentCode(null);
				newPR.setPaymentNo(getPaymentNoFromStoredProcedure(newPR));
				Integer saveCount=trPaymentRecordMapper.insertSelective(newPR);
				if(saveCount>0){
					/**
					 * 更新原冻结记录、关联新支付记录编号
					 */
					Integer newPaymentId = newPR.getPaymentId();
					TrFreezeRecord ufr = new TrFreezeRecord();
					ufr.setPaymentId(newPaymentId);
					ufr.setFreezeId(freezeRecord.getFreezeId());
					Integer uFRCount = freezeRecordMapper.updateByPrimaryKeySelective(ufr);
					if(uFRCount>0){
						
						/**
						 * 生成解冻记录
						 */
						String clientID=ClientIdUtils.CreateClientId();
						String applyTime=DateUtil.doFormatDate(new Date(), "yyyy-MM-dd HH:mm:ss");
						TrUnfreezeRecord unfreezeRecord = new TrUnfreezeRecord();
						unfreezeRecord.setFreezeId(freezeRecord.getFreezeId());
						unfreezeRecord.setUnfreezeApplyTime(applyTime);
						unfreezeRecord.setUnfreezeStatus(CommonSatus.CS0.getCode());
						unfreezeRecord.setRecevieUserId(yswPayChannel.getUserId());//收款用户编号
						unfreezeRecord.setRecevieUserName(yswPayChannel.getUserAccountName());//收款用户企业名
						unfreezeRecord.setRecevieUserPayChannelId(yswPayChannel.getUserPayChannelId());//收款用户支付渠道关联编号
						unfreezeRecord.setPayChannelId(newPR.getPayChannelId());
						unfreezeRecord.setUnfreezeClientId(clientID);
						Integer unCount = trUnfreezeRecordMapper.insertSelective(unfreezeRecord);
						
						// 添加数据验证字段
						logger.info("解冻记录表，手动解冻添加verifyCode");
						iDatabaseEncrypt.addUnFreezeVerifyCode(unfreezeRecord.getId());
						if(unCount>0){
							
							/**
							 * 发送“解冻支付”指令
							 */
							ReqDlmdetrn reqDl = new ReqDlmdetrn();
							reqDl.setPayAccNo(buyPayChannel.getUserAccountNo());
							reqDl.setTranType(TranType.BH.getCode());
							reqDl.setRecvAccNm(yswPayChannel.getUserAccountName());
							reqDl.setRecvAccNo(yswPayChannel.getUserAccountNo());
							reqDl.setTranAmt(newPR.getDealMoney().setScale(2,BigDecimal.ROUND_HALF_UP));
							reqDl.setFreezeNo(freezeRecord.getFreezeNo());
							reqDl.setTranFlag("0");
							reqDl.setClientID(clientID);
							
							String sendXML=XMLUtils.beanToXML(reqDl);
							/**
							 * 请求中信冻结资金接口
							 */
							logger.info("请求中信接口，解冻支付.入参:"+sendXML);
							String resultXML=CPayUtils.postRequest(sendXML);
							/**
							 * 将响应结果转换成实体类
							 */
							logger.info("将响应结果转换成实体类.入参:"+resultXML);
							ResCommon resCommon=(ResCommon) XMLUtils.xmlToBean(resultXML,ResCommon.class,null);
							/**
							 * 记录日志
							 */
							
							SBankLog bankLog=new SBankLog();
							bankLog.setRequestParam(sendXML);
							bankLog.setRequestInterface("强制转账-解冻支付");
							bankLog.setApplyTime(DateUtil.doFormatDate(new Date(), "yyyy-MM-dd HH:mm:ss"));
							bankLog.setPayChannelId(newPR.getPayChannelId());
							bankLog.setReplyText(resultXML);
						
							if(resCommon.getStatus().equals(ReqResult.ED02083.getCode())
									||resCommon.getStatus().equals(ReqResult.AAAAAAE.getCode())
									||resCommon.getStatus().equals(ReqResult.AAAAAAC.getCode())){
								
								bankLog.setReplyStatus(CommonSatus.CS1.getCode());
								returnDTO.setStatus(ResErrorCode.RETURN_SUCCESS_CODE);
								returnDTO.setMsg(ResErrorCode.RETURN_SUCCESS_MSG);
								logger.info("(转到退款专用账户)解冻后支付指令发送成功.");
								
							}else{
								bankLog.setReplyStatus(CommonSatus.CS2.getCode());
								returnDTO.setStatus(ResErrorCode.UNFREEZR_ERROR_CODE_8);
								returnDTO.setMsg(ResErrorCode.UNFREEZR_ERROR_MSG_8);
								logger.info("(转到退款专用账户)解冻后支付指令发送失败.");
								throw new Exception();
							}
							
							try {
								/**
								 * 保存请求日志
								 */
								bankLogMapper.insertSelective(bankLog);
								
							} catch (Exception e) {
								e.printStackTrace();
								logger.error("保存请求日志失败.",e);
							}
							
						}else{
							logger.info("更新原冻结记录、关联新支付记录编号失败.");
							returnDTO.setStatus(ResErrorCode.UNFREEZR_ERROR_CODE_6);
							returnDTO.setMsg(ResErrorCode.UNFREEZR_ERROR_MSG_6);
							throw new Exception();
						}
						
					}else{
						logger.info("更新原冻结记录、关联新支付记录编号失败.");
						returnDTO.setStatus(ResErrorCode.UNFREEZR_ERROR_CODE_5);
						returnDTO.setMsg(ResErrorCode.UNFREEZR_ERROR_MSG_5);
						throw new Exception();
					}
				}else{
					logger.info("复制支付记录失败.");
					returnDTO.setStatus(ResErrorCode.UNFREEZR_ERROR_CODE_4);
					returnDTO.setMsg(ResErrorCode.UNFREEZR_ERROR_MSG_4);
					throw new Exception();
				}
				
			}else{
				logger.info("更新支付记录状态失败.");
				return validDTO.returnError(ResErrorCode.UNFREEZR_ERROR_CODE_2, ResErrorCode.UNFREEZR_ERROR_MSG_2, returnDTO);
			}
		} catch (Exception e) {
			
			returnDTO.setStatus(ResErrorCode.RETURN_EXCE_CODE);
			returnDTO.setMsg(ResErrorCode.RETURN_EXCE_MSG);
			logger.error("存请求日志失败.",e);
			e.printStackTrace();
			throw e;
		}
		return returnDTO;
	}

	private  String getPaymentNoFromStoredProcedure(TrPaymentRecord newPR){
		Map<String,Integer> mapParam = new HashMap<String,Integer>();
		// 获取买家公司信息
		UserAccount buyer = new UserAccount();
		buyer.setMallUserName(newPR.getBuyerMallUserName());
		buyer = userDao.findAccountByMallUserName(buyer);
		logger.info("买家："+buyer.getUserId());
		// 增加退补款判断
		mapParam.put("codeType", 0);
		mapParam.put("userId", buyer.getUserId());
		mapParam.put("source", 0);
		String paymentNo = orderDao.getPaymentCode(mapParam);
		logger.info("paymentNo:"+paymentNo);
		if(paymentNo ==null || "".equals(paymentNo)){
			paymentNo = null;
		}
		return paymentNo;
	}
	
	/***
	 * 是否有补价订单
	 * update zhenghao 2015-12-24
	 */
	public ReturnDTO whetherBuJiaOrder(Map<String, Object> paramMap) throws Exception {

		ReturnDTO returnDTO = new ReturnDTO();
		try {
			/** 获取支付记录*/
			Integer paymentId = Integer.valueOf(String.valueOf(paramMap.get("paymentId")));
			TrPaymentRecord paymentRecord = trPaymentRecordMapper.selectByPrimaryKey(paymentId);
			System.out.println("paymentId:"+paymentId+" "+JSONObject.toJSONString(paymentRecord));

			//如果没有对应支付记录，抛出异常
			if(paymentRecord==null){
				return validDTO.returnError(ResErrorCode.UNFREEZR_ERROR_CODE_1, ResErrorCode.UNFREEZR_ERROR_MSG_1, returnDTO);
			}

			//检查改支付记录类型是否正常交易，如果不是，不做任何处理
			if(paymentRecord.getPayType()!=0) return returnDTO;

			//检查该支付记录下是否有买家补款 或 卖家退款支付记录
			TrPaymentRecord buchaPayment=this.quereyBuchaPaymentRecord(paymentRecord.getMallOrderId());

			//如果没有补差支付记录，直接返回
			if(buchaPayment==null) return returnDTO;

			//如果有，检查补差支付记录状态，并做对应异常提示 状态是否为：待付款 0、冻结中 9、已冻结 1
			//待付款状态
			if(buchaPayment.getPaymentStatus().equals(0)){
				returnDTO.setStatus(ResErrorCode.UNFREEZR_ERROR_CODE_11);
				returnDTO.setMsg(ResErrorCode.UNFREEZR_ERROR_MSG_11);
				returnDTO.setSuccess(false);
			}

			//已冻结状态
			if(buchaPayment.getPaymentStatus().equals(1)){
				returnDTO.setStatus(ResErrorCode.UNFREEZR_ERROR_CODE_10);
				returnDTO.setMsg(ResErrorCode.UNFREEZR_ERROR_MSG_10);
				returnDTO.setSuccess(false);
			}

			//冻结中状态
			if(buchaPayment.getPaymentStatus().equals(9)){
				returnDTO.setStatus(ResErrorCode.UNFREEZR_ERROR_CODE_9);
				returnDTO.setMsg(ResErrorCode.UNFREEZR_ERROR_MSG_9);
				returnDTO.setSuccess(false);
			}

			//保存待处理的补差支付记录编号
			Map<String,String> map = new LinkedHashMap<String,String>();
			map.put("paymentNo", buchaPayment.getPaymentNo());
			returnDTO.setData(map);


//			//是否有补价单子-------解冻之前判断是否有补价单子--开始
//			if(paymentRecord.getPayType() == 0){
//
//				TrPaymentRecord paymentRecordExten = new TrPaymentRecord();
//				paymentRecordExten.setMallOrderId(paymentRecord.getMallOrderId());
//
//				//设置查询条件为买家补款
//				paymentRecordExten.setPayType(1);
//
//				//检查相同的商城订单编号下是否有买家补款交易记录
//				List<TrPaymentRecord> listExten = trPaymentRecordMapper.selectBySelective(paymentRecordExten);
//
//				//如果有卖家补款交易记录
//				if(listExten != null && listExten.size() > 0){
//
//					paymentRecordExten = listExten.get(0);
//					Map mapParam = new HashMap();
//					mapParam.put("paymentId", paymentRecordExten.getPaymentId());
//
//					//检查买家补款交易是否有冻结记录
//					TrFreezeRecord fRecord = freezeRecordMapper.queryByPaymentId(mapParam);
//
//					Map<String,String> map = new LinkedHashMap<String,String>();
//
//					//如果有冻结记录
//					if(fRecord != null){
//						returnDTO.setStatus(ResErrorCode.UNFREEZR_ERROR_CODE_9);
//						returnDTO.setMsg(ResErrorCode.UNFREEZR_ERROR_MSG_9);
//						returnDTO.setSuccess(false);
//
//					}else{//如果没冻结就把补价单子关闭
//						mapParam.put("freezeStatus", 0);
//						fRecord = freezeRecordMapper.queryFreezeRecordByStatus(mapParam);
//						if(fRecord != null){//如果不为空，查看冻结结果
//							returnDTO.setStatus(ResErrorCode.UNFREEZR_ERROR_CODE_10);
//							returnDTO.setMsg(ResErrorCode.UNFREEZR_ERROR_MSG_10);
//							returnDTO.setSuccess(false);
//							//returnDTO.setData(paymentRecordExten);
//						}else{
//							returnDTO.setStatus(ResErrorCode.UNFREEZR_ERROR_CODE_11);
//							returnDTO.setMsg(ResErrorCode.UNFREEZR_ERROR_MSG_11);
//							returnDTO.setSuccess(false);
//						}
//
//					}
//					map.put("paymentNo", paymentRecordExten.getPaymentNo());
//					returnDTO.setData(map);
//					return returnDTO;
//				}
//			}


		} catch (Exception e) {
			returnDTO.setStatus(ResErrorCode.RETURN_EXCE_CODE);
			returnDTO.setMsg(ResErrorCode.RETURN_EXCE_MSG);
			logger.error("存请求日志失败.",e);
			e.printStackTrace();
			throw e;
		}
		return returnDTO;
	}


	/**
	 * 检查该支付记录下是否有买家补款 或 卖家退款支付记录
	 * */
	private TrPaymentRecord quereyBuchaPaymentRecord(String orderId) {

		TrPaymentRecord paymentRecordExten = new TrPaymentRecord();
		paymentRecordExten.setMallOrderId(orderId);

		//设置查询条件为买家补款
		paymentRecordExten.setPayType(1);

		//检查相同的商城订单编号下是否有买家补款交易记录
		List<TrPaymentRecord> listExten = trPaymentRecordMapper.selectBySelective(paymentRecordExten);

		if(listExten!=null && listExten.size()>0) return listExten.get(0);

		//设置查询条件为卖家退款
		paymentRecordExten.setPayType(2);

		//检查相同的商城订单编号下是否有卖家退款交易记录
		List<TrPaymentRecord> listExten_t = trPaymentRecordMapper.selectBySelective(paymentRecordExten);

		if(listExten_t!=null && listExten_t.size()>0) return listExten_t.get(0);

		return null;
	}
}