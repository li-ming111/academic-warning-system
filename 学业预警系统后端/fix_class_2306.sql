-- 修复计科2306班问题的SQL脚本

USE academic_warning_system;

-- 1. 检查计科2306班是否存在
SELECT id, name FROM classes WHERE name = '计科2306班';

-- 2. 如果不存在，创建计科2306班
INSERT INTO classes (name, college_id, major_id, teacher_id, counselor_id)
SELECT '计科2306班', 3, 1, NULL, NULL
WHERE NOT EXISTS (SELECT 1 FROM classes WHERE name = '计科2306班');

-- 3. 获取计科2306班的ID
SET @class_id = (SELECT id FROM classes WHERE name = '计科2306班');
SELECT @class_id AS '计科2306班ID';

-- 4. 将2023020616学生移动到计科2306班
UPDATE student_profile
SET class_id = @class_id
WHERE student_id = '2023020616';

-- 5. 更新对标分析表中的班级ID
UPDATE benchmark_analysis
SET class_id = @class_id
WHERE student_id = (SELECT id FROM student_profile WHERE student_id = '2023020616');

-- 6. 验证修复结果
SELECT 
    sp.student_id, 
    sp.name, 
    c.name AS class_name
FROM 
    student_profile sp
LEFT JOIN 
    classes c ON sp.class_id = c.id
WHERE 
    sp.student_id = '2023020616';

-- 7. 查看计科2306班的学生数量
SELECT 
    c.name AS class_name, 
    COUNT(sp.id) AS student_count
FROM 
    classes c
LEFT JOIN 
    student_profile sp ON c.id = sp.class_id
WHERE 
    c.name = '计科2306班'
GROUP BY 
    c.name;