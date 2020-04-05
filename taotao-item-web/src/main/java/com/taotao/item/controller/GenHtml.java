package com.taotao.item.controller;

import java.io.File;
import java.io.FileWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfigurer;
import freemarker.template.Configuration;
import freemarker.template.Template;

/*
 * 页面静态化一个简单的案例测试
 */
@Controller
public class GenHtml {
	@Autowired
	private FreeMarkerConfigurer Config;
	
	@RequestMapping("/genHtml")
	@ResponseBody
	public String genHtml() throws Exception{
		//需要创建模板文件和编写测试类：
		//使用步骤：
		//生成静态页面
		//1.根据config  获取configuration对象
		Configuration configuration = Config.getConfiguration();
		//2.设置模板文件 加载模板文件 /WEB-INF/ftl/相对路径
		Template template = configuration.getTemplate("template.ftl");
//		第三步：设置模板文件使用的字符集。一般就是utf-8.
//		//3.创建数据集  --》从数据库中获取
		Map model = new HashMap<>();
		//4.为模板添加数据
		model.put("springtestkey", "hello");
		//4.创建writer  创建文件输出流 将数据写到指定的文件中
		Writer writer = new FileWriter(new File("d:/Test/springtestfreemarker.html"));
		//5.调用方法输出
		template.process(model, writer);
		//6.关闭流
		writer.close();
		return "ok";
	}
}
