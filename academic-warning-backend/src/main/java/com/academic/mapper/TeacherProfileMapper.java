package com.academic.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.academic.entity.TeacherProfile;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface TeacherProfileMapper extends BaseMapper<TeacherProfile> {

    @Select("SELECT COUNT(*) FROM teacher_profile tp JOIN users u ON tp.user_id = u.id WHERE tp.college_id = #{collegeId} AND u.role = 2")
    int selectCountByCollegeId(Long collegeId);

}
