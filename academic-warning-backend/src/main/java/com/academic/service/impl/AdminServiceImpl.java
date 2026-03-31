package com.academic.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.academic.entity.*;
import com.academic.mapper.*;
import com.academic.service.AdminService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class AdminServiceImpl extends ServiceImpl<AdminProfileMapper, AdminProfile> implements AdminService {

    private final UserMapper userMapper;
    private final CollegeMapper collegeMapper;
    private final MajorMapper majorMapper;
    private final StudentProfileMapper studentProfileMapper;
    private final AcademicWarningMapper warningMapper;
    private final TeacherProfileMapper teacherProfileMapper;
    private final CounselorProfileMapper counselorProfileMapper;

    public AdminServiceImpl(UserMapper userMapper, CollegeMapper collegeMapper, MajorMapper majorMapper,
                           StudentProfileMapper studentProfileMapper, AcademicWarningMapper warningMapper,
                           TeacherProfileMapper teacherProfileMapper, CounselorProfileMapper counselorProfileMapper) {
        this.userMapper = userMapper;
        this.collegeMapper = collegeMapper;
        this.majorMapper = majorMapper;
        this.studentProfileMapper = studentProfileMapper;
        this.warningMapper = warningMapper;
        this.teacherProfileMapper = teacherProfileMapper;
        this.counselorProfileMapper = counselorProfileMapper;
    }

    @Override
    public AdminProfile getByUserId(Long userId) {
        QueryWrapper<AdminProfile> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId);
        return this.getOne(queryWrapper);
    }

    @Override
    public Long registerAdmin(User user, AdminProfile profile) {
        userMapper.insert(user);
        profile.setUserId(user.getId());
        this.save(profile);
        return user.getId();
    }

    @Override
    public Map<String, Object> getDashboard() {
        Map<String, Object> stats = new HashMap<>();
        
        // 学院总数
        long collegeCount = collegeMapper.selectCount(null);
        stats.put("collegeCount", collegeCount);
        
        // 专业总数
        long majorCount = majorMapper.selectCount(null);
        stats.put("majorCount", majorCount);
        
        // 学生总数
        long studentCount = studentProfileMapper.selectCount(null);
        stats.put("studentCount", studentCount);
        
        // 教师总数
        long teacherCount = teacherProfileMapper.selectCount(null);
        stats.put("teacherCount", teacherCount);
        
        // 辅导员总数
        long counselorCount = counselorProfileMapper.selectCount(null);
        stats.put("counselorCount", counselorCount);
        
        // 用户总数
        long userCount = userMapper.selectCount(null);
        stats.put("userCount", userCount);
        
        // 预警总数
        long warningCount = warningMapper.selectCount(null);
        stats.put("warningCount", warningCount);
        
        // 红色预警
        QueryWrapper<AcademicWarning> redQuery = new QueryWrapper<>();
        redQuery.eq("warning_level", "red");
        long redWarnings = warningMapper.selectCount(redQuery);
        stats.put("redWarnings", redWarnings);
        
        // 黄色预警
        QueryWrapper<AcademicWarning> yellowQuery = new QueryWrapper<>();
        yellowQuery.eq("warning_level", "yellow");
        long yellowWarnings = warningMapper.selectCount(yellowQuery);
        stats.put("yellowWarnings", yellowWarnings);
        
        // 蓝色预警
        QueryWrapper<AcademicWarning> blueQuery = new QueryWrapper<>();
        blueQuery.eq("warning_level", "blue");
        long blueWarnings = warningMapper.selectCount(blueQuery);
        stats.put("blueWarnings", blueWarnings);

        return stats;
    }

    @Override
    public List<College> getColleges() {
        return collegeMapper.selectList(null);
    }

    @Override
    public College getCollege(Long collegeId) {
        return collegeMapper.selectById(collegeId);
    }

    @Override
    public Long createCollege(College college) {
        college.setCreatedAt(LocalDateTime.now());
        college.setUpdatedAt(LocalDateTime.now());
        collegeMapper.insert(college);
        return college.getId();
    }

    @Override
    public boolean updateCollege(College college) {
        college.setUpdatedAt(LocalDateTime.now());
        return collegeMapper.updateById(college) > 0;
    }

    @Override
    public boolean deleteCollege(Long collegeId) {
        return collegeMapper.deleteById(collegeId) > 0;
    }

    @Override
    public List<Major> getMajors() {
        return majorMapper.selectList(null);
    }

    @Override
    public List<Major> getMajorsByCollege(Long collegeId) {
        QueryWrapper<Major> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("college_id", collegeId);
        return majorMapper.selectList(queryWrapper);
    }

    @Override
    public Long createMajor(Major major) {
        major.setCreatedAt(LocalDateTime.now());
        major.setUpdatedAt(LocalDateTime.now());
        majorMapper.insert(major);
        return major.getId();
    }

    @Override
    public boolean updateMajor(Major major) {
        major.setUpdatedAt(LocalDateTime.now());
        return majorMapper.updateById(major) > 0;
    }

    @Override
    public boolean deleteMajor(Long majorId) {
        return majorMapper.deleteById(majorId) > 0;
    }

    @Override
    public List<User> getUsers() {
        return userMapper.selectList(null);
    }

    @Override
    public List<User> getUsersByRole(Integer role) {
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("role", role);
        return userMapper.selectList(queryWrapper);
    }

    @Override
    public boolean disableUser(Long userId) {
        User user = userMapper.selectById(userId);
        if (user != null) {
            user.setStatus(0);
            user.setUpdatedAt(LocalDateTime.now());
            return userMapper.updateById(user) > 0;
        }
        return false;
    }

    @Override
    public boolean enableUser(Long userId) {
        User user = userMapper.selectById(userId);
        if (user != null) {
            user.setStatus(1);
            user.setUpdatedAt(LocalDateTime.now());
            return userMapper.updateById(user) > 0;
        }
        return false;
    }

    @Override
    public Map<String, Object> getStatistics() {
        return getDashboard();
    }
}
