package com.academic.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.academic.entity.*;
import com.academic.mapper.*;
import com.academic.service.AssistancePlanService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.*;

@Slf4j
@Service
public class AssistancePlanServiceImpl extends ServiceImpl<AssistancePlanMapper, AssistancePlan> implements AssistancePlanService {

    private final AssistancePlanMapper assistancePlanMapper;
    private final ScoreMapper scoreMapper;

    public AssistancePlanServiceImpl(AssistancePlanMapper assistancePlanMapper, ScoreMapper scoreMapper) {
        this.assistancePlanMapper = assistancePlanMapper;
        this.scoreMapper = scoreMapper;
    }

    @Override
    public List<AssistancePlan> getPlansByStudent(Long studentId) {
        return assistancePlanMapper.selectByStudentId(studentId);
    }

    @Override
    public List<AssistancePlan> getPlansByCounselor(Long counselorId) {
        // TODO: 实现根据辅导员获取学院所有帮扶计划
        return new ArrayList<>();
    }

    @Override
    public Long createPlan(AssistancePlan plan) {
        plan.setCreatedAt(LocalDateTime.now());
        plan.setUpdatedAt(LocalDateTime.now());
        assistancePlanMapper.insert(plan);
        return plan.getId();
    }

    @Override
    public boolean updateProgress(Long planId, Double progressPercentage) {
        AssistancePlan plan = assistancePlanMapper.selectById(planId);
        if (plan != null) {
            plan.setProgressPercentage(new java.math.BigDecimal(progressPercentage));
            plan.setUpdatedAt(LocalDateTime.now());
            return assistancePlanMapper.updateById(plan) > 0;
        }
        return false;
    }

    @Override
    public boolean updateStatus(Long planId, String status) {
        AssistancePlan plan = assistancePlanMapper.selectById(planId);
        if (plan != null) {
            plan.setStatus(status);
            plan.setUpdatedAt(LocalDateTime.now());
            if ("completed".equals(status)) {
                plan.setCompletedAt(LocalDateTime.now());
            }
            return assistancePlanMapper.updateById(plan) > 0;
        }
        return false;
    }

    @Override
    public Map<String, Object> getPlanEffectiveness(Long planId) {
        Map<String, Object> result = new HashMap<>();
        // TODO: 实现帮扶效果统计（学分提升、成绩改善）
        return result;
    }

    @Override
    public Map<String, Object> getAssistanceStatistics(Long counselorId) {
        Map<String, Object> result = new HashMap<>();
        // TODO: 实现辅导员学院帮扶计划统计
        return result;
    }
}
