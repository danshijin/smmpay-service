package com.smmpay.respository.dao;

import java.util.Map;

import com.smmpay.respository.model.UserAccount;

public interface UserAccountMapper {
    int deleteByPrimaryKey(Integer userId);

    int insert(UserAccount record);

    int insertSelective(UserAccount record);

    UserAccount selectByPrimaryKey(Integer userId);

    int countUserByUserIdAndUserName(UserAccount record);

    int updateByPrimaryKeySelective(UserAccount record);

    int updateByPrimaryKey(UserAccount record);

    UserAccount selectUser(UserAccount record);

    int countByUserNameAndPassword(UserAccount userAccount);

    int countByUserNameOrMallUserName(UserAccount userAccount);

    int countByUserCompany(UserAccount userAccount);

    int updatePassword(UserAccount userAccount);

    int updatePasswordByUserName(UserAccount userAccount);

    /**
     * 修改账户余额-增加
     * @param paramMap
     * @return
     */
    int updateUserMoneyByUserId(Map<String, Object> paramMap);
    
    UserAccount findAccountByUserName(UserAccount userAccount);
    
    /**
     * 修改账户余额-减少
     * @param paramMap
     * @return
     */
    int updateUserMoneyByUserId2(Map<String, Object> paramMap);
    
    /**
     * 修改账户余额-减少
     * @param paramMap
     * @return
     */
    int updateUserMoneyByUserId3(Map<String, Object> paramMap);

    /**
     * 修改账户冻结余额-减少
     * @param paramMap
     * @return
     */
    int updateUserMoneyByUserId4(Map<String, Object> paramMap);
    
    /**
     * 冻结金额-增加
     * @param paramMap
     * @return
     */
    int updateFreezeMoneyByUserId(Map<String, Object> paramMap);
    
    /**
     * 冻结金额-减少
     * @param paramMap
     * @return
     */
    int updateFreezeMoneyByUserId2(Map<String, Object> paramMap);
    
    
    int isOpenPayChannel(UserAccount userAccount);
    UserAccount findAccountByMallUserName(UserAccount userAccount);
    
}