USE academic_warning_system;

-- 创建计科2306班
INSERT IGNORE INTO classes (id, name, college_id, major_id, teacher_id, counselor_id) VALUES
(2, '计科2306班', 3, 1, NULL, NULL);

-- 更新2023020616学生的班级
UPDATE student_profile SET class_id = 2 WHERE student_id = '2023020616';

-- 更新对标分析表
UPDATE benchmark_analysis SET class_id = 2 WHERE student_id = 2;

-- 验证结果
SELECT sp.student_id, sp.name, c.name AS class_name 
FROM student_profile sp 
JOIN classes c ON sp.class_id = c.id 
WHERE sp.student_id = '2023020616';