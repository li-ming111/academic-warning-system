package com.academic.service.impl;

import com.academic.entity.BenchmarkAnalysis;
import com.academic.entity.StudentProfile;
import com.academic.mapper.BenchmarkAnalysisMapper;
import com.academic.mapper.ScoreMapper;
import com.academic.mapper.StudentProfileMapper;
import com.academic.service.StudentBenchmarkService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 学生对标分析服务实现
 */
@Service
@RequiredArgsConstructor
public class StudentBenchmarkServiceImpl implements StudentBenchmarkService {

    private final BenchmarkAnalysisMapper benchmarkAnalysisMapper;
    private final StudentProfileMapper studentProfileMapper;
    private final ScoreMapper scoreMapper;

    @Override
    public BenchmarkAnalysis getLatestBenchmark(Long studentId) {
        return benchmarkAnalysisMapper.getLatestAnalysis(studentId);
    }

    @Override
    public BenchmarkAnalysis getBenchmarkBySemester(Long studentId, String semester) {
        return benchmarkAnalysisMapper.getAnalysisBySemester(studentId, semester);
    }

    @Override
    public List<BenchmarkAnalysis> getHistoryBenchmark(Long studentId) {
        return benchmarkAnalysisMapper.getHistoryAnalysis(studentId);
    }

    @Override
    public Map<String, Object> getClassRanking(Long studentId, Long classId, String semester) {
        BenchmarkAnalysis benchmark = benchmarkAnalysisMapper.getAnalysisBySemester(studentId, semester);
        Map<String, Object> result = new HashMap<>();
        
        if (benchmark != null) {
            result.put("rank", benchmark.getClassRank());
            result.put("total", benchmark.getClassTotal());
            result.put("gpa", benchmark.getStudentGpa());
            result.put("classAvgGpa", benchmark.getClassAvgGpa());
            
            // 计算百分比排名
            if (benchmark.getClassTotal() != null && benchmark.getClassTotal() > 0) {
                double percentage = (double) benchmark.getClassRank() / benchmark.getClassTotal() * 100;
                result.put("percentage", Math.round(percentage * 100.0) / 100.0);
            }
        }
        
        return result;
    }

    @Override
    public Map<String, Object> getMajorRanking(Long studentId, Long majorId, String semester) {
        BenchmarkAnalysis benchmark = benchmarkAnalysisMapper.getAnalysisBySemester(studentId, semester);
        Map<String, Object> result = new HashMap<>();
        
        if (benchmark != null) {
            result.put("rank", benchmark.getMajorRank());
            result.put("total", benchmark.getMajorTotal());
            result.put("gpa", benchmark.getStudentGpa());
            result.put("majorAvgGpa", benchmark.getMajorAvgGpa());
            
            // 计算百分比排名
            if (benchmark.getMajorTotal() != null && benchmark.getMajorTotal() > 0) {
                double percentage = (double) benchmark.getMajorRank() / benchmark.getMajorTotal() * 100;
                result.put("percentage", Math.round(percentage * 100.0) / 100.0);
            }
        }
        
        return result;
    }

    @Override
    public List<BenchmarkAnalysis> getClassBenchmarkComparison(Long classId, String semester) {
        return benchmarkAnalysisMapper.getClassAnalysis(classId, semester);
    }

    @Override
    public List<BenchmarkAnalysis> getMajorBenchmarkComparison(Long majorId, String semester) {
        return benchmarkAnalysisMapper.getMajorAnalysis(majorId, semester);
    }

    @Override
    public Map<String, Object> getProgressReport(Long studentId) {
        Map<String, Object> report = new HashMap<>();
        
        // 获取学生信息
        StudentProfile student = studentProfileMapper.selectById(studentId);
        if (student == null) {
            return report;
        }
        
        report.put("studentId", studentId);
        report.put("classId", student.getClassId());
        report.put("majorId", student.getMajorId());
        
        // 获取最新学期的对标分析
        BenchmarkAnalysis latest = benchmarkAnalysisMapper.getLatestAnalysis(studentId);
        if (latest != null) {
            report.put("currentSemester", latest.getSemester());
            report.put("currentGpa", latest.getStudentGpa());
            report.put("classAvgGpa", latest.getClassAvgGpa());
            report.put("majorAvgGpa", latest.getMajorAvgGpa());
            report.put("classRank", latest.getClassRank());
            report.put("classTotal", latest.getClassTotal());
            report.put("majorRank", latest.getMajorRank());
            report.put("majorTotal", latest.getMajorTotal());
            report.put("coursesPassed", latest.getCoursesPassed());
            report.put("coursesFailed", latest.getCoursesFailed());
            report.put("creditsOnTrack", latest.getCreditsOnTrack() == 1);
            
            // 计算GPA增长趋势
            List<BenchmarkAnalysis> history = benchmarkAnalysisMapper.getHistoryAnalysis(studentId);
            if (history.size() > 1) {
                BenchmarkAnalysis previous = history.get(1); // 上一学期
                if (previous.getStudentGpa() != null && latest.getStudentGpa() != null) {
                    BigDecimal gpaTrend = latest.getStudentGpa().subtract(previous.getStudentGpa());
                    report.put("gpaTrend", gpaTrend);
                    report.put("gpaTrendPercent", gpaTrend.doubleValue() / previous.getStudentGpa().doubleValue() * 100);
                }
            }
            
            // 对标分析汇总
            Map<String, Object> benchmarkSummary = new HashMap<>();
            benchmarkSummary.put("gpaDifference", latest.getStudentGpa().subtract(latest.getClassAvgGpa()));
            benchmarkSummary.put("isAboveClassAvg", latest.getStudentGpa().compareTo(latest.getClassAvgGpa()) > 0);
            benchmarkSummary.put("isAboveMajorAvg", latest.getStudentGpa().compareTo(latest.getMajorAvgGpa()) > 0);
            benchmarkSummary.put("classRankPercentage", latest.getClassTotal() > 0 ? 
                (double) latest.getClassRank() / latest.getClassTotal() * 100 : 0);
            benchmarkSummary.put("majorRankPercentage", latest.getMajorTotal() > 0 ? 
                (double) latest.getMajorRank() / latest.getMajorTotal() * 100 : 0);
            
            report.put("benchmarkSummary", benchmarkSummary);
        }
        
        // 历史趋势数据
        List<BenchmarkAnalysis> history = benchmarkAnalysisMapper.getHistoryAnalysis(studentId);
        List<Map<String, Object>> trendData = history.stream()
            .map(b -> {
                Map<String, Object> trend = new HashMap<>();
                trend.put("semester", b.getSemester());
                trend.put("gpa", b.getStudentGpa());
                trend.put("classAvgGpa", b.getClassAvgGpa());
                trend.put("majorAvgGpa", b.getMajorAvgGpa());
                trend.put("classRank", b.getClassRank());
                return trend;
            })
            .collect(Collectors.toList());
        report.put("trendData", trendData);
        
        return report;
    }

    @Override
    public boolean updateBenchmark(BenchmarkAnalysis benchmark) {
        if (benchmark.getId() != null) {
            // 更新现有记录
            return benchmarkAnalysisMapper.updateById(benchmark) > 0;
        } else {
            // 插入新记录
            return benchmarkAnalysisMapper.insert(benchmark) > 0;
        }
    }

    @Override
    public boolean calculateClassBenchmark(Long classId, String semester) {
        // 获取班级内所有学生
        List<BenchmarkAnalysis> classAnalysis = benchmarkAnalysisMapper.getClassAnalysis(classId, semester);
        
        if (classAnalysis.isEmpty()) {
            return false;
        }
        
        // 计算班级平均GPA
        Double classAvgGpa = benchmarkAnalysisMapper.getClassAvgGpa(classId, semester);
        if (classAvgGpa == null) {
            return false;
        }
        
        // 按GPA排序，计算排名
        classAnalysis.sort((a, b) -> b.getStudentGpa().compareTo(a.getStudentGpa()));
        
        for (int i = 0; i < classAnalysis.size(); i++) {
            BenchmarkAnalysis benchmark = classAnalysis.get(i);
            benchmark.setClassAvgGpa(BigDecimal.valueOf(classAvgGpa));
            benchmark.setClassRank(i + 1);
            benchmark.setClassTotal(classAnalysis.size());
            benchmarkAnalysisMapper.updateById(benchmark);
        }
        
        return true;
    }

    @Override
    public boolean calculateMajorBenchmark(Long majorId, String semester) {
        // 获取专业内所有学生
        List<BenchmarkAnalysis> majorAnalysis = benchmarkAnalysisMapper.getMajorAnalysis(majorId, semester);
        
        if (majorAnalysis.isEmpty()) {
            return false;
        }
        
        // 计算专业平均GPA
        Double majorAvgGpa = benchmarkAnalysisMapper.getMajorAvgGpa(majorId, semester);
        if (majorAvgGpa == null) {
            return false;
        }
        
        // 按GPA排序，计算排名
        majorAnalysis.sort((a, b) -> b.getStudentGpa().compareTo(a.getStudentGpa()));
        
        for (int i = 0; i < majorAnalysis.size(); i++) {
            BenchmarkAnalysis benchmark = majorAnalysis.get(i);
            benchmark.setMajorAvgGpa(BigDecimal.valueOf(majorAvgGpa));
            benchmark.setMajorRank(i + 1);
            benchmark.setMajorTotal(majorAnalysis.size());
            benchmarkAnalysisMapper.updateById(benchmark);
        }
        
        return true;
    }
}
