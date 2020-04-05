package com.taotao.service.manager.jedis;

public interface JedisClient {
	
	String set(String key, String value); //设置值
	String get(String key); //获取值
	Boolean exists(String key); //判断值是否存在
	Long expire(String key, int seconds); //设置过期时间
	Long ttl(String key); //获取剩余时间
	Long incr(String key); //为其增加1
	Long hset(String key, String field, String value); //hash 设置值 类似里面还有一个value
	String hget(String key, String field); //hash 得到值
	Long hdel(String key, String... field); // hash类型删除值

}
