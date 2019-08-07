package com.nh.wx.util;

import org.springframework.beans.factory.annotation.Value;

public class ParamHolder {
	public static String appId;
	public static String appSecret;
	public static String basePath;
	public static String getBasePath() {
		return basePath;
	}

	@Value("${wx.basepath}")
	public void setBasePath(String basePath) {
		ParamHolder.basePath = basePath;
	}

	public static String getAppId() {
		return appId;
	}
	
	@Value("${wx.appid}")
	public void setAppId(String appId) {
		ParamHolder.appId = appId;
	}
	public static String getAppSecret() {
		return appSecret;
	}
	@Value("${wx.appsecret}")
	public void setAppSecret(String appSecret) {
		ParamHolder.appSecret = appSecret;
	}
	
}
