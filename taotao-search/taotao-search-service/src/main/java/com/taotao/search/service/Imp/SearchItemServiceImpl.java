package com.taotao.search.service.Imp;

import java.util.List;



import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.common.SolrInputDocument;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.taotao.common.pojo.SearchItem;
import com.taotao.common.pojo.SearchResult;
import com.taotao.common.pojo.TaotaoResult;
import com.taotao.search.dao.searchDao;
import com.taotao.search.mapper.SearchItemMapper;
import com.taotao.search.service.SearchItemService;

@Service(value = "searchItemServiceImpl")
public class SearchItemServiceImpl implements SearchItemService {
	@Autowired
	private SearchItemMapper searchitemmapper;
	//http生成的创建的solrserver对象
	@Autowired
	private SolrServer solrServer;
	
	@Autowired
	private searchDao searchdao;
	
	//从数据库中查询数据导入到solr库中
	@Override
	public TaotaoResult importAllItems() throws Exception {
		//1、查询所有商品数据。
		List<SearchItem> serrchItemList = searchitemmapper.getSerrchItemList();
		//2、创建一个SolrServer对象。
		//3、为每个商品创建一个SolrInputDocument对象。
		for (SearchItem searchItem : serrchItemList) {
		//为每一商品创建一个document文档
		SolrInputDocument document=new SolrInputDocument();
		//为每一文档添加域操作
		// 4、为文档添加域
		document.addField("id", searchItem.getId());
		document.addField("item_title", searchItem.getTitle());
		document.addField("item_sell_point", searchItem.getSell_point());
		document.addField("item_price", searchItem.getPrice());
		document.addField("item_image", searchItem.getImage());
		document.addField("item_category_name", searchItem.getCategory_name());
		document.addField("item_desc", searchItem.getItem_desc());
		//5、向索引库中添加文档。
		solrServer.add(document);
		}
		//添加文档操作
		solrServer.commit();
		
		//6、返回TaotaoResult。
		return TaotaoResult.ok();
	}
	
	//根据查询条件进行数据的查询
	//使用solrQuery进行条件的设置
	@Override
	public SearchResult search(String queryString, Integer page, Integer rows) throws Exception {
		// 1、创建一个SolrQuery对象。
		SolrQuery query = new SolrQuery();
		// 2、设置查询条件
		query.setQuery(queryString);
		// 3、设置分页条件
		query.setStart((page - 1) * rows);
		query.setRows(rows);
		// 4、需要指定默认搜索域。
		query.set("df", "item_title");
		// 5、设置高亮
		query.setHighlight(true);
		query.addHighlightField("item_title");
		query.setHighlightSimplePre("<em style=\"color:red\">");
		query.setHighlightSimplePost("</em>");
		// 6、执行查询，调用SearchDao。得到SearchResult
		SearchResult result = searchdao.search(query);
		// 7、需要计算总页数。
		long recordCount = result.getRecordCount();
		long pageCount = recordCount/rows;
		if (recordCount % rows > 0) {
			pageCount++;
		}
		result.setPageCount(pageCount);
		// 8、返回SearchResult
		return result;

	}
	
	//根据id查询商品导入索引库中
	@Override
	public TaotaoResult updateItemById(Long itemId) throws Exception {
		TaotaoResult result = searchdao.updateItemById(itemId);
		return result;
	}
	

}
