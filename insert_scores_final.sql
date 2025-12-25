-- 为学号 2023020616 的学生（student_profile ID=13）插入历年成绩数据

-- 设置学生 ID
SET @student_profile_id = 13;

-- 删除旧成绩（如果存在）
DELETE FROM scores WHERE student_id = @student_profile_id;

-- 2023-2024-01 秋季成绩
INSERT INTO scores (student_id, course_id, semester, score_total, grade_point, created_at, updated_at) VALUES
(@student_profile_id, 1, '2023-2024-01', 69, 1.9, NOW(), NOW()),
(@student_profile_id, 2, '2023-2024-01', 60, 1.0, NOW(), NOW()),
(@student_profile_id, 3, '2023-2024-01', 82, 3.2, NOW(), NOW()),
(@student_profile_id, 4, '2023-2024-01', 72, 2.2, NOW(), NOW()),
(@student_profile_id, 5, '2023-2024-01', 86, 3.6, NOW(), NOW()),
(@student_profile_id, 6, '2023-2024-01', 60, 1.0, NOW(), NOW()),
(@student_profile_id, 7, '2023-2024-01', 90, 4.0, NOW(), NOW()),
(@student_profile_id, 8, '2023-2024-01', 73, 2.3, NOW(), NOW()),
(@student_profile_id, 9, '2023-2024-01', 80, 3.0, NOW(), NOW()),
(@student_profile_id, 10, '2023-2024-01', 76, 2.6, NOW(), NOW());

-- 2023-2024-02 春季成绩
INSERT INTO scores (student_id, course_id, semester, score_total, grade_point, created_at, updated_at) VALUES
(@student_profile_id, 11, '2023-2024-02', 65, 1.5, NOW(), NOW()),
(@student_profile_id, 12, '2023-2024-02', 64, 1.4, NOW(), NOW()),
(@student_profile_id, 13, '2023-2024-02', 67, 1.7, NOW(), NOW()),
(@student_profile_id, 14, '2023-2024-02', 80, 3.0, NOW(), NOW()),
(@student_profile_id, 15, '2023-2024-02', 80, 3.0, NOW(), NOW()),
(@student_profile_id, 16, '2023-2024-02', 77, 2.7, NOW(), NOW()),
(@student_profile_id, 17, '2023-2024-02', 98, 4.0, NOW(), NOW()),
(@student_profile_id, 18, '2023-2024-02', 80, 3.0, NOW(), NOW()),
(@student_profile_id, 19, '2023-2024-02', 76, 2.6, NOW(), NOW());

-- 2024-2025-01 秋季成绩
INSERT INTO scores (student_id, course_id, semester, score_total, grade_point, created_at, updated_at) VALUES
(@student_profile_id, 20, '2024-2025-01', 74, 2.4, NOW(), NOW()),
(@student_profile_id, 21, '2024-2025-01', 84, 3.4, NOW(), NOW()),
(@student_profile_id, 22, '2024-2025-01', 69, 1.9, NOW(), NOW()),
(@student_profile_id, 23, '2024-2025-01', 85, 3.5, NOW(), NOW()),
(@student_profile_id, 24, '2024-2025-01', 86, 3.6, NOW(), NOW()),
(@student_profile_id, 25, '2024-2025-01', 77, 2.7, NOW(), NOW()),
(@student_profile_id, 26, '2024-2025-01', 60, 1.0, NOW(), NOW()),
(@student_profile_id, 27, '2024-2025-01', 79, 2.9, NOW(), NOW()),
(@student_profile_id, 28, '2024-2025-01', 77, 2.7, NOW(), NOW()),
(@student_profile_id, 29, '2024-2025-01', 86, 3.6, NOW(), NOW()),
(@student_profile_id, 30, '2024-2025-01', 80, 3.0, NOW(), NOW());

-- 2024-2025-02 春季成绩
INSERT INTO scores (student_id, course_id, semester, score_total, grade_point, created_at, updated_at) VALUES
(@student_profile_id, 31, '2024-2025-02', 73, 2.3, NOW(), NOW()),
(@student_profile_id, 32, '2024-2025-02', 81, 3.1, NOW(), NOW()),
(@student_profile_id, 33, '2024-2025-02', 90, 4.0, NOW(), NOW()),
(@student_profile_id, 34, '2024-2025-02', 77, 2.7, NOW(), NOW()),
(@student_profile_id, 35, '2024-2025-02', 78, 2.8, NOW(), NOW()),
(@student_profile_id, 36, '2024-2025-02', 79, 2.9, NOW(), NOW()),
(@student_profile_id, 37, '2024-2025-02', 91, 4.0, NOW(), NOW()),
(@student_profile_id, 38, '2024-2025-02', 84, 3.4, NOW(), NOW());

-- 2025-2026-01 秋季成绩
INSERT INTO scores (student_id, course_id, semester, score_total, grade_point, created_at, updated_at) VALUES
(@student_profile_id, 39, '2025-2026-01', 82, 3.2, NOW(), NOW()),
(@student_profile_id, 40, '2025-2026-01', 72, 2.2, NOW(), NOW()),
(@student_profile_id, 41, '2025-2026-01', 92, 4.0, NOW(), NOW()),
(@student_profile_id, 42, '2025-2026-01', 78, 2.8, NOW(), NOW()),
(@student_profile_id, 43, '2025-2026-01', 85, 3.5, NOW(), NOW());

-- 查询结果
SELECT 'Insert complete!' as result;
SELECT COUNT(*) as total_scores FROM scores WHERE student_id = @student_profile_id;
