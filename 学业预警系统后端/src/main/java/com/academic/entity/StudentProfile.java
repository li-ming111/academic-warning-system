package com.academic.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("student_profile")
public class StudentProfile extends BaseEntity {

    private Long userId;

    private String studentId;

    private String name;

    private Long collegeId;

    private Long majorId;

    private Long classId;

    private Integer privacyLevel;  // 0=公开，1=学院内可见，2=仅教师可见，3=仅本人与管理员

}
