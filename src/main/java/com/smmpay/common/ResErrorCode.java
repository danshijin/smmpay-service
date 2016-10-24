package com.smmpay.common;

/**
 * Created by tangshulei on 2015/11/6.
 */
public class ResErrorCode {

    public static final String RETURN_SUCCESS_CODE = "000000";
    public static final String RETURN_SUCCESS_MSG = "成功";
    
    public static final String RETURN_EXCE_CODE = "000003";
    public static final String RETURN_EXCE_MSG = "调用失败";
    public static final String RETURN_EXCE_MSG_1 = "已有出金记录";

    public static final String DECRYPTPARAM_ERROR_CODE = "000090";
    public static final String DECRYPTPARAM_ERROR_MSG = "解密失败";

    public static final String VALIDPARAM_ERROR_CODE = "000091";
    public static final String VALIDPARAM_ERROR_MSG = "参数错误";
    
    public static final String CLOSE_ERROR_CODE = "000092";
    public static final String CLOSE_ERROR_MSG = "系统闭市";

    /** 用户名 用户id验证**/
    public static final String USERID_ERROR_MSG_1 = "UserID不能为空";
    public static final String USERNAME_ERROR_MSG_1 = "UserName不能为空";
    public static final String USERID_ERROR_MSG_2 = "UserID必须为数字";

    public static final String USERID_ERROR_CODE = "000011";
    public static final String USERID_ERROR_MSG = "没有找到该用户";

    /** 验证用户账号或商城账号是否存在 **/
    public static final String CHECKUSER_ERROR_CODE = "000001";
    public static final String CHECKUSER_ERROR_MSG = "用户名或商城账号已存在";
    public static final String CHECKUSER_ERROR_MSG_1 = "CheckType不能为空";
    public static final String CHECKUSER_ERROR_MSG_2 = "UserName不能为空";
    public static final String CHECKUSER_ERROR_MSG_3 = "MallId不能为空";
    public static final String CHECKUSER_ERROR_MSG_4 = "CheckType不能为空";

    /** 登录 **/
    public static final String LOGIN_ERROR_CODE = "000010";
    public static final String LOGIN_ERROR_MSG = "用户名或密码错误";

    public static final String LOGIN_ERROR_AUDIT_CODE = "000011";
    public static final String LOGIN_ERROR_AUDIT_MSG = "用户未通过审核";

    public static final String LOGIN_ERROR_PAYCHANNEL_CODE = "000001";
    public static final String LOGIN_ERROR_PAYCHANNEL_MSG = "用户关联账户未生成";

    public static final String LOGIN_ERROR_VALID_CODE = "000012";
    public static final String LOGIN_ERROR_VALID_MSG = "无效的用户";

    public static final String LOGIN_ERROR_GET_CODE = "000013";
    public static final String LOGIN_ERROR_GET_MSG = "获取用户失败";

    /** 注册 **/
    public static final String REGISTER_ERROR_CODE = "000001";
    public static final String REGISTER_ERROR_MSG = "用户名或商城账号已存在";

    public static final String REGISTER_ERROR_SAVE_CODE = "000002";
    public static final String REGISTER_ERROR_SAVE_MSG = "用户或银行卡保存异常";

    public static final String REGISTER_ERROR_SENDMAIL_CODE = "000003";
    public static final String REGISTER_ERROR_SENDMAIL_MSG = "发送邮件失败";

    public static final String REGISTER_ERROR_COMPANY_CODE = "000004";
    public static final String REGISTER_ERROR_COMPANY_MSG = "公司名称已存在";

    public static final String USERACCOUNT_ERROR_MSG_1 = "UserName不能为空";
    public static final String USERACCOUNT_ERROR_MSG_2 = "Password不能为空";

    public static final String USERACCOUNT_ERROR_MSG_3 = "MallId不能为空";
    public static final String USERACCOUNT_ERROR_MSG_4 = "CertificateNo不能为空";
    public static final String USERACCOUNT_ERROR_MSG_5 = "CertificateUrl不能为空";
    public static final String USERACCOUNT_ERROR_MSG_6 = "CompanyName不能为空";
    public static final String USERACCOUNT_ERROR_MSG_7 = "CompanyAddr不能为空";
    public static final String USERACCOUNT_ERROR_MSG_8 = "ContactName不能为空";
    public static final String USERACCOUNT_ERROR_MSG_9 = "Phone不能为空";
    public static final String USERACCOUNT_ERROR_MSG_10 = "MobilePhone不能为空";
    public static final String USERACCOUNT_ERROR_MSG_11 = "PostCode不能为空";
    public static final String USERACCOUNT_ERROR_MSG_12 = "BankTypeId不能为空";
    public static final String USERACCOUNT_ERROR_MSG_23 = "BankTypeId必须为数字";
    public static final String USERACCOUNT_ERROR_MSG_13 = "ProvinceId不能为空";
    public static final String USERACCOUNT_ERROR_MSG_14 = "CityId不能为空";
    public static final String USERACCOUNT_ERROR_MSG_15 = "BankId不能为空";
    public static final String USERACCOUNT_ERROR_MSG_24 = "BankId必须为数字";
    public static final String USERACCOUNT_ERROR_MSG_16 = "BankAccountNo不能为空";
    public static final String USERACCOUNT_ERROR_MSG_17 = "IdCardUrl不能为空";
    public static final String USERACCOUNT_ERROR_MSG_18 = "RegisterCertificateUrl不能为空";
    public static final String USERACCOUNT_ERROR_MSG_19= "Date不能为空";
    public static final String USERACCOUNT_ERROR_MSG_20 = "RegisterIp不能为空";
    public static final String USERACCOUNT_ERROR_MSG_21 = "ProvinceId必须为数字";
    public static final String USERACCOUNT_ERROR_MSG_22 = "CityId必须为数字";
    public static final String USERACCOUNT_ERROR_MSG_25 = "LoginIp不能为空";
    public static final String USERACCOUNT_ERROR_MSG_26 = "AuthorizeUrl不能为空";

    /**根据code获取email 商城账户**/
    public static final String GETCODE_ERROR_CODE = "000001";
    public static final String GETCODE_ERROR_MSG = "无效的激活码";
    public static final String GETCODE_ERROR_MSG_1 = "code不能为空";

    /** 修改密码**/
    public static final String UPDATEPASSWORD_ERROR_CODE = "000001";
    public static final String UPDATEPASSWORD_ERROR_MSG = "修改密码错误";
    public static final String UPDATEPASSWORD_ERROR_MSG_1 = "UserId不能为空";
    public static final String UPDATEPASSWORD_ERROR_MSG_2 = "UserName不能为空";
    public static final String UPDATEPASSWORD_ERROR_MSG_3 = "OldPassword不能为空";
    public static final String UPDATEPASSWORD_ERROR_MSG_4 = "NewPassword不能为空";
    public static final String UPDATEPASSWORD_ERROR_MSG_5 = "UserId必须为数字";

    /**绑卡 **/
    public static final String BINDBANK_ERROR_SAVE_CODE = "000001";
    public static final String BINDBANK_ERROR_SAVE_MSG = "银行卡保存异常";

    public static final String BINDBANK_ERROR_CODE = "000002";
    public static final String BINDBANK_ERROR_MSG = "银行卡已存在";

    public static final String BINDBANK_ERROR_USER_CODE = "000003";
    public static final String BINDBANK_ERROR_USER_MSG = "该用户已绑定过该卡";

    /**查询余额 **/
    public static final String GETUSERACCOUNT_ERROR_CODE="000001";
    public static final String GETUSERACCOUNT_ERROR_MSG="查询余额失败";
    
    /**签约开户**/
    public static final String OPENACCOUNT_ERROR_MSG_1 = "subAccNm不能为空";
    
    /**在线销户**/
    public static final String CANCELACCOUNT_ERROR_MSG_1 = "subAccNo不能为空";

    /**银行卡金额认证**/
    public static final String CHECKMONEY_ERROR_CODE = "000001";
    public static final String CHECKMONEY_ERROR_MSG = "没找到该银行卡";

    public static final String CHECKMONEY_ERROR_SAVE_CODE = "000002";
    public static final String CHECKMONEY_ERROR_SAVE_MSG = "保存验证信息错误";

    public static final String CHECKMONEY_ERROR_VALID_CODE = "000003";
    public static final String CHECKMONEY_ERROR_VALID_MSG = "验证金额错误";

    public static final String CHECKMONEY_ERROR_NOPAY_CODE = "000004";
    public static final String CHECKMONEY_ERROR_NOPAY_MSG = "该银行卡不在验证状态";

    public static final String CHECKMONEY_ERROR_VALID_COUNT_CODE = "000005";
    public static final String CHECKMONEY_ERROR_VALID_COUNT_MSG = "今天验证失败次数过多";

    public static final String BANDID_ERROR_MSG_1 = "BandID不能为空";
    public static final String BANDMONEY_ERROR_MSG_1 = "Money不能为空";
    public static final String BANDMONEY_ERROR_MSG_2 = "Money必须为数字";
    public static final String BANDID_ERROR_MSG_2 = "BandID必须为数字";

    /** 待验证银行账户 **/
    public static final String CHECKBANK_ERROR_MSG = "不存在未验证银行卡";
    public static final String CHECKBANK_ERROR_CODE = "000001";

    /** 获取银行卡 **/
    public static final String GETBANK_ERROR_MSG = "不存在银行卡";
    public static final String GETBANK_ERROR_CODE = "000001";

    public static final String GETBANK_ERROR_MSG_1 = "BankStatus必须为数字";

    /**关闭已绑银行卡 **/
    public static final String BINDID_ERROR_MSG_1 = "BindId不能为空";
    public static final String BINDID_ERROR_MSG_2 = "BindId必须为数字";
    public static final String UPDATEBINDBANK_ERROR_MSG = "该银行卡未绑定";
    public static final String UPDATEBINDBANK_ERROR_CODE = "000001";
    public static final String UPDATEBINDBANK_ERROR_ONLY_MSG = "该银行卡为唯一绑定";
    public static final String UPDATEBINDBANK_ERROR_ONLY_CODE = "000002";
    public static final String UPDATE_ERROR_MSG = "修改数据错误";
    public static final String UPDATE_ERROR_CODE = "000003";

    /**出金 **/
    public static final String GETCASH_ERROR_MSG_1 = "CashBankId不能为空";
    public static final String GETCASH_ERROR_MSG_2 = "CashBankId必须为数字";
    public static final String GETCASH_ERROR_MSG_3 = "CashMoney不能为空";
    public static final String GETCASH_ERROR_MSG_4 = "CashMoney必须为数字";
    public static final String GETCASH_ERROR_MSG_5 = "CashType不能为空";
    public static final String GETCASH_ERROR_MSG_6 = "CashType必须为数字";
    public static final String GETCASH_ERROR_MSG_7 = "PayChannelId不能为空";
    public static final String GETCASH_ERROR_MSG_8 = "PayChannelId必须为数字";

    public static final String GETCASH_ERROR_CODE = "000001";
    public static final String GETCASH_ERROR_MSG = "保存出金记录错误";

    public static final String GETCASH_ERROR_UC_CODE = "000002";
    public static final String GETCASH_ERROR_UC_MSG = "无效的用户";

    public static final String GETCASH_ERROR_UBB_CODE = "000003";
    public static final String GETCASH_ERROR_UBB_MSG = "无效的银行卡";

    public static final String GETCASH_ERROR_UPA_CODE = "000004";
    public static final String GETCASH_ERROR_UPA_MSG = "无效的支付通道";

    public static final String GETCASH_ERROR_BANK_CODE = "000005";
    public static final String GETCASH_ERROR_BANK_MSG = "未绑定的银行卡";

    public static final String GETCASH_ERROR_USEMONRY_CODE = "000006";
    public static final String GETCASH_ERROR_USEMONRY_MSG = "出金金额加手续费大于可用余额";

    public static final String GETCASH_ERROR_FEE_CODE = "000007";
    public static final String GETCASH_ERROR_FEE_MSG = "手续费大于出金金额";

    public static final String GETCASH_ERROR_RECORD_CODE = "000008";
    public static final String GETCASH_ERROR_RECORD_MSG = "有交易正在进行中";


    /**获取所有支付记录 **/
    public static final String GETPAYRECORD_ERROR_MSG = "查询数据错误";
    public static final String GETPAYRECORD_ERROR_CODE = "000001";

    public static final String GETPAYRECORD_ERROR_MSG_1 = "IsBuy不能为空";
    public static final String GETPAYRECORD_ERROR_MSG_2 = "IsBuy必须为数字";
    public static final String GETPAYRECORD_ERROR_MSG_3 = "PaymentStatus不能为空";
    public static final String GETPAYRECORD_ERROR_MSG_4 = "PaymentStatus必须为数字";
    public static final String GETPAYRECORD_ERROR_MSG_5 = "StartDate不能为空";
    public static final String GETPAYRECORD_ERROR_MSG_6 = "EndDate不能为空";
    public static final String GETPAYRECORD_ERROR_MSG_7 = "StartDate日期格式错误";
    public static final String GETPAYRECORD_ERROR_MSG_8 = "EndDate日期格式错误";
    public static final String GETPAYRECORD_ERROR_MSG_9 = "Page不能为空";
    public static final String GETPAYRECORD_ERROR_MSG_10 = "Page必须为数字";
    public static final String GETPAYRECORD_ERROR_MSG_11 = "PageSize不能为空";
    public static final String GETPAYRECORD_ERROR_MSG_12 = "PageSize必须为数字";
    public static final String GETPAYRECORD_ERROR_MSG_13 = "subAccNo不能为空";

    /**获取支付详情 **/
    public static final String GETPAYDETAIL_ERROR_MSG_1 = "PaymentId不能为空";
    public static final String GETPAYDETAIL_ERROR_MSG_2 = "PaymentId必须为数字";

    public static final String GETPAYDETAIL_ERROR_MSG = "查询数据错误";
    public static final String GETPAYDETAIL_ERROR_CODE = "000001";

    public static final String GETPAYDETAIL_ERROR_NO_MSG = "找不到该数据";
    public static final String GETPAYDETAIL_ERROR_NO_CODE = "000002";

    public static final String GETPAYDETAIL_ERROR_NO_AUTH_MSG = "没有权限查看该记录";
    public static final String GETPAYDETAIL_ERROR_NO_AUTH_CODE = "000003";

    public static final String CLOSEPAYDETAIL_ERROR_MSG = "状态必须为代付款";
    public static final String CLOSEPAYDETAIL_ERROR_CODE = "000004";

    public static final String CLOSEPAYDETAIL_ERROR_CLOSE_MSG = "关闭交易失败";
    public static final String CLOSEPAYDETAIL_ERROR_CLOSE_CODE = "000005";

    /**获取对账单(交易记录查询) **/
    public static final String GETTRADINGRECORD_ERROR_MSG = "获取通道账户信息失败";
    public static final String GETTRADINGRECORD_ERROR_CODE = "000001";
    
    /**
     * 解冻
     */
    public static final String UNFREEZR_ERROR_MSG_1= "找不到支付记录,请检查paymentId参数";
    public static final String UNFREEZR_ERROR_CODE_1 = "000001";
    
    public static final String UNFREEZR_ERROR_MSG_2= "找不到冻结记录,请检查paymentId参数";
    public static final String UNFREEZR_ERROR_CODE_2 = "000002";
    
    public static final String UNFREEZR_ERROR_MSG_3= "更新支付记录状态失败.";
    public static final String UNFREEZR_ERROR_CODE_3 = "000003";
    
    public static final String UNFREEZR_ERROR_MSG_4= "复制支付记录失败.";
    public static final String UNFREEZR_ERROR_CODE_4 = "000004";
    
    public static final String UNFREEZR_ERROR_MSG_5= "更新原冻结记录、关联新支付记录编号失败.";
    public static final String UNFREEZR_ERROR_CODE_5 = "000005";
    
    public static final String UNFREEZR_ERROR_MSG_6= "更新原冻结记录、关联新支付记录编号失败.";
    public static final String UNFREEZR_ERROR_CODE_6 = "000006";
    
    public static final String UNFREEZR_ERROR_MSG_7= "更新原冻结记录、关联新支付记录编号失败.";
    public static final String UNFREEZR_ERROR_CODE_7 = "000007";
    
   public static final String UNFREEZR_ERROR_MSG_8= "请求中信失败.";
   public static final String UNFREEZR_ERROR_CODE_8 = "000008";
   
   public static final String UNFREEZR_ERROR_MSG_9= "有未解冻的补差价订单,请先处理";
   public static final String UNFREEZR_ERROR_CODE_9 = "000009";
   
   public static final String UNFREEZR_ERROR_MSG_10= "有正在冻结中的补差价订单,请等待冻结成功.";
   public static final String UNFREEZR_ERROR_CODE_10 = "000010";
   
   public static final String UNFREEZR_ERROR_MSG_11= "有待付款的补差价订单,请先关闭.";
   public static final String UNFREEZR_ERROR_CODE_11 = "000011";
    
}
