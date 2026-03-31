package com.academic.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.academic.entity.ClassManagementRequest;
import org.apache.ibatis.annotations.Mapper;

/**
 * 班级管理申请Mapper
 */
@Mapper
public interface ClassManagementRequestMapper extends BaseMapper<ClassManagementRequest> {
}
