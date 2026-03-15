package org.cybercaelum.household_management.service.impl;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.cybercaelum.household_management.service.OpenImService;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

/**
 * @author CyberCaelum
 * @version 1.0
 * @description: OpenIM 用户 Token 缓存服务
 * 使用 Caffeine 缓存用户 Token，避免频繁调用 OpenIM 接口
 * @date 2026/3/15
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class OpenImUserTokenService {

    private final OpenImService openImService;

    /**
     * 用户 Token 缓存
     * 过期时间设置为 23 小时（OpenIM 默认 Token 有效期通常为 30 天，
     * 但为了安全，我们提前刷新）
     */
    private final Cache<String, String> userTokenCache = Caffeine.newBuilder()
            .maximumSize(10000)  // 最大缓存数量
            .expireAfterWrite(23, TimeUnit.HOURS)  // 写入后 23 小时过期
            .removalListener((key, value, cause) -> {
                log.debug("OpenIM 用户 Token 被移除: userId={}, 原因: {}", key, cause);
            })
            .build();

    /**
     * @description 获取用户 Token（带缓存）
     * @author CyberCaelum
     * @date 2026/3/15
     * @param userId 用户ID
     * @return java.lang.String OpenIM Token
     **/
    public String getUserToken(String userId) {
        // 从缓存获取
        String cachedToken = userTokenCache.getIfPresent(userId);
        if (cachedToken != null) {
            log.debug("从缓存获取 OpenIM Token: userId={}", userId);
            return cachedToken;
        }

        // 缓存未命中，从 OpenIM 获取
        log.debug("从 OpenIM 获取新 Token: userId={}", userId);
        String newToken = openImService.getUserToken(userId);
        
        // 存入缓存
        userTokenCache.put(userId, newToken);
        
        return newToken;
    }

    /**
     * @description 刷新用户 Token（强制从 OpenIM 获取新 Token）
     * @author CyberCaelum
     * @date 2026/3/15
     * @param userId 用户ID
     * @return java.lang.String 新的 OpenIM Token
     **/
    public String refreshUserToken(String userId) {
        log.info("刷新 OpenIM Token: userId={}", userId);
        
        // 清除缓存
        userTokenCache.invalidate(userId);
        
        // 获取新 Token
        String newToken = openImService.getUserToken(userId);
        
        // 存入缓存
        userTokenCache.put(userId, newToken);
        
        return newToken;
    }

    /**
     * @description 清除用户 Token 缓存
     * @author CyberCaelum
     * @date 2026/3/15
     * @param userId 用户ID
     **/
    public void evictUserToken(String userId) {
        log.info("清除 OpenIM Token 缓存: userId={}", userId);
        userTokenCache.invalidate(userId);
    }

    /**
     * @description 清除所有缓存
     * @author CyberCaelum
     * @date 2026/3/15
     **/
    public void clearAllCache() {
        log.info("清除所有 OpenIM Token 缓存");
        userTokenCache.invalidateAll();
    }
}
