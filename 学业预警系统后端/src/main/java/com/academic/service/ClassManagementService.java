package com.academic.service;

import com.academic.entity.Class;
import com.baomidou.mybatisplus.extension.service.IService;
import java.util.List;
import java.util.Map;

/**
 * 班级管理服务
 */
public interface ClassManagementService extends IService<Class> {

    /**
     * 创建班级
     */
    Long createClass(String name, Long majorId, Long collegeId);

    /**
     * 获取班级列表
     */
    List<Class> getClassesByMajor(Long majorId);

    /**
     * 获取班级详情
     */
    Class getClassDetail(Long classId);

    /**
     * 更新班级信息
     */
    void updateClass(Long classId, String name);

    /**
     * 删除班级
     */
    void deleteClass(Long classId);

    /**
     * 获取班级的学生数量
     */
    Integer getStudentCount(Long classId);

    /**
     * 统计班级预警数据
     */
    Map<String, Object> getClassWarningStatistics(Long classId);
}
