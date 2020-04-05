package com.taotao.search.service;

import com.taotao.common.pojo.SearchResult;
import com.taotao.common.pojo.TaotaoResult;

public interface SearchItemService {

	//将查询到数据库中数据导入到索引库
	public TaotaoResult importAllItems() throws Exception;
	//根据查询条件返回查询的数据
	public SearchResult search(String queryString,Integer page,Integer rows) throws Exception;
	//根据id查询商品将商品数据导入索引库中
	public TaotaoResult updateItemById(Long itemId) throws Exception;
}
