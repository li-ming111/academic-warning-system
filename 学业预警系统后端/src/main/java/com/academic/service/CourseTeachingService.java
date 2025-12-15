package com.academic.service;

import java.util.List;
import java.util.Map;

/**
 * 课程教学分配服务
 */
public interface CourseTeachingService {

    /**
     * 分配教师到课程
     */
    void assignTeacherToCourse(Long courseId, Long teacherId);

    /**
     * 获取教师的课程列表
     */
    List<Map<String, Object>> getTeacherCourses(Long teacherId);

    /**
     * 获取课程的授课教师
     */
    List<Map<String, Object>> getCourseteachers(Long courseId);

    /**
     * 取消教师与课程的关联
     */
    void removeTeacherFromCourse(Long courseId, Long teacherId);

    /**
     * 获取课程的学生选修列表
     */
    List<Map<String, Object>> getCourseStudents(Long courseId);

    /**
     * 统计教师的教学工作量
     */
    Map<String, Object> getTeacherWorkload(Long teacherId);
}
