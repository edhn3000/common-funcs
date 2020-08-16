package com.edhn.commons.encrypt;

import org.mindrot.jbcrypt.BCrypt;

public class BCrpytUtil {
    
    public static String encrypt(String s) {
        String salt = BCrypt.gensalt(10);
        System.out.println("salt:" + salt + ",length=" + salt.length());
        return BCrypt.hashpw(s, salt);
    }
    
    public static boolean match(String input, String encryptStr) {
        return BCrypt.checkpw(input, encryptStr);
    }
    
    public static void main(String[] args) {
        String pass = "1";
        String encryptPass1 = BCrpytUtil.encrypt(pass);
        System.out.println("encryptPass1:" + encryptPass1);
        System.out.println("match of encryptPass1:" + match(pass, encryptPass1));
        String encryptPass2 = BCrpytUtil.encrypt(pass);
        System.out.println("encryptPass2:" + encryptPass2);
        System.out.println("match of encryptPass2:" + match(pass, encryptPass2));
        
        System.out.println("match of 111:" + match("111", encryptPass2));
        
        long start = System.currentTimeMillis();
        System.out.println("end:" + (System.currentTimeMillis()-start));
        
    }

}
