USE academic_warning_system;

-- ==================== 创建额外表结构 ====================

-- 课程表
CREATE TABLE IF NOT EXISTS courses (
    id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    code VARCHAR(20) NOT NULL UNIQUE,
    name VARCHAR(100) NOT NULL,
    credits DECIMAL(5, 2) NOT NULL,
    type VARCHAR(20),
    score_template VARCHAR(50),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 成绩表
CREATE TABLE IF NOT EXISTS scores (
    id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    student_id BIGINT NOT NULL,
    course_id BIGINT NOT NULL,
    semester VARCHAR(50),
    regular_score DECIMAL(5, 2),
    final_score DECIMAL(5, 2),
    score_total DECIMAL(5, 2) NOT NULL,
    grade_point DECIMAL(5, 2),
    reason_for_change VARCHAR(500),
    modified_by BIGINT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (student_id) REFERENCES student_profile(id),
    FOREIGN KEY (course_id) REFERENCES courses(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 预警规则表
CREATE TABLE IF NOT EXISTS warning_rules (
    id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    description TEXT,
    rule_condition TEXT NOT NULL,
    level VARCHAR(20) NOT NULL,
    status INT DEFAULT 1,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 学业预警表
CREATE TABLE IF NOT EXISTS academic_warnings (
    id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    student_id BIGINT NOT NULL,
    rule_id BIGINT,
    college_id BIGINT,
    warning_level VARCHAR(20) NOT NULL,
    description TEXT,
    status VARCHAR(50) DEFAULT 'pending',
    appeal_status VARCHAR(50) DEFAULT 'none',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (student_id) REFERENCES student_profile(id),
    FOREIGN KEY (rule_id) REFERENCES warning_rules(id),
    FOREIGN KEY (college_id) REFERENCES colleges(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 帮扶计划表
CREATE TABLE IF NOT EXISTS assistance_plans (
    id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    student_id BIGINT NOT NULL,
    warning_id BIGINT,
    title VARCHAR(100),
    description TEXT,
    target TEXT,
    measures TEXT,
    progress_percentage DECIMAL(5, 2) DEFAULT 0,
    status VARCHAR(50) DEFAULT 'initiated',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    completed_at TIMESTAMP,
    FOREIGN KEY (student_id) REFERENCES student_profile(id),
    FOREIGN KEY (warning_id) REFERENCES academic_warnings(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 成绩申诉表
CREATE TABLE IF NOT EXISTS score_appeals (
    id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    warning_id BIGINT NOT NULL,
    student_id BIGINT NOT NULL,
    reason TEXT,
    attachments VARCHAR(500),
    status VARCHAR(50) DEFAULT 'pending',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (warning_id) REFERENCES academic_warnings(id),
    FOREIGN KEY (student_id) REFERENCES student_profile(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 通知表
CREATE TABLE IF NOT EXISTS notifications (
    id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    title VARCHAR(100),
    content TEXT NOT NULL,
    type VARCHAR(50),
    warning_id BIGINT,
    push_channel VARCHAR(50) DEFAULT 'app',
    is_read INT DEFAULT 0,
    is_deleted INT DEFAULT 0,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id),
    FOREIGN KEY (warning_id) REFERENCES academic_warnings(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 选修课信息表
CREATE TABLE IF NOT EXISTS enrollments (
    id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    student_id BIGINT NOT NULL,
    course_id BIGINT NOT NULL,
    class_id BIGINT,
    score DECIMAL(5, 2),
    status VARCHAR(50) DEFAULT 'ongoing',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (student_id) REFERENCES student_profile(id),
    FOREIGN KEY (course_id) REFERENCES courses(id),
    FOREIGN KEY (class_id) REFERENCES classes(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 沟通日志表
CREATE TABLE IF NOT EXISTS communication_logs (
    id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    teacher_id BIGINT NOT NULL,
    student_id BIGINT,
    warning_id BIGINT,
    content TEXT,
    category VARCHAR(50),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (teacher_id) REFERENCES users(id),
    FOREIGN KEY (student_id) REFERENCES student_profile(id),
    FOREIGN KEY (warning_id) REFERENCES academic_warnings(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 学生反馈表
CREATE TABLE IF NOT EXISTS feedbacks (
    id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    teacher_id BIGINT NOT NULL,
    student_id BIGINT NOT NULL,
    content TEXT NOT NULL,
    category VARCHAR(50),
    rating INT,
    reply_content TEXT,
    replied_at TIMESTAMP,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (teacher_id) REFERENCES users(id),
    FOREIGN KEY (student_id) REFERENCES student_profile(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 安全日志表
CREATE TABLE IF NOT EXISTS security_log (
    id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    ip_address VARCHAR(50),
    login_time TIMESTAMP,
    action VARCHAR(100),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 审计日志表
CREATE TABLE IF NOT EXISTS audit_logs (
    id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT,
    action_type VARCHAR(50),
    entity_type VARCHAR(50),
    entity_id BIGINT,
    details TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 对标分析表
CREATE TABLE IF NOT EXISTS benchmark_analysis (
    id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    student_id BIGINT NOT NULL,
    class_id BIGINT,
    major_id BIGINT,
    semester VARCHAR(50),
    student_gpa DECIMAL(5, 3),
    class_avg_gpa DECIMAL(5, 3),
    major_avg_gpa DECIMAL(5, 3),
    class_rank INT,
    class_total INT,
    major_rank INT,
    major_total INT,
    courses_passed INT DEFAULT 0,
    courses_failed INT DEFAULT 0,
    required_credits DECIMAL(5, 2),
    credits_on_track INT DEFAULT 1,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (student_id) REFERENCES student_profile(id),
    FOREIGN KEY (class_id) REFERENCES classes(id),
    FOREIGN KEY (major_id) REFERENCES majors(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ==================== 插入业务数据 ====================

-- 课程数据
INSERT INTO courses (code, name, credits, type, score_template) VALUES
('CS101', '数据结构', 4.0, '必修', 'exam'),
('CS102', '算法设计', 4.0, '必修', 'exam'),
('CS201', 'Java程序设计', 3.5, '必修', 'exam'),
('CS301', '数据库系统', 4.0, '必修', 'exam'),
('CS401', '软件工程', 3.0, '选修', 'project'),
('CS402', '人工智能基础', 3.5, '选修', 'exam'),
('CS403', '云计算技术', 3.0, '选修', 'project'),
('CS404', '机器学习', 4.0, '选修', 'exam');

-- 预警规则
INSERT INTO warning_rules (name, description, condition, level, status) VALUES
('单科不及格预警', '单科成绩低于60分', 'score < 60', 'high', 1),
('平均成绩低警', '平均绩点低于3.0', 'avg_gpa < 3.0', 'medium', 1),
('学分不足警告', '获得学分未达标', 'credits < required', 'high', 1);

-- 成绩数据 - 学生1 (2023020616 - Li Si)
INSERT INTO scores (student_id, course_id, semester, regular_score, final_score, score_total, grade_point) VALUES
(1, 1, '2023-2024秋', 90, 94, 92, 4.0),
(1, 2, '2023-2024秋', 82, 87, 85, 3.8),
(1, 3, '2023-2024秋', 93, 96, 95, 4.0),
(1, 4, '2023-2024春', 86, 90, 88, 3.8),
(1, 5, '2023-2024春', 78, 82, 80, 3.5),
(1, 6, '2023-2024春', 85, 88, 87, 3.7),
(1, 7, '2023-2024春', 91, 93, 92, 4.0),
(1, 8, '2023-2024春', 89, 92, 91, 4.0);

-- 成绩数据 - 学生2 (20210001 - Zhang San)
INSERT INTO scores (student_id, course_id, semester, regular_score, final_score, score_total, grade_point) VALUES
(2, 1, '2023-2024秋', 80, 88, 85, 3.5),
(2, 2, '2023-2024秋', 75, 80, 78, 3.2),
(2, 3, '2023-2024秋', 85, 90, 88, 3.8),
(2, 4, '2023-2024春', 68, 74, 72, 3.0),
(2, 5, '2023-2024春', 90, 92, 91, 4.0),
(2, 6, '2023-2024春', 70, 75, 73, 3.0),
(2, 7, '2023-2024春', 88, 91, 90, 4.0),
(2, 8, '2023-2024春', 92, 95, 94, 4.0);

-- 预警数据 - 学生2有预警（数据库系统成绩低）
INSERT INTO academic_warnings (student_id, rule_id, college_id, warning_level, description, status, appeal_status) VALUES
(2, 2, 1, 'medium', '数据库系统成绩偏低（72分），平均绩点需加强', 'pending', 'none'),
(2, 1, 1, 'low', '数据库基础模块成绩不理想', 'pending', 'none');

-- 帮扶计划 - 为学生2制定
INSERT INTO assistance_plans (student_id, warning_id, title, description, target, measures, progress_percentage, status) VALUES
(2, 1, '数据库系统学业提升计划', '针对数据库系统成绩较低的情况，制定个性化学习计划', '将数据库系统成绩提高到80分以上，绩点达到3.5', '1.每周额外复习2小时数据库知识\n2.参加教师答疑时间\n3.完成课程设计项目', 45, 'in_progress');

-- 成绩申诉 - 学生2对预警的申诉
INSERT INTO score_appeals (warning_id, student_id, reason, status) VALUES
(1, 2, '认为数据库系统期末成绩评分不够公正，希望复查', 'pending');

-- 选修课信息
INSERT INTO enrollments (student_id, course_id, class_id, score, status) VALUES
(1, 5, 1, 91, 'completed'),
(1, 6, 1, 87, 'completed'),
(1, 7, 1, NULL, 'ongoing'),
(2, 5, 1, 80, 'completed'),
(2, 6, 1, 73, 'completed'),
(2, 7, 1, NULL, 'ongoing');

-- 通知数据
INSERT INTO notifications (user_id, title, content, type, warning_id, is_read, is_deleted) VALUES
-- 学生1 (2023020616) 的通知 - 学业良好
(1, '学业良好表扬', '恭喜！您本学期学业表现优异，成绩平均绩点3.9，继续保持！', 'system', NULL, 0, 0),
(1, '成绩发布通知', '您的所有课程成绩已发布，平均分92分', 'system', NULL, 0, 0),
(1, '选课通知', '本学期选课已开放，请及时登录系统选课', 'system', NULL, 1, 0),
-- 学生2 (20210001) 的通知 - 有预警提示
(2, '学业预警通知', '您的学业成绩有所下降，已触发中等级别预警。建议及时查看预警详情并参加帮扶计划。', 'warning', 1, 0, 0),
(2, '成绩发布通知', '您的《数据库系统》成绩已发布，成绩72分', 'system', NULL, 0, 0),
(2, '帮扶计划更新', '您的帮扶计划已更新，请查看新的学习措施和目标', 'plan_update', NULL, 0, 0);

-- 沟通日志 - 教师与学生的沟通
INSERT INTO communication_logs (teacher_id, student_id, warning_id, content, category) VALUES
(3, 2, 1, '讨论数据库系统学习进度和困难，制定改进措施，建议加强基础知识复习', '预警处理'),
(3, 1, NULL, '讨论课程项目进展和下一步选修方向，表示学业表现优异', '选修建议');

-- 学生反馈 - 对教师的评价
INSERT INTO feedbacks (teacher_id, student_id, content, category, rating, reply_content) VALUES
(3, 2, '课程内容较难，建议增加更多实例讲解和案例分析', '教学方法', 4, '感谢建议，下学期会增加更多实践案例'),
(3, 1, '老师讲课生动有趣，知识点讲解清楚，推荐同学听课', '教学质量', 5, '谢谢你的认可，这是我的教学目标');

-- 对标分析数据
INSERT INTO benchmark_analysis (student_id, class_id, major_id, semester, student_gpa, class_avg_gpa, major_avg_gpa, class_rank, class_total, major_rank, major_total, courses_passed, courses_failed, required_credits, credits_on_track) VALUES
(1, 1, 1, '2023-2024秋', 3.90, 3.15, 3.18, 5, 30, 25, 120, 12, 0, 32.00, 1),
(2, 1, 1, '2023-2024秋', 3.25, 3.15, 3.18, 12, 30, 65, 120, 12, 0, 30.00, 1);

-- 安全日志
INSERT INTO security_log (user_id, ip_address, login_time, action) VALUES
(1, '192.168.1.100', NOW(), '学生登录'),
(2, '192.168.1.101', NOW(), '学生登录'),
(3, '192.168.1.102', NOW(), '教师登录'),
(4, '192.168.1.103', NOW(), '辅导员登录'),
(5, '192.168.1.104', NOW(), '管理员登录');

-- 审计日志
INSERT INTO audit_logs (user_id, action_type, entity_type, entity_id, details) VALUES
(3, 'create', 'scores', 1, '批量录入成绩'),
(4, 'create', 'academic_warnings', 1, '系统自动生成预警'),
(3, 'create', 'assistance_plans', 1, '创建帮扶计划'),
(4, 'update', 'academic_warnings', 1, '处理预警'),
(5, 'view', 'students', 1, '管理员查看学生数据');

SELECT '✓ 完整数据初始化成功' AS status;
