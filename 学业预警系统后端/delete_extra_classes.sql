USE academic_warning_system;

-- 查看当前班级
SELECT id, name FROM classes;

-- 删除除了计科2306班以外的所有班级
DELETE FROM classes WHERE name != '计科2306班';

-- 查看删除后的班级
SELECT id, name FROM classes;