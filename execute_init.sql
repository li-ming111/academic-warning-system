-- 学业预警系统业务数据初始化
USE `academic_warning_system`;

-- 班级数据
INSERT INTO `classes` (`name`, `college_id`, `teacher_id`, `counselor_id`) VALUES
('计科2301班', 3, 3, 4),
('计科2302班', 3, 3, 4),
('信工2301班', 1, 3, 4);

-- 课程数据
INSERT INTO `courses` (`code`, `name`, `credits`, `type`, `score_template`) VALUES
('CS101', '数据结构', 4.0, '必修', 'exam'),
('CS102', '算法设计', 4.0, '必修', 'exam'),
('CS201', 'Java程序设计', 3.5, '必修', 'exam'),
('CS301', '数据库系统', 4.0, '必修', 'exam'),
('CS401', '软件工程', 3.0, '选修', 'project'),
('CS402', '人工智能基础', 3.5, '选修', 'exam'),
('CS403', '云计算技术', 3.0, '选修', 'project'),
('CS404', '机器学习', 4.0, '选修', 'exam');

-- 成绩数据
INSERT INTO `scores` (`student_id`, `course_id`, `course_score`, `continuous_score`, `final_exam_score`, `semester`, `academic_year`) VALUES
(1, 1, 85, 80, 88, '秋季', '2023-2024'),
(1, 2, 78, 75, 80, '秋季', '2023-2024'),
(1, 3, 88, 85, 90, '秋季', '2023-2024'),
(1, 4, 72, 68, 74, '春季', '2023-2024'),
(1, 5, 91, 90, 92, '春季', '2023-2024'),
(2, 1, 92, 90, 94, '秋季', '2023-2024'),
(2, 2, 85, 82, 87, '秋季', '2023-2024'),
(2, 3, 95, 93, 96, '秋季', '2023-2024'),
(2, 4, 88, 86, 90, '春季', '2023-2024'),
(2, 5, 80, 78, 82, '春季', '2023-2024');

-- 预警数据
INSERT INTO `academic_warnings` (`student_id`, `course_id`, `warning_level`, `warning_reason`) VALUES
(1, 4, 'yellow', '数据库系统成绩偏低，需要加强复习');

-- 帮扶计划
INSERT INTO `assistance_plans` (`student_id`, `subject`, `description`, `target_gpa`, `status`) VALUES
(1, '数据库系统加强', '针对数据库系统成绩较低的情况，制定个性化学习计划', 3.5, 'ongoing');

-- 成绩申诉
INSERT INTO `score_appeals` (`student_id`, `course_id`, `appeal_reason`, `status`) VALUES
(1, 4, '认为阅卷有误，数据库系统期末成绩应该更高', 'pending');

-- 选修课信息
INSERT INTO `enrollments` (`student_id`, `course_id`, `class_id`, `score`, `status`) VALUES
(1, 5, 1, 91, 'completed'),
(1, 6, 1, NULL, 'ongoing'),
(2, 6, 1, 88, 'completed'),
(2, 7, 1, NULL, 'ongoing');

-- 通知数据
INSERT INTO `notifications` (`user_id`, `title`, `content`, `type`, `is_read`) VALUES
(1, '成绩发布通知', '您的《数据库系统》成绩已发布，成绩72分', 'score_release', 0),
(1, '预警提醒', '您的《数据库系统》课程成绩存在预警风险，建议加强学习', 'warning', 0),
(1, '选课通知', '本学期选课已开放，请及时登录系统选课', 'system', 0),
(2, '学业良好', '恭喜您本学期学业表现优异，保持进步！', 'system', 0);

-- 消息数据
INSERT INTO `messages` (`sender_id`, `receiver_id`, `content`, `is_read`) VALUES
(3, 1, '李四同学，您的数据库系统成绩有所下降，请见我办公室讨论学习计划', 0),
(1, 3, '感谢老师的关心，我会加强复习，争取下次考试取得更好的成绩', 1),
(4, 1, '张三同学，您这学期表现不错，继续加油！', 0);

-- 学生反馈
INSERT INTO `feedbacks` (`teacher_id`, `student_id`, `category`, `content`, `rating`) VALUES
(3, 2, '教学质量', '老师讲课生动有趣，知识点讲解清楚，推荐同学听课', 5),
(3, 1, '教学方法', '课程内容较难，建议增加更多实例讲解', 4);

-- 沟通记录
INSERT INTO `communication_logs` (`teacher_id`, `student_id`, `content`, `communication_type`) VALUES
(3, 1, '讨论数据库系统学习进度和困难', 'office_visit'),
(3, 2, '讨论课程项目进展', 'email');

-- 毕业要求
INSERT INTO `elective_requirements` (`grade`, `required_credits`) VALUES
(2021, 12.0),
(2022, 12.0),
(2023, 12.0),
(2024, 12.0);

-- 安全日志
INSERT INTO `security_log` (`user_id`, `ip_address`, `action`) VALUES
(1, '192.168.1.100', 'login'),
(2, '192.168.1.101', 'login'),
(3, '192.168.1.102', 'login'),
(4, '192.168.1.103', 'login');

-- 审计日志
INSERT INTO `audit_logs` (`user_id`, `action_type`, `entity_type`, `entity_id`, `details`) VALUES
(3, 'create', 'scores', 1, '批量录入成绩'),
(4, 'create', 'warnings', 1, '系统自动生成预警'),
(5, 'create', 'assistance_plans', 1, '创建帮扶计划'),
(4, 'update', 'warnings', 1, '处理预警');

-- 完成
SELECT COUNT(*) as '班级数' FROM classes;
SELECT COUNT(*) as '课程数' FROM courses;
SELECT COUNT(*) as '成绩数' FROM scores;
