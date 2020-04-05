package com.taotao.order.service;

import com.taotao.common.pojo.TaotaoResult;
import com.taotao.order.pojo.OrderInfo;

public interface OrderService {
	/**
	 * 创建订单项
	 * @param info 订单项
	 * @return
	 */
	public TaotaoResult createOrders(OrderInfo info);
}
