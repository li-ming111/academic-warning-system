package com.academic.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.academic.entity.College;
import com.academic.mapper.CollegeMapper;
import com.academic.service.CollegeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class CollegeServiceImpl extends ServiceImpl<CollegeMapper, College> implements CollegeService {

    @Override
    public List<College> getAllColleges() {
        return this.list();
    }

}
