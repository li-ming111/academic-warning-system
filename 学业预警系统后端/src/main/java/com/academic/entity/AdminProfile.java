package com.academic.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("admin_profile")
public class AdminProfile extends BaseEntity {

    private Long userId;

    private String name;

    private String phone;

    private String email;

    private String department;

}
