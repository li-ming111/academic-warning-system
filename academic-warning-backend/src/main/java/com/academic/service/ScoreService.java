package com.academic.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.academic.entity.Score;

import java.math.BigDecimal;
import java.util.List;

public interface ScoreService extends IService<Score> {

    /**
     * 获取学生成绩列表
     */
    List<Score> getStudentScores(Long studentId, String semester);

    /**
     * 获取学生挂科课程
     */
    List<Score> getFailedScores(Long studentId);

    /**
     * 计算学生GPA
     */
    BigDecimal calculateGPA(Long studentId);
    
    /**
     * 计算学生指定学期的GPA
     */
    BigDecimal calculateGPABySemester(Long studentId, String semester);

    /**
     * 计算学生平均分
     */
    BigDecimal calculateAverageScore(Long studentId);

    /**
     * 获取学生课程数
     */
    Integer getCourseCount(Long studentId, String semester);

    /**
     * 检测预警
     */
    void detectWarnings(Long studentId);

    /**
     * 获取课程信息
     */
    java.util.Map<String, Object> getCourseInfo(Long courseId);

}