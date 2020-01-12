package com.atguigu.gmall.util;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

/**
 * @author lvlei
 * create on 2020-01-12-12:20
 */
public class RedisUtil {
    private JedisPool jedisPool ;

    public void init(String host,int port,int database){
        JedisPoolConfig poolConfig = new JedisPoolConfig();
        poolConfig.setMaxTotal(200);
        poolConfig.setMaxIdle(30);
        poolConfig.setMaxWaitMillis(10*1000);
        jedisPool = new JedisPool(poolConfig,host,port,10*2000);
    }

    public  Jedis getJedis(){
        Jedis resource = jedisPool.getResource();
        return resource;
    }

    /*public static void main(String[] args) {
        RedisUtil redisUtil = new RedisUtil();
        redisUtil.init("192.168.12.130",6379,0);
        Jedis jedis = redisUtil.getJedis();
        System.out.println(jedis);
    }*/
}
