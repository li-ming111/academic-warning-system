package com.academic.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.academic.entity.SupportResource;
import java.util.List;

public interface SupportResourceMapper extends BaseMapper<SupportResource> {

    // 根据课程代码和资源类型查询资源
    List<SupportResource> selectByCourseCodeAndType(String courseCode, String type);

    // 查询所有活跃的资源
    List<SupportResource> selectActiveResources();

    // 根据学生ID和课程代码匹配适合的资源
    List<SupportResource> selectMatchingResourcesForStudent(Long studentId, String courseCode);

}
