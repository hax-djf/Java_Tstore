package com.taotao.sso.service;
/**
 * 用户登录的接口
 * @author Administrator
 *
 */

import com.taotao.common.pojo.TaotaoResult;

public interface UserLoginService {
	/**
	 * 用户的登录方法
	 * @param username 用户名
	 * @param password 密码
	 * @return taotaoresult 登录成功 返回200 并且包含一个token数据
	 */
	public TaotaoResult login(String username,String password);
	
	/**
	 * 根据token获取用户的信息
	 * @param token
	 * @return  TaotaoResult 应该包含用户的信息
	 */
	public TaotaoResult getUserByToken(String token);
}
