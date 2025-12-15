package com.academic.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.academic.entity.Class;
import com.academic.entity.StudentProfile;
import com.academic.entity.AcademicWarning;
import com.academic.mapper.ClassMapper;
import com.academic.mapper.StudentProfileMapper;
import com.academic.mapper.AcademicWarningMapper;
import com.academic.service.ClassManagementService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

@Slf4j
@Service
public class ClassManagementServiceImpl extends ServiceImpl<ClassMapper, Class> implements ClassManagementService {

    private final StudentProfileMapper studentProfileMapper;
    private final AcademicWarningMapper warningMapper;

    public ClassManagementServiceImpl(StudentProfileMapper studentProfileMapper, AcademicWarningMapper warningMapper) {
        this.studentProfileMapper = studentProfileMapper;
        this.warningMapper = warningMapper;
    }

    @Override
    @Transactional
    public Long createClass(String name, Long majorId) {
        Class clazz = new Class();
        clazz.setName(name);
        clazz.setMajorId(majorId);
        this.save(clazz);
        log.info("班级创建成功: {} (专业ID: {})", name, majorId);
        return clazz.getId();
    }

    @Override
    public List<Class> getClassesByMajor(Long majorId) {
        QueryWrapper<Class> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("major_id", majorId);
        return this.list(queryWrapper);
    }

    @Override
    public Class getClassDetail(Long classId) {
        return this.getById(classId);
    }

    @Override
    @Transactional
    public void updateClass(Long classId, String name) {
        Class clazz = this.getById(classId);
        if (clazz != null) {
            clazz.setName(name);
            this.updateById(clazz);
            log.info("班级更新成功: {} (ID: {})", name, classId);
        }
    }

    @Override
    @Transactional
    public void deleteClass(Long classId) {
        this.removeById(classId);
        log.info("班级删除成功: ID={}", classId);
    }

    @Override
    public Integer getStudentCount(Long classId) {
        QueryWrapper<StudentProfile> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("class_id", classId);
        return Math.toIntExact(studentProfileMapper.selectCount(queryWrapper));
    }

    @Override
    public Map<String, Object> getClassWarningStatistics(Long classId) {
        // 获取班级的所有学生
        QueryWrapper<StudentProfile> studentQuery = new QueryWrapper<>();
        studentQuery.eq("class_id", classId);
        List<StudentProfile> students = studentProfileMapper.selectList(studentQuery);

        // 统计各级别预警数
        long redCount = 0;
        long yellowCount = 0;
        long blueCount = 0;

        for (StudentProfile student : students) {
            QueryWrapper<AcademicWarning> warningQuery = new QueryWrapper<>();
            warningQuery.eq("student_id", student.getId());
            List<AcademicWarning> warnings = warningMapper.selectList(warningQuery);

            for (AcademicWarning warning : warnings) {
                if ("red".equals(warning.getWarningLevel())) {
                    redCount++;
                } else if ("yellow".equals(warning.getWarningLevel())) {
                    yellowCount++;
                } else if ("blue".equals(warning.getWarningLevel())) {
                    blueCount++;
                }
            }
        }

        Map<String, Object> result = new HashMap<>();
        result.put("totalStudents", students.size());
        result.put("redWarnings", redCount);
        result.put("yellowWarnings", yellowCount);
        result.put("blueWarnings", blueCount);
        result.put("totalWarnings", redCount + yellowCount + blueCount);
        return result;
    }
}
