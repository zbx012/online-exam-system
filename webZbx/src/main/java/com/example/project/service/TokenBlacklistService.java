package com.example.project.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

/**
 * JWT Token 黑名单服务
 * 用户退出登录时将 Token 加入黑名单，剩余有效期 = JWT 剩余有效时间
 */
@Service
public class TokenBlacklistService {

    private static final Logger log = LoggerFactory.getLogger(TokenBlacklistService.class);
    private static final String BLACKLIST_PREFIX = "blacklist:token:";
    private static final long DEFAULT_TTL_SECONDS = 86400; // 默认 24 小时

    @Autowired(required = false)
    private RedisTemplate<String, Object> redisTemplate;

    /**
     * 将 Token 加入黑名单
     */
    public void blacklist(String token) {
        if (redisTemplate == null) {
            log.warn("Redis 不可用，Token 黑名单未生效");
            return;
        }
        try {
            String key = BLACKLIST_PREFIX + token;
            redisTemplate.opsForValue().set(key, "1", DEFAULT_TTL_SECONDS, TimeUnit.SECONDS);
            log.info("Token 已加入黑名单: {}...", token.substring(0, Math.min(20, token.length())));
        } catch (Exception e) {
            log.warn("Redis 操作失败，Token 黑名单未生效: {}", e.getMessage());
        }
    }

    /**
     * 检查 Token 是否在黑名单中（已登出）
     */
    public boolean isBlacklisted(String token) {
        if (redisTemplate == null) {
            return false; // Redis 不可用时放行
        }
        try {
            String key = BLACKLIST_PREFIX + token;
            return Boolean.TRUE.equals(redisTemplate.hasKey(key));
        } catch (Exception e) {
            log.warn("Redis 操作失败，跳过黑名单检查: {}", e.getMessage());
            return false;
        }
    }
}
