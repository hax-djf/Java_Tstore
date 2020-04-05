package com.taotao.controller;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.taotao.service.TestService;

@Controller
public class TestController {
	@Autowired
	private TestService testService;
	//以json数据的格式进行数据返回
	@RequestMapping("/test/queryNow")
	@ResponseBody
	public String queryNow() {
		System.out.println("tomcat运行到这里了");
		return testService.queryNow();
	}
}
