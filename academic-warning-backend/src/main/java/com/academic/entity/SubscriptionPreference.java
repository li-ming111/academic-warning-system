package com.academic.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableField;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * 订阅偏好实体
 */
@Data
@TableName("subscription_preferences")
public class SubscriptionPreference extends BaseEntity {
    
    @TableId
    private Long id;
    
    @TableField("student_id")
    private Long studentId;
    
    @TableField("subscribe_warnings")
    private Integer subscribeWarnings; // 1: 开启, 0: 关闭
    
    @TableField("subscribe_low")
    private Integer subscribeLow; // 1: 开启, 0: 关闭
    
    @TableField("subscribe_medium")
    private Integer subscribeMedium; // 1: 开启, 0: 关闭
    
    @TableField("subscribe_high")
    private Integer subscribeHigh; // 1: 开启, 0: 关闭
    
    @TableField("subscribe_assistance")
    private Integer subscribeAssistance; // 1: 开启, 0: 关闭
    
    @TableField("subscribe_system")
    private Integer subscribeSystem; // 1: 开启, 0: 关闭
    
    @TableField("push_email")
    private Integer pushEmail; // 1: 开启, 0: 关闭
    
    @TableField("push_sms")
    private Integer pushSms; // 1: 开启, 0: 关闭
    
    @TableField("push_app")
    private Integer pushApp; // 1: 开启, 0: 关闭
    
    @TableField("created_at")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;
    
    @TableField("updated_at")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatedAt;
}
