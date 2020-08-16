/**
 * @FileName: RSAEncoder.java
 * @Description: ...
 * @ProjectName: WritAnalyze
 * @Author: chenjinghui
 * @Date: 2015-1-7
 * @CopyRight: THUNISOFT 2015
 */
package com.edhn.commons.encrypt;

import java.security.Key;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

import javax.crypto.Cipher;

public class RSAEncoder {
	public static final String KEY_ALGORITHM = "RSA";
	public static final String SIGNATURE_ALGORITHM = "MD5withRSA";

	private byte[] publicKey;
	private byte[] privateKey;
	
	/**
	 * 加密
	 * 用私钥加密
	 * 
	 * @param data
	 * @param key
	 * @return
	 * @throws Exception
	 */
	public byte[] encryptByPrivateKey(byte[] data)
			throws Exception {
		// 取得私钥
		PKCS8EncodedKeySpec pkcs8KeySpec = new PKCS8EncodedKeySpec(privateKey);
		KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
		Key privateKey = keyFactory.generatePrivate(pkcs8KeySpec);

		// 对数据加密
		Cipher cipher = Cipher.getInstance(keyFactory.getAlgorithm());
		cipher.init(Cipher.ENCRYPT_MODE, privateKey);

		return cipher.doFinal(data);
	}
	
	/**
	 * 解密
	 * 用公钥解密
	 * 
	 * @param data
	 * @param key
	 * @return
	 * @throws Exception
	 */
	public byte[] decryptByPublicKey(byte[] data)
			throws Exception {
		// 取得公钥
		X509EncodedKeySpec x509KeySpec = new X509EncodedKeySpec(publicKey);
		KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
		Key publicKey = keyFactory.generatePublic(x509KeySpec);

		// 对数据解密
		Cipher cipher = Cipher.getInstance(keyFactory.getAlgorithm());
		cipher.init(Cipher.DECRYPT_MODE, publicKey);

		return cipher.doFinal(data);
	}
	
	/**
	 * 用私钥对信息生成数字签名
	 * 
	 * @param data
	 * @return
	 * @throws Exception
	 */
	public byte[] sign(byte[] data) throws Exception {
		// 构造PKCS8EncodedKeySpec对象
		PKCS8EncodedKeySpec pkcs8KeySpec = new PKCS8EncodedKeySpec(privateKey);

		// KEY_ALGORITHM 指定的加密算法
		KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);

		// 取私钥匙对象
		PrivateKey priKey = keyFactory.generatePrivate(pkcs8KeySpec);

		// 用私钥对信息生成数字签名
		Signature signature = Signature.getInstance(SIGNATURE_ALGORITHM);
		signature.initSign(priKey);
		signature.update(data);

		return signature.sign();
	}

	/**
	 * 校验数字签名
	 * 
	 * @param data
	 * @param sign
	 * @return
	 * @throws Exception
	 */
	public boolean verify(byte[] data, byte[] sign) throws Exception {
		// 构造X509EncodedKeySpec对象
		X509EncodedKeySpec keySpec = new X509EncodedKeySpec(publicKey);

		// KEY_ALGORITHM 指定的加密算法
		KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);

		// 取公钥匙对象
		PublicKey pubKey = keyFactory.generatePublic(keySpec);

		Signature signature = Signature.getInstance(SIGNATURE_ALGORITHM);
		signature.initVerify(pubKey);
		signature.update(data);

		// 验证签名是否正常
		return signature.verify(sign);
	}

	/**
	 * 取得私钥
	 * @return
	 * @throws Exception
	 */
	public byte[] getPrivateKey() {
		return privateKey;
	}
	
	/**
	 * 设置私钥
	 * @param prvk
	 */
	public void setPrivateKey(byte[] key) {
		privateKey = key;
	}

	/**
	 * 取得公钥
	 * @return
	 * @throws Exception
	 */
	public byte[] getPublicKey() {
		return publicKey;
	}
	
	/**
	 * 设置公钥
	 * @param pubk
	 */
	public void setPublicKey(byte[] key) {
		publicKey = key;
	}

	/**
	 * 初始化密钥
	 * 
	 * @return
	 * @throws Exception
	 */
	public void initKey() throws Exception {
		KeyPairGenerator keyPairGen = KeyPairGenerator
				.getInstance(KEY_ALGORITHM);
		
		// 可加密2048/8-11=245字节的内容
		keyPairGen.initialize(2048);

		KeyPair keyPair = keyPairGen.generateKeyPair();

		// 公钥
		RSAPublicKey pubk = (RSAPublicKey) keyPair.getPublic();
		publicKey = pubk.getEncoded();

		// 私钥
		RSAPrivateKey prvk = (RSAPrivateKey) keyPair.getPrivate();
		privateKey = prvk.getEncoded();
	}
}


