package com.academic.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.academic.entity.SecurityLog;
import com.academic.entity.StudentProfile;

import java.util.List;

public interface UserSettingsService extends IService<StudentProfile> {

    /**
     * 修改密码
     */
    boolean changePassword(Long userId, String oldPassword, String newPassword);

    /**
     * 更新隐私级别
     */
    boolean updatePrivacyLevel(Long studentId, Integer privacyLevel);

    /**
     * 获取安全日志（最近n条）
     */
    List<SecurityLog> getSecurityLogs(Long userId, Integer limit);

    /**
     * 记录登录日志
     */
    void recordLoginLog(Long userId, String ipAddress);

    /**
     * 获取用户设置信息
     */
    StudentProfile getUserSettings(Long userId);

}
