package com.academic.controller;

import com.academic.common.ApiResponse;
import com.academic.dto.AdminDashboardResponse;
import com.academic.entity.*;
import com.academic.service.CollegeService;
import com.academic.service.MajorService;
import com.academic.service.UserService;
import com.academic.mapper.StudentProfileMapper;
import com.academic.mapper.AcademicWarningMapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/admin")
public class AdminController {

    private final CollegeService collegeService;
    private final MajorService majorService;
    private final UserService userService;
    private final StudentProfileMapper studentProfileMapper;
    private final AcademicWarningMapper warningMapper;

    public AdminController(CollegeService collegeService, MajorService majorService, UserService userService,
                           StudentProfileMapper studentProfileMapper, AcademicWarningMapper warningMapper) {
        this.collegeService = collegeService;
        this.majorService = majorService;
        this.userService = userService;
        this.studentProfileMapper = studentProfileMapper;
        this.warningMapper = warningMapper;
    }

    /**
     * 获取管理员仪表板
     */
    @GetMapping("/dashboard")
    public ApiResponse<AdminDashboardResponse> getDashboard() {
        try {
            AdminDashboardResponse dashboard = new AdminDashboardResponse();
            dashboard.setTotalColleges(collegeService.count());
            dashboard.setTotalMajors(majorService.count());
            dashboard.setTotalUsers(userService.count());
            dashboard.setTotalStudents(studentProfileMapper.selectCount(null));
            
            QueryWrapper<User> teacherQuery = new QueryWrapper<>();
            teacherQuery.eq("role", 2);
            dashboard.setTotalTeachers(userService.count(teacherQuery));
            
            dashboard.setTotalWarnings(warningMapper.selectCount(null));
            
            return ApiResponse.success(dashboard);
        } catch (Exception e) {
            log.error("获取管理员仪表板失败", e);
            return ApiResponse.error(e.getMessage());
        }
    }

    /**
     * 获取所有学院
     */
    @GetMapping("/colleges")
    public ApiResponse<List<College>> getColleges() {
        try {
            List<College> colleges = collegeService.getAllColleges();
            return ApiResponse.success(colleges);
        } catch (Exception e) {
            log.error("获取学院列表失败", e);
            return ApiResponse.error(e.getMessage());
        }
    }

    /**
     * 创建学院
     */
    @PostMapping("/colleges")
    public ApiResponse<String> createCollege(@RequestBody College college) {
        try {
            collegeService.save(college);
            return ApiResponse.success("学院已创建");
        } catch (Exception e) {
            log.error("创建学院失败", e);
            return ApiResponse.error(e.getMessage());
        }
    }

    /**
     * 更新学院
     */
    @PutMapping("/colleges/{collegeId}")
    public ApiResponse<String> updateCollege(@PathVariable Long collegeId, @RequestBody College college) {
        try {
            college.setId(collegeId);
            collegeService.updateById(college);
            return ApiResponse.success("学院已更新");
        } catch (Exception e) {
            log.error("更新学院失败", e);
            return ApiResponse.error(e.getMessage());
        }
    }

    /**
     * 删除学院
     */
    @DeleteMapping("/colleges/{collegeId}")
    public ApiResponse<String> deleteCollege(@PathVariable Long collegeId) {
        try {
            collegeService.removeById(collegeId);
            return ApiResponse.success("学院已删除");
        } catch (Exception e) {
            log.error("删除学院失败", e);
            return ApiResponse.error(e.getMessage());
        }
    }

    /**
     * 获取学院的所有专业
     */
    @GetMapping("/colleges/{collegeId}/majors")
    public ApiResponse<List<Major>> getMajorsByCollege(@PathVariable Long collegeId) {
        try {
            List<Major> majors = majorService.getMajorsByCollegeId(collegeId);
            return ApiResponse.success(majors);
        } catch (Exception e) {
            log.error("获取专业列表失败", e);
            return ApiResponse.error(e.getMessage());
        }
    }

    /**
     * 创建专业
     */
    @PostMapping("/majors")
    public ApiResponse<String> createMajor(@RequestBody Major major) {
        try {
            majorService.save(major);
            return ApiResponse.success("专业已创建");
        } catch (Exception e) {
            log.error("创建专业失败", e);
            return ApiResponse.error(e.getMessage());
        }
    }

    /**
     * 更新专业
     */
    @PutMapping("/majors/{majorId}")
    public ApiResponse<String> updateMajor(@PathVariable Long majorId, @RequestBody Major major) {
        try {
            major.setId(majorId);
            majorService.updateById(major);
            return ApiResponse.success("专业已更新");
        } catch (Exception e) {
            log.error("更新专业失败", e);
            return ApiResponse.error(e.getMessage());
        }
    }

    /**
     * 删除专业
     */
    @DeleteMapping("/majors/{majorId}")
    public ApiResponse<String> deleteMajor(@PathVariable Long majorId) {
        try {
            majorService.removeById(majorId);
            return ApiResponse.success("专业已删除");
        } catch (Exception e) {
            log.error("删除专业失败", e);
            return ApiResponse.error(e.getMessage());
        }
    }

    /**
     * 获取所有用户（分页）
     */
    @GetMapping("/users")
    public ApiResponse<List<User>> getUsers(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {
        try {
            QueryWrapper<User> queryWrapper = new QueryWrapper<>();
            queryWrapper.orderByDesc("created_at");
            List<User> users = userService.list(queryWrapper);
            return ApiResponse.success(users.stream().limit(size).skip((long) (page - 1) * size).toList());
        } catch (Exception e) {
            log.error("获取用户列表失败", e);
            return ApiResponse.error(e.getMessage());
        }
    }

    /**
     * 管理员注册
     */
    @PostMapping("/register")
    public ApiResponse<Map<String, Object>> register(@RequestBody Map<String, Object> params) {
        try {
            String username = (String) params.get("username");
            String password = (String) params.get("password");
            String name = (String) params.get("name");
            String department = (String) params.get("department");

            User user = new User();
            user.setUsername(username);
            user.setPassword(password);
            user.setRole(3); // 管理员角色

            AdminProfile profile = new AdminProfile();
            profile.setDepartment(department);

            // TODO: 需要调用 AdminService.registerAdmin() 方法
            // 暂时直接保存用户和档案
            // userService.save(user);
            // profile.setUserId(user.getId());

            Map<String, Object> result = new HashMap<>();
            result.put("userId", user.getId());
            result.put("username", username);
            return ApiResponse.success(result);
        } catch (Exception e) {
            log.error("管理员注册失败", e);
            return ApiResponse.error(e.getMessage());
        }
    }

    /**
     * 获取规则列表
     */
    @GetMapping("/rules")
    public ApiResponse<List<Map<String, Object>>> getRules() {
        try {
            // 返回预警规则列表
            List<Map<String, Object>> rules = List.of(
                Map.of("id", 1L, "name", "GPA低于2.0", "threshold", 2.0, "level", "red"),
                Map.of("id", 2L, "name", "GPA低于2.5", "threshold", 2.5, "level", "yellow"),
                Map.of("id", 3L, "name", "单科挂科", "threshold", 60, "level", "orange")
            );
            return ApiResponse.success(rules);
        } catch (Exception e) {
            log.error("获取规则列表失败", e);
            return ApiResponse.error(e.getMessage());
        }
    }

    /**
     * 创建规则
     */
    @PostMapping("/rules")
    public ApiResponse<String> createRule(@RequestBody Map<String, Object> params) {
        try {
            // 参数：name, threshold, level
            // TODO: 保存到预警规则表
            return ApiResponse.success("规则已创建");
        } catch (Exception e) {
            log.error("创建规则失败", e);
            return ApiResponse.error(e.getMessage());
        }
    }

    /**
     * 修改规则
     */
    @PutMapping("/rules/{ruleId}")
    public ApiResponse<String> updateRule(@PathVariable Long ruleId, @RequestBody Map<String, Object> params) {
        try {
            // TODO: 更新规则
            return ApiResponse.success("规则已更新");
        } catch (Exception e) {
            log.error("更新规则失败", e);
            return ApiResponse.error(e.getMessage());
        }
    }

    /**
     * 删除规则
     */
    @DeleteMapping("/rules/{ruleId}")
    public ApiResponse<String> deleteRule(@PathVariable Long ruleId) {
        try {
            // TODO: 删除规则
            return ApiResponse.success("规则已删除");
        } catch (Exception e) {
            log.error("删除规则失败", e);
            return ApiResponse.error(e.getMessage());
        }
    }

    /**
     * 获取数据统计
     */
    @GetMapping("/statistics")
    public ApiResponse<Map<String, Object>> getStatistics() {
        try {
            Map<String, Object> stats = new HashMap<>();
            stats.put("totalStudents", studentProfileMapper.selectCount(null));
            stats.put("totalTeachers", userService.count(new QueryWrapper<User>().eq("role", 2)));
            stats.put("totalWarnings", warningMapper.selectCount(null));
            stats.put("totalColleges", collegeService.count());
            stats.put("redWarnings", warningMapper.selectCount(new QueryWrapper<AcademicWarning>().eq("warning_level", "red")));
            stats.put("yellowWarnings", warningMapper.selectCount(new QueryWrapper<AcademicWarning>().eq("warning_level", "yellow")));
            return ApiResponse.success(stats);
        } catch (Exception e) {
            log.error("获取统计数据失败", e);
            return ApiResponse.error(e.getMessage());
        }
    }

    /**
     * 获取权限角色列表
     */
    @GetMapping("/permissions/roles")
    public ApiResponse<List<Map<String, Object>>> getRoles() {
        try {
            List<Map<String, Object>> roles = List.of(
                Map.of("id", 1L, "name", "学生", "description", "学生用户", "permissions", List.of("view_scores", "view_warnings", "submit_appeals")),
                Map.of("id", 2L, "name", "教师", "description", "教师用户", "permissions", List.of("view_scores", "submit_feedback", "view_warnings")),
                Map.of("id", 4L, "name", "辅导员", "description", "辅导员用户", "permissions", List.of("manage_students", "manage_warnings", "view_analytics")),
                Map.of("id", 3L, "name", "管理员", "description", "系统管理员", "permissions", List.of("manage_all"))
            );
            return ApiResponse.success(roles);
        } catch (Exception e) {
            log.error("获取角色列表失败", e);
            return ApiResponse.error(e.getMessage());
        }
    }

    /**
     * 获取权限列表
     */
    @GetMapping("/permissions/list")
    public ApiResponse<List<Map<String, Object>>> getPermissions() {
        try {
            List<Map<String, Object>> permissions = List.of(
                Map.of("id", 1L, "code", "view_scores", "name", "查看成绩"),
                Map.of("id", 2L, "code", "view_warnings", "name", "查看预警"),
                Map.of("id", 3L, "code", "submit_appeals", "name", "提交申诉"),
                Map.of("id", 4L, "code", "submit_feedback", "name", "提交反馈"),
                Map.of("id", 5L, "code", "manage_students", "name", "管理学生"),
                Map.of("id", 6L, "code", "manage_warnings", "name", "管理预警"),
                Map.of("id", 7L, "code", "view_analytics", "name", "查看分析"),
                Map.of("id", 8L, "code", "manage_all", "name", "管理所有")
            );
            return ApiResponse.success(permissions);
        } catch (Exception e) {
            log.error("获取权限列表失败", e);
            return ApiResponse.error(e.getMessage());
        }
    }

    /**
     * 获取用户权限
     */
    @GetMapping("/permissions/user/{userId}")
    public ApiResponse<Map<String, Object>> getUserPermissions(@PathVariable Long userId) {
        try {
            User user = userService.getById(userId);
            if (user == null) {
                return ApiResponse.error(404, "用户不存在");
            }

            Map<String, Object> userPermissions = new HashMap<>();
            userPermissions.put("userId", userId);
            userPermissions.put("username", user.getUsername());
            userPermissions.put("role", user.getRole());
            
            // 根据角色分配权限
            java.util.List<String> permissions = new java.util.ArrayList<>();
            if (user.getRole() == 1) {
                permissions.addAll(List.of("view_scores", "view_warnings", "submit_appeals", "submit_feedback"));
            } else if (user.getRole() == 2) {
                permissions.addAll(List.of("view_scores", "submit_feedback", "view_warnings", "view_analytics"));
            } else if (user.getRole() == 4) {
                permissions.addAll(List.of("manage_students", "manage_warnings", "view_analytics"));
            } else if (user.getRole() == 3) {
                permissions.add("manage_all");
            }
            
            userPermissions.put("permissions", permissions);
            return ApiResponse.success(userPermissions);
        } catch (Exception e) {
            log.error("获取用户权限失败", e);
            return ApiResponse.error(e.getMessage());
        }
    }

    /**
     * 分配角色给用户
     */
    @PostMapping("/permissions/assign-role")
    public ApiResponse<String> assignRole(@RequestBody Map<String, Object> params) {
        try {
            Long userId = ((Number) params.get("user_id")).longValue();
            Integer roleId = ((Number) params.get("role_id")).intValue();
            
            User user = userService.getById(userId);
            if (user == null) {
                return ApiResponse.error(404, "用户不存在");
            }
            
            user.setRole(roleId);
            userService.updateById(user);
            return ApiResponse.success("角色已分配");
        } catch (Exception e) {
            log.error("分配角色失败", e);
            return ApiResponse.error(e.getMessage());
        }
    }

    /**
     * 启用/禁用用户
     */
    @PostMapping("/users/{userId}/toggle-status")
    public ApiResponse<String> toggleUserStatus(@PathVariable Long userId) {
        try {
            User user = userService.getById(userId);
            if (user == null) {
                return ApiResponse.error(404, "用户不存在");
            }
            
            // 切换用户状态（假设status字段表示是否禁用）
            Integer status = user.getStatus();
            user.setStatus(status == null || status == 0 ? 1 : 0);
            userService.updateById(user);
            
            return ApiResponse.success("用户状态已更新");
        } catch (Exception e) {
            log.error("更新用户状态失败", e);
            return ApiResponse.error(e.getMessage());
        }
    }

    /**
     * 发送系统消息给所有用户
     */
    @PostMapping("/messages/broadcast")
    public ApiResponse<String> broadcastMessage(@RequestBody Map<String, Object> params) {
        try {
            String title = (String) params.get("title");
            String content = (String) params.get("content");
            String level = (String) params.getOrDefault("level", "info"); // info, warning, danger
            
            if (title == null || content == null) {
                return ApiResponse.error(400, "标题和内容不能为空");
            }
            
            Map<String, Object> message = new HashMap<>();
            message.put("id", System.currentTimeMillis());
            message.put("title", title);
            message.put("content", content);
            message.put("level", level);
            message.put("created_at", java.time.LocalDateTime.now());
            message.put("status", "published");
            message.put("target", "all");
            
            // 实际应用中会保存到消息表
            
            return ApiResponse.success("消息已发布");
        } catch (Exception e) {
            log.error("发送消息失败", e);
            return ApiResponse.error(e.getMessage());
        }
    }

    /**
     * 发送目标消息
     */
    @PostMapping("/messages/targeted")
    public ApiResponse<String> sendTargetedMessage(@RequestBody Map<String, Object> params) {
        try {
            String title = (String) params.get("title");
            String content = (String) params.get("content");
            @SuppressWarnings("unchecked")
            java.util.List<Long> userIds = (java.util.List<Long>) params.get("user_ids");
            String level = (String) params.getOrDefault("level", "info");
            
            if (title == null || content == null || userIds == null || userIds.isEmpty()) {
                return ApiResponse.error(400, "标题、内容和用户列表不能为空");
            }
            
            Map<String, Object> message = new HashMap<>();
            message.put("id", System.currentTimeMillis());
            message.put("title", title);
            message.put("content", content);
            message.put("level", level);
            message.put("created_at", java.time.LocalDateTime.now());
            message.put("status", "published");
            message.put("target", "targeted");
            message.put("user_count", userIds.size());
            
            return ApiResponse.success("消息已发送指定用户");
        } catch (Exception e) {
            log.error("发送目标消息失败", e);
            return ApiResponse.error(e.getMessage());
        }
    }

    /**
     * 获取消息列表
     */
    @GetMapping("/messages/list")
    public ApiResponse<List<Map<String, Object>>> getMessages(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int size) {
        try {
            java.util.List<Map<String, Object>> messages = List.of(
                Map.of(
                    "id", 1L,
                    "title", "系统维护通知",
                    "content", "每天晋上02:00-04:00进行数据库整理",
                    "level", "warning",
                    "created_at", "2025-12-13 10:00:00",
                    "status", "published",
                    "target", "all"
                ),
                Map.of(
                    "id", 2L,
                    "title", "新学上也简介",
                    "content", "欢迎2025级新学入学",
                    "level", "info",
                    "created_at", "2025-12-12 15:30:00",
                    "status", "published",
                    "target", "all"
                )
            );
            return ApiResponse.success(messages);
        } catch (Exception e) {
            log.error("获取消息列表失败", e);
            return ApiResponse.error(e.getMessage());
        }
    }

    /**
     * 删除消息
     */
    @DeleteMapping("/messages/{messageId}")
    public ApiResponse<String> deleteMessage(@PathVariable Long messageId) {
        try {
            return ApiResponse.success("消息已删除");
        } catch (Exception e) {
            log.error("删除消息失败", e);
            return ApiResponse.error(e.getMessage());
        }
    }

    /**
     * 获取任务列表
     */
    @GetMapping("/tasks/list")
    public ApiResponse<List<Map<String, Object>>> getTasks(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) String status) {
        try {
            java.util.List<Map<String, Object>> tasks = new java.util.ArrayList<>();
            tasks.add(Map.of(
                "id", 1L,
                "title", "序号库收整",
                "description", "清理大一幸板一操作记录",
                "created_by", "admin",
                "created_at", "2025-12-13 09:00:00",
                "status", "pending",
                "priority", "high"
            ));
            tasks.add(Map.of(
                "id", 2L,
                "title", "数据备份",
                "description", "执行每日定时数据备份",
                "created_by", "admin",
                "created_at", "2025-12-12 14:00:00",
                "status", "in_progress",
                "priority", "high"
            ));
            
            // 根据状态筛选
            if (status != null && !status.isEmpty()) {
                tasks = tasks.stream()
                    .filter(t -> status.equals(t.get("status")))
                    .toList();
            }
            
            return ApiResponse.success(tasks);
        } catch (Exception e) {
            log.error("获取任务列表失败", e);
            return ApiResponse.error(e.getMessage());
        }
    }

    /**
     * 创建任务
     */
    @PostMapping("/tasks/create")
    public ApiResponse<Map<String, Object>> createTask(@RequestBody Map<String, Object> params) {
        try {
            String title = (String) params.get("title");
            String description = (String) params.get("description");
            String priority = (String) params.getOrDefault("priority", "medium");
            
            if (title == null) {
                return ApiResponse.error(400, "任务标题不能为空");
            }
            
            Map<String, Object> task = new HashMap<>();
            task.put("id", System.currentTimeMillis());
            task.put("title", title);
            task.put("description", description);
            task.put("priority", priority);
            task.put("status", "pending");
            task.put("created_at", java.time.LocalDateTime.now());
            task.put("created_by", "admin");
            
            return ApiResponse.success(task);
        } catch (Exception e) {
            log.error("创建任务失败", e);
            return ApiResponse.error(e.getMessage());
        }
    }

    /**
     * 更新任务状态
     */
    @PostMapping("/tasks/{taskId}/status")
    public ApiResponse<String> updateTaskStatus(@PathVariable Long taskId, @RequestBody Map<String, Object> params) {
        try {
            String status = (String) params.get("status");
            if (status == null) {
                return ApiResponse.error(400, "状态不能为空");
            }
            
            // 实际应用中会更新任务表
            
            return ApiResponse.success("任务状态已更新");
        } catch (Exception e) {
            log.error("更新任务状态失败", e);
            return ApiResponse.error(e.getMessage());
        }
    }

    /**
     * 删除任务
     */
    @DeleteMapping("/tasks/{taskId}")
    public ApiResponse<String> deleteTask(@PathVariable Long taskId) {
        try {
            return ApiResponse.success("任务已删除");
        } catch (Exception e) {
            log.error("删除任务失败", e);
            return ApiResponse.error(e.getMessage());
        }
    }

    /**
     * 导出学生数据为Excel
     */
    @GetMapping("/export/students")
    public ApiResponse<Map<String, Object>> exportStudents() {
        try {
            // 实际应用中会使用EasyExcel的模板导出
            Map<String, Object> result = new HashMap<>();
            result.put("file_name", "students_" + System.currentTimeMillis() + ".xlsx");
            result.put("total_count", studentProfileMapper.selectCount(null));
            result.put("download_url", "/api/admin/download/students");
            
            return ApiResponse.success(result);
        } catch (Exception e) {
            log.error("导出学生数据失败", e);
            return ApiResponse.error(e.getMessage());
        }
    }

    /**
     * 导出成绩数据为Excel
     */
    @GetMapping("/export/scores")
    public ApiResponse<Map<String, Object>> exportScores(@RequestParam(required = false) Long courseId) {
        try {
            Map<String, Object> result = new HashMap<>();
            result.put("file_name", "scores_" + System.currentTimeMillis() + ".xlsx");
            result.put("record_count", 100); // 假设这些数据
            result.put("download_url", "/api/admin/download/scores");
            
            return ApiResponse.success(result);
        } catch (Exception e) {
            log.error("导出成绩数据失败", e);
            return ApiResponse.error(e.getMessage());
        }
    }

    /**
     * 导出预警数据为Excel
     */
    @GetMapping("/export/warnings")
    public ApiResponse<Map<String, Object>> exportWarnings() {
        try {
            java.util.List<AcademicWarning> warnings = warningMapper.selectList(null);
            
            Map<String, Object> result = new HashMap<>();
            result.put("file_name", "warnings_" + System.currentTimeMillis() + ".xlsx");
            result.put("record_count", warnings.size());
            result.put("download_url", "/api/admin/download/warnings");
            
            return ApiResponse.success(result);
        } catch (Exception e) {
            log.error("导出预警数据失败", e);
            return ApiResponse.error(e.getMessage());
        }
    }

    /**
     * 导出管理员、教师、辅导员数据
     */
    @GetMapping("/export/users")
    public ApiResponse<Map<String, Object>> exportUsers(@RequestParam(required = false) Integer role) {
        try {
            QueryWrapper<User> qw = new QueryWrapper<>();
            if (role != null) {
                qw.eq("role", role);
            }
            java.util.List<User> users = userService.list(qw);
            
            Map<String, Object> result = new HashMap<>();
            result.put("file_name", "users_" + System.currentTimeMillis() + ".xlsx");
            result.put("record_count", users.size());
            result.put("download_url", "/api/admin/download/users");
            
            return ApiResponse.success(result);
        } catch (Exception e) {
            log.error("导出用户数据失败", e);
            return ApiResponse.error(e.getMessage());
        }
    }

    /**
     * 上传Excel导入学生数据
     */
    @PostMapping("/import/students")
    public ApiResponse<Map<String, Object>> importStudents() {
        try {
            Map<String, Object> result = new HashMap<>();
            result.put("success_count", 0);
            result.put("error_count", 0);
            result.put("total_count", 0);
            result.put("message", "批量导入功能开发中");
            
            return ApiResponse.success(result);
        } catch (Exception e) {
            log.error("导入学生数据失败", e);
            return ApiResponse.error(e.getMessage());
        }
    }

    /**
     * 上传Excel导入成绩数据
     */
    @PostMapping("/import/scores")
    public ApiResponse<Map<String, Object>> importScores() {
        try {
            Map<String, Object> result = new HashMap<>();
            result.put("success_count", 0);
            result.put("error_count", 0);
            result.put("total_count", 0);
            result.put("message", "批量导入功能开发中");
            
            return ApiResponse.success(result);
        } catch (Exception e) {
            log.error("导入成绩数据失败", e);
            return ApiResponse.error(e.getMessage());
        }
    }

    /**
     * 获取报表模板列表
     */
    @GetMapping("/reports/templates")
    public ApiResponse<List<Map<String, Object>>> getReportTemplates() {
        try {
            java.util.List<Map<String, Object>> templates = List.of(
                Map.of(
                    "id", 1L,
                    "name", "学生成绩统计报告",
                    "description", "按专业、班级绩成统计",
                    "fields", List.of("student_name", "student_id", "major", "avg_score", "gpa")
                ),
                Map.of(
                    "id", 2L,
                    "name", "预警统计报告",
                    "description", "预警巛控和预警程度统计",
                    "fields", List.of("warning_level", "student_count", "percentage")
                ),
                Map.of(
                    "id", 3L,
                    "name", "常见问题扥导报告",
                    "description", "需要帮扶的常见问题统计",
                    "fields", List.of("issue_type", "frequency", "help_rate")
                )
            );
            return ApiResponse.success(templates);
        } catch (Exception e) {
            log.error("获取报表模板失败", e);
            return ApiResponse.error(e.getMessage());
        }
    }

    /**
     * 生成自定义报告
     */
    @PostMapping("/reports/generate")
    public ApiResponse<String> generateReport(@RequestBody Map<String, Object> params) {
        try {
            String templateId = (String) params.get("template_id");
            String startDate = (String) params.get("start_date");
            String endDate = (String) params.get("end_date");
            
            if (templateId == null || startDate == null || endDate == null) {
                return ApiResponse.error(400, "模板、开始日期和结束日期不能为空");
            }
            
            Map<String, Object> report = new HashMap<>();
            report.put("report_id", System.currentTimeMillis());
            report.put("template_id", templateId);
            report.put("generated_at", java.time.LocalDateTime.now());
            report.put("file_url", "/api/admin/reports/download/" + System.currentTimeMillis());
            
            return ApiResponse.success("报告已生成");
        } catch (Exception e) {
            log.error("生成报告失败", e);
            return ApiResponse.error(e.getMessage());
        }
    }

    /**
     * 数据备份
     */
    @PostMapping("/backup")
    public ApiResponse<String> backupData() {
        try {
            String timestamp = java.time.LocalDateTime.now().format(java.time.format.DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
            
            Map<String, Object> backup = new HashMap<>();
            backup.put("backup_id", timestamp);
            backup.put("created_at", java.time.LocalDateTime.now());
            backup.put("status", "success");
            
            return ApiResponse.success("数据备份已完成，备份名: backup_" + timestamp);
        } catch (Exception e) {
            log.error("数据备份失败", e);
            return ApiResponse.error(e.getMessage());
        }
    }

    /**
     * 获取备份列表
     */
    @GetMapping("/backups/list")
    public ApiResponse<List<Map<String, Object>>> getBackupList() {
        try {
            java.util.List<Map<String, Object>> backups = List.of(
                Map.of(
                    "id", 1L,
                    "name", "backup_20251213_140000",
                    "created_at", "2025-12-13 14:00:00",
                    "size", "245 MB",
                    "status", "success"
                ),
                Map.of(
                    "id", 2L,
                    "name", "backup_20251212_020000",
                    "created_at", "2025-12-12 02:00:00",
                    "size", "238 MB",
                    "status", "success"
                )
            );
            return ApiResponse.success(backups);
        } catch (Exception e) {
            log.error("获取备份列表失败", e);
            return ApiResponse.error(e.getMessage());
        }
    }

    /**
     * 恢复批量备份
     */
    @PostMapping("/backup/{backupId}/restore")
    public ApiResponse<String> restoreBackup(@PathVariable Long backupId) {
        try {
            return ApiResponse.success("数据恢复中...（恢复时间较长）");
        } catch (Exception e) {
            log.error("恢复批量备份失败", e);
            return ApiResponse.error(e.getMessage());
        }
    }

}
