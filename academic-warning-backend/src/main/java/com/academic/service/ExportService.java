package com.academic.service;

import com.academic.entity.Score;
import java.io.ByteArrayOutputStream;
import java.util.List;

public interface ExportService {

    /**
     * 导出成绩为Excel
     */
    ByteArrayOutputStream exportScoresToExcel(Long studentId, List<Score> scores, String studentName) throws Exception;

    /**
     * 导出成绩为PDF
     */
    ByteArrayOutputStream exportScoresToPDF(Long studentId, List<Score> scores, String studentName) throws Exception;

    /**
     * 导出成绩单（包含统计信息）
     */
    ByteArrayOutputStream exportTranscript(Long studentId, String studentName, java.math.BigDecimal gpa) throws Exception;

}
