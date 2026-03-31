package com.academic.mapper;

import com.academic.entity.ExportHistory;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 导出历史数据访问层
 */
@Mapper
public interface ExportHistoryMapper extends BaseMapper<ExportHistory> {
}
