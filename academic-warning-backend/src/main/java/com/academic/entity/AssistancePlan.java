package com.academic.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("assistance_plans")
public class AssistancePlan extends BaseEntity {

    private Long studentId;

    private Long warningId;
    
    private String title;  // 计划标题
    
    private String target;  // 目标
    
    private String measures;  // 帮扶措施
    
    private String description;  // 描述

    private BigDecimal progressPercentage;

    private String status;  // initiated/in_progress/completed/cancelled
    
    private LocalDateTime completedAt;  // 完成时间

}
