package com.academic.dto;

import lombok.Data;

@Data
public class AdminDashboardResponse {

    private Long totalColleges;  // 学院总数

    private Long totalMajors;  // 专业总数

    private Long totalUsers;  // 用户总数

    private Long totalStudents;  // 学生总数

    private Long totalTeachers;  // 教师总数

    private Long totalWarnings;  // 预警总数

}
