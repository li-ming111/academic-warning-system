package com.academic.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 成绩导入请求
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ScoreImportRequest {
    
    /**
     * 课程ID
     */
    private Long courseId;
    
    /**
     * 班级ID
     */
    private Long classId;
    
    /**
     * 学期
     */
    private String semester;
    
    /**
     * 成绩导入项列表
     */
    private List<ScoreImportItem> items;
    
    /**
     * 成绩导入项
     */
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ScoreImportItem {
        /**
         * 学号
         */
        private String studentId;
        
        /**
         * 学生姓名
         */
        private String studentName;
        
        /**
         * 课程名称
         */
        private String courseName;
        
        /**
         * 成绩
         */
        private Double score;
        
        /**
         * 学分
         */
        private Double credits;
        
        /**
         * 绩点（可选，如果不提供会自动计算）
         */
        private Double gradePoint;
    }
}
