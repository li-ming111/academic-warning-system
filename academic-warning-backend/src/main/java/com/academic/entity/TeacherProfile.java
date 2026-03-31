package com.academic.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("teacher_profile")
public class TeacherProfile extends BaseEntity {

    private Long userId;

    private Long collegeId;

    private String title;

}
