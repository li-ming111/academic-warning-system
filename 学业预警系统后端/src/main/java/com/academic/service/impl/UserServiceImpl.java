package com.academic.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.academic.entity.User;
import com.academic.dto.LoginRequest;
import com.academic.dto.LoginResponse;
import com.academic.mapper.UserMapper;
import com.academic.mapper.StudentProfileMapper;
import com.academic.mapper.TeacherProfileMapper;
import com.academic.mapper.CounselorProfileMapper;
import com.academic.mapper.AdminProfileMapper;
import com.academic.entity.StudentProfile;
import com.academic.entity.TeacherProfile;
import com.academic.entity.CounselorProfile;
import com.academic.entity.AdminProfile;
import com.academic.service.UserService;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.util.Date;

@Slf4j
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    private final PasswordEncoder passwordEncoder;
    private final StudentProfileMapper studentProfileMapper;
    private final TeacherProfileMapper teacherProfileMapper;
    private final CounselorProfileMapper counselorProfileMapper;
    private final AdminProfileMapper adminProfileMapper;
    private static final String JWT_SECRET = "your-secret-key-for-jwt-academic-warning-system-2025";
    private static final long JWT_EXPIRATION = 3600000; // 1 hour

    public UserServiceImpl(PasswordEncoder passwordEncoder,
                          StudentProfileMapper studentProfileMapper,
                          TeacherProfileMapper teacherProfileMapper,
                          CounselorProfileMapper counselorProfileMapper,
                          AdminProfileMapper adminProfileMapper) {
        this.passwordEncoder = passwordEncoder;
        this.studentProfileMapper = studentProfileMapper;
        this.teacherProfileMapper = teacherProfileMapper;
        this.counselorProfileMapper = counselorProfileMapper;
        this.adminProfileMapper = adminProfileMapper;
    }

    @Override
    public LoginResponse login(LoginRequest request) {
        User user = getUserByUsername(request.getUsername());
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }

        if (!verifyPassword(request.getPassword(), user.getPassword())) {
            throw new RuntimeException("密码错误");
        }

        LoginResponse response = new LoginResponse();
        response.setUserId(user.getId());
        response.setUsername(user.getUsername());
        response.setRole(String.valueOf(user.getRole()));
        response.setToken(generateToken(user));
        
        // 根据角色获取对应的 profile ID
    int role = user.getRole();
    if (role == 1) {  // 学生
        com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<StudentProfile> qw = new com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<>();
        qw.eq("user_id", user.getId());
        StudentProfile student = studentProfileMapper.selectOne(qw);
        if (student != null) {
            response.setStudentId(student.getId());
            // 返回学生的真实姓名，前端将用此作为欢迎消息的用户名
            response.setName(student.getName() != null ? student.getName() : student.getStudentId());
        }
    } else if (role == 2) {  // 教师
        com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<TeacherProfile> qw = new com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<>();
        qw.eq("user_id", user.getId());
        TeacherProfile teacher = teacherProfileMapper.selectOne(qw);
        if (teacher != null) {
            response.setTeacherId(teacher.getId());
        }
        // 设置教师姓名
        response.setName(user.getName() != null ? user.getName() : user.getUsername());
    } else if (role == 4) {  // 辅导员
        com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<CounselorProfile> qw = new com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<>();
        qw.eq("user_id", user.getId());
        CounselorProfile counselor = counselorProfileMapper.selectOne(qw);
        if (counselor != null) {
            response.setCounselorId(counselor.getId());
        }
        // 设置辅导员姓名
        response.setName(user.getName() != null ? user.getName() : user.getUsername());
    } else if (role == 3) {  // 管理员
        com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<AdminProfile> qw = new com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<>();
        qw.eq("user_id", user.getId());
        AdminProfile admin = adminProfileMapper.selectOne(qw);
        if (admin != null) {
            response.setAdminId(admin.getId());
        }
        // 设置管理员姓名
        response.setName(user.getName() != null ? user.getName() : user.getUsername());
    }
    
    // 如果所有角色都没有设置name，使用用户名作为默认值
    if (response.getName() == null) {
        response.setName(user.getName() != null ? user.getName() : user.getUsername());
    }
        
        return response;
    }

    @Override
    public User getUserByUsername(String username) {
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("username", username);
        return this.getOne(queryWrapper);
    }

    @Override
    public boolean verifyPassword(String rawPassword, String storedPassword) {
        // 数据库中是明文，直接比对
        if (rawPassword == null || storedPassword == null) {
            return false;
        }
        // 先尝试明文比对（学院早期数据）
        if (rawPassword.equals(storedPassword)) {
            return true;
        }
        // 并尝试 BCrypt 比对（支持已漎茶的数据）
        try {
            return passwordEncoder.matches(rawPassword, storedPassword);
        } catch (Exception e) {
            return false;
        }
    }

    private String generateToken(User user) {
        return Jwts.builder()
                .setSubject(String.valueOf(user.getId()))
                .claim("username", user.getUsername())
                .claim("role", user.getRole())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + JWT_EXPIRATION))
                .signWith(Keys.hmacShaKeyFor(JWT_SECRET.getBytes(StandardCharsets.UTF_8)), SignatureAlgorithm.HS256)
                .compact();
    }

}
