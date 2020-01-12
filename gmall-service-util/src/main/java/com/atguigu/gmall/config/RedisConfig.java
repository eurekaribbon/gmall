package com.atguigu.gmall.config;

import com.atguigu.gmall.util.RedisUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author lvlei
 * create on 2020-01-12-13:08
 */
@Configuration
public class RedisConfig {

    @Value("${spring.redis.host:disabled}")
    private String host;
    @Value("${spring.redis.port:0}")
    private int port ;
    @Value("${spring.redis.database:0}")
    private int database;

    @Bean
    public RedisUtil getJedis(){
        RedisUtil redisUtil = new RedisUtil();
        redisUtil.init(host,port,database);
        return redisUtil;
    }
}
