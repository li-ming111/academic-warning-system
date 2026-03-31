package com.academic.service;

import com.academic.entity.ScoreAppeal;
import java.util.List;
import java.util.Map;

/**
 * 学生成绩申诉服务接口
 * 用于实现成绩申诉管理、历史追踪和统计分析功能
 */
public interface StudentAppealService {

    /**
     * 提交成绩申诉
     *
     * @param appeal 申诉对象（包含警告ID、学生ID、申诉原因等）
     * @return 申诉是否成功提交
     */
    boolean submitAppeal(ScoreAppeal appeal);

    /**
     * 获取学生的所有申诉记录
     *
     * @param studentId 学生ID
     * @return 申诉列表
     */
    List<ScoreAppeal> getStudentAppeals(Long studentId);

    /**
     * 获取学生的待处理申诉（状态为pending）
     *
     * @param studentId 学生ID
     * @return 待处理申诉列表
     */
    List<ScoreAppeal> getPendingAppeals(Long studentId);

    /**
     * 获取申诉详情
     *
     * @param appealId 申诉ID
     * @return 申诉对象
     */
    ScoreAppeal getAppealDetail(Long appealId);

    /**
     * 撤销申诉
     * 仅对status=pending的申诉有效
     *
     * @param appealId 申诉ID
     * @return 撤销是否成功
     */
    boolean withdrawAppeal(Long appealId);

    /**
     * 获取申诉统计信息
     * 包含申诉总数、通过数、拒绝数、待处理数等
     *
     * @param studentId 学生ID
     * @return 统计信息Map
     */
    Map<String, Object> getAppealStatistics(Long studentId);

    /**
     * 获取申诉成功率
     *
     * @param studentId 学生ID
     * @return 成功率百分比（0-100）
     */
    Double getAppealSuccessRate(Long studentId);

    /**
     * 按状态分类获取申诉
     *
     * @param studentId 学生ID
     * @param status    申诉状态（pending、approved、rejected）
     * @return 该状态下的申诉列表
     */
    List<ScoreAppeal> getAppealsByStatus(Long studentId, String status);

    /**
     * 获取申诉历史（分页）
     *
     * @param studentId 学生ID
     * @param page      页号
     * @param pageSize  每页大小
     * @return 包含{total, page, pageSize, pages, data}的Map
     */
    Map<String, Object> getAppealHistory(Long studentId, int page, int pageSize);

    /**
     * 获取最近的申诉
     *
     * @param studentId 学生ID
     * @param limit     获取数量
     * @return 最近的申诉列表
     */
    List<ScoreAppeal> getRecentAppeals(Long studentId, int limit);

    /**
     * 更新申诉状态（仅管理员/教师调用）
     *
     * @param appealId 申诉ID
     * @param status   新状态
     * @param remark   处理备注
     * @return 更新是否成功
     */
    boolean updateAppealStatus(Long appealId, String status, String remark);

    /**
     * 获取申诉原因分类统计
     *
     * @param studentId 学生ID
     * @return 原因分类及各类申诉数量
     */
    Map<String, Long> getAppealReasonStatistics(Long studentId);
}
