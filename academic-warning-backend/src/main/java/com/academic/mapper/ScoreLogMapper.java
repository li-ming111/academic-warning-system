package com.academic.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.academic.entity.ScoreLog;
import org.apache.ibatis.annotations.Mapper;

/**
 * 成绩修改日志表 Mapper
 */
@Mapper
public interface ScoreLogMapper extends BaseMapper<ScoreLog> {
}
