package com.academic.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("counselor_profile")
public class CounselorProfile extends BaseEntity {

    private Long userId;

    private Long collegeId;

}
