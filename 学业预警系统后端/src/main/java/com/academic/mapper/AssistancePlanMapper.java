package com.academic.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.academic.entity.AssistancePlan;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import java.util.List;

@Mapper
public interface AssistancePlanMapper extends BaseMapper<AssistancePlan> {
    
    @Select("SELECT id,student_id,warning_id,title,target,measures,description,progress_percentage,status,completed_at,created_at,updated_at FROM assistance_plans WHERE student_id = #{studentId} ORDER BY created_at DESC")
    List<AssistancePlan> selectByStudentId(Long studentId);
}

