package com.sky.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;

@Configuration
@Slf4j
public class RedisConfigration {

    @Bean
    public RedisTemplate redisTemplate(RedisTemplate redisTemplate){
        log.info("开始配置RedisTemplate...");
        //设置Redis的连接工厂
        redisTemplate.setConnectionFactory(redisTemplate.getConnectionFactory());
        //设置Redis key的序列化器
        redisTemplate.setKeySerializer(redisTemplate.getStringSerializer());
        return redisTemplate;
    }
}
