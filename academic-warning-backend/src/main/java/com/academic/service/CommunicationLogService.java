package com.academic.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.academic.entity.CommunicationLog;
import java.util.List;

public interface CommunicationLogService extends IService<CommunicationLog> {

    /**
     * 发送沟通消息
     */
    boolean sendMessage(CommunicationLog log);

    /**
     * 获取学生与教师的沟通历史
     */
    List<CommunicationLog> getConversation(Long studentId, Long teacherId);

    /**
     * 获取学生的所有沟通记录
     */
    List<CommunicationLog> getStudentMessages(Long studentId);

    /**
     * 获取未读消息数
     */
    Long getUnreadCount(Long studentId);

    /**
     * 标记为已读
     */
    boolean markAsRead(Long messageId);

    /**
     * 回复消息
     */
    boolean replyMessage(Long messageId, String reply);

}
