package com.academic.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("security_log")
public class SecurityLog {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long userId;

    private String ipAddress;

    private LocalDateTime loginTime;

    private Integer isSuccessful;

    private LocalDateTime createdAt;

}
