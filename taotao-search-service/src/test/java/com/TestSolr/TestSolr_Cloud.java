package com.TestSolr;

import org.apache.solr.client.solrj.impl.CloudSolrServer;

import org.apache.solr.common.SolrInputDocument;
import org.junit.Test;

public class TestSolr_Cloud {
	//@Test
	public void TestaddDocuemnt() throws Exception {
	//第一步：把solrJ相关的jar包添加到工程中。
	//第二步：创建一个SolrServer对象，需要使用CloudSolrServer子类。构造方法的参数是zookeeper的地址列表。
	CloudSolrServer solrServer=new CloudSolrServer("192.168.1.104:2181,192.168.1.104:2182,192.168.1.104:2183");	
	//第三步：需要设置DefaultCollection属性。
	solrServer.setDefaultCollection("collection2");
	//第四步：创建一SolrInputDocument对象。输入流对象
	SolrInputDocument inputDocument=new SolrInputDocument();
	//第五步：向文档对象中添加域
	inputDocument.addField("item_title", "测试商品");
	inputDocument.addField("item_price", "100");
	inputDocument.addField("id", "test01");
	//第六步：把文档对象写入索引库。
	solrServer.add(inputDocument);
	//第七步：提交。
	solrServer.commit();	
	}
	
	
	@Test
	public void testAdd() throws Exception{
		//1.创建solrserver   集群的实现类
		//指定zookeeper集群的节点列表字符串
		CloudSolrServer cloudSolrServer = new CloudSolrServer("192.168.1.104:2181,192.168.1.104:2182,192.168.1.104:2183");
		//2.设置默认的搜索的collection  默认的索引库（不是core所对应的，是指整个collection索引集合）
		cloudSolrServer.setDefaultCollection("collection2");
		//3.创建solrinputdocumenet对象
		SolrInputDocument document = new SolrInputDocument();
		//4.添加域到文档
		document.addField("id", "testcloudid");
		document.addField("item_title", "今天鸟语花香，容易睡觉");
		//5.将文档提交到索引库中
		cloudSolrServer.add(document);
		//6.提交
		cloudSolrServer.commit();
	}
}
