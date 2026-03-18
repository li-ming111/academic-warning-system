package com.academic.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.academic.entity.*;
import com.academic.mapper.*;
import com.academic.service.TeacherService;
import com.academic.service.WarningService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.Collections;
import java.math.BigDecimal;

@Slf4j
@Service
public class TeacherServiceImpl extends ServiceImpl<TeacherProfileMapper, TeacherProfile> implements TeacherService {

    private final UserMapper userMapper;
    private final StudentProfileMapper studentProfileMapper;
    private final AcademicWarningMapper warningMapper;
    private final ScoreMapper scoreMapper;
    private final ScoreLogMapper scoreLogMapper;
    private final FeedbackMapper feedbackMapper;
    private final EnrollmentMapper enrollmentMapper;
    private final CommunicationLogMapper communicationLogMapper;
    private final CourseMapper courseMapper;
    private final TeacherCourseMapper teacherCourseMapper;
    private final WarningService warningService;
    private final PasswordEncoder passwordEncoder;
    private final ClassMapper classMapper;

    public TeacherServiceImpl(UserMapper userMapper, StudentProfileMapper studentProfileMapper, 
                            AcademicWarningMapper warningMapper, ScoreMapper scoreMapper,
                            ScoreLogMapper scoreLogMapper, FeedbackMapper feedbackMapper,
                            EnrollmentMapper enrollmentMapper, CommunicationLogMapper communicationLogMapper,
                            CourseMapper courseMapper, TeacherCourseMapper teacherCourseMapper, WarningService warningService,
                            PasswordEncoder passwordEncoder, ClassMapper classMapper) {
        this.userMapper = userMapper;
        this.studentProfileMapper = studentProfileMapper;
        this.warningMapper = warningMapper;
        this.scoreMapper = scoreMapper;
        this.scoreLogMapper = scoreLogMapper;
        this.feedbackMapper = feedbackMapper;
        this.enrollmentMapper = enrollmentMapper;
        this.communicationLogMapper = communicationLogMapper;
        this.courseMapper = courseMapper;
        this.teacherCourseMapper = teacherCourseMapper;
        this.warningService = warningService;
        this.passwordEncoder = passwordEncoder;
        this.classMapper = classMapper;
    }

    @Override
    public TeacherProfile getByUserId(Long userId) {
        QueryWrapper<TeacherProfile> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId);
        return this.getOne(queryWrapper);
    }

    @Override
    public Long registerTeacher(User user, TeacherProfile profile) {
        // 保存用户
        userMapper.insert(user);
        // 保存教师档案
        profile.setUserId(user.getId());
        this.save(profile);
        return user.getId();
    }

    @Override
    public Integer getStudentCount(Long teacherId) {
        TeacherProfile teacher = this.getById(teacherId);
        if (teacher == null) {
            return 0;
        }

        QueryWrapper<StudentProfile> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("college_id", teacher.getCollegeId());
        return Math.toIntExact(studentProfileMapper.selectCount(queryWrapper));
    }

    @Override
    public Long getWarningCount(Long teacherId) {
        TeacherProfile teacher = this.getById(teacherId);
        if (teacher == null) {
            return 0L;
        }

        QueryWrapper<AcademicWarning> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("college_id", teacher.getCollegeId());
        queryWrapper.in("status", "pending", "confirmed");
        return warningMapper.selectCount(queryWrapper);
    }

    @Override
    public Long getWarningCountByLevel(Long teacherId, String level) {
        TeacherProfile teacher = this.getById(teacherId);
        if (teacher == null) {
            return 0L;
        }

        QueryWrapper<AcademicWarning> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("college_id", teacher.getCollegeId());
        queryWrapper.eq("warning_level", level);
        queryWrapper.in("status", "pending", "confirmed");
        return warningMapper.selectCount(queryWrapper);
    }

    @Override
    public List<Score> getTeacherScores(Long teacherId, String courseId) {
        TeacherProfile teacher = this.getById(teacherId);
        if (teacher == null) {
            return List.of();
        }

        QueryWrapper<Score> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("course_id", courseId);
        return scoreMapper.selectList(queryWrapper);
    }

    @Override
    public void saveOrUpdateScore(Score score) {
        if (score.getId() == null) {
            scoreMapper.insert(score);
        } else {
            // 记录修改日志
            Score oldScore = scoreMapper.selectById(score.getId());
            if (oldScore != null && oldScore.getScoreTotal() != null && score.getScoreTotal() != null) {
                ScoreLog log = new ScoreLog();
                log.setScoreId(score.getId());
                log.setOldScore(oldScore.getScoreTotal());
                log.setNewScore(score.getScoreTotal());
                log.setCreatedAt(LocalDateTime.now());
                scoreLogMapper.insert(log);
            }
            scoreMapper.updateById(score);
        }
    }

    @Override
    public List<Feedback> getTeacherFeedbacks(Long teacherId, String category) {
        QueryWrapper<Feedback> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("teacher_id", teacherId);
        if (category != null && !category.isEmpty()) {
            queryWrapper.eq("category", category);
        }
        queryWrapper.orderByDesc("created_at");
        return feedbackMapper.selectList(queryWrapper);
    }

    @Override
    public void replyFeedback(Long feedbackId, String replyContent) {
        Feedback feedback = feedbackMapper.selectById(feedbackId);
        if (feedback != null) {
            feedback.setReplyContent(replyContent);
            feedback.setUpdatedAt(LocalDateTime.now());
            feedbackMapper.updateById(feedback);
        }
    }

    @Override
    public List<Enrollment> getEnrollments(Long teacherId, String courseId) {
        // 只查询该教师对应的课程的选修课
        QueryWrapper<Enrollment> queryWrapper = new QueryWrapper<>();
        
        if (teacherId != null && teacherId > 0) {
            // 查询该教师教授的课程
            QueryWrapper<Course> courseWrapper = new QueryWrapper<>();
            courseWrapper.eq("teacher_id", teacherId);
            List<Course> teacherCourses = courseMapper.selectList(courseWrapper);
            
            if (teacherCourses.isEmpty()) {
                return List.of(); // 教师没有课程，返回空列表
            }
            
            // 获取所有课程ID
            List<Long> courseIds = teacherCourses.stream()
                    .map(Course::getId)
                    .toList();
            
            // 查询这些课程的选修课数据
            queryWrapper.in("course_id", courseIds);
        } else {
            // 如果没有传入teacherId，返回空列表
            return List.of();
        }
        
        return enrollmentMapper.selectList(queryWrapper);
    }

    @Override
    public void saveCommunicationLog(CommunicationLog log) {
        log.setCreatedAt(LocalDateTime.now());
        communicationLogMapper.insert(log);
    }

    @Override
    public List<AcademicWarning> getTeacherWarnings(Long teacherId, String status) {
        TeacherProfile teacher = this.getById(teacherId);
        if (teacher == null) {
            return List.of();
        }

        QueryWrapper<AcademicWarning> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("college_id", teacher.getCollegeId());
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
    public java.util.Map<String, Object> getTeacherTodos(Long teacherId) {
        java.util.Map<String, Object> todos = new java.util.HashMap<>();
        TeacherProfile teacher = this.getById(teacherId);
        if (teacher == null) {
            return todos;
        }

        // 未处理的预警
        QueryWrapper<AcademicWarning> warningQuery = new QueryWrapper<>();
        warningQuery.eq("college_id", teacher.getCollegeId());
        warningQuery.eq("status", "pending");
        Long pendingWarnings = warningMapper.selectCount(warningQuery);
        todos.put("pendingWarnings", pendingWarnings);

        // 待回复的反馈
        QueryWrapper<Feedback> feedbackQuery = new QueryWrapper<>();
        feedbackQuery.eq("teacher_id", teacher.getUserId());
        feedbackQuery.isNull("reply_content");
        Long pendingFeedbacks = feedbackMapper.selectCount(feedbackQuery);
        todos.put("pendingFeedbacks", pendingFeedbacks);

        // 被指派的成绩修改䮿求
        QueryWrapper<com.academic.entity.ScoreAppeal> appealQuery = new QueryWrapper<>();
        appealQuery.eq("status", "under_appeal");
        // TODO: 需要根据教师管理的课程来筛选
        Long pendingAppeals = 0L;
        todos.put("pendingAppeals", pendingAppeals);

        return todos;
    }

    @Override
    public void generateWarningsForStudent(Long studentId, String semester) {
        warningService.generateWarningsByFailedCount(studentId, semester);
    }

    @Override
    public List<Course> getTeacherCourses(Long teacherId) {
        TeacherProfile teacher = this.getById(teacherId);
        if (teacher == null) {
            return List.of();
        }
        
        // 直接返回所有课程，这样教师可以看到所有课程，包括新创建的课程
        QueryWrapper<Course> courseWrapper = new QueryWrapper<>();
        courseWrapper.orderByDesc("created_at");
        return courseMapper.selectList(courseWrapper);
    }
    
    @Override
    public List<Course> getTeacherCoursesByTeacherId(Long teacherId) {
        // 通过teacher_course表查询教师的课程
        QueryWrapper<TeacherCourse> tcWrapper = new QueryWrapper<>();
        tcWrapper.eq("teacher_id", teacherId);
        List<TeacherCourse> teacherCourses = teacherCourseMapper.selectList(tcWrapper);
        
        if (teacherCourses.isEmpty()) {
            return List.of();
        }
        
        List<Long> courseIds = teacherCourses.stream()
            .map(TeacherCourse::getCourseId)
            .toList();
        
        QueryWrapper<Course> courseWrapper = new QueryWrapper<>();
        courseWrapper.in("id", courseIds).orderByDesc("created_at");
        return courseMapper.selectList(courseWrapper);
    }
    
    @Override
    public void assignCourseToTeacher(Long teacherId, Long courseId) {
        TeacherCourse tc = new TeacherCourse();
        tc.setTeacherId(teacherId);
        tc.setCourseId(courseId);
        teacherCourseMapper.insert(tc);
    }
    
    @Override
    public void removeCourseFromTeacher(Long teacherId, Long courseId) {
        QueryWrapper<TeacherCourse> wrapper = new QueryWrapper<>();
        wrapper.eq("teacher_id", teacherId).eq("course_id", courseId);
        teacherCourseMapper.delete(wrapper);
    }

    @Override
    public List<Map<String, Object>> getCourseScores(Long courseId) {
        List<Map<String, Object>> result = new ArrayList<>();
        
        QueryWrapper<Score> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("course_id", courseId);
        // 不限制学期，查询所有学期的成绩
        List<Score> scores = scoreMapper.selectList(queryWrapper);
        
        for (Score score : scores) {
            Map<String, Object> item = new HashMap<>();
            item.put("id", score.getId());
            item.put("studentId", score.getStudentId());
            
            StudentProfile student = studentProfileMapper.selectById(score.getStudentId());
            if (student != null) {
                item.put("studentName", student.getName());
            } else {
                item.put("studentName", "未知");
            }
            
            item.put("regularScore", score.getRegularScore() != null ? score.getRegularScore().intValue() : 0);
            item.put("finalScore", score.getFinalScore() != null ? score.getFinalScore().intValue() : 0);
            item.put("totalScore", score.getScoreTotal() != null ? score.getScoreTotal().intValue() : 0);
            item.put("grade", calculateGrade(score.getScoreTotal()));
            
            result.add(item);
        }
        
        return result;
    }
    
    /**
     * 获取当前学期
     */
    private String getCurrentSemester() {
        java.util.Calendar cal = java.util.Calendar.getInstance();
        int year = cal.get(java.util.Calendar.YEAR);
        int month = cal.get(java.util.Calendar.MONTH) + 1; // 月份从0开始，所以加1
        
        // 第一学期：9月-次年2月
        // 第二学期：3月-8月
        if (month >= 9 || month <= 2) {
            // 第一学期
            int startYear = month <= 2 ? year - 1 : year;
            int endYear = month <= 2 ? year : year + 1;
            return startYear + "-" + endYear + "-1";
        } else {
            // 第二学期
            return year + "-" + (year + 1) + "-2";
        }
    }

    @Override
    public List<Score> getScoresByStudentId(Long studentId) {
        QueryWrapper<Score> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("student_id", studentId);
        queryWrapper.orderByDesc("created_at");
        return scoreMapper.selectList(queryWrapper);
    }

    @Override
    public List<ScoreLog> getScoreLogs(Long teacherId) {
        TeacherProfile teacher = this.getById(teacherId);
        if (teacher == null) {
            return List.of();
        }
        // 返回教师下所有学生的成绩修改日志
        QueryWrapper<ScoreLog> queryWrapper = new QueryWrapper<>();
        queryWrapper.orderByDesc("created_at");
        return scoreLogMapper.selectList(queryWrapper);
    }

    @Override
    public Score getScoreById(Long scoreId) {
        return scoreMapper.selectById(scoreId);
    }

    private String calculateGrade(BigDecimal scoreTotal) {
        if (scoreTotal == null) return "-";
        int score = scoreTotal.intValue();
        if (score >= 90) return "A";
        if (score >= 80) return "B";
        if (score >= 70) return "C";
        if (score >= 60) return "D";
        return "F";
    }

    @Override
    public Map<String, Object> importStudents(Long classId, List<Map<String, Object>> studentData) {
        Map<String, Object> result = new HashMap<>();
        int successCount = 0;
        int failureCount = 0;
        List<String> errorMessages = new ArrayList<>();
        
        // 验证班级是否存在
        com.academic.entity.Class clazz = classMapper.selectById(classId);
        if (clazz == null) {
            result.put("totalCount", studentData.size());
            result.put("successCount", 0);
            result.put("failureCount", studentData.size());
            result.put("errorMessages", Collections.singletonList("班级不存在"));
            return result;
        }
        
        for (Map<String, Object> studentInfo : studentData) {
            try {
                String studentId = (String) studentInfo.get("studentId");
                String name = (String) studentInfo.get("name");
                String phone = (String) studentInfo.get("phone");
                String email = (String) studentInfo.get("email");
                
                // 验证必要字段
                if (studentId == null || studentId.isEmpty() || name == null || name.isEmpty()) {
                    failureCount++;
                    errorMessages.add("学号或姓名为空");
                    continue;
                }
                
                // 检查学生是否已存在
                QueryWrapper<StudentProfile> studentQuery = new QueryWrapper<>();
                studentQuery.eq("student_id", studentId);
                StudentProfile existingStudent = studentProfileMapper.selectOne(studentQuery);
                
                if (existingStudent != null) {
                    // 更新现有学生信息
                    existingStudent.setName(name);
                    existingStudent.setClassId(classId);
                    studentProfileMapper.updateById(existingStudent);
                    
                    // 更新用户信息
                    User user = userMapper.selectById(existingStudent.getUserId());
                    if (user != null) {
                        if (phone != null && !phone.isEmpty()) user.setPhone(phone);
                        if (email != null && !email.isEmpty()) user.setEmail(email);
                        userMapper.updateById(user);
                    }
                    successCount++;
                    log.info("更新学生信息: {}", studentId);
                } else {
                    // 创建新用户
                    User user = new User();
                    user.setUsername(studentId);
                    user.setPassword(passwordEncoder.encode("123456")); // 默认密码
                    user.setPhone(phone);
                    user.setEmail(email);
                    user.setRole(1); // 学生角色
                    userMapper.insert(user);
                    
                    // 创建学生档案
                    StudentProfile studentProfile = new StudentProfile();
                    studentProfile.setUserId(user.getId());
                    studentProfile.setStudentId(studentId);
                    studentProfile.setName(name);
                    studentProfile.setClassId(classId);
                    studentProfile.setCollegeId(clazz.getCollegeId());
                    studentProfile.setMajorId(clazz.getMajorId());
                    studentProfile.setPrivacyLevel(0); // 默认公开
                    studentProfileMapper.insert(studentProfile);
                    
                    successCount++;
                    log.info("创建新学生: {}", studentId);
                }
            } catch (Exception e) {
                failureCount++;
                errorMessages.add("导入失败: " + e.getMessage());
                log.error("导入学生失败", e);
            }
        }
        
        result.put("totalCount", studentData.size());
        result.put("successCount", successCount);
        result.put("failureCount", failureCount);
        result.put("errorMessages", errorMessages);
        result.put("message", String.format("导入完成: 成功 %d 条，失败 %d 条", successCount, failureCount));
        
        return result;
    }
    
    @Override
    public Map<String, Object> getCourseScoreAnalysis(Long courseId) {
        Map<String, Object> analysis = new HashMap<>();
        
        // 获取课程信息
        Course course = courseMapper.selectById(courseId);
        if (course == null) {
            analysis.put("error", "课程不存在");
            return analysis;
        }
        analysis.put("courseName", course.getName());
        
        // 获取成绩数据
        List<Map<String, Object>> scores = getCourseScores(courseId);
        
        // 计算统计数据
        int totalStudents = scores.size();
        double totalScore = 0;
        int excellentCount = 0; // >=90
        int goodCount = 0;      // 80-89
        int averageCount = 0;   // 70-79
        int passCount = 0;      // 60-69
        int failCount = 0;      // <60
        
        for (Map<String, Object> scoreMap : scores) {
            int score = (int) scoreMap.get("totalScore");
            totalScore += score;
            
            if (score >= 90) excellentCount++;
            else if (score >= 80) goodCount++;
            else if (score >= 70) averageCount++;
            else if (score >= 60) passCount++;
            else failCount++;
        }
        
        double averageScore = totalStudents > 0 ? totalScore / totalStudents : 0;
        double passRate = totalStudents > 0 ? (double)(totalStudents - failCount) / totalStudents * 100 : 0;
        double excellentRate = totalStudents > 0 ? (double)excellentCount / totalStudents * 100 : 0;
        
        analysis.put("totalStudents", totalStudents);
        analysis.put("averageScore", Math.round(averageScore * 100) / 100.0);
        analysis.put("passRate", Math.round(passRate * 100) / 100.0);
        analysis.put("excellentRate", Math.round(excellentRate * 100) / 100.0);
        analysis.put("scoreDistribution", Map.of(
            "excellent", excellentCount,
            "good", goodCount,
            "average", averageCount,
            "pass", passCount,
            "fail", failCount
        ));
        
        return analysis;
    }
    
    @Override
    public Map<String, Object> getStudentScoreAnalysis(Long studentId) {
        Map<String, Object> analysis = new HashMap<>();
        
        // 获取学生信息
        StudentProfile student = studentProfileMapper.selectById(studentId);
        if (student == null) {
            analysis.put("error", "学生不存在");
            return analysis;
        }
        analysis.put("studentName", student.getName());
        analysis.put("studentId", student.getStudentId());
        
        // 获取学生所有成绩
        List<Score> scores = getScoresByStudentId(studentId);
        
        // 计算统计数据
        int totalCourses = scores.size();
        double totalScore = 0;
        int passCount = 0;
        int failCount = 0;
        Map<String, Double> courseScores = new HashMap<>();
        
        for (Score score : scores) {
            Course course = courseMapper.selectById(score.getCourseId());
            if (course != null) {
                double scoreValue = score.getScoreTotal() != null ? score.getScoreTotal().doubleValue() : 0;
                totalScore += scoreValue;
                courseScores.put(course.getName(), scoreValue);
                
                if (scoreValue >= 60) passCount++;
                else failCount++;
            }
        }
        
        double averageScore = totalCourses > 0 ? totalScore / totalCourses : 0;
        double passRate = totalCourses > 0 ? (double)passCount / totalCourses * 100 : 0;
        
        analysis.put("totalCourses", totalCourses);
        analysis.put("averageScore", Math.round(averageScore * 100) / 100.0);
        analysis.put("passRate", Math.round(passRate * 100) / 100.0);
        analysis.put("passCount", passCount);
        analysis.put("failCount", failCount);
        analysis.put("courseScores", courseScores);
        
        return analysis;
    }
    
    @Override
    public Map<String, Object> getClassScoreAnalysis(Long classId) {
        Map<String, Object> analysis = new HashMap<>();
        
        // 获取班级信息
        com.academic.entity.Class clazz = classMapper.selectById(classId);
        if (clazz == null) {
            analysis.put("error", "班级不存在");
            return analysis;
        }
        analysis.put("className", clazz.getName());
        
        // 获取班级学生
        QueryWrapper<StudentProfile> studentQuery = new QueryWrapper<>();
        studentQuery.eq("class_id", classId);
        List<StudentProfile> students = studentProfileMapper.selectList(studentQuery);
        
        // 统计班级成绩和QPI
        int totalStudents = students.size();
        double totalScore = 0;
        int passCount = 0;
        int failCount = 0;
        double totalQPI = 0;
        int qpiCount = 0;
        Map<String, Integer> scoreDistribution = new HashMap<>();
        scoreDistribution.put("excellent", 0);
        scoreDistribution.put("good", 0);
        scoreDistribution.put("average", 0);
        scoreDistribution.put("pass", 0);
        scoreDistribution.put("fail", 0);
        
        for (StudentProfile student : students) {
            List<Score> scores = getScoresByStudentId(student.getId());
            for (Score score : scores) {
                double scoreValue = score.getScoreTotal() != null ? score.getScoreTotal().doubleValue() : 0;
                totalScore += scoreValue;
                
                // 计算QPI
                double qpi = calculateQPI(scoreValue);
                totalQPI += qpi;
                qpiCount++;
                
                if (scoreValue >= 90) scoreDistribution.put("excellent", scoreDistribution.get("excellent") + 1);
                else if (scoreValue >= 80) scoreDistribution.put("good", scoreDistribution.get("good") + 1);
                else if (scoreValue >= 70) scoreDistribution.put("average", scoreDistribution.get("average") + 1);
                else if (scoreValue >= 60) scoreDistribution.put("pass", scoreDistribution.get("pass") + 1);
                else scoreDistribution.put("fail", scoreDistribution.get("fail") + 1);
                
                if (scoreValue >= 60) passCount++;
                else failCount++;
            }
        }
        
        int totalScores = passCount + failCount;
        double averageScore = totalScores > 0 ? totalScore / totalScores : 0;
        double passRate = totalScores > 0 ? (double)passCount / totalScores * 100 : 0;
        double averageQPI = qpiCount > 0 ? totalQPI / qpiCount : 0;
        
        analysis.put("totalStudents", totalStudents);
        analysis.put("totalScores", totalScores);
        analysis.put("averageScore", Math.round(averageScore * 100) / 100.0);
        analysis.put("passRate", Math.round(passRate * 100) / 100.0);
        analysis.put("scoreDistribution", scoreDistribution);
        analysis.put("averageQPI", Math.round(averageQPI * 100) / 100.0);
        
        return analysis;
    }
    
    /**
     * 计算QPI (Quality Point Index)
     */
    private double calculateQPI(double score) {
        if (score >= 90) return 4.0;
        else if (score >= 85) return 3.7;
        else if (score >= 80) return 3.3;
        else if (score >= 75) return 3.0;
        else if (score >= 70) return 2.7;
        else if (score >= 65) return 2.3;
        else if (score >= 60) return 2.0;
        else return 0.0;
    }
    
    @Override
    public Map<String, Object> triggerScoreWarning(Long courseId, String semester) {
        Map<String, Object> result = new HashMap<>();
        int warningCount = 0;
        List<Long> warningStudentIds = new ArrayList<>();
        
        // 获取课程成绩
        List<Map<String, Object>> scores = getCourseScores(courseId);
        
        for (Map<String, Object> scoreMap : scores) {
            int studentId = (int) scoreMap.get("studentId");
            int score = (int) scoreMap.get("totalScore");
            
            // 触发预警（成绩低于60分）
            if (score < 60) {
                warningCount++;
                warningStudentIds.add((long) studentId);
                // 生成预警记录
                generateWarningsForStudent((long) studentId, semester);
            }
        }
        
        result.put("warningCount", warningCount);
        result.put("warningStudentIds", warningStudentIds);
        result.put("message", String.format("成功触发 %d 条预警", warningCount));
        
        return result;
    }
    
    @Override
    public Map<String, Object> batchUpdateScores(List<Map<String, Object>> scoreUpdates) {
        Map<String, Object> result = new HashMap<>();
        int successCount = 0;
        int failureCount = 0;
        List<String> errorMessages = new ArrayList<>();
        
        for (Map<String, Object> update : scoreUpdates) {
            try {
                Long scoreId = ((Number) update.get("id")).longValue();
                BigDecimal scoreTotal = new BigDecimal(update.get("scoreTotal").toString());
                
                Score score = scoreMapper.selectById(scoreId);
                if (score != null) {
                    score.setScoreTotal(scoreTotal);
                    saveOrUpdateScore(score);
                    successCount++;
                } else {
                    failureCount++;
                    errorMessages.add("成绩ID不存在: " + scoreId);
                }
            } catch (Exception e) {
                failureCount++;
                errorMessages.add("更新失败: " + e.getMessage());
                log.error("批量更新成绩失败", e);
            }
        }
        
        result.put("totalCount", scoreUpdates.size());
        result.put("successCount", successCount);
        result.put("failureCount", failureCount);
        result.put("errorMessages", errorMessages);
        result.put("message", String.format("批量更新完成: 成功 %d 条，失败 %d 条", successCount, failureCount));
        
        return result;
    }
    
    @Override
    public List<Map<String, Object>> getScoreExportData(Long courseId, String semester) {
        List<Map<String, Object>> exportData = new ArrayList<>();
        
        // 获取课程信息
        Course course = courseMapper.selectById(courseId);
        if (course == null) {
            return exportData;
        }
        
        // 获取成绩数据
        List<Map<String, Object>> scores = getCourseScores(courseId);
        
        for (Map<String, Object> scoreMap : scores) {
            Map<String, Object> exportItem = new HashMap<>();
            exportItem.put("courseName", course.getName());
            exportItem.put("studentId", scoreMap.get("studentId"));
            exportItem.put("studentName", scoreMap.get("studentName"));
            exportItem.put("regularScore", scoreMap.get("regularScore"));
            exportItem.put("finalScore", scoreMap.get("finalScore"));
            exportItem.put("totalScore", scoreMap.get("totalScore"));
            exportItem.put("grade", scoreMap.get("grade"));
            exportItem.put("semester", semester);
            exportData.add(exportItem);
        }
        
        return exportData;
    }
    
    @Override
    public Map<String, Object> predictStudentScore(Long studentId, Long courseId) {
        Map<String, Object> prediction = new HashMap<>();
        
        // 获取学生信息
        StudentProfile student = studentProfileMapper.selectById(studentId);
        if (student == null) {
            prediction.put("error", "学生不存在");
            return prediction;
        }
        
        // 获取课程信息
        Course course = courseMapper.selectById(courseId);
        if (course == null) {
            prediction.put("error", "课程不存在");
            return prediction;
        }
        
        prediction.put("studentName", student.getName());
        prediction.put("courseName", course.getName());
        
        // 基于历史成绩预测（简单实现）
        List<Score> studentScores = getScoresByStudentId(studentId);
        if (studentScores.isEmpty()) {
            prediction.put("predictedScore", 60);
            prediction.put("confidence", 0.5);
            prediction.put("message", "没有历史成绩数据，基于默认值预测");
            return prediction;
        }
        
        // 计算历史平均成绩
        double totalScore = 0;
        for (Score score : studentScores) {
            if (score.getScoreTotal() != null) {
                totalScore += score.getScoreTotal().doubleValue();
            }
        }
        double averageScore = totalScore / studentScores.size();
        
        // 预测成绩（基于历史平均值）
        double predictedScore = Math.min(Math.max(averageScore, 0), 100);
        double confidence = Math.min(studentScores.size() / 10.0, 1.0); // 基于课程数量的置信度
        
        prediction.put("predictedScore", Math.round(predictedScore));
        prediction.put("confidence", Math.round(confidence * 100) / 100.0);
        prediction.put("historicalAverage", Math.round(averageScore * 100) / 100.0);
        prediction.put("message", "基于历史成绩预测");
        
        return prediction;
    }
    
    @Override
    public List<Map<String, Object>> detectScoreAnomalies(Long courseId) {
        List<Map<String, Object>> anomalies = new ArrayList<>();
        
        // 获取课程成绩
        List<Map<String, Object>> scores = getCourseScores(courseId);
        if (scores.isEmpty()) {
            return anomalies;
        }
        
        // 计算平均成绩和标准差
        double totalScore = 0;
        for (Map<String, Object> scoreMap : scores) {
            totalScore += (int) scoreMap.get("totalScore");
        }
        double averageScore = totalScore / scores.size();
        
        double sumSquaredDiff = 0;
        for (Map<String, Object> scoreMap : scores) {
            double score = (int) scoreMap.get("totalScore");
            sumSquaredDiff += Math.pow(score - averageScore, 2);
        }
        double standardDeviation = Math.sqrt(sumSquaredDiff / scores.size());
        
        // 检测异常（偏离平均值超过2个标准差）
        for (Map<String, Object> scoreMap : scores) {
            int studentId = (int) scoreMap.get("studentId");
            int score = (int) scoreMap.get("totalScore");
            double deviation = Math.abs(score - averageScore);
            
            if (deviation > 2 * standardDeviation) {
                Map<String, Object> anomaly = new HashMap<>();
                anomaly.put("studentId", studentId);
                anomaly.put("studentName", scoreMap.get("studentName"));
                anomaly.put("score", score);
                anomaly.put("averageScore", Math.round(averageScore * 100) / 100.0);
                anomaly.put("deviation", Math.round(deviation * 100) / 100.0);
                anomaly.put("type", score < averageScore ? "低于平均" : "高于平均");
                anomalies.add(anomaly);
            }
        }
        
        return anomalies;
    }
    
    @Override
    public Map<Long, String> generateStudentComments(Long courseId) {
        Map<Long, String> comments = new HashMap<>();
        
        // 获取课程成绩
        List<Map<String, Object>> scores = getCourseScores(courseId);
        
        for (Map<String, Object> scoreMap : scores) {
            Long studentId = Long.valueOf((int) scoreMap.get("studentId"));
            int score = (int) scoreMap.get("totalScore");
            String comment;
            
            if (score >= 90) {
                comment = "表现优秀，继续保持！";
            } else if (score >= 80) {
                comment = "表现良好，有潜力进一步提高。";
            } else if (score >= 70) {
                comment = "表现一般，需要加强学习。";
            } else if (score >= 60) {
                comment = "刚刚及格，需要更加努力。";
            } else {
                comment = "成绩不及格，需要认真分析原因，制定学习计划。";
            }
            
            comments.put(studentId, comment);
        }
        
        return comments;
    }
    
    @Override
    public Map<String, Object> analyzeCourseScores(Long courseId) {
        Map<String, Object> analysis = new HashMap<>();
        
        // 获取课程信息
        Course course = courseMapper.selectById(courseId);
        if (course == null) {
            analysis.put("error", "课程不存在");
            return analysis;
        }
        analysis.put("courseName", course.getName());
        
        // 获取成绩数据
        List<Map<String, Object>> scores = getCourseScores(courseId);
        
        // 计算统计数据
        int totalStudents = scores.size();
        double totalScore = 0;
        int excellentCount = 0; // >=90
        int goodCount = 0;      // 80-89
        int averageCount = 0;   // 70-79
        int passCount = 0;      // 60-69
        int failCount = 0;      // <60
        int maxScore = 0;
        int minScore = 100;
        
        for (Map<String, Object> scoreMap : scores) {
            int score = (int) scoreMap.get("totalScore");
            totalScore += score;
            maxScore = Math.max(maxScore, score);
            minScore = Math.min(minScore, score);
            
            if (score >= 90) excellentCount++;
            else if (score >= 80) goodCount++;
            else if (score >= 70) averageCount++;
            else if (score >= 60) passCount++;
            else failCount++;
        }
        
        double averageScore = totalStudents > 0 ? totalScore / totalStudents : 0;
        double passRate = totalStudents > 0 ? (double)(totalStudents - failCount) / totalStudents : 0;
        double excellentRate = totalStudents > 0 ? (double)excellentCount / totalStudents : 0;
        
        // 计算标准差
        double sumSquaredDiff = 0;
        for (Map<String, Object> scoreMap : scores) {
            double score = (int) scoreMap.get("totalScore");
            sumSquaredDiff += Math.pow(score - averageScore, 2);
        }
        double standardDeviation = totalStudents > 0 ? Math.sqrt(sumSquaredDiff / totalStudents) : 0;
        
        analysis.put("totalStudents", totalStudents);
        analysis.put("averageScore", Math.round(averageScore * 100) / 100.0);
        analysis.put("maxScore", maxScore);
        analysis.put("minScore", minScore);
        analysis.put("standardDeviation", Math.round(standardDeviation * 100) / 100.0);
        analysis.put("passRate", passRate);
        analysis.put("excellentRate", excellentRate);
        analysis.put("scoreDistribution", Map.of(
            "90-100", excellentCount,
            "80-89", goodCount,
            "70-79", averageCount,
            "60-69", passCount,
            "0-59", failCount
        ));
        
        return analysis;
    }
    
    @Override
    public int triggerScoreWarnings(Long courseId) {
        int warningCount = 0;
        
        // 获取课程成绩
        List<Map<String, Object>> scores = getCourseScores(courseId);
        String currentSemester = getCurrentSemester();
        
        for (Map<String, Object> scoreMap : scores) {
            Long studentId = Long.valueOf((int) scoreMap.get("studentId"));
            int score = (int) scoreMap.get("totalScore");
            
            // 触发预警（成绩低于60分）
            if (score < 60) {
                warningCount++;
                // 生成预警记录
                generateWarningsForStudent(studentId, currentSemester);
            }
        }
        
        return warningCount;
    }
    
    @Override
    public Map<String, Object> analyzeStudentScore(Long studentId, Long courseId) {
        Map<String, Object> analysis = new HashMap<>();
        
        // 获取学生信息
        StudentProfile student = studentProfileMapper.selectById(studentId);
        if (student == null) {
            analysis.put("error", "学生不存在");
            return analysis;
        }
        analysis.put("studentId", student.getStudentId());
        analysis.put("studentName", student.getName());
        
        // 获取课程信息
        Course course = courseMapper.selectById(courseId);
        if (course == null) {
            analysis.put("error", "课程不存在");
            return analysis;
        }
        
        // 获取当前课程成绩
        QueryWrapper<Score> scoreQuery = new QueryWrapper<>();
        scoreQuery.eq("student_id", studentId);
        scoreQuery.eq("course_id", courseId);
        Score currentScore = scoreMapper.selectOne(scoreQuery);
        
        int currentScoreValue = 0;
        if (currentScore != null && currentScore.getScoreTotal() != null) {
            currentScoreValue = currentScore.getScoreTotal().intValue();
        }
        analysis.put("currentScore", currentScoreValue);
        
        // 获取学生所有成绩
        List<Score> studentScores = getScoresByStudentId(studentId);
        
        // 计算统计数据
        int totalCourses = studentScores.size();
        double totalScore = 0;
        int passCount = 0;
        int failCount = 0;
        
        for (Score score : studentScores) {
            if (score.getScoreTotal() != null) {
                double scoreValue = score.getScoreTotal().doubleValue();
                totalScore += scoreValue;
                if (scoreValue >= 60) passCount++;
                else failCount++;
            }
        }
        
        double averageScore = totalCourses > 0 ? totalScore / totalCourses : 0;
        analysis.put("averageScore", Math.round(averageScore * 100) / 100.0);
        analysis.put("failedCount", failCount);
        
        // 预测成绩
        Map<String, Object> prediction = predictStudentScore(studentId, courseId);
        analysis.put("predictedScore", prediction.get("predictedScore"));
        
        // 生成预警状态
        String warningLevel;
        if (failCount >= 5) {
            warningLevel = "高级预警";
        } else if (failCount >= 3) {
            warningLevel = "中级预警";
        } else if (failCount >= 1) {
            warningLevel = "低级预警";
        } else {
            warningLevel = "无预警";
        }
        analysis.put("warningLevel", warningLevel);
        
        // 生成评语
        String comment;
        if (currentScoreValue >= 90) {
            comment = "成绩优秀，学习态度认真，继续保持！";
        } else if (currentScoreValue >= 80) {
            comment = "成绩良好，有一定潜力，建议进一步提升。";
        } else if (currentScoreValue >= 70) {
            comment = "成绩中等，需要加强学习，提高学习效率。";
        } else if (currentScoreValue >= 60) {
            comment = "成绩及格，需要更加努力，巩固基础知识。";
        } else {
            comment = "成绩不及格，需要认真分析原因，制定详细的学习计划，建议寻求老师帮助。";
        }
        analysis.put("comment", comment);
        
        // 分析成绩趋势
        if (studentScores.size() >= 2) {
            // 简单趋势分析
            double firstHalf = 0;
            double secondHalf = 0;
            int firstCount = 0;
            int secondCount = 0;
            
            for (int i = 0; i < studentScores.size(); i++) {
                Score score = studentScores.get(i);
                if (score.getScoreTotal() != null) {
                    if (i < studentScores.size() / 2) {
                        firstHalf += score.getScoreTotal().doubleValue();
                        firstCount++;
                    } else {
                        secondHalf += score.getScoreTotal().doubleValue();
                        secondCount++;
                    }
                }
            }
            
            double firstAverage = firstCount > 0 ? firstHalf / firstCount : 0;
            double secondAverage = secondCount > 0 ? secondHalf / secondCount : 0;
            
            String trend;
            if (secondAverage > firstAverage + 5) {
                trend = "上升";
            } else if (secondAverage < firstAverage - 5) {
                trend = "下降";
            } else {
                trend = "稳定";
            }
            analysis.put("trend", trend);
        } else {
            analysis.put("trend", "未知");
        }
        
        return analysis;
    }
    
    @Override
    public void deleteScore(Long scoreId) {
        // 先检查成绩是否存在
        Score score = scoreMapper.selectById(scoreId);
        if (score == null) {
            throw new RuntimeException("成绩不存在");
        }
        
        // 删除成绩
        scoreMapper.deleteById(scoreId);
        
        // 记录删除操作（可选）
        log.info("删除成绩，ID: {}", scoreId);
    }
    
    @Override
    public void batchDeleteScores(List<Long> scoreIds) {
        if (scoreIds == null || scoreIds.isEmpty()) {
            throw new RuntimeException("成绩ID列表不能为空");
        }
        
        // 批量删除成绩
        scoreMapper.deleteBatchIds(scoreIds);
        
        // 记录批量删除操作
        log.info("批量删除成绩，数量: {}, IDs: {}", scoreIds.size(), scoreIds);
    }

    @Override
    public List<Map<String, Object>> getAllTeachers() {
        List<Map<String, Object>> teachers = new ArrayList<>();
        try {
            // 查询所有角色为教师的用户
            QueryWrapper<User> userQuery = new QueryWrapper<>();
            userQuery.eq("role", 2); // 2 是教师角色
            List<User> users = userMapper.selectList(userQuery);

            for (User user : users) {
                Map<String, Object> teacherInfo = new HashMap<>();
                teacherInfo.put("id", user.getId());
                teacherInfo.put("name", user.getName());
                teacherInfo.put("username", user.getUsername());
                teacherInfo.put("email", user.getEmail());
                teacherInfo.put("phone", user.getPhone());
                teachers.add(teacherInfo);
            }
        } catch (Exception e) {
            log.error("获取教师列表失败", e);
        }
        return teachers;
    }

    @Override
    public List<CommunicationLog> getTeacherMessages(Long teacherId) {
        QueryWrapper<CommunicationLog> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("teacher_id", teacherId);
        queryWrapper.orderByDesc("created_at");
        return communicationLogMapper.selectList(queryWrapper);
    }

    @Override
    public Long getTeacherUnreadCount(Long teacherId) {
        QueryWrapper<CommunicationLog> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("teacher_id", teacherId);
        // 0表示未读
        queryWrapper.eq("status", 0);
        return communicationLogMapper.selectCount(queryWrapper);
    }

    @Override
    public boolean markMessageAsRead(Long messageId) {
        CommunicationLog log = communicationLogMapper.selectById(messageId);
        if (log != null) {
            log.setStatus(1); // 1表示已读
            communicationLogMapper.updateById(log);
            return true;
        }
        return false;
    }
}