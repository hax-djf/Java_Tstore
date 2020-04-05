package com.taotao.item.controller;

import org.springframework.beans.factory.annotation.Autowired;


import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import com.taotao.item.pojo.Item;
import com.taotao.pojo.TbItem;
import com.taotao.pojo.TbItemDesc;
import com.taotao.service.ItemListService;

@Controller
public class ItemController {
	
	@Autowired
	private ItemListService ItemService;
	
	@RequestMapping("/item/{itemId}")
	public String showItemInfo(@PathVariable Long itemId,Model model) {
		//根据商品ip 查询商品信息
		TbItem tbItem = ItemService.getItemById(itemId);
		//将商品信息装成item
		Item itemone =new Item(tbItem); 
		//根据商品id 查询商品的详情信息
		TbItemDesc descById = ItemService.getItemDescById(itemId);
		
		//将数据设置到页面中
		model.addAttribute("item", itemone);
		model.addAttribute("itemDesc", descById);
		//返回视图页面
		return "item";
		
	}
}
