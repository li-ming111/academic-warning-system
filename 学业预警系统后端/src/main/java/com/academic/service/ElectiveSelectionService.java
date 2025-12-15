package com.academic.service;

import java.util.List;
import java.util.Map;

/**
 * 选课管理服务
 */
public interface ElectiveSelectionService {

    /**
     * 学生选修课程
     */
    void selectCourse(Long studentId, Long courseId);

    /**
     * 学生取消选修课程
     */
    void cancelCourse(Long studentId, Long courseId);

    /**
     * 获取学生已选课程列表
     */
    List<Map<String, Object>> getStudentSelectedCourses(Long studentId);

    /**
     * 获取学生可选课程列表（按模块）
     */
    List<Map<String, Object>> getAvailableCourses(Long studentId);

    /**
     * 获取课程的选修学生列表
     */
    List<Map<String, Object>> getCoursestudents(Long courseId);

    /**
     * 检查学生是否已选课程
     */
    Boolean isCourseSelected(Long studentId, Long courseId);

    /**
     * 获取学生的选修课统计
     */
    Map<String, Object> getStudentElectiveStatistics(Long studentId);

    /**
     * 按模块获取可选课程
     */
    List<Map<String, Object>> getCoursesByModule(Long moduleId);
}
