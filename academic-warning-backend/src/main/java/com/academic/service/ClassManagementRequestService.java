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
     * 教师或辅导员提交班级管理申请
     * @param userId 用户ID（教师或辅导员）
     * @param classId 班级ID
     * @param userType 用户类型: 'teacher' 或 'counselor'
     * @param reason 申请原因
     */
    Long submitRequest(Long userId, Long classId, String userType, String reason);
    
    /**
     * 搜索班级
     */
    List<Map<String, Object>> searchClasses(String keyword);
    
    /**
     * 获取教师的所有班级
     */
    List<Map<String, Object>> getTeacherClasses(Long teacherId);
    
    /**
     * 获取辅导员的所有班级
     */
    List<Map<String, Object>> getCounselorClasses(Long counselorId);
    
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
     * 获取辅导员的所有申请
     */
    List<ClassManagementRequest> getCounselorRequests(Long counselorId);
    
    /**
     * 获取班级的申请历彲
     */
    List<ClassManagementRequest> getClassRequests(Long classId);
}
