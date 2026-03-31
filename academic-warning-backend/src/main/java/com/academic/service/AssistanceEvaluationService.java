package com.academic.service;

import com.academic.entity.AssistanceEvaluation;
import java.util.List;
import java.util.Map;

/**
 * 帮扶评价服务接口
 * 用于学生提交帮扶计划的评价反馈
 */
public interface AssistanceEvaluationService {

    /**
     * 提交帮扶评价
     *
     * @param evaluation 评价对象
     * @return 是否成功
     */
    boolean submitEvaluation(AssistanceEvaluation evaluation);

    /**
     * 获取学生的评价列表
     *
     * @param studentId 学生ID
     * @return 评价列表
     */
    List<AssistanceEvaluation> getStudentEvaluations(Long studentId);

    /**
     * 获取计划的评价
     *
     * @param planId 计划ID
     * @return 评价对象
     */
    AssistanceEvaluation getPlanEvaluation(Long planId);

    /**
     * 获取评价统计（平均分、满意度等）
     *
     * @param studentId 学生ID
     * @return 统计数据Map
     */
    Map<String, Object> getEvaluationStatistics(Long studentId);

    /**
     * 更新评价
     *
     * @param evaluation 评价对象
     * @return 是否成功
     */
    boolean updateEvaluation(AssistanceEvaluation evaluation);

}
