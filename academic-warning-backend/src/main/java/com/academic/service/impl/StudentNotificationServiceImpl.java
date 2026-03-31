package com.academic.service.impl;

import com.academic.entity.Notification;
import com.academic.entity.SubscriptionPreference;
import com.academic.mapper.NotificationMapper;
import com.academic.mapper.SubscriptionPreferenceMapper;
import com.academic.service.StudentNotificationService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.*;

/**
 * 学生通知服务实现
 */
@Service
@RequiredArgsConstructor
public class StudentNotificationServiceImpl implements StudentNotificationService {
    
    private final NotificationMapper notificationMapper;
    private final SubscriptionPreferenceMapper subscriptionPreferenceMapper;
    
    // ========== 通知管理实现 ==========
    
    @Override
    public List<Notification> getUnreadNotifications(Long userId) {
        return notificationMapper.selectUnreadNotifications(userId);
    }
    
    @Override
    public Map<String, Object> getNotifications(Long userId, int page, int pageSize) {
        // 计算偏移量
        int offset = (page - 1) * pageSize;
        
        // 获取总数
        QueryWrapper<Notification> countQw = new QueryWrapper<>();
        countQw.eq("user_id", userId)
               .eq("is_deleted", 0);
        long total = notificationMapper.selectCount(countQw);
        
        // 使用自定义 SQL 查询，避免查询 title 列
        List<Notification> notifications = notificationMapper.selectNotificationsByUser(userId, pageSize, offset);
        
        // 构建响应
        Map<String, Object> result = new HashMap<>();
        result.put("total", total);
        result.put("page", page);
        result.put("pageSize", pageSize);
        result.put("pages", (total + pageSize - 1) / pageSize);
        result.put("data", notifications);
        return result;
    }
    
    @Override
    public boolean markAsRead(Long notificationId) {
        UpdateWrapper<Notification> uw = new UpdateWrapper<>();
        uw.eq("id", notificationId)
          .set("is_read", 1)
          .set("updated_at", LocalDateTime.now());
        return notificationMapper.update(null, uw) > 0;
    }
    
    @Override
    public boolean markMultipleAsRead(List<Long> notificationIds) {
        if (notificationIds == null || notificationIds.isEmpty()) {
            return false;
        }
        UpdateWrapper<Notification> uw = new UpdateWrapper<>();
        uw.in("id", notificationIds)
          .set("is_read", 1)
          .set("updated_at", LocalDateTime.now());
        return notificationMapper.update(null, uw) > 0;
    }
    
    @Override
    public boolean deleteNotification(Long notificationId) {
        // 逻辑删除
        UpdateWrapper<Notification> uw = new UpdateWrapper<>();
        uw.eq("id", notificationId)
          .set("is_deleted", 1)
          .set("updated_at", LocalDateTime.now());
        return notificationMapper.update(null, uw) > 0;
    }
    
    @Override
    public int getUnreadCount(Long userId) {
        QueryWrapper<Notification> qw = new QueryWrapper<>();
        qw.eq("user_id", userId)
          .eq("is_read", 0)
          .eq("is_deleted", 0);
        return Math.toIntExact(notificationMapper.selectCount(qw));
    }
    
    @Override
    public boolean clearReadNotifications(Long userId) {
        UpdateWrapper<Notification> uw = new UpdateWrapper<>();
        uw.eq("user_id", userId)
          .eq("is_read", 1)
          .set("is_deleted", 1)
          .set("updated_at", LocalDateTime.now());
        return notificationMapper.update(null, uw) > 0;
    }
    
    // ========== 订阅管理实现 ==========
    
    @Override
    public SubscriptionPreference getSubscriptionPreferences(Long studentId) {
        QueryWrapper<SubscriptionPreference> qw = new QueryWrapper<>();
        qw.eq("student_id", studentId);
        SubscriptionPreference pref = subscriptionPreferenceMapper.selectOne(qw);
        
        // 如果不存在则创建默认偏好
        if (pref == null) {
            pref = new SubscriptionPreference();
            pref.setStudentId(studentId);
            pref.setSubscribeWarnings(1);
            pref.setSubscribeLow(1);
            pref.setSubscribeMedium(1);
            pref.setSubscribeHigh(1);
            pref.setSubscribeAssistance(1);
            pref.setSubscribeSystem(1);
            pref.setPushEmail(1);
            pref.setPushSms(0);
            pref.setPushApp(1);
            pref.setCreatedAt(LocalDateTime.now());
            pref.setUpdatedAt(LocalDateTime.now());
            subscriptionPreferenceMapper.insert(pref);
        }
        return pref;
    }
    
    @Override
    public boolean updateSubscriptionPreferences(SubscriptionPreference preference) {
        preference.setUpdatedAt(LocalDateTime.now());
        return subscriptionPreferenceMapper.updateById(preference) > 0;
    }
    
    @Override
    public boolean subscribeWarningLevel(Long studentId, String level) {
        SubscriptionPreference pref = getSubscriptionPreferences(studentId);
        switch (level.toLowerCase()) {
            case "low":
                pref.setSubscribeLow(1);
                break;
            case "medium":
                pref.setSubscribeMedium(1);
                break;
            case "high":
                pref.setSubscribeHigh(1);
                break;
            default:
                return false;
        }
        return updateSubscriptionPreferences(pref);
    }
    
    @Override
    public boolean unsubscribeWarningLevel(Long studentId, String level) {
        SubscriptionPreference pref = getSubscriptionPreferences(studentId);
        switch (level.toLowerCase()) {
            case "low":
                pref.setSubscribeLow(0);
                break;
            case "medium":
                pref.setSubscribeMedium(0);
                break;
            case "high":
                pref.setSubscribeHigh(0);
                break;
            default:
                return false;
        }
        return updateSubscriptionPreferences(pref);
    }
    
    @Override
    public boolean setPushChannel(Long studentId, String channel, boolean enabled) {
        SubscriptionPreference pref = getSubscriptionPreferences(studentId);
        int status = enabled ? 1 : 0;
        
        switch (channel.toLowerCase()) {
            case "email":
                pref.setPushEmail(status);
                break;
            case "sms":
                pref.setPushSms(status);
                break;
            case "app":
                pref.setPushApp(status);
                break;
            default:
                return false;
        }
        return updateSubscriptionPreferences(pref);
    }
    
    // ========== 通知创建实现 ==========
    
    @Override
    public void createWarningNotification(Long studentId, Long warningId, String warningLevel) {
        Notification notification = new Notification();
        notification.setUserId(studentId);
        notification.setWarningId(warningId);
        notification.setType("warning");
        notification.setContent("您有一条" + warningLevel + "级学业预警，请及时查看");
        notification.setPushChannel("app");
        notification.setIsRead(0);
        notification.setIsDeleted(0);
        notification.setCreatedAt(LocalDateTime.now());
        notification.setUpdatedAt(LocalDateTime.now());
        
        // 检查订阅设置
        SubscriptionPreference pref = getSubscriptionPreferences(studentId);
        if (pref.getSubscribeWarnings() == 0) {
            return; // 用户未订阅预警
        }
        
        notificationMapper.insert(notification);
    }
    
    @Override
    public void createAssistanceNotification(Long studentId, Long planId, String message) {
        Notification notification = new Notification();
        notification.setUserId(studentId);
        notification.setType("plan_update");
        notification.setContent(message);
        notification.setPushChannel("app");
        notification.setIsRead(0);
        notification.setIsDeleted(0);
        notification.setCreatedAt(LocalDateTime.now());
        notification.setUpdatedAt(LocalDateTime.now());
        
        SubscriptionPreference pref = getSubscriptionPreferences(studentId);
        if (pref.getSubscribeAssistance() == 0) {
            return;
        }
        
        notificationMapper.insert(notification);
    }
    
    @Override
    public void sendSystemMessage(Long userId, String title, String content) {
        Notification notification = new Notification();
        notification.setUserId(userId);
        notification.setType("system_message");
        notification.setContent(content);
        notification.setPushChannel("app");
        notification.setIsRead(0);
        notification.setIsDeleted(0);
        notification.setCreatedAt(LocalDateTime.now());
        notification.setUpdatedAt(LocalDateTime.now());
        
        notificationMapper.insert(notification);
    }
}
