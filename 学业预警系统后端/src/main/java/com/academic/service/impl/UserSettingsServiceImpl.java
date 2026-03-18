package com.academic.service.impl;

import com.academic.mapper.StudentProfileMapper;
import com.academic.mapper.SecurityLogMapper;
import com.academic.mapper.UserMapper;
import com.academic.mapper.CollegeMapper;
import com.academic.mapper.MajorMapper;
import com.academic.entity.SecurityLog;
import com.academic.entity.StudentProfile;
import com.academic.entity.User;
import com.academic.entity.College;
import com.academic.entity.Major;
import com.academic.service.UserSettingsService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserSettingsServiceImpl extends ServiceImpl<StudentProfileMapper, StudentProfile> implements UserSettingsService {

    private final StudentProfileMapper studentProfileMapper;
    private final SecurityLogMapper securityLogMapper;
    private final UserMapper userMapper;
    private final CollegeMapper collegeMapper;
    private final MajorMapper majorMapper;

    /**
     * 修改密码
     */
    @Override
    @Transactional
    public boolean changePassword(Long userId, String oldPassword, String newPassword) {
        try {
            User user = userMapper.selectById(userId);
            if (user == null) {
                return false;
            }

            // 验证旧密码（这里应该使用实际的密码验证逻辑，例如BCrypt）
            // 为了简化示例，这里只做基本检查
            if (!user.getPassword().equals(oldPassword)) {
                return false;
            }

            // 更新密码（实际应该加密）
            user.setPassword(newPassword);
            userMapper.updateById(user);

            log.info("用户 {} 修改密码成功", userId);
            return true;
        } catch (Exception e) {
            log.error("修改密码失败", e);
            return false;
        }
    }

    /**
     * 更新隐私级别
     */
    @Override
    @Transactional
    public boolean updatePrivacyLevel(Long userId, Integer privacyLevel) {
        try {
            QueryWrapper<StudentProfile> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("user_id", userId);
            StudentProfile student = studentProfileMapper.selectOne(queryWrapper);
            if (student == null) {
                return false;
            }

            student.setPrivacyLevel(privacyLevel);
            student.setUpdatedAt(LocalDateTime.now());
            studentProfileMapper.updateById(student);

            log.info("用户 {} 隐私级别更新为 {}", userId, privacyLevel);
            return true;
        } catch (Exception e) {
            log.error("更新隐私级别失败", e);
            return false;
        }
    }

    /**
     * 获取安全日志（最近n条）
     */
    @Override
    public List<SecurityLog> getSecurityLogs(Long userId, Integer limit) {
        QueryWrapper<SecurityLog> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId)
                .orderByDesc("created_at")
                .last("LIMIT " + limit);
        return securityLogMapper.selectList(queryWrapper);
    }

    /**
     * 记录登录日志
     */
    @Override
    public void recordLoginLog(Long userId, String ipAddress) {
        try {
            SecurityLog log = new SecurityLog();
            log.setUserId(userId);
            log.setIpAddress(ipAddress);
            log.setLoginTime(LocalDateTime.now());
            log.setAction("LOGIN_SUCCESS");
            securityLogMapper.insert(log);
        } catch (Exception e) {
            log.error("记录登录日志失败", e);
        }
    }

    /**
     * 获取用户设置信息
     */
    @Override
    public StudentProfile getUserSettings(Long userId) {
        QueryWrapper<StudentProfile> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId);
        StudentProfile student = studentProfileMapper.selectOne(queryWrapper);
        
        if (student == null) {
            return null;
        }
        
        // 查询用户信息（訪users表获取email、phone）
        User user = userMapper.selectById(userId);
        if (user != null) {
            student.setEmail(user.getEmail());
            student.setPhone(user.getPhone());
        }
        
        // 查询学院名称
        if (student.getCollegeId() != null) {
            College college = collegeMapper.selectById(student.getCollegeId());
            if (college != null) {
                student.setCollegeName(college.getName());
            }
        }
        
        // 查询专业名称
        if (student.getMajorId() != null) {
            Major major = majorMapper.selectById(student.getMajorId());
            if (major != null) {
                student.setMajorName(major.getName());
            }
        }
        
        return student;
    }

}
