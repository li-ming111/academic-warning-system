package com.academic.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.academic.entity.*;
import com.academic.mapper.*;
import com.academic.service.CounselorService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
public class CounselorServiceImpl extends ServiceImpl<CounselorProfileMapper, CounselorProfile> implements CounselorService {

    private final UserMapper userMapper;
    private final StudentProfileMapper studentProfileMapper;
    private final AcademicWarningMapper warningMapper;
    private final EnrollmentMapper enrollmentMapper;
    private final NotificationMapper notificationMapper;

    public CounselorServiceImpl(UserMapper userMapper, StudentProfileMapper studentProfileMapper, 
                              AcademicWarningMapper warningMapper, EnrollmentMapper enrollmentMapper,
                              NotificationMapper notificationMapper) {
        this.userMapper = userMapper;
        this.studentProfileMapper = studentProfileMapper;
        this.warningMapper = warningMapper;
        this.enrollmentMapper = enrollmentMapper;
        this.notificationMapper = notificationMapper;
    }

    @Override
    public CounselorProfile getByUserId(Long userId) {
        QueryWrapper<CounselorProfile> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId);
        return this.getOne(queryWrapper);
    }

    @Override
    public Long registerCounselor(User user, CounselorProfile profile) {
        // 保存用户
        userMapper.insert(user);
        // 保存辅导员档案
        profile.setUserId(user.getId());
        this.save(profile);
        return user.getId();
    }

    @Override
    public Integer getStudentCount(Long counselorId) {
        CounselorProfile counselor = this.getById(counselorId);
        if (counselor == null) {
            return 0;
        }

        QueryWrapper<StudentProfile> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("college_id", counselor.getCollegeId());
        return Math.toIntExact(studentProfileMapper.selectCount(queryWrapper));
    }

    @Override
    public Long getWarningCount(Long counselorId) {
        CounselorProfile counselor = this.getById(counselorId);
        if (counselor == null) {
            return 0L;
        }

        QueryWrapper<AcademicWarning> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("college_id", counselor.getCollegeId());
        queryWrapper.in("status", "pending", "confirmed");
        return warningMapper.selectCount(queryWrapper);
    }

    @Override
    public Long getWarningCountByLevel(Long counselorId, String level) {
        CounselorProfile counselor = this.getById(counselorId);
        if (counselor == null) {
            return 0L;
        }

        QueryWrapper<AcademicWarning> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("college_id", counselor.getCollegeId());
        queryWrapper.eq("warning_level", level);
        queryWrapper.in("status", "pending", "confirmed");
        return warningMapper.selectCount(queryWrapper);
    }

    @Override
    public List<StudentProfile> getStudents(Long counselorId) {
        CounselorProfile counselor = this.getById(counselorId);
        if (counselor == null) {
            return List.of();
        }

        QueryWrapper<StudentProfile> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("college_id", counselor.getCollegeId());
        return studentProfileMapper.selectList(queryWrapper);
    }

    @Override
    public StudentProfile getStudentDetail(Long studentId) {
        return studentProfileMapper.selectById(studentId);
    }

    @Override
    public void notifyStudents(List<Long> studentIds, String message) {
        for (Long studentId : studentIds) {
            Notification notification = new Notification();
            notification.setUserId(studentId);
            notification.setContent(message);
            notification.setCreatedAt(LocalDateTime.now());
            notificationMapper.insert(notification);
        }
    }

    @Override
    public List<AcademicWarning> getWarnings(Long counselorId, String status) {
        CounselorProfile counselor = this.getById(counselorId);
        if (counselor == null) {
            return List.of();
        }

        QueryWrapper<AcademicWarning> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("college_id", counselor.getCollegeId());
        if (status != null && !status.isEmpty()) {
            queryWrapper.eq("status", status);
        }
        queryWrapper.orderByDesc("created_at");
        return warningMapper.selectList(queryWrapper);
    }

    @Override
    public void processWarning(Long warningId, String measures) {
        AcademicWarning warning = warningMapper.selectById(warningId);
        if (warning != null) {
            warning.setStatus("processed");
            warning.setUpdatedAt(LocalDateTime.now());
            warningMapper.updateById(warning);
        }
    }

    @Override
    public List<Enrollment> getEnrollments(Long counselorId) {
        QueryWrapper<Enrollment> queryWrapper = new QueryWrapper<>();
        return enrollmentMapper.selectList(queryWrapper);
    }
}
