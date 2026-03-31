package com.academic.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 对标分析实体类
 * 用于存储学生与班级和专业同级学生的成绩对标分析数据
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName("benchmark_analysis")
public class BenchmarkAnalysis extends BaseEntity {

    /**
     * 学生ID
     */
    private Long studentId;

    /**
     * 班级ID
     */
    private Long classId;

    /**
     * 专业ID
     */
    private Long majorId;

    /**
     * 学期（如2023-2024秋）
     */
    private String semester;

    /**
     * 学生GPA
     */
    private BigDecimal studentGpa;

    /**
     * 班级平均GPA
     */
    private BigDecimal classAvgGpa;

    /**
     * 专业平均GPA
     */
    private BigDecimal majorAvgGpa;

    /**
     * 班级排名
     */
    private Integer classRank;

    /**
     * 班级总人数
     */
    private Integer classTotal;

    /**
     * 专业排名
     */
    private Integer majorRank;

    /**
     * 专业总人数
     */
    private Integer majorTotal;

    /**
     * 通过课程数
     */
    private Integer coursesPassed;

    /**
     * 不及格课程数
     */
    private Integer coursesFailed;

    /**
     * 修习学分
     */
    private BigDecimal requiredCredits;

    /**
     * 学分是否达标：1=达标，0=未达标
     */
    private Integer creditsOnTrack;
}
