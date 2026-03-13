-- 添加缺失的列到feedbacks表
ALTER TABLE feedbacks ADD COLUMN `status` VARCHAR(50) DEFAULT 'pending' AFTER `category`;
ALTER TABLE feedbacks ADD COLUMN `reply` TEXT AFTER `status`;
