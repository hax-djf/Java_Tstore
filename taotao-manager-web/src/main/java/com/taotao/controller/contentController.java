package com.taotao.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.taotao.common.EasyUIDataGridResult;
import com.taotao.common.pojo.TaotaoResult;
import com.taotao.content.service.contentService;
import com.taotao.pojo.TbContent;

@Controller
public class contentController {
	@Autowired
	private contentService contentService;
	
	//分页查询分页内容
	@RequestMapping(value="/content/query/list",method = RequestMethod.GET)
	@ResponseBody
	public EasyUIDataGridResult getcontentByCategory(long categoryId,int page, int rows) {
		System.out.println(categoryId);
		System.out.println(page);
		System.out.println(rows);
		//进行服务服务调用
		return contentService.getcontentByCategory(categoryId, page, rows);
	}
	
	//新增分页内容信息
	@RequestMapping(value = "/content/save",method = RequestMethod.POST)
	@ResponseBody
	public  TaotaoResult saveContent(TbContent content) {
		//图片上传操作已经做了直接访问itemlist里面有一个图片处理操作
		return this.contentService.saveContent(content);
	}
	
	//根据id编辑内容
	@RequestMapping(value = "/rest/content/edit",method = RequestMethod.POST)
	@ResponseBody
	public TaotaoResult updateContent(TbContent tbContent) {
		return this.contentService.updateContent(tbContent);
	}
	
	//根据ids字符串删除数据
	@RequestMapping(value = "/content/delete",method = RequestMethod.POST)
	@ResponseBody
	public TaotaoResult deleteContent(String  ids) {
		return this.contentService.deleteContent(ids);
	}
	
}
