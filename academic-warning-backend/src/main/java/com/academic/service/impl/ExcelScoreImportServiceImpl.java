package com.academic.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.academic.dto.ScoreImportRequest;
import com.academic.entity.Score;
import com.academic.entity.Course;
import com.academic.entity.StudentProfile;
import com.academic.entity.User;
import com.academic.entity.Class;
import com.academic.mapper.CourseMapper;
import com.academic.mapper.StudentProfileMapper;
import com.academic.mapper.UserMapper;
import com.academic.mapper.ClassMapper;
import com.academic.service.ExcelScoreImportService;
import com.academic.service.ScoreService;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

/**
 * Excel 成绩导入服务实现
 */
@Slf4j
@Service
public class ExcelScoreImportServiceImpl implements ExcelScoreImportService {

    private final ScoreService scoreService;
    private final CourseMapper courseMapper;
    private final StudentProfileMapper studentProfileMapper;
    private final UserMapper userMapper;
    private final ClassMapper classMapper;

    public ExcelScoreImportServiceImpl(ScoreService scoreService,
                                      CourseMapper courseMapper,
                                      StudentProfileMapper studentProfileMapper,
                                      UserMapper userMapper,
                                      ClassMapper classMapper) {
        this.scoreService = scoreService;
        this.courseMapper = courseMapper;
        this.studentProfileMapper = studentProfileMapper;
        this.userMapper = userMapper;
        this.classMapper = classMapper;
    }

    @Override
    public List<ScoreImportRequest.ScoreImportItem> parseExcelFile(MultipartFile file) {
        List<ScoreImportRequest.ScoreImportItem> items = new ArrayList<>();
        
        try (InputStream inputStream = file.getInputStream();
             Workbook workbook = WorkbookFactory.create(inputStream)) {
            
            Sheet sheet = workbook.getSheetAt(0);
            
            // 自动检测标题行和数据行
            int headerRowIndex = findHeaderRow(sheet);
            if (headerRowIndex == -1) {
                throw new RuntimeException("无法找到标题行，请检查Excel文件格式");
            }
            
            // 自动检测列索引
            Map<String, Integer> columnMap = detectColumns(sheet.getRow(headerRowIndex));
            
            // 从标题行的下一行开始解析数据
            for (int i = headerRowIndex + 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row == null) continue;
                
                try {
                    ScoreImportRequest.ScoreImportItem item = new ScoreImportRequest.ScoreImportItem();
                    
                    // 根据检测到的列索引获取数据
                    String studentId = getStringCellValue(row.getCell(columnMap.getOrDefault("学号", 1)));
                    String studentName = getStringCellValue(row.getCell(columnMap.getOrDefault("姓名", 2)));
                    double regularScore = getNumericCellValue(row.getCell(columnMap.getOrDefault("平时成绩", 3)));
                    double finalScore = getNumericCellValue(row.getCell(columnMap.getOrDefault("期末成绩", 4)));
                    double totalScore = getNumericCellValue(row.getCell(columnMap.getOrDefault("综合成绩", 5)));
                    double credits = getNumericCellValue(row.getCell(columnMap.getOrDefault("学分", 6)));
                    double gradePoint = getNumericCellValue(row.getCell(columnMap.getOrDefault("绩点", 7)));
                    
                    // 验证必要字段
                    if (studentId == null || studentId.isEmpty() ||
                        studentName == null || studentName.isEmpty() ||
                        totalScore < 0) {
                        log.warn("第 {} 行数据不完整，跳过", i + 1);
                        continue;
                    }
                    
                    item.setStudentId(studentId);
                    item.setStudentName(studentName);
                    item.setCourseName(getCourseNameFromHeader(sheet)); // 从表头提取课程名称
                    item.setScore(totalScore);
                    item.setCredits(credits > 0 ? credits : 4.0); // 默认4学分
                    item.setGradePoint(gradePoint > 0 ? gradePoint : calculateGradePoint(totalScore));
                    
                    items.add(item);
                } catch (Exception e) {
                    log.warn("第 {} 行解析失败: {}", i + 1, e.getMessage());
                }
            }
            
            log.info("Excel 文件解析完成，共解析 {} 条成绩记录", items.size());
        } catch (Exception e) {
            log.error("解析 Excel 文件失败", e);
            throw new RuntimeException("解析 Excel 文件失败: " + e.getMessage());
        }
        
        return items;
    }
    
    /**
     * 查找标题行
     */
    private int findHeaderRow(Sheet sheet) {
        for (int i = 0; i <= Math.min(10, sheet.getLastRowNum()); i++) {
            Row row = sheet.getRow(i);
            if (row == null) continue;
            
            for (int j = 0; j < row.getLastCellNum(); j++) {
                Cell cell = row.getCell(j);
                if (cell == null) continue;
                
                String value = getStringCellValue(cell);
                if (value != null && (value.contains("学号") || value.contains("姓名") || value.contains("成绩"))) {
                    return i;
                }
            }
        }
        return -1;
    }
    
    /**
     * 检测列索引
     */
    private Map<String, Integer> detectColumns(Row headerRow) {
        Map<String, Integer> columnMap = new HashMap<>();
        
        for (int i = 0; i < headerRow.getLastCellNum(); i++) {
            Cell cell = headerRow.getCell(i);
            if (cell == null) continue;
            
            String value = getStringCellValue(cell);
            if (value == null) continue;
            
            if (value.contains("学号")) {
                columnMap.put("学号", i);
            } else if (value.contains("姓名")) {
                columnMap.put("姓名", i);
            } else if (value.contains("平时")) {
                columnMap.put("平时成绩", i);
            } else if (value.contains("期末")) {
                columnMap.put("期末成绩", i);
            } else if (value.contains("综合") || value.contains("总分")) {
                columnMap.put("综合成绩", i);
            } else if (value.contains("学分")) {
                columnMap.put("学分", i);
            } else if (value.contains("绩点")) {
                columnMap.put("绩点", i);
            }
        }
        
        return columnMap;
    }
    
    /**
     * 从表头提取课程名称
     */
    private String getCourseNameFromHeader(Sheet sheet) {
        // 尝试从标题行上方的单元格获取课程名称
        for (int i = 0; i < Math.min(5, sheet.getLastRowNum()); i++) {
            Row row = sheet.getRow(i);
            if (row == null) continue;
            
            for (int j = 0; j < row.getLastCellNum(); j++) {
                Cell cell = row.getCell(j);
                if (cell == null) continue;
                
                String value = getStringCellValue(cell);
                if (value != null && value.length() > 2 && !value.contains("学号") && !value.contains("姓名")) {
                    return value;
                }
            }
        }
        return "未知课程";
    }

    @Override
    @Transactional
    public Map<String, Object> importScores(ScoreImportRequest importRequest) {
        Map<String, Object> result = new HashMap<>();
        int successCount = 0;
        int failureCount = 0;
        List<String> errorMessages = new ArrayList<>();
        
        String semester = importRequest.getSemester();
        Long classId = importRequest.getClassId();
        
        // 班级信息
        Long collegeId = 1L; // 默认学院
        Long majorId = 1L; // 默认专业
        Long counselorId = 1L; // 默认辅导员
        
        // 如果提供了classId，使用班级的学院、专业和辅导员信息
        if (classId != null) {
            Class clazz = classMapper.selectById(classId);
            if (clazz != null) {
                collegeId = clazz.getCollegeId();
                majorId = clazz.getMajorId();
                counselorId = clazz.getCounselorId();
            }
        }
        
        // 批量处理：收集需要保存和更新的成绩
        List<Score> insertScores = new ArrayList<>();
        List<Score> updateScores = new ArrayList<>();
        
        for (ScoreImportRequest.ScoreImportItem item : importRequest.getItems()) {
            try {
                // 1. 查找学生
                QueryWrapper<StudentProfile> studentQuery = new QueryWrapper<>();
                studentQuery.eq("student_id", item.getStudentId());
                StudentProfile student = studentProfileMapper.selectOne(studentQuery);
                
                if (student == null) {
                    // 自动创建学生
                    log.info("创建新学生: 学号={}, 姓名={}", item.getStudentId(), item.getStudentName());
                    
                    // 创建用户
                    User user = new User();
                    user.setUsername(item.getStudentId());
                    user.setPassword("123456"); // 默认密码
                    user.setName(item.getStudentName());
                    user.setRole(1); // 学生角色
                    user.setStatus(1); // 启用状态
                    userMapper.insert(user);
                    
                    // 创建学生档案
                    student = new StudentProfile();
                    student.setUserId(user.getId());
                    student.setStudentId(item.getStudentId());
                    student.setName(item.getStudentName());
                    student.setCollegeId(collegeId);
                    student.setMajorId(majorId);
                    student.setClassId(classId);
                    studentProfileMapper.insert(student);
                    
                    log.info("学生创建成功: 学号={}, 姓名={}", item.getStudentId(), item.getStudentName());
                }
                
                // 2. 查找或创建课程
                QueryWrapper<Course> courseQuery = new QueryWrapper<>();
                courseQuery.like("name", item.getCourseName());
                Course course = courseMapper.selectOne(courseQuery);
                
                if (course == null) {
                    // 创建新课程
                    course = new Course();
                    course.setName(item.getCourseName());
                    course.setCredits(new BigDecimal(item.getCredits() != null ? item.getCredits() : 0));
                    course.setType("elective");
                    courseMapper.insert(course);
                    log.info("创建新课程: {}", item.getCourseName());
                }
                
                // 3. 计算绩点（如果没有提供）
                double gradePoint = item.getGradePoint() != null && item.getGradePoint() > 0 
                    ? item.getGradePoint() 
                    : calculateGradePoint(item.getScore());
                
                // 4. 保存或更新成绩
                QueryWrapper<Score> scoreQuery = new QueryWrapper<>();
                scoreQuery.eq("student_id", student.getId())
                        .eq("course_id", course.getId())
                        .eq("semester", semester);
                
                Score existingScore = scoreService.getOne(scoreQuery);
                
                if (existingScore != null) {
                    // 更新现有成绩
                    existingScore.setScoreTotal(new BigDecimal(item.getScore()));
                    existingScore.setGradePoint(new BigDecimal(gradePoint));
                    // 简单计算平时分和期末分：平时分=总分*0.3，期末分=总分*0.7
                    existingScore.setRegularScore(new BigDecimal(item.getScore() * 0.3));
                    existingScore.setFinalScore(new BigDecimal(item.getScore() * 0.7));
                    existingScore.setUpdatedAt(LocalDateTime.now());
                    updateScores.add(existingScore);
                } else {
                    // 创建新成绩记录
                    Score score = new Score();
                    score.setStudentId(student.getId());
                    score.setCourseId(course.getId());
                    score.setSemester(semester);
                    // 简单计算平时分和期末分：平时分=总分*0.3，期末分=总分*0.7
                    score.setRegularScore(new BigDecimal(item.getScore() * 0.3));
                    score.setFinalScore(new BigDecimal(item.getScore() * 0.7));
                    score.setScoreTotal(new BigDecimal(item.getScore()));
                    score.setGradePoint(new BigDecimal(gradePoint));
                    score.setCreatedAt(LocalDateTime.now());
                    score.setUpdatedAt(LocalDateTime.now());
                    insertScores.add(score);
                }
                
                successCount++;
                log.info("导入成绩: 学号={}, 课程={}, 成绩={}, 绩点={}", 
                    item.getStudentId(), item.getCourseName(), item.getScore(), gradePoint);
                
            } catch (Exception e) {
                failureCount++;
                errorMessages.add("学号 " + item.getStudentId() + " 导入失败: " + e.getMessage());
                log.error("导入成绩失败", e);
            }
        }
        
        // 批量执行保存和更新操作
        if (!insertScores.isEmpty()) {
            scoreService.saveBatch(insertScores);
            log.info("批量插入成绩 {} 条", insertScores.size());
        }
        if (!updateScores.isEmpty()) {
            scoreService.updateBatchById(updateScores);
            log.info("批量更新成绩 {} 条", updateScores.size());
        }
        
        result.put("totalCount", importRequest.getItems().size());
        result.put("successCount", successCount);
        result.put("failureCount", failureCount);
        result.put("errors", errorMessages);
        result.put("message", String.format("导入完成: 成功 %d 条，失败 %d 条", successCount, failureCount));
        
        log.info("成绩导入统计 - 总计: {}, 成功: {}, 失败: {}", 
            importRequest.getItems().size(), successCount, failureCount);
        
        return result;
    }

    /**
     * 根据百分制成绩计算绩点（四分制）
     */
    private double calculateGradePoint(double score) {
        if (score >= 90) {
            return 4.0;
        } else if (score >= 85) {
            return 3.7;
        } else if (score >= 80) {
            return 3.3;
        } else if (score >= 75) {
            return 3.0;
        } else if (score >= 70) {
            return 2.7;
        } else if (score >= 65) {
            return 2.3;
        } else if (score >= 60) {
            return 2.0;
        } else {
            return 0;
        }
    }

    /**
     * 获取字符串单元格值
     */
    private String getStringCellValue(Cell cell) {
        if (cell == null) return null;
        
        switch (cell.getCellType()) {
            case STRING:
                return cell.getStringCellValue().trim();
            case NUMERIC:
                return String.valueOf((int) cell.getNumericCellValue());
            default:
                return null;
        }
    }

    /**
     * 获取数值单元格值
     */
    private double getNumericCellValue(Cell cell) {
        if (cell == null) return 0;
        
        try {
            return cell.getNumericCellValue();
        } catch (Exception e) {
            return 0;
        }
    }
    
    @Override
    @Transactional
    public Map<String, Object> importScoresByCourseId(Long courseId, List<ScoreImportRequest.ScoreImportItem> items) {
        // 使用默认学期
        return importScoresByCourseId(courseId, items, "2025-2026-1");
    }
    
    @Override
    @Transactional
    public Map<String, Object> importScoresByCourseId(Long courseId, List<ScoreImportRequest.ScoreImportItem> items, String semester) {
        Map<String, Object> result = new HashMap<>();
        int successCount = 0;
        int failureCount = 0;
        List<String> errorMessages = new ArrayList<>();
        
        // 查找课程
        Course course = courseMapper.selectById(courseId);
        if (course == null) {
            result.put("totalCount", items.size());
            result.put("successCount", 0);
            result.put("failureCount", items.size());
            result.put("errorMessages", Collections.singletonList("课程不存在"));
            return result;
        }
        
        // 班级信息
        Long collegeId = 1L; // 默认学院
        Long majorId = 1L; // 默认专业
        Long counselorId = 1L; // 默认辅导员
        
        // 批量处理：收集需要保存和更新的成绩
        List<Score> insertScores = new ArrayList<>();
        List<Score> updateScores = new ArrayList<>();
        
        // 使用传入的学期参数
        
        for (ScoreImportRequest.ScoreImportItem item : items) {
            try {
                // 1. 查找学生
                QueryWrapper<StudentProfile> studentQuery = new QueryWrapper<>();
                studentQuery.eq("student_id", item.getStudentId());
                StudentProfile student = studentProfileMapper.selectOne(studentQuery);
                
                if (student == null) {
                    // 自动创建学生
                    log.info("创建新学生: 学号={}, 姓名={}", item.getStudentId(), item.getStudentName());
                    
                    // 创建用户
                    User user = new User();
                    user.setUsername(item.getStudentId());
                    user.setPassword("123456"); // 默认密码
                    user.setName(item.getStudentName());
                    user.setRole(1); // 学生角色
                    user.setStatus(1); // 启用状态
                    userMapper.insert(user);
                    
                    // 创建学生档案
                    student = new StudentProfile();
                    student.setUserId(user.getId());
                    student.setStudentId(item.getStudentId());
                    student.setName(item.getStudentName());
                    student.setCollegeId(collegeId);
                    student.setMajorId(majorId);
                    studentProfileMapper.insert(student);
                    
                    log.info("学生创建成功: 学号={}, 姓名={}", item.getStudentId(), item.getStudentName());
                }
                
                // 2. 计算绩点
                double gradePoint = item.getGradePoint() != null && item.getGradePoint() > 0 
                    ? item.getGradePoint() 
                    : calculateGradePoint(item.getScore());
                
                // 3. 保存或更新成绩
                QueryWrapper<Score> scoreQuery = new QueryWrapper<>();
                scoreQuery.eq("student_id", student.getId())
                        .eq("course_id", courseId)
                        .eq("semester", semester);
                
                Score existingScore = scoreService.getOne(scoreQuery);
                
                if (existingScore != null) {
                    // 更新现有成绩
                    existingScore.setScoreTotal(new BigDecimal(item.getScore()));
                    existingScore.setGradePoint(new BigDecimal(gradePoint));
                    // 简单计算平时分和期末分：平时分=总分*0.3，期末分=总分*0.7
                    existingScore.setRegularScore(new BigDecimal(item.getScore() * 0.3));
                    existingScore.setFinalScore(new BigDecimal(item.getScore() * 0.7));
                    existingScore.setUpdatedAt(LocalDateTime.now());
                    updateScores.add(existingScore);
                } else {
                    // 创建新成绩记录
                    Score score = new Score();
                    score.setStudentId(student.getId());
                    score.setCourseId(courseId);
                    score.setSemester(semester);
                    // 简单计算平时分和期末分：平时分=总分*0.3，期末分=总分*0.7
                    score.setRegularScore(new BigDecimal(item.getScore() * 0.3));
                    score.setFinalScore(new BigDecimal(item.getScore() * 0.7));
                    score.setScoreTotal(new BigDecimal(item.getScore()));
                    score.setGradePoint(new BigDecimal(gradePoint));
                    score.setCreatedAt(LocalDateTime.now());
                    score.setUpdatedAt(LocalDateTime.now());
                    insertScores.add(score);
                }
                
                successCount++;
                log.info("导入成绩: 学号={}, 课程={}, 成绩={}, 绩点={}", 
                    item.getStudentId(), course.getName(), item.getScore(), gradePoint);
                
            } catch (Exception e) {
                failureCount++;
                errorMessages.add("学号 " + item.getStudentId() + " 导入失败: " + e.getMessage());
                log.error("导入成绩失败", e);
            }
        }
        
        // 批量执行保存和更新操作
        if (!insertScores.isEmpty()) {
            scoreService.saveBatch(insertScores);
            log.info("批量插入成绩 {} 条", insertScores.size());
        }
        if (!updateScores.isEmpty()) {
            scoreService.updateBatchById(updateScores);
            log.info("批量更新成绩 {} 条", updateScores.size());
        }
        
        result.put("totalCount", items.size());
        result.put("successCount", successCount);
        result.put("failureCount", failureCount);
        result.put("errorMessages", errorMessages);
        return result;
    }
}
