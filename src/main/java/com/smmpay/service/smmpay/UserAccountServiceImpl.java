package com.smmpay.service.smmpay;

import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;

import com.smmpay.inter.dto.req.*;
import com.smmpay.inter.dto.res.*;
import com.smmpay.respository.model.MallUserRecord;
import com.smmpay.respository.model.UserPayChannel;
import com.smmpay.util.MD5;
import com.smmpay.util.SendMessage;
import com.smmpay.util.StringUtils;
import org.apache.commons.beanutils.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.smmpay.common.ResErrorCode;
import com.smmpay.inter.smmpay.UserAccountService;
import com.smmpay.internal.service.UAService;
import com.smmpay.payment.Payment;
import com.smmpay.internal.service.ValidDTO;
import com.smmpay.respository.model.UserAccount;
import com.smmpay.respository.model.UserBindBank;
import com.smmpay.service.base.BaseService;
import org.apache.log4j.Logger;

import javax.mail.MessagingException;

/**
 * Created by tangshulei on 2015/11/5.
 */
@Service("userAccountService")
public class UserAccountServiceImpl extends BaseService implements UserAccountService {

    Logger log = Logger.getLogger(this.getClass());

    @Autowired
    ValidDTO validDTO;

    @Autowired
    UAService uaService;

    @Autowired
    Payment payment;

    /**
     * 注册
     * @param map
     * @return
     */
    public ReturnDTO registerUser(Map<String,String> map) {
        ReturnDTO returnDTO = new ReturnDTO();
        UserAccountDTO userAccountDTO = new UserAccountDTO();
        try {
            BeanUtils.populate(userAccountDTO, map);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }

        /**
         * 判断参数
         */
        String msg = validDTO.validRegister(userAccountDTO);
        if(msg != null){
            //参数错误，直接返回
            returnDTO.setMsg(msg);
            returnDTO.setStatus(ResErrorCode.VALIDPARAM_ERROR_CODE);
            returnDTO.setSuccess(false);
            return returnDTO;
        }
        /**
         * 创建User类 添加进数据库
         */
        UserAccount userAccount = new UserAccount();
        /**创建userBindBank类 添加进数据库 **/
        UserBindBank userBindBank = new UserBindBank();
        try {
            validDTO.copyUserAccount(userAccount, userAccountDTO);
            validDTO.copyUserBindBank(userBindBank, userAccountDTO);
        }catch(Exception e){
            log.error(ResErrorCode.VALIDPARAM_ERROR_MSG,e);
            //参数错误，直接返回
            returnDTO = validDTO.returnError(ResErrorCode.VALIDPARAM_ERROR_CODE, ResErrorCode.VALIDPARAM_ERROR_MSG, returnDTO);
            return returnDTO;
        }
        /**
         * 判断用户或商城账号是否已存在
         */
        int isExist = uaService.countByUserNameOrMallUserName(userAccount);
        if(isExist > 0){
            returnDTO = validDTO.returnError(ResErrorCode.REGISTER_ERROR_CODE, ResErrorCode.REGISTER_ERROR_MSG, returnDTO);
            return returnDTO;
        }

        /**
         * 判断用户公司是否已存在
         */
        isExist = uaService.countByUserCompany(userAccount);
        if(isExist > 0){
            returnDTO = validDTO.returnError(ResErrorCode.REGISTER_ERROR_COMPANY_CODE, ResErrorCode.REGISTER_ERROR_COMPANY_MSG, returnDTO);
            return returnDTO;
        }

        /**验证银行卡是否已存在 **/
        int isExists = uaService.countUserBindBank(userBindBank);
        if(isExists > 0){
            returnDTO = validDTO.returnError(ResErrorCode.BINDBANK_ERROR_CODE, ResErrorCode.BINDBANK_ERROR_MSG, returnDTO);
            return returnDTO;
        }

        try {
            uaService.registerUser(userAccount, userBindBank);
        }catch(Exception e){
            log.error(ResErrorCode.REGISTER_ERROR_SAVE_MSG,e);
            returnDTO = validDTO.returnError(ResErrorCode.REGISTER_ERROR_SAVE_CODE, ResErrorCode.REGISTER_ERROR_SAVE_MSG, returnDTO);
            return returnDTO;
        }

        try {
            ResUserAccountDTO resUserAccountDTO = copy(userAccount, ResUserAccountDTO.class);
            returnDTO.setData(resUserAccountDTO);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return returnDTO;
    }

    /**根据code获取邮箱和商城账户 **/
    public ReturnDTO getUserByCode(Map<String,String> map){
        ReturnDTO returnDTO = new ReturnDTO();
        String code = map.get("code");
        if(code == null || "".equals(code)){
            return validDTO.returnError(ResErrorCode.VALIDPARAM_ERROR_CODE,ResErrorCode.GETCODE_ERROR_MSG_1,returnDTO);
        }

        /**获取激活码记录 **/
        MallUserRecord mallUserRecord = uaService.getMallUserRecordByCode(code);
        if(mallUserRecord == null){
            return validDTO.returnError(ResErrorCode.GETCODE_ERROR_CODE,ResErrorCode.GETCODE_ERROR_MSG,returnDTO);
        }

        ResUserInfo resUserInfo = new ResUserInfo();
        resUserInfo.setUserName(mallUserRecord.getEmail());
        resUserInfo.setMallUserName(mallUserRecord.getMallAccount());

        /**
         * 判断用户或商城账号是否已存在
         */
        UserAccount userAccount = new UserAccount();
        userAccount.setUserName(mallUserRecord.getEmail());
        userAccount.setMallUserName(mallUserRecord.getMallAccount());
        int isExist = uaService.countByUserNameOrMallUserName(userAccount);
        if(isExist > 0){
            resUserInfo.setIsExist(true);
        }
        returnDTO.setData(resUserInfo);
        return returnDTO;
    }

    /**
     * 商城用户注册
     */
    public ReturnDTO registerUserFromMall(Map<String,String> map){
        ReturnDTO returnDTO = new ReturnDTO();

        String email = map.get("email");
        String mallAccount = map.get("mallUserName");
        String realName = map.get("realName");
        long time = System.currentTimeMillis();
        MallUserRecord mallUserRecord = new MallUserRecord();
        mallUserRecord.setEmail(email);
        mallUserRecord.setRealName(realName);
        mallUserRecord.setMallAccount(mallAccount);
        SimpleDateFormat sdf = new SimpleDateFormat("YYYY-MM-dd HH:mm:ss");
        String nowTime =  sdf.format(new Date());
        mallUserRecord.setCreateTime(nowTime);
        String code = MD5.md5AndSha(email + mallAccount + time);
        mallUserRecord.setCode(code);
        try{
            int rows = uaService.registerMallUser(mallUserRecord);
            if(rows == 0) returnDTO.setStatus("000001");
            log.info("rows" + rows);
        }catch(Exception e){
            log.error(ResErrorCode.REGISTER_ERROR_SAVE_MSG, e);
            returnDTO = validDTO.returnError(ResErrorCode.REGISTER_ERROR_SAVE_CODE, ResErrorCode.REGISTER_ERROR_SAVE_MSG, returnDTO);
            return returnDTO;
        }
        return returnDTO;
    }

    /**
     * 会员检验
     * @param map
     * @return
     */
    public ReturnDTO checkUser(Map<String,String> map) {
        ReturnDTO returnDTO = new ReturnDTO();

        CheckUserDTO checkUserDTO = new CheckUserDTO();
        try {
            BeanUtils.populate(checkUserDTO, map);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        //参数错误
        String msg = validDTO.validCheckUser(checkUserDTO);
        if(msg != null){
            returnDTO = validDTO.returnError(ResErrorCode.VALIDPARAM_ERROR_CODE, msg, returnDTO);
            return returnDTO;
        }
        /** 判断用户名或者商城名称是否存在 **/
        UserAccount userAccount = new UserAccount();
        userAccount.setUserName(checkUserDTO.getUserName());
        userAccount.setMallUserName(checkUserDTO.getMallUserName());
        int isExist = uaService.countByUserNameOrMallUserName(userAccount);
        if(isExist > 0){
            //返回错误，用户名或者商城账号已存在
            returnDTO = validDTO.returnError(ResErrorCode.CHECKUSER_ERROR_CODE, ResErrorCode.CHECKUSER_ERROR_MSG, returnDTO);
            return returnDTO;
        }

        return returnDTO;
    }

    /**
     * 会员登录
     * @param map
     * @return
     */
    public ReturnDTO loginUser(Map<String, String> map) {
        ReturnDTO returnDTO = new ReturnDTO();

        String userName = map.get("userName");
        String password = map.get("password");
        String loginIp = map.get("loginIp");
        if(userName == null || "".equals(userName)){
            return validDTO.returnError(ResErrorCode.VALIDPARAM_ERROR_CODE,ResErrorCode.USERACCOUNT_ERROR_MSG_1,returnDTO);
        }
        if(password == null || "".equals(password)){
            return validDTO.returnError(ResErrorCode.VALIDPARAM_ERROR_CODE,ResErrorCode.USERACCOUNT_ERROR_MSG_2,returnDTO);
        }
//        if(loginIp == null || "".equals(loginIp)){
//            return validDTO.returnError(ResErrorCode.VALIDPARAM_ERROR_CODE,ResErrorCode.USERACCOUNT_ERROR_MSG_25,returnDTO);
//        }
        UserAccount userAccountDTO = new UserAccount();
        userAccountDTO.setUserName(userName);
        userAccountDTO.setPassword(MD5.md5(password));
        /**验证用户名 密码 **/
        UserAccount userAccount = null;
        try {
            userAccount = uaService.selectUser(userAccountDTO);
        }catch (Exception e){
            log.error(ResErrorCode.LOGIN_ERROR_GET_MSG, e);
            return validDTO.returnError(ResErrorCode.LOGIN_ERROR_GET_CODE,ResErrorCode.LOGIN_ERROR_GET_MSG,returnDTO);
        }
        if(userAccount == null){
            returnDTO = validDTO.returnError(ResErrorCode.LOGIN_ERROR_CODE, ResErrorCode.LOGIN_ERROR_MSG, returnDTO);
            return returnDTO;
        }

        if(UserAccount.UA_AUDIT_STATUS_1 != userAccount.getAuditStatus()){
            returnDTO = validDTO.returnError(ResErrorCode.LOGIN_ERROR_AUDIT_CODE, ResErrorCode.LOGIN_ERROR_AUDIT_MSG, returnDTO);
            return returnDTO;
        }

        UserPayChannel userPayChannel = new UserPayChannel();
        userPayChannel.setUserId(userAccount.getUserId());
        userPayChannel.setPayChannelId(10001);
        userPayChannel = uaService.getUserPayChannel(userPayChannel);
        if(userPayChannel == null){
            return validDTO.returnError(ResErrorCode.LOGIN_ERROR_PAYCHANNEL_CODE,ResErrorCode.LOGIN_ERROR_PAYCHANNEL_MSG,returnDTO);
        }

        try {
            ResUserAccountDTO resUserAccountDTO = copy(userAccount, ResUserAccountDTO.class);
            resUserAccountDTO.setAccountNo(userPayChannel.getUserAccountNo());
            resUserAccountDTO.setAccountName(userPayChannel.getUserAccountName());
            returnDTO.setData(resUserAccountDTO);
        } catch (Exception e) {
            e.printStackTrace();
        }

        UserAccount userAccount1 = new UserAccount();
        userAccount1.setUserId(userAccount.getUserId());
        userAccount1.setLastLoginIp(loginIp);
        SimpleDateFormat sdf = new SimpleDateFormat("YYYY-MM-dd HH:mm:ss");
        String nowTime =  sdf.format(new Date());
        userAccount1.setLastLoginTime(nowTime);
        try {
            uaService.updateByPrimaryKeySelective(userAccount1);
        }catch(Exception e){
            log.error("更新最近登录ip出错",e);
        }
        return returnDTO;
    }

    /**
     * 修改密码
     * @param map
     * @return
     */
    public ReturnDTO updateUserPassword(Map<String, String> map) {
        ReturnDTO returnDTO = new ReturnDTO();
        UpdateUserPasswordDTO updateUserPasswordDTO = new UpdateUserPasswordDTO();
        try {
            BeanUtils.populate(updateUserPasswordDTO, map);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }

        /**
         * 判断参数
         */
        String msg = validDTO.updatePassword(updateUserPasswordDTO);
        if(msg != null){
            //参数错误，直接返回
            returnDTO = validDTO.returnError(ResErrorCode.VALIDPARAM_ERROR_CODE, msg, returnDTO);
            return returnDTO;
        }

        UserAccount userAccountDTO = new UserAccount();
        userAccountDTO.setUserId(Integer.valueOf(updateUserPasswordDTO.getUserId()));
        userAccountDTO.setUserName(updateUserPasswordDTO.getUserName());
        userAccountDTO.setPassword(MD5.md5(updateUserPasswordDTO.getOldPassword()));
        /**验证用户名 密码 **/
        UserAccount userAccount = null;
        try {
            userAccount = uaService.selectUser(userAccountDTO);
        }catch (Exception e){
            log.error(ResErrorCode.LOGIN_ERROR_GET_MSG,e);
            return validDTO.returnError(ResErrorCode.LOGIN_ERROR_GET_CODE,ResErrorCode.LOGIN_ERROR_GET_MSG,returnDTO);
        }
        if (userAccount == null){
            returnDTO = validDTO.returnError(ResErrorCode.LOGIN_ERROR_CODE, ResErrorCode.LOGIN_ERROR_MSG, returnDTO);
            return returnDTO;
        }

        if(UserAccount.UA_AUDIT_STATUS_1 != userAccount.getAuditStatus()){
            returnDTO = validDTO.returnError(ResErrorCode.LOGIN_ERROR_AUDIT_CODE, ResErrorCode.LOGIN_ERROR_AUDIT_MSG, returnDTO);
            return returnDTO;
        }

        userAccount.setPassword(MD5.md5(updateUserPasswordDTO.getNewPassword()));
        try {
            uaService.updatePassword(userAccount);
        }catch (Exception e){
            log.error(e.getMessage(),e);
            returnDTO = validDTO.returnError(ResErrorCode.UPDATEPASSWORD_ERROR_CODE, ResErrorCode.UPDATEPASSWORD_ERROR_MSG, returnDTO);
            return returnDTO;
        }
        return returnDTO;
    }

    public ReturnDTO setUserPassword(Map<String, String> map){
        ReturnDTO returnDTO = new ReturnDTO();
        String userName = map.get("userName");
        String password = map.get("password");
        if(userName == null || "".equals(userName)){
            return validDTO.returnError(ResErrorCode.VALIDPARAM_ERROR_CODE,ResErrorCode.USERACCOUNT_ERROR_MSG_1,returnDTO);
        }
        if(password == null || "".equals(password)){
            return validDTO.returnError(ResErrorCode.VALIDPARAM_ERROR_CODE,ResErrorCode.USERACCOUNT_ERROR_MSG_2,returnDTO);
        }
        UserAccount userAccount = new UserAccount();
        userAccount.setUserName(userName);
        userAccount.setPassword(MD5.md5(password));
        try {
            uaService.updatePasswordByUserName(userAccount);
        }catch (Exception e){
            log.error(e.getMessage(),e);
            returnDTO = validDTO.returnError(ResErrorCode.UPDATEPASSWORD_ERROR_CODE, ResErrorCode.UPDATEPASSWORD_ERROR_MSG, returnDTO);
            return returnDTO;
        }
        return returnDTO;
    }

    /**
     * 获取用户账户余额
     * @param map
     * @return
     */
    public ReturnDTO getUserAccount(Map<String, String> map) {
    	log.info("前台调用获取余额,入参:"+map);
    	
        final String userId = map.get("userId");
        String userName = map.get("userName");

        ReturnDTO returnDTO = validDTO.validUserIdAndUserName(userId, userName);

        UserAccount search = new UserAccount();
        search.setUserId(Integer.valueOf(userId));
        search.setUserName(userName);
        UserAccount userAccount = uaService.selectUser(search);

        /** 返回数据**/
        ResGetUserAccountDTO rguAccountDTO = new ResGetUserAccountDTO();
//        if(resultMap != null && resultMap.size() > 0){
        rguAccountDTO.setUserId(userId);
        rguAccountDTO.setUserName(userName);
        rguAccountDTO.setFreezeMoney(userAccount.getFreezeMoney());
        rguAccountDTO.setTotalMoney(userAccount.getFreezeMoney().add(userAccount.getUseMoney()));
        rguAccountDTO.setUserMoney(userAccount.getUseMoney());

        /* 查询中信余额*/
        Thread thread = new Thread(new Runnable(){
            public void run() {
                ResGetUserAccountDTO resGetUserAccountDTO = new ResGetUserAccountDTO();
                Map<String,Object> payMap = new HashMap<String, Object>();
                Map<String,Object> resultMap = new HashMap<String, Object>();
                payMap.put("userId", userId);
                payMap.put("operateType", 2);
                try {
                    resultMap = payment.queryPayment(payMap);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        thread.start();

//        //查询余额
//        ResGetUserAccountDTO resGetUserAccountDTO = new ResGetUserAccountDTO();
//        Map<String,Object> payMap = new HashMap<String, Object>();
//        Map<String,Object> resultMap = new HashMap<String, Object>();
//        payMap.put("userId", userId);
//        try {
//            resultMap = payment.queryPayment(payMap);
//        } catch (Exception e) {
//            e.printStackTrace();
//            validDTO.returnError(ResErrorCode.GETUSERACCOUNT_ERROR_CODE, ResErrorCode.GETUSERACCOUNT_ERROR_MSG, returnDTO);
//            return returnDTO;
//        }
//
//        if(resultMap == null ){//|| !"success".equals(resultMap.get("resultCode"))){
//            validDTO.returnError(ResErrorCode.GETUSERACCOUNT_ERROR_CODE, ResErrorCode.GETUSERACCOUNT_ERROR_MSG, returnDTO);
//            return returnDTO;
//        }
//        if(!"success".equals(resultMap.get("resultCode"))){
//            String code = ResErrorCode.GETUSERACCOUNT_ERROR_CODE;
//            String msg = ResErrorCode.GETUSERACCOUNT_ERROR_MSG;
//            if(resultMap.get("resCode") != null){
//                code = resultMap.get("resCode").toString();
//            }
//            if(resultMap.get("resultMesg") != null){
//                msg = resultMap.get("resultMesg").toString();
//            }
//            validDTO.returnError(code, msg, returnDTO);
//            return returnDTO;
//        }

        /** 返回数据**/
//        ResGetUserAccountDTO rguAccountDTO = new ResGetUserAccountDTO();
////        if(resultMap != null && resultMap.size() > 0){
//        rguAccountDTO.setUserId(userId);
//        rguAccountDTO.setUserName(userName);
//        try {
//            BeanUtils.populate(rguAccountDTO, resultMap);
//        } catch (Exception e) {
//            log.error(ResErrorCode.GETUSERACCOUNT_ERROR_MSG, e);
//            returnDTO = validDTO.returnError(ResErrorCode.GETUSERACCOUNT_ERROR_CODE, ResErrorCode.GETUSERACCOUNT_ERROR_MSG, returnDTO);
//            return returnDTO;
//        }
//        }
        returnDTO.setData(rguAccountDTO);
        return returnDTO;
    }

    /**
     * 绑定银行卡
     * @param map
     * @return
     */
    public ReturnDTO bindUserBank(Map<String, String> map) {
        ReturnDTO returnDTO = new ReturnDTO();

        UserAccountDTO userAccountDTO = new UserAccountDTO();
        try {
            BeanUtils.populate(userAccountDTO, map);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }

        /**
         * 判断参数
         */
        String msg = validDTO.validBindBank(userAccountDTO);
        if(msg != null){
            //参数错误，直接返回
            returnDTO = validDTO.returnError(ResErrorCode.VALIDPARAM_ERROR_CODE, msg, returnDTO);
            return returnDTO;
        }

        returnDTO = validDTO.validUserIdAndUserName(userAccountDTO.getUserId(), userAccountDTO.getUserName());
        if(!returnDTO.isSuccess()){
            return returnDTO;
        }

        /**新建绑卡 **/
        UserBindBank userBindBank = new UserBindBank();
        try {
            validDTO.copyUserBindBank(userBindBank, userAccountDTO);
        }catch(Exception e){
            e.printStackTrace();
            //参数错误，直接返回
            returnDTO = validDTO.returnError(ResErrorCode.VALIDPARAM_ERROR_CODE, ResErrorCode.VALIDPARAM_ERROR_MSG, returnDTO);
            return returnDTO;
        }

        UserAccount userAccount = new UserAccount();
        userAccount.setUserId(Integer.valueOf(userAccountDTO.getUserId()));
        userAccount.setUserName(userAccountDTO.getUserName());

        userBindBank.setUserId(Integer.valueOf(userAccountDTO.getUserId()));
        /**验证银行卡是否已在用户名下存在 **/
        int isExistsByUserId = uaService.countUserBindBank(userBindBank);
        if(isExistsByUserId > 0){
            returnDTO = validDTO.returnError(ResErrorCode.BINDBANK_ERROR_USER_CODE, ResErrorCode.BINDBANK_ERROR_USER_MSG, returnDTO);
            return returnDTO;
        }
        userBindBank.setUserId(null);
        /**验证银行卡是否已存在 **/
        int isExists = uaService.countUserBindBank(userBindBank);
        if(isExists > 0){
            returnDTO = validDTO.returnError(ResErrorCode.BINDBANK_ERROR_CODE, ResErrorCode.BINDBANK_ERROR_MSG, returnDTO);
            return returnDTO;
        }
        userBindBank.setUserId(Integer.valueOf(userAccountDTO.getUserId()));
        /** 插入数据库 **/
        try {
            uaService.bindUserBank(userAccount.getUserId(), userBindBank);
        }catch (Exception e){
            log.error(ResErrorCode.BINDBANK_ERROR_SAVE_MSG, e);
            returnDTO = validDTO.returnError(ResErrorCode.BINDBANK_ERROR_SAVE_CODE, ResErrorCode.BINDBANK_ERROR_SAVE_MSG, returnDTO);
            return returnDTO;
        }
        return returnDTO;
    }

    /**
     * 验证银行卡打款金额
     * @param map
     * @return
     */
    public ReturnDTO checkBankMoney(Map<String, String> map) {
        ReturnDTO returnDTO = new ReturnDTO();

        CheckBankMoneyDTO checkBankMoneyDTO = new CheckBankMoneyDTO();
        try {
            BeanUtils.populate(checkBankMoneyDTO, map);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }

        /**
         * 判断参数
         */
        String msg = validDTO.validBankMoney(checkBankMoneyDTO);
        if(msg != null){
            //参数错误，直接返回
            returnDTO = validDTO.returnError(ResErrorCode.VALIDPARAM_ERROR_CODE, msg, returnDTO);
            return returnDTO;
        }

        returnDTO = validDTO.validUserIdAndUserName(checkBankMoneyDTO.getUserId(), checkBankMoneyDTO.getUserName());
        if(!returnDTO.isSuccess()){
            return returnDTO;
        }
        try {
            return uaService.checkBankMoney(checkBankMoneyDTO, returnDTO);
        }catch (Exception e){
            log.error(ResErrorCode.CHECKMONEY_ERROR_SAVE_MSG,e);
            returnDTO = validDTO.returnError(ResErrorCode.CHECKMONEY_ERROR_SAVE_CODE, ResErrorCode.CHECKMONEY_ERROR_SAVE_MSG, returnDTO);
            return returnDTO;
        }
//        UserBindBank userBindBank = uaService.selectUserBindBank(Integer.valueOf(checkBankMoneyDTO.getBindId()));
//        if(userBindBank == null){
//            returnDTO = validDTO.returnError(ResErrorCode.CHECKMONEY_ERROR_CODE, ResErrorCode.CHECKMONEY_ERROR_MSG, returnDTO);
//            return returnDTO;
//        }
//
//        if(userBindBank.getIsPayment() != UserBindBank.UB_AUDIT_STATUS_1 || userBindBank.getAuditStatus() != UserBindBank.UB_AUDIT_STATUS_0){
//            returnDTO = validDTO.returnError(ResErrorCode.CHECKMONEY_ERROR_NOPAY_CODE, ResErrorCode.CHECKMONEY_ERROR_NOPAY_MSG, returnDTO);
//            return returnDTO;
//        }
//
//        BigDecimal decimal = new BigDecimal(checkBankMoneyDTO.getMoney());
//        if(decimal == null || userBindBank.getDrawMoney() == null || decimal.compareTo(userBindBank.getDrawMoney()) != 0){
//            returnDTO = validDTO.returnError(ResErrorCode.CHECKMONEY_ERROR_VALID_CODE, ResErrorCode.CHECKMONEY_ERROR_VALID_MSG, returnDTO);
//            return returnDTO;
//        }
//        try {
//            uaService.updateCheckMoney(userBindBank.getBindId());
//        }catch (Exception e){
//            log.error(ResErrorCode.CHECKMONEY_ERROR_SAVE_MSG,e);
//            returnDTO = validDTO.returnError(ResErrorCode.CHECKMONEY_ERROR_SAVE_CODE, ResErrorCode.CHECKMONEY_ERROR_SAVE_MSG, returnDTO);
//            return returnDTO;
//        }
//        return returnDTO;
    }

    /**
     * 验证账户是否存在待验证银行卡
     * @param map
     * @return
     */
    public ReturnDTO checkBank(Map<String, String> map) {
        ReturnDTO returnDTO = new ReturnDTO();
        String userId = map.get("userId");
        String userName = map.get("userName");

        returnDTO = validDTO.validUserIdAndUserName(userId, userName);
        if(!returnDTO.isSuccess()){
            return returnDTO;
        }

        int isExist = uaService.countUserBindBankByStatus(Integer.valueOf(userId));
        if(isExist <= 0){
            returnDTO = validDTO.returnError(ResErrorCode.CHECKBANK_ERROR_CODE, ResErrorCode.CHECKBANK_ERROR_MSG, returnDTO);
            return returnDTO;
        }

        return returnDTO;
    }

    /**
     * 获取银行卡信息
     * @param map
     * @return
     */
    public ReturnDTO getBank(Map<String, String> map) {
        ReturnDTO returnDTO = new ReturnDTO();
        String userId = map.get("userId");
        String userName = map.get("userName");
        String bankStatus = map.get("bankStatus");

        returnDTO = validDTO.validUserIdAndUserName(userId, userName);
        if(!returnDTO.isSuccess()){
            return returnDTO;
        }
        UserBindBank userBindBank = new UserBindBank();
        userBindBank.setUserId(Integer.valueOf(userId));
        if(bankStatus != null && !"".equals(bankStatus)){
            if(!StringUtils.isNumeric(bankStatus)){
                return validDTO.returnError(ResErrorCode.VALIDPARAM_ERROR_CODE,ResErrorCode.GETBANK_ERROR_MSG_1,returnDTO);
            }
            userBindBank.setAuditStatus(Integer.valueOf(bankStatus));
        }

        List<UserBindBank> list = uaService.selectUserBindBankByUserId(userBindBank);
        if(list == null || list.size() <= 0){
            returnDTO = validDTO.returnError(ResErrorCode.GETBANK_ERROR_CODE, ResErrorCode.GETBANK_ERROR_MSG, returnDTO);
            return returnDTO;
        }
        List<ResUserBindBank> resList = new ArrayList<ResUserBindBank>();
        for(UserBindBank ubb : list){
            ResUserBindBank resUserBindBank = new ResUserBindBank();
            validDTO.copyResUserBindBank(ubb, resUserBindBank);
            resList.add(resUserBindBank);
        }
        returnDTO.setData(resList);
        return returnDTO;
    }

    /**
     * 关闭已绑定银行卡
     * @param map
     * @return
     */
    public ReturnDTO updateUserBindBank(Map<String,String> map){
        String userId = map.get("userId");
        String userName = map.get("userName");
        String bindId = map.get("bindId");
        /**
         * 参数认证
         */
        ReturnDTO returnDTO = validDTO.validUserIdAndUserName(userId, userName);
        if(!returnDTO.isSuccess()){
            return returnDTO;
        }
        if(bindId == null || "".equals(bindId)){
            return validDTO.returnError(ResErrorCode.VALIDPARAM_ERROR_CODE, ResErrorCode.BINDID_ERROR_MSG_1,returnDTO);
        }
        if(!StringUtils.isNumeric(bindId)){
            return validDTO.returnError(ResErrorCode.VALIDPARAM_ERROR_CODE, ResErrorCode.BINDID_ERROR_MSG_2,returnDTO);
        }

        /**
         * 查询银行卡信息
         */
        UserBindBank userBindBank = uaService.selectUserBindBank(Integer.valueOf(bindId));
        if(userBindBank == null || userBindBank.getAuditStatus() == UserBindBank.UB_AUDIT_STATUS_2){
            return validDTO.returnError(ResErrorCode.UPDATEBINDBANK_ERROR_CODE, ResErrorCode.UPDATEBINDBANK_ERROR_MSG,returnDTO);
        }
        if(Integer.valueOf(userId) != userBindBank.getUserId()){
            return validDTO.returnError(ResErrorCode.GETPAYDETAIL_ERROR_NO_AUTH_CODE, ResErrorCode.GETPAYDETAIL_ERROR_NO_AUTH_MSG,returnDTO);
        }

        /**
         * 查询是否已绑唯一银行卡
         */
        int size = uaService.countUserBindBankAll(userBindBank);
        if (size <= 1) {
            return validDTO.returnError(ResErrorCode.UPDATEBINDBANK_ERROR_ONLY_CODE, ResErrorCode.UPDATEBINDBANK_ERROR_ONLY_MSG, returnDTO);
        }

        if(UserBindBank.UB_AUDIT_STATUS_1 == userBindBank.getAuditStatus()) {
            int size2 = uaService.countUserBindBankForList(userBindBank);
            if (size2 <= 1) {
                return validDTO.returnError(ResErrorCode.UPDATEBINDBANK_ERROR_ONLY_CODE, ResErrorCode.UPDATEBINDBANK_ERROR_ONLY_MSG, returnDTO);
            }
        }

        userBindBank.setAuditStatus(UserBindBank.UB_AUDIT_STATUS_2);
        try {
            uaService.updateUserBindBankToClose(userBindBank);
        }catch (Exception e){
            log.error(ResErrorCode.UPDATE_ERROR_MSG,e);
            return validDTO.returnError(ResErrorCode.UPDATE_ERROR_CODE,ResErrorCode.UPDATE_ERROR_MSG,returnDTO);
        }
        return returnDTO;
    }


    public ReturnDTO checkUserPayChannel(Map<String,String> map){
    	ReturnDTO returnDTO = new ReturnDTO();
    	UserAccount userAccount = null;
        CheckUserDTO checkUserDTO = new CheckUserDTO();
        try {
            BeanUtils.populate(checkUserDTO, map);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        
        try{
        	userAccount = this.copy(checkUserDTO, UserAccount.class);
        }catch (Exception e) {
            e.printStackTrace();
        } 
      
        boolean isPayChannel = uaService.checkPayUserExsists(userAccount);
        if(!isPayChannel){
        	returnDTO = new ReturnDTO("000005",false,"支付附属账户未开通");
        }
        return returnDTO;
    }

    /**
     * 出金
     * @param map
     * @return
     */
    public ReturnDTO getCash(Map<String,String> map){
        ReturnDTO returnDTO = new ReturnDTO();
        GetCashDTO getCashDTO = new GetCashDTO();
        try {
            BeanUtils.populate(getCashDTO, map);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }

        /**
         * 判断参数
         */
        String msg = validDTO.validGetCash(getCashDTO);
        if(msg != null){
            //参数错误，直接返回
            returnDTO = validDTO.returnError(ResErrorCode.VALIDPARAM_ERROR_CODE, msg, returnDTO);
            return returnDTO;
        }
        /**验证userID userName **/
        returnDTO = validDTO.validUserIdAndUserName(getCashDTO.getUserId(), getCashDTO.getUserName());
        if(!returnDTO.isSuccess()){
            return returnDTO;
        }

        Map<String,Object> payMap = new HashMap<String, Object>();
        Map<String,Object> resultMap = new HashMap<String, Object>();
        payMap.put("userId", getCashDTO.getUserId());
        payMap.put("operateType", 4);
        try {
            resultMap = payment.queryPayment(payMap);
        } catch (Exception e) {
            e.printStackTrace();
            validDTO.returnError(ResErrorCode.GETUSERACCOUNT_ERROR_CODE, ResErrorCode.GETUSERACCOUNT_ERROR_MSG, returnDTO);
            return returnDTO;
        }

        if(resultMap == null  || !"success".equals(resultMap.get("resultCode"))){
            validDTO.returnError(ResErrorCode.GETUSERACCOUNT_ERROR_CODE, ResErrorCode.GETUSERACCOUNT_ERROR_MSG, returnDTO);
            return returnDTO;
        }

        try {
            int res = uaService.getCash(getCashDTO);
            if(res == 1){
                return validDTO.returnError(ResErrorCode.GETCASH_ERROR_UC_CODE, ResErrorCode.GETCASH_ERROR_UC_MSG, returnDTO);
            }
            if(res == 2){
                return validDTO.returnError(ResErrorCode.GETCASH_ERROR_UBB_CODE, ResErrorCode.GETCASH_ERROR_UBB_MSG, returnDTO);
            }
            if(res == 3){
                return validDTO.returnError(ResErrorCode.GETCASH_ERROR_UPA_CODE, ResErrorCode.GETCASH_ERROR_UPA_MSG, returnDTO);
            }
            if(res == 4){
                return validDTO.returnError(ResErrorCode.GETCASH_ERROR_BANK_CODE, ResErrorCode.GETCASH_ERROR_BANK_MSG, returnDTO);
            }
            if(res == 5){
                return validDTO.returnError(ResErrorCode.GETCASH_ERROR_USEMONRY_CODE, ResErrorCode.GETCASH_ERROR_USEMONRY_MSG, returnDTO);
            }
            if(res == 6){
                return validDTO.returnError(ResErrorCode.GETCASH_ERROR_FEE_CODE, ResErrorCode.GETCASH_ERROR_FEE_MSG, returnDTO);
            }
            if(res != 0){
                return validDTO.returnError(ResErrorCode.GETCASH_ERROR_CODE, ResErrorCode.GETCASH_ERROR_MSG, returnDTO);
            }
        }catch (Exception e){
            log.error(ResErrorCode.BANDID_ERROR_MSG_1, e);
            return validDTO.returnError(ResErrorCode.GETCASH_ERROR_CODE, ResErrorCode.GETCASH_ERROR_MSG, returnDTO);
        }
        return returnDTO;
    }

}
