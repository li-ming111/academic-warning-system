package com.academic.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.academic.entity.StudentProfile;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface StudentProfileMapper extends BaseMapper<StudentProfile> {

    @Select("SELECT COUNT(*) FROM student_profile WHERE college_id = #{collegeId}")
    int selectCountByCollegeId(Long collegeId);

}
