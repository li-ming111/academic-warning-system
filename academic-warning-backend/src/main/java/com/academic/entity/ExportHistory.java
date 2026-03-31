package com.academic.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 导出历史记录实体类
 */
@Data
@TableName("export_history")
public class ExportHistory implements Serializable {
    
    @TableId(type = IdType.AUTO)
    private Long id;
    
    /**
     * 导出数据类型：学生数据、成绩数据、预警数据、用户数据
     */
    private String dataType;
    
    /**
     * 文件名
     */
    private String fileName;
    
    /**
     * 记录数
     */
    private Integer recordCount;
    
    /**
     * 导出人员ID
     */
    private Long exportedBy;
    
    /**
     * 导出时间
     */
    private LocalDateTime createdAt;
}
