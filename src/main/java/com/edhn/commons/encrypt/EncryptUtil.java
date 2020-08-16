package com.edhn.commons.encrypt;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * EncryptUtil
 * @author fengyq
 * @version 1.0
 *
 */
public class EncryptUtil {

    /**
     * 通用加密
     * @param data
     * @param algorithm
     * @return
     * @throws NoSuchAlgorithmException 
     */
    public static String encrypt(byte[] data, String algorithm) throws NoSuchAlgorithmException {
        StringBuilder sb = new StringBuilder();
        MessageDigest digest = null;
        digest = MessageDigest.getInstance(algorithm);
        //生成一组length=16的byte数组  
        byte[] bs = digest.digest(data);
        for (int i = 0; i < bs.length; i++) {
            int c = bs[i] & 0xFF; //byte转int为了不丢失符号位， 所以&0xFF  
            if (c < 16) { //如果c小于16，就说明，可以只用1位16进制来表示， 那么在前面补一个0  
                sb.append("0");
            }
            sb.append(Integer.toHexString(c));
        }
        return sb.toString();
    }
    
    /**
     * MD5加密
     * @param s 要加密的字符串
     * @return
     */
    public static String encryptMD5(String s) {
        try {
            return encrypt(s.getBytes(), "MD5");
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }
    
    /**
     * SHA1加密
     * @param s 要加密的字符串
     * @return
     */
    public static String encryptSHA1(String s) {
        try {
            return encrypt(s.getBytes(), "SHA1");
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }
    
    /**
     * SHA256加密
     * @param s 要加密的字符串
     * @return
     */
    public static String encryptSHA256(String s) {
        try {
            return encrypt(s.getBytes(), "SHA-256");
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }
}
