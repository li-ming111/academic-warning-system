package com.academic.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 帮扶评价实体类
 * 用于学生对帮扶计划的效果评价和反馈
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("assistance_evaluations")
public class AssistanceEvaluation extends BaseEntity {

    /**
     * 帮扶计划ID
     */
    private Long planId;

    /**
     * 学生ID
     */
    private Long studentId;

    /**
     * 帮扶计划评分：1-5
     */
    private Integer rating;

    /**
     * 有效性评分：1-5
     * 用于评价帮扶计划的实际效果
     */
    private Integer effectiveness;

    /**
     * 评价反馈文字
     */
    private String feedback;

}
