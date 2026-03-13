-- Add missing columns to colleges table
ALTER TABLE `colleges` ADD `student_count` INT DEFAULT 0;
ALTER TABLE `colleges` ADD `teacher_count` INT DEFAULT 0;

-- Add missing columns to class_management_request table
ALTER TABLE `class_management_request` ADD `counselor_id` BIGINT;
ALTER TABLE `class_management_request` ADD `user_type` VARCHAR(20);

-- Set default values
UPDATE `class_management_request` SET `user_type` = 'teacher' WHERE `user_type` IS NULL OR `user_type` = '';
