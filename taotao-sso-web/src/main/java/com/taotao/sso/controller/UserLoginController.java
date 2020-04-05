package com.taotao.sso.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.taotao.common.pojo.TaotaoResult;
import com.taotao.common.util.CookieUtils;
import com.taotao.common.util.JsonUtils;
import com.taotao.sso.service.UserLoginService;

@Controller
public class UserLoginController {
	
	@Autowired
	private UserLoginService loginService;
	
	@Value("${TT_TOKEN_KEY}")
	private String TT_TOKEN_KEY;//根据的是cookie的进行跨域效果这样来实现共享token数据
	@RequestMapping(value = "/user/login",method = RequestMethod.POST)
	@ResponseBody
	public TaotaoResult login(HttpServletRequest request,HttpServletResponse response,String username,String password) {
		//1.引入服务
		//2.注入服务
		//3.调用服务
		TaotaoResult result = loginService.login(username, password);
		//4.需要设置token到cookie中 可以使用 工具类  cookie需要跨域
		if(result.getStatus()==200) {
			//如果状态码为200的话 表示设置校验成功 引入工具包
			CookieUtils.setCookie(request, response,TT_TOKEN_KEY, result.getData().toString());
		}
		//返回数据
		return result;
	}
	
	//进入其他操作的时候进行是否登陆的校验  每个域中都可以拿到这个token数据,进行登录校验的时候,会将cookie带过来 
	@RequestMapping(value="/user/token/{token}",method = RequestMethod.GET,produces=MediaType.APPLICATION_JSON_UTF8_VALUE)
	@ResponseBody
	public String getUserByToken(@PathVariable String token,String callback) {
		//先进行判断是否是jsonp的请求
		if(StringUtils.isNotBlank(callback)) {
			//如果是jsonp 需要拼接 类似于fun({id:1});
			TaotaoResult result = loginService.getUserByToken(token);
			//进行拼接的操作
			String jsonpstr = callback+"("+JsonUtils.objectToJson(result)+")";
			//返回json数据
			return jsonpstr;
		}
		//如果不是json数据的话
		//服务的调用
		TaotaoResult result = loginService.getUserByToken(token);
		return JsonUtils.objectToJson(result);
		
	}
	
}
