package com.example.project.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Service;

/**
 * JWT Token 黑名单服务（基于 Caffeine 本地缓存）
 * 用户退出登录时将 Token 加入黑名单，24 小时后自动过期
 */
@Service
public class TokenBlacklistService {

    private static final Logger log = LoggerFactory.getLogger(TokenBlacklistService.class);
    private static final String CACHE_NAME = "tokenBlacklist";

    @Autowired
    private CacheManager cacheManager;

    public void blacklist(String token) {
        try {
            Cache cache = cacheManager.getCache(CACHE_NAME);
            if (cache != null) {
                cache.put(token, "1");
                log.info("Token 已加入黑名单: {}...", token.substring(0, Math.min(20, token.length())));
            }
        } catch (Exception e) {
            log.warn("Token 黑名单操作失败: {}", e.getMessage());
        }
    }

    public boolean isBlacklisted(String token) {
        try {
            Cache cache = cacheManager.getCache(CACHE_NAME);
            if (cache != null) {
                return cache.get(token) != null;
            }
        } catch (Exception e) {
            log.warn("Token 黑名单查询失败: {}", e.getMessage());
        }
        return false;
    }
}
