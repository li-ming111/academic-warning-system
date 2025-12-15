USE academic_warning_system;
INSERT INTO users (username, password, email, phone, role, status) VALUES ('2023020616', '$2a$10$4BX1wL9yYJ6Y7N2K8Q3R4S5T6U7V8W9X0Y1Z2A3B4C5D6E7F8G9H0', '2023020616@example.com', '13800138010', 1, 1);
INSERT INTO student_profile (user_id, student_id, name, college_id, major_id, class_id) VALUES (LAST_INSERT_ID(), '2023020616', 'Li Si', 1, 1, 1);
