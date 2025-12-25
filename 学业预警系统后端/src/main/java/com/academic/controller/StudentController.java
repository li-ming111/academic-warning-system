package com.academic.controller;

import com.academic.common.ApiResponse;
import com.academic.dto.StudentRegisterRequest;
import com.academic.dto.StudentDashboardResponse;
import com.academic.dto.ScoreResponse;
import com.academic.dto.ScoreAppealRequest;
import com.academic.entity.StudentProfile;
import com.academic.entity.Score;
import com.academic.entity.AcademicWarning;
import com.academic.entity.AssistancePlan;
import com.academic.entity.ScoreAppeal;
import com.academic.entity.CommunicationLog;
import com.academic.service.StudentService;
import com.academic.service.ScoreService;
import com.academic.service.WarningService;
import com.academic.service.AssistancePlanService;
import com.academic.service.ScoreAppealService;
import com.academic.service.CommunicationLogService;
import com.academic.service.UserSettingsService;
import com.academic.service.StatisticsService;
import com.academic.service.ExportService;
import com.academic.service.NotificationService;
import com.academic.service.CacheService;
import com.academic.service.StudentNotificationService;
import com.academic.service.StudentBenchmarkService;
import com.academic.service.StudentAppealService;
import com.academic.service.AssistanceEvaluationService;
import com.academic.entity.SecurityLog;
import com.academic.entity.Notification;
import com.academic.entity.SubscriptionPreference;
import com.academic.entity.BenchmarkAnalysis;
import com.academic.entity.AssistanceEvaluation;
import com.academic.entity.Course;
import com.academic.mapper.CourseMapper;
import com.academic.util.StudentIdParser;
import lombok.extern.slf4j.Slf4j;
import java.util.Map;
import java.util.HashMap;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/students")
public class StudentController {

    private final StudentService studentService;
    private final ScoreService scoreService;
    private final WarningService warningService;
    private final AssistancePlanService assistancePlanService;
    private final ScoreAppealService scoreAppealService;
    private final CommunicationLogService communicationLogService;
    private final UserSettingsService userSettingsService;
    private final StatisticsService statisticsService;
    private final ExportService exportService;
    private final NotificationService notificationService;
    private final CacheService cacheService;
    private final CourseMapper courseMapper;
    private final StudentNotificationService studentNotificationService;
    private final StudentBenchmarkService studentBenchmarkService;
    private final StudentAppealService studentAppealService;
    private final AssistanceEvaluationService assistanceEvaluationService;

    public StudentController(StudentService studentService, ScoreService scoreService,
                             WarningService warningService, AssistancePlanService assistancePlanService,
                             ScoreAppealService scoreAppealService, CommunicationLogService communicationLogService,
                             UserSettingsService userSettingsService, StatisticsService statisticsService, ExportService exportService, NotificationService notificationService, CacheService cacheService, CourseMapper courseMapper, StudentNotificationService studentNotificationService, StudentBenchmarkService studentBenchmarkService, StudentAppealService studentAppealService, AssistanceEvaluationService assistanceEvaluationService) {
        this.studentService = studentService;
        this.scoreService = scoreService;
        this.warningService = warningService;
        this.assistancePlanService = assistancePlanService;
        this.scoreAppealService = scoreAppealService;
        this.communicationLogService = communicationLogService;
        this.userSettingsService = userSettingsService;
        this.statisticsService = statisticsService;
        this.exportService = exportService;
        this.notificationService = notificationService;
        this.cacheService = cacheService;
        this.courseMapper = courseMapper;
        this.studentNotificationService = studentNotificationService;
        this.studentBenchmarkService = studentBenchmarkService;
        this.studentAppealService = studentAppealService;
        this.assistanceEvaluationService = assistanceEvaluationService;
    }

    /**
     * 学生注册
     */
    @PostMapping("/register")
    public ApiResponse<String> register(@RequestBody StudentRegisterRequest request) {
        try {
            studentService.register(request);
            return ApiResponse.success("注册成功");
        } catch (Exception e) {
            log.error("注册失败", e);
            return ApiResponse.error(e.getMessage());
        }
    }

    /**
     * 获取学生信息
     */
    @GetMapping("/{studentId}")
    public ApiResponse<StudentProfile> getStudentInfo(@PathVariable String studentId) {
        try {
            StudentProfile student = studentService.getByStudentId(studentId);
            if (student == null) {
                return ApiResponse.error(404, "学生不存在");
            }
            return ApiResponse.success(student);
        } catch (Exception e) {
            log.error("获取学生信息失败", e);
            return ApiResponse.error(e.getMessage());
        }
    }

    /**
     * 根据userId获取学生信息
     */
    @GetMapping("/student-by-user/{userId}")
    public ApiResponse<StudentProfile> getStudentInfoByUserId(@PathVariable Long userId) {
        try {
            StudentProfile student = studentService.getByUserId(userId);
            if (student == null) {
                return ApiResponse.error(404, "学生不存在");
            }
            return ApiResponse.success(student);
        } catch (Exception e) {
            log.error("根据userId获取学生信息失败", e);
            return ApiResponse.error(e.getMessage());
        }
    }

    /**
     * 获取学生GPA
     */
    @GetMapping("/{studentId}/gpa")
    public ApiResponse<BigDecimal> getStudentGPA(@PathVariable String studentId) {
        try {
            StudentProfile student = studentService.getByStudentId(studentId);
            if (student == null) {
                return ApiResponse.error(404, "学生不存在");
            }
            BigDecimal gpa = studentService.getStudentGPA(student.getId());
            return ApiResponse.success(gpa);
        } catch (Exception e) {
            log.error("获取学生GPA失败", e);
            return ApiResponse.error(e.getMessage());
        }
    }

    /**
     * 获取学业看板数据
     */
    @GetMapping("/dashboard/{userId}")
    public ApiResponse<StudentDashboardResponse> getDashboard(@PathVariable Long userId) {
        try {
            log.info("查询学业看板: userId={}", userId);
            
            String cacheKey = CacheService.STUDENT_DASHBOARD_PREFIX + userId;
            
            // 尝试从缓存获取
            Object cachedDashboard = cacheService.get(cacheKey);
            if (cachedDashboard instanceof StudentDashboardResponse) {
                log.debug("从缓存获取看板数据: userId={}", userId);
                return ApiResponse.success((StudentDashboardResponse) cachedDashboard);
            }
            
            StudentProfile student = studentService.getByUserId(userId);
            log.info("查询student_profile: userId={}, student={}", userId, student);
            
            if (student == null) {
                log.error("学生不存在: userId={}", userId);
                return ApiResponse.error(404, "学生不存在");
            }

            StudentDashboardResponse dashboard = new StudentDashboardResponse();
            // 计算当前学期
            String currentSemester = calculateCurrentSemester();

            // 本学期课程数
            try {
                Integer courseCount = scoreService.getCourseCount(student.getId(), currentSemester);
                dashboard.setCourseCount(courseCount != null ? courseCount : 0);
            } catch (Exception e) {
                log.warn("查询课程数失败", e);
                dashboard.setCourseCount(0);
            }

            // GPA
            try {
                BigDecimal gpa = scoreService.calculateGPA(student.getId());
                dashboard.setGpa(gpa != null ? gpa : java.math.BigDecimal.ZERO);
            } catch (Exception e) {
                log.warn("计算GPA失败", e);
                dashboard.setGpa(java.math.BigDecimal.ZERO);
            }

            // 预警统计
            try {
                // 先生成当前学期的预警
                warningService.generateWarningsByFailedCount(student.getId(), currentSemester);
                Long warningCount = warningService.countWarnings(student.getId());
                dashboard.setWarningCount(warningCount != null ? warningCount : 0L);
                Long redWarnings = warningService.countWarningsByLevel(student.getId(), "high");
                dashboard.setRedWarnings(redWarnings != null ? redWarnings : 0L);
                Long yellowWarnings = warningService.countWarningsByLevel(student.getId(), "medium");
                dashboard.setYellowWarnings(yellowWarnings != null ? yellowWarnings : 0L);
            } catch (Exception e) {
                log.warn("查询预警信息失败", e);
                dashboard.setWarningCount(0L);
                dashboard.setRedWarnings(0L);
                dashboard.setYellowWarnings(0L);
            }

            // 帮扶计划数
            try {
                List<AssistancePlan> plans = assistancePlanService.getPlansByStudent(student.getId());
                dashboard.setAssistanceCount((long) (plans != null ? plans.size() : 0));
            } catch (Exception e) {
                log.warn("查询帮扶计划失败", e);
                dashboard.setAssistanceCount(0L);
            }
            
            // 存入缓存（1小时过期）
            cacheService.set(cacheKey, dashboard, CacheService.CACHE_TIMEOUT_HOUR, java.util.concurrent.TimeUnit.HOURS);
            log.debug("看板数据已缓存: userId={}", userId);

            return ApiResponse.success(dashboard);
        } catch (Exception e) {
            log.error("获取学业看板失败", e);
            return ApiResponse.error(e.getMessage());
        }
    }

    /**
     * 获取学生成绩列表
     */
    @GetMapping("/scores/{userId}")
    public ApiResponse<List<ScoreResponse>> getScores(@PathVariable Long userId,
                                                      @RequestParam(required = false) String semester) {
        try {
            log.info("查询成绩: userId={}, semester={}", userId, semester);
            
            // 先查询学生信息获取studentId
            StudentProfile student = studentService.getByUserId(userId);
            if (student == null) {
                log.warn("学生不存在: userId={}", userId);
                return ApiResponse.success(new java.util.ArrayList<>());
            }
            
            List<Score> scores = scoreService.getStudentScores(student.getId(), semester);
            log.info("查询到 {} 条成绩", scores.size());
            List<ScoreResponse> responses = new java.util.ArrayList<>();
            
            for (Score score : scores) {
                ScoreResponse response = new ScoreResponse();
                response.setId(score.getId());
                response.setScoreTotal(score.getScoreTotal());
                response.setGradePoint(score.getGradePoint());
                response.setSemester(score.getSemester());
                response.setCreatedAt(score.getCreatedAt());

                // 直接从 Score 对象获取 courseId，用 SQL 查询课程名称和学分
                if (score.getCourseId() != null) {
                    try {
                        // 调用 Service 方法通过 SQL 查询课程信息
                        var courseInfo = scoreService.getCourseInfo(score.getCourseId());
                        if (courseInfo != null) {
                            response.setCourseName((String) courseInfo.get("name"));
                            Object credits = courseInfo.get("credits");
                            if (credits != null) {
                                response.setCredits(new java.math.BigDecimal(credits.toString()));
                            }
                            log.debug("成绩ID={}, 课程ID={}, 课程名={}, 学分={}", score.getId(), score.getCourseId(), courseInfo.get("name"), courseInfo.get("credits"));
                        } else {
                            log.warn("课程不存在: courseId={}", score.getCourseId());
                        }
                    } catch (Exception e) {
                        log.error("查询课程信息失败: courseId={}", score.getCourseId(), e);
                    }
                }
                responses.add(response);
            }

            return ApiResponse.success(responses);
        } catch (Exception e) {
            log.error("获取学生成绩失败", e);
            return ApiResponse.success(new java.util.ArrayList<>());
        }
    }

    /**
     * 获取学生预警列表
     */
    @GetMapping("/warnings/{userId}")
    public ApiResponse<List<AcademicWarning>> getWarnings(@PathVariable Long userId) {
        try {
            StudentProfile student = studentService.getByUserId(userId);
            if (student == null) {
                return ApiResponse.success(new java.util.ArrayList<>());
            }
            List<AcademicWarning> warnings = warningService.getStudentWarnings(student.getId());
            return ApiResponse.success(warnings != null ? warnings : new java.util.ArrayList<>());
        } catch (Exception e) {
            log.error("获取学生预警失败", e);
            return ApiResponse.success(new java.util.ArrayList<>());
        }
    }

    /**
     * 获取学生帮扶计划
     */
    @GetMapping("/assistance/{userId}")
    public ApiResponse<List<AssistancePlan>> getAssistancePlans(@PathVariable Long userId) {
        try {
            StudentProfile student = studentService.getByUserId(userId);
            if (student == null) {
                return ApiResponse.success(new java.util.ArrayList<>());
            }
            List<AssistancePlan> plans = assistancePlanService.getPlansByStudent(student.getId());
            if (student == null) {
                return ApiResponse.success(new java.util.ArrayList<>());
            }
            return ApiResponse.success(plans);
        } catch (Exception e) {
            log.error("获取帮扶计划失败", e);
            return ApiResponse.error(e.getMessage());
        }
    }

    /**
     * 更新帮扶计划进度
     */
    @PostMapping("/assistance/{planId}/progress")
    public ApiResponse<String> updatePlanProgress(@PathVariable Long planId,
                                                  @RequestParam Double progress) {
        try {
            assistancePlanService.updateProgress(planId, progress);
            return ApiResponse.success("进度已更新");
        } catch (Exception e) {
            log.error("更新帮扶计划进度失败", e);
            return ApiResponse.error(e.getMessage());
        }
    }

    // ============= 旧版API已删除，使用新版 /appeals/submit 替代 =============

    /**
     * 发送沟通消息
     */
    @PostMapping("/messages")
    public ApiResponse<String> sendMessage(@RequestBody CommunicationLog message) {
        try {
            boolean success = communicationLogService.sendMessage(message);
            if (success) {
                return ApiResponse.success("消息已发送");
            }
            return ApiResponse.error("发送失败");
        } catch (Exception e) {
            log.error("发送消息失败", e);
            return ApiResponse.error(e.getMessage());
        }
    }

    /**
     * 获取学生的沟通记录
     */
    @GetMapping("/messages/{userId}")
    public ApiResponse<List<CommunicationLog>> getMessages(@PathVariable Long userId) {
        try {
            StudentProfile student = studentService.getByUserId(userId);
            if (student == null) {
                return ApiResponse.success(new java.util.ArrayList<>());
            }
            List<CommunicationLog> messages = communicationLogService.getStudentMessages(student.getId());
            return ApiResponse.success(messages);
        } catch (Exception e) {
            log.error("获取沟通记录失败", e);
            return ApiResponse.error(e.getMessage());
        }
    }

    /**
     * 获取未读消息数
     */
    @GetMapping("/messages/{userId}/unread-count")
    public ApiResponse<Long> getUnreadMessageCount(@PathVariable Long userId) {
        try {
            Long count = communicationLogService.getUnreadCount(userId);
            return ApiResponse.success(count);
        } catch (Exception e) {
            log.error("获取未读消息数失败", e);
            return ApiResponse.error(e.getMessage());
        }
    }

    /**
     * 标记消息为已读
     */
    @PostMapping("/messages/{messageId}/read")
    public ApiResponse<String> markMessageAsRead(@PathVariable Long messageId) {
        try {
            boolean success = communicationLogService.markAsRead(messageId);
            if (success) {
                return ApiResponse.success("已标记为已读");
            }
            return ApiResponse.error("标记失败");
        } catch (Exception e) {
            log.error("标记消息为已读失败", e);
            return ApiResponse.error(e.getMessage());
        }
    }

    /**
     * 获取用户设置信息
     */
    @GetMapping("/settings/{userId}")
    public ApiResponse<StudentProfile> getUserSettings(@PathVariable Long userId) {
        try {
            StudentProfile settings = userSettingsService.getUserSettings(userId);
            if (settings == null) {
                return ApiResponse.error(404, "用户不存在");
            }
            return ApiResponse.success(settings);
        } catch (Exception e) {
            log.error("获取用户设置失败", e);
            return ApiResponse.error(e.getMessage());
        }
    }

    /**
     * 修改密码
     */
    @PostMapping("/settings/{userId}/change-password")
    public ApiResponse<String> changePassword(@PathVariable Long userId,
                                             @RequestParam String oldPassword,
                                             @RequestParam String newPassword) {
        try {
            boolean success = userSettingsService.changePassword(userId, oldPassword, newPassword);
            if (success) {
                return ApiResponse.success("密码修改成功");
            }
            return ApiResponse.error("密码修改失败，请检查原密码");
        } catch (Exception e) {
            log.error("修改密码失败", e);
            return ApiResponse.error(e.getMessage());
        }
    }

    /**
     * 更新隐私级别
     */
    @PostMapping("/settings/{userId}/privacy-level")
    public ApiResponse<String> updatePrivacyLevel(@PathVariable Long userId,
                                                 @RequestParam Integer privacyLevel) {
        try {
            boolean success = userSettingsService.updatePrivacyLevel(userId, privacyLevel);
            if (success) {
                return ApiResponse.success("隐私设置已更新");
            }
            return ApiResponse.error("更新失败");
        } catch (Exception e) {
            log.error("更新隐私级别失败", e);
            return ApiResponse.error(e.getMessage());
        }
    }

    /**
     * 获取安全日志
     */
    @GetMapping("/settings/{userId}/security-logs")
    public ApiResponse<List<SecurityLog>> getSecurityLogs(@PathVariable Long userId,
                                                         @RequestParam(defaultValue = "5") Integer limit) {
        try {
            List<SecurityLog> logs = userSettingsService.getSecurityLogs(userId, limit);
            return ApiResponse.success(logs);
        } catch (Exception e) {
            log.error("获取安全日志失败", e);
            return ApiResponse.error(e.getMessage());
        }
    }

    /**
     * 获取学生统计分析数据
     */
    @GetMapping("/statistics/{userId}")
    public ApiResponse<com.academic.dto.StatisticsResponse> getStatistics(@PathVariable Long userId) {
        try {
            String cacheKey = CacheService.STUDENT_STATISTICS_PREFIX + userId;
            
            // 尝试从缓存获取
            Object cachedStatistics = cacheService.get(cacheKey);
            if (cachedStatistics instanceof com.academic.dto.StatisticsResponse) {
                log.debug("从缓存获取统计数据: userId={}", userId);
                return ApiResponse.success((com.academic.dto.StatisticsResponse) cachedStatistics);
            }
            
            com.academic.dto.StatisticsResponse statistics = statisticsService.getStudentStatistics(userId);
            
            // 存入缓存（1小时过期）
            cacheService.set(cacheKey, statistics, CacheService.CACHE_TIMEOUT_HOUR, java.util.concurrent.TimeUnit.HOURS);
            log.debug("统计数据已缓存: userId={}", userId);
            
            return ApiResponse.success(statistics);
        } catch (Exception e) {
            log.error("获取统计数据失败", e);
            return ApiResponse.error(e.getMessage());
        }
    }

    /**
     * 导出成绩Excel
     */
    @GetMapping("/export/scores/{userId}/excel")
    public ApiResponse<String> exportScoresExcel(@PathVariable Long userId) {
        try {
            StudentProfile student = studentService.getByUserId(userId);
            if (student == null) {
                return ApiResponse.error(404, "学生不存在");
            }

            List<Score> scores = scoreService.getStudentScores(userId, null);
            java.io.ByteArrayOutputStream outputStream = exportService.exportScoresToExcel(userId, scores, student.getName());

            // 返回成功，前端根据这个信号下载文件
            return ApiResponse.success("成绩导出成功");
        } catch (Exception e) {
            log.error("导出成绩失败", e);
            return ApiResponse.error(e.getMessage());
        }
    }

    /**
     * 下载成绩Excel文件
     */
    @GetMapping("/download/scores/{userId}")
    public org.springframework.http.ResponseEntity<byte[]> downloadScoresExcel(@PathVariable Long userId) {
        try {
            StudentProfile student = studentService.getByUserId(userId);
            if (student == null) {
                return org.springframework.http.ResponseEntity.notFound().build();
            }

            List<Score> scores = scoreService.getStudentScores(userId, null);
            java.io.ByteArrayOutputStream outputStream = exportService.exportScoresToExcel(userId, scores, student.getName());

            org.springframework.http.HttpHeaders headers = new org.springframework.http.HttpHeaders();
            headers.setContentType(org.springframework.http.MediaType.APPLICATION_OCTET_STREAM);
            headers.set(org.springframework.http.HttpHeaders.CONTENT_DISPOSITION, 
                    "attachment; filename=scores_" + System.currentTimeMillis() + ".xlsx");

            return org.springframework.http.ResponseEntity.ok()
                    .headers(headers)
                    .body(outputStream.toByteArray());
        } catch (Exception e) {
            log.error("下载成绩失败", e);
            return org.springframework.http.ResponseEntity.internalServerError().build();
        }
    }

    // ============= 旧版通知API已删除，使用新版 /notification-center 替代 =============

    /**
     * 为学生生成预警（根据挂科数量）
     */
    @PostMapping("/{studentId}/generate-warnings")
    public ApiResponse<String> generateWarnings(@PathVariable Long studentId,
                                                 @RequestParam(required = false) String semester) {
        try {
            String currentSemester = semester != null ? semester : "2024-2025春";
            warningService.generateWarningsByFailedCount(studentId, currentSemester);
            return ApiResponse.success("预警已根据挂科数量生成");
        } catch (Exception e) {
            log.error("生成预警失败", e);
            return ApiResponse.error(e.getMessage());
        }
    }

    // ========== 新增：StudentNotificationService指提的API ==========

    /**
     * 获取学生未读通知列表
     */
    @GetMapping("/notification-center/{userId}/unread")
    public ApiResponse<List<Notification>> getUnreadNotifications(@PathVariable Long userId) {
        try {
            List<Notification> notifications = studentNotificationService.getUnreadNotifications(userId);
            return ApiResponse.success(notifications);
        } catch (Exception e) {
            log.error("获取未读通知失败", e);
            return ApiResponse.error(e.getMessage());
        }
    }

    /**
     * 获取所有通知（分页）
     */
    @GetMapping("/notification-center/{userId}/list")
    public ApiResponse<java.util.Map<String, Object>> getNotificationsList(@PathVariable Long userId,
                                                                             @RequestParam(defaultValue = "1") int page,
                                                                             @RequestParam(defaultValue = "10") int pageSize) {
        try {
            java.util.Map<String, Object> result = studentNotificationService.getNotifications(userId, page, pageSize);
            return ApiResponse.success(result);
        } catch (Exception e) {
            log.error("获取通知列表失败", e);
            return ApiResponse.error(e.getMessage());
        }
    }

    /**
     * 批量标记通知为已读
     */
    @PostMapping("/notification-center/{userId}/mark-batch-read")
    public ApiResponse<String> markMultipleAsRead(@PathVariable Long userId,
                                                   @RequestBody List<Long> notificationIds) {
        try {
            boolean success = studentNotificationService.markMultipleAsRead(notificationIds);
            if (success) {
                return ApiResponse.success("通知已标记为已读");
            }
            return ApiResponse.error("标记失败");
        } catch (Exception e) {
            log.error("批量标记通知失败", e);
            return ApiResponse.error(e.getMessage());
        }
    }

    /**
     * 软删除通知
     */
    @PostMapping("/notification-center/{notificationId}/delete")
    public ApiResponse<String> deleteNotificationMsg(@PathVariable Long notificationId) {
        try {
            boolean success = studentNotificationService.deleteNotification(notificationId);
            if (success) {
                return ApiResponse.success("通知已删除");
            }
            return ApiResponse.error("删除失败");
        } catch (Exception e) {
            log.error("删除通知失败", e);
            return ApiResponse.error(e.getMessage());
        }
    }

    /**
     * 获取未读通知数量
     */
    @GetMapping("/notification-center/{userId}/unread-count")
    public ApiResponse<Integer> getUnreadCount(@PathVariable Long userId) {
        try {
            int count = studentNotificationService.getUnreadCount(userId);
            return ApiResponse.success(count);
        } catch (Exception e) {
            log.error("获取未读通知数失败", e);
            return ApiResponse.error(e.getMessage());
        }
    }

    /**
     * 清空所有已读通知
     */
    @PostMapping("/notification-center/{userId}/clear-read")
    public ApiResponse<String> clearReadNotifications(@PathVariable Long userId) {
        try {
            boolean success = studentNotificationService.clearReadNotifications(userId);
            if (success) {
                return ApiResponse.success("已读通知已清空");
            }
            return ApiResponse.error("清空失败");
        } catch (Exception e) {
            log.error("清空已读通知失败", e);
            return ApiResponse.error(e.getMessage());
        }
    }

    // ========== 订阅偏好管理 ==========

    /**
     * 获取学生订阅偏好
     */
    @GetMapping("/subscription/{studentId}/preferences")
    public ApiResponse<SubscriptionPreference> getSubscriptionPreferences(@PathVariable Long studentId) {
        try {
            SubscriptionPreference pref = studentNotificationService.getSubscriptionPreferences(studentId);
            return ApiResponse.success(pref);
        } catch (Exception e) {
            log.error("获取订阅偏好失败", e);
            return ApiResponse.error(e.getMessage());
        }
    }

    /**
     * 更新订阅偏好
     */
    @PostMapping("/subscription/{studentId}/update")
    public ApiResponse<String> updateSubscriptionPreferences(@PathVariable Long studentId,
                                                             @RequestBody SubscriptionPreference preference) {
        try {
            preference.setStudentId(studentId);
            boolean success = studentNotificationService.updateSubscriptionPreferences(preference);
            if (success) {
                return ApiResponse.success("订阅偏好已更新");
            }
            return ApiResponse.error("更新失败");
        } catch (Exception e) {
            log.error("更新订阅偏好失败", e);
            return ApiResponse.error(e.getMessage());
        }
    }

    /**
     * 订阅特定预警等级
     */
    @PostMapping("/subscription/{studentId}/subscribe-level")
    public ApiResponse<String> subscribeWarningLevel(@PathVariable Long studentId,
                                                    @RequestParam String level) {
        try {
            boolean success = studentNotificationService.subscribeWarningLevel(studentId, level);
            if (success) {
                return ApiResponse.success("订阅成功");
            }
            return ApiResponse.error("订阅失败");
        } catch (Exception e) {
            log.error("订阅预警等级失败", e);
            return ApiResponse.error(e.getMessage());
        }
    }

    /**
     * 取消订阅特定预警等级
     */
    @PostMapping("/subscription/{studentId}/unsubscribe-level")
    public ApiResponse<String> unsubscribeWarningLevel(@PathVariable Long studentId,
                                                      @RequestParam String level) {
        try {
            boolean success = studentNotificationService.unsubscribeWarningLevel(studentId, level);
            if (success) {
                return ApiResponse.success("取消订阅成功");
            }
            return ApiResponse.error("取消订阅失败");
        } catch (Exception e) {
            log.error("取消订阅预警等级失败", e);
            return ApiResponse.error(e.getMessage());
        }
    }

    /**
     * 设置推送渠道
     */
    @PostMapping("/subscription/{studentId}/set-channel")
    public ApiResponse<String> setPushChannel(@PathVariable Long studentId,
                                             @RequestParam String channel,
                                             @RequestParam boolean enabled) {
        try {
            boolean success = studentNotificationService.setPushChannel(studentId, channel, enabled);
            if (success) {
                return ApiResponse.success("推送渠道设置成功");
            }
            return ApiResponse.error("设置失败");
        } catch (Exception e) {
            log.error("设置推送渠道失败", e);
            return ApiResponse.error(e.getMessage());
        }
    }

    /**
     * 获取学生最新学期对标分析数据
     */
    @GetMapping("/benchmark/{studentId}/latest")
    public ApiResponse<BenchmarkAnalysis> getLatestBenchmark(@PathVariable Long studentId) {
        try {
            BenchmarkAnalysis benchmark = studentBenchmarkService.getLatestBenchmark(studentId);
            if (benchmark == null) {
                return ApiResponse.error(404, "暂无对标分析数据");
            }
            return ApiResponse.success(benchmark);
        } catch (Exception e) {
            log.error("获取对标分析数据失败", e);
            return ApiResponse.error(e.getMessage());
        }
    }

    /**
     * 获取学生指定学期对标分析数据
     */
    @GetMapping("/benchmark/{studentId}/semester")
    public ApiResponse<BenchmarkAnalysis> getBenchmarkBySemester(@PathVariable Long studentId,
                                                                 @RequestParam String semester) {
        try {
            BenchmarkAnalysis benchmark = studentBenchmarkService.getBenchmarkBySemester(studentId, semester);
            if (benchmark == null) {
                return ApiResponse.error(404, "该学期暂无对标分析数据");
            }
            return ApiResponse.success(benchmark);
        } catch (Exception e) {
            log.error("获取对标分析数据失败", e);
            return ApiResponse.error(e.getMessage());
        }
    }

    /**
     * 获取学生历史对标分析数据
     */
    @GetMapping("/benchmark/{studentId}/history")
    public ApiResponse<List<BenchmarkAnalysis>> getHistoryBenchmark(@PathVariable Long studentId) {
        try {
            List<BenchmarkAnalysis> benchmarks = studentBenchmarkService.getHistoryBenchmark(studentId);
            return ApiResponse.success(benchmarks);
        } catch (Exception e) {
            log.error("获取历史对标分析数据失败", e);
            return ApiResponse.error(e.getMessage());
        }
    }

    /**
     * 获取班级排名信息
     */
    @GetMapping("/benchmark/{studentId}/class-ranking")
    public ApiResponse<Map<String, Object>> getClassRanking(@PathVariable Long studentId,
                                                            @RequestParam Long classId,
                                                            @RequestParam String semester) {
        try {
            Map<String, Object> ranking = studentBenchmarkService.getClassRanking(studentId, classId, semester);
            return ApiResponse.success(ranking);
        } catch (Exception e) {
            log.error("获取班级排名失败", e);
            return ApiResponse.error(e.getMessage());
        }
    }

    /**
     * 获取专业排名信息
     */
    @GetMapping("/benchmark/{studentId}/major-ranking")
    public ApiResponse<Map<String, Object>> getMajorRanking(@PathVariable Long studentId,
                                                            @RequestParam Long majorId,
                                                            @RequestParam String semester) {
        try {
            Map<String, Object> ranking = studentBenchmarkService.getMajorRanking(studentId, majorId, semester);
            return ApiResponse.success(ranking);
        } catch (Exception e) {
            log.error("获取专业排名失败", e);
            return ApiResponse.error(e.getMessage());
        }
    }

    /**
     * 获取班级对标对比数据
     */
    @GetMapping("/benchmark/class/{classId}/comparison")
    public ApiResponse<List<BenchmarkAnalysis>> getClassBenchmarkComparison(@PathVariable Long classId,
                                                                            @RequestParam String semester) {
        try {
            List<BenchmarkAnalysis> benchmarks = studentBenchmarkService.getClassBenchmarkComparison(classId, semester);
            return ApiResponse.success(benchmarks);
        } catch (Exception e) {
            log.error("获取班级对标对比数据失败", e);
            return ApiResponse.error(e.getMessage());
        }
    }

    /**
     * 获取专业对标对比数据
     */
    @GetMapping("/benchmark/major/{majorId}/comparison")
    public ApiResponse<List<BenchmarkAnalysis>> getMajorBenchmarkComparison(@PathVariable Long majorId,
                                                                            @RequestParam String semester) {
        try {
            List<BenchmarkAnalysis> benchmarks = studentBenchmarkService.getMajorBenchmarkComparison(majorId, semester);
            return ApiResponse.success(benchmarks);
        } catch (Exception e) {
            log.error("获取专业对标对比数据失败", e);
            return ApiResponse.error(e.getMessage());
        }
    }

    /**
     * 获取学生进度报告
     */
    @GetMapping("/benchmark/{studentId}/progress-report")
    public ApiResponse<Map<String, Object>> getProgressReport(@PathVariable Long studentId) {
        try {
            Map<String, Object> report = studentBenchmarkService.getProgressReport(studentId);
            return ApiResponse.success(report);
        } catch (Exception e) {
            log.error("获取进度报告失败", e);
            return ApiResponse.error(e.getMessage());
        }
    }

    /**
     * 提交成绩申诉
     */
    @PostMapping("/appeals/submit")
    public ApiResponse<String> submitAppeal(@RequestBody ScoreAppeal appeal) {
        try {
            boolean success = studentAppealService.submitAppeal(appeal);
            if (success) {
                return ApiResponse.success("申诉提交成功");
            }
            return ApiResponse.error("申诉提交失败");
        } catch (Exception e) {
            log.error("提交申诉失败", e);
            return ApiResponse.error(e.getMessage());
        }
    }

    /**
     * 获取学生的所有申诉记录
     */
    @GetMapping("/appeals/{studentId}/list")
    public ApiResponse<List<ScoreAppeal>> getStudentAppeals(@PathVariable Long studentId) {
        try {
            List<ScoreAppeal> appeals = studentAppealService.getStudentAppeals(studentId);
            return ApiResponse.success(appeals);
        } catch (Exception e) {
            log.error("获取申诉记录失败", e);
            return ApiResponse.error(e.getMessage());
        }
    }

    /**
     * 获取待处理申诉
     */
    @GetMapping("/appeals/{studentId}/pending")
    public ApiResponse<List<ScoreAppeal>> getPendingAppeals(@PathVariable Long studentId) {
        try {
            List<ScoreAppeal> appeals = studentAppealService.getPendingAppeals(studentId);
            return ApiResponse.success(appeals);
        } catch (Exception e) {
            log.error("获取待处理申诉失败", e);
            return ApiResponse.error(e.getMessage());
        }
    }

    /**
     * 获取申诉视情
     */
    @GetMapping("/appeals/{appealId}/detail")
    public ApiResponse<ScoreAppeal> getAppealDetail(@PathVariable Long appealId) {
        try {
            ScoreAppeal appeal = studentAppealService.getAppealDetail(appealId);
            if (appeal == null) {
                return ApiResponse.error(404, "申诉不存在");
            }
            return ApiResponse.success(appeal);
        } catch (Exception e) {
            log.error("获取申诉详情失败", e);
            return ApiResponse.error(e.getMessage());
        }
    }

    /**
     * 撤销申诉
     */
    @PostMapping("/appeals/{appealId}/withdraw")
    public ApiResponse<String> withdrawAppeal(@PathVariable Long appealId) {
        try {
            boolean success = studentAppealService.withdrawAppeal(appealId);
            if (success) {
                return ApiResponse.success("撤销成功");
            }
            return ApiResponse.error("撤销失败，仅有待处理的申诉可以撤销");
        } catch (Exception e) {
            log.error("撤销申诉失败", e);
            return ApiResponse.error(e.getMessage());
        }
    }

    /**
     * 获取申诉统计信息
     */
    @GetMapping("/appeals/{studentId}/statistics")
    public ApiResponse<Map<String, Object>> getAppealStatistics(@PathVariable Long studentId) {
        try {
            Map<String, Object> stats = studentAppealService.getAppealStatistics(studentId);
            return ApiResponse.success(stats);
        } catch (Exception e) {
            log.error("获取申诉统计信息失败", e);
            return ApiResponse.error(e.getMessage());
        }
    }

    /**
     * 获取申诉成功率
     */
    @GetMapping("/appeals/{studentId}/success-rate")
    public ApiResponse<Map<String, Object>> getAppealSuccessRate(@PathVariable Long studentId) {
        try {
            Double successRate = studentAppealService.getAppealSuccessRate(studentId);
            Map<String, Object> result = new HashMap<>();
            result.put("successRate", successRate);
            return ApiResponse.success(result);
        } catch (Exception e) {
            log.error("获取申诉成功率失败", e);
            return ApiResponse.error(e.getMessage());
        }
    }

    /**
     * 按状态获取申诉
     */
    @GetMapping("/appeals/{studentId}/by-status")
    public ApiResponse<List<ScoreAppeal>> getAppealsByStatus(@PathVariable Long studentId, @RequestParam String status) {
        try {
            List<ScoreAppeal> appeals = studentAppealService.getAppealsByStatus(studentId, status);
            return ApiResponse.success(appeals);
        } catch (Exception e) {
            log.error("按状态获取申诉失败", e);
            return ApiResponse.error(e.getMessage());
        }
    }

    /**
     * 获取申诉历史（分页）
     */
    @GetMapping("/appeals/{studentId}/history")
    public ApiResponse<Map<String, Object>> getAppealHistory(@PathVariable Long studentId,
                                                             @RequestParam(defaultValue = "1") int page,
                                                             @RequestParam(defaultValue = "10") int pageSize) {
        try {
            Map<String, Object> history = studentAppealService.getAppealHistory(studentId, page, pageSize);
            return ApiResponse.success(history);
        } catch (Exception e) {
            log.error("获取申诉历史失败", e);
            return ApiResponse.error(e.getMessage());
        }
    }

    /**
     * 获取申诉原因分类统计
     */
    @GetMapping("/appeals/{studentId}/reason-statistics")
    public ApiResponse<Map<String, Long>> getAppealReasonStatistics(@PathVariable Long studentId) {
        try {
            Map<String, Long> stats = studentAppealService.getAppealReasonStatistics(studentId);
            return ApiResponse.success(stats);
        } catch (Exception e) {
            log.error("获取申诉原因统计失败", e);
            return ApiResponse.error(e.getMessage());
        }
    }

    // ============= 帮扶反馈API =============

    /**
     * 提交帮扶评价
     */
    @PostMapping("/evaluations/submit")
    public ApiResponse<String> submitEvaluation(@RequestBody AssistanceEvaluation evaluation) {
        try {
            boolean success = assistanceEvaluationService.submitEvaluation(evaluation);
            return success ? ApiResponse.success("评价已提交") : ApiResponse.error("提交失败");
        } catch (Exception e) {
            log.error("提交评价失败", e);
            return ApiResponse.error(e.getMessage());
        }
    }

    /**
     * 获取学生的评价列表
     */
    @GetMapping("/evaluations/{studentId}/list")
    public ApiResponse<List<AssistanceEvaluation>> getStudentEvaluations(@PathVariable Long studentId) {
        try {
            List<AssistanceEvaluation> evaluations = assistanceEvaluationService.getStudentEvaluations(studentId);
            return ApiResponse.success(evaluations);
        } catch (Exception e) {
            log.error("获取评价列表失败", e);
            return ApiResponse.error(e.getMessage());
        }
    }

    /**
     * 获取计划的评价详情
     */
    @GetMapping("/evaluations/{planId}/detail")
    public ApiResponse<AssistanceEvaluation> getPlanEvaluation(@PathVariable Long planId) {
        try {
            AssistanceEvaluation evaluation = assistanceEvaluationService.getPlanEvaluation(planId);
            return ApiResponse.success(evaluation);
        } catch (Exception e) {
            log.error("获取评价详情失败", e);
            return ApiResponse.error(e.getMessage());
        }
    }

    /**
     * 获取评价统计数据
     */
    @GetMapping("/evaluations/{studentId}/statistics")
    public ApiResponse<Map<String, Object>> getEvaluationStatistics(@PathVariable Long studentId) {
        try {
            Map<String, Object> stats = assistanceEvaluationService.getEvaluationStatistics(studentId);
            return ApiResponse.success(stats);
        } catch (Exception e) {
            log.error("获取评价统计失败", e);
            return ApiResponse.error(e.getMessage());
        }
    }

    /**
     * 计算当前学期
     * 格式: YYYY-YYYY-1 (秋季) 或 YYYY-YYYY-2 (春季)
     * 每年9月到次年2月为秋季（第1学期）
     * 每年3月到8月为春季（第2学期）
     */
    private String calculateCurrentSemester() {
        java.time.LocalDate today = java.time.LocalDate.now();
        int year = today.getYear();
        int month = today.getMonthValue();
        
        if (month >= 9) {
            // 9月到12月，当前是秋季
            return year + "-" + (year + 1) + "-1";
        } else {
            // 1月到8月，上一年的秋季还在进行或者是春季
            if (month >= 3) {
                // 3月到8月，春季
                return (year - 1) + "-" + year + "-2";
            } else {
                // 1月到2月，上一年秋季到春季
                return (year - 1) + "-" + year + "-2";
            }
        }
    }

    /**
     * 根据学号获取班级信息
     * 根据StudentIdParser解析学号中的班级编码，返回对应的班级信息
     */
    @GetMapping("/{studentId}/class-info")
    public ApiResponse<Map<String, Object>> getClassInfo(@PathVariable String studentId) {
        try {
            // 解析学号
            StudentIdParser.StudentIdInfo idInfo = StudentIdParser.parseStudentId(studentId);
            if (!idInfo.getValid()) {
                return ApiResponse.error("学号格式无效：" + idInfo.getErrorMessage());
            }

            Map<String, Object> result = new HashMap<>();
            result.put("studentId", studentId);
            result.put("enrollmentYear", idInfo.getEnrollmentYear());
            result.put("majorCode", idInfo.getMajorCode());
            result.put("classCode", idInfo.getClassCode());
            result.put("rankInClass", idInfo.getRankInClass());

            // 生成班级标识
            String classIdentifier = idInfo.getEnrollmentYear() + "级" + 
                                   "专业" + idInfo.getMajorCode() + "班" + 
                                   idInfo.getClassCode();
            result.put("classIdentifier", classIdentifier);

            // 尝试从数据库获取班级详情
            StudentProfile student = studentService.getByStudentId(studentId);
            if (student != null && student.getClassId() != null) {
                result.put("classId", student.getClassId());
                
                // 尝试获取完整班级信息
                try {
                    com.academic.entity.Class clazz = new com.academic.entity.Class();
                    clazz.setId(student.getClassId());
                    // 这里需要通过服务获取班级信息
                    result.put("classDetails", clazz);
                } catch (Exception e) {
                    log.warn("获取班级详情失败", e);
                }
            }

            log.info("成功解析学号班级信息: {}", studentId);
            return ApiResponse.success(result);

        } catch (Exception e) {
            log.error("获取班级信息失败", e);
            return ApiResponse.error(e.getMessage());
        }
    }
}