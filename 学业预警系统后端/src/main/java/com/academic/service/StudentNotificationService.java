package com.academic.service;

import com.academic.entity.Notification;
import com.academic.entity.SubscriptionPreference;
import java.util.List;
import java.util.Map;

/**
 * 学生通知服务接口
 */
public interface StudentNotificationService {
    
    // ========== 通知管理 ==========
    
    /**
     * 获取用户未读通知列表
     */
    List<Notification> getUnreadNotifications(Long userId);
    
    /**
     * 获取用户所有通知
     * @param userId 用户ID
     * @param page 页码（从1开始）
     * @param pageSize 每页大小
     */
    Map<String, Object> getNotifications(Long userId, int page, int pageSize);
    
    /**
     * 标记单个通知为已读
     */
    boolean markAsRead(Long notificationId);
    
    /**
     * 标记多个通知为已读
     */
    boolean markMultipleAsRead(List<Long> notificationIds);
    
    /**
     * 删除通知
     */
    boolean deleteNotification(Long notificationId);
    
    /**
     * 获取未读通知数量
     */
    int getUnreadCount(Long userId);
    
    /**
     * 清空所有已读通知
     */
    boolean clearReadNotifications(Long userId);
    
    // ========== 订阅管理 ==========
    
    /**
     * 获取学生订阅偏好
     */
    SubscriptionPreference getSubscriptionPreferences(Long studentId);
    
    /**
     * 更新订阅偏好
     */
    boolean updateSubscriptionPreferences(SubscriptionPreference preference);
    
    /**
     * 订阅特定预警等级
     */
    boolean subscribeWarningLevel(Long studentId, String level);
    
    /**
     * 取消订阅特定预警等级
     */
    boolean unsubscribeWarningLevel(Long studentId, String level);
    
    /**
     * 设置推送渠道
     */
    boolean setPushChannel(Long studentId, String channel, boolean enabled);
    
    // ========== 通知创建（系统调用） ==========
    
    /**
     * 创建预警通知
     */
    void createWarningNotification(Long studentId, Long warningId, String warningLevel);
    
    /**
     * 创建帮扶计划更新通知
     */
    void createAssistanceNotification(Long studentId, Long planId, String message);
    
    /**
     * 发送系统消息到用户
     */
    void sendSystemMessage(Long userId, String title, String content);
}
