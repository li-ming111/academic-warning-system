package com.academic.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.academic.entity.Feedback;
import java.util.List;

public interface FeedbackService extends IService<Feedback> {

    /**
     * 提交反馈
     */
    boolean submitFeedback(Feedback feedback);

    /**
     * 获取学生的反馈列表
     */
    List<Feedback> getStudentFeedbacks(Long studentId);

    /**
     * 获取某个分类的反馈
     */
    List<Feedback> getFeedbacksByCategory(Long studentId, String category);

    /**
     * 获取未回复的反馈数
     */
    Long getUnrepliedCount(Long studentId);

}
