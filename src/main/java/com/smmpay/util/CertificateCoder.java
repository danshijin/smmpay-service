package com.smmpay.util;
import java.io.InputStream;
import java.math.BigInteger;
import java.net.URL;
import java.security.KeyFactory;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.security.spec.RSAPublicKeySpec;
import java.util.Date;

import javax.crypto.Cipher;


/**
 * 证书组件
 * 
 * @version 1.0
 * @since 1.0
 */
public abstract class CertificateCoder {
	/**
	 * Java密钥�?Java Key Store，JKS)KEY_STORE
	 */
	public static final String KEY_STORE = "JKS";

	public static final String X509 = "X.509";

	public static PublicKey getPublicKey() throws Exception {

		String modulus = "110650354432426107930751531813574087280159327915064107126610938514852377419585992127754550703857385113588507705164196179802076570603530462010340973158216109469539814740378942272047009375386249954080188633783672599085760057537567421206878608007329675073957104124596128137851196232103365106576893048750852318851";
		String publicExponent = "65537";
		BigInteger m = new BigInteger(modulus);

		BigInteger e = new BigInteger(publicExponent);

		RSAPublicKeySpec keySpec = new RSAPublicKeySpec(m, e);

		KeyFactory keyFactory = KeyFactory.getInstance("RSA");
		PublicKey publicKey = keyFactory.generatePublic(keySpec);

		return publicKey;

	}

	public static PublicKey getPublicKey(String certificatePath) throws Exception {
		Certificate certificate = getCertificate(certificatePath);
		PublicKey key = certificate.getPublicKey();
		return key;
	}

	// public static PrivateKey getPrivateKey() throws Exception {
	 String modulus =
	 "110650354432426107930751531813574087280159327915064107126610938514852377419585992127754550703857385113588507705164196179802076570603530462010340973158216109469539814740378942272047009375386249954080188633783672599085760057537567421206878608007329675073957104124596128137851196232103365106576893048750852318851";
	 String privateExponent =
	 "19429700456358387629386279935160452819324557816905835555686691188625069187552002645928244648061259866749722243481233038393003908853097159723743899157800185324170555439865247891548344261707673614960869572396170491319267108846696650626047587726679719644777000975492319613110175717515765043535116401356531399809";
	// BigInteger m = new BigInteger(modulus);
	//
	// BigInteger e = new BigInteger(privateExponent);
	//
	// RSAPrivateKeySpec keySpec = new RSAPrivateKeySpec(m,e);
	//
	// KeyFactory keyFactory = KeyFactory.getInstance("RSA");
	//
	// PrivateKey privateKey = keyFactory.generatePrivate(keySpec);
	//
	// return privateKey;
	//
	// }

	// ///////////////////////

	/**
	 * 由KeyStore获得私钥
	 * 
	 * @param keyStorePath
	 * @param alias
	 * @param password
	 * @return
	 * @throws Exception
	 *//*
	private static PrivateKey getPrivateKey(String keyStorePath, String alias,
			String password) throws Exception {
		KeyStore ks = getKeyStore(keyStorePath, password);
		PrivateKey key = (PrivateKey) ks.getKey(alias, password.toCharArray());
		return key;
	}*/

	/**
	 * 由Certificate获得公钥
	 * 
	 * @param certificatePath
	 * @return
	 * @throws Exception
	 */
	private static PublicKey getPublicKeyBy(String certificatePath)
			throws Exception {
		Certificate certificate = getCertificate(certificatePath);
		PublicKey key = certificate.getPublicKey();
		return key;
	}


	public static PrivateKey getPrivateKey(String keyStorePath, String alias, String password)
	 throws Exception {
		KeyStore ks = getKeyStore(keyStorePath, password);
		PrivateKey key = (PrivateKey) ks.getKey(alias, password.toCharArray());
		return key;
		}
	
	private static KeyStore getKeyStore(String keyStorePath, String password) throws Exception {
	    InputStream is = CertificateCoder.class.getClassLoader().getResourceAsStream(keyStorePath);
		KeyStore ks = KeyStore.getInstance(KEY_STORE);
		ks.load(is, password.toCharArray());
		is.close();
		return ks;
}

	/**
	 * 获得Certificate
	 * 
	 * @param certificatePath
	 * @return
	 * @throws Exception
	 */
	private static Certificate getCertificate(String certificatePath)
			throws Exception {
		CertificateFactory certificateFactory = CertificateFactory
				.getInstance(X509);
		// java
	// FileInputStream in = new FileInputStream(certificatePath);
		// android
		// InputStream in = (InputStream)
		// MainPageActivity.context.getResources().openRawResource(R.raw.myprivate);
		InputStream in = CertificateCoder.class.getClassLoader().getResourceAsStream(certificatePath);
		Certificate certificate = certificateFactory.generateCertificate(in);
		in.close();

		return certificate;
	}

	/**
	 * 获得Certificate
	 * 
	 * @param keyStorePath
	 * @param alias
	 * @param password
	 * @return
	 * @throws Exception
	 */
	private static Certificate getCertificate(String keyStorePath,
			String alias, String password) throws Exception {
		KeyStore ks = getKeyStore(keyStorePath, password);
		Certificate certificate = ks.getCertificate(alias);

		return certificate;
	}

	/**
	 * 获得KeyStore
	 * 
	 * @param keyStorePath
	 * @param password
	 * @return
	 * @throws Exception
	 */
//	private static KeyStore getKeyStore(String keyStorePath, String password)
//			throws Exception {
//  	    FileInputStream is = new FileInputStream(keyStorePath);
//		InputStream in = MainPageActivity.context.getAssets().open(
//				"xyy.keystore");
//		KeyStore ks = KeyStore.getInstance(KEY_STORE);
//		ks.load(in, password.toCharArray());
//		in.close();
//		return ks;
//	}

	/**
	 * 私钥加密
	 * 
	 * @param data
	 * @param keyStorePath
	 * @param alias
	 * @param password
	 * @return
	 * @throws Exception
	 */
	public static byte[] encryptByPrivateKey(byte[] data, String keyStorePath,
			String alias, String password) throws Exception {
		// 取得私钥
		PrivateKey privateKey = getPrivateKey(keyStorePath, alias, password);

		// 对数据加�?
		Cipher cipher = Cipher.getInstance(privateKey.getAlgorithm());
		cipher.init(Cipher.ENCRYPT_MODE, privateKey);

		return cipher.doFinal(data);

	}

	/**
	 * 私钥解密
	 * 
	 * @param data
	 * @param keyStorePath
	 * @param alias
	 * @param password
	 * @return
	 * @throws Exception
	 */
	 public static byte[] decryptByPrivateKey(byte[] data, String
	 keyStorePath,
	 String alias, String password) throws Exception {
	 // 取得私钥
	 PrivateKey privateKey = getPrivateKey(keyStorePath,alias,password);
	  
	 // 对数据加�?
	 Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1PADDING");
	 cipher.init(Cipher.DECRYPT_MODE, privateKey);
	  
	 return cipher.doFinal(data);
	  
	 }

	/**
	 * 公钥加密
	 * 
	 * @param data
	 * @param certificatePath
	 * @return
	 * @throws Exception
	 */
	public static byte[] encryptByPublicKey(byte[] data, String certificatePath)
			throws Exception {

		// 取得公钥
		PublicKey publicKey = getPublicKey(certificatePath);
		// 对数据加�?
		Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1PADDING");
		cipher.init(Cipher.ENCRYPT_MODE, publicKey);

		return cipher.doFinal(data);

	}

	/**
	 * 公钥解密
	 * 
	 * @param data
	 * @param certificatePath
	 * @return
	 * @throws Exception
	 */
	public static byte[] decryptByPublicKey(byte[] data, String certificatePath)
			throws Exception {
		// 取得公钥
		PublicKey publicKey = getPublicKey(certificatePath);

		// 对数据加�?
		Cipher cipher = Cipher.getInstance(publicKey.getAlgorithm());
		cipher.init(Cipher.DECRYPT_MODE, publicKey);

		return cipher.doFinal(data);

	}

	/**
	 * 验证Certificate
	 * 
	 * @param certificatePath
	 * @return
	 */
//	public static boolean verifyCertificate(String certificatePath) {
//		return verifyCertificate(new Date(), certificatePath);
//	}

	/**
	 * 验证Certificate是否过期或无�?
	 * 
	 * @param date
	 * @param certificatePath
	 * @return
	 */
//	public static boolean verifyCertificate(Date date, String certificatePath) {
//		boolean status = true;
//		try {
//			// 取得证书
//			Certificate certificate = getCertificate(certificatePath);
//			// 验证证书是否过期或无�?
//			status = verifyCertificate(date, certificate);
//		} catch (Exception e) {
//			status = false;
//		}
//		return status;
//	}

	/**
	 * 验证证书是否过期或无�?
	 * 
	 * @param date
	 * @param certificate
	 * @return
	 */
	private static boolean verifyCertificate(Date date, Certificate certificate) {
		boolean status = true;
		try {
			X509Certificate x509Certificate = (X509Certificate) certificate;
			x509Certificate.checkValidity(date);
		} catch (Exception e) {
			status = false;
		}
		return status;
	}

	/**
	 * 签名
	 * 
	 * @param keyStorePath
	 * @param alias
	 * @param password
	 * 
	 * @return
	 * @throws Exception
	 */
	public static String sign(byte[] sign, String keyStorePath, String alias,
			String password) throws Exception {
		// 获得证书
		X509Certificate x509Certificate = (X509Certificate) getCertificate(
				keyStorePath, alias, password);
		// 获取私钥
		KeyStore ks = getKeyStore(keyStorePath, password);
		// 取得私钥
		PrivateKey privateKey = (PrivateKey) ks.getKey(alias, password
				.toCharArray());

		// 构建签名
		Signature signature = Signature.getInstance(x509Certificate
				.getSigAlgName());
		signature.initSign(privateKey);
		signature.update(sign);
		return Base64.getBase64(signature.sign());
	}

	/**
	 * 验证签名
	 * 
	 * @param data
	 * @param sign
	 * @param certificatePath
	 * @return
	 * @throws Exception
	 */
	public static boolean verify(byte[] data, String sign,
			String certificatePath) throws Exception {
		// 获得证书
		X509Certificate x509Certificate = (X509Certificate) getCertificate(certificatePath);
		// 获得公钥
		PublicKey publicKey = x509Certificate.getPublicKey();
		// 构建签名
		Signature signature = Signature.getInstance(x509Certificate
				.getSigAlgName());
		signature.initVerify(publicKey);
		signature.update(data);

		return signature.verify(Base64.getFromBase64(sign));

	}

	/**
	 * 验证Certificate
	 * 
	 * @param keyStorePath
	 * @param alias
	 * @param password
	 * @return
	 */
	public static boolean verifyCertificate(Date date, String keyStorePath,
			String alias, String password) {
		boolean status = true;
		try {
			Certificate certificate = getCertificate(keyStorePath, alias,
					password);
			status = verifyCertificate(date, certificate);
		} catch (Exception e) {
			status = false;
		}
		return status;
	}

	/**
	 * 验证Certificate
	 * 
	 * @param keyStorePath
	 * @param alias
	 * @param password
	 * @return
	 */
	public static boolean verifyCertificate(String keyStorePath, String alias,
			String password) {
		return verifyCertificate(new Date(), keyStorePath, alias, password);
	}
	
	public static void main(String args[]) throws Exception{
		try{
			String strTemp="12345678 12345678 123456";
			String desKey = "{\"data\":[{\"billCode\":\"3130005135467579\",\"resultCode\":\"100004\",\"resultContent\":\"已审核\"}]}";
			
			URL url = ClassLoader.getSystemResource("smm.cer");
			URL urlPri = ClassLoader.getSystemResource("smm.keystore");
			System.out.println("加密�?"+url.getPath());			
			byte[] encryptByte = encryptByPublicKey(desKey.getBytes(),url.getPath());
			String encryptStr = Base64.getBase64(encryptByte);
			System.out.println("加密�?"+encryptStr);	
			
			byte[] decryptStr =decryptByPrivateKey(Base64.getFromBase64(encryptStr),urlPri.getPath(),"www.smm.com","smm_123");
			System.out.println("解密�?"+new String(decryptStr));	
			
			
			
			//byte[] jieEndStr=decryptByPrivateKey(bb,"smm.keystore","www.smm.com","www.smm.com");
			
	    	//System.out.println(ecPostJson);
		}catch(Exception e){
			e.printStackTrace();
		}
	}
}