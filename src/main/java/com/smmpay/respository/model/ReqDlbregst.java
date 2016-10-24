package com.smmpay.respository.model;

import java.io.Serializable;

import com.smmpay.respository.model.base.ReqCommon2;

/**
 * 附属账户预签约
 * 请求报文
 * @author zengshihua
 *
 */
public class ReqDlbregst extends ReqCommon2 implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1661823384852565759L;
	
	/**
	 * 请求码
	 */
	private final String action="DLBREGST";
	
	
	/**
	 * 应用系统char(1)， 
	 *    2：B2B电子商务；
	 *    3：投标保证金
	 */
	private String appFlag="2";
	
	/**
	 * 附属账户生成方式char(1) ，0：自动输入 ；1：手动生成
	 */
	private String accGenType="0";
	
	/**
	 * 附属账号 char(14) ，在accGenType生成方式为1：手动输入时，必输；为0：自动生成时可空
	 */
	private String subAccNo;
	
	/**
	 * 附属账户名称 varchar(100)，可空，appFlag为2时必输，appFlag为3时可空，若不为空则其值必须为客户名称
	 */
	private String subAccNm;
	
	/**
	 * 附属账户类型 char(2)，
	 * 03：一般交易账号；
	 * 04：保证金账号；
	 * 11：招投标保证金
	 */
	private String accType="03";
	
	/**
	 * 是否计算利息标志 char(1)， 
	 * 	0：不计息；
	 * 	1：不分段计息；
	 * 	2：分段计息；
	 * 当appFlag为3时，是否计算利息标志必须为0
	 */
	private String calInterestFlag="0";
	
	/**
	 * -默认计息利率 decimal(9.7)，calInterestFlag为 0时，可空；appFlag为3时，必须为0
	 */
	private String interestRate;
	
	/**
	 * 是否允许透支char(1)，
	 * 	0：不允许；
	 * 	1：限额透支；
	 * 	2：全额透支 ；
	 * appFlag为3时，必须为0
	 */
	private String overFlag="0";
	
	/**
	 * 透支额度decimal(15.2)，当overFlag为 0时，可空；appFlag为3时，必须为空
	 */
	private String overAmt;
	
	/**
	 * 透支利率decimal(9.7)，
	 * 当overFlag为 0时，可空；appFlag为3时，必须为空
	 */
	private String overRate;
	
	/**
	 * 自动分配利息标示char(1)，
	 * 	0：否；
	 * 	1：是；
	 * appFlag为3时，必须为0
	 */
	private String autoAssignInterestFlag="0";
	
	/**
	 * 自动分配转账手续费标char(1)，
	 * 	0：否；
	 * 	1：是；
	 * appFlag为3时，必须为0
	 */
	private String autoAssignTranFeeFlag="0";
	
	/**
	 * 手续费收取方式 char(1)，
	 * 	0：不收取；
	 * 	1：实时收取；
	 * 	2：月末累计；
	 * appFlag为3时，必须为0
	 */
	private String feeType="1";
	
	/**
	 * 实名制更换char(1) ，
	 * 	0：账户名与账号全不换；
	 * 	1：账户名与账号全换；
	 * 	2：换账户名；
	 * 	3：换账号；
	 * appFlag为3时，必须为0
	 */
	private String realNameParm="1";
	
	/**
	 * -附属账户凭证打印更换 char(1)，
	 * 		0：全部显示；
	 * 		1：显示附属账户名和账号；
	 * 		2：显示实体账户名和账号；
	 * 		3：显示附属账户名和实体账号；
	 * 		4：显示实体账户名和附属账号；
	 * 	appFlag为3时，必须为0
	 */
	private String subAccPrintParm="1";
	
	/**
	 * 会与确认中心231001
	 */
	private String mngNode="231001";
	
	//测试数据
	/*public static ReqDlbregst init() {
		ReqDlbregst rb = new ReqDlbregst();
		rb.setAction("DLBREGST");
		rb.setUserName("YSWTEST");
		rb.setMainAccNo("7313510182600037822");
		rb.setAppFlag("2");
		rb.setAccGenType("0");
		rb.setSubAccNo("");
		rb.setSubAccNm("上海有色网测试附属账户456");
		rb.setAccType("03");
		rb.setCalInterestFlag("0");
		rb.setInterestRate("");
		rb.setOverFlag("0");
		rb.setOverAmt("");
		rb.setOverRate("");
		rb.setAutoAssignInterestFlag("1");
		rb.setAutoAssignTranFeeFlag("1");
		rb.setFeeType("2");
		rb.setRealNameParm("0");
		rb.setSubAccPrintParm("0");
		rb.setMngNode("231001");
		return rb;
	}*/

	public String getAction() {
		return action;
	}

	public String getAppFlag() {
		return appFlag;
	}

	public void setAppFlag(String appFlag) {
		this.appFlag = appFlag;
	}

	public String getAccGenType() {
		return accGenType;
	}

	public void setAccGenType(String accGenType) {
		this.accGenType = accGenType;
	}

	public String getSubAccNo() {
		return subAccNo;
	}

	public void setSubAccNo(String subAccNo) {
		this.subAccNo = subAccNo;
	}

	public String getSubAccNm() {
		return subAccNm;
	}

	public void setSubAccNm(String subAccNm) {
		this.subAccNm = subAccNm;
	}

	public String getAccType() {
		return accType;
	}

	public void setAccType(String accType) {
		this.accType = accType;
	}

	public String getCalInterestFlag() {
		return calInterestFlag;
	}

	public void setCalInterestFlag(String calInterestFlag) {
		this.calInterestFlag = calInterestFlag;
	}

	public String getInterestRate() {
		return interestRate;
	}

	public void setInterestRate(String interestRate) {
		this.interestRate = interestRate;
	}

	public String getOverFlag() {
		return overFlag;
	}

	public void setOverFlag(String overFlag) {
		this.overFlag = overFlag;
	}

	public String getOverAmt() {
		return overAmt;
	}

	public void setOverAmt(String overAmt) {
		this.overAmt = overAmt;
	}

	public String getOverRate() {
		return overRate;
	}

	public void setOverRate(String overRate) {
		this.overRate = overRate;
	}

	public String getAutoAssignInterestFlag() {
		return autoAssignInterestFlag;
	}

	public void setAutoAssignInterestFlag(String autoAssignInterestFlag) {
		this.autoAssignInterestFlag = autoAssignInterestFlag;
	}

	public String getAutoAssignTranFeeFlag() {
		return autoAssignTranFeeFlag;
	}

	public void setAutoAssignTranFeeFlag(String autoAssignTranFeeFlag) {
		this.autoAssignTranFeeFlag = autoAssignTranFeeFlag;
	}

	public String getFeeType() {
		return feeType;
	}

	public void setFeeType(String feeType) {
		this.feeType = feeType;
	}

	public String getRealNameParm() {
		return realNameParm;
	}

	public void setRealNameParm(String realNameParm) {
		this.realNameParm = realNameParm;
	}

	public String getSubAccPrintParm() {
		return subAccPrintParm;
	}

	public void setSubAccPrintParm(String subAccPrintParm) {
		this.subAccPrintParm = subAccPrintParm;
	}

	public String getMngNode() {
		return mngNode;
	}

	public void setMngNode(String mngNode) {
		this.mngNode = mngNode;
	}

}
