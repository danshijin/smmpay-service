package com.smmpay.internal.service;

import com.smmpay.inter.dto.req.TradingRecordDTO;
import com.smmpay.respository.model.TrPaymentRecord;
import com.smmpay.respository.model.UserPayChannel;

import java.util.List;

/**
 * Created by tangshulei on 2015/11/11.
 */
public interface TRService {

    List<TrPaymentRecord> getTrPaymentRecordList(TradingRecordDTO tradingRecordDTO);

    int countTrPaymentRecordList(TradingRecordDTO tradingRecordDTO);

    TrPaymentRecord getTrPaymentRecordDetail(int paymentId);
    TrPaymentRecord getTrPaymentRecordDetailByNo(String paymentNo);

    int closeTrPaymentRecordDetail(TrPaymentRecord trPaymentRecord);

    UserPayChannel getUserPayChannelDetail(int userPayChannelId);
    
   
}
