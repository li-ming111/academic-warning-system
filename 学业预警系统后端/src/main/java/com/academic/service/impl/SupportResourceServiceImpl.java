package com.academic.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.academic.entity.SupportResource;
import com.academic.mapper.SupportResourceMapper;
import com.academic.service.SupportResourceService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class SupportResourceServiceImpl extends ServiceImpl<SupportResourceMapper, SupportResource> implements SupportResourceService {

    private final SupportResourceMapper supportResourceMapper;

    public SupportResourceServiceImpl(SupportResourceMapper supportResourceMapper) {
        this.supportResourceMapper = supportResourceMapper;
    }

    @Override
    public Long createResource(SupportResource resource) {
        this.save(resource);
        log.info("创建帮扶资源: {}", resource.getName());
        return resource.getId();
    }

    @Override
    public void updateResource(Long resourceId, SupportResource resource) {
        resource.setId(resourceId);
        this.updateById(resource);
        log.info("更新帮扶资源: {}", resource.getName());
    }

    @Override
    public void deleteResource(Long resourceId) {
        this.removeById(resourceId);
        log.info("删除帮扶资源: ID={}", resourceId);
    }

    @Override
    public SupportResource getResourceById(Long resourceId) {
        return this.getById(resourceId);
    }

    @Override
    public List<SupportResource> getResourcesByCourseCode(String courseCode) {
        QueryWrapper<SupportResource> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("course_code", courseCode);
        queryWrapper.eq("status", "active");
        return this.list(queryWrapper);
    }

    @Override
    public List<SupportResource> getResourcesByType(String type) {
        QueryWrapper<SupportResource> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("type", type);
        queryWrapper.eq("status", "active");
        return this.list(queryWrapper);
    }

    @Override
    public List<SupportResource> getActiveResources() {
        QueryWrapper<SupportResource> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("status", "active");
        return this.list(queryWrapper);
    }

    @Override
    public List<SupportResource> matchResourcesForStudent(Long studentId, String courseCode) {
        // 首先根据课程代码查询相关资源
        List<SupportResource> resources = getResourcesByCourseCode(courseCode);
        
        // 如果没有找到资源，尝试根据课程名称的关键字匹配
        if (resources.isEmpty()) {
            resources = matchResourcesByKeyword(courseCode);
        }
        
        log.info("为学生 {} 匹配到 {} 个帮扶资源", studentId, resources.size());
        return resources;
    }

    @Override
    public List<SupportResource> matchResourcesForWarning(Long studentId, String warningReason) {
        // 从预警原因中提取课程信息
        String courseCode = extractCourseCodeFromWarning(warningReason);
        
        if (courseCode != null) {
            return matchResourcesForStudent(studentId, courseCode);
        }
        
        return List.of();
    }

    // 根据关键字匹配资源
    private List<SupportResource> matchResourcesByKeyword(String keyword) {
        QueryWrapper<SupportResource> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("status", "active");
        queryWrapper.like("course_code", keyword);
        return this.list(queryWrapper);
    }

    // 从预警原因中提取课程代码
    private String extractCourseCodeFromWarning(String warningReason) {
        // 简单的提取逻辑，实际可能需要更复杂的解析
        if (warningReason.contains("高数")) {
            return "MATH101";
        } else if (warningReason.contains("英语")) {
            return "ENGL101";
        } else if (warningReason.contains("物理")) {
            return "PHYS101";
        } else if (warningReason.contains("计算机")) {
            return "CS101";
        }
        return null;
    }

}
