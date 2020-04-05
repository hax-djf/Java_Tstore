package com.taotao.TestJedis;

import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.taotao.content.jedis.JedisClient;

/**
 * 对客户端进行一个测试
 * @author Administrator
 */

public class TestjedisClient {
	@Test
	public void TestJedisClient() {
		//加载配置文件
		ApplicationContext applicationContext = new ClassPathXmlApplicationContext("classpath:spring/applicationContext-redis.xml");
		//从容器中获取到对象
		JedisClient jedisClient = applicationContext.getBean(JedisClient.class);
		//设置一个值
		jedisClient.set("hello", "testclient");
		//获取值
		String string = jedisClient.get("hello");
		System.out.println(string);
	}
}
