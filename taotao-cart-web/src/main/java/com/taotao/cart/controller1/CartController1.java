package com.taotao.cart.controller1;

import java.util.ArrayList;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.taotao.cart.service.CartService;
import com.taotao.common.pojo.TaotaoResult;
import com.taotao.common.util.CookieUtils;
import com.taotao.common.util.JsonUtils;
import com.taotao.pojo.TbItem;
import com.taotao.pojo.TbUser;
import com.taotao.service.ItemListService;
import com.taotao.sso.service.UserLoginService;

/**
 * 购物车的controller开发
 * @author Administrator
 *
 */

@Controller
public class CartController1 {
	@Autowired
	private CartService cartService;
	@Autowired
	private UserLoginService loginService;
	@Autowired
	private ItemListService itemlstService;
	
	@Value("${TT_TOKEN_KEY}") //cookie中的token码 校验是否登录状态
	private String TT_TOKEN_KEY;
	@Value("${TT_CART_KEY}")
	private String TT_CART_KEY;
	//url:/cart/add/{itemId}?num=2 参数：商品的id 以及num 返回值：jsp页面
	@RequestMapping("/cart/add/{itemId}")
	public String addItemCart(@PathVariable Long itemId,Integer num,HttpServletRequest request,HttpServletResponse response) {
		//注入loginservice 服务
		//注入 cartService 服务
		//1.判断用户是否登录
		String token = CookieUtils.getCookieValue(request, TT_TOKEN_KEY);
		//获得到token数据
		// 调用SSO的服务查询用户的信息
		TaotaoResult result = loginService.getUserByToken(token);
		// 获取商品的数据 根据id 获取到商品数据
		TbItem tbItem = itemlstService.getItemById(itemId);
		
		//测试
		System.out.println("数据库中查询出来的商品信息"+tbItem);
		//============
		
		if(result.getStatus()==200) {
			//表示用户的信息存在
			TbUser user = (TbUser)result.getData();
			//调用carService 服务
			cartService.addItemCart(tbItem, num, user.getId());
		}else {
			// 5.如果没有登录 调用设置到cookie的方法
		// 先根据cookie获取购物车的列表
		List<TbItem> cartList = getCookieCartList(request);
		//测试
		System.out.println("cookies查询出来的商品信息"+cartList);
		//====
		
		boolean flag = false;
		// 判断如果购物车中有包含要添加的商品 商品数量相加
		if(cartList.size()>0) {
			System.out.println("111111111");
			for (TbItem tbItem2 : cartList) {
				if (tbItem2.getId() == itemId.longValue()) {
					
					System.out.println("相同的商品数据:"+tbItem2);
					// 找到列表中的商品 更新数量
					tbItem2.setNum(tbItem2.getNum() + num);
					flag = true;
					break;
				}
			}
		}
		
		if (flag == true) {
			// 如果找到对应的商品，更新数量后，还需要设置回cookie中
			CookieUtils.setCookie(request, response, TT_CART_KEY, JsonUtils.objectToJson(cartList), 7 * 24 * 3600,
					true);
		} else {	
			// 如果没有就直接添加到购物车
			// 调用商品服务
			// 设置数量
			tbItem.setNum(num);
			// 设置图片为一张
			if (tbItem.getImage() != null) {
				tbItem.setImage(tbItem.getImage().split(",")[0]);
			}
			// 添加商品到购物车中
			cartList.add(tbItem);
			// 设置到cookie中
			CookieUtils.setCookie(request, response, TT_CART_KEY, JsonUtils.objectToJson(cartList), 7 * 24 * 3600,
					true);
		}	
		}
		
		return "cartSuccess";
	}

	//展示购物车的列表
	@RequestMapping("/cart/cart")
	public String getCartList(HttpServletRequest request) {
		// 1.引入服务
		// 2.注入服务

		// 3.判断用户是否登录
		// 从cookie中获取用户的token信息
		String token = CookieUtils.getCookieValue(request, TT_TOKEN_KEY);
		// 调用SSO的服务查询用户的信息
		TaotaoResult result = loginService.getUserByToken(token);
		System.out.println(result.getData());
		// 获取商品的数据
		if (result.getStatus() == 200) {
			// 4.如果已登录，调用service的方法
			TbUser user = (TbUser) result.getData();
			List<TbItem> cartList = cartService.getCartList(user.getId());
			System.out.println(cartList.size());
			request.setAttribute("cartList", cartList);
		} else {
			// 5.如果没有登录 调用cookie的方法 获取商品的列表
			List<TbItem> cartList = getCookieCartList(request);
			// 将数据传递到页面中
			request.setAttribute("cartList", cartList);
		}
		return "cart";
	}
	
	/** 根据商品id 更新商品的信息
	 * url:/cart/update/num/{itemId}/{num} 参数：商品的id 和更新后的数量 还有用户的id 返回值：json
	 */
	@RequestMapping("/service/cart/update/num/{itemId}/{num}")
	@ResponseBody
	public TaotaoResult updateItemCartByItemId(@PathVariable Long itemId,@PathVariable Integer num,HttpServletRequest request,HttpServletResponse response) {
		//注入服务
		//判断是否登录状态
		String token = CookieUtils.getCookieValue(request, TT_TOKEN_KEY);
		//调用sso服务
		TaotaoResult result = loginService.getUserByToken(token);
		if(result.getStatus()==200) {
			//表示登录
			TbUser user=(TbUser)result.getData();
			//修改
			TaotaoResult taotaoResult = cartService.updateItemCartByItemId(user.getId(), itemId, num);
			return taotaoResult;
		}else {
			//如果没有登录 调用cookie的方法 更新cookie中的商品的数量的方法
			updateCookieItemCart(itemId, num, request, response);
		}
		return TaotaoResult.ok();
		
	}
	
	//删除商品服务 重定向到查询页面
	@RequestMapping("/cart/delete/{itemId}")
	public String deleteItemCartByItemId(@PathVariable Long itemId,HttpServletResponse response,HttpServletRequest request) {
		//注入服务
		//引入服务

		// 3.判断用户是否登录
		// 从cookie中获取用户的token信息
		String token = CookieUtils.getCookieValue(request, TT_TOKEN_KEY);
		//调用服务
		TaotaoResult result = loginService.getUserByToken(token);
		if(result.getStatus()==200) {
			//表示登录成功
			TbUser user=(TbUser)result.getData();
			TaotaoResult result2 = cartService.deleteItemCartByItemId(user.getId(), itemId);
		}else {
			deleteCookieItemCartByItemId(itemId, response, request);
		}
		//重定向到这个页面
		return "redirect:/cart/cart.html";
		
		
	}

	
	
	//=====================这里面添加一个方法是当用户没有登录的时候,添加商品带购物车的话是将商品的信息添加到了cookie,并且是设置有效期的=======
	//cookies  添加商品信息
	private void getCookieItemByItemId(Long itemId, Integer num, HttpServletRequest request,
			HttpServletResponse response, TbItem tbItem) {
		//表示用户不是登录的状态
		//将用户添加的
		//先查询购物车中是否存在数商品添加到cookie中据
		List<TbItem> cartList = getCookieCartList(request);
		//如果存在数据数据的话,直接改num
		boolean flag = false;
		if(cartList!=null) {
			//判断商品中是否存在数据
			for (TbItem tbItem2 : cartList) {
				if(tbItem2.getId() == itemId.longValue()) {
					//有一样的将num 设置
					tbItem2.setNum(tbItem2.getNum()+num);
					//在将数据设置回去
					flag=true;
					break;//结束循环
				}
			}
		}
		
		//将数据设置到cookie中
		if(flag) {
			// 如果找到对应的商品，更新数量后 ,将所有商品设置回去,还需要设置回cookie中
			CookieUtils.setCookie(request, response, TT_CART_KEY, JsonUtils.objectToJson(cartList), 7 * 24 * 3600,
				true);
		}else {
			//没有找到商品 直接将商品存储到cookie中
			tbItem.setNum(num);
			//设置图片一张
			if(tbItem.getImage()!=null) {
				tbItem.setImage(tbItem.getImage().split(",")[0]);
			}
			//将数据添加到cartlist中
			cartList.add(tbItem);
			//将商品信息设置回去
			CookieUtils.setCookie(request, response, TT_CART_KEY, JsonUtils.objectToJson(cartList), 7 * 24 * 3600,
					true);
		}
	}
	
	//获取 cookeis 中的商品列表信息
	public List<TbItem> getCookieCartList(HttpServletRequest request) {
		// 从cookie中获取商品的列表
		String jsonstr = CookieUtils.getCookieValue(request, TT_CART_KEY, true);// 商品的列表的JSON
		// 讲商品的列表的JSON转成 对象
		if (StringUtils.isNotBlank(jsonstr)) {
			List<TbItem> list = JsonUtils.jsonToList(jsonstr, TbItem.class);
			return list;
		}
		return new ArrayList<>();

	}
	//cookies 修改
	private void updateCookieItemCart(Long ItemId, Integer num, HttpServletRequest request,
			HttpServletResponse response) {
		//没有登录的话 
		List<TbItem> cartList = getCookieCartList(request);
		boolean flag=false;
		if(cartList.size()>0) {
		for (TbItem tbItem : cartList) {
			if(tbItem.getId()==ItemId.longValue()) {
				tbItem.setNum(tbItem.getNum()+num);
				flag=true;
				break;
			}
		}
		}
		//找到数据更新
		if(flag) {
			//3.如果存在 更新数量
			CookieUtils.setCookie(request, response, TT_CART_KEY, JsonUtils.objectToJson(cartList), 7 * 24 * 3600,
					true);
		}else {
			//错误
			
		}
	}
	
	//删除 cookies
	private void deleteCookieItemCartByItemId(Long itemId, HttpServletResponse response, HttpServletRequest request) {
		//表示没有登录的话
		List<TbItem> cartList = getCookieCartList(request);
		boolean flag=false;
		for (TbItem tbItem : cartList) {
			if(tbItem.getId()==itemId) {
				//找到数据删除
				cartList.remove(tbItem);
				flag=true;
				break;
			}
		}
		if(flag) {
			//存在删除
		CookieUtils.setCookie(request, response, TT_CART_KEY, JsonUtils.objectToJson(cartList), 7 * 24 * 3600,
				true);
		}else {
			//不管了
		}
	}
	
}
