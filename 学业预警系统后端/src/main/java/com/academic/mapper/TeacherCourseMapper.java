package com.academic.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.academic.entity.TeacherCourse;
import org.apache.ibatis.annotations.Mapper;

/**
 * 教师课程关联表Mapper
 */
@Mapper
public interface TeacherCourseMapper extends BaseMapper<TeacherCourse> {
}
