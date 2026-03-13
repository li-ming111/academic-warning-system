package com.academic.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("scores")
public class Score extends BaseEntity {

    private Long studentId;

    private Long courseId;

    private String semester;

    private BigDecimal regularScore;

    private BigDecimal finalScore;

    private BigDecimal scoreTotal;

    private BigDecimal gradePoint;

}
