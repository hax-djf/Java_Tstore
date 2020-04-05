package com.taotao.TestJedis;

import java.util.HashSet;
import java.util.Set;

import org.junit.Test;

import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisCluster;
import redis.clients.jedis.JedisPool;

/*
 *写一个单机版的redis的连接 
 */
public class testjedis {
	//测试单机版
	//@Test
	public void Testjedis() {
		//创建一个jedis对象 并且指定其连接的ip和端口
		Jedis jedis=new Jedis("192.168.1.103",6379);
		//使用jedis对数据库进行操作
		String result = jedis.get("key2");
		//打印出结果
		System.out.println(result);
		//关闭连接资源
		jedis.close();
	}
	
	//使用连接池来连接
	//@Test
	public void Testjedispool() {
		// 第一步：创建一个JedisPool对象。需要指定服务端的ip及端口。
		JedisPool jedisPool = new JedisPool("192.168.1.103", 6379);
		// 第二步：从JedisPool中获得Jedis对象。
		Jedis jedis = jedisPool.getResource();
		// 第三步：使用Jedis操作redis服务器。
		jedis.set("jedispool", "test");
		String result = jedis.get("jedispool");
		System.out.println(result);
		// 第四步：操作完毕后关闭jedis对象，连接池回收资源。
		jedis.close();
		// 第五步：关闭JedisPool对象。
		jedisPool.close();
	}
	
	//使用群集版本的进行连接
	@Test
	public void TestJedisCluster() {
		/**
		 * 第一步：使用JedisCluster对象。需要一个Set<HostAndPort>参数。Redis节点的列表。
		   第二步：直接使用JedisCluster对象操作redis。在系统中单例存在。
		   第三步：打印结果
		   第四步：系统关闭前，关闭JedisCluster对象。
		 */
		//使用JedisCluster对象。需要一个Set<HostAndPort>参数。Redis节点的列表。
		Set<HostAndPort> nodes = new HashSet<>();
		nodes.add(new HostAndPort("192.168.1.103", 7001));
		nodes.add(new HostAndPort("192.168.1.103", 7002));
		nodes.add(new HostAndPort("192.168.1.103", 7003));
		nodes.add(new HostAndPort("192.168.1.103", 7004));
		nodes.add(new HostAndPort("192.168.1.103", 7005));
		nodes.add(new HostAndPort("192.168.1.103", 7006));
		//进行节点添加到JedisCluster对象中
		JedisCluster jedisCluster=new JedisCluster(nodes);
		jedisCluster.set("jediscluster", "jedisclusterTest");
		String result = jedisCluster.get("jediscluster");
		//打印结果
		System.out.println(result);
		//关闭jediscluster
		jedisCluster.close();
	}
	
}
