package com.academic.service.impl;

import com.academic.mapper.ScoreMapper;
import com.academic.mapper.CourseMapper;
import com.academic.service.WarningService;
import com.academic.service.ScoreService;
import com.academic.service.StatisticsService;
import com.academic.dto.StatisticsResponse;
import com.academic.entity.Score;
import com.academic.entity.Course;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class StatisticsServiceImpl implements StatisticsService {

    private final ScoreMapper scoreMapper;
    private final CourseMapper courseMapper;
    private final WarningService warningService;
    private final ScoreService scoreService;

    @Override
    public StatisticsResponse getStudentStatistics(Long studentId) {
        StatisticsResponse response = new StatisticsResponse();

        try {
            // 获取所有成绩
            List<Score> allScores = scoreService.getStudentScores(studentId, null);

            if (allScores.isEmpty()) {
                return response;
            }

            // 计算GPA相关数据
            BigDecimal currentGPA = scoreService.calculateGPA(studentId);
            response.setCurrentGPA(currentGPA);

            // 计算平均分、最高分、最低分
            List<BigDecimal> scores = allScores.stream()
                    .map(Score::getScoreTotal)
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());

            if (!scores.isEmpty()) {
                BigDecimal avg = scores.stream()
                        .reduce(BigDecimal.ZERO, BigDecimal::add)
                        .divide(BigDecimal.valueOf(scores.size()), 2, BigDecimal.ROUND_HALF_UP);
                response.setAverageGPA(avg);

                response.setHighestGPA(scores.stream().max(BigDecimal::compareTo).orElse(BigDecimal.ZERO));
                response.setLowestGPA(scores.stream().min(BigDecimal::compareTo).orElse(BigDecimal.ZERO));
            }

            // 计算成绩分布
            int excellent = (int) scores.stream().filter(s -> s.compareTo(BigDecimal.valueOf(85)) >= 0).count();
            int good = (int) scores.stream().filter(s -> s.compareTo(BigDecimal.valueOf(75)) >= 0 && s.compareTo(BigDecimal.valueOf(85)) < 0).count();
            int pass = (int) scores.stream().filter(s -> s.compareTo(BigDecimal.valueOf(60)) >= 0 && s.compareTo(BigDecimal.valueOf(75)) < 0).count();
            int fail = (int) scores.stream().filter(s -> s.compareTo(BigDecimal.valueOf(60)) < 0).count();

            response.setExcellentCount(excellent);
            response.setGoodCount(good);
            response.setPassCount(pass);
            response.setFailCount(fail);

            // 获取预警统计
            response.setHighWarningCount(warningService.countWarningsByLevel(studentId, "high"));
            response.setMediumWarningCount(warningService.countWarningsByLevel(studentId, "medium"));
            response.setLowWarningCount(warningService.countWarningsByLevel(studentId, "low"));

            // 获取学期成绩趋势
            response.setSemesterTrend(getSemesterTrend(studentId));

            // 获取课程成绩分布
            List<Map<String, Object>> distribution = new ArrayList<>();
            for (Score score : allScores) {
                Map<String, Object> map = new HashMap<>();
                Course course = courseMapper.selectById(score.getCourseId());
                if (course != null) {
                    map.put("name", course.getName());
                    map.put("value", score.getScoreTotal());
                }
                distribution.add(map);
            }
            response.setCourseDistribution(distribution);

            // 获取课程成绩详情
            List<StatisticsResponse.CourseScoreInfo> courseScores = new ArrayList<>();
            for (Score score : allScores) {
                Course course = courseMapper.selectById(score.getCourseId());
                if (course != null) {
                    StatisticsResponse.CourseScoreInfo info = new StatisticsResponse.CourseScoreInfo();
                    info.setCourseName(course.getName());
                    info.setScore(score.getScoreTotal());
                    info.setCredits(course.getCredits().intValue());
                    info.setSemester(score.getSemester());
                    info.setTrend("stable"); // 实际应该通过历史数据计算趋势
                    courseScores.add(info);
                }
            }
            response.setCourseScores(courseScores);

        } catch (Exception e) {
            log.error("获取学生统计数据失败", e);
        }

        return response;
    }

    @Override
    public StatisticsResponse.CourseScoreInfo[] getScoreDistribution(Long studentId) {
        List<Score> scores = scoreService.getStudentScores(studentId, null);
        return scores.stream().map(score -> {
            StatisticsResponse.CourseScoreInfo info = new StatisticsResponse.CourseScoreInfo();
            Course course = courseMapper.selectById(score.getCourseId());
            if (course != null) {
                info.setCourseName(course.getName());
                info.setScore(score.getScoreTotal());
                info.setCredits(course.getCredits().intValue());
            }
            return info;
        }).toArray(StatisticsResponse.CourseScoreInfo[]::new);
    }

    @Override
    public List<Map<String, Object>> getSemesterTrend(Long studentId) {
        List<Score> scores = scoreService.getStudentScores(studentId, null);
        
        // 按学期分组
        Map<String, List<Score>> bySemester = scores.stream()
                .collect(Collectors.groupingBy(Score::getSemester));

        List<Map<String, Object>> trend = new ArrayList<>();
        
        for (Map.Entry<String, List<Score>> entry : bySemester.entrySet()) {
            Map<String, Object> map = new HashMap<>();
            List<BigDecimal> semesterScores = entry.getValue().stream()
                    .map(Score::getScoreTotal)
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());
            
            if (!semesterScores.isEmpty()) {
                BigDecimal avg = semesterScores.stream()
                        .reduce(BigDecimal.ZERO, BigDecimal::add)
                        .divide(BigDecimal.valueOf(semesterScores.size()), 2, BigDecimal.ROUND_HALF_UP);
                map.put("semester", entry.getKey());
                map.put("score", avg);
            }
            
            if (!map.isEmpty()) {
                trend.add(map);
            }
        }

        // 按学期排序
        trend.sort((a, b) -> String.valueOf(a.get("semester")).compareTo(String.valueOf(b.get("semester"))));

        return trend;
    }

    @Override
    public String calculateGrade(BigDecimal score) {
        if (score == null) {
            return "N/A";
        }
        
        if (score.compareTo(BigDecimal.valueOf(90)) >= 0) {
            return "A";
        } else if (score.compareTo(BigDecimal.valueOf(80)) >= 0) {
            return "B";
        } else if (score.compareTo(BigDecimal.valueOf(70)) >= 0) {
            return "C";
        } else if (score.compareTo(BigDecimal.valueOf(60)) >= 0) {
            return "D";
        } else {
            return "F";
        }
    }

}
