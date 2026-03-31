package com.academic.service;

import com.academic.dto.StudentDashboardResponse;

public interface StudentService {
    
    /**
     * 获取学生仪表盘数据
     */
    StudentDashboardResponse getStudentDashboard(Long studentId);
    
    /**
     * 获取学生成绩
     */
    Object getStudentScores(Long studentId);
    
    /**
     * 获取学生预警信息
     */
    Object getStudentWarnings(Long studentId);
    
    /**
     * 获取学生信息
     */
    Object getStudentInfo(Long studentId);
}
