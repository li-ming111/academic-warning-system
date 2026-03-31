package com.academic.controller;

import com.academic.common.ApiResponse;
import com.academic.dto.StudentDashboardResponse;
import com.academic.service.StudentService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

/**
 * Student Controller
 */
@Slf4j
@RestController
@RequestMapping("/students")
public class StudentController {

    private final StudentService studentService;

    public StudentController(StudentService studentService) {
        this.studentService = studentService;
    }

    /**
     * Get student dashboard data
     */
    @GetMapping("/dashboard/{studentId}")
    public ApiResponse<StudentDashboardResponse> getStudentDashboard(@PathVariable Long studentId) {
        try {
            log.info("Get student dashboard: {}", studentId);
            StudentDashboardResponse dashboard = studentService.getStudentDashboard(studentId);
            log.info("Get student dashboard successful: {}", studentId);
            return ApiResponse.success(dashboard);
        } catch (Exception e) {
            log.error("Get student dashboard failed: {}", e.getMessage());
            return ApiResponse.error(e.getMessage());
        }
    }

    /**
     * Get student scores
     */
    @GetMapping("/scores/{studentId}")
    public ApiResponse<Object> getStudentScores(@PathVariable Long studentId) {
        try {
            log.info("Get student scores: {}", studentId);
            Object scores = studentService.getStudentScores(studentId);
            return ApiResponse.success(scores);
        } catch (Exception e) {
            log.error("Get student scores failed: {}", e.getMessage());
            return ApiResponse.error(e.getMessage());
        }
    }

    /**
     * Get student warnings
     */
    @GetMapping("/warnings/{studentId}")
    public ApiResponse<Object> getStudentWarnings(@PathVariable Long studentId) {
        try {
            log.info("Get student warnings: {}", studentId);
            Object warnings = studentService.getStudentWarnings(studentId);
            return ApiResponse.success(warnings);
        } catch (Exception e) {
            log.error("Get student warnings failed: {}", e.getMessage());
            return ApiResponse.error(e.getMessage());
        }
    }

    /**
     * Get student info
     */
    @GetMapping("/info/{studentId}")
    public ApiResponse<Object> getStudentInfo(@PathVariable Long studentId) {
        try {
            log.info("Get student info: {}", studentId);
            Object info = studentService.getStudentInfo(studentId);
            return ApiResponse.success(info);
        } catch (Exception e) {
            log.error("Get student info failed: {}", e.getMessage());
            return ApiResponse.error(e.getMessage());
        }
    }
}
