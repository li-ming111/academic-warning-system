-- 为学号 2023020616 的学生插入历年成绩数据

-- 首先删除旧数据
-- 此会也削除与成绩爳关的策略数据，要清理好
DELETE FROM scores WHERE user_id = (SELECT id FROM users WHERE username = '2023020616' LIMIT 1);

-- 2023-2024-01 秋季成绩
INSERT INTO scores (user_id, course_id, semester, score_total, gpa, grade, status, created_time, updated_time) VALUES
((SELECT id FROM users WHERE username = '2023020616' LIMIT 1), 1, '2023-2024-01', 69, 1.9, 'D', 'normal', NOW(), NOW()),
((SELECT id FROM users WHERE username = '2023020616' LIMIT 1), 2, '2023-2024-01', 60, 1.0, 'D', 'normal', NOW(), NOW()),
((SELECT id FROM users WHERE username = '2023020616' LIMIT 1), 3, '2023-2024-01', 82, 3.2, 'B', 'normal', NOW(), NOW()),
((SELECT id FROM users WHERE username = '2023020616' LIMIT 1), 4, '2023-2024-01', 72, 2.2, 'B', 'normal', NOW(), NOW()),
((SELECT id FROM users WHERE username = '2023020616' LIMIT 1), 5, '2023-2024-01', 86, 3.6, 'A-', 'normal', NOW(), NOW()),
((SELECT id FROM users WHERE username = '2023020616' LIMIT 1), 6, '2023-2024-01', 60, 1.0, 'D', 'normal', NOW(), NOW()),
((SELECT id FROM users WHERE username = '2023020616' LIMIT 1), 7, '2023-2024-01', 90, 4.0, 'A', 'normal', NOW(), NOW()),
((SELECT id FROM users WHERE username = '2023020616' LIMIT 1), 8, '2023-2024-01', 73, 2.3, 'B', 'normal', NOW(), NOW()),
((SELECT id FROM users WHERE username = '2023020616' LIMIT 1), 9, '2023-2024-01', 80, 3.0, 'B', 'normal', NOW(), NOW()),
((SELECT id FROM users WHERE username = '2023020616' LIMIT 1), 10, '2023-2024-01', 76, 2.6, 'B', 'normal', NOW(), NOW());

-- 2023-2024-02 春季成绩
INSERT INTO scores (user_id, course_id, semester, score_total, gpa, grade, status, created_time, updated_time) VALUES
((SELECT id FROM users WHERE username = '2023020616' LIMIT 1), 11, '2023-2024-02', 65, 1.5, 'D', 'normal', NOW(), NOW()),
((SELECT id FROM users WHERE username = '2023020616' LIMIT 1), 12, '2023-2024-02', 64, 1.4, 'D', 'normal', NOW(), NOW()),
((SELECT id FROM users WHERE username = '2023020616' LIMIT 1), 13, '2023-2024-02', 67, 1.7, 'D', 'normal', NOW(), NOW()),
((SELECT id FROM users WHERE username = '2023020616' LIMIT 1), 14, '2023-2024-02', 80, 3.0, 'B', 'normal', NOW(), NOW()),
((SELECT id FROM users WHERE username = '2023020616' LIMIT 1), 15, '2023-2024-02', 80, 3.0, 'B', 'normal', NOW(), NOW()),
((SELECT id FROM users WHERE username = '2023020616' LIMIT 1), 16, '2023-2024-02', 77, 2.7, 'B', 'normal', NOW(), NOW()),
((SELECT id FROM users WHERE username = '2023020616' LIMIT 1), 17, '2023-2024-02', 98, 4.0, 'A', 'normal', NOW(), NOW()),
((SELECT id FROM users WHERE username = '2023020616' LIMIT 1), 18, '2023-2024-02', 80, 3.0, 'B', 'normal', NOW(), NOW()),
((SELECT id FROM users WHERE username = '2023020616' LIMIT 1), 19, '2023-2024-02', 76, 2.6, 'B', 'normal', NOW(), NOW());

-- 2024-2025-01 秋季成绩
INSERT INTO scores (user_id, course_id, semester, score_total, gpa, grade, status, created_time, updated_time) VALUES
((SELECT id FROM users WHERE username = '2023020616' LIMIT 1), 20, '2024-2025-01', 74, 2.4, 'B', 'normal', NOW(), NOW()),
((SELECT id FROM users WHERE username = '2023020616' LIMIT 1), 21, '2024-2025-01', 84, 3.4, 'A-', 'normal', NOW(), NOW()),
((SELECT id FROM users WHERE username = '2023020616' LIMIT 1), 22, '2024-2025-01', 69, 1.9, 'D', 'normal', NOW(), NOW()),
((SELECT id FROM users WHERE username = '2023020616' LIMIT 1), 23, '2024-2025-01', 85, 3.5, 'A-', 'normal', NOW(), NOW()),
((SELECT id FROM users WHERE username = '2023020616' LIMIT 1), 24, '2024-2025-01', 86, 3.6, 'A-', 'normal', NOW(), NOW()),
((SELECT id FROM users WHERE username = '2023020616' LIMIT 1), 25, '2024-2025-01', 77, 2.7, 'B', 'normal', NOW(), NOW()),
((SELECT id FROM users WHERE username = '2023020616' LIMIT 1), 26, '2024-2025-01', 60, 1.0, 'D', 'normal', NOW(), NOW()),
((SELECT id FROM users WHERE username = '2023020616' LIMIT 1), 27, '2024-2025-01', 79, 2.9, 'B', 'normal', NOW(), NOW()),
((SELECT id FROM users WHERE username = '2023020616' LIMIT 1), 28, '2024-2025-01', 77, 2.7, 'B', 'normal', NOW(), NOW()),
((SELECT id FROM users WHERE username = '2023020616' LIMIT 1), 29, '2024-2025-01', 86, 3.6, 'A-', 'normal', NOW(), NOW()),
((SELECT id FROM users WHERE username = '2023020616' LIMIT 1), 30, '2024-2025-01', 80, 3.0, 'B', 'normal', NOW(), NOW());

-- 2024-2025-02 春季成绩
INSERT INTO scores (user_id, course_id, semester, score_total, gpa, grade, status, created_time, updated_time) VALUES
((SELECT id FROM users WHERE username = '2023020616' LIMIT 1), 31, '2024-2025-02', 73, 2.3, 'B', 'normal', NOW(), NOW()),
((SELECT id FROM users WHERE username = '2023020616' LIMIT 1), 32, '2024-2025-02', 81, 3.1, 'B', 'normal', NOW(), NOW()),
((SELECT id FROM users WHERE username = '2023020616' LIMIT 1), 33, '2024-2025-02', 90, 4.0, 'A', 'normal', NOW(), NOW()),
((SELECT id FROM users WHERE username = '2023020616' LIMIT 1), 34, '2024-2025-02', 77, 2.7, 'B', 'normal', NOW(), NOW()),
((SELECT id FROM users WHERE username = '2023020616' LIMIT 1), 35, '2024-2025-02', 78, 2.8, 'B', 'normal', NOW(), NOW()),
((SELECT id FROM users WHERE username = '2023020616' LIMIT 1), 36, '2024-2025-02', 79, 2.9, 'B', 'normal', NOW(), NOW()),
((SELECT id FROM users WHERE username = '2023020616' LIMIT 1), 37, '2024-2025-02', 91, 4.0, 'A', 'normal', NOW(), NOW()),
((SELECT id FROM users WHERE username = '2023020616' LIMIT 1), 38, '2024-2025-02', 84, 3.4, 'A-', 'normal', NOW(), NOW());

-- 2025-2026-01 秋季成绩
INSERT INTO scores (user_id, course_id, semester, score_total, gpa, grade, status, created_time, updated_time) VALUES
((SELECT id FROM users WHERE username = '2023020616' LIMIT 1), 39, '2025-2026-01', 82, 3.2, 'B', 'normal', NOW(), NOW()),
((SELECT id FROM users WHERE username = '2023020616' LIMIT 1), 40, '2025-2026-01', 72, 2.2, 'B', 'normal', NOW(), NOW()),
((SELECT id FROM users WHERE username = '2023020616' LIMIT 1), 41, '2025-2026-01', 92, 4.0, 'A', 'normal', NOW(), NOW()),
((SELECT id FROM users WHERE username = '2023020616' LIMIT 1), 42, '2025-2026-01', 78, 2.8, 'B', 'normal', NOW(), NOW()),
((SELECT id FROM users WHERE username = '2023020616' LIMIT 1), 43, '2025-2026-01', 85, 3.5, 'A-', 'normal', NOW(), NOW());
