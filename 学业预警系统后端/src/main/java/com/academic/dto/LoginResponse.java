package com.academic.dto;

import lombok.Data;

@Data
public class LoginResponse {

    private Long userId;

    private String username;

    private String role;

    private String token;
    
    private Long studentId;  // 学生ID
    
    private Long teacherId;  // 教师ID
    
    private Long counselorId;  // 辅导员ID
    
    private Long adminId;  // 管理员ID

}
