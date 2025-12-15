package com.academic.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.academic.entity.College;

public interface CollegeService extends IService<College> {

    /**
     * 获取所有学院
     */
    java.util.List<College> getAllColleges();

}
