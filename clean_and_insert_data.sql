USE academic_warning_system;

-- 删除所有表中的数据
DELETE FROM audit_logs;
DELETE FROM security_log;
DELETE FROM notifications;
DELETE FROM elective_requirements;
DELETE FROM enrollments;
DELETE FROM feedbacks;
DELETE FROM communication_logs;
DELETE FROM score_logs;
DELETE FROM assistance_plans;
DELETE FROM academic_warnings;
DELETE FROM warning_rules;
DELETE FROM scores;
DELETE FROM courses;
DELETE FROM admin_profile;
DELETE FROM counselor_profile;
DELETE FROM teacher_profile;
DELETE FROM student_profile;
DELETE FROM users;
DELETE FROM classes;
DELETE FROM majors;
DELETE FROM colleges;

-- 重置自增ID
ALTER TABLE colleges AUTO_INCREMENT = 1;
ALTER TABLE majors AUTO_INCREMENT = 1;
ALTER TABLE classes AUTO_INCREMENT = 1;
ALTER TABLE users AUTO_INCREMENT = 1;
ALTER TABLE student_profile AUTO_INCREMENT = 1;
ALTER TABLE teacher_profile AUTO_INCREMENT = 1;
ALTER TABLE counselor_profile AUTO_INCREMENT = 1;
ALTER TABLE admin_profile AUTO_INCREMENT = 1;
ALTER TABLE courses AUTO_INCREMENT = 1;
ALTER TABLE scores AUTO_INCREMENT = 1;
ALTER TABLE warning_rules AUTO_INCREMENT = 1;
ALTER TABLE academic_warnings AUTO_INCREMENT = 1;
ALTER TABLE assistance_plans AUTO_INCREMENT = 1;
ALTER TABLE score_logs AUTO_INCREMENT = 1;
ALTER TABLE communication_logs AUTO_INCREMENT = 1;
ALTER TABLE feedbacks AUTO_INCREMENT = 1;
ALTER TABLE enrollments AUTO_INCREMENT = 1;
ALTER TABLE elective_requirements AUTO_INCREMENT = 1;
ALTER TABLE notifications AUTO_INCREMENT = 1;
ALTER TABLE security_log AUTO_INCREMENT = 1;
ALTER TABLE audit_logs AUTO_INCREMENT = 1;

-- 插入初始学院数据
INSERT INTO `colleges` (`id`, `name`) VALUES
(1, 'College of Information Engineering'),
(2, 'College of AI'),
(3, 'College of Computer Science'),
(4, 'College of Electronic Engineering');

-- 插入初始专业数据
INSERT INTO `majors` (`id`, `code`, `name`, `college_id`) VALUES
(1, '01', 'Computer Science', 3),
(2, '02', 'Software Engineering', 3),
(3, '03', 'Network Engineering', 1),
(4, '04', 'Artificial Intelligence', 2),
(5, '05', 'Machine Learning', 2);

-- 插入班级数据
INSERT INTO `classes` (`id`, `name`, `college_id`) VALUES
(1, '2021 Computer Class 1', 3),
(2, '2021 Computer Class 2', 3),
(3, '2022 Software Engineering Class', 3);

-- 插入用户数据
INSERT INTO `users` (`id`, `username`, `password`, `email`, `phone`, `role`, `status`) VALUES
(1, 'student001', '$2a$10$yKBwVGJjJ0nN.L1K8P1ZluqJ2uN6vK3N.X8P2L5M4N3O2P1Q0R9S8', 'student001@example.com', '13800138000', 1, 1),
(2, '2023020616', '$2a$10$4BX1wL9yYJ6Y7N2K8Q3R4S5T6U7V8W9X0Y1Z2A3B4C5D6E7F8G9H0', '2023020616@example.com', '13800138010', 1, 1),
(3, 'teacher001', '$2a$10$yKBwVGJjJ0nN.L1K8P1ZluqJ2uN6vK3N.X8P2L5M4N3O2P1Q0R9S8', 'teacher001@example.com', '13800138001', 2, 1),
(4, 'counselor001', '$2a$10$yKBwVGJjJ0nN.L1K8P1ZluqJ2uN6vK3N.X8P2L5M4N3O2P1Q0R9S8', 'counselor001@example.com', '13800138002', 4, 1),
(5, 'admin001', '$2a$10$yKBwVGJjJ0nN.L1K8P1ZluqJ2uN6vK3N.X8P2L5M4N3O2P1Q0R9S8', 'admin001@example.com', '13800138003', 3, 1),
(6, '20210003', '$2a$10$yKBwVGJjJ0nN.L1K8P1ZluqJ2uN6vK3N.X8P2L5M4N3O2P1Q0R9S8', 'student003@example.com', '13800138003', 1, 1),
(7, '20210004', '$2a$10$yKBwVGJjJ0nN.L1K8P1ZluqJ2uN6vK3N.X8P2L5M4N3O2P1Q0R9S8', 'student004@example.com', '13800138004', 1, 1),
(8, '20210005', '$2a$10$yKBwVGJjJ0nN.L1K8P1ZluqJ2uN6vK3N.X8P2L5M4N3O2P1Q0R9S8', 'student005@example.com', '13800138005', 1, 1),
(9, 'teacher002', '$2a$10$yKBwVGJjJ0nN.L1K8P1ZluqJ2uN6vK3N.X8P2L5M4N3O2P1Q0R9S8', 'teacher002@example.com', '13800138011', 2, 1);

-- 插入学生档案
INSERT INTO `student_profile` (`id`, `user_id`, `student_id`, `name`, `college_id`, `major_id`, `class_id`) VALUES
(1, 1, '20210001', 'Zhang San', 3, 1, 1),
(2, 2, '2023020616', 'Li Si', 3, 1, 1),
(3, 6, '20210003', 'Wang Wu', 3, 2, 2),
(4, 7, '20210004', 'Zhao Liu', 3, 1, 1),
(5, 8, '20210005', 'Sun Qi', 3, 2, 2);

-- 插入教师档案
INSERT INTO `teacher_profile` (`id`, `user_id`, `college_id`, `title`) VALUES
(1, 3, 3, 'Professor'),
(2, 9, 3, 'Associate Professor');

-- 插入辅导员档案
INSERT INTO `counselor_profile` (`id`, `user_id`, `college_id`) VALUES
(1, 4, 3);

-- 插入管理员档案
INSERT INTO `admin_profile` (`id`, `user_id`, `department`) VALUES
(1, 5, 'Academic Affairs Office');

-- 插入课程数据
INSERT INTO `courses` (`id`, `name`, `code`, `credits`, `score_template`, `type`) VALUES
(1, 'Calculus', 'MATH001', 4, 'exam', 'Required'),
(2, 'Java Programming', 'CS001', 4, 'exam', 'Required'),
(3, 'Data Structures', 'CS002', 3, 'exam', 'Required'),
(4, 'Database Principles', 'CS003', 3, 'exam', 'Required'),
(5, 'Linear Algebra', 'MATH002', 3, 'exam', 'Required'),
(6, 'Python Programming', 'CS004', 3, 'exam', 'Elective'),
(7, 'Machine Learning Basics', 'AI001', 3, 'exam', 'Elective'),
(8, 'Natural Language Processing', 'AI002', 3, 'exam', 'Elective');

-- 插入成绩数据（第一个学生）
INSERT INTO `scores` (`id`, `student_id`, `course_id`, `semester`, `regular_score`, `final_score`, `score_total`, `grade_point`) VALUES
(1, 1, 1, '2023-2024Fall', 75, 65, 68.0, 3.0),
(2, 1, 2, '2023-2024Fall', 85, 82, 82.6, 3.5),
(3, 1, 3, '2023-2024Fall', 70, 62, 64.4, 2.5),
(4, 1, 4, '2023-2024Fall', 88, 90, 89.4, 4.0),
(5, 1, 5, '2023-2024Fall', 55, 50, 51.5, 1.0),
(6, 1, 1, '2024-2025Spring', 80, 75, 76.5, 3.2),
(7, 1, 2, '2024-2025Spring', 90, 88, 88.6, 4.0),
(8, 1, 3, '2024-2025Spring', 78, 75, 75.9, 3.5);

-- 插入成绩数据（第二个学生）
INSERT INTO `scores` (`id`, `student_id`, `course_id`, `semester`, `regular_score`, `final_score`, `score_total`, `grade_point`) VALUES
(9, 2, 1, '2023-2024Fall', 88, 92, 90.8, 4.0),
(10, 2, 2, '2023-2024Fall', 90, 88, 88.6, 4.0),
(11, 2, 3, '2023-2024Fall', 85, 80, 81.5, 3.5),
(12, 2, 4, '2023-2024Fall', 92, 95, 94.1, 4.0),
(13, 2, 5, '2023-2024Fall', 88, 85, 85.9, 3.5),
(14, 2, 6, '2024-2025Spring', 85, 80, 81.5, 3.5);

-- 插入成绩数据（第三个学生 - 有挂科）
INSERT INTO `scores` (`id`, `student_id`, `course_id`, `semester`, `regular_score`, `final_score`, `score_total`, `grade_point`) VALUES
(15, 3, 1, '2023-2024Fall', 45, 40, 41.5, 0.0),
(16, 3, 2, '2023-2024Fall', 50, 45, 46.5, 0.5),
(17, 3, 3, '2023-2024Fall', 60, 58, 58.4, 1.5),
(18, 3, 4, '2023-2024Fall', 75, 72, 72.9, 3.0),
(19, 3, 5, '2023-2024Fall', 35, 30, 31.5, 0.0);

-- 插入预警规则
INSERT INTO `warning_rules` (`id`, `name`, `description`, `condition`, `level`, `status`) VALUES
(1, 'Low Risk Warning', 'Failed courses <= 3', 'failed_count <= 3', 'low', 1),
(2, 'Medium Risk Warning', 'Failed courses 3-5', '3 < failed_count <= 5', 'medium', 1),
(3, 'High Risk Warning', 'Failed courses > 5', 'failed_count > 5', 'high', 1);

-- 插入学业预警数据
INSERT INTO `academic_warnings` (`id`, `student_id`, `rule_id`, `college_id`, `warning_level`, `description`, `status`, `appeal_status`) VALUES
(1, 1, 1, 3, 'low', 'Linear Algebra score 51.5, need remedial course', 'pending', 'none'),
(2, 1, 1, 3, 'low', 'Calculus score 68, need strengthening', 'confirmed', 'none'),
(3, 3, 3, 3, 'high', 'Calculus 41.5, high risk warning', 'pending', 'none'),
(4, 3, 3, 3, 'high', 'Java Programming 46.5, high risk warning', 'pending', 'none');

-- 插入帮扶计划
INSERT INTO `assistance_plans` (`id`, `student_id`, `warning_id`, `title`, `description`, `target`, `measures`, `progress_percentage`, `status`) VALUES
(1, 1, 1, 'Linear Algebra Remedial Plan', 'Remediation for weak areas', 'Improve to passing score', 'Weekly tutoring + online practice', 60, 'in_progress'),
(2, 1, 2, 'Calculus Strengthening Plan', 'Consolidate fundamentals', 'Improve to 80+', 'Study group + Q&A', 45, 'in_progress'),
(3, 3, 3, 'Math Foundation Reconstruction', 'Rebuild math basics', 'Reach passing standard (60)', 'One-on-one tutoring twice weekly', 30, 'initiated'),
(4, 3, 4, 'Programming Skills Development', 'Learn Java from basics', 'Pass supplementary exam', 'Summer training camp + group study', 20, 'initiated');

-- 插入成绩修改日志
INSERT INTO `score_logs` (`id`, `score_id`, `modified_by`, `old_score`, `new_score`, `reason`) VALUES
(1, 1, 3, 65.0, 68.0, 'Calculation error corrected'),
(2, 5, 3, 50.0, 51.5, 'Grading error found in review');

-- 插入沟通记录
INSERT INTO `communication_logs` (`id`, `teacher_id`, `student_id`, `warning_id`, `content`, `category`) VALUES
(1, 3, 1, 1, 'Student lacks understanding of linear algebra theory', 'Warning handling'),
(2, 3, 1, 2, 'Main difficulty is in calculus operations', 'Warning handling'),
(3, 3, 1, NULL, 'Student interested in machine learning', 'Course recommendation'),
(4, 3, 3, 3, 'Family reasons caused absences, good attitude', 'Warning handling');

-- 插入学生反馈
INSERT INTO `feedbacks` (`id`, `teacher_id`, `student_id`, `content`, `category`, `rating`, `reply_content`) VALUES
(1, 3, 1, 'Clear explanation but needs more examples', 'Course Content', 4, 'Will add more practical cases'),
(2, 3, 1, 'Pace is too fast', 'Teaching Method', 3, 'Will provide recordings'),
(3, 3, 2, 'Excellent teaching and timely Q&A', 'Overall Evaluation', 5, 'Thank you for feedback'),
(4, 3, 3, 'Need more practical exercises', 'Course Content', 3, 'Will add project modules');

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
(1, 1, 'Academic Warning', 'Linear Algebra score 51.5', 'warning', 'unread'),
(2, 1, 'Plan Update', 'Linear Algebra plan progress 60%', 'plan_update', 'read'),
(3, 2, 'Course Recommendation', 'Recommended for Machine Learning', 'recommendation', 'unread'),
(4, 3, 'New Warnings', '3 new warnings to handle', 'warning', 'unread');

-- 插入安全日志
INSERT INTO `security_log` (`id`, `user_id`, `ip_address`, `login_time`, `action`) VALUES
(1, 1, '192.168.1.100', NOW(), 'Student Login'),
(2, 2, '192.168.1.101', NOW(), 'Student Login'),
(3, 3, '192.168.1.102', NOW(), 'Teacher Login'),
(4, 4, '192.168.1.103', NOW(), 'Counselor Login'),
(5, 5, '192.168.1.104', NOW(), 'Admin Login');

-- 插入审计日志
INSERT INTO `audit_logs` (`id`, `user_id`, `action_type`, `entity_type`, `entity_id`, `details`) VALUES
(1, 3, 'update', 'scores', 1, 'Updated student Calculus score'),
(2, 3, 'update', 'scores', 5, 'Updated student Linear Algebra score'),
(3, 3, 'create', 'communication_logs', 1, 'Added communication record'),
(4, 4, 'update', 'assistance_plans', 1, 'Updated plan progress to 60%'),
(5, 5, 'create', 'warning_rules', 1, 'Created new warning rule');

COMMIT;
