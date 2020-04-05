package com.taotao.common.pojo;

import java.io.Serializable;
import java.util.List;

/**
 * 实现序列化操作
 * @author Administrator
 *
 */
public class SearchResult implements Serializable {
	private List<SearchItem> itemlist;// 查询到所有的数据
	private long recordCount;// 查询到总的记录数
	private long pageCount;//总的页数
	
	public List<SearchItem> getItemlist() {
		return itemlist;
	}
	public void setItemlist(List<SearchItem> itemlist) {
		this.itemlist = itemlist;
	}
	public long getRecordCount() {
		return recordCount;
	}
	public void setRecordCount(long recordCount) {
		this.recordCount = recordCount;
	}
	public long getPageCount() {
		return pageCount;
	}
	public void setPageCount(long pageCount) {
		this.pageCount = pageCount;
	}
}
