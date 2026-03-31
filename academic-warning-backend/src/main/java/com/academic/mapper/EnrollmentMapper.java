package com.academic.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.academic.entity.Enrollment;
import org.apache.ibatis.annotations.Mapper;

/**
 * 选修课信息表 Mapper
 */
@Mapper
public interface EnrollmentMapper extends BaseMapper<Enrollment> {
}
