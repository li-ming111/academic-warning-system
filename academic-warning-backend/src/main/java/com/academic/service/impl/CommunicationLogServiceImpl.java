package com.academic.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.academic.entity.CommunicationLog;
import com.academic.mapper.CommunicationLogMapper;
import com.academic.service.CommunicationLogService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import java.util.List;

@Slf4j
@Service
public class CommunicationLogServiceImpl extends ServiceImpl<CommunicationLogMapper, CommunicationLog> implements CommunicationLogService {

    @Override
    public boolean sendMessage(CommunicationLog log) {
        return this.save(log);
    }

    @Override
    public List<CommunicationLog> getConversation(Long studentId, Long teacherId) {
        QueryWrapper<CommunicationLog> queryWrapper = new QueryWrapper<>();
        queryWrapper.and(wrapper -> 
            wrapper.eq("student_id", studentId).eq("teacher_id", teacherId)
        );
        queryWrapper.orderByAsc("created_at");
        return this.list(queryWrapper);
    }

    @Override
    public List<CommunicationLog> getStudentMessages(Long studentId) {
        QueryWrapper<CommunicationLog> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("student_id", studentId);
        queryWrapper.orderByDesc("created_at");
        return this.list(queryWrapper);
    }

    @Override
    public Long getUnreadCount(Long studentId) {
        QueryWrapper<CommunicationLog> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("student_id", studentId);
        queryWrapper.eq("status", 0); // 0=未读
        return this.count(queryWrapper);
    }

    @Override
    public boolean markAsRead(Long messageId) {
        CommunicationLog log = this.getById(messageId);
        if (log != null) {
            log.setStatus(1); // 1表示已读
            return this.updateById(log);
        }
        return false;
    }

    @Override
    public boolean replyMessage(Long messageId, String reply) {
        return true;
    }

}
