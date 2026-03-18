-- 更新沟通日志表，添加缺失的列
ALTER TABLE `communication_logs` 
ADD COLUMN `status` INT DEFAULT 0 COMMENT '状态：0=未读，1=已读',
ADD COLUMN `reply` TEXT COMMENT '回复内容';
