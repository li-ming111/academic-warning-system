package com.academic.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.academic.entity.ClassManagementRequest;
import com.academic.entity.Class;
import com.academic.entity.User;
import com.academic.entity.StudentProfile;
import com.academic.mapper.ClassManagementRequestMapper;
import com.academic.mapper.ClassMapper;
import com.academic.mapper.UserMapper;
import com.academic.mapper.StudentProfileMapper;
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
    private final StudentProfileMapper studentProfileMapper;

    public ClassManagementRequestServiceImpl(ClassManagementRequestMapper requestMapper,
                                           UserMapper userMapper,
                                           ClassMapper classMapper,
                                           StudentProfileMapper studentProfileMapper) {
        this.requestMapper = requestMapper;
        this.userMapper = userMapper;
        this.classMapper = classMapper;
        this.studentProfileMapper = studentProfileMapper;
    }

    @Override
    public Long submitRequest(Long userId, Long classId, String userType, String reason) {
        // 验证用户和班级是否存在
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }

        Class clazz = classMapper.selectById(classId);
        if (clazz == null) {
            throw new RuntimeException("班级不存在");
        }

        // 检查是否已有待审批或已批准的申请
        QueryWrapper<ClassManagementRequest> wrapper = new QueryWrapper<>();
        wrapper.eq("class_id", classId)
                .eq("user_type", userType)
                .in("status", "pending", "approved");
        ClassManagementRequest existing = requestMapper.selectOne(wrapper);
        if (existing != null) {
            throw new RuntimeException("该班级已有" + ("teacher".equals(userType) ? "教师" : "辅导员") + "的待处理或已批准的申请");
        }

        // 创建新申请
        ClassManagementRequest request = new ClassManagementRequest();
        if ("teacher".equals(userType)) {
            request.setTeacherId(userId);
        } else if ("counselor".equals(userType)) {
            request.setCounselorId(userId);
        } else {
            throw new RuntimeException("无效的用户类型");
        }
        request.setClassId(classId);
        request.setUserType(userType);
        request.setReason(reason);
        request.setStatus("pending");
        request.setCreatedAt(LocalDateTime.now());
        request.setUpdatedAt(LocalDateTime.now());

        requestMapper.insert(request);
        log.info("班级管理申请已提交: 用户ID={}, 用户类型={}, 班级ID={}", userId, userType, classId);
        return request.getId();
    }

    @Override
    public List<Map<String, Object>> searchClasses(String keyword) {
        log.info("Searching classes with keyword: {}", keyword);
        QueryWrapper<Class> wrapper = new QueryWrapper<>();
        if (keyword != null && !keyword.isEmpty()) {
            wrapper.like("name", keyword);
        }
        List<Class> classes = classMapper.selectList(wrapper);
        log.info("Found {} classes", classes.size());

        List<Map<String, Object>> result = new ArrayList<>();
        for (Class clazz : classes) {
            Map<String, Object> map = new HashMap<>();
            map.put("id", clazz.getId());
            map.put("name", clazz.getName());
            
            // 获取班级学生数
            QueryWrapper<StudentProfile> studentQuery = new QueryWrapper<>();
            studentQuery.eq("class_id", clazz.getId());
            Long studentCount = studentProfileMapper.selectCount(studentQuery);
            map.put("studentCount", studentCount);
            
            // 获取专业信息
            if (clazz.getMajorId() != null) {
                map.put("majorId", clazz.getMajorId());
            }
            
            // 检查是否已有教师或辅导员
            QueryWrapper<ClassManagementRequest> requestQuery = new QueryWrapper<>();
            requestQuery.eq("class_id", clazz.getId()).eq("status", "approved");
            
            // 检查教师
            requestQuery.eq("user_type", "teacher");
            ClassManagementRequest teacherRequest = requestMapper.selectOne(requestQuery);
            map.put("hasTeacher", teacherRequest != null || clazz.getTeacherId() != null);
            
            // 检查辅导员
            requestQuery.clear();
            requestQuery.eq("class_id", clazz.getId()).eq("status", "approved").eq("user_type", "counselor");
            ClassManagementRequest counselorRequest = requestMapper.selectOne(requestQuery);
            map.put("hasCounselor", counselorRequest != null || clazz.getCounselorId() != null);
            
            result.add(map);
        }
        return result;
    }

    @Override
    public List<Map<String, Object>> getTeacherClasses(Long teacherId) {
        QueryWrapper<ClassManagementRequest> wrapper = new QueryWrapper<>();
        wrapper.eq("teacher_id", teacherId).eq("status", "approved");
        List<ClassManagementRequest> requests = requestMapper.selectList(wrapper);

        // 也获取直接在 classes 表中的班级
        QueryWrapper<Class> classWrapper = new QueryWrapper<>();
        classWrapper.eq("teacher_id", teacherId);
        List<Class> directClasses = classMapper.selectList(classWrapper);

        List<Map<String, Object>> result = new ArrayList<>();
        
        // 处理申请的班级
        for (ClassManagementRequest req : requests) {
            Class clazz = classMapper.selectById(req.getClassId());
            if (clazz != null) {
                result.add(buildClassMap(clazz));
            }
        }
        
        // 处理直接关联的班级
        for (Class clazz : directClasses) {
            Map<String, Object> map = buildClassMap(clazz);
            // 避免重复
            if (!result.stream().anyMatch(m -> m.get("id").equals(map.get("id")))) {
                result.add(map);
            }
        }
        
        return result;
    }

    @Override
    public List<Map<String, Object>> getCounselorClasses(Long counselorId) {
        QueryWrapper<ClassManagementRequest> wrapper = new QueryWrapper<>();
        wrapper.eq("counselor_id", counselorId).eq("status", "approved");
        List<ClassManagementRequest> requests = requestMapper.selectList(wrapper);

        // 也获取直接在 classes 表中的班级
        QueryWrapper<Class> classWrapper = new QueryWrapper<>();
        classWrapper.eq("counselor_id", counselorId);
        List<Class> directClasses = classMapper.selectList(classWrapper);

        List<Map<String, Object>> result = new ArrayList<>();
        
        // 处理申请的班级
        for (ClassManagementRequest req : requests) {
            Class clazz = classMapper.selectById(req.getClassId());
            if (clazz != null) {
                result.add(buildClassMap(clazz));
            }
        }
        
        // 处理直接关联的班级
        for (Class clazz : directClasses) {
            Map<String, Object> map = buildClassMap(clazz);
            // 避免重复
            if (!result.stream().anyMatch(m -> m.get("id").equals(map.get("id")))) {
                result.add(map);
            }
        }
        
        return result;
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
            map.put("userType", req.getUserType());
            map.put("classId", req.getClassId());
            map.put("status", req.getStatus());
            map.put("reason", req.getReason());
            map.put("createdAt", req.getCreatedAt());

            // 获取用户信息
            User user = null;
            if ("teacher".equals(req.getUserType()) && req.getTeacherId() != null) {
                // 直接查询User（teacherId存储的是User的ID）
                user = userMapper.selectById(req.getTeacherId());
            } else if ("counselor".equals(req.getUserType()) && req.getCounselorId() != null) {
                // 直接查询User（counselorId存储的是User的ID）
                user = userMapper.selectById(req.getCounselorId());
            }
            
            if (user != null) {
                map.put("teacherName", user.getName());
                map.put("teacherUsername", user.getUsername());
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

        Class clazz = classMapper.selectById(request.getClassId());
        if (clazz == null) {
            throw new RuntimeException("班级不存在");
        }

        // 根据用户类型更新班级
        if ("teacher".equals(request.getUserType())) {
            // 直接更新教师ID，允许覆盖现有教师
            clazz.setTeacherId(request.getTeacherId());
        } else if ("counselor".equals(request.getUserType())) {
            // 直接更新辅导员ID，允许覆盖现有辅导员
            clazz.setCounselorId(request.getCounselorId());
        }

        classMapper.updateById(clazz);

        // 更新申请状态
        request.setStatus("approved");
        request.setUpdatedAt(LocalDateTime.now());
        requestMapper.updateById(request);

        log.info("班级管理申请已批准: 申请ID={}, 班级ID={}", requestId, request.getClassId());
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
    public List<ClassManagementRequest> getCounselorRequests(Long counselorId) {
        QueryWrapper<ClassManagementRequest> wrapper = new QueryWrapper<>();
        wrapper.eq("counselor_id", counselorId).orderByDesc("created_at");
        return requestMapper.selectList(wrapper);
    }

    @Override
    public List<ClassManagementRequest> getClassRequests(Long classId) {
        QueryWrapper<ClassManagementRequest> wrapper = new QueryWrapper<>();
        wrapper.eq("class_id", classId).orderByDesc("created_at");
        return requestMapper.selectList(wrapper);
    }

    /**
     * 构建班级地图
     */
    private Map<String, Object> buildClassMap(Class clazz) {
        Map<String, Object> map = new HashMap<>();
        map.put("id", clazz.getId());
        map.put("name", clazz.getName());
        
        // 获取班级学生数
        QueryWrapper<StudentProfile> studentQuery = new QueryWrapper<>();
        studentQuery.eq("class_id", clazz.getId());
        Long studentCount = studentProfileMapper.selectCount(studentQuery);
        map.put("studentCount", studentCount);
        
        map.put("majorId", clazz.getMajorId());
        map.put("teacherId", clazz.getTeacherId());
        map.put("counselorId", clazz.getCounselorId());
        
        return map;
    }
}
