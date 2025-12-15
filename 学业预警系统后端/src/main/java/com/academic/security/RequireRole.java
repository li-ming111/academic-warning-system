package com.academic.security;

import java.lang.annotation.*;

/**
 * 角色权限注解
 * 用于标注API接口所需的角色权限
 * 
 * 角色值：
 * 1 = 学生
 * 2 = 教师
 * 3 = 管理员
 * 4 = 辅导员
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RequireRole {
    /**
     * 允许访问的角色ID数组
     * 
     * @return 角色ID数组
     */
    int[] value() default {1, 2, 3, 4};

    /**
     * 权限描述
     */
    String description() default "";
}
