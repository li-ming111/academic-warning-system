-- 创建导出历史表
CREATE TABLE IF NOT EXISTS export_history (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '导出记录ID',
    data_type VARCHAR(50) NOT NULL COMMENT '导出数据类型',
    file_name VARCHAR(255) NOT NULL COMMENT '文件名',
    record_count INT DEFAULT 0 COMMENT '记录数',
    exported_by BIGINT COMMENT '导出人员ID',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '导出时间',
    INDEX idx_created_at (created_at),
    INDEX idx_data_type (data_type)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='数据导出历史记录表';
