package com.academic.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("classes")
public class Class extends BaseEntity {

    private String name;  // 班级名称

    private Long collegeId;  // 学院ID

    private Long majorId;  // 专业ID
    
    private Long teacherId;  // 授课教师ID

    private Long counselorId;  // 辅导员ID

}
