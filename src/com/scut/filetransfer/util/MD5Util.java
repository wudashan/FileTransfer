package com.scut.filetransfer.util;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * MD5º”√‹
 * 
 * @author ccz
 * 
 */
public class MD5Util {

	public static String getMD5(String val) throws NoSuchAlgorithmException {
		MessageDigest md5 = MessageDigest.getInstance("MD5");
		md5.update(val.getBytes());
		byte[] m = md5.digest();// º”√‹
		return getString(m);
	}

	private static String getString(byte[] b) {
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < b.length; i++) {
			sb.append(b[i]);
		}
		return sb.toString();
	}
}