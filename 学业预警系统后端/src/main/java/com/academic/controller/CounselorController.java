package com.academic.controller;

import com.academic.common.ApiResponse;
import com.academic.dto.TeacherDashboardResponse;
import com.academic.entity.*;
import com.academic.service.*;
import com.academic.mapper.StudentProfileMapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/counselors")
public class CounselorController {

    private final CounselorService counselorService;
    private final WarningService warningService;
    private final StudentProfileMapper studentProfileMapper;
    private final AssistancePlanService assistancePlanService;
    private final CounselorScoreService counselorScoreService;
    private final CounselorNotificationService counselorNotificationService;
    private final CounselorClassService counselorClassService;
    private final CounselorAnalyticsService counselorAnalyticsService;

    public CounselorController(CounselorService counselorService, WarningService warningService,
                               StudentProfileMapper studentProfileMapper, AssistancePlanService assistancePlanService,
                               CounselorScoreService counselorScoreService, CounselorNotificationService counselorNotificationService,
                               CounselorClassService counselorClassService, CounselorAnalyticsService counselorAnalyticsService) {
        this.counselorService = counselorService;
        this.warningService = warningService;
        this.studentProfileMapper = studentProfileMapper;
        this.assistancePlanService = assistancePlanService;
        this.counselorScoreService = counselorScoreService;
        this.counselorNotificationService = counselorNotificationService;
        this.counselorClassService = counselorClassService;
        this.counselorAnalyticsService = counselorAnalyticsService;
    }

    /**
     * 辅导员注册
     */
    @PostMapping("/register")
    public ApiResponse<Map<String, Object>> register(@RequestBody Map<String, Object> params) {
        try {
            String username = (String) params.get("username");
            String password = (String) params.get("password");
            String name = (String) params.get("name");
            Long collegeId = ((Number) params.get("college_id")).longValue();

            User user = new User();
            user.setUsername(username);
            user.setPassword(password);
            user.setRole(4); // 辅导员角色

            CounselorProfile profile = new CounselorProfile();
            profile.setName(name);
            profile.setCollegeId(collegeId);

            Long userId = counselorService.registerCounselor(user, profile);
            Map<String, Object> result = new HashMap<>();
            result.put("userId", userId);
            result.put("username", username);
            return ApiResponse.success(result);
        } catch (Exception e) {
            log.error("辅导员注册失败", e);
            return ApiResponse.error(e.getMessage());
        }
    }

    /**
     * 获取辅导员仪表板数据
     */
    @GetMapping("/dashboard/{userId}")
    public ApiResponse<TeacherDashboardResponse> getDashboard(@PathVariable Long userId) {
        try {
            CounselorProfile counselor = counselorService.getByUserId(userId);
            if (counselor == null) {
                return ApiResponse.error(404, "辅导员不存在");
            }

            TeacherDashboardResponse dashboard = new TeacherDashboardResponse();
            dashboard.setStudentCount(counselorService.getStudentCount(counselor.getId()));
            dashboard.setWarningCount(warningService.countWarnings(userId));
            dashboard.setRedWarnings(warningService.countWarningsByLevel(userId, "high"));
            dashboard.setYellowWarnings(warningService.countWarningsByLevel(userId, "medium"));

            return ApiResponse.success(dashboard);
        } catch (Exception e) {
            log.error("获取辅导员仪表板失败", e);
            return ApiResponse.error(e.getMessage());
        }
    }

    /**
     * 获取学生列表
     */
    @GetMapping("/students")
    public ApiResponse<List<StudentProfile>> getStudents(@RequestParam(required = false) Long counselorId) {
        try {
            List<StudentProfile> students = counselorService.getStudents(counselorId);
            return ApiResponse.success(students);
        } catch (Exception e) {
            log.error("获取学生列表失败", e);
            return ApiResponse.error(e.getMessage());
        }
    }

    /**
     * 获取学生详情
     */
    @GetMapping("/students/{studentId}")
    public ApiResponse<StudentProfile> getStudentDetail(@PathVariable Long studentId) {
        try {
            StudentProfile student = counselorService.getStudentDetail(studentId);
            if (student == null) {
                return ApiResponse.error(404, "学生不存在");
            }
            return ApiResponse.success(student);
        } catch (Exception e) {
            log.error("获取学生详情失败", e);
            return ApiResponse.error(e.getMessage());
        }
    }

    /**
     * 批量通知学生
     */
    @PostMapping("/students/notify")
    public ApiResponse<String> notifyStudents(@RequestBody Map<String, Object> params) {
        try {
            @SuppressWarnings("unchecked")
            List<Long> studentIds = (List<Long>) params.get("student_ids");
            String message = (String) params.get("message");
            
            counselorService.notifyStudents(studentIds, message);
            return ApiResponse.success("通知已发送");
        } catch (Exception e) {
            log.error("通知学生失败", e);
            return ApiResponse.error(e.getMessage());
        }
    }

    /**
     * 获取预警列表
     */
    @GetMapping("/warnings")
    public ApiResponse<List<AcademicWarning>> getWarnings(@RequestParam(required = false) Long counselorId,
                                                          @RequestParam(required = false) String status) {
        try {
            List<AcademicWarning> warnings = counselorService.getWarnings(counselorId, status);
            return ApiResponse.success(warnings);
        } catch (Exception e) {
            log.error("获取预警列表失败", e);
            return ApiResponse.error(e.getMessage());
        }
    }

    /**
     * 处理预警
     */
    @PostMapping("/warnings/{warningId}/process")
    public ApiResponse<String> processWarning(@PathVariable Long warningId, @RequestBody(required = false) Map<String, String> params) {
        try {
            String measures = params != null ? params.get("measures") : null;
            counselorService.processWarning(warningId, measures);
            return ApiResponse.success("预警已处理");
        } catch (Exception e) {
            log.error("处理预警失败", e);
            return ApiResponse.error(e.getMessage());
        }
    }

    /**
     * 批量处理预警
     */
    @PostMapping("/warnings/batch-process")
    public ApiResponse<String> batchProcessWarnings(@RequestBody List<Long> warningIds) {
        try {
            for (Long warningId : warningIds) {
                counselorService.processWarning(warningId, null);
            }
            return ApiResponse.success("预警已批量处理");
        } catch (Exception e) {
            log.error("批量处理预警失败", e);
            return ApiResponse.error(e.getMessage());
        }
    }

    /**
     * 获取选修课信息
     */
    @GetMapping("/enrollments")
    public ApiResponse<List<Enrollment>> getEnrollments(@RequestParam(required = false) Long counselorId) {
        try {
            List<Enrollment> enrollments = counselorService.getEnrollments(counselorId);
            return ApiResponse.success(enrollments);
        } catch (Exception e) {
            log.error("获取选修课信息失败", e);
            return ApiResponse.error(e.getMessage());
        }
    }

    // ============= 帮扶计划API =============
    /**
     * 获取学生帮扶计划
     */
    @GetMapping("/assistance/student/{studentId}")
    public ApiResponse<List<AssistancePlan>> getPlansByStudent(@PathVariable Long studentId) {
        try {
            List<AssistancePlan> plans = assistancePlanService.getPlansByStudent(studentId);
            return ApiResponse.success(plans);
        } catch (Exception e) {
            log.error("获取帮扶计划失败", e);
            return ApiResponse.error(e.getMessage());
        }
    }

    /**
     * 创建帮扶计划
     */
    @PostMapping("/assistance/create")
    public ApiResponse<Long> createAssistancePlan(@RequestBody AssistancePlan plan) {
        try {
            Long planId = assistancePlanService.createPlan(plan);
            return ApiResponse.success(planId);
        } catch (Exception e) {
            log.error("创建帮扶计划失败", e);
            return ApiResponse.error(e.getMessage());
        }
    }

    /**
     * 更新帮扶计划进度
     */
    @PostMapping("/assistance/{planId}/progress")
    public ApiResponse<String> updatePlanProgress(@PathVariable Long planId, @RequestBody Map<String, Object> params) {
        try {
            Double progressPercentage = ((Number) params.get("progress_percentage")).doubleValue();
            boolean success = assistancePlanService.updateProgress(planId, progressPercentage);
            return success ? ApiResponse.success("计划进度已更新") : ApiResponse.error("更新失败");
        } catch (Exception e) {
            log.error("更新计划进度失败", e);
            return ApiResponse.error(e.getMessage());
        }
    }

    // ============= 成绩跟踪API =============
    /**
     * 获取班级成绩
     */
    @GetMapping("/scores/class/{classId}")
    public ApiResponse<List<Map<String, Object>>> getClassScores(@RequestParam Long counselorId, @PathVariable Long classId) {
        try {
            List<Map<String, Object>> scores = counselorScoreService.getClassScores(counselorId, classId);
            return ApiResponse.success(scores);
        } catch (Exception e) {
            log.error("获取班级成绩失败", e);
            return ApiResponse.error(e.getMessage());
        }
    }

    /**
     * 获取课程成绩分布
     */
    @GetMapping("/scores/distribution/{courseId}")
    public ApiResponse<Map<String, Object>> getCourseScoreDistribution(@PathVariable Long courseId) {
        try {
            Map<String, Object> distribution = counselorScoreService.getCourseScoreDistribution(courseId);
            return ApiResponse.success(distribution);
        } catch (Exception e) {
            log.error("获取成绩分布失败", e);
            return ApiResponse.error(e.getMessage());
        }
    }

    /**
     * 获取低分学生
     */
    @GetMapping("/scores/low-score")
    public ApiResponse<List<Map<String, Object>>> getLowScoreStudents(@RequestParam Long counselorId) {
        try {
            List<Map<String, Object>> students = counselorScoreService.getLowScoreStudents(counselorId);
            return ApiResponse.success(students);
        } catch (Exception e) {
            log.error("获取低分学生失败", e);
            return ApiResponse.error(e.getMessage());
        }
    }

    // ============= 通知中心API =============
    /**
     * 获取通知历史
     */
    @GetMapping("/notifications/history")
    public ApiResponse<List<Map<String, Object>>> getNotificationHistory(@RequestParam Long counselorId,
                                                                         @RequestParam(defaultValue = "1") int page,
                                                                         @RequestParam(defaultValue = "10") int size) {
        try {
            List<Map<String, Object>> history = counselorNotificationService.getNotificationHistory(counselorId, page, size);
            return ApiResponse.success(history);
        } catch (Exception e) {
            log.error("获取通知历史失败", e);
            return ApiResponse.error(e.getMessage());
        }
    }

    /**
     * 获取通知模板
     */
    @GetMapping("/notifications/templates")
    public ApiResponse<List<Map<String, Object>>> getNotificationTemplates() {
        try {
            List<Map<String, Object>> templates = counselorNotificationService.getNotificationTemplates();
            return ApiResponse.success(templates);
        } catch (Exception e) {
            log.error("获取通知模板失败", e);
            return ApiResponse.error(e.getMessage());
        }
    }

    /**
     * 获取本周通知数
     */
    @GetMapping("/notifications/weekly-count")
    public ApiResponse<Long> getWeeklyNotifications(@RequestParam Long counselorId) {
        try {
            Long count = counselorNotificationService.countWeeklyNotifications(counselorId);
            return ApiResponse.success(count);
        } catch (Exception e) {
            log.error("获取通知数失败", e);
            return ApiResponse.error(e.getMessage());
        }
    }

    // ============= 班级管理API =============
    /**
     * 获取班级列表
     */
    @GetMapping("/classes")
    public ApiResponse<List<Map<String, Object>>> getClasses(@RequestParam Long counselorId) {
        try {
            List<Map<String, Object>> classes = counselorClassService.getClasses(counselorId);
            return ApiResponse.success(classes);
        } catch (Exception e) {
            log.error("获取班级列表失败", e);
            return ApiResponse.error(e.getMessage());
        }
    }

    /**
     * 获取班级详情
     */
    @GetMapping("/classes/{classId}/detail")
    public ApiResponse<Map<String, Object>> getClassDetail(@PathVariable Long classId) {
        try {
            Map<String, Object> detail = counselorClassService.getClassDetail(classId);
            return ApiResponse.success(detail);
        } catch (Exception e) {
            log.error("获取班级详情失败", e);
            return ApiResponse.error(e.getMessage());
        }
    }

    /**
     * 获取班级学生列表
     */
    @GetMapping("/classes/{classId}/students")
    public ApiResponse<List<Map<String, Object>>> getClassStudents(@PathVariable Long classId) {
        try {
            List<Map<String, Object>> students = counselorClassService.getClassStudents(classId);
            return ApiResponse.success(students);
        } catch (Exception e) {
            log.error("获取班级学生失败", e);
            return ApiResponse.error(e.getMessage());
        }
    }

    /**
     * 获取班级预警概览
     */
    @GetMapping("/classes/{classId}/warnings")
    public ApiResponse<Map<String, Object>> getClassWarningOverview(@PathVariable Long classId) {
        try {
            Map<String, Object> overview = counselorClassService.getClassWarningOverview(classId);
            return ApiResponse.success(overview);
        } catch (Exception e) {
            log.error("获取班级预警概览失败", e);
            return ApiResponse.error(e.getMessage());
        }
    }

    /**
     * 对比班级预警
     */
    @GetMapping("/classes/warnings/compare")
    public ApiResponse<List<Map<String, Object>>> compareClassWarnings(@RequestParam Long counselorId) {
        try {
            List<Map<String, Object>> comparison = counselorClassService.compareClassWarnings(counselorId);
            return ApiResponse.success(comparison);
        } catch (Exception e) {
            log.error("班级预警对比失败", e);
            return ApiResponse.error(e.getMessage());
        }
    }

    // ============= 数据分析API =============
    /**
     * 获取学分不足率
     */
    @GetMapping("/analytics/credit-insufficient")
    public ApiResponse<Map<String, Object>> getCreditInsufficientRate(@RequestParam Long counselorId) {
        try {
            Map<String, Object> result = counselorAnalyticsService.getCreditInsufficientRate(counselorId);
            return ApiResponse.success(result);
        } catch (Exception e) {
            log.error("获取学分不足率失败", e);
            return ApiResponse.error(e.getMessage());
        }
    }

    /**
     * 获取预警级别分布
     */
    @GetMapping("/analytics/warning-distribution")
    public ApiResponse<Map<String, Object>> getWarningLevelDistribution(@RequestParam Long counselorId) {
        try {
            Map<String, Object> result = counselorAnalyticsService.getWarningLevelDistribution(counselorId);
            return ApiResponse.success(result);
        } catch (Exception e) {
            log.error("获取预警分布失败", e);
            return ApiResponse.error(e.getMessage());
        }
    }

    /**
     * 获取预警处理效率
     */
    @GetMapping("/analytics/handling-efficiency")
    public ApiResponse<Map<String, Object>> getWarningHandlingEfficiency(@RequestParam Long counselorId) {
        try {
            Map<String, Object> result = counselorAnalyticsService.getWarningHandlingEfficiency(counselorId);
            return ApiResponse.success(result);
        } catch (Exception e) {
            log.error("获取处理效率失败", e);
            return ApiResponse.error(e.getMessage());
        }
    }

    /**
     * 获取班级达标率排名
     */
    @GetMapping("/analytics/credit-achievement-ranking")
    public ApiResponse<List<Map<String, Object>>> getClassCreditAchievementRanking(@RequestParam Long counselorId) {
        try {
            List<Map<String, Object>> result = counselorAnalyticsService.getClassCreditAchievementRanking(counselorId);
            return ApiResponse.success(result);
        } catch (Exception e) {
            log.error("获取达标率排名失败", e);
            return ApiResponse.error(e.getMessage());
        }
    }

    /**
     * 获取预警趋势
     */
    @GetMapping("/analytics/warning-trend")
    public ApiResponse<List<Map<String, Object>>> getWarningTrend(@RequestParam Long counselorId) {
        try {
            List<Map<String, Object>> result = counselorAnalyticsService.getWarningTrend(counselorId);
            return ApiResponse.success(result);
        } catch (Exception e) {
            log.error("获取预警趋势失败", e);
            return ApiResponse.error(e.getMessage());
        }
    }

    /**
     * 获取帮扶计划完成率
     */
    @GetMapping("/analytics/assistance-completion")
    public ApiResponse<Map<String, Object>> getAssistancePlanCompletionRate(@RequestParam Long counselorId) {
        try {
            Map<String, Object> result = counselorAnalyticsService.getAssistancePlanCompletionRate(counselorId);
            return ApiResponse.success(result);
        } catch (Exception e) {
            log.error("获取帮扶完成率失败", e);
            return ApiResponse.error(e.getMessage());
        }
    }

    /**
     * 获取班级学分监控数据
     */
    @GetMapping("/credit-monitor")
    public ApiResponse<Map<String, Object>> getCreditMonitor(@RequestParam Long counselorId) {
        try {
            CounselorProfile counselor = counselorService.getById(counselorId);
            if (counselor == null) {
                return ApiResponse.error(404, "辅导员不存在");
            }

            Map<String, Object> monitor = new HashMap<>();
            List<Map<String, Object>> classMonitors = new java.util.ArrayList<>();
            
            double totalAchievementRate = 0;
            int totalClasses = 0;
            int excellentClasses = 0;
            int mediumClasses = 0;
            int lowClasses = 0;
            
            // 获取该辅导员管理的所有学生
            List<StudentProfile> students = counselorService.getStudents(counselorId);
            if (students != null && !students.isEmpty()) {
                // 按班级分组统计
                java.util.Map<Long, java.util.List<StudentProfile>> classByGroup = new java.util.HashMap<>();
                for (StudentProfile student : students) {
                    Long classId = student.getClassId();
                    classByGroup.computeIfAbsent(classId, k -> new java.util.ArrayList<>()).add(student);
                }
                
                // 对每个班级进行统计
                for (java.util.Map.Entry<Long, java.util.List<StudentProfile>> entry : classByGroup.entrySet()) {
                    Long classId = entry.getKey();
                    java.util.List<StudentProfile> classStudents = entry.getValue();
                    
                    Map<String, Object> classStat = counselorScoreService.getClassCreditStatistics(counselorId, classId);
                    if (classStat != null) {
                        double rate = ((Number) classStat.getOrDefault("creditAchievementRate", 0)).doubleValue();
                        classStat.put("achievementRate", rate);
                        classMonitors.add(classStat);
                        
                        totalAchievementRate += rate;
                        totalClasses++;
                        
                        if (rate >= 80) excellentClasses++;
                        else if (rate >= 60) mediumClasses++;
                        else lowClasses++;
                    }
                }
                
                totalAchievementRate = totalClasses > 0 ? totalAchievementRate / totalClasses : 0;
            }
            
            monitor.put("overallAchievementRate", Math.round(totalAchievementRate * 100) / 100.0);
            monitor.put("totalClasses", totalClasses);
            monitor.put("excellentClasses", excellentClasses);
            monitor.put("mediumClasses", mediumClasses);
            monitor.put("lowClasses", lowClasses);
            monitor.put("classMonitors", classMonitors);
            
            return ApiResponse.success(monitor);
        } catch (Exception e) {
            log.error("获取学分监控失败", e);
            return ApiResponse.error(e.getMessage());
        }
    }

    /**
     * 获取学分不足学生列表
     */
    @GetMapping("/credit-insufficient")
    public ApiResponse<List<Map<String, Object>>> getCreditInsufficientStudents(@RequestParam Long counselorId,
                                                                                 @RequestParam(defaultValue = "1") int page,
                                                                                 @RequestParam(defaultValue = "20") int size) {
        try {
            List<Map<String, Object>> result = new java.util.ArrayList<>();
            
            // 获取低分学生
            List<Map<String, Object>> lowScoreStudents = counselorScoreService.getLowScoreStudents(counselorId);
            
            // 按学分升序排序，最低分排前
            if (lowScoreStudents != null) {
                lowScoreStudents.sort((a, b) -> {
                    Double scoreA = (Double) a.get("score");
                    Double scoreB = (Double) b.get("score");
                    return scoreA.compareTo(scoreB);
                });
                
                // 分页
                int start = (page - 1) * size;
                int end = Math.min(start + size, lowScoreStudents.size());
                result.addAll(lowScoreStudents.subList(start, end));
            }
            
            return ApiResponse.success(result);
        } catch (Exception e) {
            log.error("获取学分不足学生失败", e);
            return ApiResponse.error(e.getMessage());
        }
    }

}
