package com.academic.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import java.time.LocalDateTime;

/**
 * Warning Rule Entity
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("warning_rules")
public class WarningRule {
    
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private String name;
    private String description;
    private String condition;
    private String level;
    private Integer status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
