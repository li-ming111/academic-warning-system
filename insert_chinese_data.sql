USE academic_warning_system;
SET FOREIGN_KEY_CHECKS=0;

DELETE FROM colleges;
INSERT INTO `colleges` (`id`, `name`) VALUES 
(1, 'InfoTech'),
(2, 'AI'),
(3, 'CS'),
(4, 'EE');

DELETE FROM majors;
INSERT INTO `majors` (`code`, `name`, `college_id`) VALUES
('01', 'Computer Science', 3),
('02', 'Software Engineering', 3),
('03', 'Network Engineering', 1),
('04', 'Artificial Intelligence', 2),
('05', 'Machine Learning', 2);

DELETE FROM courses;
INSERT INTO `courses` (`name`, `type`, `credits`, `score_template`) VALUES
('Advanced Math', 'required', 4.0, 'exam'),
('Linear Algebra', 'required', 3.0, 'exam'),
('Database System', 'required', 3.5, 'exam'),
('Computer Network', 'required', 3.0, 'exam'),
('Algorithm Design', 'elective', 2.5, 'exam'),
('Machine Learning Intro', 'elective', 3.0, 'exam');

DELETE FROM warning_rules;
INSERT INTO `warning_rules` (`condition`, `level`, `status`) VALUES
('Score < 60', 'high', 1),
('GPA < 2.0', 'medium', 1),
('Failed > 3 courses', 'high', 1),
('GPA decrease > 30%', 'medium', 1);

DELETE FROM academic_warnings;
INSERT INTO `academic_warnings` (`student_id`, `warning_level`, `description`, `status`, `appeal_status`) VALUES
(1, 'high', 'Linear Algebra score 51.5, below 60, recommend remedial', 'pending', 'none'),
(1, 'medium', 'GPA below average, recommend more study', 'pending', 'none');

DELETE FROM score_appeals;
INSERT INTO `score_appeals` (`warning_id`, `reason`, `status`) VALUES
(1, 'Exam delayed due to illness, request retake', 'pending');

DELETE FROM communication_logs;
INSERT INTO `communication_logs` (`teacher_id`, `student_id`, `content`) VALUES
(3, 1, 'Recommend tutoring for Linear Algebra on weekends');

DELETE FROM audit_logs;
INSERT INTO `audit_logs` (`user_id`, `action_type`, `action_details`) VALUES
(5, 'create', 'System initialization'),
(5, 'create', 'Create warning rules'),
(3, 'create', 'Upload student scores');

DELETE FROM notifications;
INSERT INTO `notifications` (`user_id`, `content`, `status`) VALUES
(1, 'WARNING: Linear Algebra score 51.5, below 60, contact teacher', 'unread'),
(1, 'Assistance plan created, attend tutoring', 'unread'),
(2, 'Grades released, view details', 'read');

SET FOREIGN_KEY_CHECKS=1;

SELECT * FROM colleges;
SELECT * FROM majors;
SELECT * FROM users;
SELECT * FROM scores;
SELECT * FROM academic_warnings;
SELECT * FROM notifications;
