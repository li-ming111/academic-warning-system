package com.academic.dto;

import lombok.Data;

@Data
public class StudentDashboardResponse {
    private Long studentId;
    private String studentName;
    private String className;
    private String majorName;
    private Double gpa;
    private Integer warningCount;
    private String warningLevel;
    private Integer failedCoursesCount;
    private Integer totalCoursesCount;
    private Integer completedCredits;
    private Integer requiredCredits;
}
