package com.academic.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.academic.entity.College;
import com.academic.mapper.CollegeMapper;
import com.academic.mapper.StudentProfileMapper;
import com.academic.mapper.TeacherProfileMapper;
import com.academic.service.CollegeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class CollegeServiceImpl extends ServiceImpl<CollegeMapper, College> implements CollegeService {

    @Autowired
    private StudentProfileMapper studentProfileMapper;

    @Autowired
    private TeacherProfileMapper teacherProfileMapper;

    @Override
    public List<College> getAllColleges() {
        List<College> colleges = this.list();
        // 为每个学院计算学生数和教师数
        for (College college : colleges) {
            int studentCount = studentProfileMapper.selectCountByCollegeId(college.getId());
            int teacherCount = teacherProfileMapper.selectCountByCollegeId(college.getId());
            college.setStudentCount(studentCount);
            college.setTeacherCount(teacherCount);
        }
        return colleges;
    }

}
