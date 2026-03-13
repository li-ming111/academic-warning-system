package com.academic.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.academic.entity.TeacherProfile;
import com.academic.entity.Score;
import com.academic.entity.ScoreLog;
import com.academic.entity.Feedback;
import com.academic.entity.Enrollment;
import com.academic.entity.CommunicationLog;
import com.academic.entity.AcademicWarning;
import com.academic.entity.User;
import com.academic.entity.Course;
import java.util.List;
import java.util.Map;

public interface TeacherService extends IService<TeacherProfile> {

    /**
     * 根据用户ID获取教师档案
     */
    TeacherProfile getByUserId(Long userId);

    /**
     * 教师注册
     */
    Long registerTeacher(User user, TeacherProfile profile);

    /**
     * 获取教师管理的学生人数
     */
    Integer getStudentCount(Long teacherId);

    /**
     * 获取教师班级的预警数量
     */
    Long getWarningCount(Long teacherId);

    /**
     * 获取教师班级特定等级的预警数
     */
    Long getWarningCountByLevel(Long teacherId, String level);

    /**
     * 查询教师班级的成绩
     */
    List<Score> getTeacherScores(Long teacherId, String courseId);

    /**
     * 保存或更新成绩
     */
    void saveOrUpdateScore(Score score);

    /**
     * 查询教师收到的反馈
     */
    List<Feedback> getTeacherFeedbacks(Long teacherId, String category);

    /**
     * 回复反馈
     */
    void replyFeedback(Long feedbackId, String replyContent);

    /**
     * 查询选修课信息
     */
    List<Enrollment> getEnrollments(Long teacherId, String courseId);

    /**
     * 保存沟通记录
     */
    void saveCommunicationLog(CommunicationLog log);

    /**
     * 查询教师班级的预警
     */
    List<AcademicWarning> getTeacherWarnings(Long teacherId, String status);

    /**
     * 处理预警
     */
    void processWarning(Long warningId, String measures);

    /**
     * 根据挂科数量为学生生成预警
     */
    void generateWarningsForStudent(Long studentId, String semester);

    /**
     * 获取教师的待办事项（未处理的预警、待回复的反馈等）
     */
    Map<String, Object> getTeacherTodos(Long teacherId);

    /**
     * 获取教师的课程列表
     */
    List<Course> getTeacherCourses(Long teacherId);

    /**
     * 获取课程成绩列表（包含学生信息）
     */
    List<Map<String, Object>> getCourseScores(Long courseId);
    
    /**
     * 为教师关联课程
     */
    void assignCourseToTeacher(Long teacherId, Long courseId);
    
    /**
     * 移除教师与课程的关联
     */
    void removeCourseFromTeacher(Long teacherId, Long courseId);
    
    /**
     * 获取教师的课程列表（通过teacher_course表查询）
     */
    List<Course> getTeacherCoursesByTeacherId(Long teacherId);

    /**
     * 获取学生成绩列表
     */
    List<Score> getScoresByStudentId(Long studentId);

    /**
     * 获取成绩修改日志
     */
    List<ScoreLog> getScoreLogs(Long teacherId);

    /**
     * 获取单个成绩
     */
    Score getScoreById(Long scoreId);
    
    /**
     * 导入学生信息
     */
    Map<String, Object> importStudents(Long classId, List<Map<String, Object>> studentData);
    
    /**
     * 获取课程成绩分析
     */
    Map<String, Object> getCourseScoreAnalysis(Long courseId);
    
    /**
     * 获取学生成绩分析
     */
    Map<String, Object> getStudentScoreAnalysis(Long studentId);
    
    /**
     * 获取班级成绩分析
     */
    Map<String, Object> getClassScoreAnalysis(Long classId);
    
    /**
     * 触发成绩预警分析
     */
    Map<String, Object> triggerScoreWarning(Long courseId, String semester);
    
    /**
     * 批量更新成绩
     */
    Map<String, Object> batchUpdateScores(List<Map<String, Object>> scoreUpdates);
    
    /**
     * 获取成绩导出数据
     */
    List<Map<String, Object>> getScoreExportData(Long courseId, String semester);
    
    /**
     * 预测学生成绩
     */
    Map<String, Object> predictStudentScore(Long studentId, Long courseId);
    
    /**
     * 检测异常成绩
     */
    List<Map<String, Object>> detectScoreAnomalies(Long courseId);
    
    /**
     * 生成学生评语
     */
    Map<Long, String> generateStudentComments(Long courseId);
    
    /**
     * 课程成绩分析
     */
    Map<String, Object> analyzeCourseScores(Long courseId);
    
    /**
     * 触发预警
     */
    int triggerScoreWarnings(Long courseId);
    
    /**
     * 学生成绩分析
     */
    Map<String, Object> analyzeStudentScore(Long studentId, Long courseId);
    
    /**
     * 删除成绩
     */
    void deleteScore(Long scoreId);
    
    /**
     * 批量删除成绩
     */
    void batchDeleteScores(List<Long> scoreIds);
}
