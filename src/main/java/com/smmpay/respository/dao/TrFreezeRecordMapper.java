package com.smmpay.respository.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.smmpay.respository.model.TrFreezeRecord;

public interface TrFreezeRecordMapper {
    int deleteByPrimaryKey(Integer freezeId);

    int insert(TrFreezeRecord record);

    int insertSelective(TrFreezeRecord record);

    TrFreezeRecord selectByPrimaryKey(Integer freezeId);

    int updateByPrimaryKeySelective(TrFreezeRecord record);

    int updateByPrimaryKey(TrFreezeRecord record);
    
    /**
     * 检查账户是否有冻结状态为请求中记录
     * @param paramMap
     * @return
     */
    Integer queryFreezeRecordStatus(Map<String, Object> paramMap); 
    
    /**
     *  查询所有冻结状态为请求中的记录 
     * @param paramMap
     * @return
     */
    List<TrFreezeRecord> queryFreezeRecordList(Map<String, Object> paramMap);
    
    
    Integer updateFreezeStatus(TrFreezeRecord record);
    
    /**
     * 判断冻结编号是否存在于数据库
     * @param paramMap
     * @return
     */
    Integer queryByFreezeNo(Map<String, Object> paramMap);
    
    /**
     * 回填冻结编号、修改冻结状态
     */
    Integer updateFreezeNo(TrFreezeRecord record);
    
    /**
     *  查询所有解冻状态为请求中的记录 
     * @param paramMap
     * @return
     */
    List<TrFreezeRecord> queryFreezeRecordListByUNFreeze(Map<String, Object> paramMap);
    
    /**
     * 修改解冻状态
     * @param record
     * @return
     */
    Integer updateUNFreezeStatus(TrFreezeRecord record);
    
    /**
     * 获取最后一条冻结记录
     */
    List<TrFreezeRecord> lastFreezeRecord(Map<String, Object> paramMap);
    
    /**
     * 根据paymentId查询
     */
    TrFreezeRecord queryByPaymentId(Map<String, Object> paramMap);
    
    TrFreezeRecord queryFreezeRecordByStatus(Map<String, Object> paramMap);
    
    TrFreezeRecord getMd5Params(int freezeId);
    
    /** 插入数据行验证码
     * @param veriyCode
     * @return
     */
    int addVerifyCode(@Param(value="freezeRecordId") int freezeRecordId, @Param(value="veriyCode") String veriyCode);
    
    /** 获取数据行验证码
     * @param freezeRecordId
     * @return
     */
    String getVerify(int freezeRecordId);
    

}