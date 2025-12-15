package com.academic.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class StudentDashboardResponse {

    private Integer courseCount;  // 本学期课程数

    private BigDecimal gpa;  // GPA

    private Long warningCount;  // 预警数量

    private Long assistanceCount;  // 帮扶计划数

    private Long redWarnings;  // 红色预警数

    private Long yellowWarnings;  // 黄色预警数

}
