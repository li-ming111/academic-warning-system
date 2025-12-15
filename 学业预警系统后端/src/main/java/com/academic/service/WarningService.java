package com.academic.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.academic.entity.AcademicWarning;

import java.util.List;

public interface WarningService extends IService<AcademicWarning> {

    /**
     * 获取学生的预警列表
     */
    List<AcademicWarning> getStudentWarnings(Long studentId);

    /**
     * 获取待处理的预警
     */
    List<AcademicWarning> getPendingWarnings(Long studentId);

    /**
     * 统计预警数量
     */
    Long countWarnings(Long studentId);

    /**
     * 统计特定级别的预警
     */
    Long countWarningsByLevel(Long studentId, String level);

    /**
     * 确认预警
     */
    void confirmWarning(Long warningId);

    /**
     * 计算学生的挂科数量（成绩<60）
     */
    Long countFailedCourses(Long studentId, String semester);

    /**
     * 根据挂科数量生成预警
     */
    void generateWarningsByFailedCount(Long studentId, String semester);

}
