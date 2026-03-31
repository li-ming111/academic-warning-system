package com.academic.controller;

import com.academic.common.ApiResponse;
import com.academic.dto.TeacherDashboardResponse;
import com.academic.dto.ScoreImportRequest;
import com.academic.entity.*;
import com.academic.service.TeacherService;
import com.academic.service.WarningService;
import com.academic.service.ClassManagementRequestService;
import com.academic.service.ExcelScoreImportService;
import com.academic.mapper.StudentProfileMapper;
import com.academic.mapper.ClassMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import java.io.InputStream;
import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/teachers")
public class TeacherController {

    private final TeacherService teacherService;
    private final WarningService warningService;
    private final StudentProfileMapper studentProfileMapper;
    private final ClassManagementRequestService classManagementRequestService;
    private final ExcelScoreImportService excelScoreImportService;
    private final ClassMapper classMapper;

    public TeacherController(TeacherService teacherService, WarningService warningService,
                             StudentProfileMapper studentProfileMapper,
                             ClassManagementRequestService classManagementRequestService,
                             ExcelScoreImportService excelScoreImportService,
                             ClassMapper classMapper) {
        this.teacherService = teacherService;
        this.warningService = warningService;
        this.studentProfileMapper = studentProfileMapper;
        this.classManagementRequestService = classManagementRequestService;
        this.excelScoreImportService = excelScoreImportService;
        this.classMapper = classMapper;
    }

    /**
     * 教师注册
     */
    @PostMapping("/register")
    public ApiResponse<Map<String, Object>> register(@RequestBody Map<String, Object> params) {
        try {
            String username = (String) params.get("username");
            String password = (String) params.get("password");
            Long collegeId = ((Number) params.get("college_id")).longValue();

            User user = new User();
            user.setUsername(username);
            // 如果密码为空，设置默认密码123456
            user.setPassword(password != null && !password.isEmpty() ? password : "123456");
            user.setRole(2);

            TeacherProfile profile = new TeacherProfile();
            profile.setCollegeId(collegeId);

            Long userId = teacherService.registerTeacher(user, profile);
            Map<String, Object> result = new HashMap<>();
            result.put("userId", userId);
            result.put("username", username);
            return ApiResponse.success(result);
        } catch (Exception e) {
            log.error("教师注册失败", e);
            return ApiResponse.error(e.getMessage());
        }
    }

    /**
     * 获取教师仪表板数据
     */
    @GetMapping("/dashboard/{userId}")
    public ApiResponse<TeacherDashboardResponse> getDashboard(@PathVariable Long userId) {
        try {
            TeacherProfile teacher = teacherService.getByUserId(userId);
            if (teacher == null) {
                return ApiResponse.error(404, "教师不存在");
            }

            TeacherDashboardResponse dashboard = new TeacherDashboardResponse();
            dashboard.setStudentCount(teacherService.getStudentCount(teacher.getId()));
            dashboard.setWarningCount(warningService.countWarnings(teacher.getId()));
            dashboard.setRedWarnings(warningService.countWarningsByLevel(teacher.getId(), "high"));
            dashboard.setYellowWarnings(warningService.countWarningsByLevel(teacher.getId(), "medium"));

            return ApiResponse.success(dashboard);
        } catch (Exception e) {
            log.error("获取教师仪表板数据失败", e);
            return ApiResponse.error(e.getMessage());
        }
    }

    /**
     * 查询教师课程列表
     */
    @GetMapping("/courses")
    public ApiResponse<List<Course>> getCourses(@RequestParam("teacher_id") Long teacherId) {
        try {
            List<Course> courses = teacherService.getTeacherCourses(teacherId);
            return ApiResponse.success(courses);
        } catch (Exception e) {
            log.error("查询课程列表失败", e);
            return ApiResponse.error(e.getMessage());
        }
    }

    /**
     * 查询成绩
     */
    @GetMapping("/scores")
    public ApiResponse<List<Map<String, Object>>> getScores(@RequestParam("course_id") Long courseId) {
        try {
            List<Map<String, Object>> scores = teacherService.getCourseScores(courseId);
            return ApiResponse.success(scores);
        } catch (Exception e) {
            log.error("查询成绩失败", e);
            return ApiResponse.error(e.getMessage());
        }
    }

    /**
     * 批量录入成绩
     */
    @PostMapping("/scores")
    public ApiResponse<String> saveScores(@RequestBody List<Score> scores) {
        try {
            for (Score score : scores) {
                teacherService.saveOrUpdateScore(score);
            }
            return ApiResponse.success("成绩保存成功");
        } catch (Exception e) {
            log.error("保存成绩失败", e);
            return ApiResponse.error(e.getMessage());
        }
    }

    /**
     * 修改成绩
     */
    @PutMapping("/scores/{scoreId}")
    public ApiResponse<String> updateScore(@PathVariable Long scoreId, @RequestBody Map<String, Object> params) {
        try {
            Score score = new Score();
            score.setId(scoreId);
            if (params.containsKey("score_total")) {
                score.setScoreTotal(new java.math.BigDecimal(params.get("score_total").toString()));
            }
            if (params.containsKey("grade_point")) {
                score.setGradePoint(new java.math.BigDecimal(params.get("grade_point").toString()));
            }
            teacherService.saveOrUpdateScore(score);
            return ApiResponse.success("成绩修改成功");
        } catch (Exception e) {
            log.error("修改成绩失败", e);
            return ApiResponse.error(e.getMessage());
        }
    }

    /**
     * 查询反馈
     */
    @GetMapping("/feedbacks")
    public ApiResponse<List<Feedback>> getFeedbacks(@RequestParam(required = false) Long teacherId,
                                                    @RequestParam(required = false) String category) {
        try {
            List<Feedback> feedbacks = teacherService.getTeacherFeedbacks(teacherId, category);
            return ApiResponse.success(feedbacks);
        } catch (Exception e) {
            log.error("查询反馈失败", e);
            return ApiResponse.error(e.getMessage());
        }
    }

    /**
     * 回复反馈
     */
    @PutMapping("/feedbacks/{feedbackId}")
    public ApiResponse<String> replyFeedback(@PathVariable Long feedbackId, @RequestBody Map<String, String> params) {
        try {
            String replyContent = params.get("reply_content");
            teacherService.replyFeedback(feedbackId, replyContent);
            return ApiResponse.success("回复成功");
        } catch (Exception e) {
            log.error("回复反馈失败", e);
            return ApiResponse.error(e.getMessage());
        }
    }

    /**
     * 查询选修课
     */
    @GetMapping("/enrollments")
    public ApiResponse<List<Enrollment>> getEnrollments(
            @RequestParam(required = false) Long teacherId,
            @RequestParam(required = false) Long courseId) {
        try {
            List<Enrollment> enrollments = teacherService.getEnrollments(teacherId, courseId != null ? courseId.toString() : null);
            return ApiResponse.success(enrollments);
        } catch (Exception e) {
            log.error("查询选修课失败", e);
            return ApiResponse.error(e.getMessage());
        }
    }

    /**
     * 保存沟通记录
     */
    @PostMapping("/communications")
    public ApiResponse<String> saveCommunication(@RequestBody CommunicationLog commLog) {
        try {
            teacherService.saveCommunicationLog(commLog);
            return ApiResponse.success("沟通记录已保存");
        } catch (Exception e) {
            log.error("保存沟通记录失败", e);
            return ApiResponse.error(e.getMessage());
        }
    }

    /**
     * 获取教师的消息列表
     */
    @GetMapping("/messages/{userId}")
    public ApiResponse<List<CommunicationLog>> getMessages(@PathVariable Long userId) {
        try {
            TeacherProfile teacher = teacherService.getByUserId(userId);
            if (teacher == null) {
                return ApiResponse.error(404, "教师不存在");
            }
            List<CommunicationLog> messages = teacherService.getTeacherMessages(teacher.getId());
            return ApiResponse.success(messages);
        } catch (Exception e) {
            log.error("获取消息列表失败", e);
            return ApiResponse.error(e.getMessage());
        }
    }

    /**
     * 获取教师的未读消息数
     */
    @GetMapping("/messages/{userId}/unread-count")
    public ApiResponse<Long> getUnreadCount(@PathVariable Long userId) {
        try {
            TeacherProfile teacher = teacherService.getByUserId(userId);
            if (teacher == null) {
                return ApiResponse.error(404, "教师不存在");
            }
            Long count = teacherService.getTeacherUnreadCount(teacher.getId());
            return ApiResponse.success(count);
        } catch (Exception e) {
            log.error("获取未读消息数失败", e);
            return ApiResponse.error(e.getMessage());
        }
    }

    /**
     * 标记消息为已读
     */
    @PostMapping("/messages/{messageId}/mark-read")
    public ApiResponse<String> markMessageAsRead(@PathVariable Long messageId) {
        try {
            boolean success = teacherService.markMessageAsRead(messageId);
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
     * 查询教师班级的学生列表
     */
    @GetMapping("/students/{teacherId}")
    public ApiResponse<List<StudentProfile>> getStudentList(@PathVariable Long teacherId) {
        try {
            TeacherProfile teacher = teacherService.getById(teacherId);
            if (teacher == null) {
                return ApiResponse.error(404, "教师不存在");
            }

            // 查询教师所教班级的学生
            com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<com.academic.entity.Class> classQuery =
                    new com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<>();
            classQuery.eq("teacher_id", teacherId);
            List<com.academic.entity.Class> classes = classMapper.selectList(classQuery);

            if (classes.isEmpty()) {
                // 如果教师没有分配班级，返回空列表
                return ApiResponse.success(new java.util.ArrayList<>());
            }

            // 提取班级ID
            java.util.List<Long> classIds = classes.stream()
                    .map(com.academic.entity.Class::getId)
                    .collect(java.util.stream.Collectors.toList());

            // 查询这些班级的学生
            com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<StudentProfile> studentQuery =
                    new com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<>();
            studentQuery.in("class_id", classIds);
            List<StudentProfile> students = studentProfileMapper.selectList(studentQuery);

            return ApiResponse.success(students);
        } catch (Exception e) {
            log.error("获取学生列表失败", e);
            return ApiResponse.error(e.getMessage());
        }
    }

    /**
     * 查询教师班级的预警列表
     */
    @GetMapping("/warnings/{teacherId}")
    public ApiResponse<List<AcademicWarning>> getWarnings(@PathVariable Long teacherId,
                                                          @RequestParam(required = false) String status) {
        try {
            List<AcademicWarning> warnings = teacherService.getTeacherWarnings(teacherId, status);
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
            teacherService.processWarning(warningId, measures);
            return ApiResponse.success("预警已处理");
        } catch (Exception e) {
            log.error("处理预警失败", e);
            return ApiResponse.error(e.getMessage());
        }
    }

    /**
     * 获取教师的待办事项
     */
    @GetMapping("/todos/{teacherId}")
    public ApiResponse<Map<String, Object>> getTodos(@PathVariable Long teacherId) {
        try {
            Map<String, Object> todos = teacherService.getTeacherTodos(teacherId);
            return ApiResponse.success(todos);
        } catch (Exception e) {
            log.error("获取待办事项失败", e);
            return ApiResponse.error(e.getMessage());
        }
    }

    /**
     * 获取课程成绩分布统计
     */
    @GetMapping("/courses/{courseId}/distribution")
    public ApiResponse<Map<String, Object>> getCourseScoreDistribution(@PathVariable Long courseId) {
        try {
            List<Map<String, Object>> scores = teacherService.getCourseScores(courseId);
            Map<String, Object> distribution = new HashMap<>();
            
            // 按成绩段分类统计
            long excellent = scores.stream()
                .filter(s -> {
                    Object score = s.get("score_total");
                    if (score instanceof Number) {
                        return ((Number) score).doubleValue() >= 90;
                    }
                    return false;
                })
                .count();
            long good = scores.stream()
                .filter(s -> {
                    Object score = s.get("score_total");
                    if (score instanceof Number) {
                        double val = ((Number) score).doubleValue();
                        return val >= 80 && val < 90;
                    }
                    return false;
                })
                .count();
            long normal = scores.stream()
                .filter(s -> {
                    Object score = s.get("score_total");
                    if (score instanceof Number) {
                        double val = ((Number) score).doubleValue();
                        return val >= 70 && val < 80;
                    }
                    return false;
                })
                .count();
            long pass = scores.stream()
                .filter(s -> {
                    Object score = s.get("score_total");
                    if (score instanceof Number) {
                        double val = ((Number) score).doubleValue();
                        return val >= 60 && val < 70;
                    }
                    return false;
                })
                .count();
            long fail = scores.stream()
                .filter(s -> {
                    Object score = s.get("score_total");
                    if (score instanceof Number) {
                        return ((Number) score).doubleValue() < 60;
                    }
                    return true;
                })
                .count();
            
            distribution.put("excellent", excellent);
            distribution.put("good", good);
            distribution.put("normal", normal);
            distribution.put("pass", pass);
            distribution.put("fail", fail);
            distribution.put("total", scores.size());
            
            // 计算平均分
            double avgScore = scores.stream()
                .mapToDouble(s -> {
                    Object score = s.get("score_total");
                    return score instanceof Number ? ((Number) score).doubleValue() : 0;
                })
                .average()
                .orElse(0);
            distribution.put("averageScore", Math.round(avgScore * 100.0) / 100.0);
            
            return ApiResponse.success(distribution);
        } catch (Exception e) {
            log.error("获取成绩分布失败", e);
            return ApiResponse.error(e.getMessage());
        }
    }

    /**
     * 获取异常成绩学生列表（成绩异常或风险）
     */
    @GetMapping("/courses/{courseId}/anomaly")
    public ApiResponse<List<Map<String, Object>>> getCourseAnomalies(@PathVariable Long courseId,
                                                                      @RequestParam(defaultValue = "60") double threshold) {
        try {
            List<Map<String, Object>> scores = teacherService.getCourseScores(courseId);
            List<Map<String, Object>> anomalies = new java.util.ArrayList<>();
            
            for (Map<String, Object> scoreMap : scores) {
                Object scoreObj = scoreMap.get("score_total");
                double score = scoreObj instanceof Number ? ((Number) scoreObj).doubleValue() : 0;
                
                if (score < threshold) {
                    Map<String, Object> anomaly = new HashMap<>();
                    anomaly.put("studentId", scoreMap.get("student_id"));
                    anomaly.put("studentName", scoreMap.get("student_name"));
                    anomaly.put("score", score);
                    anomaly.put("riskLevel", score < 50 ? "high" : "medium");
                    anomalies.add(anomaly);
                }
            }
            
            // 按成绩升序排序，最低分排前
            anomalies.sort((a, b) -> Double.compare(
                (Double) a.get("score"),
                (Double) b.get("score")
            ));
            
            return ApiResponse.success(anomalies);
        } catch (Exception e) {
            log.error("获取异常成绩失败", e);
            return ApiResponse.error(e.getMessage());
        }
    }

    /**
     * 获取课程学生成绩列表
     */
    @GetMapping("/courses/{courseId}/students")
    public ApiResponse<List<Map<String, Object>>> getCourseStudents(@PathVariable Long courseId,
                                                                    @RequestParam(defaultValue = "1") int page,
                                                                    @RequestParam(defaultValue = "20") int size) {
        try {
            List<Map<String, Object>> scores = teacherService.getCourseScores(courseId);
            
            // 按成绩降序排序
            scores.sort((a, b) -> {
                Object scoreA = a.get("score_total");
                Object scoreB = b.get("score_total");
                double valA = scoreA instanceof Number ? ((Number) scoreA).doubleValue() : 0;
                double valB = scoreB instanceof Number ? ((Number) scoreB).doubleValue() : 0;
                return Double.compare(valB, valA);
            });
            
            // 添加排名
            for (int i = 0; i < scores.size(); i++) {
                scores.get(i).put("rank", i + 1);
            }
            
            // 分页处理
            int start = (page - 1) * size;
            int end = Math.min(start + size, scores.size());
            List<Map<String, Object>> paginated = scores.subList(start, end);
            
            return ApiResponse.success(paginated);
        } catch (Exception e) {
            log.error("获取课程学生成绩失败", e);
            return ApiResponse.error(e.getMessage());
        }
    }

    /**
     * 获取学生学分预测
     */
    @GetMapping("/students/{studentId}/credit-prediction")
    public ApiResponse<Map<String, Object>> getCreditPrediction(@PathVariable Long studentId) {
        try {
            Map<String, Object> prediction = new HashMap<>();
            
            // 获取学生成绩
            com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<Score> qw = 
                new com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<>();
            qw.eq("student_id", studentId);
            List<Score> scores = teacherService.getScoresByStudentId(studentId);
            
            // 计算已修学分
            double creditAchieved = 0;
            double totalCreditsRequired = 120; // 假设总学分要求
            int excellentCount = 0;
            int passCount = 0;
            
            for (Score score : scores) {
                if (score.getScoreTotal() != null && score.getScoreTotal().doubleValue() >= 60) {
                    // 假设每门课1学分
                    creditAchieved += 1;
                    passCount++;
                    
                    if (score.getScoreTotal().doubleValue() >= 80) {
                        excellentCount++;
                    }
                }
            }
            
            // 计算达标率
            double achievementRate = totalCreditsRequired > 0 ? (creditAchieved / totalCreditsRequired) * 100 : 0;
            achievementRate = Math.min(achievementRate, 100);
            
            // 预测未来学分
            double remainingCredits = totalCreditsRequired - creditAchieved;
            double predictedFinalRate = Math.min(achievementRate + (remainingCredits > 0 ? 20 : 0), 100);
            
            prediction.put("studentId", studentId);
            prediction.put("creditAchieved", Math.round(creditAchieved));
            prediction.put("totalRequired", (int) totalCreditsRequired);
            prediction.put("currentRate", Math.round(achievementRate * 100) / 100.0);
            prediction.put("predictedFinalRate", Math.round(predictedFinalRate * 100) / 100.0);
            prediction.put("riskLevel", achievementRate < 50 ? "high" : (achievementRate < 80 ? "medium" : "low"));
            prediction.put("passCount", passCount);
            prediction.put("excellentCount", excellentCount);
            prediction.put("totalCourses", scores.size());
            
            return ApiResponse.success(prediction);
        } catch (Exception e) {
            log.error("获取学生学分预测失败", e);
            return ApiResponse.error(e.getMessage());
        }
    }

    /**
     * 获取课程推荐
     */
    @GetMapping("/courses/{courseId}/recommendations")
    public ApiResponse<List<Map<String, Object>>> getCourseRecommendations(@PathVariable Long courseId,
                                                                            @RequestParam(defaultValue = "5") int limit) {
        try {
            List<Map<String, Object>> scores = teacherService.getCourseScores(courseId);
            List<Map<String, Object>> recommendations = new java.util.ArrayList<>();
            
            // 找出成绩低于70分的学生
            for (Map<String, Object> scoreMap : scores) {
                Object scoreObj = scoreMap.get("score_total");
                double score = scoreObj instanceof Number ? ((Number) scoreObj).doubleValue() : 0;
                
                if (score < 70 && score >= 0) {
                    Map<String, Object> rec = new HashMap<>();
                    rec.put("studentId", scoreMap.get("student_id"));
                    rec.put("studentName", scoreMap.get("student_name"));
                    rec.put("currentScore", score);
                    rec.put("recommendationType", score < 50 ? "strong" : "normal");
                    rec.put("suggestions", score < 50 ? 
                        "建议进行课外辅导，建议参加答疑班" : 
                        "建议复习基础知识，参考往年题目");
                    recommendations.add(rec);
                }
            }
            
            // 按成绩升序排序，最低分优先
            recommendations.sort((a, b) -> Double.compare(
                (Double) a.get("currentScore"),
                (Double) b.get("currentScore")
            ));
            
            // 限制返回数量
            List<Map<String, Object>> result = recommendations.stream()
                .limit(limit)
                .collect(java.util.stream.Collectors.toList());
            
            return ApiResponse.success(result);
        } catch (Exception e) {
            log.error("获取课程推荐失败", e);
            return ApiResponse.error(e.getMessage());
        }
    }

    /**
     * 获取成绩修改日志
     */
    @GetMapping("/scores/logs/{teacherId}")
    public ApiResponse<List<Map<String, Object>>> getScoreLogs(@PathVariable Long teacherId,
                                                               @RequestParam(defaultValue = "1") int page,
                                                               @RequestParam(defaultValue = "20") int size) {
        try {
            com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<ScoreLog> qw = 
                new com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<>();
            qw.orderByDesc("created_at");
            List<ScoreLog> logs = teacherService.getScoreLogs(teacherId);
            
            List<Map<String, Object>> result = new java.util.ArrayList<>();
            for (ScoreLog log : logs) {
                Score score = teacherService.getScoreById(log.getScoreId());
                if (score != null) {
                    StudentProfile student = studentProfileMapper.selectById(score.getStudentId());
                    Map<String, Object> item = new HashMap<>();
                    item.put("id", log.getId());
                    item.put("scoreId", score.getId());
                    item.put("studentId", score.getStudentId());
                    item.put("studentName", student != null ? student.getName() : "未知");
                    item.put("courseId", score.getCourseId());
                    item.put("oldScore", log.getOldScore() != null ? log.getOldScore().doubleValue() : 0);
                    item.put("newScore", log.getNewScore() != null ? log.getNewScore().doubleValue() : 0);
                    item.put("difference", log.getNewScore() != null && log.getOldScore() != null ? 
                        (log.getNewScore().doubleValue() - log.getOldScore().doubleValue()) : 0);
                    item.put("modifiedAt", log.getCreatedAt());
                    result.add(item);
                }
            }
            
            // 分页处理
            int start = (page - 1) * size;
            int end = Math.min(start + size, result.size());
            List<Map<String, Object>> paginated = result.subList(start, end);
            
            return ApiResponse.success(paginated);
        } catch (Exception e) {
            log.error("获取成绩日志失败", e);
            return ApiResponse.error(e.getMessage());
        }
    }

    /**
     * 获取学生反馈列表
     */
    @GetMapping("/feedbacks/{teacherId}/list")
    public ApiResponse<List<Map<String, Object>>> getFeedbackList(@PathVariable Long teacherId,
                                                                  @RequestParam(required = false) String category,
                                                                  @RequestParam(defaultValue = "1") int page,
                                                                  @RequestParam(defaultValue = "20") int size) {
        try {
            List<Feedback> feedbacks = teacherService.getTeacherFeedbacks(teacherId, category);
            List<Map<String, Object>> result = new java.util.ArrayList<>();
            
            for (Feedback fb : feedbacks) {
                StudentProfile student = studentProfileMapper.selectById(fb.getStudentId());
                Map<String, Object> item = new HashMap<>();
                item.put("id", fb.getId());
                item.put("studentId", fb.getStudentId());
                item.put("studentName", student != null ? student.getName() : "未知");
                item.put("category", fb.getCategory());
                item.put("content", fb.getContent());
                item.put("reply_content", fb.getReplyContent());
                item.put("status", fb.getReplyContent() != null && !fb.getReplyContent().isEmpty() ? "replied" : "pending");
                item.put("createdAt", fb.getCreatedAt());
                result.add(item);
            }
            
            // 按创建时间降序
            result.sort((a, b) -> {
                Object timeA = a.get("createdAt");
                Object timeB = b.get("createdAt");
                if (timeA != null && timeB != null) {
                    return timeB.toString().compareTo(timeA.toString());
                }
                return 0;
            });
            
            // 分页处理
            int start = (page - 1) * size;
            int end = Math.min(start + size, result.size());
            List<Map<String, Object>> paginated = result.subList(start, end);
            
            return ApiResponse.success(paginated);
        } catch (Exception e) {
            log.error("获取反馈列表失败", e);
            return ApiResponse.error(e.getMessage());
        }
    }

    /**
     * 回复学生反馈
     */
    @PostMapping("/feedbacks/{feedbackId}/reply")
    public ApiResponse<String> replyToFeedback(@PathVariable Long feedbackId, @RequestBody Map<String, String> params) {
        try {
            String replyContent = params.get("reply_content");
            teacherService.replyFeedback(feedbackId, replyContent);
            return ApiResponse.success("回复成功");
        } catch (Exception e) {
            log.error("回复反馈失败", e);
            return ApiResponse.error(e.getMessage());
        }
    }

    /**
     * 教师申请管理班级
     */
    @PostMapping("/class-management/request")
    public ApiResponse<Long> requestClassManagement(@RequestBody Map<String, Object> params) {
        try {
            Long teacherId = ((Number) params.get("teacherId")).longValue();
            Long classId = ((Number) params.get("classId")).longValue();
            String reason = (String) params.get("reason");

            Long requestId = classManagementRequestService.submitRequest(teacherId, classId, "teacher", reason);
            return ApiResponse.success(requestId);
        } catch (Exception e) {
            log.error("申请班级管理失败", e);
            return ApiResponse.error(e.getMessage());
        }
    }

    /**
     * 获取所有教师列表
     */
    @GetMapping("/list")
    public ApiResponse<List<Map<String, Object>>> getTeachers() {
        try {
            List<Map<String, Object>> teachers = teacherService.getAllTeachers();
            return ApiResponse.success(teachers);
        } catch (Exception e) {
            log.error("获取教师列表失败", e);
            return ApiResponse.error(e.getMessage());
        }
    }

    /**
     * 获取教师的班级管理申请列表
     */
    @GetMapping("/class-management/requests")
    public ApiResponse<List<ClassManagementRequest>> getMyClassRequests(@RequestParam Long teacherId) {
        try {
            List<ClassManagementRequest> requests = classManagementRequestService.getTeacherRequests(teacherId);
            return ApiResponse.success(requests);
        } catch (Exception e) {
            log.error("获取班级管理申请失败", e);
            return ApiResponse.error(e.getMessage());
        }
    }
    
    /**
     * 搜索班级
     */
    @GetMapping("/class-management/search")
    public ApiResponse<List<Map<String, Object>>> searchClasses(@RequestParam(required = false) String keyword) {
        try {
            List<Map<String, Object>> classes = classManagementRequestService.searchClasses(keyword);
            return ApiResponse.success(classes);
        } catch (Exception e) {
            log.error("搜索班级失败", e);
            return ApiResponse.error(e.getMessage());
        }
    }
    
    /**
     * 教师申请管理班级
     */
    @PostMapping("/class-management/apply")
    public ApiResponse<Map<String, Object>> applyClassManagement(@RequestBody Map<String, Object> params) {
        try {
            Long teacherId = ((Number) params.get("teacherId")).longValue();
            Long classId = ((Number) params.get("classId")).longValue();
            String reason = (String) params.get("reason");
            
            Long requestId = classManagementRequestService.submitRequest(teacherId, classId, "teacher", reason);
            
            Map<String, Object> result = new HashMap<>();
            result.put("requestId", requestId);
            result.put("message", "申请已提交，等待管理员审核");
            return ApiResponse.success(result);
        } catch (Exception e) {
            log.error("申请班级管理失败", e);
            return ApiResponse.error(e.getMessage());
        }
    }
    
    /**
     * 获取教师的所有班级
     */
    @GetMapping("/class-management/my-classes")
    public ApiResponse<List<Map<String, Object>>> getMyClasses(@RequestParam Long teacherId) {
        try {
            List<Map<String, Object>> classes = classManagementRequestService.getTeacherClasses(teacherId);
            return ApiResponse.success(classes);
        } catch (Exception e) {
            log.error("获取我的班级失败", e);
            return ApiResponse.error(e.getMessage());
        }
    }
    
    /**
     * 解析 Excel 成绩文件
     */
    @PostMapping("/scores/parse-excel")
    public ApiResponse<List<ScoreImportRequest.ScoreImportItem>> parseExcelFile(
            @RequestParam("file") MultipartFile file) {
        try {
            if (file.isEmpty()) {
                return ApiResponse.error("请上传 Excel 文件");
            }
            
            // 检查文件类型
            String filename = file.getOriginalFilename();
            if (!filename.endsWith(".xlsx") && !filename.endsWith(".xls")) {
                return ApiResponse.error("仅支持 Excel 文件");
            }
            
            List<ScoreImportRequest.ScoreImportItem> items = excelScoreImportService.parseExcelFile(file);
            return ApiResponse.success(items);
        } catch (Exception e) {
            log.error("解析 Excel 文件失败", e);
            return ApiResponse.error(e.getMessage());
        }
    }
    
    /**
     * 导入成绩（根据Excel中的课程名称自动识别）
     */
    @PostMapping("/scores/import")
    public ApiResponse<Map<String, Object>> importScores(
            @RequestParam("file") MultipartFile file,
            @RequestParam("semester") String semester,
            @RequestParam(required = false) Long courseId) {
        try {
            if (file.isEmpty()) {
                return ApiResponse.error("文件不能为空");
            }
            
            if (semester == null || semester.isEmpty()) {
                return ApiResponse.error("学期不能为空");
            }
            
            // 解析Excel文件
            List<ScoreImportRequest.ScoreImportItem> items = excelScoreImportService.parseExcelFile(file);
            
            if (items == null || items.isEmpty()) {
                return ApiResponse.error("成绩数据为空");
            }
            
            Map<String, Object> result;
            if (courseId != null) {
                // 如果提供了courseId，使用按课程ID导入的方法
                result = excelScoreImportService.importScoresByCourseId(courseId, items, semester);
            } else {
                // 否则使用根据课程名称自动识别的方法
                ScoreImportRequest importRequest = new ScoreImportRequest();
                importRequest.setSemester(semester);
                importRequest.setItems(items);
                result = excelScoreImportService.importScores(importRequest);
            }
            
            return ApiResponse.success(result);
        } catch (Exception e) {
            log.error("导入成绩失败", e);
            return ApiResponse.error(e.getMessage());
        }
    }
    
    /**
     * 导入成绩（按课程ID）
     */
    @PostMapping("/scores/import-by-course")
    public ApiResponse<Map<String, Object>> importScoresByCourse(
            @RequestParam("file") MultipartFile file,
            @RequestParam("courseId") Long courseId,
            @RequestParam("semester") String semester) {
        try {
            if (file.isEmpty()) {
                return ApiResponse.error("文件不能为空");
            }
            
            if (courseId == null) {
                return ApiResponse.error("课程ID不能为空");
            }
            
            if (semester == null || semester.isEmpty()) {
                return ApiResponse.error("学期不能为空");
            }
            
            // 解析Excel文件
            List<ScoreImportRequest.ScoreImportItem> items = excelScoreImportService.parseExcelFile(file);
            
            if (items == null || items.isEmpty()) {
                return ApiResponse.error("成绩数据为空");
            }
            
            // 直接调用方法保存成绩（按courseId）
            Map<String, Object> result = excelScoreImportService.importScoresByCourseId(courseId, items, semester);
            return ApiResponse.success(result);
        } catch (Exception e) {
            log.error("导入成绩失败", e);
            return ApiResponse.error(e.getMessage());
        }
    }
    
    /**
     * 导入学生信息
     */
    @PostMapping("/students/import")
    public ApiResponse<Map<String, Object>> importStudents(
            @RequestParam("file") MultipartFile file,
            @RequestParam("classId") Long classId) {
        try {
            if (file.isEmpty()) {
                return ApiResponse.error("文件不能为空");
            }
            
            if (classId == null) {
                return ApiResponse.error("班级ID不能为空");
            }
            
            // 解析Excel文件
            List<Map<String, Object>> studentData = parseStudentExcelFile(file);
            
            if (studentData == null || studentData.isEmpty()) {
                return ApiResponse.error("学生数据为空");
            }
            
            // 导入学生信息
            Map<String, Object> result = teacherService.importStudents(classId, studentData);
            return ApiResponse.success(result);
        } catch (Exception e) {
            log.error("导入学生失败", e);
            return ApiResponse.error(e.getMessage());
        }
    }
    
    /**
     * 课程成绩分析
     */
    @GetMapping("/scores/analyze")
    public ApiResponse<Map<String, Object>> analyzeScores(@RequestParam("course_id") Long courseId) {
        try {
            Map<String, Object> analysis = teacherService.analyzeCourseScores(courseId);
            return ApiResponse.success(analysis);
        } catch (Exception e) {
            log.error("课程成绩分析失败", e);
            return ApiResponse.error(e.getMessage());
        }
    }
    
    /**
     * 成绩异常检测
     */
    @GetMapping("/scores/anomalies")
    public ApiResponse<List<Map<String, Object>>> detectAnomalies(@RequestParam("course_id") Long courseId) {
        try {
            List<Map<String, Object>> anomalies = teacherService.detectScoreAnomalies(courseId);
            return ApiResponse.success(anomalies);
        } catch (Exception e) {
            log.error("成绩异常检测失败", e);
            return ApiResponse.error(e.getMessage());
        }
    }
    
    /**
     * 触发预警
     */
    @PostMapping("/scores/warnings")
    public ApiResponse<Integer> triggerWarnings(@RequestParam("course_id") Long courseId) {
        try {
            int warningCount = teacherService.triggerScoreWarnings(courseId);
            return ApiResponse.success(warningCount);
        } catch (Exception e) {
            log.error("触发预警失败", e);
            return ApiResponse.error(e.getMessage());
        }
    }
    
    /**
     * 学生成绩分析
     */
    @GetMapping("/scores/student/analyze")
    public ApiResponse<Map<String, Object>> analyzeStudentScore(
            @RequestParam("student_id") Long studentId,
            @RequestParam("course_id") Long courseId) {
        try {
            Map<String, Object> analysis = teacherService.analyzeStudentScore(studentId, courseId);
            return ApiResponse.success(analysis);
        } catch (Exception e) {
            log.error("学生成绩分析失败", e);
            return ApiResponse.error(e.getMessage());
        }
    }
    
    /**
     * 删除成绩
     */
    @DeleteMapping("/scores/{scoreId}")
    public ApiResponse<String> deleteScore(@PathVariable Long scoreId) {
        try {
            teacherService.deleteScore(scoreId);
            return ApiResponse.success("成绩删除成功");
        } catch (Exception e) {
            log.error("删除成绩失败", e);
            return ApiResponse.error(e.getMessage());
        }
    }
    
    /**
     * 批量删除成绩
     */
    @PostMapping("/scores/batch-delete")
    public ApiResponse<String> batchDeleteScores(@RequestBody List<Long> scoreIds) {
        try {
            teacherService.batchDeleteScores(scoreIds);
            return ApiResponse.success("批量删除成绩成功");
        } catch (Exception e) {
            log.error("批量删除成绩失败", e);
            return ApiResponse.error(e.getMessage());
        }
    }
    
    /**
     * 获取班级成绩分析
     */
    @GetMapping("/classes/{classId}/analysis")
    public ApiResponse<Map<String, Object>> getClassScoreAnalysis(@PathVariable Long classId) {
        try {
            Map<String, Object> analysis = teacherService.getClassScoreAnalysis(classId);
            return ApiResponse.success(analysis);
        } catch (Exception e) {
            log.error("获取班级成绩分析失败", e);
            return ApiResponse.error(e.getMessage());
        }
    }
    
    /**
     * 解析学生Excel文件
     */
    private List<Map<String, Object>> parseStudentExcelFile(MultipartFile file) throws Exception {
        List<Map<String, Object>> studentData = new ArrayList<>();
        
        try (InputStream inputStream = file.getInputStream();
             Workbook workbook = WorkbookFactory.create(inputStream)) {
            
            Sheet sheet = workbook.getSheetAt(0);
            
            // 跳过标题行，从第二行开始
            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row == null) continue;
                
                try {
                    Map<String, Object> student = new HashMap<>();
                    
                    // 获取单元格值
                    String studentId = getStringCellValue(row.getCell(0));
                    String name = getStringCellValue(row.getCell(1));
                    String gender = getStringCellValue(row.getCell(2));
                    String phone = getStringCellValue(row.getCell(3));
                    String email = getStringCellValue(row.getCell(4));
                    
                    // 验证必要字段
                    if (studentId == null || studentId.isEmpty() ||
                        name == null || name.isEmpty()) {
                        log.warn("第 {} 行数据不完整，跳过", i + 1);
                        continue;
                    }
                    
                    student.put("studentId", studentId);
                    student.put("name", name);
                    student.put("gender", gender);
                    student.put("phone", phone);
                    student.put("email", email);
                    
                    studentData.add(student);
                } catch (Exception e) {
                    log.warn("第 {} 行解析失败: {}", i + 1, e.getMessage());
                }
            }
            
            log.info("Excel 文件解析完成，共解析 {} 条学生记录", studentData.size());
        }
        
        return studentData;
    }
    
    /**
     * 获取字符串单元格值
     */
    private String getStringCellValue(Cell cell) {
        if (cell == null) return null;
        
        switch (cell.getCellType()) {
            case STRING:
                return cell.getStringCellValue().trim();
            case NUMERIC:
                return String.valueOf((int) cell.getNumericCellValue());
            default:
                return null;
        }
    }
}
