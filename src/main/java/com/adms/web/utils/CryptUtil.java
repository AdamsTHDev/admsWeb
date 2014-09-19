package com.adms.web.utils;


public class CryptUtil {
	
	private static CryptUtil instatnce = null;
	
	protected CryptUtil() {
		
	}
	
	public static CryptUtil getInstance() {
		if(instatnce == null) {
			instatnce = new CryptUtil();
		}
		return instatnce;
	}

	public String base64Encode(byte[] data) {
		return org.primefaces.util.Base64.encodeToString(data, false);
	}
	
	public byte[] base64Decode(String data) {
		return org.primefaces.util.Base64.decodeFast(data);
	}
	
}
