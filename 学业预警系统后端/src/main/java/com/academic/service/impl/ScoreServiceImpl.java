package com.academic.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.academic.entity.Score;
import com.academic.entity.Course;
import com.academic.mapper.ScoreMapper;
import com.academic.mapper.CourseMapper;
import com.academic.service.ScoreService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Slf4j
@Service
public class ScoreServiceImpl extends ServiceImpl<ScoreMapper, Score> implements ScoreService {

    private final CourseMapper courseMapper;

    public ScoreServiceImpl(CourseMapper courseMapper) {
        this.courseMapper = courseMapper;
    }

    @Override
    public List<Score> getStudentScores(Long studentId, String semester) {
        QueryWrapper<Score> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("student_id", studentId);
        if (semester != null && !semester.isEmpty()) {
            queryWrapper.eq("semester", semester);
        }
        queryWrapper.orderByDesc("created_at");
        List<Score> scores = this.list(queryWrapper);
        log.info("查询成绩: studentId={}, semester={}, 找到 {} 条成绩", studentId, semester, scores.size());
        return scores;
    }

    @Override
    public BigDecimal calculateGPA(Long studentId) {
        QueryWrapper<Score> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("student_id", studentId);
        List<Score> scores = this.list(queryWrapper);

        if (scores.isEmpty()) {
            return BigDecimal.ZERO;
        }

        BigDecimal totalGradePoints = BigDecimal.ZERO;
        BigDecimal totalCredits = BigDecimal.ZERO;

        for (Score score : scores) {
            if (score.getGradePoint() != null && score.getCourseId() != null) {
                Course course = courseMapper.selectById(score.getCourseId());
                if (course != null && course.getCredits() != null) {
                    BigDecimal credits = course.getCredits();
                    BigDecimal gradePoints = score.getGradePoint().multiply(credits);
                    totalGradePoints = totalGradePoints.add(gradePoints);
                    totalCredits = totalCredits.add(credits);
                }
            }
        }

        if (totalCredits.compareTo(BigDecimal.ZERO) > 0) {
            return totalGradePoints.divide(totalCredits, 2, BigDecimal.ROUND_HALF_UP);
        }

        return BigDecimal.ZERO;
    }

    @Override
    public BigDecimal calculateAverageScore(Long studentId) {
        QueryWrapper<Score> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("student_id", studentId);
        List<Score> scores = this.list(queryWrapper);

        if (scores.isEmpty()) {
            return BigDecimal.ZERO;
        }

        BigDecimal totalScore = BigDecimal.ZERO;
        for (Score score : scores) {
            if (score.getScoreTotal() != null) {
                totalScore = totalScore.add(score.getScoreTotal());
            }
        }

        return totalScore.divide(new BigDecimal(scores.size()), 2, BigDecimal.ROUND_HALF_UP);
    }

    @Override
    public Integer getCourseCount(Long studentId, String semester) {
        QueryWrapper<Score> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("student_id", studentId);
        if (semester != null && !semester.isEmpty()) {
            queryWrapper.eq("semester", semester);
        }
        return (int) this.count(queryWrapper);
    }

    @Override
    public void detectWarnings(Long studentId) {
        // 检测预警逻辑，将在预警服务中实现
        log.info("检测学生 {} 的预警", studentId);
    }

}
