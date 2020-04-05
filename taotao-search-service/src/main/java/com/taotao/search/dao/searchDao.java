package com.taotao.search.dao;


import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.apache.solr.common.SolrInputDocument;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.taotao.common.pojo.SearchItem;
import com.taotao.common.pojo.SearchResult;
import com.taotao.common.pojo.TaotaoResult;
import com.taotao.search.mapper.SearchItemMapper;

@Repository
public class searchDao {
	@Autowired
	private SolrServer solrServer;
	
	@Autowired
	private SearchItemMapper itemMapper;
	
	public SearchResult search(SolrQuery query) throws Exception {
	//访问索引库的类。定义一些通用的数据访问方法。
	//业务逻辑就是查询索引库。
	//参数：SolrQuery对象
	//业务逻辑：
	//1、根据Query对象进行查询。
	QueryResponse response = solrServer.query(query);
	//2.查询响应对象获取结果(商品的列表)
	SolrDocumentList documentList = response.getResults();
	//将结果赋值到SearchResult中去
	//创建一个list<Searchitem>
	List<SearchItem> searchitemList = new ArrayList<>();
	for (SolrDocument solrDocument : documentList) {
		//创建一个Searchitem对象
		SearchItem item = new SearchItem();
		item.setId(Long.parseLong(solrDocument.get("id").toString()));
		item.setCategory_name((String) solrDocument.get("item_category_name"));
		item.setImage((String) solrDocument.get("item_image"));
		item.setPrice((long) solrDocument.get("item_price"));
		item.setSell_point((String) solrDocument.get("item_sell_point"));
		//取高亮
		Map<String, Map<String, List<String>>> highlighting = response.getHighlighting();
		List<String> list = highlighting.get(solrDocument.get("id")).get("item_title");
		String itemTitle = "";
		//有高亮显示的内容时。
		if (list != null && list.size() > 0) {
			itemTitle = list.get(0);
		} else {
			itemTitle = (String) solrDocument.get("item_title");
		}
		//将高亮的值进行设置
		item.setTitle(itemTitle);
		//将商品添加到集合中
		searchitemList.add(item);
	}
	//3、返回查询结果。包括List<SearchItem>、查询结果的总记录数。
	//的到总的记录数为
	
	SearchResult result=new SearchResult();
	//设置查询到的商品记录数
	result.setItemlist(searchitemList);
	//设置总的记录数
	result.setRecordCount(documentList.getNumFound());
	//返回结果集
	return result;
	}
	
	//根据id 将数据导入索引库中
	
	public TaotaoResult updateItemById(Long itemId) throws Exception{
		//1.数据库查询数据
		SearchItem searchItem = itemMapper.getsearchItem(itemId);
		//2.创建solrinputdocument
		SolrInputDocument  document=new SolrInputDocument();
		//3. 文档添加数据
		document.addField("id", searchItem.getId());
		document.addField("item_title", searchItem.getTitle());
		document.addField("item_sell_point", searchItem.getSell_point());
		document.addField("item_price", searchItem.getPrice());
		document.addField("item_image", searchItem.getImage());
		document.addField("item_category_name", searchItem.getCategory_name());
		document.addField("item_desc", searchItem.getItem_desc());
		//4.添加大索引库中
		solrServer.add(document);
		//提交 数据
		solrServer.commit();
		//返回数据
		return TaotaoResult.ok();
	}
	
	
}
