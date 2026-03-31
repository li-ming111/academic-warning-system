package com.academic.mapper;

import com.academic.entity.StudentProfile;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface StudentProfileMapper {
    
    @Select("SELECT * FROM student_profile WHERE user_id = #{userId}")
    StudentProfile selectByUserId(Long userId);
    
    @Select("SELECT * FROM student_profile WHERE id = #{id}")
    StudentProfile selectById(Long id);
}
