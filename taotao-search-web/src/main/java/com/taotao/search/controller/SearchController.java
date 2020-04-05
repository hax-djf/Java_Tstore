package com.taotao.search.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.taotao.common.pojo.SearchItem;
import com.taotao.common.pojo.SearchResult;
import com.taotao.search.service.SearchItemService;

@Controller
public class SearchController {
	@Autowired
	private SearchItemService searchItemService;
	
	//每一页显示的页数
	@Value("${ITEM_ROWS}")
	private Integer ITEM_ROWS; 
	
	@RequestMapping("/search")
	public String search(@RequestParam(value = "q")String stringquery,@RequestParam(defaultValue = "1")Integer page,Model model) throws Exception {
		//注入
		//对获取到的querystring 数据进行 编码处理get 请求
		stringquery=new String(stringquery.getBytes("iso8859-1"), "utf-8");
		//在索引库中进行数据的查询
		SearchResult searchresult = searchItemService.search(stringquery,page,ITEM_ROWS);
		//将数据设置到对应的页面中
		//传递给页面
		model.addAttribute("query", stringquery);
		model.addAttribute("totalPages", searchresult.getPageCount());
		model.addAttribute("itemList", searchresult.getItemlist());
		//测试
		List<SearchItem> itemlist = searchresult.getItemlist();
		 SearchItem searchItem = itemlist.get(0);
		System.err.println("商品的图片"+searchItem.getImage());
		String[] itemImage = searchItem.getSearchItemImage();
		System.out.println("商品"+searchItem);
		System.out.println("商品的第一张图片"+itemImage[0]);
		//===================
		model.addAttribute("page", page);
		//返回逻辑视图
		return "search";
	}
}
