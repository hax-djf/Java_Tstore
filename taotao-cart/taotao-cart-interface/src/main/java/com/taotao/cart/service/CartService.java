package com.taotao.cart.service;

import java.util.List;

import com.taotao.common.pojo.TaotaoResult;
import com.taotao.pojo.TbItem;

/**
 * 购物车的接口主要实现购物车的增删查改的操作
 * @author Administrator
 *
 */
public interface CartService {
	/**
	 * 将商品添加到购物车中 
	 * @param item 商品
	 * @param num 数量
	 * @param userId 用户的id 
	 * @return 返回taotaoresult 
	 */
	public TaotaoResult addItemCart(TbItem item,Integer num,Long userId);
	/**
	 *  根据用户的id 查询是商品列表
	 * @param userId 用户的id 
	 * @return 返回一个list的 结构集
	 */
	public List<TbItem> getCartList(Long userId);
	/**
	 *  更新商品的存储操作
	 * @param userId 用户id 
	 * @param itemId 商品id 
	 * @param num 获取到商品的数量
	 * @return 返回taotaoresult 结构集
	 */
	public TaotaoResult updateItemCartByItemId(Long userId,Long itemId,Integer num);
	/**
	 * 删除商品的id 
	 * @param userId 用户的id 
	 * @param itemId 商品的id 
	 * @return
	 */
	public TaotaoResult deleteItemCartByItemId(Long userId,Long itemId);
}
