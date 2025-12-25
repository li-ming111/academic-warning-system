USE academic_warning_system;

SET FOREIGN_KEY_CHECKS = 0;

-- 删除旧课程
DELETE FROM scores WHERE course_id IN (574, 575, 576, 577, 578);
DELETE FROM courses WHERE id IN (574, 575, 576, 577, 578);
DELETE FROM courses WHERE id IN (569, 570, 571, 572, 573);

SET FOREIGN_KEY_CHECKS = 1;

-- 重新插入课程
INSERT INTO courses (code, name, credits, type, created_at, updated_at) VALUES
('INET001', '互联网程序设计', 4.00, 'required', NOW(), NOW()),
('OS001', 'Linux操作系统', 4.00, 'required', NOW(), NOW()),
('PYTHON001', 'Python程序设计', 4.00, 'required', NOW(), NOW()),
('PSY001', '生活中的心理学', 4.00, 'required', NOW(), NOW()),
('SE001', '软件工程', 3.50, 'required', NOW(), NOW());

-- 获取新课程ID
SELECT id, name, credits FROM courses ORDER BY id DESC LIMIT 5;
