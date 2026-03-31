package com.academic.service.impl;

import com.academic.dto.StudentDashboardResponse;
import com.academic.entity.AcademicWarning;
import com.academic.entity.Score;
import com.academic.entity.StudentProfile;
import com.academic.mapper.AcademicWarningMapper;
import com.academic.mapper.ScoreMapper;
import com.academic.mapper.StudentProfileMapper;
import com.academic.service.StudentService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class StudentServiceImpl implements StudentService {

    private final StudentProfileMapper studentProfileMapper;
    private final ScoreMapper scoreMapper;
    private final AcademicWarningMapper academicWarningMapper;

    public StudentServiceImpl(StudentProfileMapper studentProfileMapper, ScoreMapper scoreMapper, AcademicWarningMapper academicWarningMapper) {
        this.studentProfileMapper = studentProfileMapper;
        this.scoreMapper = scoreMapper;
        this.academicWarningMapper = academicWarningMapper;
    }

    @Override
    public StudentDashboardResponse getStudentDashboard(Long studentId) {
        log.info("获取学生仪表盘数据: studentId={}", studentId);
        
        // 从数据库查询学生信息
        StudentProfile studentProfile = studentProfileMapper.selectById(studentId);
        if (studentProfile == null) {
            log.warn("学生不存在: studentId={}", studentId);
            return new StudentDashboardResponse();
        }
        
        // 从数据库查询学生成绩
        List<Score> scores = scoreMapper.selectByStudentId(studentId);
        int totalCoursesCount = scores.size();
        int failedCoursesCount = 0;
        double totalScore = 0;
        
        for (Score score : scores) {
            if (score.getScoreTotal() < 60) {
                failedCoursesCount++;
            }
            totalScore += score.getScoreTotal();
        }
        
        double gpa = totalCoursesCount > 0 ? totalScore / totalCoursesCount / 20 : 0;
        
        // 从数据库查询学生预警信息
        List<AcademicWarning> warnings = academicWarningMapper.selectPendingByStudentId(studentId);
        int warningCount = warnings.size();
        String warningLevel = "正常";
        if (warningCount > 0) {
            warningLevel = warnings.get(0).getWarningLevel();
        }
        
        // 构建响应对象
        StudentDashboardResponse response = new StudentDashboardResponse();
        response.setStudentId(studentId);
        response.setStudentName(studentProfile.getName());
        response.setClassName("计算机科学与技术2023级1班"); // 实际应该从班级表查询
        response.setMajorName("计算机科学与技术"); // 实际应该从专业表查询
        response.setGpa(gpa);
        response.setWarningCount(warningCount);
        response.setWarningLevel(warningLevel);
        response.setFailedCoursesCount(failedCoursesCount);
        response.setTotalCoursesCount(totalCoursesCount);
        response.setCompletedCredits(60); // 实际应该从学分表查询
        response.setRequiredCredits(120); // 实际应该从专业表查询
        
        return response;
    }

    @Override
    public Object getStudentScores(Long studentId) {
        log.info("获取学生成绩: studentId={}", studentId);
        // 从数据库查询学生成绩
        List<Score> scores = scoreMapper.selectByStudentId(studentId);
        return scores;
    }

    @Override
    public Object getStudentWarnings(Long studentId) {
        log.info("获取学生预警信息: studentId={}", studentId);
        // 从数据库查询学生预警信息
        List<AcademicWarning> warnings = academicWarningMapper.selectByStudentId(studentId);
        return warnings;
    }

    @Override
    public Object getStudentInfo(Long studentId) {
        log.info("获取学生信息: studentId={}", studentId);
        // 从数据库查询学生信息
        StudentProfile studentProfile = studentProfileMapper.selectById(studentId);
        return studentProfile;
    }
}
