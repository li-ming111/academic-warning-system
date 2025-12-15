package com.academic.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.academic.entity.User;
import com.academic.dto.LoginRequest;
import com.academic.dto.LoginResponse;

public interface UserService extends IService<User> {

    /**
     * 用户登录
     */
    LoginResponse login(LoginRequest request);

    /**
     * 根据用户名查询用户
     */
    User getUserByUsername(String username);

    /**
     * 验证密码
     */
    boolean verifyPassword(String rawPassword, String encodedPassword);

}
