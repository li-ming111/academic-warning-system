INSERT INTO academic_warnings (student_id, description, warning_level, status, created_at) VALUES (4, 'low grade', 'high', 'initiated', NOW());
INSERT INTO academic_warnings (student_id, description, warning_level, status, created_at) VALUES (4, 'low credit', 'medium', 'initiated', NOW());
INSERT INTO notifications (user_id, content, type, title, created_at, is_read, is_deleted) VALUES (3, 'You have a new warning', 'warning', 'New Warning', NOW(), 0, 0);
INSERT INTO scores (student_id, course_id, score_total, semester, grade_point) VALUES (4, 7, 85, '2024-2025', 3.5);
INSERT INTO scores (student_id, course_id, score_total, semester, grade_point) VALUES (4, 8, 78, '2024-2025', 3.2);
INSERT INTO scores (student_id, course_id, score_total, semester, grade_point) VALUES (4, 9, 88, '2024-2025', 3.8);
