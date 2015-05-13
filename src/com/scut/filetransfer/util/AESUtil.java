package com.scut.filetransfer.util;

import java.security.SecureRandom;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import android.annotation.SuppressLint;

public class AESUtil {

	private static AESUtil aesUtil;
	private static String seed;

	private AESUtil() {

	}

	public static AESUtil getInstance(String seed) {
		if (aesUtil == null) {
			aesUtil = new AESUtil();
		}
		AESUtil.seed = seed;
		System.out.println("对称加密密码：" + seed);
		return aesUtil;
	}
	
	public static AESUtil getInstance() {
		if (aesUtil == null) {
			aesUtil = new AESUtil();
		}
		return aesUtil;
	}

	/**
	 * 加密调用的接口
	 * 
	 * @param seed
	 * @param cleartext
	 * @return
	 * @throws Exception
	 */
	public synchronized byte[] encrypt(byte[] cleartext) throws Exception {
		byte[] rawKey = getRawKey(seed.getBytes());
		byte[] result = encrypt(rawKey, cleartext);
		return toHex(result).getBytes();
	}

	/**
	 * 解密调用的接口
	 * 
	 * @param seed
	 * @param encrypted
	 * @return
	 * @throws Exception
	 */
	public synchronized byte[] decrypt(byte[] encrypted) throws Exception {
		byte[] rawKey = getRawKey(seed.getBytes());
		byte[] enc = toByte(new String(encrypted));
		byte[] result = decrypt(rawKey, enc);
		return result;
	}

	public String getSeed() {
		return seed;
	}

	@SuppressWarnings("static-access")
	public void setSeed(String seed) {
		this.seed = seed;
	}

	@SuppressLint("TrulyRandom")
	private byte[] encrypt(byte[] raw, byte[] clear) throws Exception {
		SecretKeySpec skeySpec = new SecretKeySpec(raw, "AES");
		Cipher cipher = Cipher.getInstance("AES");
		cipher.init(Cipher.ENCRYPT_MODE, skeySpec);
		byte[] encrypted = cipher.doFinal(clear);
		return encrypted;
	}

	private String toHex(byte[] buf) {
		final String HEX = "0123456789ABCDEF";
		if (buf == null)
			return "";
		StringBuffer result = new StringBuffer(2 * buf.length);
		for (int i = 0; i < buf.length; i++) {
			result.append(HEX.charAt((buf[i] >> 4) & 0x0f)).append(
					HEX.charAt(buf[i] & 0x0f));
		}
		return result.toString();
	}

	private byte[] decrypt(byte[] raw, byte[] encrypted) throws Exception {
		SecretKeySpec skeySpec = new SecretKeySpec(raw, "AES");
		Cipher cipher = Cipher.getInstance("AES");
		cipher.init(Cipher.DECRYPT_MODE, skeySpec);
		byte[] decrypted = cipher.doFinal(encrypted);
		return decrypted;
	}

	private byte[] toByte(String hexString) {
		int len = hexString.length() / 2;
		byte[] result = new byte[len];
		for (int i = 0; i < len; i++)
			result[i] = Integer.valueOf(hexString.substring(2 * i, 2 * i + 2),
					16).byteValue();
		return result;
	}

	/**
	 * 获取key
	 * 
	 * @param seed
	 * @return
	 * @throws Exception
	 */
	private byte[] getRawKey(byte[] seed) throws Exception {
		KeyGenerator kgen = KeyGenerator.getInstance("AES");
		SecureRandom sr = SecureRandom.getInstance("SHA1PRNG");
		sr.setSeed(seed);
		kgen.init(128, sr);
		SecretKey skey = kgen.generateKey();
		byte[] raw = skey.getEncoded();
		return raw;
	}
}