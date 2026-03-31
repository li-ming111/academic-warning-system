package com.academic.service;

import java.util.concurrent.TimeUnit;

/**
 * 缓存服务接口
 */
public interface CacheService {
    
    /**
     * 设置缓存
     */
    void set(String key, Object value);
    
    /**
     * 设置缓存（带过期时间）
     */
    void set(String key, Object value, long timeout, TimeUnit unit);
    
    /**
     * 获取缓存
     */
    Object get(String key);
    
    /**
     * 删除缓存
     */
    void delete(String key);
    
    /**
     * 判断缓存是否存在
     */
    boolean exists(String key);
    
    /**
     * 清空所有缓存
     */
    void clear();
    
    /**
     * 缓存键前缀
     */
    String STUDENT_GPA_PREFIX = "student:gpa:";
    String STUDENT_SCORES_PREFIX = "student:scores:";
    String STUDENT_WARNINGS_PREFIX = "student:warnings:";
    String STUDENT_DASHBOARD_PREFIX = "student:dashboard:";
    String STUDENT_STATISTICS_PREFIX = "student:statistics:";
    
    // 缓存过期时间常量
    long CACHE_TIMEOUT_HOUR = 1;
    long CACHE_TIMEOUT_DAY = 24;
}
