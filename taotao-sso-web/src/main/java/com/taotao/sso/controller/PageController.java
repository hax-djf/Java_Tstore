package com.taotao.sso.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class PageController {
	//登录注册页面的展示
	/**
	 * 
	 * @param page 跳转的页面
	 * @param redirect 登录以后重定向的地址
	 * @param model 设置阈值
	 * @return 返回view视图
	 */
	@RequestMapping("/page/{page}")
	public String Showpage(@PathVariable String page,String redirect,Model model){
		System.out.println(redirect);
		model.addAttribute("redirect", redirect);
		return page;
	}
}
