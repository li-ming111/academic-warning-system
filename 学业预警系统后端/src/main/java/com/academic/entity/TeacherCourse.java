package com.academic.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 教师课程关联表
 * 用于维护教师与课程的多对多关系
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("teacher_course")
public class TeacherCourse extends BaseEntity {
    
    @TableId(type = IdType.AUTO)
    private Long id;
    
    /**
     * 教师档案ID
     */
    private Long teacherId;
    
    /**
     * 课程ID
     */
    private Long courseId;
}
