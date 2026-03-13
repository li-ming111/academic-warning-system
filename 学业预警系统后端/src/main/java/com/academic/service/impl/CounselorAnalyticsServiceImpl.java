package com.academic.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.academic.entity.*;
import com.academic.mapper.*;
import com.academic.service.CounselorAnalyticsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.*;

@Slf4j
@Service
public class CounselorAnalyticsServiceImpl implements CounselorAnalyticsService {

    private final EnrollmentMapper enrollmentMapper;
    private final StudentProfileMapper studentProfileMapper;
    private final ScoreMapper scoreMapper;
    private final WarningMapper warningMapper;
    private final AssistancePlanMapper assistancePlanMapper;
    private final ClassMapper classMapper;

    public CounselorAnalyticsServiceImpl(EnrollmentMapper enrollmentMapper, StudentProfileMapper studentProfileMapper, ScoreMapper scoreMapper, 
                                        WarningMapper warningMapper, AssistancePlanMapper assistancePlanMapper,
                                        ClassMapper classMapper) {
        this.enrollmentMapper = enrollmentMapper;
        this.studentProfileMapper = studentProfileMapper;
        this.scoreMapper = scoreMapper;
        this.warningMapper = warningMapper;
        this.assistancePlanMapper = assistancePlanMapper;
        this.classMapper = classMapper;
    }

    @Override
    public Map<String, Object> getCreditInsufficientRate(Long counselorId) {
        // 班级学分不足率统计
        List<com.academic.entity.Class> classes = classMapper.selectList(new QueryWrapper<>());
        
        Map<String, Object> result = new HashMap<>();
        double totalInsufficientRate = 0;
        
        for (com.academic.entity.Class cls : classes) {
            // 使用StudentProfile查询班级学生而不是Enrollment
            QueryWrapper<StudentProfile> studentQw = new QueryWrapper<>();
            studentQw.eq("class_id", cls.getId());
            List<StudentProfile> students = studentProfileMapper.selectList(studentQw);
            
            int insufficientCount = 0;
            for (StudentProfile student : students) {
                QueryWrapper<Score> scoreQw = new QueryWrapper<>();
                scoreQw.eq("student_id", student.getId());
                List<Score> scores = scoreMapper.selectList(scoreQw);
                
                double totalCredits = scores.stream()
                        .mapToDouble(s -> s.getGradePoint() != null ? s.getGradePoint().doubleValue() : 0)
                        .sum();
                
                if (totalCredits < 120) {
                    insufficientCount++;
                }
            }
            
            double rate = students.isEmpty() ? 0 : (insufficientCount * 100.0 / students.size());
            totalInsufficientRate += rate;
        }
        
        result.put("rate", classes.isEmpty() ? 0 : (totalInsufficientRate / classes.size()));
        return result;
    }

    @Override
    public Map<String, Object> getWarningLevelDistribution(Long counselorId) {
        // 预警级别分布
        List<AcademicWarning> warnings = warningMapper.selectList(new QueryWrapper<>());
        
        long redCount = warnings.stream().filter(w -> "red".equals(w.getWarningLevel())).count();
        long yellowCount = warnings.stream().filter(w -> "yellow".equals(w.getWarningLevel())).count();
        long blueCount = warnings.stream().filter(w -> "blue".equals(w.getWarningLevel())).count();
        
        Map<String, Object> result = new HashMap<>();
        result.put("red", redCount);
        result.put("yellow", yellowCount);
        result.put("blue", blueCount);
        result.put("total", warnings.size());
        return result;
    }

    @Override
    public Map<String, Object> getWarningHandlingEfficiency(Long counselorId) {
        // 预警处理效率（平均处理时间）
        QueryWrapper<AcademicWarning> qw = new QueryWrapper<>();
        qw.eq("status", "processed");
        List<AcademicWarning> processedWarnings = warningMapper.selectList(qw);
        
        // 计算平均处理时间（天数）
        long totalDays = 0;
        for (AcademicWarning warning : processedWarnings) {
            if (warning.getCreatedAt() != null) {
                long days = java.time.temporal.ChronoUnit.DAYS.between(
                        warning.getCreatedAt().toLocalDate(),
                        LocalDateTime.now().toLocalDate()
                );
                totalDays += days;
            }
        }
        
        double avgDays = processedWarnings.isEmpty() ? 0 : (totalDays * 1.0 / processedWarnings.size());
        
        Map<String, Object> result = new HashMap<>();
        result.put("averageHandlingDays", String.format("%.1f", avgDays));
        result.put("processedCount", processedWarnings.size());
        return result;
    }

    @Override
    public List<Map<String, Object>> getClassCreditAchievementRanking(Long counselorId) {
        // 学生学分达标率排名（班级排名）
        List<com.academic.entity.Class> classes = classMapper.selectList(new QueryWrapper<>());
        
        List<Map<String, Object>> result = new ArrayList<>();
        for (com.academic.entity.Class cls : classes) {
            // 使用StudentProfile查询班级学生而不是Enrollment
            QueryWrapper<StudentProfile> studentQw = new QueryWrapper<>();
            studentQw.eq("class_id", cls.getId());
            List<StudentProfile> students = studentProfileMapper.selectList(studentQw);
            
            int creditAchievementCount = 0;
            for (StudentProfile student : students) {
                QueryWrapper<Score> scoreQw = new QueryWrapper<>();
                scoreQw.eq("student_id", student.getId());
                List<Score> scores = scoreMapper.selectList(scoreQw);
                
                double totalCredits = scores.stream()
                        .mapToDouble(s -> s.getGradePoint() != null ? s.getGradePoint().doubleValue() : 0)
                        .sum();
                
                if (totalCredits >= 120) {
                    creditAchievementCount++;
                }
            }
            
            double rate = students.isEmpty() ? 0 : (creditAchievementCount * 100.0 / students.size());
            
            Map<String, Object> map = new HashMap<>();
            map.put("className", cls.getName());
            map.put("achievementRate", String.format("%.1f", rate));
            map.put("achievement", creditAchievementCount);
            map.put("total", students.size());
            result.add(map);
        }
        
        // 按达标率排序
        result.sort((a, b) -> {
            double rateA = Double.parseDouble((String) a.get("achievementRate"));
            double rateB = Double.parseDouble((String) b.get("achievementRate"));
            return Double.compare(rateB, rateA);
        });
        
        return result;
    }

    @Override
    public List<Map<String, Object>> getWarningTrend(Long counselorId) {
        // 获取预警趋势（近6个月）
        LocalDateTime now = LocalDateTime.now();
        List<Map<String, Object>> result = new ArrayList<>();
        
        for (int i = 5; i >= 0; i--) {
            LocalDateTime monthStart = now.minusMonths(i).withDayOfMonth(1);
            LocalDateTime monthEnd = monthStart.plusMonths(1).minusDays(1);
            
            QueryWrapper<AcademicWarning> redQw = new QueryWrapper<>();
            redQw.between("created_at", monthStart, monthEnd)
                  .eq("warning_level", "red");
            long redCount = warningMapper.selectCount(redQw);
            
            QueryWrapper<AcademicWarning> yellowQw = new QueryWrapper<>();
            yellowQw.between("created_at", monthStart, monthEnd)
                    .eq("warning_level", "yellow");
            long yellowCount = warningMapper.selectCount(yellowQw);
            
            Map<String, Object> map = new HashMap<>();
            map.put("month", monthStart.getMonthValue() + "月");
            map.put("red", redCount);
            map.put("yellow", yellowCount);
            result.add(map);
        }
        
        return result;
    }

    @Override
    public Map<String, Object> getAssistancePlanCompletionRate(Long counselorId) {
        // 获取帮扶计划完成率
        List<AssistancePlan> plans = assistancePlanMapper.selectList(new QueryWrapper<>());
        
        long completedCount = plans.stream().filter(p -> "completed".equals(p.getStatus())).count();
        long inProgressCount = plans.stream().filter(p -> "in_progress".equals(p.getStatus())).count();
        long totalCount = plans.size();
        
        double completionRate = totalCount == 0 ? 0 : (completedCount * 100.0 / totalCount);
        
        Map<String, Object> result = new HashMap<>();
        result.put("completionRate", String.format("%.1f", completionRate));
        result.put("completedCount", completedCount);
        result.put("inProgressCount", inProgressCount);
        result.put("totalCount", totalCount);
        return result;
    }
}
