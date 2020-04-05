package com.taotao.sso.service;

import com.taotao.common.pojo.TaotaoResult;
import com.taotao.pojo.TbUser;

/**
 * 用户的注册操作
 * @author Administrator
 */

public interface UserRegisterService {
	//用户信息注册
	public TaotaoResult register(TbUser user);
	//注册用户信息的进行数据的校验操作(防止数据已经被注册的操作)
	public TaotaoResult checkData(String param, Integer type);
}
