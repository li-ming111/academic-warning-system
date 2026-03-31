package com.academic.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.academic.entity.*;
import java.util.List;
import java.util.Map;

public interface AssistancePlanService extends IService<AssistancePlan> {

    /**
     * 获取学生的帮扶计划列表
     */
    List<AssistancePlan> getPlansByStudent(Long studentId);

    /**
     * 获取辅导员学院的所有帮扶计划
     */
    List<AssistancePlan> getPlansByCounselor(Long counselorId);

    /**
     * 创建帮扶计划
     */
    Long createPlan(AssistancePlan plan);

    /**
     * 更新帮扶计划进度
     */
    boolean updateProgress(Long planId, Double progressPercentage);

    /**
     * 更新帮扶计划状态
     */
    boolean updateStatus(Long planId, String status);

    /**
     * 获取帮扶效果统计（关联学生的成绩变化）
     */
    Map<String, Object> getPlanEffectiveness(Long planId);

    /**
     * 获取辅导员学院帮扶计划统计
     */
    Map<String, Object> getAssistanceStatistics(Long counselorId);
}
