package com.academic.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.academic.entity.AcademicWarning;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import java.util.List;

@Mapper
public interface AcademicWarningMapper extends BaseMapper<AcademicWarning> {
    
    @Select("SELECT aw.id,aw.student_id,aw.warning_level,aw.title,aw.description,aw.status,aw.appeal_status,aw.created_at,aw.updated_at,aw.college_id,aw.class_id,aw.course_id,c.name as courseName FROM academic_warnings aw LEFT JOIN courses c ON aw.course_id = c.id WHERE aw.student_id = #{studentId} ORDER BY aw.created_at DESC")
    List<AcademicWarning> selectByStudentId(Long studentId);
}

