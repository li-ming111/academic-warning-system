package com.academic.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * 学生统计分析DTO
 */
@Data
public class StatisticsResponse {

    // GPA相关
    private BigDecimal currentGPA;
    private BigDecimal averageGPA;
    private BigDecimal highestGPA;
    private BigDecimal lowestGPA;

    // 成绩分布
    private Integer excellentCount;    // 优秀（>=85）
    private Integer goodCount;         // 良好（75-84）
    private Integer passCount;         // 及格（60-74）
    private Integer failCount;         // 不及格（<60）

    // 预警相关
    private Long highWarningCount;     // 红色预警数
    private Long mediumWarningCount;   // 黄色预警数
    private Long lowWarningCount;      // 绿色预警数
    private String warningTrend;       // 预警趋势：up/down/stable

    // 学期成绩趋势（用于图表）
    private List<Map<String, Object>> semesterTrend;

    // 课程成绩分布（用于图表）
    private List<Map<String, Object>> courseDistribution;

    // 各科成绩（用于表格）
    private List<CourseScoreInfo> courseScores;

    @Data
    public static class CourseScoreInfo {
        private String courseName;
        private BigDecimal score;
        private Integer credits;
        private String semester;
        private String trend;  // up/down/stable
    }

}
