-- 检查教师用户信息
SELECT id, username, name, role FROM users WHERE role = 2;

-- 检查所有消息记录
SELECT * FROM communication_logs;

-- 检查学生信息
SELECT id, username, name FROM users WHERE role = 1;
