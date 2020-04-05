package com.taotao.controller;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.taotao.common.pojo.TaotaoResult;
import com.taotao.search.service.SearchItemService;

@Controller
public class SearchItemController {
	@Autowired
	private SearchItemService itemService;
	@RequestMapping("/index/importall")
	public TaotaoResult importAllItems() {
		//进行数据导入solr库中
		TaotaoResult result=null;
		try {
			result = itemService.importAllItems();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return result;
	}
	
}
