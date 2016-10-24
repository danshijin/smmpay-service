package com.smmpay.internal.service.impl;

import java.io.InputStream;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.smmpay.common.ResErrorCode;
import com.smmpay.inter.dto.req.CheckBankMoneyDTO;
import com.smmpay.inter.dto.req.GetCashDTO;
import com.smmpay.inter.dto.req.PlatformDebitsDTO;
import com.smmpay.inter.dto.req.UserAccountDTO;
import com.smmpay.inter.dto.res.ReturnDTO;
import com.smmpay.internal.service.DebitsService;
import com.smmpay.internal.service.IDatabaseEncrypt;
import com.smmpay.internal.service.UAService;
import com.smmpay.internal.service.ValidDTO;
import com.smmpay.respository.dao.DaBankMapper;
import com.smmpay.respository.dao.DaCodeMapper;
import com.smmpay.respository.dao.DaProvinceCityMapper;
import com.smmpay.respository.dao.MallUserRecordMapper;
import com.smmpay.respository.dao.SCheckBankRecordMapper;
import com.smmpay.respository.dao.TrCashApplyMapper;
import com.smmpay.respository.dao.UserAccountMapper;
import com.smmpay.respository.dao.UserBindBankMapper;
import com.smmpay.respository.dao.UserPayChannelMapper;
import com.smmpay.respository.model.DaBank;
import com.smmpay.respository.model.DaCode;
import com.smmpay.respository.model.DaProvinceCity;
import com.smmpay.respository.model.MallUserRecord;
import com.smmpay.respository.model.SCheckBankRecord;
import com.smmpay.respository.model.TrCashApply;
import com.smmpay.respository.model.UserAccount;
import com.smmpay.respository.model.UserBindBank;
import com.smmpay.respository.model.UserPayChannel;
import com.smmpay.util.DatabaseEncryptUtil;
import com.smmpay.util.MD5;
import com.smmpay.util.StringUtils;
import com.smmpay.util.UtilMail;


/**
 * Created by tangshulei on 2015/11/9.
 */
@Service("uaService")
@Transactional
public class UAServiceImpl implements UAService{
	private static Logger log = Logger.getLogger(UAService.class);

//    @Autowired
//    private SimpleMailMessage simpleMailMessage;
//    @Autowired
//    private JavaMailSender javaMailSender;
    @Autowired
    private UserAccountMapper userAccountMapper;
    @Autowired
    private UserBindBankMapper userBindBankMapper;
    @Autowired
    private MallUserRecordMapper mallUserRecordMapper;
    @Autowired
    private DaProvinceCityMapper provinceCityDao;
    @Autowired
    private DaBankMapper bankDao;
    @Autowired
    private DaCodeMapper codeMapper;
    @Autowired
    private UserPayChannelMapper userPayChannelMapper;
    @Autowired
    private TrCashApplyMapper trCashApplyMapper;
    @Autowired
    private ValidDTO validDTO;
    @Autowired
    private SCheckBankRecordMapper sCheckBankRecordMapper;
    @Autowired
    private DebitsService debitsService;
    @Autowired
    private IDatabaseEncrypt iDatabaseEncrypt;
    
    /**
     *有色网退款专用账户
     */
    @Value("#{common['FRONT.HOST']}")
    private String frontHost;


    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    SimpleDateFormat dSdf = new SimpleDateFormat("yyyy-MM-dd");

    @Transactional(propagation = Propagation.REQUIRED,rollbackFor = Exception.class)
    public void registerUser(UserAccount userAccount, UserBindBank userBindBank) throws Exception {
        String nowTime =  sdf.format(new Date());
        userAccount.setPassword(MD5.md5(userAccount.getPassword()));
        userAccount.setAuditStatus(UserAccount.UA_AUDIT_STATUS_0);
        userAccount.setRegisterTime(nowTime);
        int userId = userAccountMapper.insertSelective(userAccount);
        int i = bindUserBank(userAccount.getUserId(), userBindBank);
        if(i != 0){
            throw new Exception();
        }
    }

    public int bindUserBank(int userId, UserBindBank userBindBank){
        String nowTime =  sdf.format(new Date());
        userBindBank.setUserId(userId);
        //打款金额
        BigDecimal decimal = StringUtils.getNumber(1, 2);
        userBindBank.setDrawMoney(decimal);
        userBindBank.setIsPayment(UserBindBank.UB_IS_PAYMENT_0);
        userBindBank.setAuditStatus(UserBindBank.UB_AUDIT_STATUS_0);
        userBindBank.setCreateTime(nowTime);

        DaBank bank = bankDao.selectByPrimaryKey(userBindBank.getBankId());
        if(bank == null ){
            return 1;
        }
        DaProvinceCity province = provinceCityDao.selectByPrimaryKey(userBindBank.getBankAreaProvince());
        if(province == null){
            return 2;
        }
        DaProvinceCity city = provinceCityDao.selectByPrimaryKey(userBindBank.getBankAreaCity());
        if(city == null){
            return 3;
        }
        DaCode code = codeMapper.selectDaCodeByCode(userBindBank.getBankTypeId());
        if(code == null){
            return 4;
        }

        userBindBank.setBankType(code.getCodeName());
        userBindBank.setBankName(bank.getShortName());
        userBindBank.setBankProvinceName(province.getAreaName());
        userBindBank.setBankCityName(city.getAreaName());
        //支行联行号
        userBindBank.setCnaps(bank.getCnaps());

        userBindBankMapper.insertSelective(userBindBank);
        return 0;
    }

    /**
     * 根据userid username 验证用户有效性
     * @param userAccount
     * @return
     */
    public boolean validUserByUserIdAndUserName(UserAccount userAccount){
        int count = userAccountMapper.countUserByUserIdAndUserName(userAccount);
        if(count != 1) {
            return false;
        }
        return true;
    }

    /**
     * 判断用户或商城账号是否已存在
     */
    public int countByUserNameOrMallUserName(UserAccount userAccount){
        return userAccountMapper.countByUserNameOrMallUserName(userAccount);
    }

    /**
     * 判断用户公司是否存在
     */
    public int countByUserCompany(UserAccount userAccount){
        return userAccountMapper.countByUserCompany(userAccount);
    }

    /**
     * 通过用户名密码获取用户
     * @param userAccount
     * @return
     */
    public UserAccount selectUser(UserAccount userAccount) {

//        simpleMailMessage.setTo("tangshulei@smm.com.cn");
//        simpleMailMessage.setSubject("主题");
//        simpleMailMessage.setText("内容");
//        javaMailSender.send(simpleMailMessage);
        return userAccountMapper.selectUser(userAccount);
    }

    /**
     * 修改密码
     * @param userAccount
     * @return
     */
    public int updatePassword(UserAccount userAccount){
        return userAccountMapper.updatePassword(userAccount);
    }

    /**
     * 修改密码
     * @param userAccount
     * @return
     */
    public int updatePasswordByUserName(UserAccount userAccount){
        return userAccountMapper.updatePasswordByUserName(userAccount);
    }

    public int updateByPrimaryKeySelective(UserAccount userAccount){
        return  userAccountMapper.updateByPrimaryKeySelective(userAccount);
    }

    /**
     * 查看是否
     * @param userBindBank
     * @return
     */
    public int countUserBindBank(UserBindBank userBindBank){
        return userBindBankMapper.countUserBindBank(userBindBank);
    }

    public  int countUserBindBankForList(UserBindBank userBindBank){
        return userBindBankMapper.countUserBindBankForList(userBindBank);
    }

    public  int countUserBindBankAll(UserBindBank userBindBank){
        return userBindBankMapper.countUserBindBankAll(userBindBank);
    }

    public int updateUserBindBankToClose(UserBindBank userBindBank){
        return userBindBankMapper.updateByPrimaryKeySelective(userBindBank);
    }

    /**
     * 出金
     * @param getCashDTO
     */
    @Transactional(propagation = Propagation.REQUIRED,rollbackFor = Exception.class)
    public int getCash(GetCashDTO getCashDTO){
        int userId = Integer.valueOf(getCashDTO.getUserId());
        int payChannelId = Integer.valueOf(getCashDTO.getPayChannelId());
        int cashBankId = Integer.valueOf(getCashDTO.getCashBankId());
        UserAccount userAccount = userAccountMapper.selectByPrimaryKey(userId);
        if(userAccount == null || UserAccount.UA_AUDIT_STATUS_1 != userAccount.getAuditStatus()){
            return 1;
        }
        UserBindBank userBindBank = userBindBankMapper.selectByPrimaryKey(cashBankId);
        if(userBindBank == null || UserBindBank.UB_AUDIT_STATUS_1 != userBindBank.getAuditStatus()){
            return 2;
        }
        if(userBindBank.getUserId() != userId){
            return 4;
        }
        BigDecimal cashMoney = new BigDecimal(getCashDTO.getCashMoney());
        BigDecimal fee = validDTO.getFee(cashMoney);
        if(userBindBank.getBankTypeId() == 108){
            fee = new BigDecimal(0);
        }
        BigDecimal total = cashMoney.add(fee);
        if(total.compareTo(userAccount.getUseMoney()) > 0){
            return 5;
        }

        int cashType = Integer.valueOf(getCashDTO.getCashType());
        String nowTime =  sdf.format(new Date());

        TrCashApply trCashApply = new TrCashApply();
        UserPayChannel userPayChannel = new UserPayChannel();
        userPayChannel.setUserId(userId);
        userPayChannel.setPayChannelId(payChannelId);
        userPayChannel = userPayChannelMapper.selectByUserIdAndPayChannelId(userPayChannel);

        if(userPayChannel == null || UserPayChannel.UPC_ACCOUNT_STATUS_0 != userPayChannel.getUserAccountStatus()){
            return 3;
        }

        //修改支付渠道冻结金额
        userPayChannel.setSmmFreezeMoney(userPayChannel.getSmmFreezeMoney().add(total));
        userPayChannel.setUseMoney(userPayChannel.getUseMoney().subtract(total));
        userPayChannelMapper.updateByPrimaryKey(userPayChannel);

        //修改用户账户可用余额
        userAccount.setUseMoney(userAccount.getUseMoney().subtract(total));
//        userAccount.setFreezeMoney(userAccount.getFreezeMoney().add(cashMoney));
        userAccountMapper.updateByPrimaryKey(userAccount);

        trCashApply.setCounterFee(fee);
        trCashApply.setCashBankId(cashBankId);
        trCashApply.setCashMoney(cashMoney);
        trCashApply.setCashBankNo(userBindBank.getBankAccountNo());
        trCashApply.setCashBankName(userBindBank.getBankName());
        trCashApply.setCashBankCnaps(userBindBank.getCnaps());
        trCashApply.setCashType(cashType);
        trCashApply.setUserId(userId);
        trCashApply.setUserEmail(userAccount.getUserName());
        trCashApply.setUserCompanyName(userAccount.getCompanyName());
        trCashApply.setUserUseMoney(userAccount.getUseMoney());
        trCashApply.setUserFreezeMoney(userAccount.getFreezeMoney());
        trCashApply.setApplyTime(nowTime);
        trCashApply.setReplayStatus(TrCashApply.REPLAY_STATUS_0);
        trCashApply.setApplyStatus(TrCashApply.APPLY_STATUS_0);
        trCashApply.setPayChannelId(payChannelId);
        trCashApply.setPayChannelUserAccount(userPayChannel.getUserAccountNo());
        trCashApplyMapper.insert(trCashApply);
        
        //
        iDatabaseEncrypt.addCashApplyVerifyCode(trCashApply.getId());
        
        return 0;
    }
    

    /**
     * 出金
     * @param getCashDTO
     */
    @Transactional(propagation = Propagation.REQUIRED,rollbackFor = Exception.class)
    public ReturnDTO getCashForPlat(GetCashDTO getCashDTO, ReturnDTO returnDTO){

        int userId = Integer.valueOf(getCashDTO.getUserId());
        int payChannelId = Integer.valueOf(getCashDTO.getPayChannelId());
        int cashBankId = Integer.valueOf(getCashDTO.getCashBankId());

        UserAccount userAccount = userAccountMapper.selectByPrimaryKey(userId);

        if(userAccount == null || UserAccount.UA_AUDIT_STATUS_1 != userAccount.getAuditStatus()){
            return validDTO.returnError(ResErrorCode.GETCASH_ERROR_UC_CODE, ResErrorCode.GETCASH_ERROR_UC_MSG, returnDTO);
        }
        UserBindBank userBindBank = userBindBankMapper.selectByPrimaryKey(cashBankId);
        if(userBindBank == null || UserBindBank.UB_AUDIT_STATUS_1 != userBindBank.getAuditStatus()){
            return validDTO.returnError(ResErrorCode.GETCASH_ERROR_UBB_CODE, ResErrorCode.GETCASH_ERROR_UBB_MSG, returnDTO);
        }
        if(userBindBank.getUserId() != userId){
            return validDTO.returnError(ResErrorCode.GETCASH_ERROR_BANK_CODE, ResErrorCode.GETCASH_ERROR_BANK_MSG, returnDTO);
        }


        BigDecimal cashMoney = new BigDecimal(getCashDTO.getCashMoney());
        BigDecimal fee = validDTO.getFee(cashMoney);
        if(userBindBank.getBankTypeId() == 108){
            fee = new BigDecimal(0);
        }

        BigDecimal total = cashMoney.add(fee);

        if(total.compareTo(userAccount.getUseMoney()) > 0){
            return validDTO.returnError(ResErrorCode.GETCASH_ERROR_USEMONRY_CODE, ResErrorCode.GETCASH_ERROR_USEMONRY_MSG, returnDTO);
        }

        int cashType = Integer.valueOf(getCashDTO.getCashType());
        String nowTime =  sdf.format(new Date());

        TrCashApply trCashApply = new TrCashApply();
        UserPayChannel userPayChannel = new UserPayChannel();
        userPayChannel.setUserId(userId);
        userPayChannel.setPayChannelId(payChannelId);
        userPayChannel = userPayChannelMapper.selectByUserIdAndPayChannelId(userPayChannel);

        if(userPayChannel == null || UserPayChannel.UPC_ACCOUNT_STATUS_0 != userPayChannel.getUserAccountStatus()){
            return validDTO.returnError(ResErrorCode.GETCASH_ERROR_UPA_CODE, ResErrorCode.GETCASH_ERROR_UPA_MSG, returnDTO);
        }

        //修改支付渠道冻结金额
//        userPayChannel.setUserPayChannelId();
        userPayChannel.setSmmFreezeMoney(userPayChannel.getSmmFreezeMoney().add(total));
        userPayChannel.setUseMoney(userPayChannel.getUseMoney().subtract(total));
        userPayChannelMapper.updateByPrimaryKey(userPayChannel);

        //修改用户账户可用余额
        userAccount.setUseMoney(userAccount.getUseMoney().subtract(total));
//        userAccount.setFreezeMoney(userAccount.getFreezeMoney().add(cashMoney));
        userAccountMapper.updateByPrimaryKey(userAccount);

        trCashApply.setCounterFee(fee);
        trCashApply.setCashBankId(cashBankId);
        trCashApply.setCashMoney(cashMoney);
        trCashApply.setCashBankNo(userBindBank.getBankAccountNo());
        trCashApply.setCashBankName(userBindBank.getBankName());
        trCashApply.setCashBankCnaps(userBindBank.getCnaps());
        trCashApply.setCashType(cashType);
        trCashApply.setUserId(userId);
        trCashApply.setUserEmail(userAccount.getUserName());
        trCashApply.setUserCompanyName(userAccount.getCompanyName());
        trCashApply.setUserUseMoney(userAccount.getUseMoney());
        trCashApply.setUserFreezeMoney(userAccount.getFreezeMoney());
        trCashApply.setApplyTime(nowTime);
        trCashApply.setApplyRemark("后台管理员平台出金");
        trCashApply.setReplayStatus(TrCashApply.REPLAY_STATUS_1);
        trCashApply.setApplyStatus(TrCashApply.APPLY_STATUS_0);
        trCashApply.setPayChannelId(payChannelId);
        trCashApply.setPayChannelUserAccount(userPayChannel.getUserAccountNo());
        int cashId =  trCashApplyMapper.insert(trCashApply);
//        returnDTO.setData(trCashApply);

        PlatformDebitsDTO platformDebitsDTO = new PlatformDebitsDTO();
        platformDebitsDTO.setCashApplyId(trCashApply.getId());
        platformDebitsDTO.setAccountNo(trCashApply.getPayChannelUserAccount());
        platformDebitsDTO.setCashBankId(trCashApply.getCashBankId());
        platformDebitsDTO.setCashType(trCashApply.getCashType());
        platformDebitsDTO.setCounterFee(trCashApply.getCounterFee());
        platformDebitsDTO.setPayChannelId(trCashApply.getPayChannelId());
        platformDebitsDTO.setUserPayChannelId(userPayChannel.getUserPayChannelId());
        platformDebitsDTO.setPayChannelUserAccount(trCashApply.getPayChannelUserAccount());
        platformDebitsDTO.setRecvAccNm(trCashApply.getUserCompanyName());
        platformDebitsDTO.setRecvAccNo(trCashApply.getCashBankNo());
        platformDebitsDTO.setRecvBankNm(trCashApply.getCashBankName());
        platformDebitsDTO.setRecvTgfi(trCashApply.getCashBankCnaps());
        platformDebitsDTO.setTranAmt(trCashApply.getCashMoney());
        platformDebitsDTO.setUserId(trCashApply.getUserId());
        platformDebitsDTO.setUserCompanyName(trCashApply.getUserCompanyName());
        platformDebitsDTO.setUserEmail(trCashApply.getUserEmail());
        platformDebitsDTO.setUserUseMoney(trCashApply.getUserUseMoney());
        platformDebitsDTO.setUserFreezeMoney(trCashApply.getUserFreezeMoney());
        if(userBindBank.getBankTypeId() == 108){
            platformDebitsDTO.setSameBank("0");
        }else{
            platformDebitsDTO.setSameBank("1");
        }

        returnDTO = debitsService.debits(platformDebitsDTO, returnDTO);

        return returnDTO;
    }

    public UserPayChannel getUserPayChannel(UserPayChannel userPayChannel){
        return userPayChannelMapper.selectByUserIdAndPayChannelId(userPayChannel);
    }

    /**
     * 根据id获取绑定银行卡信息
     * @param bindId
     * @return
     */
    public UserBindBank selectUserBindBank(int bindId){
        return userBindBankMapper.selectByPrimaryKey(bindId);
    }

    /**
     * 更新划款验证信息
     * @param bindId
     * @return
     */
    public int updateCheckMoney(Integer bindId){
        return userBindBankMapper.updateCheckMoney(bindId);
    }

    /**
     * 查询是否存在未验证银行卡
     * @param userId
     * @return
     */
    public int countUserBindBankByStatus(int userId){
        return userBindBankMapper.countUserBindBankByStatus(userId);
    }

    public List<UserBindBank> selectUserBindBankByUserId(UserBindBank userBindBank){
        return userBindBankMapper.selectUserBindBankByUserId(userBindBank);
    }

    @Transactional(propagation = Propagation.REQUIRED,rollbackFor = Exception.class)
    public ReturnDTO checkBankMoney(CheckBankMoneyDTO checkBankMoneyDTO, ReturnDTO returnDTO){
        String nowTime =  sdf.format(new Date());
        UserBindBank userBindBank = selectUserBindBank(Integer.valueOf(checkBankMoneyDTO.getBindId()));//uaService.selectUserBindBank(Integer.valueOf(checkBankMoneyDTO.getBindId()));
        if(userBindBank == null){
            returnDTO = validDTO.returnError(ResErrorCode.CHECKMONEY_ERROR_CODE, ResErrorCode.CHECKMONEY_ERROR_MSG, returnDTO);
            return returnDTO;
        }

        if(userBindBank.getIsPayment() != UserBindBank.UB_AUDIT_STATUS_1 || userBindBank.getAuditStatus() != UserBindBank.UB_AUDIT_STATUS_0){
            returnDTO = validDTO.returnError(ResErrorCode.CHECKMONEY_ERROR_NOPAY_CODE, ResErrorCode.CHECKMONEY_ERROR_NOPAY_MSG, returnDTO);
            return returnDTO;
        }

        String TTime =  dSdf.format(new Date());
        TTime = TTime + " 00:00:00";
        BigDecimal decimal = new BigDecimal(checkBankMoneyDTO.getMoney());
        SCheckBankRecord sCheckBankRecord = new SCheckBankRecord();
        sCheckBankRecord.setUserId(userBindBank.getUserId());
        sCheckBankRecord.setBankNo(userBindBank.getBankAccountNo());
        sCheckBankRecord.setBankName(userBindBank.getBankName());
        sCheckBankRecord.setUserBandId(userBindBank.getBindId());
        sCheckBankRecord.setInputMoney(decimal);
        sCheckBankRecord.setCheckTime(TTime);
        sCheckBankRecord.setCheckResult(SCheckBankRecord.CHECK_RESULT_0);

        int count = sCheckBankRecordMapper.selectByBindBankId(sCheckBankRecord);
        if(count >= 5){
            returnDTO = validDTO.returnError(ResErrorCode.CHECKMONEY_ERROR_VALID_COUNT_CODE, ResErrorCode.CHECKMONEY_ERROR_VALID_COUNT_MSG, returnDTO);
            return returnDTO;
        }
        sCheckBankRecord.setCheckTime(nowTime);

        if(decimal == null || userBindBank.getDrawMoney() == null || decimal.compareTo(userBindBank.getDrawMoney()) != 0){
            returnDTO = validDTO.returnError(ResErrorCode.CHECKMONEY_ERROR_VALID_CODE, ResErrorCode.CHECKMONEY_ERROR_VALID_MSG, returnDTO);
            sCheckBankRecord.setCheckResult(SCheckBankRecord.CHECK_RESULT_1);
            sCheckBankRecordMapper.insert(sCheckBankRecord);
            return returnDTO;
        }

        sCheckBankRecord.setCheckResult(SCheckBankRecord.CHECK_RESULT_0);
        sCheckBankRecordMapper.insert(sCheckBankRecord);

        userBindBankMapper.updateCheckMoney(userBindBank.getBindId());
//            uaService.updateCheckMoney(userBindBank.getBindId());
        return returnDTO;
    }

    /**
     * 插入商城进来的用户
     */
    @Transactional(propagation = Propagation.REQUIRED,rollbackFor = Exception.class)
    public int registerMallUser(final MallUserRecord mallUserRecord){
    	UserAccount account = new UserAccount();
    	account.setUserName(mallUserRecord.getEmail());
    	account.setMallUserName(mallUserRecord.getMallAccount());
    	int rows = userAccountMapper.countByUserNameOrMallUserName(account);
    	if(rows == 0){
            rows = mallUserRecordMapper.insertSelective(mallUserRecord);

            //发送邮件
            log.info("邮件发送" + mallUserRecord.getEmail());
            Thread th = new Thread(new Runnable(){
    			public void run() {
                    UtilMail se = new UtilMail(false);
    				   log.info("邮件开始发送" + mallUserRecord.getEmail());
    		            try{
    		                InputStream is = ClassLoader.getSystemResourceAsStream("SendCodeMail.html");
    		                byte[] readByte = new byte[is.available()];
    		                is.read(readByte);
    		                String mailContent = new String(readByte,"utf-8");
    		
//    		                String url_a = "<a href='"+frontHost+"SMMPayClient/rest/register/validatecode?code="+mallUserRecord.getCode()+"'>点击链接，激活SMM支付账户</a>";
//    		                String url = frontHost+"SMMPayClient/rest/register/validatecode?code="+mallUserRecord.getCode();

//    		                mailContent = mailContent.replace("${url_a}", url_a);
//    		                mailContent = mailContent.replace("${url}", url);
                            mailContent = mailContent.replace("${code}",mallUserRecord.getCode());
    		                mailContent = mailContent.replace("${email}", mallUserRecord.getEmail());
    		                mailContent = mailContent.replace("${mallNo}", mallUserRecord.getRealName());
                            mailContent = mailContent.replace("${host}", frontHost);
    						se.doSendHtmlEmail("激活账户", mailContent, mallUserRecord.getEmail());
                     		log.info("邮件发送成功");
    		            }catch (Exception e){
    		                log.error("邮件发送失败",e);
    		            }
               
    		    }
            });
            th.start();
            log.info("邮件发送" + mallUserRecord.getEmail());
//            try {
//                SendMessage.send("tangshulei@smm.cn", "111", "2222");
//            } catch (MessagingException e) {
//                log.error(ResErrorCode.REGISTER_ERROR_SENDMAIL_MSG, e);
//                returnDTO = validDTO.returnError(ResErrorCode.REGISTER_ERROR_SENDMAIL_CODE, ResErrorCode.REGISTER_ERROR_SENDMAIL_MSG, returnDTO);
//            }

            return rows;
    	}else
    		return 0;

    }

    public MallUserRecord getMallUserRecordByCode(String code){
        return mallUserRecordMapper.selectByCode(code);
    }


	public UserAccountDTO findAccountByUserName(UserAccount userAccount) {
		userAccount = userAccountMapper.findAccountByUserName(userAccount);
		UserAccountDTO userDto = new UserAccountDTO(); 
		userDto.setUserId(userAccount.getUserId().toString());
		userDto.setUserName(userAccount.getUserName());
		userDto.setCompanyName(userAccount.getCompanyName());
		userDto.setMallUserName(userAccount.getMallUserName());
		return userDto;
	}

    public DaCode selectDaCodeByCode(int code){
        return codeMapper.selectDaCodeByCode(code);
    }

    public boolean checkPayUserExsists(UserAccount userAccount){
    	log.info("userAccount"+userAccount.getMallUserName());
    	int rows = userAccountMapper.isOpenPayChannel(userAccount);
    	log.info("rows"+rows);
    	if(rows > 0) return true;
    	return false;
    }
    
    public static void main(String args[]){
    	 SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    	 System.out.println(sdf.format(new Date()));
    }
}
