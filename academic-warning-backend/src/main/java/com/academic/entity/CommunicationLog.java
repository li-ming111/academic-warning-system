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

    private Integer status;  // 消息状态：0=未读，1=已读

    private String reply;  // 教师回复内容

}
