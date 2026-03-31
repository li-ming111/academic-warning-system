package com.academic.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableField;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * 通知实体
 */
@Data
@TableName("notifications")
public class Notification extends BaseEntity {
    
    @TableId
    private Long id;
    
    @TableField("user_id")
    private Long userId;
    
    @TableField("content")
    private String content;
    
    @TableField("type")
    private String type; // warning, plan_update, system_message 等
    
    @TableField("warning_id")
    private Long warningId; // 关联的预警ID
    
    @TableField("push_channel")
    private String pushChannel; // app, email, sms
    
    @TableField("is_read")
    private Integer isRead; // 0: 未读, 1: 已读
    
    @TableField("is_deleted")
    private Integer isDeleted; // 0: 正常, 1: 已删除
    
    @TableField("created_at")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;
    
    @TableField("updated_at")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatedAt;
}