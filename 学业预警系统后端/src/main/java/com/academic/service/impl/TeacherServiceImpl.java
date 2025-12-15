package com.academic.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.academic.entity.*;
import com.academic.mapper.*;
import com.academic.service.TeacherService;
import com.academic.service.WarningService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.ArrayList;
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
    private final WarningService warningService;

    public TeacherServiceImpl(UserMapper userMapper, StudentProfileMapper studentProfileMapper, 
                            AcademicWarningMapper warningMapper, ScoreMapper scoreMapper,
                            ScoreLogMapper scoreLogMapper, FeedbackMapper feedbackMapper,
                            EnrollmentMapper enrollmentMapper, CommunicationLogMapper communicationLogMapper,
                            CourseMapper courseMapper, WarningService warningService) {
        this.userMapper = userMapper;
        this.studentProfileMapper = studentProfileMapper;
        this.warningMapper = warningMapper;
        this.scoreMapper = scoreMapper;
        this.scoreLogMapper = scoreLogMapper;
        this.feedbackMapper = feedbackMapper;
        this.enrollmentMapper = enrollmentMapper;
        this.communicationLogMapper = communicationLogMapper;
        this.courseMapper = courseMapper;
        this.warningService = warningService;
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
        QueryWrapper<Enrollment> queryWrapper = new QueryWrapper<>();
        if (courseId != null && !courseId.isEmpty()) {
            queryWrapper.eq("course_id", courseId);
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
        
        QueryWrapper<Course> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("teacher_id", teacherId);
        queryWrapper.orderByDesc("created_at");
        return courseMapper.selectList(queryWrapper);
    }

    @Override
    public List<Map<String, Object>> getCourseScores(Long courseId) {
        List<Map<String, Object>> result = new ArrayList<>();
        
        QueryWrapper<Score> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("course_id", courseId);
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
            
            item.put("regularScore", 0);
            item.put("finalScore", 0);
            item.put("totalScore", score.getScoreTotal() != null ? score.getScoreTotal().intValue() : 0);
            item.put("grade", calculateGrade(score.getScoreTotal()));
            
            result.add(item);
        }
        
        return result;
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
}