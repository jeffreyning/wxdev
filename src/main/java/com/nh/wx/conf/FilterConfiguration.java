/**
 * 
 */
package com.nh.wx.conf;

import javax.annotation.Resource;

import com.nh.wx.filter.WxOpenIdFilter;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;



/**
 * 
 * @Description:
 * @Author: ninghao
 * @Date: 2019年5月13日
 * @Version: 1.0
 */
@Configuration
public class FilterConfiguration {
	
	@Resource
	Environment env;
	
    @Bean
    public FilterRegistrationBean wxOpenIdFilterRegistration() {
        FilterRegistrationBean registration = new FilterRegistrationBean();
        registration.setFilter(new WxOpenIdFilter());//添加过滤器
        registration.addUrlPatterns("/*");//设置过滤路径，/*所有路径
        
        String appId=env.getProperty("wx.appid");
        registration.addInitParameter("appId", appId);//添加默认参数
        String appSecret=env.getProperty("wx.appsecret");
        registration.addInitParameter("appSecret", appSecret);//添加默认参数
        
        String htmlPattern=env.getProperty("wx.htmlPattern");
        if(htmlPattern!=null){
        	registration.addInitParameter("htmlPattern", htmlPattern);//添加默认参数
        }
        String excludePattern=env.getProperty("wx.excludePattern");
        if(excludePattern!=null){
        	registration.addInitParameter("excludePattern", excludePattern);//添加默认参数
        }
        
        registration.setName("WxOpenIdFilter");//设置优先级
        registration.setOrder(1);//设置优先级
        return registration;
    }

}
