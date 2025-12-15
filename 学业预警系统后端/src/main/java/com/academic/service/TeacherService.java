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
}
