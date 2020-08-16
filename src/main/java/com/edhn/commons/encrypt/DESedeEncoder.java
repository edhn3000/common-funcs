/**
 * @FileName: DESedeEncoder.java
 * @Description: ...
 * @ProjectName: WritAnalyze
 * @Author: chenjinghui
 * @Date: 2015-1-7
 * @CopyRight: THUNISOFT 2015
 */
package com.edhn.commons.encrypt;

import java.security.SecureRandom;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESedeKeySpec;

public class DESedeEncoder {
	public static final String KEY_ALGORITHM = "DESede";
	
	private byte[] symKey;
	
	/**
	 * DESede加密
	 * 
	 * @param data
	 * @return
	 * @throws Exception
	 */
	public byte[] encryptDESede(byte[] data)
			throws Exception {
        // 从原始密钥数据创建DESedeKeySpec对象
        DESedeKeySpec dks = new DESedeKeySpec(symKey);
 
        // 创建一个密钥工厂，然后用它把DESedeKeySpec转换成SecretKey对象
        SecretKeyFactory keyFactory = SecretKeyFactory.getInstance(KEY_ALGORITHM);
        SecretKey securekey = keyFactory.generateSecret(dks);
 
        // Cipher对象实际完成加密操作
        Cipher cipher = Cipher.getInstance(keyFactory.getAlgorithm());
 
        // 用密钥初始化Cipher对象
        cipher.init(Cipher.ENCRYPT_MODE, securekey);
 
        return cipher.doFinal(data);
	}
	
	/**
	 * DESede解密
	 * 
	 * @param data
	 * @return
	 * @throws Exception
	 */
	public byte[] decryptDESede(byte[] data) throws Exception {
        // 从原始密钥数据创建DESedeKeySpec对象
        DESedeKeySpec dks = new DESedeKeySpec(symKey);
 
        // 创建一个密钥工厂，然后用它把DESedeKeySpec转换成SecretKey对象
        SecretKeyFactory keyFactory = SecretKeyFactory.getInstance(KEY_ALGORITHM);
        SecretKey securekey = keyFactory.generateSecret(dks);
 
        // Cipher对象实际完成解密操作
        Cipher cipher = Cipher.getInstance(keyFactory.getAlgorithm());
 
        // 用密钥初始化Cipher对象
        cipher.init(Cipher.DECRYPT_MODE, securekey);
 
        return cipher.doFinal(data);
    }
	
	/**
	 * 初始化密钥
	 * 
	 * @return
	 * @throws Exception
	 */
	public void initKey() throws Exception {
		SecureRandom secureRandom = new SecureRandom();

		KeyGenerator kg = KeyGenerator.getInstance(KEY_ALGORITHM);
		kg.init(secureRandom);

		SecretKey secretKey = kg.generateKey();
		symKey = secretKey.getEncoded();
	}
	
	/**
	 * 设置密钥
	 * 
	 * @param key
	 * @throws Exception
	 */
	public void setKey(byte[] key) {
		symKey = key;
	}

	/**
	 * 取得密钥
	 * 
	 * @return
	 * @throws Exception
	 */
	public byte[] getKey() {
		return symKey;
	}
}


