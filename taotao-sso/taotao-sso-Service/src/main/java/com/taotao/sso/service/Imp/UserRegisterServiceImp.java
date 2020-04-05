package com.taotao.sso.service.Imp;

import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import com.taotao.common.pojo.TaotaoResult;
import com.taotao.mapper.TbUserMapper;
import com.taotao.pojo.TbUser;
import com.taotao.pojo.TbUserExample;
import com.taotao.pojo.TbUserExample.Criteria;
import com.taotao.sso.service.UserRegisterService;
@Service
public class UserRegisterServiceImp implements UserRegisterService {
	@Autowired
	private TbUserMapper usermapper;
	
	@Override
	public TaotaoResult register(TbUser user) {
		//1.注入mapper
		//2.校验数据
		//2.1 校验用户名和密码不能为空
		//用户名的校验
		if(StringUtils.isEmpty(user.getUsername())) {
			return TaotaoResult.build(400, "注册失败.请校验数据以后在提交数据");
		}
		if(StringUtils.isEmpty(user.getPassword())) {
			return TaotaoResult.build(400, "请校验数据以后在提交数据");
		}
		//校验数据是否被提交
		TaotaoResult result=checkData(user.getUsername(), 1);
		if(!(boolean)result.getData()) { //如果fasle 表示数据不可用
			return TaotaoResult.build(400, "注册失败. 请校验用户后请再提交数据");
		}
		//校验电话号码
		if(StringUtils.isNotBlank(user.getPhone())) {
			//对数据校验
			TaotaoResult result2 = checkData(user.getPhone(), 2);
			if(!(boolean)result2.getData()) {
				return TaotaoResult.build(400, "注册失败. 请校验手机号请再提交数据");
			}
		}
		//校验邮箱
		if(StringUtils.isNotBlank(user.getEmail())) {
			//对数据校验
			TaotaoResult result3 = checkData(user.getEmail(), 3);
			if(!(boolean)result3.getData()) {
				return TaotaoResult.build(400, "注册失败. 请校验邮箱请再提交数据");
			}
		}
		
		//如果数据校验成功对其他的数据信息进行补全
		user.setCreated(new Date());
		user.setUpdated(user.getCreated());
		//对密码加密
		String md5password = DigestUtils.md5DigestAsHex(user.getPassword().getBytes());
		user.setPassword(md5password);
		//数据插入
		usermapper.insert(user);
		
		return TaotaoResult.ok();
	}

	@Override
	public TaotaoResult checkData(String param, Integer type) {
		//1.注入mapper
		//2.根据参数动态的生成查询的条件
		TbUserExample example = new TbUserExample();
		Criteria criteria = example.createCriteria();	
		if(type==1) {//如果类型1 表示是用户名
		if(StringUtils.isEmpty(param)){
			return TaotaoResult.ok(false);
		}
		//数据的查询操作 设置数据
		criteria.andUsernameEqualTo(param);
		}else if(type==2){
			//phone
			criteria.andPhoneEqualTo(param);
		}else if(type==3){
			//email
			criteria.andEmailEqualTo(param);
		}else{
			//是非法的参数    
			//return 非法的
			return TaotaoResult.build(400, "非法的参数");
		}
		//调用服务进行查询操作
		List<TbUser> list = usermapper.selectByExample(example);
		//4.如果查询到了数据   --数据不可以用   false
		if(list!=null && list.size()>0){
			return TaotaoResult.ok(false);
		}
		//5.如果没查到数据  ---数据是可以用  true
		return TaotaoResult.ok(true);
	}

}
