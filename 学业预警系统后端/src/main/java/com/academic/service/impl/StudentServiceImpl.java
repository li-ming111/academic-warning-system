package com.academic.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.academic.entity.StudentProfile;
import com.academic.entity.User;
import com.academic.entity.Major;
import com.academic.entity.Score;
import com.academic.dto.StudentRegisterRequest;
import com.academic.mapper.StudentProfileMapper;
import com.academic.mapper.ScoreMapper;
import com.academic.service.StudentService;
import com.academic.service.UserService;
import com.academic.mapper.MajorMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Slf4j
@Service
public class StudentServiceImpl extends ServiceImpl<StudentProfileMapper, StudentProfile> implements StudentService {

    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final MajorMapper majorMapper;
    private final ScoreMapper scoreMapper;

    public StudentServiceImpl(UserService userService, PasswordEncoder passwordEncoder,
                             MajorMapper majorMapper, ScoreMapper scoreMapper) {
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
        this.majorMapper = majorMapper;
        this.scoreMapper = scoreMapper;
    }

    @Override
    @Transactional
    public void register(StudentRegisterRequest request) {
        // 验证学号格式：必须为10位纯数字
        if (request.getStudentId() == null || request.getStudentId().length() != 10) {
            throw new RuntimeException("学号必须为10位纯数字");
        }
        if (!request.getStudentId().matches("\\d{10}")) {
            throw new RuntimeException("学号必须为10位纯数字");
        }

        // 验证必填项
        if (request.getPhone() == null || request.getPhone().trim().isEmpty()) {
            throw new RuntimeException("手机号为必填项");
        }
        if (request.getEmail() == null || request.getEmail().trim().isEmpty()) {
            throw new RuntimeException("邮箱为必填项");
        }
        if (request.getPassword() == null || request.getPassword().trim().isEmpty()) {
            throw new RuntimeException("密码为必填项");
        }

        // 检查学号是否已存在
        StudentProfile existingStudent = getByStudentId(request.getStudentId());
        if (existingStudent != null) {
            throw new RuntimeException("学号已被注册");
        }

        // 智能解析：截取学号第5-6位作为专业编码
        String majorCode = request.getStudentId().substring(4, 6);
        QueryWrapper<Major> majorQuery = new QueryWrapper<>();
        majorQuery.eq("code", majorCode);
        Major major = majorMapper.selectOne(majorQuery);

        // 若专业编码不存在，提示"请联系管理员"
        if (major == null) {
            throw new RuntimeException("无效的专业编码：" + majorCode + "，请联系管理员");
        }

        // 创建用户
        User user = new User();
        user.setUsername(request.getStudentId());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setEmail(request.getEmail());
        user.setPhone(request.getPhone());
        user.setRole(1); // 学生角色
        userService.save(user);

        // 创建学生档案，自动填充学院和专业
        StudentProfile studentProfile = new StudentProfile();
        studentProfile.setUserId(user.getId());
        studentProfile.setStudentId(request.getStudentId());
        studentProfile.setName(request.getName());
        studentProfile.setMajorId(major.getId());
        studentProfile.setCollegeId(major.getCollegeId());
        studentProfile.setPrivacyLevel(0); // 默认公开
        this.save(studentProfile);

        log.info("学生智能注册成功: {}，专业: {}，学院ID: {}", request.getStudentId(), majorCode, major.getCollegeId());
    }

    @Override
    public StudentProfile getByStudentId(String studentId) {
        QueryWrapper<StudentProfile> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("student_id", studentId);
        return this.getOne(queryWrapper);
    }
    
    @Override
    public StudentProfile getByUserId(Long userId) {
        QueryWrapper<StudentProfile> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId);
        return this.getOne(queryWrapper);
    }

    @Override
    public BigDecimal getStudentGPA(Long studentId) {
        QueryWrapper<Score> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("student_id", studentId);
        List<Score> scores = scoreMapper.selectList(queryWrapper);

        if (scores.isEmpty()) {
            return BigDecimal.ZERO;
        }

        BigDecimal totalGradePoints = BigDecimal.ZERO;
        BigDecimal totalCredits = BigDecimal.ZERO;

        for (Score score : scores) {
            if (score.getGradePoint() != null && score.getGradePoint().compareTo(BigDecimal.ZERO) > 0) {
                // GPA计算需要课程学分信息，这里暂时返回简化版本
                totalGradePoints = totalGradePoints.add(score.getGradePoint());
            }
        }

        if (totalGradePoints.compareTo(BigDecimal.ZERO) > 0) {
            return totalGradePoints.divide(new BigDecimal(scores.size()), 2, BigDecimal.ROUND_HALF_UP);
        }

        return BigDecimal.ZERO;
    }

}
