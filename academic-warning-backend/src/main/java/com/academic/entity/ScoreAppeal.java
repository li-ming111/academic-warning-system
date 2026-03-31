package com.academic.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("score_appeals")
public class ScoreAppeal extends BaseEntity {

    private Long warningId;  // 预警ID

    private Long studentId;  // 学生ID

    private String reason;  // 申诉原因

    private String attachments;  // 附件

    private String status;  // 状态：pending/approved/rejected

}
