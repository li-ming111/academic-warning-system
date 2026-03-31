package com.academic.dto;

import lombok.Data;

@Data
public class TeacherDashboardResponse {

    private Integer studentCount;  // 学生总数

    private Long warningCount;  // 预警总数

    private Long redWarnings;  // 红色预警数

    private Long yellowWarnings;  // 黄色预警数

}
