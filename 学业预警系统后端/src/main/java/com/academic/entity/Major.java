package com.academic.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("majors")
public class Major extends BaseEntity {

    private String code;

    private String name;

    private String shortName; // 专业简称

    private Long collegeId;

}
