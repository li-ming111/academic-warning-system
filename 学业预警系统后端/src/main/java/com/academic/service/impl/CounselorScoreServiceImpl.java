package com.academic.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.academic.entity.*;
import com.academic.mapper.*;
import com.academic.service.CounselorScoreService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import java.util.*;

@Slf4j
@Service
public class CounselorScoreServiceImpl implements CounselorScoreService {

    private final ScoreMapper scoreMapper;
    private final StudentProfileMapper studentProfileMapper;
    private final EnrollmentMapper enrollmentMapper;
    private final ClassMapper classMapper;

    public CounselorScoreServiceImpl(ScoreMapper scoreMapper, StudentProfileMapper studentProfileMapper, 
                                    EnrollmentMapper enrollmentMapper, ClassMapper classMapper) {
        this.scoreMapper = scoreMapper;
        this.studentProfileMapper = studentProfileMapper;
        this.enrollmentMapper = enrollmentMapper;
        this.classMapper = classMapper;
    }

    @Override
    public List<Map<String, Object>> getClassScores(Long counselorId, Long classId) {
        // 获取班级学生列表
        QueryWrapper<Enrollment> enrollQw = new QueryWrapper<>();
        enrollQw.eq("class_id", classId);
        List<Enrollment> enrollments = enrollmentMapper.selectList(enrollQw);
        
        List<Map<String, Object>> result = new ArrayList<>();
        for (Enrollment enrollment : enrollments) {
            // 获取学生的成绩
            QueryWrapper<Score> scoreQw = new QueryWrapper<>();
            scoreQw.eq("student_id", enrollment.getStudentId());
            List<Score> scores = scoreMapper.selectList(scoreQw);
            
            // 计算平均成绩
            double avgScore = scores.stream()
                    .mapToDouble(s -> s.getScoreTotal() != null ? s.getScoreTotal().doubleValue() : 0)
                    .average()
                    .orElse(0);
            
            StudentProfile student = studentProfileMapper.selectById(enrollment.getStudentId());
            Map<String, Object> map = new HashMap<>();
            map.put("studentId", enrollment.getStudentId());
            if (student != null) {
                map.put("studentName", student.getName());
            }
            map.put("averageScore", String.format("%.2f", avgScore));
            map.put("scoreCount", scores.size());
            result.add(map);
        }
        return result;
    }

    @Override
    public Map<String, Object> getCourseScoreDistribution(Long courseId) {
        // 获取该课程的所有成绩
        QueryWrapper<Score> qw = new QueryWrapper<>();
        qw.eq("course_id", courseId);
        List<Score> scores = scoreMapper.selectList(qw);
        
        Map<String, Object> result = new HashMap<>();
        // 按成绩段分类统计
        long excellent = scores.stream().filter(s -> s.getScoreTotal() != null && s.getScoreTotal().compareTo(new java.math.BigDecimal(90)) >= 0).count();
        long good = scores.stream().filter(s -> s.getScoreTotal() != null && s.getScoreTotal().compareTo(new java.math.BigDecimal(80)) >= 0 && s.getScoreTotal().compareTo(new java.math.BigDecimal(90)) < 0).count();
        long normal = scores.stream().filter(s -> s.getScoreTotal() != null && s.getScoreTotal().compareTo(new java.math.BigDecimal(70)) >= 0 && s.getScoreTotal().compareTo(new java.math.BigDecimal(80)) < 0).count();
        long pass = scores.stream().filter(s -> s.getScoreTotal() != null && s.getScoreTotal().compareTo(new java.math.BigDecimal(60)) >= 0 && s.getScoreTotal().compareTo(new java.math.BigDecimal(70)) < 0).count();
        long fail = scores.stream().filter(s -> s.getScoreTotal() == null || s.getScoreTotal().compareTo(new java.math.BigDecimal(60)) < 0).count();
        
        result.put("excellent", excellent);
        result.put("good", good);
        result.put("normal", normal);
        result.put("pass", pass);
        result.put("fail", fail);
        result.put("total", scores.size());
        return result;
    }

    @Override
    public List<Map<String, Object>> getLowScoreStudents(Long counselorId) {
        // 获取所有低分学生（成绩低于60分）
        List<Score> lowScores = scoreMapper.selectList(
                new QueryWrapper<Score>().lt("score_total", 60)
        );
        
        List<Map<String, Object>> result = new ArrayList<>();
        for (Score score : lowScores) {
            StudentProfile student = studentProfileMapper.selectById(score.getStudentId());
            if (student != null) {
                Map<String, Object> map = new HashMap<>();
                map.put("studentId", student.getId());
                map.put("studentName", student.getName());
                map.put("score", score.getScoreTotal() != null ? score.getScoreTotal().doubleValue() : 0);
                map.put("riskLevel", score.getScoreTotal() != null && score.getScoreTotal().doubleValue() < 50 ? "red" : "yellow");
                result.add(map);
            }
        }
        return result;
    }

    @Override
    public List<Map<String, Object>> getScoreTrend(Long studentId) {
        // 获取学生的成绩变化趋势
        QueryWrapper<Score> qw = new QueryWrapper<>();
        qw.eq("student_id", studentId);
        qw.orderByAsc("semester");
        List<Score> scores = scoreMapper.selectList(qw);
        
        List<Map<String, Object>> result = new ArrayList<>();
        for (Score score : scores) {
            Map<String, Object> map = new HashMap<>();
            map.put("semester", score.getSemester() != null ? score.getSemester() : "未知学期");
            map.put("score", score.getScoreTotal() != null ? score.getScoreTotal() : 0);
            map.put("courseId", score.getCourseId());
            result.add(map);
        }
        return result;
    }

    @Override
    public Map<String, Object> getClassCreditStatistics(Long counselorId, Long classId) {
        // 获取班级学分统计
        QueryWrapper<Enrollment> enrollQw = new QueryWrapper<>();
        enrollQw.eq("class_id", classId);
        List<Enrollment> enrollments = enrollmentMapper.selectList(enrollQw);
        
        int creditAchievementCount = 0; // 学分达标的学生数
        int creditInsufficientCount = 0; // 学分不足的学生数
        
        for (Enrollment enrollment : enrollments) {
            // 计算学生的总学分
            QueryWrapper<Score> scoreQw = new QueryWrapper<>();
            scoreQw.eq("student_id", enrollment.getStudentId());
            List<Score> scores = scoreMapper.selectList(scoreQw);
            
            double totalCredits = scores.stream()
                    .mapToDouble(s -> s.getGradePoint() != null ? s.getGradePoint().doubleValue() : 0)
                    .sum();
            
            // 假设学分达标线为120
            if (totalCredits >= 120) {
                creditAchievementCount++;
            } else {
                creditInsufficientCount++;
            }
        }
        
        Map<String, Object> result = new HashMap<>();
        result.put("totalStudents", enrollments.size());
        result.put("creditAchievementCount", creditAchievementCount);
        result.put("creditInsufficientCount", creditInsufficientCount);
        result.put("creditAchievementRate", enrollments.isEmpty() ? 0 : 
                (creditAchievementCount * 100.0 / enrollments.size()));
        return result;
    }
}
