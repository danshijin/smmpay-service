package com.smmpay.internal.service;

import java.util.List;

import com.smmpay.inter.dto.req.CheckBankMoneyDTO;
import com.smmpay.inter.dto.req.GetCashDTO;
import com.smmpay.inter.dto.req.UserAccountDTO;
import com.smmpay.inter.dto.res.ReturnDTO;
import com.smmpay.respository.model.DaCode;
import com.smmpay.respository.model.MallUserRecord;
import com.smmpay.respository.model.UserAccount;
import com.smmpay.respository.model.UserBindBank;
import com.smmpay.respository.model.UserPayChannel;

/**
 * Created by tangshulei on 2015/11/9.
 */
public interface UAService {

    void registerUser(UserAccount userAccount, UserBindBank userBindBank) throws Exception;

    int countByUserNameOrMallUserName(UserAccount userAccount);

    int countByUserCompany(UserAccount userAccount);

    UserAccount selectUser(UserAccount userAccount);

    boolean validUserByUserIdAndUserName(UserAccount userAccount);

    int updatePassword(UserAccount userAccount);

    int updatePasswordByUserName(UserAccount userAccount);

    int updateByPrimaryKeySelective(UserAccount userAccount);

    int bindUserBank(int userId, UserBindBank userBindBank);

    int countUserBindBank(UserBindBank userBindBank);

    int countUserBindBankForList(UserBindBank userBindBank);

    int countUserBindBankAll(UserBindBank userBindBank);

    ReturnDTO checkBankMoney(CheckBankMoneyDTO checkBankMoneyDTO, ReturnDTO returnDTO);

    UserBindBank selectUserBindBank(int bindId);

    int updateCheckMoney(Integer bindId);

    int countUserBindBankByStatus(int userId);

    List<UserBindBank> selectUserBindBankByUserId(UserBindBank userBindBank);

    MallUserRecord getMallUserRecordByCode(String code);

    int updateUserBindBankToClose(UserBindBank userBindBank);

    int getCash(GetCashDTO getCashDTO);

    ReturnDTO getCashForPlat(GetCashDTO getCashDTO, ReturnDTO returnDTO);

    UserPayChannel getUserPayChannel(UserPayChannel userPayChannel);

    /**
     * 插入商城进来的用户
     */
    int registerMallUser(MallUserRecord mallUserRecord);
    
    UserAccountDTO findAccountByUserName(UserAccount userAccount);

    DaCode selectDaCodeByCode(int code);
    
    boolean checkPayUserExsists(UserAccount userAccount);
}
