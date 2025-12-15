package com.academic.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.academic.entity.AssistanceEvaluation;
import com.academic.mapper.AssistanceEvaluationMapper;
import com.academic.service.AssistanceEvaluationService;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 帮扶评价服务实现
 */
@Service
public class AssistanceEvaluationServiceImpl implements AssistanceEvaluationService {

    private final AssistanceEvaluationMapper evaluationMapper;

    public AssistanceEvaluationServiceImpl(AssistanceEvaluationMapper evaluationMapper) {
        this.evaluationMapper = evaluationMapper;
    }

    @Override
    public boolean submitEvaluation(AssistanceEvaluation evaluation) {
        try {
            // 检查是否已评价该计划
            QueryWrapper<AssistanceEvaluation> wrapper = new QueryWrapper<>();
            wrapper.eq("plan_id", evaluation.getPlanId())
                   .eq("student_id", evaluation.getStudentId());
            AssistanceEvaluation existing = evaluationMapper.selectOne(wrapper);
            
            if (existing != null) {
                // 已存在则更新
                evaluation.setId(existing.getId());
                evaluation.setUpdatedAt(LocalDateTime.now());
                return evaluationMapper.updateById(evaluation) > 0;
            } else {
                // 新增
                evaluation.setCreatedAt(LocalDateTime.now());
                evaluation.setUpdatedAt(LocalDateTime.now());
                return evaluationMapper.insert(evaluation) > 0;
            }
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public List<AssistanceEvaluation> getStudentEvaluations(Long studentId) {
        QueryWrapper<AssistanceEvaluation> wrapper = new QueryWrapper<>();
        wrapper.eq("student_id", studentId).orderByDesc("created_at");
        return evaluationMapper.selectList(wrapper);
    }

    @Override
    public AssistanceEvaluation getPlanEvaluation(Long planId) {
        QueryWrapper<AssistanceEvaluation> wrapper = new QueryWrapper<>();
        wrapper.eq("plan_id", planId);
        return evaluationMapper.selectOne(wrapper);
    }

    @Override
    public Map<String, Object> getEvaluationStatistics(Long studentId) {
        Map<String, Object> result = new HashMap<>();
        List<AssistanceEvaluation> evaluations = getStudentEvaluations(studentId);
        
        if (evaluations == null || evaluations.isEmpty()) {
            result.put("total", 0);
            result.put("avgRating", 0);
            result.put("avgEffectiveness", 0);
            return result;
        }

        // 计算统计数据
        double totalRating = 0;
        double totalEffectiveness = 0;
        int count = 0;

        for (AssistanceEvaluation eval : evaluations) {
            if (eval.getRating() != null) {
                totalRating += eval.getRating();
            }
            if (eval.getEffectiveness() != null) {
                totalEffectiveness += eval.getEffectiveness();
            }
            count++;
        }

        result.put("total", count);
        result.put("avgRating", count > 0 ? totalRating / count : 0);
        result.put("avgEffectiveness", count > 0 ? totalEffectiveness / count : 0);
        result.put("ratingDistribution", getRatingDistribution(evaluations));
        
        return result;
    }

    @Override
    public boolean updateEvaluation(AssistanceEvaluation evaluation) {
        evaluation.setUpdatedAt(LocalDateTime.now());
        return evaluationMapper.updateById(evaluation) > 0;
    }

    /**
     * 获取评分分布
     */
    private Map<Integer, Long> getRatingDistribution(List<AssistanceEvaluation> evaluations) {
        Map<Integer, Long> distribution = new HashMap<>();
        for (int i = 1; i <= 5; i++) {
            distribution.put(i, 0L);
        }

        for (AssistanceEvaluation eval : evaluations) {
            if (eval.getRating() != null && eval.getRating() >= 1 && eval.getRating() <= 5) {
                distribution.put(eval.getRating(), distribution.get(eval.getRating()) + 1);
            }
        }

        return distribution;
    }

}
