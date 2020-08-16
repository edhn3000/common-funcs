package com.edhn.commons.test.cases;

import org.junit.Test;

import com.edhn.commons.encrypt.EncryptUtil;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class EncryptTests {
	
	@Test
	public void testHash() {
        String pass = "123456";
        log.info("md5:{}", EncryptUtil.encryptMD5(pass));
        log.info("sha1:{}", EncryptUtil.encryptSHA1(pass));
        log.info("sha256:{}", EncryptUtil.encryptSHA256(pass));
	}

}
