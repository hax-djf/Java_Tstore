package com.taotao.order.Interceptor;

import javax.servlet.http.HttpServletRequest;


import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import com.taotao.common.pojo.TaotaoResult;
import com.taotao.common.util.CookieUtils;
import com.taotao.sso.service.UserLoginService;

/**
 * 进行用户的登录的拦截操作 (用户的是否登录的校验)
 * @author Administrator
 */
public class LoginInterceptor implements HandlerInterceptor {
	@Value("${TT_TOKEN_KEY}")
	private String TT_TOKEN_KEY;
	
	@Value("${SSO_URL}")
	private String SSO_URL;
	
	//导入loginService 服务
	@Autowired
	private UserLoginService loginService;
	
	//在进入目标方法之前执行
	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws Exception {

//		//用户的身份认证在此验证
//		//1.取cookie中的token 
//		//1.从cookies中获取用户的token 
//		String token = CookieUtils.getCookieValue(request, TT_TOKEN_KEY);
//		//2.判断token是否存在，
//		if(StringUtils.isEmpty(token)) {
//			//如果不为空 表示没有登录 
//			//没有登录的话,就重定向到登录页面
//			//request.getRequestURL().toString()：就是访问的URL localhost:8092/order/order-cart.html
//			//表示登录以后在跳转到订单的显示页面
//			response.sendRedirect(SSO_URL+"/page/login?redirect="+request.getRequestURL().toString());
//			return  false;
//		}
//		//用户存在话
//		//调用服务查看
//		TaotaoResult result = loginService.getUserByToken(token);
//		if(result.getStatus()!=200) {
//			//表示已经过期了
//			//重定向到登录的页面
//			response.sendRedirect(SSO_URL+"/page/login?redirect="+request.getRequestURL().toString());
//			return false;
//		}
//		//表示登录成功了 
//		//设置用户信息到request中 ，目标方法的request就可以获取用户的信息
//		request.setAttribute("USER_INFO", result.getData());
//		return true;
		
		//用户的身份认证在此验证
		
		//1.取cookie中的token 
		//1.从cookie中获取用户的token 
		String token = CookieUtils.getCookieValue(request, TT_TOKEN_KEY);
		//2.判断token是否存在，
		if(StringUtils.isEmpty(token)){
			//3.如果不存在，说明没登录   ---》重定向到登录的页面
			//request.getRequestURL().toString()：就是访问的URL localhost:8092/order/order-cart.html
			response.sendRedirect(SSO_URL+"/page/login?redirect="+request.getRequestURL().toString());
			return false;
		}
		//4.如果token存在，调用SSO的服务 查询用户的信息（看是否用户已经过期）
		TaotaoResult result = loginService.getUserByToken(token);
		if(result.getStatus()!=200){
			//5.用户已经过期  --》重定向到登录的页面
			response.sendRedirect(SSO_URL+"/page/login?redirect="+request.getRequestURL().toString());
			return false;
		}
		//6.用户没过期（说明登录了）--》放行
		//设置用户信息到request中 ，目标方法的request就可以获取用户的信息
		request.setAttribute("USER_INFO", result.getData());
		System.out.println(result.getData());
		return true;
	}
	
	//在进入目标方法之后，在返回modelandview之前执行
	//共用变量的一些设置。
	@Override
	public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
			ModelAndView modelAndView) throws Exception {
		// TODO Auto-generated method stub

	}
	//返回modelandview之后，渲染到页面之前
	//异常处理 ，清理工作
	@Override
	public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex)
			throws Exception {
		// TODO Auto-generated method stub

	}

}
