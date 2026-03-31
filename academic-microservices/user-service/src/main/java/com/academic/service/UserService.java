package com.academic.service;

import com.academic.dto.LoginRequest;
import com.academic.dto.LoginResponse;

public interface UserService {
    
    /**
     * 用户登录
     */
    LoginResponse login(LoginRequest request);
    
    /**
     * 用户注册
     */
    String register(Object request);
    
    /**
     * 获取用户信息
     */
    Object getUserInfo(Long userId);
}
