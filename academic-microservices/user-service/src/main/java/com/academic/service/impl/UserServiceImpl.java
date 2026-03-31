package com.academic.service.impl;

import com.academic.dto.LoginRequest;
import com.academic.dto.LoginResponse;
import com.academic.entity.User;
import com.academic.mapper.UserMapper;
import com.academic.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class UserServiceImpl implements UserService {

    private final UserMapper userMapper;

    public UserServiceImpl(UserMapper userMapper) {
        this.userMapper = userMapper;
    }

    @Override
    public LoginResponse login(LoginRequest request) {
        log.info("用户登录: username={}", request.getUsername());
        
        // 从数据库查询用户信息
        User user = userMapper.selectByUsername(request.getUsername());
        if (user == null) {
            log.warn("用户不存在: username={}", request.getUsername());
            throw new RuntimeException("用户名或密码错误");
        }
        
        // 验证密码（实际应该使用加密密码验证）
        if (!user.getPassword().equals(request.getPassword())) {
            log.warn("密码错误: username={}", request.getUsername());
            throw new RuntimeException("用户名或密码错误");
        }
        
        // 构建登录响应
        LoginResponse response = new LoginResponse();
        response.setToken("token-" + System.currentTimeMillis());
        response.setUserId(user.getId());
        response.setUsername(user.getUsername());
        response.setName(user.getName());
        response.setRole(user.getRole());
        response.setEmail(user.getEmail());
        response.setPhone(user.getPhone());
        
        return response;
    }

    @Override
    public String register(Object request) {
        log.info("用户注册");
        // 实际应该验证并保存用户信息
        return "注册成功";
    }

    @Override
    public Object getUserInfo(Long userId) {
        log.info("获取用户信息: userId={}", userId);
        // 从数据库查询用户信息
        User user = userMapper.selectById(userId);
        return user;
    }
}
