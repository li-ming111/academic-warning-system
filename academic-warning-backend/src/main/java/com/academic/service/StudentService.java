package com.academic.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.academic.entity.StudentProfile;
import com.academic.dto.StudentRegisterRequest;
import com.academic.dto.StudentDashboardResponse;

public interface StudentService extends IService<StudentProfile> {

    /**
     * 学生注册
     */
    void register(StudentRegisterRequest request);

    /**
     * 根据学号查询学生
     */
    StudentProfile getByStudentId(String studentId);
    
    /**
     * 根据用户ID查询学生
     */
    StudentProfile getByUserId(Long userId);

    /**
     * 获取学生GPA
     */
    java.math.BigDecimal getStudentGPA(Long studentId);

    /**
     * 根据班级ID查询班级的所有学生
     */
    java.util.List<StudentProfile> getStudentsByClassId(Long classId);

    /**
     * 获取学生仪表盘数据
     */
    StudentDashboardResponse getStudentDashboard(Long studentId);

}
