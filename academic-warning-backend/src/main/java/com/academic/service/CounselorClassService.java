package com.academic.service;

import java.util.List;
import java.util.Map;

public interface CounselorClassService {

    /**
     * 获取辅导员所属学院的班级列表
     */
    List<Map<String, Object>> getClasses(Long counselorId);

    /**
     * 获取班级详情（学生数、专业、年级等）
     */
    Map<String, Object> getClassDetail(Long classId);

    /**
     * 获取班级的学生名单
     */
    List<Map<String, Object>> getClassStudents(Long classId);

    /**
     * 获取班级预警概览（各级别预警数）
     */
    Map<String, Object> getClassWarningOverview(Long classId);

    /**
     * 对比班级间的预警数据
     */
    List<Map<String, Object>> compareClassWarnings(Long counselorId);
}
