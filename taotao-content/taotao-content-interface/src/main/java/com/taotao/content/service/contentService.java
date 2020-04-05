package com.taotao.content.service;

import java.util.List;

import com.taotao.common.EasyUIDataGridResult;
import com.taotao.common.pojo.TaotaoResult;
import com.taotao.pojo.TbContent;

public interface contentService {
	public EasyUIDataGridResult getcontentByCategory(long categoryId,int page, int rows);
	public  TaotaoResult saveContent(TbContent content);
	public TaotaoResult updateContent(TbContent tbContent);
	public TaotaoResult deleteContent(String  ids);
	public List<TbContent> getContentList(long cid);
}
