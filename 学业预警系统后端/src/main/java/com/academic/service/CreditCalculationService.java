package com.academic.service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * 学分计算与预测引擎
 */
public interface CreditCalculationService {

    /**
     * 计算学生已获学分
     */
    BigDecimal calculateCompletedCredits(Long studentId);

    /**
     * 计算学生需修学分
     */
    BigDecimal calculateRequiredCredits(Long studentId);

    /**
     * 预测学生学分达成情况
     */
    Map<String, Object> predictStudentCredits(Long studentId);

    /**
     * 计算班级的学分达标率
     */
    BigDecimal calculateClassCreditAchievementRate(Long classId);

    /**
     * 计算学生的绩点（GPA）
     */
    BigDecimal calculateStudentGPA(Long studentId);

    /**
     * 获取学生的学分不足课程
     */
    List<Map<String, Object>> getInsufficientCreditCourses(Long studentId);

    /**
     * 统计班级学分分布
     */
    Map<String, Object> getClassCreditDistribution(Long classId);

    /**
     * 识别学分风险学生
     */
    List<Map<String, Object>> identifyCreditRiskStudents(Long classId);

    /**
     * 推荐必修补充课程
     */
    List<Map<String, Object>> recommendCourses(Long studentId);

    /**
     * 获取学生的学分预测报告
     */
    Map<String, Object> generateCreditForecastReport(Long studentId);
}
