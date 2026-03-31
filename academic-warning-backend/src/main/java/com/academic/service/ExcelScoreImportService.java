package com.academic.service;

import com.academic.dto.ScoreImportRequest;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

/**
 * Excel 成绩导入服务
 */
public interface ExcelScoreImportService {
    
    /**
     * 从 Excel 文件解析成绩数据
     * @param file Excel 文件
     * @return 成绩导入项列表
     */
    List<ScoreImportRequest.ScoreImportItem> parseExcelFile(MultipartFile file);
    
    /**
     * 导入成绩数据
     * @param importRequest 导入请求
     * @return 导入结果统计
     */
    Map<String, Object> importScores(ScoreImportRequest importRequest);
    
    /**
     * 按课程ID导入成绩数据
     * @param courseId 课程ID
     * @param items 成绩项列表
     * @return 导入结果统计
     */
    Map<String, Object> importScoresByCourseId(Long courseId, List<ScoreImportRequest.ScoreImportItem> items);
    
    /**
     * 按课程ID导入成绩数据（指定学期）
     * @param courseId 课程ID
     * @param items 成绩项列表
     * @param semester 学期
     * @return 导入结果统计
     */
    Map<String, Object> importScoresByCourseId(Long courseId, List<ScoreImportRequest.ScoreImportItem> items, String semester);
}
