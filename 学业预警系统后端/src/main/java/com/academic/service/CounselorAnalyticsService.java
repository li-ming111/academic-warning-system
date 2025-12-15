package com.academic.service;

import java.util.List;
import java.util.Map;

public interface CounselorAnalyticsService {

    /**
     * 班级学分不足率统计
     */
    Map<String, Object> getCreditInsufficientRate(Long counselorId);

    /**
     * 预警级别分布
     */
    Map<String, Object> getWarningLevelDistribution(Long counselorId);

    /**
     * 预警处理效率
     */
    Map<String, Object> getWarningHandlingEfficiency(Long counselorId);

    /**
     * 学生学分达标率排名（班级排名）
     */
    List<Map<String, Object>> getClassCreditAchievementRanking(Long counselorId);

    /**
     * 获取预警趋势（近6个月）
     */
    List<Map<String, Object>> getWarningTrend(Long counselorId);

    /**
     * 获取帮扶计划完成率
     */
    Map<String, Object> getAssistancePlanCompletionRate(Long counselorId);
}
