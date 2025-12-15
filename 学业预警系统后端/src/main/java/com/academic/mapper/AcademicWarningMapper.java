package com.academic.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.academic.entity.AcademicWarning;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import java.util.List;

@Mapper
public interface AcademicWarningMapper extends BaseMapper<AcademicWarning> {
    
    @Select("SELECT id,student_id,warning_level,description,status,appeal_status,created_at,updated_at,college_id,class_id FROM academic_warnings WHERE student_id = #{studentId} ORDER BY created_at DESC")
    List<AcademicWarning> selectByStudentId(Long studentId);
}

