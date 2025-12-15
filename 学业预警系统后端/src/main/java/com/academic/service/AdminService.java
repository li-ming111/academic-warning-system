package com.academic.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.academic.entity.*;
import java.util.List;
import java.util.Map;

public interface AdminService extends IService<AdminProfile> {

    /**
     * 根据用户ID获取管理员档案
     */
    AdminProfile getByUserId(Long userId);

    /**
     * 管理员注册
     */
    Long registerAdmin(User user, AdminProfile profile);

    /**
     * 获取系统统计数据
     */
    Map<String, Object> getDashboard();

    /**
     * 查询所有学院
     */
    List<College> getColleges();

    /**
     * 根据ID查询学院
     */
    College getCollege(Long collegeId);

    /**
     * 创建学院
     */
    Long createCollege(College college);

    /**
     * 更新学院
     */
    boolean updateCollege(College college);

    /**
     * 删除学院
     */
    boolean deleteCollege(Long collegeId);

    /**
     * 查询所有专业
     */
    List<Major> getMajors();

    /**
     * 查询学院下的专业
     */
    List<Major> getMajorsByCollege(Long collegeId);

    /**
     * 创建专业
     */
    Long createMajor(Major major);

    /**
     * 更新专业
     */
    boolean updateMajor(Major major);

    /**
     * 删除专业
     */
    boolean deleteMajor(Long majorId);

    /**
     * 查询所有用户
     */
    List<User> getUsers();

    /**
     * 查询特定角色的用户
     */
    List<User> getUsersByRole(Integer role);

    /**
     * 禁用用户
     */
    boolean disableUser(Long userId);

    /**
     * 启用用户
     */
    boolean enableUser(Long userId);

    /**
     * 获取系统统计数据
     */
    Map<String, Object> getStatistics();
}
