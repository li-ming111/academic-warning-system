package com.academic.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.academic.entity.ClassManagementRequest;
import com.academic.entity.Class;
import com.academic.entity.User;
import com.academic.mapper.ClassManagementRequestMapper;
import com.academic.mapper.ClassMapper;
import com.academic.mapper.UserMapper;
import com.academic.service.ClassManagementRequestService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.ArrayList;

/**
 * 班级管理申请服务实现
 */
@Slf4j
@Service
public class ClassManagementRequestServiceImpl extends ServiceImpl<ClassManagementRequestMapper, ClassManagementRequest> 
        implements ClassManagementRequestService {

    private final ClassManagementRequestMapper requestMapper;
    private final UserMapper userMapper;
    private final ClassMapper classMapper;

    public ClassManagementRequestServiceImpl(ClassManagementRequestMapper requestMapper,
                                           UserMapper userMapper,
                                           ClassMapper classMapper) {
        this.requestMapper = requestMapper;
        this.userMapper = userMapper;
        this.classMapper = classMapper;
    }

    @Override
    public Long submitRequest(Long teacherId, Long classId, String reason) {
        // 验证教师和班级是否存在
        User teacher = userMapper.selectById(teacherId);
        if (teacher == null) {
            throw new RuntimeException("教师不存在");
        }

        Class clazz = classMapper.selectById(classId);
        if (clazz == null) {
            throw new RuntimeException("班级不存在");
        }

        // 检查是否已有待审批或已批准的申请
        QueryWrapper<ClassManagementRequest> wrapper = new QueryWrapper<>();
        wrapper.eq("teacher_id", teacherId).eq("class_id", classId)
                .in("status", "pending", "approved");
        ClassManagementRequest existing = requestMapper.selectOne(wrapper);
        if (existing != null) {
            throw new RuntimeException("该教师已有该班级的待处理或已批准的申请");
        }

        // 创建新申请
        ClassManagementRequest request = new ClassManagementRequest();
        request.setTeacherId(teacherId);
        request.setClassId(classId);
        request.setReason(reason);
        request.setStatus("pending");
        request.setCreatedAt(LocalDateTime.now());
        request.setUpdatedAt(LocalDateTime.now());

        requestMapper.insert(request);
        log.info("班级管理申请已提交: 教师ID={}, 班级ID={}", teacherId, classId);
        return request.getId();
    }

    @Override
    public List<Map<String, Object>> getPendingRequests() {
        QueryWrapper<ClassManagementRequest> wrapper = new QueryWrapper<>();
        wrapper.eq("status", "pending").orderByDesc("created_at");
        List<ClassManagementRequest> requests = requestMapper.selectList(wrapper);

        List<Map<String, Object>> result = new ArrayList<>();
        for (ClassManagementRequest req : requests) {
            Map<String, Object> map = new HashMap<>();
            map.put("id", req.getId());
            map.put("teacherId", req.getTeacherId());
            map.put("classId", req.getClassId());
            map.put("status", req.getStatus());
            map.put("reason", req.getReason());
            map.put("createdAt", req.getCreatedAt());

            // 获取教师信息
            User teacher = userMapper.selectById(req.getTeacherId());
            if (teacher != null) {
                map.put("teacherName", teacher.getName());
                map.put("teacherUsername", teacher.getUsername());
            }

            // 获取班级信息
            Class clazz = classMapper.selectById(req.getClassId());
            if (clazz != null) {
                map.put("className", clazz.getName());
            }

            result.add(map);
        }
        return result;
    }

    @Override
    public void approveRequest(Long requestId) {
        ClassManagementRequest request = requestMapper.selectById(requestId);
        if (request == null) {
            throw new RuntimeException("申请不存在");
        }

        if (!"pending".equals(request.getStatus())) {
            throw new RuntimeException("只能批准待审批的申请");
        }

        // 检查该班级是否已被其他教师管理
        Class clazz = classMapper.selectById(request.getClassId());
        if (clazz != null && clazz.getTeacherId() != null && !clazz.getTeacherId().equals(request.getTeacherId())) {
            throw new RuntimeException("该班级已被其他教师管理");
        }

        // 更新班级关联的教师
        clazz.setTeacherId(request.getTeacherId());
        classMapper.updateById(clazz);

        // 更新申请状态
        request.setStatus("approved");
        request.setUpdatedAt(LocalDateTime.now());
        requestMapper.updateById(request);

        log.info("班级管理申请已批准: 申请ID={}, 教师ID={}, 班级ID={}", requestId, request.getTeacherId(), request.getClassId());
    }

    @Override
    public void rejectRequest(Long requestId, String rejectReason) {
        ClassManagementRequest request = requestMapper.selectById(requestId);
        if (request == null) {
            throw new RuntimeException("申请不存在");
        }

        if (!"pending".equals(request.getStatus())) {
            throw new RuntimeException("只能拒绝待审批的申请");
        }

        request.setStatus("rejected");
        request.setRejectReason(rejectReason);
        request.setUpdatedAt(LocalDateTime.now());
        requestMapper.updateById(request);

        log.info("班级管理申请已拒绝: 申请ID={}", requestId);
    }

    @Override
    public List<ClassManagementRequest> getTeacherRequests(Long teacherId) {
        QueryWrapper<ClassManagementRequest> wrapper = new QueryWrapper<>();
        wrapper.eq("teacher_id", teacherId).orderByDesc("created_at");
        return requestMapper.selectList(wrapper);
    }

    @Override
    public List<ClassManagementRequest> getClassRequests(Long classId) {
        QueryWrapper<ClassManagementRequest> wrapper = new QueryWrapper<>();
        wrapper.eq("class_id", classId).orderByDesc("created_at");
        return requestMapper.selectList(wrapper);
    }
}
