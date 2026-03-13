package com.academic.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("feedbacks")
public class Feedback extends BaseEntity {

    private Long teacherId;  // 教师ID

    private Long studentId;  // 学生ID

    private String category;  // 反馈分类：course/teacher/system/other

    private String content;  // 反馈内容

    @TableField(exist = false)
    private String status;  // 状态：pending/reviewing/replied/closed

    @TableField(exist = false)
    private String reply;  // 管理员回复

    private String replyContent;  // 回复内容

    private Integer rating;  // 满意度评分（1-5）

}
