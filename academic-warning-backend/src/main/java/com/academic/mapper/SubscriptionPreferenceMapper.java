package com.academic.mapper;

import com.academic.entity.SubscriptionPreference;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface SubscriptionPreferenceMapper extends BaseMapper<SubscriptionPreference> {
    
    /**
     * 根据学生ID获取订阅偏好
     */
    SubscriptionPreference selectByStudentId(Long studentId);
}
