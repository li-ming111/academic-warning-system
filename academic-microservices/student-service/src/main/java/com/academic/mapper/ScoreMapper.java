package com.academic.mapper;

import com.academic.entity.Score;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface ScoreMapper {
    
    @Select("SELECT * FROM score WHERE student_id = #{studentId}")
    List<Score> selectByStudentId(Long studentId);
    
    @Select("SELECT * FROM score WHERE student_id = #{studentId} AND semester = #{semester}")
    List<Score> selectByStudentIdAndSemester(Long studentId, String semester);
}
