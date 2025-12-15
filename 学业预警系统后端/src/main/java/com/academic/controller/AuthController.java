package com.academic.controller;

import com.academic.common.ApiResponse;
import com.academic.dto.LoginRequest;
import com.academic.dto.LoginResponse;
import com.academic.service.UserService;
import com.academic.service.StudentService;
import com.academic.service.TeacherService;
import com.academic.service.CounselorService;
import com.academic.mapper.CollegeMapper;
import com.academic.mapper.MajorMapper;
import com.academic.entity.*;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import java.util.Map;
import java.util.HashMap;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/auth")
public class AuthController {

    private final UserService userService;
    private final StudentService studentService;
    private final TeacherService teacherService;
    private final CounselorService counselorService;
    private final CollegeMapper collegeMapper;
    private final MajorMapper majorMapper;
    private final PasswordEncoder passwordEncoder;

    public AuthController(UserService userService, StudentService studentService,
                         TeacherService teacherService, CounselorService counselorService,
                         CollegeMapper collegeMapper, MajorMapper majorMapper, PasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.studentService = studentService;
        this.teacherService = teacherService;
        this.counselorService = counselorService;
        this.collegeMapper = collegeMapper;
        this.majorMapper = majorMapper;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * 用户登录
     */
    @PostMapping("/login")
    public ApiResponse<LoginResponse> login(@RequestBody LoginRequest request) {
        try {
            LoginResponse response = userService.login(request);
            return ApiResponse.success(response);
        } catch (Exception e) {
            log.error("登录失败", e);
            return ApiResponse.error(e.getMessage());
        }
    }

    /**
     * 获取学院列表
     */
    @GetMapping("/colleges")
    public ApiResponse<List<College>> getColleges() {
        try {
            List<College> colleges = collegeMapper.selectList(null);
            return ApiResponse.success(colleges);
        } catch (Exception e) {
            log.error("获取学院列表失败", e);
            return ApiResponse.error(e.getMessage());
        }
    }

    /**
     * 学生注册
     */
    @PostMapping("/register/student")
    public ApiResponse<Map<String, Object>> registerStudent(@RequestBody Map<String, Object> params) {
        try {
            String studentId = (String) params.get("studentId");
            String password = (String) params.get("password");
            String name = (String) params.get("name");
            String phone = (String) params.get("phone");
            String email = (String) params.get("email");

            // 验证必填项
            if (studentId == null || studentId.trim().isEmpty()) {
                throw new RuntimeException("学号为必填项");
            }
            if (password == null || password.trim().isEmpty()) {
                throw new RuntimeException("密码为必填项");
            }
            if (name == null || name.trim().isEmpty()) {
                throw new RuntimeException("姓名为必填项");
            }
            if (phone == null || phone.trim().isEmpty()) {
                throw new RuntimeException("手机号为必填项");
            }
            if (email == null || email.trim().isEmpty()) {
                throw new RuntimeException("邮箱为必填项");
            }

            // 构造请求对象
            com.academic.dto.StudentRegisterRequest request = new com.academic.dto.StudentRegisterRequest();
            request.setStudentId(studentId);
            request.setPassword(password);
            request.setName(name);
            request.setPhone(phone);
            request.setEmail(email);

            // 调用学生注册服务
            studentService.register(request);

            Map<String, Object> result = new HashMap<>();
            result.put("success", true);
            result.put("message", "注册成功");
            result.put("studentId", studentId);
            return ApiResponse.success(result);
        } catch (Exception e) {
            log.error("学生注册失败", e);
            return ApiResponse.error(e.getMessage());
        }
    }

    /**
     * 教师注册
     */
    @PostMapping("/register/teacher")
    public ApiResponse<Map<String, Object>> registerTeacher(@RequestBody Map<String, Object> params) {
        try {
            // 获取参数
            String username = (String) params.get("username");
            String password = (String) params.get("password");
            String majorCode = (String) params.get("majorCode");
            String phone = (String) params.get("phone");
            String email = (String) params.get("email");

            // 验证必填项
            if (username == null || username.trim().isEmpty()) {
                throw new RuntimeException("工号为必填项");
            }
            if (password == null || password.trim().isEmpty()) {
                throw new RuntimeException("密码为必填项");
            }
            if (majorCode == null || majorCode.trim().isEmpty()) {
                throw new RuntimeException("专业为必填项");
            }
            if (phone == null || phone.trim().isEmpty()) {
                throw new RuntimeException("手机号为必填项（用于学生联系）");
            }
            if (email == null || email.trim().isEmpty()) {
                throw new RuntimeException("邮箱为必填项（用于学生联系）");
            }

            // 根据专业编码查询专业信息
            QueryWrapper<Major> majorQuery = new QueryWrapper<>();
            majorQuery.eq("code", majorCode);
            Major major = majorMapper.selectOne(majorQuery);
            if (major == null) {
                throw new RuntimeException("无效的专业编码：" + majorCode + "，请联系管理员");
            }

            // 创建用户对象
            User user = new User();
            user.setUsername(username);
            user.setPassword(passwordEncoder.encode(password)); // 密码加密
            user.setPhone(phone);
            user.setEmail(email);
            user.setRole(2); // 教师角色

            // 创建教师档案，自动关联学院（通过专业）
            TeacherProfile profile = new TeacherProfile();
            profile.setCollegeId(major.getCollegeId()); // 从专业自动获取学院ID

            // 调用注册服务
            Long userId = teacherService.registerTeacher(user, profile);
            Map<String, Object> result = new HashMap<>();
            result.put("userId", userId);
            result.put("username", username);
            result.put("majorCode", majorCode);
            result.put("collegeId", major.getCollegeId());
            log.info("教师注册成功: {}，专业编码: {}，学院ID: {}", username, majorCode, major.getCollegeId());
            return ApiResponse.success(result);
        } catch (Exception e) {
            log.error("教师注册失败", e);
            return ApiResponse.error(e.getMessage());
        }
    }

    /**
     * 辅导员注册
     */
    @PostMapping("/register/counselor")
    public ApiResponse<Map<String, Object>> registerCounselor(@RequestBody Map<String, Object> params) {
        try {
            String username = (String) params.get("username");
            String password = (String) params.get("password");
            String majorCode = (String) params.get("majorCode");
            String phone = (String) params.get("phone");
            String email = (String) params.get("email");

            // 验证必填项
            if (username == null || username.trim().isEmpty()) {
                throw new RuntimeException("工号为必填项");
            }
            if (password == null || password.trim().isEmpty()) {
                throw new RuntimeException("密码为必填项");
            }
            if (majorCode == null || majorCode.trim().isEmpty()) {
                throw new RuntimeException("专业为必填项");
            }
            if (phone == null || phone.trim().isEmpty()) {
                throw new RuntimeException("手机号为必填项");
            }
            if (email == null || email.trim().isEmpty()) {
                throw new RuntimeException("邮箱为必填项");
            }

            // 根据专业编码查询专业信息
            QueryWrapper<Major> majorQuery = new QueryWrapper<>();
            majorQuery.eq("code", majorCode);
            Major major = majorMapper.selectOne(majorQuery);
            if (major == null) {
                throw new RuntimeException("无效的专业编码：" + majorCode + "，请联系管理员");
            }

            User user = new User();
            user.setUsername(username);
            user.setPassword(passwordEncoder.encode(password));
            user.setPhone(phone);
            user.setEmail(email);
            user.setRole(4);

            CounselorProfile profile = new CounselorProfile();
            profile.setCollegeId(major.getCollegeId()); // 从专业自动获取学院ID

            Long userId = counselorService.registerCounselor(user, profile);
            Map<String, Object> result = new HashMap<>();
            result.put("userId", userId);
            result.put("username", username);
            result.put("majorCode", majorCode);
            result.put("collegeId", major.getCollegeId());
            log.info("辅导员注册成功: {}，专业编码: {}，学院ID: {}", username, majorCode, major.getCollegeId());
            return ApiResponse.success(result);
        } catch (Exception e) {
            log.error("辅导员注册失败", e);
            return ApiResponse.error(e.getMessage());
        }
    }

    /**
     * 管理员注册
     */
    @PostMapping("/register/admin")
    public ApiResponse<Map<String, Object>> registerAdmin(@RequestBody Map<String, Object> params) {
        try {
            String username = (String) params.get("username");
            String password = (String) params.get("password");
            String name = (String) params.get("name");
            String department = (String) params.get("department");
            String phone = (String) params.get("phone");
            String email = (String) params.get("email");

            User user = new User();
            user.setUsername(username);
            user.setPassword(passwordEncoder.encode(password)); // 密码加密
            user.setPhone(phone);
            user.setEmail(email);
            user.setRole(3);

            AdminProfile profile = new AdminProfile();
            profile.setDepartment(department);

            Map<String, Object> result = new HashMap<>();
            result.put("userId", 0);
            result.put("username", username);
            return ApiResponse.success(result);
        } catch (Exception e) {
            log.error("管理员注册失败", e);
            return ApiResponse.error(e.getMessage());
        }
    }

}
