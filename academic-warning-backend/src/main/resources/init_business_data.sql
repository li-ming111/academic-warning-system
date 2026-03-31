-- 学业预警系统 - 业务数据初始化脚本
-- 此脚本在init_database.sql执行后运行，用于创建完整的测试业务数据

USE `academic_warning_system`;

-- ==================== 插入额外课程数据 ====================
INSERT INTO `courses` (`code`, `name`, `credits`, `type`, `score_template`) VALUES
('CS101', '数据结构', 4.0, '必修', 'exam'),
('CS102', '算法设计', 4.0, '必修', 'exam'),
('CS201', 'Java程序设计', 3.5, '必修', 'exam'),
('CS301', '数据库系统', 4.0, '必修', 'exam'),
('CS401', '软件工程', 3.0, '选修', 'project'),
('CS402', '人工智能基础', 3.5, '选修', 'exam'),
('CS403', '云计算技术', 3.0, '选修', 'project'),
('CS404', '机器学习', 4.0, '选修', 'exam');

-- ==================== 成绩数据 ====================
-- 学生1 (20210001 - 张三)
INSERT INTO `scores` (`student_id`, `course_id`, `regular_score`, `final_score`, `score_total`, `grade_point`, `semester`) VALUES
(1, 1, 80, 88, 85, 3.5, '2023-2024秋'),
(1, 2, 75, 80, 78, 3.2, '2023-2024秋'),
(1, 3, 85, 90, 88, 3.8, '2023-2024秋'),
(1, 4, 68, 74, 72, 3.0, '2023-2024春'),
(1, 5, 90, 92, 91, 4.0, '2023-2024春'),
(1, 6, 70, 75, 73, 3.0, '2023-2024春'),
(1, 7, 88, 91, 90, 4.0, '2023-2024春'),
(1, 8, 92, 95, 94, 4.0, '2023-2024春');

-- 学生2 (2023020616 - 李四)
INSERT INTO `scores` (`student_id`, `course_id`, `regular_score`, `final_score`, `score_total`, `grade_point`, `semester`) VALUES
(2, 1, 90, 94, 92, 4.0, '2023-2024秋'),
(2, 2, 82, 87, 85, 3.8, '2023-2024秋'),
(2, 3, 93, 96, 95, 4.0, '2023-2024秋'),
(2, 4, 86, 90, 88, 3.8, '2023-2024春'),
(2, 5, 78, 82, 80, 3.5, '2023-2024春'),
(2, 6, 85, 88, 87, 3.7, '2023-2024春'),
(2, 7, 91, 93, 92, 4.0, '2023-2024春'),
(2, 8, 89, 92, 91, 4.0, '2023-2024春');

-- ==================== 预警规则数据 ====================
INSERT INTO `warning_rules` (`name`, `description`, `condition`, `level`, `status`) VALUES
('单科不及格预警', '单科成绩低于60分', 'score < 60', 'high', 1),
('平均成绩低警', '平均绩点低于3.0', 'avg_gpa < 3.0', 'medium', 1),
('学分不足警告', '获得学分未达标', 'credits < required', 'high', 1);

-- ==================== 预警数据 ====================
-- 学生1的预警：数据库系统成绩偏低
INSERT INTO `academic_warnings` (`student_id`, `rule_id`, `college_id`, `warning_level`, `description`, `status`, `appeal_status`) VALUES
(1, 2, 3, 'medium', '数据库系统成绩偏低（72分），平均绩点需加强', 'pending', 'none'),
(1, 1, 3, 'low', '数据库基础模块成绩不理想', 'pending', 'none');

-- 学生2的预警：学业良好，暂无预警

-- ==================== 帮扶计划 ====================
INSERT INTO `assistance_plans` (`student_id`, `warning_id`, `title`, `description`, `target`, `measures`, `progress_percentage`, `status`) VALUES
(1, 1, '数据库系统学业提升计划', '针对数据库系统成绩较低的情况，制定个性化学习计划', '将数据库系统成绩提高到80分以上，绩点达到3.5', '1.每周额外复习2小时数据库知识\n2.参加教师答疑时间\n3.完成课程设计项目', 45, 'in_progress');

-- ==================== 成绩申诉 ====================
INSERT INTO `score_appeals` (`warning_id`, `student_id`, `reason`, `status`) VALUES
(1, 1, '认为数据库系统期末成绩评分不够公正，希望复查', 'pending');

-- ==================== 选修课信息 ====================
INSERT INTO `enrollments` (`student_id`, `course_id`, `class_id`, `score`, `status`) VALUES
(1, 5, 1, 91, 'completed'),
(1, 6, 1, 73, 'completed'),
(1, 7, 1, NULL, 'ongoing'),
(2, 5, 1, 80, 'completed'),
(2, 6, 1, 87, 'completed'),
(2, 7, 1, NULL, 'ongoing');

-- ==================== 通知数据 ====================
INSERT INTO `notifications` (`user_id`, `title`, `content`, `type`, `warning_id`, `is_read`, `is_deleted`) VALUES
-- 学生1的通知
(1, '学业预警通知', '您的学业成绩有所下降，已触发中等级别预警。建议及时查看预警详情并参加帮扶计划。', 'warning', 1, 0, 0),
(1, '成绩发布通知', '您的《数据库系统》成绩已发布，成绩72分', 'system', NULL, 0, 0),
(1, '帮扶计划更新', '您的帮扶计划已更新，请查看新的学习措施和目标', 'plan_update', NULL, 0, 0),
(1, '选课通知', '本学期选课已开放，请及时登录系统选课', 'system', NULL, 1, 0),
-- 学生2的通知
(2, '学业良好表扬', '恭喜！您本学期学业表现优异，成绩平均绩点3.9，继续保持！', 'system', NULL, 0, 0),
(2, '成绩发布通知', '您的所有课程成绩已发布，平均分92分，',  'system', NULL, 0, 0);

-- ==================== 沟通日志 ====================
INSERT INTO `communication_logs` (`teacher_id`, `student_id`, `warning_id`, `content`, `category`) VALUES
(3, 1, 1, '讨论数据库系统学习进度和困难，制定改进措施', '预警处理'),
(3, 2, NULL, '讨论课程项目进展和下一步选修方向', '选修建议');

-- ==================== 学生反馈 ====================
INSERT INTO `feedbacks` (`teacher_id`, `student_id`, `category`, `content`, `rating`, `reply_content`) VALUES
(3, 1, '教学方法', '课程内容较难，建议增加更多实例讲解和案例分析', 4, '感谢建议，下学期会增加更多实践案例'),
(3, 2, '教学质量', '老师讲课生动有趣，知识点讲解清楚，推荐同学听课', 5, '谢谢你的认可，这是我的教学目标');

-- ==================== 毕业要求 ====================
INSERT INTO `elective_requirements` (`grade`, `required_credits`) VALUES
(2021, 12.0),
(2022, 12.0),
(2023, 12.0),
(2024, 12.0);

-- ==================== 安全日志 ====================
INSERT INTO `security_log` (`user_id`, `ip_address`, `login_time`, `action`) VALUES
(1, '192.168.1.100', NOW(), '学生登录'),
(2, '192.168.1.101', NOW(), '学生登录'),
(3, '192.168.1.102', NOW(), '教师登录'),
(4, '192.168.1.103', NOW(), '辅导员登录'),
(5, '192.168.1.104', NOW(), '管理员登录');

-- ==================== 审计日志 ====================
INSERT INTO `audit_logs` (`user_id`, `action_type`, `entity_type`, `entity_id`, `details`) VALUES
(3, 'create', 'scores', 1, '批量录入成绩'),
(4, 'create', 'academic_warnings', 1, '系统自动生成预警'),
(3, 'create', 'assistance_plans', 1, '创建帮扶计划'),
(4, 'update', 'academic_warnings', 1, '处理预警'),
(5, 'view', 'students', 1, '管理员查看学生数据');

-- 数据初始化完成
SELECT '✓ 业务数据初始化成功' AS status;
