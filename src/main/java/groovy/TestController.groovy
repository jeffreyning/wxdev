package groovy;

import javax.servlet.http.Cookie
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse

import org.springframework.web.bind.annotation.CrossOrigin;

import com.nh.micro.controller.MicroUrlMapping
import com.nh.micro.template.MicroServiceTemplateSupport

@CrossOrigin
@MicroUrlMapping(name = "/test")
class TestController extends MicroServiceTemplateSupport  {  
	private String getOpenId(HttpServletRequest httpRequest){
		Cookie[] cookies = httpRequest.getCookies();
		String openId="";
		if(cookies!=null){
			for (Cookie c : cookies) {
				if (c.getName().equals("wxsso")) {
					openId = c.getValue();
					System.out.println("openid="+openId);
				}
			}
		}
		return openId;
	}
	
	@MicroUrlMapping(name = "/saveInfo")
	public void saveInfo(HttpServletRequest httpRequest, HttpServletResponse httpResponse) {
		String phone=httpRequest.getParameter("phone");
		String openId=getOpenId(httpRequest);
		Map paramMap=new HashMap();
		paramMap.put("phone", phone);
		paramMap.put("openid",openId);
		paramMap.put("create_time","now()");
		createInfoService(paramMap, "tojp");
		Map retMap=new HashMap();
		retMap.put("status", "0");
		httpResponse.getOutputStream().write(retMap.toString().getBytes("UTF-8"));
		return ;
	}

	@MicroUrlMapping(name = "/echo")
	public void echo(HttpServletRequest httpRequest, HttpServletResponse httpResponse) {
		System.out.println("this is controller proxy");
		Map retMap=new HashMap();
		retMap.put("status", "0");
		httpResponse.getOutputStream().write(retMap.toString().getBytes("UTF-8"));
		return ;
	}

	@MicroUrlMapping(name = "/showopenid")
	public void showOpenId(HttpServletRequest httpRequest, HttpServletResponse httpResponse) {
		System.out.println("this is show openid");
		Cookie[] cookies = httpRequest.getCookies();
		String openId="";
		System.out.println("Cookie长度："+cookies.length);
		for (Cookie c : cookies) {
			System.out.print(c.getName()+"...");
			if (c.getName().equals("wxsso")) {
				openId = c.getValue();
				System.out.println("openid="+openId);
			}
		}

		Map retMap=new HashMap();
		retMap.put("status", "0");
		retMap.put("openId", openId);
		httpResponse.getOutputStream().write(retMap.toString().getBytes("UTF-8"));
		return ;
	}
}
