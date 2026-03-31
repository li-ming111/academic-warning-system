package com.academic.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.academic.entity.ScoreAppeal;
import com.academic.mapper.ScoreAppealMapper;
import com.academic.service.StudentAppealService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 学生成绩申诉服务实现
 */
@Service
@RequiredArgsConstructor
public class StudentAppealServiceImpl implements StudentAppealService {

    private final ScoreAppealMapper scoreAppealMapper;

    @Override
    public boolean submitAppeal(ScoreAppeal appeal) {
        if (appeal == null || appeal.getStudentId() == null || appeal.getWarningId() == null) {
            return false;
        }
        appeal.setStatus("pending");
        appeal.setCreatedAt(LocalDateTime.now());
        appeal.setUpdatedAt(LocalDateTime.now());
        return scoreAppealMapper.insert(appeal) > 0;
    }

    @Override
    public List<ScoreAppeal> getStudentAppeals(Long studentId) {
        QueryWrapper<ScoreAppeal> qw = new QueryWrapper<>();
        qw.eq("student_id", studentId);
        qw.orderByDesc("created_at");
        return scoreAppealMapper.selectList(qw);
    }

    @Override
    public List<ScoreAppeal> getPendingAppeals(Long studentId) {
        QueryWrapper<ScoreAppeal> qw = new QueryWrapper<>();
        qw.eq("student_id", studentId);
        qw.eq("status", "pending");
        qw.orderByDesc("created_at");
        return scoreAppealMapper.selectList(qw);
    }

    @Override
    public ScoreAppeal getAppealDetail(Long appealId) {
        return scoreAppealMapper.selectById(appealId);
    }

    @Override
    public boolean withdrawAppeal(Long appealId) {
        ScoreAppeal appeal = scoreAppealMapper.selectById(appealId);
        if (appeal == null || !"pending".equals(appeal.getStatus())) {
            return false;
        }
        appeal.setStatus("withdrawn");
        appeal.setUpdatedAt(LocalDateTime.now());
        return scoreAppealMapper.updateById(appeal) > 0;
    }

    @Override
    public Map<String, Object> getAppealStatistics(Long studentId) {
        List<ScoreAppeal> appeals = getStudentAppeals(studentId);
        
        Map<String, Object> stats = new HashMap<>();
        stats.put("total", appeals.size());
        stats.put("pending", appeals.stream().filter(a -> "pending".equals(a.getStatus())).count());
        stats.put("approved", appeals.stream().filter(a -> "approved".equals(a.getStatus())).count());
        stats.put("rejected", appeals.stream().filter(a -> "rejected".equals(a.getStatus())).count());
        stats.put("withdrawn", appeals.stream().filter(a -> "withdrawn".equals(a.getStatus())).count());
        
        return stats;
    }

    @Override
    public Double getAppealSuccessRate(Long studentId) {
        List<ScoreAppeal> appeals = getStudentAppeals(studentId);
        
        if (appeals.isEmpty()) {
            return 0.0;
        }
        
        long approved = appeals.stream().filter(a -> "approved".equals(a.getStatus())).count();
        long decided = appeals.stream().filter(a -> "approved".equals(a.getStatus()) || "rejected".equals(a.getStatus())).count();
        
        if (decided == 0) {
            return 0.0;
        }
        
        return (approved * 100.0) / decided;
    }

    @Override
    public List<ScoreAppeal> getAppealsByStatus(Long studentId, String status) {
        QueryWrapper<ScoreAppeal> qw = new QueryWrapper<>();
        qw.eq("student_id", studentId);
        qw.eq("status", status);
        qw.orderByDesc("created_at");
        return scoreAppealMapper.selectList(qw);
    }

    @Override
    public Map<String, Object> getAppealHistory(Long studentId, int page, int pageSize) {
        Page<ScoreAppeal> pageObj = new Page<>(page, pageSize);
        QueryWrapper<ScoreAppeal> qw = new QueryWrapper<>();
        qw.eq("student_id", studentId);
        qw.orderByDesc("created_at");
        
        Page<ScoreAppeal> result = scoreAppealMapper.selectPage(pageObj, qw);
        
        Map<String, Object> response = new HashMap<>();
        response.put("total", result.getTotal());
        response.put("page", page);
        response.put("pageSize", pageSize);
        response.put("pages", result.getPages());
        response.put("data", result.getRecords());
        
        return response;
    }

    @Override
    public List<ScoreAppeal> getRecentAppeals(Long studentId, int limit) {
        QueryWrapper<ScoreAppeal> qw = new QueryWrapper<>();
        qw.eq("student_id", studentId);
        qw.orderByDesc("created_at");
        qw.last("LIMIT " + limit);
        return scoreAppealMapper.selectList(qw);
    }

    @Override
    public boolean updateAppealStatus(Long appealId, String status, String remark) {
        ScoreAppeal appeal = scoreAppealMapper.selectById(appealId);
        if (appeal == null) {
            return false;
        }
        appeal.setStatus(status);
        appeal.setUpdatedAt(LocalDateTime.now());
        return scoreAppealMapper.updateById(appeal) > 0;
    }

    @Override
    public Map<String, Long> getAppealReasonStatistics(Long studentId) {
        List<ScoreAppeal> appeals = getStudentAppeals(studentId);
        
        // 按申诉原因的关键词分类统计
        Map<String, Long> stats = new HashMap<>();
        
        for (ScoreAppeal appeal : appeals) {
            String reason = appeal.getReason();
            if (reason != null) {
                // 简单的关键词匹配分类
                if (reason.contains("评分") || reason.contains("评价")) {
                    stats.merge("成绩评分异议", 1L, Long::sum);
                } else if (reason.contains("计算") || reason.contains("统计")) {
                    stats.merge("分数计算错误", 1L, Long::sum);
                } else if (reason.contains("阅卷") || reason.contains("改卷")) {
                    stats.merge("阅卷相关", 1L, Long::sum);
                } else if (reason.contains("复查") || reason.contains("检查")) {
                    stats.merge("要求复查", 1L, Long::sum);
                } else {
                    stats.merge("其他", 1L, Long::sum);
                }
            }
        }
        
        return stats;
    }
}
