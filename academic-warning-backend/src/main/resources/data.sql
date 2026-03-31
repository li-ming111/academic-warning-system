USE `academic_warning_system`;

-- Insert courses
INSERT IGNORE INTO `courses` (`code`, `name`, `credits`, `type`, `score_template`) VALUES
('CS101', 'Data Structures', 4.0, 'Required', 'exam'),
('CS102', 'Algorithm Design', 4.0, 'Required', 'exam'),
('CS201', 'Java Programming', 3.5, 'Required', 'exam'),
('CS301', 'Database Systems', 4.0, 'Required', 'exam'),
('CS401', 'Software Engineering', 3.0, 'Elective', 'project'),
('CS402', 'AI Foundations', 3.5, 'Elective', 'exam');

-- Insert warning rules
INSERT IGNORE INTO `warning_rules` (`name`, `description`, `rule_condition`, `level`, `status`) VALUES
('Low Single Score', 'Single course score below 60', 'score < 60', 'high', 1),
('Low Average GPA', 'Average GPA below 3.0', 'avg_gpa < 3.0', 'medium', 1),
('Insufficient Credits', 'Credits acquired below requirement', 'credits < required', 'high', 1);

-- Insert student scores - Student 1 (2023020616 - excellent performance)
INSERT IGNORE INTO `scores` (`student_id`, `course_id`, `semester`, `regular_score`, `final_score`, `score_total`, `grade_point`) VALUES
(1, 1, '2023-2024Fall', 90, 94, 92, 4.0),
(1, 2, '2023-2024Fall', 82, 87, 85, 3.8),
(1, 3, '2023-2024Fall', 93, 96, 95, 4.0),
(1, 4, '2023-2024Spring', 86, 90, 88, 3.8),
(1, 5, '2023-2024Spring', 78, 82, 80, 3.5),
(1, 6, '2023-2024Spring', 85, 88, 87, 3.7);

-- Insert student scores - Student 2 (20210001 - needs improvement)
INSERT IGNORE INTO `scores` (`student_id`, `course_id`, `semester`, `regular_score`, `final_score`, `score_total`, `grade_point`) VALUES
(2, 1, '2023-2024Fall', 80, 88, 85, 3.5),
(2, 2, '2023-2024Fall', 75, 80, 78, 3.2),
(2, 3, '2023-2024Fall', 85, 90, 88, 3.8),
(2, 4, '2023-2024Spring', 68, 74, 72, 3.0),
(2, 5, '2023-2024Spring', 90, 92, 91, 4.0),
(2, 6, '2023-2024Spring', 70, 75, 73, 3.0);

-- Insert academic warnings for Student 2
INSERT IGNORE INTO `academic_warnings` (`student_id`, `rule_id`, `college_id`, `warning_level`, `description`, `status`, `appeal_status`) VALUES
(2, 2, 1, 'medium', 'Database Systems score is low (72), GPA needs improvement', 'pending', 'none'),
(2, 1, 1, 'low', 'Database Systems score below expectation', 'pending', 'none');

-- Insert assistance plans for Student 2
INSERT IGNORE INTO `assistance_plans` (`student_id`, `warning_id`, `title`, `description`, `target`, `measures`, `progress_percentage`, `status`) VALUES
(2, 1, 'Database Study Enhancement Plan', 'Personalized study plan for low database score', 'Improve Database score above 80 and GPA to 3.5', 'Weekly 2-hour study sessions plus office hours and project completion', 45, 'in_progress');

-- Insert score appeals for Student 2
INSERT IGNORE INTO `score_appeals` (`warning_id`, `student_id`, `reason`, `status`) VALUES
(1, 2, 'Request review of Database Systems final exam scoring', 'pending');

-- Insert enrollments (elective courses)
INSERT IGNORE INTO `enrollments` (`student_id`, `course_id`, `class_id`, `score`, `status`) VALUES
(1, 5, 1, 91, 'completed'),
(1, 6, 1, 87, 'completed'),
(2, 5, 1, 80, 'completed'),
(2, 6, 1, 73, 'completed');

-- Insert notifications for all users
INSERT IGNORE INTO `notifications` (`user_id`, `title`, `content`, `type`, `warning_id`, `is_read`, `is_deleted`) VALUES
(1, 'Academic Excellence', 'Congratulations! Your GPA is 3.9 this semester!', 'system', NULL, 0, 0),
(1, 'Scores Released', 'All course scores released, average 92 points', 'system', NULL, 0, 0),
(1, 'Course Selection', 'Course selection for next semester now open', 'system', NULL, 1, 0),
(2, 'Academic Warning', 'Your GPA has declined and triggered a medium-level warning', 'warning', 1, 0, 0),
(2, 'Scores Released', 'Database Systems score released: 72 points', 'system', NULL, 0, 0),
(2, 'Assistance Plan Update', 'Your assistance plan has been updated, please review', 'plan_update', NULL, 0, 0),
(3, 'System Message', 'Course materials for next semester are ready', 'system', NULL, 1, 0),
(4, 'Counselor Notice', 'Please review student performance data', 'system', NULL, 0, 0),
(5, 'Administrator Alert', 'Database backup completed successfully', 'system', NULL, 0, 0);

-- Insert communication logs
INSERT IGNORE INTO `communication_logs` (`teacher_id`, `student_id`, `warning_id`, `content`, `category`) VALUES
(3, 2, 1, 'Discussed database systems study progress and improvement strategies', 'Warning Management'),
(3, 1, NULL, 'Discussed project progress and elective course recommendations', 'Elective Recommendation');

-- Insert feedbacks (student feedback to teachers)
INSERT IGNORE INTO `feedbacks` (`teacher_id`, `student_id`, `content`, `category`, `rating`, `reply_content`) VALUES
(3, 2, 'Course content is challenging, would benefit from more examples and case studies', 'Teaching Method', 4, 'Thank you, will add more practical examples next semester'),
(3, 1, 'Instructor teaches dynamically with clear explanations, highly recommend', 'Teaching Quality', 5, 'Thank you for your recognition, it is my teaching goal');

-- Insert benchmark analysis
INSERT IGNORE INTO `benchmark_analysis` (`student_id`, `class_id`, `major_id`, `semester`, `student_gpa`, `class_avg_gpa`, `major_avg_gpa`, `class_rank`, `class_total`, `major_rank`, `major_total`, `courses_passed`, `courses_failed`, `required_credits`, `credits_on_track`) VALUES
(1, 1, 1, '2023-2024Fall', 3.90, 3.15, 3.18, 5, 30, 25, 120, 12, 0, 32.00, 1),
(2, 1, 1, '2023-2024Fall', 3.25, 3.15, 3.18, 12, 30, 65, 120, 12, 0, 30.00, 1);

-- Insert security logs
INSERT IGNORE INTO `security_log` (`user_id`, `ip_address`, `login_time`, `action`) VALUES
(1, '192.168.1.100', NOW(), 'Student Login'),
(2, '192.168.1.101', NOW(), 'Student Login'),
(3, '192.168.1.102', NOW(), 'Teacher Login'),
(4, '192.168.1.103', NOW(), 'Counselor Login'),
(5, '192.168.1.104', NOW(), 'Admin Login');

-- Insert audit logs
INSERT IGNORE INTO `audit_logs` (`user_id`, `action_type`, `entity_type`, `entity_id`, `details`) VALUES
(3, 'create', 'scores', 1, 'Batch score entry'),
(4, 'create', 'academic_warnings', 1, 'System auto-generated warning'),
(3, 'create', 'assistance_plans', 1, 'Created assistance plan'),
(4, 'update', 'academic_warnings', 1, 'Updated warning status'),
(5, 'view', 'students', 1, 'Admin viewed student data');
