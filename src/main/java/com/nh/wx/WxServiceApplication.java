package com.nh.wx;


import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import com.nh.wx.util.ParamHolder;
import com.nh.wx.util.WxSignUtil;
import com.nh.wx.util.WxUtil;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;



/**
 * 
 * @author ninghao
 *
 */
@RestController
@SpringBootApplication
@EnableAutoConfiguration
public class WxServiceApplication {
	
	@Bean
	public ParamHolder paramHolder() {
		return new ParamHolder();
	}
	
/*	@PostConstruct
	public void init(){
		WxUtil.appid=appid;
		WxUtil.secret=secret;
	}*/
	
	public static void main(String[] args) {
		
		new SpringApplicationBuilder(WxServiceApplication.class).web(true).run(args);
	}

	/**
	 * �?通微信时验证
	 * 
	 * @param
	 * @return
	 */
	@RequestMapping(value = "/confirm", method = RequestMethod.GET)
	@ResponseBody
	public String confirm(HttpServletRequest request) {
		String echostr = request.getParameter("echostr");
		if (echostr == null) {
			echostr = "";
		}
		System.out.println("echostr=" + echostr);
		return echostr;
	}


	/**
	 * 前台微信js赋权时调�?
	 * 
	 * @param
	 * @return
	 */
	@RequestMapping(value = "/createSign")
	@ResponseBody	
	public Map createSign(HttpServletRequest request) throws Exception{

		String url=request.getParameter("url");
		String token= WxUtil.getAccessToken();
		String ticket=WxUtil.getTicket(token);
		Map signMap= WxSignUtil.sign(ticket, url);
		return signMap;
	}

}
