package com.academic.service;

import com.academic.entity.SupportResource;
import java.util.List;

public interface SupportResourceService {

    // 创建帮扶资源
    Long createResource(SupportResource resource);

    // 更新帮扶资源
    void updateResource(Long resourceId, SupportResource resource);

    // 删除帮扶资源
    void deleteResource(Long resourceId);

    // 获取资源详情
    SupportResource getResourceById(Long resourceId);

    // 根据课程代码查询资源
    List<SupportResource> getResourcesByCourseCode(String courseCode);

    // 根据资源类型查询资源
    List<SupportResource> getResourcesByType(String type);

    // 查询所有活跃资源
    List<SupportResource> getActiveResources();

    // 为学生匹配适合的资源
    List<SupportResource> matchResourcesForStudent(Long studentId, String courseCode);

    // 为预警匹配资源
    List<SupportResource> matchResourcesForWarning(Long studentId, String warningReason);

}
