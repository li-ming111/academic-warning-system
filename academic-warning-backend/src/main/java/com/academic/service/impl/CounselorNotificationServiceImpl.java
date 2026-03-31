package com.academic.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.academic.entity.*;
import com.academic.mapper.*;
import com.academic.service.CounselorNotificationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.*;

@Slf4j
@Service
public class CounselorNotificationServiceImpl implements CounselorNotificationService {

    private final NotificationMapper notificationMapper;

    public CounselorNotificationServiceImpl(NotificationMapper notificationMapper) {
        this.notificationMapper = notificationMapper;
    }

    @Override
    public List<Map<String, Object>> getNotificationHistory(Long counselorId, int page, int size) {
        // 分页获取通知记录
        int offset = (page - 1) * size;
        QueryWrapper<Notification> qw = new QueryWrapper<>();
        qw.orderByDesc("created_at");
        qw.last("limit " + offset + ", " + size);
        List<Notification> notifications = notificationMapper.selectList(qw);
        
        List<Map<String, Object>> result = new ArrayList<>();
        for (Notification notification : notifications) {
            Map<String, Object> map = new HashMap<>();
            map.put("id", notification.getId());
            map.put("userId", notification.getUserId());
            map.put("userName", "学生-" + notification.getUserId());
            map.put("content", notification.getContent());
            map.put("status", notification.getIsRead() == 1 ? "read" : "unread");
            map.put("createdAt", notification.getCreatedAt());
            result.add(map);
        }
        return result;
    }

    @Override
    public List<Map<String, Object>> getNotificationTemplates() {
        // 返回常见通知模板
        List<Map<String, Object>> templates = new ArrayList<>();
        
        // 模板1: 学分不足预警
        Map<String, Object> template1 = new HashMap<>();
        template1.put("id", 1L);
        template1.put("name", "学分不足预警");
        template1.put("content", "同学，您的学分不足，请及时补修课程。");
        templates.add(template1);
        
        // 模板2: 成绩低迷预警
        Map<String, Object> template2 = new HashMap<>();
        template2.put("id", 2L);
        template2.put("name", "成绩低迷预警");
        template2.put("content", "同学，您最近成绩表现不佳，建议联系任课老师了解情况。");
        templates.add(template2);
        
        // 模板3: 学术辅导
        Map<String, Object> template3 = new HashMap<>();
        template3.put("id", 3L);
        template3.put("name", "学术辅导");
        template3.put("content", "我们为您安排了学术辅导，请准时参加。");
        templates.add(template3);
        
        return templates;
    }

    @Override
    public Long createTemplate(String name, String content) {
        // 创建新模板（模拟实现）
        Long templateId = System.currentTimeMillis();
        log.info("创建通知模板: {} - {}", name, content);
        return templateId;
    }

    @Override
    public Map<String, Object> getNotificationReadStatus(Long notificationId) {
        // 获取特定通知的阅读状态
        Notification notification = notificationMapper.selectById(notificationId);
        
        Map<String, Object> result = new HashMap<>();
        if (notification != null) {
            result.put("notificationId", notificationId);
            result.put("status", notification.getIsRead() == 1 ? "read" : "unread");
            result.put("readCount", notification.getIsRead() == 1 ? 1 : 0);
            result.put("unreadCount", notification.getIsRead() == 0 ? 1 : 0);
            result.put("createdAt", notification.getCreatedAt());
        }
        return result;
    }

    @Override
    public Long countWeeklyNotifications(Long counselorId) {
        // 统计本周发送的通知数（从周一到周日）
        LocalDateTime now = LocalDateTime.now();
        // 计算本周一的日期
        LocalDateTime weekStart = now.minusDays(now.getDayOfWeek().getValue() - 1);
        LocalDateTime weekEnd = weekStart.plusDays(7);
        
        QueryWrapper<Notification> qw = new QueryWrapper<>();
        qw.between("created_at", weekStart, weekEnd);
        return (long) notificationMapper.selectList(qw).size();
    }
}
