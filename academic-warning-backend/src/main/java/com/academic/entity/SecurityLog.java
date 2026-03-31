package com.academic.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("security_log")
public class SecurityLog {

    @TableId(type = IdType.AUTO)
    private Long id;

    @TableField("user_id")
    private Long userId;

    @TableField("ip_address")
    private String ipAddress;

    @TableField("login_time")
    private LocalDateTime loginTime;

    @TableField("action")
    private String action;

    @TableField("created_at")
    private LocalDateTime createdAt;

}
