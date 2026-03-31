package com.academic.service;

import com.academic.dto.StatisticsResponse;

public interface StatisticsService {

    /**
     * 获取学生统计分析数据
     */
    StatisticsResponse getStudentStatistics(Long studentId);

    /**
     * 获取成绩分布
     */
    StatisticsResponse.CourseScoreInfo[] getScoreDistribution(Long studentId);

    /**
     * 获取学期成绩趋势
     */
    java.util.List<java.util.Map<String, Object>> getSemesterTrend(Long studentId);

    /**
     * 计算成绩等级
     */
    String calculateGrade(java.math.BigDecimal score);

}
