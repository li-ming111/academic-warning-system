package com.academic.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.academic.entity.ClassManagementRequest;
import java.util.List;
import java.util.Map;

/**
 * 班级管理申请服务接口
 */
public interface ClassManagementRequestService extends IService<ClassManagementRequest> {
    
    /**
     * 教师提交班级管理申请
     */
    Long submitRequest(Long teacherId, Long classId, String reason);
    
    /**
     * 获取待审批申请列表（管理员）
     */
    List<Map<String, Object>> getPendingRequests();
    
    /**
     * 批准申请
     */
    void approveRequest(Long requestId);
    
    /**
     * 拒绝申请
     */
    void rejectRequest(Long requestId, String rejectReason);
    
    /**
     * 获取教师的所有申请
     */
    List<ClassManagementRequest> getTeacherRequests(Long teacherId);
    
    /**
     * 获取班级的申请历史
     */
    List<ClassManagementRequest> getClassRequests(Long classId);
}
