USE academic_warning_system;

INSERT INTO courses (id, code, name, credits, type, created_at, updated_at) VALUES
(147, 'INET001', '互联网程序设计', 4.00, 'required', NOW(), NOW()),
(148, 'OS001', 'Linux操作系统', 4.00, 'required', NOW(), NOW()),
(149, 'PY001', 'Python程序设计', 4.00, 'required', NOW(), NOW()),
(150, 'PSY001', '生活中的心理学', 4.00, 'required', NOW(), NOW()),
(151, 'SE001', '软件工程', 3.50, 'required', NOW(), NOW());

INSERT INTO scores (student_id, course_id, semester, score_total, grade_point, created_at, updated_at) VALUES
(1, 147, '2025-2026-1', 82, 3.2, NOW(), NOW()),
(1, 148, '2025-2026-1', 72, 2.2, NOW(), NOW()),
(1, 149, '2025-2026-1', 92, 4.0, NOW(), NOW()),
(1, 150, '2025-2026-1', 85, 4.0, NOW(), NOW()),
(1, 151, '2025-2026-1', 78, 2.8, NOW(), NOW());
