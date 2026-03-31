package com.academic.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.academic.entity.BenchmarkAnalysis;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

/**
 * 对标分析数据访问接口
 */
@Mapper
public interface BenchmarkAnalysisMapper extends BaseMapper<BenchmarkAnalysis> {

    /**
     * 获取学生最新学期的对标分析数据
     *
     * @param studentId 学生ID
     * @return 对标分析对象
     */
    BenchmarkAnalysis getLatestAnalysis(@Param("studentId") Long studentId);

    /**
     * 获取学生指定学期的对标分析数据
     *
     * @param studentId 学生ID
     * @param semester  学期
     * @return 对标分析对象
     */
    BenchmarkAnalysis getAnalysisBySemester(@Param("studentId") Long studentId, @Param("semester") String semester);

    /**
     * 获取班级内所有学生的对标分析数据（指定学期）
     *
     * @param classId  班级ID
     * @param semester 学期
     * @return 对标分析列表
     */
    List<BenchmarkAnalysis> getClassAnalysis(@Param("classId") Long classId, @Param("semester") String semester);

    /**
     * 获取专业内所有学生的对标分析数据（指定学期）
     *
     * @param majorId  专业ID
     * @param semester 学期
     * @return 对标分析列表
     */
    List<BenchmarkAnalysis> getMajorAnalysis(@Param("majorId") Long majorId, @Param("semester") String semester);

    /**
     * 获取学生的历史对标分析数据
     *
     * @param studentId 学生ID
     * @return 对标分析列表，按学期降序排列
     */
    List<BenchmarkAnalysis> getHistoryAnalysis(@Param("studentId") Long studentId);

    /**
     * 获取班级平均GPA（指定学期）
     *
     * @param classId  班级ID
     * @param semester 学期
     * @return 班级平均GPA
     */
    Double getClassAvgGpa(@Param("classId") Long classId, @Param("semester") String semester);

    /**
     * 获取专业平均GPA（指定学期）
     *
     * @param majorId  专业ID
     * @param semester 学期
     * @return 专业平均GPA
     */
    Double getMajorAvgGpa(@Param("majorId") Long majorId, @Param("semester") String semester);
}
