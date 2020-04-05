package com.taotao.controller;

import javax.swing.text.html.ParagraphView;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.taotao.common.pojo.TaotaoResult;
import com.taotao.common.util.JsonUtils;
import com.taotao.pojo.TbItem;
import com.taotao.service.ItemListService;

@Controller
public class PageController {
	
	@Autowired
	private ItemListService service;
	/**
	 * 首页面的的展示
	 * @return
	 */
	@RequestMapping("/")
	public String showIndex() {
		return "index";
	}
	/**
	 * 数据页面的订单显示 使用的是resful的风格
	 * @param page
	 * @return
	 */
	@RequestMapping("/{page}")
	public String showItemList(@PathVariable String page) {
		
		return page;
	}
	
	@RequestMapping("/rest/page/{item-edit}")
	public String  showPage_deit(@PathVariable(value = "item-edit")Long itemId,Model model,String item_edit) {
		//返回页面做加载数据处理
		TbItem tbItem = service.getItemById(itemId);
		//回显数据
		model.addAttribute("itemList", tbItem);
		return item_edit;
	} 
}
