package com.academic.service.impl;

import com.academic.service.CacheService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

/**
 * 缓存服务实现类 - 基于Redis
 */
@Slf4j
@Service
public class CacheServiceImpl implements CacheService {
    
    private final RedisTemplate<String, Object> redisTemplate;
    
    public CacheServiceImpl(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }
    
    @Override
    public void set(String key, Object value) {
        try {
            redisTemplate.opsForValue().set(key, value);
            log.debug("缓存设置成功: key={}", key);
        } catch (Exception e) {
            log.error("缓存设置失败: key={}", key, e);
        }
    }
    
    @Override
    public void set(String key, Object value, long timeout, TimeUnit unit) {
        try {
            redisTemplate.opsForValue().set(key, value, timeout, unit);
            log.debug("缓存设置成功（带过期时间）: key={}, timeout={}, unit={}", key, timeout, unit);
        } catch (Exception e) {
            log.error("缓存设置失败: key={}", key, e);
        }
    }
    
    @Override
    public Object get(String key) {
        try {
            Object value = redisTemplate.opsForValue().get(key);
            if (value != null) {
                log.debug("缓存命中: key={}", key);
            } else {
                log.debug("缓存未命中: key={}", key);
            }
            return value;
        } catch (Exception e) {
            log.error("缓存获取失败: key={}", key, e);
            return null;
        }
    }
    
    @Override
    public void delete(String key) {
        try {
            redisTemplate.delete(key);
            log.debug("缓存删除成功: key={}", key);
        } catch (Exception e) {
            log.error("缓存删除失败: key={}", key, e);
        }
    }
    
    @Override
    public boolean exists(String key) {
        try {
            return Boolean.TRUE.equals(redisTemplate.hasKey(key));
        } catch (Exception e) {
            log.error("缓存存在性判断失败: key={}", key, e);
            return false;
        }
    }
    
    @Override
    public void clear() {
        try {
            redisTemplate.getConnectionFactory().getConnection().flushAll();
            log.info("所有缓存已清空");
        } catch (Exception e) {
            log.error("缓存清空失败", e);
        }
    }
}
