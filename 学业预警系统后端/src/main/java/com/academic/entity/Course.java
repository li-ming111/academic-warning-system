package com.academic.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("courses")
public class Course extends BaseEntity {

    private String code;  // 课程代码

    private String name;

    private java.math.BigDecimal credits;

    private String type;  // 必修，选修

    private Long moduleId;  // 模块ID（选修课）

    private String scoreTemplate;  // exam等

}
