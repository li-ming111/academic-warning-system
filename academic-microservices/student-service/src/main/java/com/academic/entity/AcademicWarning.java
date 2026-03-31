package com.academic.entity;

import lombok.Data;

@Data
public class AcademicWarning {
    private Long id;
    private Long studentId;
    private String warningLevel;
    private String title;
    private String description;
    private String status;
    private String createdAt;
    private String updatedAt;
}
