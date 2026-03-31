package com.academic.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.academic.entity.Major;

public interface MajorService extends IService<Major> {

    /**
     * 根据学院ID获取所有专业
     */
    java.util.List<Major> getMajorsByCollegeId(Long collegeId);

}
