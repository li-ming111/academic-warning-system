package com.academic.entity;

import lombok.Data;

@Data
public class Score {
    private Long id;
    private Long studentId;
    private Long courseId;
    private String semester;
    private Double scoreTotal;
    private Double scoreExam;
    private Double score平时;
    private String status;
    private String createdAt;
    private String updatedAt;
}
