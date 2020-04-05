package com.taotao.search.exception;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.ModelAndView;

/**
 * 全局异常处理器
 * @author Administrator
 *
 */
public class ClobalExceptionlover implements HandlerExceptionResolver {

	@Override
	public ModelAndView resolveException(HttpServletRequest request, HttpServletResponse response, Object handler,
			Exception ex) {
		//1.日志写入到日志文件中  这里打印
		System.out.println(ex.getMessage());
		ex.printStackTrace();
		//2.及时的通知开发人员  发短信 发邮件  （通过第三方的接口发）
		System.out.println("发短信");
		//3.给用户一个友好的提示 ：您的网络有异常，请重试。
		ModelAndView modelAndView=new ModelAndView();
		//设置转发的页面
		modelAndView.setViewName("error/exception");
		modelAndView.addObject("msg", "您的网络有误,稍后重试");
		return modelAndView;
	}
	
}
