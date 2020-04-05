package com.TestSolr;

import java.util.List;
import java.util.Map;

import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.impl.HttpSolrServer;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.apache.solr.common.SolrInputDocument;
import org.junit.Test;

public class TestSolr {

	/**
	 * 文件添加数据操作
	 * @throws Exception
	 */
	//添加数据到索引库中
	@Test
	public  void addDocument() throws Exception{
	//第一步：把solrJ的jar包添加到工程中。
	//第二步：创建一个SolrServer，使用HttpSolrServer创建对象。	
	SolrServer solrServer=new HttpSolrServer("http://192.168.1.104:8080/solr");
	//第三步：创建一个文档对象SolrInputDocument对象。	
	SolrInputDocument document =new SolrInputDocument();
	//第四步：向文档中添加域。必须有id域，域的名称必须在schema.xml中定义。
	document.addField("id", "test001");
	document.addField("item_title", "测试商品");
	document.addField("item_price", "199");
	//第五步：把文档添加到索引库中。
	solrServer.add(document);
	//第六步：提交。
	solrServer.commit();
	}
	
	/**
	 * 根据id删除文档
	 */
	@Test
	public void deleteDocumentById() throws Exception {
	//	第一步：创建一个SolrServer对象。
		SolrServer solrServer=new HttpSolrServer("http://192.168.1.104:8080/solr");
	//  第二步：调用SolrServer对象的根据id删除的方法。
		solrServer.deleteById("1");
	//	第三步：提交。
		solrServer.commit();
	}
	
	@Test
	public void deleteDocumentByQuery() throws Exception {
		//第一步：创建一个SolrServer对象。
		SolrServer solrServer = new HttpSolrServer("http://192.168.1.104:8080/solr");
		//第二步：调用SolrServer对象的根据查询内容删除的方法。
		solrServer.deleteByQuery("title:change.me");
		//提交
		solrServer.commit();
	}
	
	/*
	 * 在solrj中进行数据查询操作
	 */
	public void queryDocument() throws Exception{
	//查询步骤：
	//第一步：创建一个SolrServer对象
		SolrServer solrServer=new HttpSolrServer("http://192.168.1.104:8080/solr");
	//第二步：创建一个SolrQuery对象。
		SolrQuery solrQuery=new SolrQuery();
	//第三步：向SolrQuery中添加查询条件、过滤条件。。。
		solrQuery.setQuery("*:*");
	//第四步：执行查询。得到一个Response对象。
		QueryResponse response = solrServer.query(solrQuery);
	//第五步：取查询结果。
		SolrDocumentList solrDocumentList  = response.getResults();
		System.out.println("查询结果的总记录数：" + solrDocumentList .getNumFound());
	//第六步：遍历结果并打印。
		for (SolrDocument solrDocument : solrDocumentList) {
			System.out.println(solrDocument.get("id"));
			System.out.println(solrDocument.get("item_title"));
			System.out.println(solrDocument.get("item_price"));
		}
	}
	
	/**
	 * 有高亮度的显示查询
	 * @throws Exception
	 */
	@Test
	public void queryDocumentWithHighLighting() throws Exception {
		//查询步骤：
		//第一步：创建一个SolrServer对象
			SolrServer solrServer=new HttpSolrServer("http://192.168.1.104:8080/solr");
		//第二步：创建一个SolrQuery对象。
			SolrQuery solrQuery=new SolrQuery();
		//第三步：向SolrQuery中添加查询条件、过滤条件。。。
			solrQuery.setQuery("测试");
			//设置指定的搜索域
			solrQuery.set("df", "item_keywords");
			//开启高亮显示
			solrQuery.setHighlight(true);
			//设置高亮显示的区域
			solrQuery.addHighlightField("item_title");
			solrQuery.setHighlightSimplePre("<em>");
			solrQuery.setHighlightSimplePost("</em>");
		//第四步：执行查询。得到一个Response对象。
			QueryResponse response = solrServer.query(solrQuery);
		//第五步：取查询结果。
			SolrDocumentList solrDocumentList  = response.getResults();
			System.out.println("查询结果的总记录数：" + solrDocumentList .getNumFound());
		//第六步：遍历结果并打印。
			for (SolrDocument solrDocument : solrDocumentList) {
				System.out.println(solrDocument.get("id"));
				//取高亮显示
				Map<String, Map<String, List<String>>> highlighting = response.getHighlighting();
				
				List<String> list = highlighting.get(solrDocument.get("id")).get("item_title");
				//定义个空的字符串显示高亮
				String itemTitle = null;
				if (list != null && list.size() > 0) {
					itemTitle = list.get(0);
				} else {
					itemTitle = (String) solrDocument.get("item_title");
				}
				//输出高亮
				System.out.println(itemTitle);
				System.out.println(solrDocument.get("item_price"));
			}
		}
	
}
