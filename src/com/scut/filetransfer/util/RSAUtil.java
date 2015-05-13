package com.scut.filetransfer.util;
import java.util.Map;

public class RSAUtil {

	private static RSAUtil rsaUtil;
	private String publicKey;
	private String privateKey;

	private RSAUtil() {
		Map<String, Object> keyMap;
		try {
			keyMap = RSACoder.genKeyPair();
			publicKey = RSACoder.getPublicKey(keyMap);
			privateKey = RSACoder.getPrivateKey(keyMap);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static RSAUtil getInstance() {
		if (rsaUtil == null) {
			rsaUtil = new RSAUtil();
		}
		return rsaUtil;
	}

	/**
	 * π´‘øº”√‹
	 * 
	 * @param message
	 * @return
	 */
	public byte[] encodeByPublic(String message) {
		return encodeByPublic(message, publicKey);
	}
	
	public byte[] encodeByPublic(String message, String key) {
		byte[] data = message.getBytes();
		byte[] encodedData = null;
		try {
			encodedData = RSACoder.encryptByPublicKey(data, key);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return encodedData;
	}

	/**
	 * ÀΩ‘øº”√‹
	 * 
	 * @param message
	 * @return
	 */
	public byte[] encodeByPrivate(String message) {
		byte[] data = message.getBytes();
		byte[] encodedData = null;
		try {
			encodedData = RSACoder.encryptByPrivateKey(data, privateKey);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return encodedData;
	}

	/**
	 * π´‘øΩ‚√‹
	 * 
	 * @param message
	 * @return
	 */
	public String decodeByPublic(byte[] data) {
		return decodeByPublic(data, publicKey);
	}

	public String decodeByPublic(byte[] data, String key) {
		byte[] decodedData = null;
		try {
			decodedData = RSACoder.decryptByPublicKey(data, key);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return new String(decodedData);
	}
	/**
	 * ÀΩ‘øΩ‚√‹
	 * 
	 * @param message
	 * @return
	 */
	public String decodeByPrivate(byte[] data) {
		byte[] decodedData = null;
		try {
			decodedData = RSACoder.decryptByPrivateKey(data, privateKey);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return new String(decodedData);
	}

	public String getPublicKey() {
		return publicKey;
	}

	public void setPublicKey(String publicKey) {
		this.publicKey = publicKey;
	}

	public String getPrivateKey() {
		return privateKey;
	}

	public void setPrivateKey(String privateKey) {
		this.privateKey = privateKey;
	}

}