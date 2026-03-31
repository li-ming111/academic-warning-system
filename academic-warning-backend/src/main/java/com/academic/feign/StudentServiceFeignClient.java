package com.academic.feign;

import com.academic.dto.StudentDashboardResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * 学生服务Feign客户端
 */
@FeignClient(name = "student-service", url = "${feign.client.url.student-service:http://localhost:8083}")
public interface StudentServiceFeignClient {
    
    @GetMapping("/api/students/dashboard/{studentId}")
    StudentDashboardResponse getStudentDashboard(@PathVariable("studentId") Long studentId);
    
    @GetMapping("/api/students/scores/{studentId}")
    Object getStudentScores(@PathVariable("studentId") Long studentId);
    
    @GetMapping("/api/students/warnings/{studentId}")
    Object getStudentWarnings(@PathVariable("studentId") Long studentId);
}
