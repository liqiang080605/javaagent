package com.jd.jvm.jmonitor.core.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AuthUtils {
	private static final Logger logger = LoggerFactory.getLogger(AuthUtils.class);
	
	private static final String AUTH = "monitor_token_key";
	
	private static final long TIME_INTERVAL = 60;
	
	public static boolean auth(String timestamp, String token) {
		if(StringUtils.isBlank(timestamp) || StringUtils.isBlank(token)) {
			logger.info("Auth params is wrong!");
			return false;
		}
		
		long currentTime = System.currentTimeMillis()/1000;
		
		if(Math.abs(currentTime - Long.valueOf(timestamp)) > TIME_INTERVAL) {
			logger.info("Auth params is wrong!");
			return false;
		}
		
		String authToken = md5(timestamp + "|" + AUTH);
		if(!token.equalsIgnoreCase(authToken)) {
			logger.info("Auth params is wrong!");
			return false;
		}
		return true;
	}
	
	public static String md5(String str) {
		String result = null;
		try {
			result = new String(str);
			MessageDigest md = MessageDigest.getInstance("MD5");
			StringBuilder sb = new StringBuilder();
			for (byte b : md.digest(str.getBytes())) {
				sb.append(String.format("%02X", b));
			}
			return sb.toString();
		} catch (NoSuchAlgorithmException ex) {
			ex.printStackTrace();
		}
		return result;
	}
}
