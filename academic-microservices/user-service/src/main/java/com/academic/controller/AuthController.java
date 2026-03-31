package com.academic.controller;

import com.academic.common.ApiResponse;
import com.academic.dto.LoginRequest;
import com.academic.dto.LoginResponse;
import com.academic.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

/**
 * Authentication Controller
 */
@Slf4j
@RestController
@RequestMapping("/users")
public class AuthController {

    private final UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    /**
     * User login
     */
    @PostMapping("/login")
    public ApiResponse<LoginResponse> login(@RequestBody LoginRequest request) {
        try {
            log.info("User login: {}", request.getUsername());
            LoginResponse response = userService.login(request);
            log.info("Login successful: {}", request.getUsername());
            return ApiResponse.success(response);
        } catch (Exception e) {
            log.error("Login failed: {}", e.getMessage());
            return ApiResponse.error(e.getMessage());
        }
    }

    /**
     * User register
     */
    @PostMapping("/register")
    public ApiResponse<String> register(@RequestBody Object request) {
        try {
            log.info("User register request");
            String result = userService.register(request);
            return ApiResponse.success(result);
        } catch (Exception e) {
            log.error("Register failed: {}", e.getMessage());
            return ApiResponse.error(e.getMessage());
        }
    }

    /**
     * Get user info
     */
    @GetMapping("/info/{userId}")
    public ApiResponse<Object> getUserInfo(@PathVariable Long userId) {
        try {
            log.info("Get user info: {}", userId);
            Object info = userService.getUserInfo(userId);
            return ApiResponse.success(info);
        } catch (Exception e) {
            log.error("Get user info failed: {}", e.getMessage());
            return ApiResponse.error(e.getMessage());
        }
    }
}
