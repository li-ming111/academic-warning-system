package com.academic.feign;

import com.academic.dto.TeacherDashboardResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * 教师服务Feign客户端
 */
@FeignClient(name = "teacher-service", url = "${feign.client.url.teacher-service:http://localhost:8084}")
public interface TeacherServiceFeignClient {
    
    @GetMapping("/api/teachers/dashboard/{teacherId}")
    TeacherDashboardResponse getTeacherDashboard(@PathVariable("teacherId") Long teacherId);
    
    @GetMapping("/api/teachers/courses/{teacherId}")
    Object getTeacherCourses(@PathVariable("teacherId") Long teacherId);
    
    @GetMapping("/api/teachers/students/{teacherId}")
    Object getTeacherStudents(@PathVariable("teacherId") Long teacherId);
}
