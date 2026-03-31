package com.academic.entity;

import lombok.Data;

@Data
public class StudentProfile {
    private Long id;
    private Long userId;
    private String studentId;
    private String name;
    private String gender;
    private String phone;
    private String email;
    private Long classId;
    private Long majorId;
    private Long collegeId;
    private String admissionYear;
    private String status;
    private String createdAt;
    private String updatedAt;
}
