package com.example.project.config;

import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

/**
 * 本地缓存配置（Caffeine）
 * 题目查询结果缓存 30 分钟，Token 黑名单缓存 24 小时
 */
@Configuration
@EnableCaching
public class CacheConfig {

    @Bean
    public CacheManager cacheManager() {
        CaffeineCacheManager manager = new CaffeineCacheManager();

        // 题目缓存：30 分钟过期
        manager.registerCustomCache("questions",
                Caffeine.newBuilder()
                        .expireAfterWrite(30, TimeUnit.MINUTES)
                        .maximumSize(500)
                        .build());

        // Token 黑名单：24 小时过期
        manager.registerCustomCache("tokenBlacklist",
                Caffeine.newBuilder()
                        .expireAfterWrite(24, TimeUnit.HOURS)
                        .maximumSize(1000)
                        .build());

        return manager;
    }
}
