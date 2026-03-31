package com.academic.service.impl;

import com.academic.entity.Notification;
import com.academic.mapper.NotificationMapper;
import com.academic.service.NotificationService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 通知服务实现类
 */
@Slf4j
@Service
public class NotificationServiceImpl implements NotificationService {
    
    private final NotificationMapper notificationMapper;
    
    public NotificationServiceImpl(NotificationMapper notificationMapper) {
        this.notificationMapper = notificationMapper;
    }
    
    @Override
    public void createNotification(Notification notification) {
        if (notification.getCreatedAt() == null) {
            notification.setCreatedAt(LocalDateTime.now());
        }
        notificationMapper.insert(notification);
        log.info("通知已创建: userId={}", notification.getUserId());
    }
    
    @Override
    public void sendWarningNotification(Long userId, Long warningId, String title, String content) {
        Notification notification = new Notification();
        notification.setUserId(userId);
        notification.setContent(content);
        notification.setCreatedAt(LocalDateTime.now());
        
        createNotification(notification);
    }
    
    @Override
    public void sendAppealResultNotification(Long userId, Long appealId, String title, String content) {
        Notification notification = new Notification();
        notification.setUserId(userId);
        notification.setContent(content);
        notification.setCreatedAt(LocalDateTime.now());
        
        createNotification(notification);
    }
    
    @Override
    public void sendGradeUpdateNotification(Long userId, Long gradeId, String courseName, String grade) {
        Notification notification = new Notification();
        notification.setUserId(userId);
        notification.setContent("您的课程 " + courseName + " 成绩已更新为 " + grade);
        notification.setCreatedAt(LocalDateTime.now());
        
        createNotification(notification);
    }
    
    @Override
    public void sendAssistancePlanNotification(Long userId, Long planId, String title, String content) {
        Notification notification = new Notification();
        notification.setUserId(userId);
        notification.setContent(content);
        notification.setCreatedAt(LocalDateTime.now());
        
        createNotification(notification);
    }
    
    @Override
    public List<Notification> getUserNotifications(Long userId) {
        return notificationMapper.selectUnreadNotifications(userId);
    }
    
    @Override
    public Long getUnreadCount(Long userId) {
        return 0L;
    }
    
    @Override
    public void markAsRead(Long notificationId) {
    }
    
    @Override
    public void markBatchAsRead(Long userId) {
    }
    
    @Override
    public void deleteNotification(Long notificationId) {
        notificationMapper.deleteById(notificationId);
    }
}
