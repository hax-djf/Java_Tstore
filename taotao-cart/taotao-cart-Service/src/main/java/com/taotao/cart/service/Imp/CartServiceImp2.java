package com.taotao.cart.service.Imp;

import java.util.ArrayList;

import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.taotao.cart.jedis.JedisClient;
import com.taotao.cart.service.CartService;
import com.taotao.common.pojo.TaotaoResult;
import com.taotao.common.util.JsonUtils;
import com.taotao.pojo.TbItem;
//@Service
public class CartServiceImp2 implements CartService{
	@Autowired
	private JedisClient jedisclient;
	@Value("${TT_CART_REDIS_PRE_KEY}")
	private String TT_CART_REDIS_PRE_KEY;
	
	//根据hash key 进行商品数据查询
	private TbItem queryItemByItemIdAndUserId(Long itemId, Long userId) {
		String hget = jedisclient.hget(TT_CART_REDIS_PRE_KEY+":"+userId, itemId+"");
		if(StringUtils.isNoneBlank(hget)) {
			//字符串不为空的话
			return JsonUtils.jsonToPojo(hget, TbItem.class);
		}
		//否则返回
		return null;
	}
	
	@Override
	public TaotaoResult addItemCart(TbItem item, Integer num, Long userId) {
		//1.注入jedisclient 这个地方是将购物车的商品添加到redis中
		//调用服务
		TbItem jedisItem = queryItemByItemIdAndUserId(item.getId(), userId);
		if(jedisItem!=null) {
			//2.查看redis中是否存在商品 存在话直接进行num加1
			//不为空的话 直接在数据中添加1 
			System.out.println("这个商品的数据不为空");
			System.out.println("jedis中插叙出来的数据"+jedisItem);
			jedisItem.setNum(jedisItem.getNum()+num);
			//在设置回去
			//图片只取一张
			jedisclient.hset(TT_CART_REDIS_PRE_KEY+":"+userId, jedisItem.getId()+"", JsonUtils.objectToJson(jedisItem));
		}else {
			//3.不存在的话,将商品数据以格式为 hash的方式进行数据存储
			//设置
			item.setNum(num);
			//只设置一张图片
			if(item.getImage()!=null) {
				String[] strings = item.getImage().split(",");
				//存第一张图片
				item.setImage(strings[0]);
			}
			jedisclient.hset(TT_CART_REDIS_PRE_KEY+":"+userId, item.getId()+"", JsonUtils.objectToJson(item));
		}
		
		return TaotaoResult.ok();
	}
	
	
	
	@Override
	public List<TbItem> getCartList(Long userId) {
		//根据用户id 获取商品列表
		Map<String, String> hgetAll = jedisclient.hgetAll(TT_CART_REDIS_PRE_KEY+":"+userId+"");
		//创建一个list集合
		List<TbItem> list =new ArrayList<>();
		if(hgetAll!=null) {
		for (Map.Entry<String, String> entry : hgetAll.entrySet()) {
			//得到每一个entry数据
			String valueString=entry.getValue();
			TbItem tbItem = JsonUtils.jsonToPojo(valueString, TbItem.class);
			//将数据设置到list中
			list.add(tbItem);
		}
		}
		
		return list;
	}

	@Override
	public TaotaoResult updateItemCartByItemId(Long userId, Long itemId, Integer num) {
		//1.根据用户id和商品的id获取商品的对象
		TbItem tbItem = queryItemByItemIdAndUserId(itemId,userId);
		//判断是否存在
		if(tbItem!=null){
			//2.更新数量
			tbItem.setNum(num);
			//设置回redis中
			jedisclient.hset(TT_CART_REDIS_PRE_KEY+":"+userId, itemId+"", JsonUtils.objectToJson(tbItem));
		}else{
			//几乎不可能初相见这个情况的
		}
		return TaotaoResult.ok();
	}

	@Override
	public TaotaoResult deleteItemCartByItemId(Long userId, Long itemId) {
		jedisclient.hdel(TT_CART_REDIS_PRE_KEY+":"+userId, itemId+"");
		return TaotaoResult.ok();
	}
	

}
