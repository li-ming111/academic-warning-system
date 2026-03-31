package com.academic.mapper;

import com.academic.entity.AcademicWarning;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface AcademicWarningMapper {
    
    @Select("SELECT * FROM academic_warning WHERE student_id = #{studentId}")
    List<AcademicWarning> selectByStudentId(Long studentId);
    
    @Select("SELECT * FROM academic_warning WHERE student_id = #{studentId} AND status IN ('pending', 'confirmed')")
    List<AcademicWarning> selectPendingByStudentId(Long studentId);
}
