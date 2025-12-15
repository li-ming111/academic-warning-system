package com.academic.service;

import com.academic.entity.BenchmarkAnalysis;
import java.util.List;
import java.util.Map;

/**
 * 学生对标分析服务接口
 * 用于实现学生与班级平均水平、专业同级学生的对比分析功能
 */
public interface StudentBenchmarkService {

    /**
     * 获取学生最新学期的对标分析数据
     * 包含：学生GPA、班级平均GPA、专业平均GPA、班级排名、专业排名等
     *
     * @param studentId 学生ID
     * @return 对标分析对象
     */
    BenchmarkAnalysis getLatestBenchmark(Long studentId);

    /**
     * 获取学生指定学期的对标分析数据
     *
     * @param studentId 学生ID
     * @param semester  学期（如2023-2024秋）
     * @return 对标分析对象
     */
    BenchmarkAnalysis getBenchmarkBySemester(Long studentId, String semester);

    /**
     * 获取学生的历史对标分析数据（多个学期）
     *
     * @param studentId 学生ID
     * @return 对标分析列表，按学期降序排列
     */
    List<BenchmarkAnalysis> getHistoryBenchmark(Long studentId);

    /**
     * 获取班级内学生的排名信息
     *
     * @param studentId 学生ID
     * @param classId   班级ID
     * @param semester  学期
     * @return 包含学生排名信息的Map，如：{rank: 5, total: 30, percentage: 16.67}
     */
    Map<String, Object> getClassRanking(Long studentId, Long classId, String semester);

    /**
     * 获取专业内学生的排名信息
     *
     * @param studentId 学生ID
     * @param majorId   专业ID
     * @param semester  学期
     * @return 包含学生排名信息的Map，如：{rank: 42, total: 120, percentage: 35}
     */
    Map<String, Object> getMajorRanking(Long studentId, Long majorId, String semester);

    /**
     * 获取班级对标分析对比数据
     * 包含班级中所有学生的对标数据，便于可视化展示
     *
     * @param classId  班级ID
     * @param semester 学期
     * @return 对标分析列表
     */
    List<BenchmarkAnalysis> getClassBenchmarkComparison(Long classId, String semester);

    /**
     * 获取专业对标分析对比数据
     * 包含专业中所有学生的对标数据，便于可视化展示
     *
     * @param majorId  专业ID
     * @param semester 学期
     * @return 对标分析列表
     */
    List<BenchmarkAnalysis> getMajorBenchmarkComparison(Long majorId, String semester);

    /**
     * 获取学生的进度报告（综合对标分析）
     * 返回详细的对比信息、排名变化趋势等
     *
     * @param studentId 学生ID
     * @return 包含多个关键指标的Map
     */
    Map<String, Object> getProgressReport(Long studentId);

    /**
     * 更新学生的对标分析数据
     * 通常在学期末或评分导入时调用
     *
     * @param benchmark 对标分析对象
     * @return 更新是否成功
     */
    boolean updateBenchmark(BenchmarkAnalysis benchmark);

    /**
     * 计算班级中所有学生的排名和对标数据
     * 批量操作方法，用于定期更新对标分析
     *
     * @param classId  班级ID
     * @param semester 学期
     * @return 计算是否成功
     */
    boolean calculateClassBenchmark(Long classId, String semester);

    /**
     * 计算专业中所有学生的排名和对标数据
     * 批量操作方法，用于定期更新对标分析
     *
     * @param majorId  专业ID
     * @param semester 学期
     * @return 计算是否成功
     */
    boolean calculateMajorBenchmark(Long majorId, String semester);
}
