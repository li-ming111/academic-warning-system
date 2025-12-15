package com.academic.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("communication_logs")
public class CommunicationLog extends BaseEntity {

    private Long studentId;  // 学生ID

    private Long teacherId;  // 教师ID

    private String content;  // 沟通内容

}
