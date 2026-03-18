package com.academic.controller;

import com.academic.common.ApiResponse;
import com.academic.entity.SupportResource;
import com.academic.service.SupportResourceService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/support-resources")
public class SupportResourceController {

    private final SupportResourceService supportResourceService;

    public SupportResourceController(SupportResourceService supportResourceService) {
        this.supportResourceService = supportResourceService;
    }

    /**
     * 创建帮扶资源
     */
    @PostMapping
    public ApiResponse<Long> createResource(@RequestBody SupportResource resource) {
        try {
            Long resourceId = supportResourceService.createResource(resource);
            return ApiResponse.success(resourceId);
        } catch (Exception e) {
            log.error("创建帮扶资源失败", e);
            return ApiResponse.error(e.getMessage());
        }
    }

    /**
     * 更新帮扶资源
     */
    @PutMapping("/{resourceId}")
    public ApiResponse<String> updateResource(@PathVariable Long resourceId, @RequestBody SupportResource resource) {
        try {
            supportResourceService.updateResource(resourceId, resource);
            return ApiResponse.success("资源更新成功");
        } catch (Exception e) {
            log.error("更新帮扶资源失败", e);
            return ApiResponse.error(e.getMessage());
        }
    }

    /**
     * 删除帮扶资源
     */
    @DeleteMapping("/{resourceId}")
    public ApiResponse<String> deleteResource(@PathVariable Long resourceId) {
        try {
            supportResourceService.deleteResource(resourceId);
            return ApiResponse.success("资源删除成功");
        } catch (Exception e) {
            log.error("删除帮扶资源失败", e);
            return ApiResponse.error(e.getMessage());
        }
    }

    /**
     * 获取资源详情
     */
    @GetMapping("/{resourceId}")
    public ApiResponse<SupportResource> getResourceById(@PathVariable Long resourceId) {
        try {
            SupportResource resource = supportResourceService.getResourceById(resourceId);
            return ApiResponse.success(resource);
        } catch (Exception e) {
            log.error("获取资源详情失败", e);
            return ApiResponse.error(e.getMessage());
        }
    }

    /**
     * 根据课程代码获取资源
     */
    @GetMapping("/by-course/{courseCode}")
    public ApiResponse<List<SupportResource>> getResourcesByCourseCode(@PathVariable String courseCode) {
        try {
            List<SupportResource> resources = supportResourceService.getResourcesByCourseCode(courseCode);
            return ApiResponse.success(resources);
        } catch (Exception e) {
            log.error("获取课程资源失败", e);
            return ApiResponse.error(e.getMessage());
        }
    }

    /**
     * 根据资源类型获取资源
     */
    @GetMapping("/by-type/{type}")
    public ApiResponse<List<SupportResource>> getResourcesByType(@PathVariable String type) {
        try {
            List<SupportResource> resources = supportResourceService.getResourcesByType(type);
            return ApiResponse.success(resources);
        } catch (Exception e) {
            log.error("获取类型资源失败", e);
            return ApiResponse.error(e.getMessage());
        }
    }

    /**
     * 获取所有活跃资源
     */
    @GetMapping("/active")
    public ApiResponse<List<SupportResource>> getActiveResources() {
        try {
            List<SupportResource> resources = supportResourceService.getActiveResources();
            return ApiResponse.success(resources);
        } catch (Exception e) {
            log.error("获取活跃资源失败", e);
            return ApiResponse.error(e.getMessage());
        }
    }

    /**
     * 为学生匹配资源
     */
    @GetMapping("/match/{studentId}/{courseCode}")
    public ApiResponse<List<SupportResource>> matchResourcesForStudent(@PathVariable Long studentId, @PathVariable String courseCode) {
        try {
            List<SupportResource> resources = supportResourceService.matchResourcesForStudent(studentId, courseCode);
            return ApiResponse.success(resources);
        } catch (Exception e) {
            log.error("匹配资源失败", e);
            return ApiResponse.error(e.getMessage());
        }
    }

    /**
     * 为预警匹配资源
     */
    @PostMapping("/match-for-warning")
    public ApiResponse<List<SupportResource>> matchResourcesForWarning(@RequestBody MatchRequest request) {
        try {
            List<SupportResource> resources = supportResourceService.matchResourcesForWarning(request.getStudentId(), request.getWarningReason());
            return ApiResponse.success(resources);
        } catch (Exception e) {
            log.error("为预警匹配资源失败", e);
            return ApiResponse.error(e.getMessage());
        }
    }

    // 匹配请求类
    public static class MatchRequest {
        private Long studentId;
        private String warningReason;

        public Long getStudentId() {
            return studentId;
        }

        public void setStudentId(Long studentId) {
            this.studentId = studentId;
        }

        public String getWarningReason() {
            return warningReason;
        }

        public void setWarningReason(String warningReason) {
            this.warningReason = warningReason;
        }
    }

}
