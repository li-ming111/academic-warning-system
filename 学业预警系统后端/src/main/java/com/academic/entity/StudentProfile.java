package com.academic.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;
import lombok.EqualsAndHashCode;
import java.time.LocalDateTime;

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

    // 不映射到数据库表的字段
    @TableField(exist = false)
    private String email;  // 来自 users 表

    @TableField(exist = false)
    private String phone;  // 来自 users 表

    @TableField(exist = false)
    private String collegeName;  // 来自 colleges 表

    @TableField(exist = false)
    private String majorName;  // 来自 majors 表

}
