package com.academic.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

/**
 * 班级管理申请
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName("class_management_request")
public class ClassManagementRequest {
    
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    
    @TableField("teacher_id")
    private Long teacherId;  // 教师ID（如果是教师申请）
    
    @TableField("counselor_id")
    private Long counselorId;  // 辅导员ID（如果是辅导员申请）
    
    @TableField("class_id")
    private Long classId;
    
    @TableField("user_type")
    private String userType;  // 'teacher' 或 'counselor'
    
    @TableField("status")
    private String status; // pending, approved, rejected
    
    @TableField("reason")
    private String reason;
    
    @TableField("reject_reason")
    private String rejectReason;
    
    @TableField("created_at")
    private LocalDateTime createdAt;
    
    @TableField("updated_at")
    private LocalDateTime updatedAt;
}
