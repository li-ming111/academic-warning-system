USE `academic_warning_system`;

-- 清空成绩表
TRUNCATE TABLE `scores`;

-- 重新插入成绩数据
-- 学生1 (id=1)
INSERT INTO `scores` (`student_id`, `course_id`, `score_total`, `grade_point`, `semester`) VALUES
(1, 7, 85, 3.5, '秋季'),
(1, 8, 78, 3.2, '秋季'),
(1, 9, 88, 3.8, '秋季'),
(1, 10, 72, 3.0, '春季'),
(1, 11, 91, 4.0, '春季');

-- 学生2 (id=2)
INSERT INTO `scores` (`student_id`, `course_id`, `score_total`, `grade_point`, `semester`) VALUES
(2, 7, 92, 4.0, '秋季'),
(2, 8, 85, 3.8, '秋季'),
(2, 9, 95, 4.0, '秋季'),
(2, 10, 88, 3.8, '春季'),
(2, 11, 80, 3.5, '春季');

SELECT 'Scores reinitialized successfully' AS status;
