package com.kma.demo.config;


import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;
import java.util.Objects;

//@Configuration
//@EnableCaching
public class CacheConfig {

//    @Bean
//    public RedisCacheConfiguration redisCacheConfiguration() {
//        RedisCacheConfiguration redisCacheConfiguration = RedisCacheConfiguration
//                .defaultCacheConfig()
//                .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(new GenericJackson2JsonRedisSerializer()))
//                .entryTtl(Duration.ofMinutes(5));
//        return redisCacheConfiguration;
//    }
//
//    @Bean
//    public RedisCacheManager redisCacheManager(RedisConnectionFactory redisConnectionFactory, RedisCacheConfiguration redisCacheConfiguration) {
//        return RedisCacheManager
//                .builder(redisConnectionFactory)
//                .cacheDefaults(redisCacheConfiguration)
//                .build();
//    }
}
