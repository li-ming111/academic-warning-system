USE academic_warning_system;
SELECT * FROM student_profile WHERE student_id='2023020616';
SELECT * FROM user WHERE id IN (SELECT user_id FROM student_profile WHERE student_id='2023020616');
SELECT * FROM assistance_plans WHERE student_id IN (SELECT id FROM student_profile WHERE student_id='2023020616');
SELECT * FROM assistance_evaluations WHERE plan_id IN (SELECT id FROM assistance_plans WHERE student_id IN (SELECT id FROM student_profile WHERE student_id='2023020616'));