package com.nh.wx.util;



import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.UUID;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import com.alibaba.fastjson.JSONObject;



public class WxUtil {
public static Map tokenHolder=new HashMap();
public static Map ticketHolder=new HashMap();


public static String getNowStr(){
	Date date=new Date();
	SimpleDateFormat sdf=new SimpleDateFormat("yyyyMMddHH");
	String formatStr=sdf.format(date);
	return formatStr;
}

/**
 * 获取accessToken
 * 
 * @param
 * @return
 */

public static String getAccessToken() throws Exception{
	
	String formatStr=getNowStr();
	String temp=(String)tokenHolder.get(formatStr);
	if(temp!=null && !"".equals(temp)){
		return temp;
	}
	
    HttpClient httpclient = new DefaultHttpClient();
    //获得HttpGet对象
    HttpGet httpGet = null;

    String url="https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&appid="+ParamHolder.appId+"&secret="+ParamHolder.appSecret;
    httpGet = new HttpGet(url);
    //发送请求
    HttpResponse response = httpclient.execute(httpGet);
    //输出返回值
    InputStream is = response.getEntity().getContent();
    BufferedReader br = new BufferedReader(new InputStreamReader(is));
    StringBuilder sb=new StringBuilder("");
    String line = "";
    while((line = br.readLine())!=null){
    	sb.append(line);
        System.out.println(line);
    }
    String objStr=sb.toString();
    
    JSONObject retObj=JSONObject.parseObject(objStr);
    String accessToken=retObj.getString("access_token");	
    tokenHolder.put(formatStr, accessToken);
    return accessToken;
}

/**
 * 前端js赋权时需要获取ticket
 * 
 * @param
 * @return
 */
public static String getTicket(String token) throws Exception{
	String formatStr=getNowStr();
	String temp=(String)ticketHolder.get(formatStr);
	if(temp!=null && !"".equals(temp)){
		return temp;
	}	
    HttpClient httpclient = new DefaultHttpClient();
    //获得HttpGet对象
    HttpGet httpGet = null;

    String url="https://api.weixin.qq.com/cgi-bin/ticket/getticket?access_token="+token+"&type=jsapi";
    httpGet = new HttpGet(url);
    //发送请求
    HttpResponse response = httpclient.execute(httpGet);
    //输出返回值
    InputStream is = response.getEntity().getContent();
    BufferedReader br = new BufferedReader(new InputStreamReader(is));
    StringBuilder sb=new StringBuilder("");
    String line = "";
    while((line = br.readLine())!=null){
    	sb.append(line);
        System.out.println(line);
    }
    String objStr=sb.toString();
    
    JSONObject retObj=JSONObject.parseObject(objStr);
    String ticket=retObj.getString("ticket");	
    ticketHolder.put(formatStr, ticket);
    return ticket;
}

/**
 * 组装前端js赋权时需要的签名串
 * 
 * @param
 * @return
 */
/*public static Map getSign(String url) throws Exception{
	String token=getAccessToken();
	String ticket=getTicket(token);
	Map paramMap=new HashMap();
	String uuid=UUID.randomUUID().toString();
	Long stamp=(new Date()).getTime()/1000L;
	paramMap.put("jsapi_ticket", ticket);
	paramMap.put("noncestr", uuid);
	paramMap.put("timestamp", stamp.toString());
	paramMap.put("url", url);
	String sign=Sha1.SHA1(paramMap);
	Map retMap=new HashMap();
	retMap.put("noncestr", uuid);
	retMap.put("timestamp", stamp.toString());
	retMap.put("url", url);
	retMap.put("signature", sign);
	System.out.println(retMap);
	return retMap;

}*/



/**
 * 通过前台传来的code，从微信后台获取openid
 * 
 * @param
 * @return
 */
public static String getOpenid(String code) throws Throwable {
	String openId=null;
    //获得httpclient对象
    HttpClient httpclient = new DefaultHttpClient();
    //获得HttpGet对象
    HttpGet httpGet = null;
    String url="https://api.weixin.qq.com/sns/oauth2/access_token?appid="+ParamHolder.appId+"&secret="+ParamHolder.appSecret+"&code="+code+"&grant_type=authorization_code";
    httpGet = new HttpGet(url);
    //发送请求
    HttpResponse response = httpclient.execute(httpGet);
    //输出返回值
    InputStream is = response.getEntity().getContent();
    BufferedReader br = new BufferedReader(new InputStreamReader(is));
    StringBuilder sb=new StringBuilder("");
    String line = "";
    while((line = br.readLine())!=null){
    	sb.append(line);
        System.out.println(line);
    }
    String objStr=sb.toString();
    
    JSONObject retObj=JSONObject.parseObject(objStr);
    openId=retObj.getString("openid");

    return openId;
}

}
