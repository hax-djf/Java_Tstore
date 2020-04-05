package com.taotao.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.taotao.common.pojo.EasyUITreeNode;
import com.taotao.service.ItemCatService;

@Controller
public class itemCatController {
	@Autowired
	private ItemCatService catService;
	@RequestMapping("/item/cat/list")
	@ResponseBody
	public List<EasyUITreeNode> getItemCatListByParentId(@RequestParam(value ="id",defaultValue = "0") Long parentId){
		//将数据以json格式进行返回
		return this.catService.getItemCatListByParentId(parentId);
	}
}
