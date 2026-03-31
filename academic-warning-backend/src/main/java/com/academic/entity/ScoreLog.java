package com.academic.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import java.time.LocalDateTime;
import java.math.BigDecimal;

/**
 * 成绩修改日志表
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("score_logs")
public class ScoreLog extends BaseEntity {

    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 成绩ID
     */
    private Long scoreId;

    /**
     * 修改人ID
     */
    private Long modifiedBy;

    /**
     * 原分数
     */
    private BigDecimal oldScore;

    /**
     * 新分数
     */
    private BigDecimal newScore;

    /**
     * 修改原因
     */
    private String reason;

    /**
     * 创建时间
     */
    private LocalDateTime createdAt;
}
