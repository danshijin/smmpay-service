package com.smmpay.internal.service.impl;

import com.smmpay.inter.dto.req.TradingRecordDTO;
import com.smmpay.internal.service.TRService;
import com.smmpay.respository.dao.TrPaymentRecordMapper;
import com.smmpay.respository.dao.UserPayChannelMapper;
import com.smmpay.respository.model.TrPaymentRecord;
import com.smmpay.respository.model.UserPayChannel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Created by tangshulei on 2015/11/11.
 */
@Service("trService")
@Transactional
public class TRServiceImpl implements TRService {

    @Autowired
    TrPaymentRecordMapper trPaymentRecordMapper;

    @Autowired
    UserPayChannelMapper userPayChannelMapper;

    public List<TrPaymentRecord> getTrPaymentRecordList(TradingRecordDTO tradingRecordDTO) {
        return trPaymentRecordMapper.selectForList(tradingRecordDTO);
    }

    public int countTrPaymentRecordList(TradingRecordDTO tradingRecordDTO) {
        return trPaymentRecordMapper.countForList(tradingRecordDTO);
    }

    public TrPaymentRecord getTrPaymentRecordDetail(int paymentId) {
        return trPaymentRecordMapper.selectByPrimaryKey(paymentId);
    }

    public int closeTrPaymentRecordDetail(TrPaymentRecord trPaymentRecord){
        return trPaymentRecordMapper.updateByPrimaryKeySelective(trPaymentRecord);
    }

    public UserPayChannel getUserPayChannelDetail(int userPayChannelId) {
        return userPayChannelMapper.selectByPrimaryKey(userPayChannelId);
    }

	public TrPaymentRecord getTrPaymentRecordDetailByNo(String paymentNo) {
		  return trPaymentRecordMapper.selectByPaymentNo(paymentNo);
	}
    
    
}
