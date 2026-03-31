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
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
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

SELECT '✓ 完整数据初始化成功' AS status;
