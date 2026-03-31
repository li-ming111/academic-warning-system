package com.academic.service;

import java.util.List;
import java.util.Map;

public interface CounselorNotificationService {

    /**
     * 获取通知记录
     */
    List<Map<String, Object>> getNotificationHistory(Long counselorId, int page, int size);

    /**
     * 获取通知模板列表
     */
    List<Map<String, Object>> getNotificationTemplates();

    /**
     * 创建通知模板
     */
    Long createTemplate(String name, String content);

    /**
     * 获取学生对通知的阅读情况
     */
    Map<String, Object> getNotificationReadStatus(Long notificationId);

    /**
     * 统计本周通知发送数
     */
    Long countWeeklyNotifications(Long counselorId);
}
