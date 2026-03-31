package com.academic.service.impl;

import com.academic.mapper.CourseMapper;
import com.academic.service.ExportService;
import com.academic.entity.Score;
import com.academic.entity.Course;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class ExportServiceImpl implements ExportService {

    private final CourseMapper courseMapper;

    @Override
    public ByteArrayOutputStream exportScoresToExcel(Long studentId, List<Score> scores, String studentName) throws Exception {
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("成绩信息");

        // 设置列宽
        sheet.setColumnWidth(0, 25 * 256);
        sheet.setColumnWidth(1, 20 * 256);
        sheet.setColumnWidth(2, 15 * 256);
        sheet.setColumnWidth(3, 15 * 256);
        sheet.setColumnWidth(4, 15 * 256);

        // 创建标题行
        Row titleRow = sheet.createRow(0);
        titleRow.createCell(0).setCellValue("学生成绩表");
        titleRow.createCell(1).setCellValue("学生: " + studentName);

        // 创建表头行
        Row headerRow = sheet.createRow(2);
        String[] headers = {"课程名称", "学期", "成绩", "绩点", "学分"};
        for (int i = 0; i < headers.length; i++) {
            headerRow.createCell(i).setCellValue(headers[i]);
        }

        // 填充数据行
        int rowNum = 3;
        for (Score score : scores) {
            Row dataRow = sheet.createRow(rowNum++);
            
            // 获取课程信息
            Course course = courseMapper.selectById(score.getCourseId());
            String courseName = course != null ? course.getName() : "未知课程";
            
            dataRow.createCell(0).setCellValue(courseName);
            dataRow.createCell(1).setCellValue(score.getSemester());
            dataRow.createCell(2).setCellValue(score.getScoreTotal().doubleValue());
            dataRow.createCell(3).setCellValue(score.getGradePoint() != null ? score.getGradePoint().doubleValue() : 0);
            dataRow.createCell(4).setCellValue(course != null ? course.getCredits().doubleValue() : 0);
        }

        // 写入输出流
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        workbook.write(outputStream);
        workbook.close();

        return outputStream;
    }

    @Override
    public ByteArrayOutputStream exportScoresToPDF(Long studentId, List<Score> scores, String studentName) throws Exception {
        // 导出为Excel（简化实现，可后续使用iText增强）
        return exportScoresToExcel(studentId, scores, studentName);
    }

    @Override
    public ByteArrayOutputStream exportTranscript(Long studentId, String studentName, BigDecimal gpa) throws Exception {
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("成绩单");

        // 设置列宽
        sheet.setColumnWidth(0, 20 * 256);
        sheet.setColumnWidth(1, 40 * 256);

        // 创建标题
        Row row1 = sheet.createRow(0);
        row1.createCell(0).setCellValue("学生成绩单");

        // 学生信息
        int rowNum = 2;
        sheet.createRow(rowNum++).createCell(0).setCellValue("学生姓名: " + studentName);
        sheet.createRow(rowNum++).createCell(0).setCellValue("学号: " + studentId);
        sheet.createRow(rowNum++).createCell(0).setCellValue("GPA: " + (gpa != null ? gpa.toString() : "0.00"));
        sheet.createRow(rowNum++).createCell(0).setCellValue("生成时间: " + LocalDateTime.now());

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        workbook.write(outputStream);
        workbook.close();

        return outputStream;
    }

}
