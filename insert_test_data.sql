-- 学业预警系统 - 测试数据初始化脚本
USE academic_warning_system;

-- 插入初始学院数据
INSERT INTO `colleges` (`id`, `name`) VALUES
(1, '信息工程学院'),
(2, '人工智能学院'),
(3, '计算机科学学院'),
(4, '电子工程学院');

-- 插入初始专业数据
INSERT INTO `majors` (`id`, `code`, `name`, `college_id`) VALUES
(1, '01', '计算机科学与技术', 3),
(2, '02', '软件工程', 3),
(3, '03', '网络工程', 1),
(4, '04', '人工智能', 2),
(5, '05', '机器学习', 2);

-- 插入班级数据
INSERT INTO `classes` (`id`, `name`, `college_id`) VALUES
(1, '2021级计算机1班', 3),
(2, '2021级计算机2班', 3),
(3, '2022级软件工程班', 3);

-- 插入用户数据
INSERT INTO `users` (`id`, `username`, `password`, `email`, `phone`, `role`, `status`) VALUES
(1, 'student001', '$2a$10$yKBwVGJjJ0nN.L1K8P1ZluqJ2uN6vK3N.X8P2L5M4N3O2P1Q0R9S8', 'student001@example.com', '13800138000', 1, 1),
(2, '2023020616', '$2a$10$4BX1wL9yYJ6Y7N2K8Q3R4S5T6U7V8W9X0Y1Z2A3B4C5D6E7F8G9H0', '2023020616@example.com', '13800138010', 1, 1),
(3, 'teacher001', '$2a$10$yKBwVGJjJ0nN.L1K8P1ZluqJ2uN6vK3N.X8P2L5M4N3O2P1Q0R9S8', 'teacher001@example.com', '13800138001', 2, 1),
(4, 'counselor001', '$2a$10$yKBwVGJjJ0nN.L1K8P1ZluqJ2uN6vK3N.X8P2L5M4N3O2P1Q0R9S8', 'counselor001@example.com', '13800138002', 4, 1),
(5, 'admin001', '$2a$10$yKBwVGJjJ0nN.L1K8P1ZluqJ2uN6vK3N.X8P2L5M4N3O2P1Q0R9S8', 'admin001@example.com', '13800138003', 3, 1);

-- 插入学生档案
INSERT INTO `student_profile` (`id`, `user_id`, `student_id`, `name`, `college_id`, `major_id`, `class_id`) VALUES
(1, 1, '20210001', '张三', 3, 1, 1),
(2, 2, '2023020616', '李四', 3, 1, 1),
(3, 6, '20210003', '王五', 3, 2, 2),
(4, 7, '20210004', '赵六', 3, 1, 1),
(5, 8, '20210005', '孙七', 3, 2, 2);

-- 插入缺失的学生用户
INSERT INTO `users` (`id`, `username`, `password`, `email`, `phone`, `role`, `status`) VALUES
(6, '20210003', '$2a$10$yKBwVGJjJ0nN.L1K8P1ZluqJ2uN6vK3N.X8P2L5M4N3O2P1Q0R9S8', 'student003@example.com', '13800138003', 1, 1),
(7, '20210004', '$2a$10$yKBwVGJjJ0nN.L1K8P1ZluqJ2uN6vK3N.X8P2L5M4N3O2P1Q0R9S8', 'student004@example.com', '13800138004', 1, 1),
(8, '20210005', '$2a$10$yKBwVGJjJ0nN.L1K8P1ZluqJ2uN6vK3N.X8P2L5M4N3O2P1Q0R9S8', 'student005@example.com', '13800138005', 1, 1);

-- 插入教师档案
INSERT INTO `teacher_profile` (`id`, `user_id`, `college_id`, `title`) VALUES
(1, 3, 3, '教授'),
(2, 9, 3, '副教授');

-- 插入缺失的教师用户
INSERT INTO `users` (`id`, `username`, `password`, `email`, `phone`, `role`, `status`) VALUES
(9, 'teacher002', '$2a$10$yKBwVGJjJ0nN.L1K8P1ZluqJ2uN6vK3N.X8P2L5M4N3O2P1Q0R9S8', 'teacher002@example.com', '13800138011', 2, 1);

-- 插入辅导员档案
INSERT INTO `counselor_profile` (`id`, `user_id`, `college_id`) VALUES
(1, 4, 3);

-- 插入管理员档案
INSERT INTO `admin_profile` (`id`, `user_id`, `department`) VALUES
(1, 5, '教务处');

-- 插入课程数据
INSERT INTO `courses` (`id`, `course_name`, `course_code`, `credit`, `score_template`, `type`) VALUES
(1, '高等数学', 'MATH001', 4, 'exam', '必修'),
(2, 'Java编程', 'CS001', 4, 'exam', '必修'),
(3, '数据结构', 'CS002', 3, 'exam', '必修'),
(4, '数据库原理', 'CS003', 3, 'exam', '必修'),
(5, '线性代数', 'MATH002', 3, 'exam', '必修'),
(6, 'Python编程', 'CS004', 3, 'exam', '选修'),
(7, '机器学习基础', 'AI001', 3, 'exam', '选修'),
(8, '自然语言处理', 'AI002', 3, 'exam', '选修');

-- 插入成绩数据（第一个学生）
INSERT INTO `scores` (`id`, `student_id`, `course_id`, `semester`, `regular_score`, `final_score`, `score_total`, `grade_point`) VALUES
(1, 1, 1, '2023-2024秋', 75, 65, 68.0, 3.0),
(2, 1, 2, '2023-2024秋', 85, 82, 82.6, 3.5),
(3, 1, 3, '2023-2024秋', 70, 62, 64.4, 2.5),
(4, 1, 4, '2023-2024秋', 88, 90, 89.4, 4.0),
(5, 1, 5, '2023-2024秋', 55, 50, 51.5, 1.0),
(6, 1, 1, '2024-2025春', 80, 75, 76.5, 3.2),
(7, 1, 2, '2024-2025春', 90, 88, 88.6, 4.0),
(8, 1, 3, '2024-2025春', 78, 75, 75.9, 3.5);

-- 插入成绩数据（第二个学生）
INSERT INTO `scores` (`id`, `student_id`, `course_id`, `semester`, `regular_score`, `final_score`, `score_total`, `grade_point`) VALUES
(9, 2, 1, '2023-2024秋', 88, 92, 90.8, 4.0),
(10, 2, 2, '2023-2024秋', 90, 88, 88.6, 4.0),
(11, 2, 3, '2023-2024秋', 85, 80, 81.5, 3.5),
(12, 2, 4, '2023-2024秋', 92, 95, 94.1, 4.0),
(13, 2, 5, '2023-2024秋', 88, 85, 85.9, 3.5),
(14, 2, 6, '2024-2025春', 85, 80, 81.5, 3.5);

-- 插入成绩数据（第三个学生 - 有挂科）
INSERT INTO `scores` (`id`, `student_id`, `course_id`, `semester`, `regular_score`, `final_score`, `score_total`, `grade_point`) VALUES
(15, 3, 1, '2023-2024秋', 45, 40, 41.5, 0.0),
(16, 3, 2, '2023-2024秋', 50, 45, 46.5, 0.5),
(17, 3, 3, '2023-2024秋', 60, 58, 58.4, 1.5),
(18, 3, 4, '2023-2024秋', 75, 72, 72.9, 3.0),
(19, 3, 5, '2023-2024秋', 35, 30, 31.5, 0.0);

-- 插入预警规则
INSERT INTO `warning_rules` (`id`, `name`, `description`, `condition`, `level`, `status`) VALUES
(1, '低急预警', '指学期末挂科数量≤3科', 'failed_count <= 3', 'low', 1),
(2, '中急预警', '指学期末挂科数量3-5科', '3 < failed_count <= 5', 'medium', 1),
(3, '高急预警', '指学期末挂科数量>5科', 'failed_count > 5', 'high', 1);

-- 插入学业预警数据
INSERT INTO `academic_warnings` (`id`, `student_id`, `rule_id`, `college_id`, `warning_level`, `description`, `status`, `appeal_status`) VALUES
(1, 1, 1, 3, 'low', '线性代数成绩51.5分，需要补考和加课辅导', 'pending', 'none'),
(2, 1, 1, 3, 'low', '高等数学成绩68分，需要加强复习', 'confirmed', 'none'),
(3, 3, 3, 3, 'high', '高等数学41.5分，已触发高危预警，请辅导员介入', 'pending', 'none'),
(4, 3, 3, 3, 'high', 'Java编程46.5分，已触发高危预警，需制定帮扶计划', 'pending', 'none');

-- 插入帮扶计划
INSERT INTO `assistance_plans` (`id`, `student_id`, `warning_id`, `title`, `description`, `target`, `measures`, `progress_percentage`, `status`) VALUES
(1, 1, 1, '线性代数补救计划', '针对线性代数薄弱环节的补救', '提升线性代数成绩到及格线以上', '每周三下午专项辅导 + 在线练习', 60, 'in_progress'),
(2, 1, 2, '高等数学强化计划', '巩固高等数学基础知识', '提升高等数学成绩到80分以上', '参加学习小组 + 定期答疑', 45, 'in_progress'),
(3, 3, 3, '数学基础重建计划', '系统重建高等数学基础', '达到及格标准（60分）', '一对一辅导每周两次 + 心理疏导', 30, 'initiated'),
(4, 3, 4, '编程能力培养计划', '从零开始学习Java编程', '掌握基础编程知识并通过补考', '参加暑期集训班 + 小组学习', 20, 'initiated');

-- 插入成绩修改日志
INSERT INTO `score_logs` (`id`, `score_id`, `modified_by`, `old_score`, `new_score`, `reason`) VALUES
(1, 1, 3, 65.0, 68.0, '学生提交证明存在计算错误，确认后修正'),
(2, 5, 3, 50.0, 51.5, '卷面复查后发现批改有误');

-- 插入沟通记录
INSERT INTO `communication_logs` (`id`, `teacher_id`, `student_id`, `warning_id`, `content`, `category`) VALUES
(1, 3, 1, 1, '学生反映线性代数理论部分掌握不足，建议加强向量空间的理解', '预警处理'),
(2, 3, 1, 2, '与学生沟通后得知主要困难在积分运算，制定了针对性复习计划', '预警处理'),
(3, 3, 1, NULL, '学生对机器学习感兴趣，推荐修读《机器学习基础》选修课', '选修建议'),
(4, 3, 3, 3, '家庭原因导致上学期缺课较多，该生学习态度较好，需要心理疏导和学业支持', '预警处理');

-- 插入学生反馈
INSERT INTO `feedbacks` (`id`, `teacher_id`, `student_id`, `content`, `category`, `rating`, `reply_content`) VALUES
(1, 3, 1, '高等数学课程内容讲解清晰，但例题数量较少，希望增加实际应用案例', '课程内容', 4, '感谢反馈，已在后续课程中加入更多工程应用案例'),
(2, 3, 1, '上课速度有些快，建议放慢节奏或提供课程录视频', '教学方法', 3, '已调整，后续会提供录播资源供复习'),
(3, 3, 2, '教学内容充分，讲解清晰，课后答疑及时', '整体评价', 5, '感谢您的认可，会继续提高教学质量'),
(4, 3, 3, '希望能有更多的实战练习和项目作业', '课程内容', 3, '已计划在下个学期增加项目实战模块');

-- 插入选修课报名
INSERT INTO `enrollments` (`id`, `student_id`, `course_id`, `class_id`, `score`, `status`) VALUES
(1, 1, 6, 1, 82, 'completed'),
(2, 1, 7, 1, NULL, 'ongoing'),
(3, 2, 6, 1, 88, 'completed'),
(4, 2, 7, 1, 85, 'completed'),
(5, 3, 6, 2, 65, 'completed');

-- 插入毕业要求
INSERT INTO `elective_requirements` (`id`, `grade`, `required_credits`) VALUES
(1, 2021, 8),
(2, 2022, 8),
(3, 2023, 8);

-- 插入通知
INSERT INTO `notifications` (`id`, `user_id`, `title`, `content`, `type`, `status`) VALUES
(1, 1, '学业预警通知', '您有一条新的学业预警：线性代数成绩51.5分', 'warning', 'unread'),
(2, 1, '帮扶计划更新', '您的线性代数补救计划已更新进度为60%', 'plan_update', 'read'),
(3, 2, '选课建议', '根据您的学习成绩，推荐您修读《机器学习基础》课程', 'recommendation', 'unread'),
(4, 3, '新预警提醒', '您管理的班级有3个新预警需要处理', 'warning', 'unread');

-- 插入安全日志
INSERT INTO `security_log` (`id`, `user_id`, `ip_address`, `login_time`, `action`) VALUES
(1, 1, '192.168.1.100', NOW(), '学生登录'),
(2, 2, '192.168.1.101', NOW(), '学生登录'),
(3, 3, '192.168.1.102', NOW(), '教师登录'),
(4, 4, '192.168.1.103', NOW(), '辅导员登录'),
(5, 5, '192.168.1.104', NOW(), '管理员登录');

-- 插入审计日志
INSERT INTO `audit_logs` (`id`, `user_id`, `action_type`, `entity_type`, `entity_id`, `details`) VALUES
(1, 3, 'update', 'scores', 1, '修改学生张三的高等数学成绩'),
(2, 3, 'update', 'scores', 5, '修改学生张三的线性代数成绩'),
(3, 3, 'create', 'communication_logs', 1, '添加与学生张三的沟通记录'),
(4, 4, 'update', 'assistance_plans', 1, '更新帮扶计划进度到60%'),
(5, 5, 'create', 'warning_rules', 1, '创建新的预警规则');

COMMIT;
