package com.tu.config;

import com.alibaba.fastjson.support.spring.GenericFastJsonRedisSerializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;

/**
 * @Author: jfy
 * @Date: 2024/3/2
 */
@Configuration
public class RedisConfig {

    /**
     * 自定义redisTemplate配置
     * <p>
     * 使用的序列化方式
     *
     * @param redisConnectionFactory
     * @return
     */
    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory redisConnectionFactory) {
        RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(redisConnectionFactory);
        //key值使用spring默认的StringRedisSerializer
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        //value值使用fastjson的GenericFastJsonRedisSerializer
        GenericFastJsonRedisSerializer fastJsonRedisSerializer = new GenericFastJsonRedisSerializer();
        redisTemplate.setValueSerializer(fastJsonRedisSerializer);
        //以下是hash序列化的配置
        redisTemplate.setHashKeySerializer(new StringRedisSerializer());
        redisTemplate.setHashValueSerializer(fastJsonRedisSerializer);

        return redisTemplate;
    }

//    @Bean
//    public RedisCacheConfiguration redisCacheConfiguration(CacheProperties cacheProperties) {
//
//        RedisCacheConfiguration redisCacheConfiguration = RedisCacheConfiguration.defaultCacheConfig();
//
//        // 先载入配置文件中的配置信息
//        CacheProperties.Redis redisProperties = cacheProperties.getRedis();
//
//        // 根据配置文件中的定义，初始化 Redis Cache 配置
//        if (redisProperties.getTimeToLive() != null) {
//            redisCacheConfiguration = redisCacheConfiguration.entryTtl(redisProperties.getTimeToLive());
//        }
//        if (StringUtils.hasText(redisProperties.getKeyPrefix())) {
//            redisCacheConfiguration = redisCacheConfiguration.prefixCacheNameWith(redisProperties.getKeyPrefix());
//        }
//        if (!redisProperties.isCacheNullValues()) {
//            redisCacheConfiguration = redisCacheConfiguration.disableCachingNullValues();
//        }
//        if (!redisProperties.isUseKeyPrefix()) {
//            redisCacheConfiguration = redisCacheConfiguration.disableKeyPrefix();
//        }
//
//        // Fastjson 的通用 RedisSerializer 实现
//        GenericFastJsonRedisSerializer serializer = new GenericFastJsonRedisSerializer();
//
//        // 设置 Value 的序列化方式
//        return redisCacheConfiguration
//                .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(serializer));
//    }
}
