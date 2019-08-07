package com.nh.wx.filter;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.regex.Pattern;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import com.alibaba.fastjson.JSONObject;




/**
 * 处理微信浏览器请求
 * 
 * @author ninghao
 * 
 */

public class WxOpenIdFilter implements Filter {

	private String appId="";
	private String appSecret="";
    private String OPEN_ID = "wxsso";
    
    //微信侧从用户浏览器跳转过来标识
    private static String WX_MARKET = "wxredirectpaom";
    
    private static final int FILTER_NOTHING = 0;
    private static final int FILTER_HTML = 1;

    
    private Pattern htmlPattern=null;
    private Pattern excludePattern=null;

    
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
    	appId=filterConfig.getInitParameter("appId");
    	appSecret=filterConfig.getInitParameter("appSecret");
    	String tempHtmlPattern=filterConfig.getInitParameter("htmlPattern");
    	if(tempHtmlPattern!=null && !"".equals(tempHtmlPattern)){
    		htmlPattern=Pattern.compile(tempHtmlPattern, Pattern.CASE_INSENSITIVE);
    	}
    	String tempExcludePattern=filterConfig.getInitParameter("excludePattern");
    	if(tempExcludePattern!=null && !"".equals(tempExcludePattern)){
    		excludePattern=Pattern.compile(tempExcludePattern, Pattern.CASE_INSENSITIVE); 
    	}
    }
    


    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain)
            throws IOException, ServletException {

        HttpServletRequest request = (HttpServletRequest)servletRequest;
        HttpServletResponse response = (HttpServletResponse)servletResponse;
        String url = getFullURL(request, null);
        System.out.println(url);
        int filterType = getFilterType(request);
        
        if (filterType == FILTER_HTML) {//拦截页面请求
        	System.out.println("this is page");
            String code = request.getParameter("code");
            String state = request.getParameter("state");
            
            String openid = null;
            String unionid = null;
            
            if (WX_MARKET.equals(state)) { //微信侧携带code从用户浏览器跳转过来
            	System.out.println("2. get code to get openid");
                if (code != null && !"authdeny".equals(code)) {

					try {
						openid = getOpenid(code);
					} catch (Throwable e) {
						// TODO Auto-generated catch block
						throw new ServletException(e);
					}

                    if (openid != null) {

                        setCookie(OPEN_ID, openid, 1800, request, response, null, "/");
                        String targetUrl=url;
                        targetUrl=url.replace("&state="+WX_MARKET, "");
                        System.out.println("3. setcookiet and to targetUrl="+targetUrl);
                        response.sendRedirect(targetUrl);
                        return;
                    }else{
                    	throw new ServletException("");
                    }
                }
            } else { //用户浏览器直接跳转
            	
                openid = getCookie(OPEN_ID, request);
                if (openid == null) { //不能从cookie中取出openid
                	System.out.println("1.not cookie to wx page");
                    //用户浏览器跳转到微信侧获取code
                    redirectWxOAuth(response,url);
                }else{//可以从cookie中取出openid，跳转实际页面
                	System.out.println("4.have cookie getin");
                	setCookie(OPEN_ID, openid, 1800, request, response, null, "/");//延长cookie时间
                    filterChain.doFilter(request, response);
                }
            }

        } else {//非页面不拦截
            filterChain.doFilter(request, response);
        }
        
    }

    @Override
    public void destroy() {

    }
    
    
    //判断是否需跳转获取openid，ajax即使设了也取不到openid
    private int getFilterType(HttpServletRequest request) {
        
        String uri = request.getRequestURI();
        
        if (excludePattern != null && excludePattern.matcher(uri).matches()) {
            return FILTER_NOTHING;
        }
        
        if (htmlPattern != null && htmlPattern.matcher(uri).matches()) {
            return FILTER_HTML;
        }
        
        return FILTER_NOTHING;
    }
    
    /**
     * 由用户浏览器跳转到微信侧
     * @param response
     * @param url
     * @throws IOException
     */
    private void redirectWxOAuth(HttpServletResponse response, String url) throws IOException {
        
        StringBuilder oauthUrl = new StringBuilder();
        oauthUrl.append("https://open.weixin.qq.com/connect/oauth2/authorize?");
        oauthUrl.append("appid=").append(appId);
        oauthUrl.append("&redirect_uri=").append(urlEncoder(url));
        oauthUrl.append("&response_type=code");
        oauthUrl.append("&scope=snsapi_base");
        oauthUrl.append("&state=");
        oauthUrl.append(WX_MARKET);
        oauthUrl.append("#wechat_redirect");
        response.sendRedirect(oauthUrl.toString());
    }
    
    private String urlEncoder(String s) {

        try {
            return URLEncoder.encode(s, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            return "";
        }
    }
    
    /**
     * 获得请求的客户端完整URL
     * @param request             HttpServletRequest对象
     * @return  请求的客户端完整URL
     */
    public static String getFullURL(HttpServletRequest request) {

        return getFullURL(request, null);
    }
    
    public static String getFullURL(HttpServletRequest request, String replaceDomain) {
        
        StringBuffer urlBuffer = request.getRequestURL();
        String queryString = request.getQueryString();
        if (queryString != null && queryString.length() > 0) {
            urlBuffer.append("?");
            urlBuffer.append(queryString);
        }
        
        String url = urlBuffer.toString();
        
        if (replaceDomain != null && replaceDomain.length() > 0) {
            int i = url.indexOf("//");
            int j = url.indexOf("/", i + 2);
            url = url.substring(0, i + 2) + replaceDomain + url.substring(j);
            
        } 
        
        return url;
    }
    
    
    /**
     * 获取Cookie
     * 
     * @param key
     *            要获取的Cookie名字
     * @param req
     *            HttpServletRequest对象
     * @return
     */
    public static String getCookie(String key, HttpServletRequest req) {

        if (null == key || key.trim().equals("") || req == null) {
            return null;
        }

        Cookie[] cookies = req.getCookies();

        if (cookies != null) {
            for (int i = 0; i < cookies.length; i++) {
                if (key.equals(cookies[i].getName())) {
                    return cookies[i].getValue();
                }
            }
        }
        return null;
    }  
    
    
    /**
     * 设置Cookie
     * 
     * @param key
     *            要设置的Cookie名字
     * @param value
     *            要设置的Cookie值
     * @param maxAge
     *            设置Cookie的有效期
     * @param res
     *            HttpServletResponse对象
     * @param domain
     *            设置Cookie的domain
     * @param path
     *            设置Cookie的path
     */
    public static void setCookie(String key, String value, int maxAge, HttpServletRequest req, HttpServletResponse res,
            String domain, String path) {

        if (null == key || key.trim().equals("") || res == null) {
            return;
        }

        Cookie cookie = null;
        cookie = getCookieEntity(key, req);
        if (cookie == null) {
            cookie = new Cookie(key, value);
        } else {
            cookie.setValue(value);
        }
        cookie.setMaxAge(maxAge);

        if (domain != null) {
            cookie.setDomain(domain);
        }
        if (path != null) {
            cookie.setPath(path);
        }
        res.addCookie(cookie);
    } 
    
    
    /**
     * 获取 Cookie
     * 
     * @param key
     *            要获取的Cookie的名字
     * @param req
     *            HttpServletRequest对象
     * @return
     */
    public static Cookie getCookieEntity(String key, HttpServletRequest req) {

        if (null == key || key.trim().equals("") || req == null) {
            return null;
        }

        Cookie[] cookies = req.getCookies();

        if (cookies != null) {
            for (int i = 0; i < cookies.length; i++) {
                if (key.equals(cookies[i].getName())) {
                    return cookies[i];
                }
            }
        }
        return null;
    }    
    
    //根据code取openid
    public String getOpenid(String code) throws Throwable {
    	String openId=null;
        //获得httpclient对象
        HttpClient httpclient = new DefaultHttpClient();
        //获得HttpGet对象
        HttpGet httpGet = null;
        String url="https://api.weixin.qq.com/sns/oauth2/access_token?appid="+appId+"&secret="+appSecret+"&code="+code+"&grant_type=authorization_code";
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
        }
        String objStr=sb.toString();
        
        JSONObject retObj=JSONObject.parseObject(objStr);
        openId=retObj.getString("openid");

        return openId;
    }    
}
