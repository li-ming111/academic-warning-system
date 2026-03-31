package com.academic.service;

import java.util.List;
import java.util.Map;

public interface CounselorScoreService {

    /**
     * 获取班级学生的成绩列表
     */
    List<Map<String, Object>> getClassScores(Long counselorId, Long classId);

    /**
     * 获取特定课程的成绩分布
     */
    Map<String, Object> getCourseScoreDistribution(Long courseId);

    /**
     * 识别低分学生（成绩低于60分）
     */
    List<Map<String, Object>> getLowScoreStudents(Long counselorId);

    /**
     * 获取学生成绩变化趋势
     */
    List<Map<String, Object>> getScoreTrend(Long studentId);

    /**
     * 获取班级学分统计
     */
    Map<String, Object> getClassCreditStatistics(Long counselorId, Long classId);
}
