package com.academic.service;

import com.academic.entity.Notification;

import java.util.List;

/**
 * 通知服务接口
 */
public interface NotificationService {
    
    /**
     * 创建通知
     */
    void createNotification(Notification notification);
    
    /**
     * 发送预警通知
     */
    void sendWarningNotification(Long userId, Long warningId, String title, String content);
    
    /**
     * 发送申诉结果通知
     */
    void sendAppealResultNotification(Long userId, Long appealId, String title, String content);
    
    /**
     * 发送成绩更新通知
     */
    void sendGradeUpdateNotification(Long userId, Long gradeId, String courseName, String grade);
    
    /**
     * 发送帮扶计划通知
     */
    void sendAssistancePlanNotification(Long userId, Long planId, String title, String content);
    
    /**
     * 获取用户的通知列表
     */
    List<Notification> getUserNotifications(Long userId);
    
    /**
     * 获取用户的未读通知数
     */
    Long getUnreadCount(Long userId);
    
    /**
     * 标记通知为已读
     */
    void markAsRead(Long notificationId);
    
    /**
     * 批量标记通知为已读
     */
    void markBatchAsRead(Long userId);
    
    /**
     * 删除通知
     */
    void deleteNotification(Long notificationId);
}
