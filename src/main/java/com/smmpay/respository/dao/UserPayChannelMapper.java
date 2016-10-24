package com.smmpay.respository.dao;

import java.util.List;
import java.util.Map;

import com.smmpay.respository.model.UserPayChannel;

public interface UserPayChannelMapper {
    int deleteByPrimaryKey(Integer userPayChannelId);

    int insert(UserPayChannel record);

    int insertSelective(UserPayChannel record);

    UserPayChannel selectByPrimaryKey(Integer userPayChannelId);

    int updateByPrimaryKeySelective(UserPayChannel record);

    int updateByPrimaryKey(UserPayChannel record);
    /**
     * 更新用户支付通道余额
     * @param paramMap
     * @return
     */
    int updateUserMoneyByUserIdPCId(Map<String, Object> paramMap);
    
    
    
    /**
     * 查询用户支付通道余额
     */
    UserPayChannel queryUserMoney(Map<String, Object> paramMap);
    
    /**
     * 查询用户支付通道
     */
    List<UserPayChannel> queryPayChannelByUserId(Map<String, Object> paramMap);
    
    UserPayChannel queryPayChannelByUserIdAndOrderChannel(UserPayChannel userPayChannel);

    UserPayChannel selectByUserIdAndPayChannelId(UserPayChannel userPayChannel);
    
    /**
     * 更新用户支付通道余额
     * @param paramMap
     * @return
     */
    int updateUserMoneyByUserIdPCId2(Map<String, Object> paramMap);
    
    
    /**
     * 更新用户支付通道余额
     * @param paramMap
     * @return
     */
    int updateUserMoneyByUserIdPCId3(Map<String, Object> paramMap);

    /**
     * 更新用户支付通道smm冻结余额
     * @param paramMap
     * @return
     */
    int updateUserMoneyByUserIdPCId4(Map<String, Object> paramMap);
    
    /**
     * 查询所有附属账户
     * @param paramMap
     * @return
     */
    List<UserPayChannel> selectAll(Map<String, Object> paramMap);
    
    
    /**
     * 更新用户支付通道冻结金额-增加
     * @param paramMap
     * @return
     */
    int updateFreezeMoneyByUserIdPCId(Map<String, Object> paramMap);
    
    /**
     * 更新用户支付通道冻结金额-减少
     * @param paramMap
     * @return
     */
    int updateFreezeMoneyByUserIdPCId2(Map<String, Object> paramMap);
    
    
}