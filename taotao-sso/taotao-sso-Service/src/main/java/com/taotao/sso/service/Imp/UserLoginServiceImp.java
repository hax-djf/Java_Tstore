package com.taotao.sso.service.Imp;

import java.util.List;
import java.util.UUID;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import com.taotao.common.pojo.TaotaoResult;
import com.taotao.common.util.JsonUtils;
import com.taotao.mapper.TbUserMapper;
import com.taotao.pojo.TbUser;
import com.taotao.pojo.TbUserExample;
import com.taotao.pojo.TbUserExample.Criteria;
import com.taotao.sso.jedis.JedisClient;
import com.taotao.sso.service.UserLoginService;

@Service
public class UserLoginServiceImp implements UserLoginService {
	@Autowired 
	private TbUserMapper usermapper;
	@Autowired
	private JedisClient jedisClient;
	@Value("${USER_INFO}")
	private String USER_INFO; //设置标识符
	@Value("${EXPIRE_TIME}")
	private Integer EXPIRE_TIME;//过期时间
	@Override
	public TaotaoResult login(String username, String password) {
		//1.注入mapper
		//2.校验用户名和密码是否为空
		if(StringUtils.isEmpty(username)||StringUtils.isEmpty(password)) {
			return  TaotaoResult.build(400, "用户名账号密码错误");
		}
		//3.先校验用户名
		TbUserExample example=new TbUserExample();
		Criteria criteria = example.createCriteria();
		criteria.andUsernameEqualTo(username);
		List<TbUser> selectUsername = usermapper.selectByExample(example);
		if(selectUsername==null && selectUsername.size()==0) {
			//表示没有查询到数据
			return TaotaoResult.build(400, "用户名不存在");
		}
		//得到账号
		TbUser user=selectUsername.get(0);
		//4.再校验密码 密码是经过了加密的操作
		String md5DigestAsHex = DigestUtils.md5DigestAsHex(password.getBytes());
		//直接根据用户对密码的查询
		if(!md5DigestAsHex.equals(user.getPassword())) {
			//表示密码不正确
			return TaotaoResult.build(400, "密码错误");
		}
		//5.检验成功的话
		//6.生成token : uuid生成    ，还需要设置token的有效性期来模拟session  用户的数据存放在redis  (key:token,value:用户的数据JSON)
		//生成一个token的随机码
		String token = UUID.randomUUID().toString();
		//将随机码和用户的信息存储到session(redis)中
		//存放用户数据到redis中，使用jedis的客户端,为了管理方便加一个前缀"kkk:token"
		//设置密码为空
		user.setPassword(null);
		//设置用户信息
		jedisClient.set(USER_INFO+":"+token, JsonUtils.objectToJson(user));
		//设置有效时间
		jedisClient.expire(USER_INFO+":"+token, EXPIRE_TIME);
		//将token码转发到data数中
		return TaotaoResult.ok(token);
	}
	
	
	//根据taken获取到用户的信息
	@Override
	public TaotaoResult getUserByToken(String token) {
		//1.注入jedis
		//2.jedis进行数据获取
		String value = jedisClient.get(USER_INFO+":"+token);
		//判断数据是否存在
		if(StringUtils.isNotBlank(value)) {
			//将json数据转成pojo
			TbUser user = JsonUtils.jsonToPojo(value, TbUser.class);
			//重新设置过期时间
			jedisClient.expire(USER_INFO+":"+token, EXPIRE_TIME);
			//将数据设置带其中
			return TaotaoResult.ok(user);
		}
		//否则数据不存已经过期了
		return TaotaoResult.build(400, "用户登陆过期请重新登陆");
	}

}
