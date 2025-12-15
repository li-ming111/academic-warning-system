package com.academic.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.academic.entity.Major;
import com.academic.mapper.MajorMapper;
import com.academic.service.MajorService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class MajorServiceImpl extends ServiceImpl<MajorMapper, Major> implements MajorService {

    @Override
    public List<Major> getMajorsByCollegeId(Long collegeId) {
        QueryWrapper<Major> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("college_id", collegeId);
        return this.list(queryWrapper);
    }

}
