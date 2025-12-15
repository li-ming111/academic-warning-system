-- 完整数据表结构和初始数据脚本 (UTF-8编码)
-- 使用中文测试数据

USE academic_warning_system;
SET FOREIGN_KEY_CHECKS=0;

-- 更新学院数据为中文
DELETE FROM colleges;
INSERT INTO `colleges` (`id`, `name`) VALUES 
(1, '信息工程学院'),
(2, '人工智能学院'),
(3, '计算机科学学院'),
(4, '电子工程学院');

-- 更新专业数据为中文
DELETE FROM majors;
INSERT INTO `majors` (`code`, `name`, `college_id`) VALUES
('01', '计算机科学与技术', 3),
('02', '软件工程', 3),
('03', '网络工程', 1),
('04', '人工智能', 2),
('05', '机器学习', 2);

-- 更新课程数据为中文
DELETE FROM courses;
INSERT INTO `courses` (`name`, `type`, `credits`, `score_template`) VALUES
('高等数学', 'required', 4.0, 'exam'),
('线性代数', 'required', 3.0, 'exam'),
('数据库系统', 'required', 3.5, 'exam'),
('计算机网络', 'required', 3.0, 'exam'),
('算法设计', 'elective', 2.5, 'exam'),
('机器学习基础', 'elective', 3.0, 'exam');

-- 更新预警规则数据为中文
DELETE FROM warning_rules;
INSERT INTO `warning_rules` (`condition`, `level`, `status`) VALUES
('成绩低于60分', 'high', 1),
('平均绩点低于2.0', 'medium', 1),
('挂科超过3门', 'high', 1),
('学分绩点下降超过30%', 'medium', 1);

-- 更新预警记录数据为中文
DELETE FROM academic_warnings;
INSERT INTO `academic_warnings` (`student_id`, `warning_level`, `description`, `status`, `appeal_status`) VALUES
(1, 'high', '线性代数成绩51.5分，低于及格线，建议补考或参加辅导', 'pending', 'none'),
(1, 'medium', '本学期平均绩点较低，建议加强学习', 'pending', 'none');

-- 更新成绩申诉数据为中文
DELETE FROM score_appeals;
INSERT INTO `score_appeals` (`warning_id`, `reason`, `status`) VALUES
(1, '线性代数考试期间因病缓考，希望申请重考机会', 'pending');

-- 更新沟通记录数据为中文
DELETE FROM communication_logs;
INSERT INTO `communication_logs` (`teacher_id`, `student_id`, `content`) VALUES
(3, 1, '关于线性代数成绩，建议参加周末的专项辅导班，同时加强基础知识复习');

-- 更新审计日志数据为中文
DELETE FROM audit_logs;
INSERT INTO `audit_logs` (`user_id`, `action_type`, `action_details`) VALUES
(5, 'create', '系统初始化，创建了基础数据'),
(5, 'create', '创建了预警规则'),
(3, 'create', '录入了学生成绩');

-- 更新通知数据为中文
DELETE FROM notifications;
INSERT INTO `notifications` (`user_id`, `content`, `status`) VALUES
(1, '【红色预警】线性代数成绩51.5分，已低于及格线，请立即联系教师获取帮扶方案', 'unread'),
(1, '帮扶计划已生成，请查看详情并按时参加辅导课程', 'unread'),
(2, '您本学期成绩已发布，请登录查看详细信息', 'read');

-- 验证数据
SELECT '===== 学院数据 =====' as '';
SELECT * FROM colleges;

SELECT '===== 专业数据 =====' as '';
SELECT * FROM majors;

SELECT '===== 用户数据统计 =====' as '';
SELECT role, COUNT(*) as count FROM users GROUP BY role;

SELECT '===== 成绩数据统计 =====' as '';
SELECT COUNT(*) as total_scores FROM scores;

SELECT '===== 预警记录 =====' as '';
SELECT * FROM academic_warnings;

SELECT '===== 通知记录 =====' as '';
SELECT * FROM notifications;
SET FOREIGN_KEY_CHECKS=1;
