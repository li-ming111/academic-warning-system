package com.academic.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.academic.entity.ScoreAppeal;
import java.util.List;

public interface ScoreAppealService extends IService<ScoreAppeal> {

    /**
     * 提交成绩申诉
     */
    boolean submitAppeal(ScoreAppeal appeal);

    /**
     * 获取学生的申诉列表
     */
    List<ScoreAppeal> getStudentAppeals(Long studentId);

    /**
     * 获取待处理的申诉列表
     */
    List<ScoreAppeal> getPendingAppeals();

    /**
     * 教师回复申诉
     */
    boolean replyAppeal(Long appealId, String message, String status);

    /**
     * 获取申诉详情
     */
    ScoreAppeal getAppealDetail(Long appealId);

}
