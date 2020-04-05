package com.taotao.order.controller;

import java.util.List;


import javax.servlet.http.HttpServletRequest;

import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.taotao.cart.service.CartService;
import com.taotao.common.pojo.TaotaoResult;
import com.taotao.order.pojo.OrderInfo;
import com.taotao.order.service.OrderService;
import com.taotao.pojo.TbItem;
import com.taotao.pojo.TbUser;
import com.taotao.sso.service.UserLoginService;

/**
 * 创建订单
 * @author Administrator
 */
@Controller
public class OrderController {
	@Autowired
	private OrderService orderService;
	
	@Autowired
	private CartService cartService;
	
	@Value("${TT_TOKEN_KEY}")
	private String TT_TOKEN_KEY;
	
	/**
	 * url:/order/order-cart.html
	 * 参数：没有参数，但需要用户的id  从cookie中获取token 调用SSO的服务获取用户的ID
	 * 返回值：逻辑视图 （订单的确认页面）
	 */
	@RequestMapping("/order/order-cart")
	public String showOrder(HttpServletRequest request) {
		//测试是否有数据的输出
		System.out.println(request.getAttribute("USER_INFO"));
		//获取到用户的id
		TbUser user = (TbUser)request.getAttribute("USER_INFO");
		//调用cart服务获取到订单的信息
		List<TbItem> list = cartService.getCartList(user.getId());
		//将数据设置到页面中去
		request.setAttribute("cartList", list);
		//返回页面
		return "order-cart";
	}
	
	@RequestMapping(value="/order/create",method=RequestMethod.POST)
	public String CreateOrder(HttpServletRequest request ,OrderInfo info) {
		//创建订单
		TbUser user = (TbUser)request.getAttribute("USER_INFO");
		//补全用户的信息
		info.setUserId(user.getId());
		info.setBuyerNick(user.getUsername());
		//调用服务 将数据存储到数据库中
		TaotaoResult reslut = orderService.createOrders(info);
		//进行数据的回显 回显到成功的页面
		request.setAttribute("orderId", reslut.getData());
		//设置付款时间
		request.setAttribute("payment", info.getPayment());
		//设置发货时间
		DateTime dateTime = new DateTime();
		DateTime plusDays = dateTime.plusDays(3);//加3天
		//设置格式
		request.setAttribute("date", plusDays.toString("yyyy-MM-dd"));
		//返回成功的页面
		return "success";
	}
	
}
