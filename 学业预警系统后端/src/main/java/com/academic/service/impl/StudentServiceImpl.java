package com.academic.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.academic.entity.StudentProfile;
import com.academic.entity.User;
import com.academic.entity.Major;
import com.academic.entity.Score;
import com.academic.entity.Class;
import com.academic.dto.StudentRegisterRequest;
import com.academic.mapper.StudentProfileMapper;
import com.academic.mapper.ScoreMapper;
import com.academic.mapper.MajorMapper;
import com.academic.mapper.ClassMapper;
import com.academic.service.StudentService;
import com.academic.service.UserService;
import com.academic.service.ClassManagementService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
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
    private final ClassManagementService classManagementService;
    private final JdbcTemplate jdbcTemplate;

    public StudentServiceImpl(UserService userService, PasswordEncoder passwordEncoder,
                             MajorMapper majorMapper, ScoreMapper scoreMapper,
                             ClassManagementService classManagementService, JdbcTemplate jdbcTemplate) {
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
        this.majorMapper = majorMapper;
        this.scoreMapper = scoreMapper;
        this.classManagementService = classManagementService;
        this.jdbcTemplate = jdbcTemplate;
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

        // 检查学号是否已存在（使用JdbcTemplate直接查询，绕过MyBatis-Plus缓存）
        Integer count = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM student_profile WHERE student_id = ?", Integer.class, request.getStudentId());
        if (count != null && count > 0) {
            // 检查是否存在对应的用户
            List<Long> userIds = jdbcTemplate.queryForList("SELECT user_id FROM student_profile WHERE student_id = ?", Long.class, request.getStudentId());
            boolean hasValidUser = false;
            for (Long userId : userIds) {
                User existingUser = userService.getById(userId);
                if (existingUser != null) {
                    hasValidUser = true;
                    break;
                }
            }
            
            if (!hasValidUser) {
                // 所有相关用户都不存在，说明是残留数据，删除后继续注册
                try {
                    // 先删除所有相关的成绩申诉记录
                    jdbcTemplate.update("DELETE FROM score_appeals WHERE student_id IN (SELECT id FROM student_profile WHERE student_id = ?)", request.getStudentId());
                    // 删除所有相关的沟通日志记录
                    jdbcTemplate.update("DELETE FROM communication_logs WHERE student_id IN (SELECT id FROM student_profile WHERE student_id = ?)", request.getStudentId());
                    // 删除所有相关的学生反馈记录
                    jdbcTemplate.update("DELETE FROM feedbacks WHERE student_id IN (SELECT id FROM student_profile WHERE student_id = ?)", request.getStudentId());
                    // 删除所有相关的辅助评估记录
                    jdbcTemplate.update("DELETE FROM assistance_evaluations WHERE plan_id IN (SELECT id FROM assistance_plans WHERE student_id IN (SELECT id FROM student_profile WHERE student_id = ?))", request.getStudentId());
                    // 删除所有相关的辅助计划记录
                    jdbcTemplate.update("DELETE FROM assistance_plans WHERE student_id IN (SELECT id FROM student_profile WHERE student_id = ?)", request.getStudentId());
                    // 删除所有相关的学业预警记录
                    jdbcTemplate.update("DELETE FROM academic_warnings WHERE student_id IN (SELECT id FROM student_profile WHERE student_id = ?)", request.getStudentId());
                    // 删除所有相关的基准分析记录
                    jdbcTemplate.update("DELETE FROM benchmark_analysis WHERE student_id IN (SELECT id FROM student_profile WHERE student_id = ?)", request.getStudentId());
                    // 删除所有相关的课程注册记录
                    jdbcTemplate.update("DELETE FROM enrollments WHERE student_id IN (SELECT id FROM student_profile WHERE student_id = ?)", request.getStudentId());
                    // 删除所有相关的成绩记录
                    jdbcTemplate.update("DELETE FROM scores WHERE student_id IN (SELECT id FROM student_profile WHERE student_id = ?)", request.getStudentId());
                    // 删除所有相关的订阅偏好记录
                    jdbcTemplate.update("DELETE FROM subscription_preferences WHERE student_id IN (SELECT id FROM student_profile WHERE student_id = ?)", request.getStudentId());
                    // 最后删除所有相关的学生档案
                    int deleteCount = jdbcTemplate.update("DELETE FROM student_profile WHERE student_id = ?", request.getStudentId());
                    log.info("删除残留的学生档案: {}，影响行数: {}", request.getStudentId(), deleteCount);
                    
                    // 再次检查学号是否已存在（使用JdbcTemplate直接查询，绕过MyBatis-Plus缓存）
                    count = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM student_profile WHERE student_id = ?", Integer.class, request.getStudentId());
                    if (count != null && count > 0) {
                        throw new RuntimeException("学号已被注册");
                    }
                } catch (Exception e) {
                    log.error("删除残留学生档案失败: {}", e.getMessage());
                    throw new RuntimeException("学号已被注册");
                }
            } else {
                throw new RuntimeException("学号已被注册");
            }
        }

        // 智能解析：截取学号第5-6位作为专业编码
        String majorCode = request.getStudentId().substring(4, 6);
        log.info("解析学号 {} 的专业编码：{}", request.getStudentId(), majorCode);
        
        QueryWrapper<Major> majorQuery = new QueryWrapper<>();
        majorQuery.eq("code", majorCode);
        Major major = majorMapper.selectOne(majorQuery);

        // 若专业编码不存在，提示"请联系管理员"
        if (major == null) {
            throw new RuntimeException("无效的专业编码：" + majorCode + "，请联系管理员");
        }
        
        log.info("专业编码 {} 映射到的专业：ID={}, 名称={}, 简称={}", majorCode, major.getId(), major.getName(), major.getShortName());

        // 验证专业编码是否正确映射
        if ("01".equals(majorCode) && !"软件工程".equals(major.getName())) {
            log.warn("专业编码 '01' 映射到了错误的专业：{}，应该映射到 '软件工程'", major.getName());
            // 尝试通过专业名称查询正确的专业
            QueryWrapper<Major> correctMajorQuery = new QueryWrapper<>();
            correctMajorQuery.eq("name", "软件工程");
            Major correctMajor = majorMapper.selectOne(correctMajorQuery);
            if (correctMajor != null) {
                log.info("使用正确的专业：ID={}, 名称={}, 简称={}（专业编码：{}", correctMajor.getId(), correctMajor.getName(), correctMajor.getShortName(), correctMajor.getCode());
                major = correctMajor;
            } else {
                log.error("未找到名称为 '软件工程' 的专业");
            }
        } else if ("02".equals(majorCode) && !"计算机科学与技术".equals(major.getName())) {
            log.warn("专业编码 '02' 映射到了错误的专业：{}，应该映射到 '计算机科学与技术'", major.getName());
            // 尝试通过专业名称查询正确的专业
            QueryWrapper<Major> correctMajorQuery = new QueryWrapper<>();
            correctMajorQuery.eq("name", "计算机科学与技术");
            Major correctMajor = majorMapper.selectOne(correctMajorQuery);
            if (correctMajor != null) {
                log.info("使用正确的专业：ID={}, 名称={}, 简称={}（专业编码：{}", correctMajor.getId(), correctMajor.getName(), correctMajor.getShortName(), correctMajor.getCode());
                major = correctMajor;
            } else {
                log.error("未找到名称为 '计算机科学与技术' 的专业");
            }
        }
        
        // 确保专业编码使用正确的简称
        if ("01".equals(majorCode)) {
            log.info("专业编码 '01' 强制使用简称 '软工'");
            // 临时修改major对象的shortName属性
            major.setShortName("软工");
        } else if ("02".equals(majorCode)) {
            log.info("专业编码 '02' 强制使用简称 '计科'");
            // 临时修改major对象的shortName属性
            major.setShortName("计科");
        }
        
        log.info("最终使用的专业：ID={}, 名称={}, 简称={}", major.getId(), major.getName(), major.getShortName());

        // 创建用户
        User user = new User();
        user.setUsername(request.getStudentId());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setEmail(request.getEmail());
        user.setPhone(request.getPhone());
        user.setName(request.getName()); // 设置用户姓名
        user.setRole(1); // 学生角色
        userService.save(user);

        // 根据学号推导班级信息
        // 学号格式：第0-3位年份，第4-5位专业编码，第6-7位班级编号，第8-9位排名
        String enrollmentYear = request.getStudentId().substring(0, 4);
        String enrollmentYearShort = request.getStudentId().substring(2, 4); // 年份后两位，如：23
        String classNum = request.getStudentId().substring(6, 8);
        
        // 班级名称格式：{专业简称}{年份后两位}{班级编号}班
        // 例如：计我2306班
        String className = major.getShortName() + enrollmentYearShort + classNum + "班";
        
        // 根据班级名称和专业ID，查找或创建班级
        QueryWrapper<Class> classQuery = new QueryWrapper<>();
        classQuery.eq("name", className).eq("major_id", major.getId());
        Class clazz = classManagementService.getOne(classQuery);
        
        // 如果班级不存在，创建新班级
        if (clazz == null) {
            Long classId = classManagementService.createClass(className, major.getId(), major.getCollegeId());
            clazz = new Class();
            clazz.setId(classId);
            clazz.setName(className);
            clazz.setMajorId(major.getId());
            clazz.setCollegeId(major.getCollegeId());
            clazz.setTeacherId(null); // 明确设置教师ID为null，避免自动分配
            log.info("自动创建班级: {}", className);
        }

        // 创建学生档案，自动填充学院、专业和班级
        StudentProfile studentProfile = new StudentProfile();
        studentProfile.setUserId(user.getId());
        studentProfile.setStudentId(request.getStudentId());
        studentProfile.setName(request.getName());
        studentProfile.setMajorId(major.getId());
        studentProfile.setCollegeId(major.getCollegeId());
        studentProfile.setClassId(clazz.getId());
        studentProfile.setPrivacyLevel(0); // 默认公开
        this.save(studentProfile);

        log.info("学生智能注册成功: {}，专业: {}，学院ID: {}，班级: {}辅家: {}", request.getStudentId(), majorCode, major.getCollegeId(), className, major.getShortName());
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

    @Override
    public List<StudentProfile> getStudentsByClassId(Long classId) {
        if (classId == null) {
            return new java.util.ArrayList<>();
        }
        QueryWrapper<StudentProfile> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("class_id", classId)
                   .orderByDesc("id");
        return this.list(queryWrapper);
    }

}
