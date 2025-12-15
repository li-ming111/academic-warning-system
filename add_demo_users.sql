-- 添加其他三个用户端的演示账号
USE academic_warning_system;

-- 更新或删除重复的用户记录（如果存在）
DELETE FROM users WHERE username IN ('teacher001', 'counselor001', 'admin001');

-- 重新插入四个演示账号（明文密码以便于演示）
-- 学生账号: 2023020616 / 123456
-- 教师账号: teacher001 / teacher123
-- 辅导员账号: counselor001 / counselor123
-- 管理员账号: admin001 / admin123

INSERT INTO users (username, password, email, phone, role, status) VALUES
('2023020616', '123456', 'student001@example.com', '13800138000', 1, 1),
('teacher001', 'teacher123', 'teacher001@example.com', '13800138001', 2, 1),
('counselor001', 'counselor123', 'counselor001@example.com', '13800138002', 4, 1),
('admin001', 'admin123', 'admin001@example.com', '13800138003', 3, 1);
