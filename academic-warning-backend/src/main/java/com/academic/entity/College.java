package com.academic.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("colleges")
public class College extends BaseEntity {

    private String name;

    @TableField("student_count")
    private Integer studentCount;

    @TableField("teacher_count")
    private Integer teacherCount;

}
