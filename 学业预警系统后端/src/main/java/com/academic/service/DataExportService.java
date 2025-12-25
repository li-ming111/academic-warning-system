package com.academic.service;

import com.academic.entity.ExportHistory;
import com.academic.mapper.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * 数据导出服务 - 通用导出逻辑
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class DataExportService {

    private final JdbcTemplate jdbcTemplate;
    private final ExportHistoryMapper exportHistoryMapper;

    /**
     * 导出学生数据
     */
    public List<Map<String, Object>> exportStudents() {
        String sql = "SELECT student_id AS '学号', name AS '姓名', college_id AS '学院', major_id AS '专业', class_id AS '班级', created_at AS '创建时间' FROM student_profile";
        try {
            return jdbcTemplate.queryForList(sql);
        } catch (Exception e) {
            log.error("导出学生数据失败", e);
            return List.of();
        }
    }

    /**
     * 导出成绩数据
     */
    public List<Map<String, Object>> exportScores() {
        String sql = "SELECT s.student_id AS '学号', sp.name AS '学生姓名', c.name AS '课程名称', s.semester AS '学期', " +
                     "s.score_total AS '成绩', s.grade_point AS '绩点', c.credits AS '学分', s.created_at AS '录入时间' " +
                     "FROM scores s " +
                     "LEFT JOIN student_profile sp ON s.student_id = sp.id " +
                     "LEFT JOIN courses c ON s.course_id = c.id";
        try {
            return jdbcTemplate.queryForList(sql);
        } catch (Exception e) {
            log.error("导出成绩数据失败", e);
            return List.of();
        }
    }

    /**
     * 导出预警数据
     */
    public List<Map<String, Object>> exportWarnings() {
        String sql = "SELECT sp.student_id AS '学号', sp.name AS '学生姓名', aw.warning_level AS '预警等级', " +
                     "aw.title AS '预警标题', aw.description AS '预警描述', aw.status AS '状态', aw.created_at AS '预警时间' " +
                     "FROM academic_warnings aw " +
                     "LEFT JOIN student_profile sp ON aw.student_id = sp.id";
        try {
            return jdbcTemplate.queryForList(sql);
        } catch (Exception e) {
            log.error("导出预警数据失败", e);
            return List.of();
        }
    }

    /**
     * 导出用户数据
     */
    public List<Map<String, Object>> exportUsers() {
        String sql = "SELECT id AS '用户ID', username AS '用户名', email AS '邮箱', role AS '角色', " +
                     "status AS '状态', created_at AS '创建时间', updated_at AS '更新时间', name AS '姓名' " +
                     "FROM users WHERE deleted_at IS NULL";
        try {
            return jdbcTemplate.queryForList(sql);
        } catch (Exception e) {
            log.error("导出用户数据失败", e);
            return List.of();
        }
    }

    /**
     * 记录导出历史
     */
    public void recordExport(String dataType, String fileName, Integer recordCount, Long exportedById) {
        try {
            ExportHistory history = new ExportHistory();
            history.setDataType(dataType);
            history.setFileName(fileName);
            history.setRecordCount(recordCount);
            history.setExportedBy(exportedById);
            history.setCreatedAt(LocalDateTime.now());
            exportHistoryMapper.insert(history);
        } catch (Exception e) {
            log.error("记录导出历史失败", e);
        }
    }

    /**
     * 获取导出历史
     */
    public List<ExportHistory> getExportHistory() {
        try {
            return exportHistoryMapper.selectList(null);
        } catch (Exception e) {
            log.error("获取导出历史失败", e);
            return List.of();
        }
    }

    /**
     * 删除导出历史
     */
    public void deleteExport(Long id) {
        try {
            exportHistoryMapper.deleteById(id);
        } catch (Exception e) {
            log.error("删除导出历史失败", e);
        }
    }
}
