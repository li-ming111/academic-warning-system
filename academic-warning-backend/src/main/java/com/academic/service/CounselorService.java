package com.academic.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.academic.entity.*;
import java.util.List;

public interface CounselorService extends IService<CounselorProfile> {

    /**
     * 根据用户ID获取辅导员档案
     */
    CounselorProfile getByUserId(Long userId);

    /**
     * 辅导员注册
     */
    Long registerCounselor(User user, CounselorProfile profile);

    /**
     * 获取辅导员学院的学生总数
     */
    Integer getStudentCount(Long counselorId);

    /**
     * 获取辅导员学院的预警数
     */
    Long getWarningCount(Long counselorId);

    /**
     * 获取辅导员学院特定等级的预警数
     */
    Long getWarningCountByLevel(Long counselorId, String level);

    /**
     * 查询学生列表
     */
    List<StudentProfile> getStudents(Long counselorId);

    /**
     * 查询学生详情
     */
    StudentProfile getStudentDetail(Long studentId);

    /**
     * 批量通知学生
     */
    void notifyStudents(List<Long> studentIds, String message);

    /**
     * 查询预警列表
     */
    List<AcademicWarning> getWarnings(Long counselorId, String status);

    /**
     * 处理预警
     */
    void processWarning(Long warningId, String measures);

    /**
     * 查询选修课信息
     */
    List<Enrollment> getEnrollments(Long counselorId);
}
