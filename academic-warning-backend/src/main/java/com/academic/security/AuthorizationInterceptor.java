package com.academic.security;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.servlet.HandlerInterceptor;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * 权限与角色拦截器
 */
@Slf4j
public class AuthorizationInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String requestPath = request.getRequestURI();
        String method = request.getMethod();
    
        // 获取当前用户的角色信息（从为token或session获取）
        String userRole = (String) request.getAttribute("userRole");
        Long userId = (Long) request.getAttribute("userId");

        log.debug("请求路径: {} [{}], 用户角色: {}", requestPath, method, userRole);

        // 权限规则定义
        // 学生端接口：/api/student/*, /api/auth/login, /api/auth/register/student
        if (requestPath.startsWith("/api/student/")) {
            if (!"1".equals(userRole)) { // 1 = 学生
                response.setStatus(403);
                response.getWriter().write("{\"code\": 403, \"message\": \"访问被拒绝：只有学生可以访问\"}");
                return false;
            }
        }

        // 教师端接口：/api/teacher/*, /api/auth/register/teacher
        if (requestPath.startsWith("/api/teacher/")) {
            if (!"2".equals(userRole)) { // 2 = 教师
                response.setStatus(403);
                response.getWriter().write("{\"code\": 403, \"message\": \"访问被拒绝：只有教师可以访问\"}");
                return false;
            }
        }

        // 辅导员端接口：/api/counselor/*
        if (requestPath.startsWith("/api/counselor/")) {
            if (!"4".equals(userRole)) { // 4 = 辅导员
                response.setStatus(403);
                response.getWriter().write("{\"code\": 403, \"message\": \"访问被拒绝：只有辅导员可以访问\"}");
                return false;
            }
        }

        // 管理员端接口：/api/admin/*
        if (requestPath.startsWith("/api/admin/")) {
            if (!"3".equals(userRole)) { // 3 = 管理员
                response.setStatus(403);
                response.getWriter().write("{\"code\": 403, \"message\": \"访问被拒绝：只有管理员可以访问\"}");
                return false;
            }
        }

        // 数据范围限制
        // 教师只能看自己教的课程和班级的学生
        if (requestPath.startsWith("/api/teacher/scores") && "2".equals(userRole)) {
            String courseIdParam = request.getParameter("courseId");
            // TODO: 校验该教师是否教这门课程
        }

        // 辅导员只能看自己学院的学生和班级
        if (requestPath.startsWith("/api/counselor/students") && "4".equals(userRole)) {
            // TODO: 校验学生是否属于该辅导员所在学院
        }

        return true;
    }
}
