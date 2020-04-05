package com.taotao.search.mapper;

import java.util.List;

import com.taotao.common.pojo.SearchItem;

public interface SearchItemMapper {
	//从数据库中获取到search查询数据
	public  List<SearchItem> getSerrchItemList();
	
	//根据id到数据库中查询数据
	public SearchItem getsearchItem(Long itemid);
}
