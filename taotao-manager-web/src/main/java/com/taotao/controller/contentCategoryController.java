package com.taotao.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.taotao.common.pojo.EasyUITreeNode;
import com.taotao.common.pojo.TaotaoResult;
import com.taotao.content.service.contentCategoryService;

@Controller
@RequestMapping("/content/category") 
public class contentCategoryController {
	@Autowired
	private contentCategoryService categoryService;
	
	//select
	@RequestMapping(value = "/list",method = RequestMethod.GET)
	@ResponseBody
	public List<EasyUITreeNode> getContentCategoryList(@RequestParam(value = "id",defaultValue = "0") Long parentId) {
		System.out.println(parentId);
		//注入数据返回json格式
		List<EasyUITreeNode> contentCategoryList = this.categoryService.getContentCategoryList(parentId);
	
		return contentCategoryList;
	}
	//create
	@RequestMapping("/create")
	@ResponseBody
	public TaotaoResult addContentCategory(long parentId, String name) {
		//返回json数据格式
		return this.categoryService.addContentCategory(parentId, name);
	}
	//update
	@RequestMapping("/update")
	@ResponseBody
	public TaotaoResult updateContebtCategory(long id, String name) {
		return this.categoryService.updateContebtCategory(id, name);
	}
	//delete
	@RequestMapping("/delete")
	@ResponseBody
	public TaotaoResult deleteContentCategory(long id) {
		return this.categoryService.deleteContentCategory(id);
	}
	
}
