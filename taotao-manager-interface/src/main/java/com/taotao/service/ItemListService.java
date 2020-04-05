package com.taotao.service;

import com.taotao.common.EasyUIDataGridResult;
import com.taotao.common.pojo.TaotaoResult;
import com.taotao.pojo.TbItem;
import com.taotao.pojo.TbItemDesc;

public interface ItemListService {
	public EasyUIDataGridResult getItemListpage(int page,int rows);
	//商品信息的保存
	public TaotaoResult saveItem(TbItem item,String desc);
	//根据商品的id查询商品
	public TbItem getItemById(Long itemId);
	//根据商品id查询商品的详情信息
	public TbItemDesc getItemDescById(Long itemId);
	
	///rest/item/delete 删除商品信息
	public TaotaoResult deleteItemAndDescById(Long itemID);
	///rest/item/instock 下架商品
	public TaotaoResult instock(Long itemid);
	///rest/item/reshelf //上架商品
	public TaotaoResult reshelf(Long itemId);
	///rest/item/param/item/query/ 查看商品的参数数据
	public TaotaoResult queryItem(Long itemId);
}
