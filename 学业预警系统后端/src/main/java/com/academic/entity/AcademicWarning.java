package com.academic.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("academic_warnings")
public class AcademicWarning extends BaseEntity {

    @TableField("student_id")
    private Long studentId;
    
    @TableField("college_id")
    private Long collegeId;  // 学院ID
    
    @TableField("class_id")
    private Long classId;  // 班级ID

    private String warningLevel;  // red/yellow/blue

    private String title;  // 预警标题

    private String description;

    private String status;  // pending, confirmed, processed, closed

    private String appealStatus;  // none, pending, under_appeal, resolved, rejected

    @TableField(exist = false)
    private String courseName;  // 课程名称（不存储到数据库）

}
