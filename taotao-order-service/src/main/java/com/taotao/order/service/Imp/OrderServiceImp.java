package com.taotao.order.service.Imp;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.taotao.common.pojo.TaotaoResult;
import com.taotao.mapper.TbOrderItemMapper;
import com.taotao.mapper.TbOrderMapper;
import com.taotao.mapper.TbOrderShippingMapper;
import com.taotao.order.jedis.JedisClient;
import com.taotao.order.pojo.OrderInfo;
import com.taotao.order.service.OrderService;
import com.taotao.pojo.TbOrderItem;
import com.taotao.pojo.TbOrderShipping;
@Service
public class OrderServiceImp implements OrderService {
	@Autowired
	private TbOrderMapper ordermapper;
	@Autowired 
	private TbOrderItemMapper orderItemMapper;
	@Autowired
	private TbOrderShippingMapper shipingMapper;
	@Autowired
	private JedisClient jedisClient;
	
	@Value("${GEN_ORDER_ID_KEY}")
	private String GEN_ORDER_ID_KEY;
	
	@Value("${GEN_ORDER_ID_INIT}")
	private String GEN_ORDER_ID_INIT;
	
	@Value("${GEN_ORDER_ITEM_ID_KEY}")
	private String GEN_ORDER_ITEM_ID_KEY;
	
	//创建订单信息
	@Override
	public TaotaoResult createOrders(OrderInfo info) {
		//1.插入订单表
		//通过redis的incr 生成订单id
		//判断如果key没存在 需要初始化一个key设置一个初始值
		if(!jedisClient.exists(GEN_ORDER_ID_KEY)) {
			jedisClient.set(GEN_ORDER_ID_KEY, GEN_ORDER_ID_INIT);
		}
		//获取到订单id 
		String orderId = jedisClient.incr(GEN_ORDER_ID_KEY).toString();
		//将订单信息进行补全操作
		//订单创建的时间
		info.setCreateTime(new Date());
		//创建订单id
		info.setOrderId(orderId);
		//穿件订单的邮费
		info.setPostFee("0");
		// 1、未付款，2、已付款，3、未发货，4、已发货，5、交易成功，6、交易关闭
		info.setStatus(1);
		//创建订单修改的时间
		info.setUpdateTime(info.getCreateTime());
		//info.setUserId(userId);由controller设置
		//info.setBuyerNick(buyerNick);  在controller设置
		//注入mapper  将上面的信息插入到订单中
		ordermapper.insert(info);
		//2.插入订单项表
		//补全其他的属性
		List<TbOrderItem> orderItems = info.getOrderItems();
		//将每一个订单项设置到订单中
		for (TbOrderItem tbOrderItem : orderItems) {
			//创建订单项的id
			if(!jedisClient.exists(GEN_ORDER_ITEM_ID_KEY)) {
				jedisClient.set(GEN_ORDER_ITEM_ID_KEY, GEN_ORDER_ID_INIT);
			}
			String OrderItems = jedisClient.incr(GEN_ORDER_ITEM_ID_KEY).toString();
			//设置订单id
			tbOrderItem.setOrderId(orderId);
			tbOrderItem.setId(OrderItems);
			//将数据插入到数据库中
			orderItemMapper.insert(tbOrderItem);
		}
		//3.插入订单物流表
		//设置订单id
		TbOrderShipping shipping = info.getOrderShipping();
		//设置订单id
		shipping.setOrderId(orderId);
		shipping.setCreated(info.getCreateTime());
		shipping.setUpdated(info.getUpdateTime());
		//将数据插入到数据库中
		shipingMapper.insert(shipping);
		//返回商品的id 进行数据的回显
		return TaotaoResult.ok(orderId);
	}

}
