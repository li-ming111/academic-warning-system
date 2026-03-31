package com.academic.service.impl;

import com.academic.entity.Notification;
import com.academic.mapper.NotificationMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import org.springframework.beans.factory.annotation.Autowired;
import java.time.LocalDateTime;

/**
 * 活动记录服务
 */
@Slf4j
@Service
public class ActivityService {

    @Autowired
    private NotificationMapper notificationMapper;

    /**
     * 记录系统动态
     * @param content 动态内容
     * @param type 动态类型
     */
    public void recordActivity(String content, String type) {
        try {
            Notification notification = new Notification();
            notification.setUserId(0L); // 0表示系统通知
            notification.setContent(content);
            notification.setType(type);
            notification.setIsRead(0);
            notification.setIsDeleted(0);
            notification.setCreatedAt(LocalDateTime.now());
            notification.setUpdatedAt(LocalDateTime.now());
            
            notificationMapper.insert(notification);
            log.info("记录系统动态: {}", content);
        } catch (Exception e) {
            log.error("记录系统动态失败", e);
        }
    }

    /**
     * 记录预警动态
     * @param studentName 学生姓名
     * @param warningLevel 预警等级
     */
    public void recordWarningActivity(String studentName, String warningLevel) {
        String content = "学生" + studentName + "新增" + warningLevel + "预警";
        recordActivity(content, "warning");
    }

    /**
     * 记录成绩修改动态
     * @param teacherName 教师姓名
     * @param className 班级名称
     */
    public void recordScoreActivity(String teacherName, String className) {
        String content = teacherName + "老师修改了" + className + "的成绩";
        recordActivity(content, "score_update");
    }

    /**
     * 记录用户注册动态
     * @param userName 用户名
     * @param roleName 角色名称
     */
    public void recordRegisterActivity(String userName, String roleName) {
        String content = "用户" + userName + "注册为" + roleName;
        recordActivity(content, "user_register");
    }

    /**
     * 记录系统通知动态
     * @param message 通知内容
     */
    public void recordSystemActivity(String message) {
        recordActivity(message, "system_message");
    }
}
