-- 创建数据库
CREATE DATABASE IF NOT EXISTS `academic_warning_system` CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE `academic_warning_system`;

-- 一、基础信息表（系统基石）

-- 1. 学院表
CREATE TABLE IF NOT EXISTS `colleges` (
    `id` INT NOT NULL AUTO_INCREMENT PRIMARY KEY COMMENT '学院ID',
    `name` VARCHAR(50) NOT NULL UNIQUE COMMENT '学院名称',
    `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX `idx_name` (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='学院表';

-- 2. 专业表
CREATE TABLE IF NOT EXISTS `majors` (
    `id` INT NOT NULL AUTO_INCREMENT PRIMARY KEY COMMENT '专业ID',
    `code` VARCHAR(2) NOT NULL UNIQUE COMMENT '专业编码（学号第5-6位）',
    `name` VARCHAR(30) NOT NULL COMMENT '专业名称',
    `college_id` INT NOT NULL COMMENT '关联学院ID',
    `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    FOREIGN KEY (`college_id`) REFERENCES `colleges` (`id`),
    INDEX `idx_code` (`code`),
    INDEX `idx_college_id` (`college_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='专业表';

-- 二、用户信息表（角色身份）

-- 3. 用户账号表
CREATE TABLE IF NOT EXISTS `users` (
    `id` INT NOT NULL AUTO_INCREMENT PRIMARY KEY COMMENT '用户ID',
    `username` VARCHAR(20) NOT NULL UNIQUE COMMENT '账号（工号/学号）',
    `password` VARCHAR(64) NOT NULL COMMENT '密码（SHA-256 + 盐值）',
    `role` TINYINT NOT NULL COMMENT '角色：1=学生，2=教师，3=管理员，4=辅导员',
    `phone` VARCHAR(11) COMMENT '手机号',
    `email` VARCHAR(50) COMMENT '邮箱',
    `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    UNIQUE INDEX `idx_username` (`username`),
    INDEX `idx_role` (`role`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户账号表';

-- 4. 学生档案表
CREATE TABLE IF NOT EXISTS `student_profile` (
    `id` INT NOT NULL AUTO_INCREMENT PRIMARY KEY COMMENT '档案ID',
    `user_id` INT NOT NULL UNIQUE COMMENT '关联用户ID',
    `college_id` INT COMMENT '学院ID',
    `major_id` INT COMMENT '专业ID',
    `privacy_level` TINYINT DEFAULT 2 COMMENT '隐私设置：0=公开，1=学院可见，2=仅教师可见',
    `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    FOREIGN KEY (`user_id`) REFERENCES `users` (`id`),
    FOREIGN KEY (`college_id`) REFERENCES `colleges` (`id`),
    FOREIGN KEY (`major_id`) REFERENCES `majors` (`id`),
    INDEX `idx_college_id` (`college_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='学生档案表';

-- 5. 教师档案表
CREATE TABLE IF NOT EXISTS `teacher_profile` (
    `id` INT NOT NULL AUTO_INCREMENT PRIMARY KEY COMMENT '档案ID',
    `user_id` INT NOT NULL UNIQUE COMMENT '关联用户ID',
    `college_id` INT NOT NULL COMMENT '所属学院ID',
    `title` VARCHAR(30) COMMENT '职称',
    `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    FOREIGN KEY (`user_id`) REFERENCES `users` (`id`),
    FOREIGN KEY (`college_id`) REFERENCES `colleges` (`id`),
    INDEX `idx_college_id` (`college_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='教师档案表';

-- 6. 辅导员档案表
CREATE TABLE IF NOT EXISTS `counselor_profile` (
    `id` INT NOT NULL AUTO_INCREMENT PRIMARY KEY COMMENT '档案ID',
    `user_id` INT NOT NULL UNIQUE COMMENT '关联用户ID',
    `college_id` INT NOT NULL COMMENT '所属学院ID',
    `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    FOREIGN KEY (`user_id`) REFERENCES `users` (`id`),
    FOREIGN KEY (`college_id`) REFERENCES `colleges` (`id`),
    INDEX `idx_college_id` (`college_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='辅导员档案表';

-- 7. 管理员档案表
CREATE TABLE IF NOT EXISTS `admin_profile` (
    `id` INT NOT NULL AUTO_INCREMENT PRIMARY KEY COMMENT '档案ID',
    `user_id` INT NOT NULL UNIQUE COMMENT '关联用户ID',
    `department` VARCHAR(30) NOT NULL COMMENT '部门',
    `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    FOREIGN KEY (`user_id`) REFERENCES `users` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='管理员档案表';

-- 三、学业数据表（核心业务）

-- 8. 课程表
CREATE TABLE IF NOT EXISTS `courses` (
    `id` INT NOT NULL AUTO_INCREMENT PRIMARY KEY COMMENT '课程ID',
    `name` VARCHAR(100) NOT NULL COMMENT '课程名称',
    `type` VARCHAR(10) DEFAULT '必修' COMMENT '课程类型：必修/选修',
    `credits` DECIMAL(3,1) NOT NULL COMMENT '学分',
    `score_template` VARCHAR(20) DEFAULT 'exam' COMMENT '考核模板',
    `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX `idx_name` (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='课程表';

-- 9. 成绩表
CREATE TABLE IF NOT EXISTS `scores` (
    `id` INT NOT NULL AUTO_INCREMENT PRIMARY KEY COMMENT '成绩ID',
    `student_id` INT NOT NULL COMMENT '学生ID',
    `course_id` INT NOT NULL COMMENT '课程ID',
    `score_total` DECIMAL(5,2) DEFAULT 0.00 COMMENT '总分',
    `semester` VARCHAR(20) NOT NULL COMMENT '学期',
    `grade_point` DECIMAL(3,1) DEFAULT 0.00 COMMENT '学分绩点',
    `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    FOREIGN KEY (`student_id`) REFERENCES `student_profile` (`id`),
    FOREIGN KEY (`course_id`) REFERENCES `courses` (`id`),
    INDEX `idx_student_id` (`student_id`),
    INDEX `idx_course_id` (`course_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='成绩表';

-- 10. 选课表
CREATE TABLE IF NOT EXISTS `enrollments` (
    `id` INT NOT NULL AUTO_INCREMENT PRIMARY KEY COMMENT '选课ID',
    `student_id` INT NOT NULL COMMENT '学生ID',
    `course_id` INT NOT NULL COMMENT '课程ID',
    `class_id` INT COMMENT '班级ID',
    `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    FOREIGN KEY (`student_id`) REFERENCES `student_profile` (`id`),
    FOREIGN KEY (`course_id`) REFERENCES `courses` (`id`),
    UNIQUE INDEX `idx_student_course` (`student_id`, `course_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='选课表';

-- 四、预警与帮扶表（核心闭环）

-- 11. 预警规则表
CREATE TABLE IF NOT EXISTS `warning_rules` (
    `id` INT NOT NULL AUTO_INCREMENT PRIMARY KEY COMMENT '规则ID',
    `condition` TEXT NOT NULL COMMENT '规则条件',
    `level` VARCHAR(10) NOT NULL COMMENT '预警级别：low/medium/high',
    `status` TINYINT DEFAULT 1 COMMENT '状态：1=启用，0=禁用',
    `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX `idx_level` (`level`),
    INDEX `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='预警规则表';

-- 12. 学业预警表
CREATE TABLE IF NOT EXISTS `academic_warnings` (
    `id` INT NOT NULL AUTO_INCREMENT PRIMARY KEY COMMENT '预警ID',
    `student_id` INT NOT NULL COMMENT '学生ID',
    `warning_level` VARCHAR(10) NOT NULL COMMENT '预警级别：low/medium/high',
    `description` TEXT NOT NULL COMMENT '预警提示',
    `status` VARCHAR(20) DEFAULT 'pending' COMMENT '状态：pending/confirmed/processed/closed',
    `appeal_status` VARCHAR(20) DEFAULT 'none' COMMENT '申诉状态：none/pending/under_appeal',
    `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    FOREIGN KEY (`student_id`) REFERENCES `student_profile` (`id`),
    INDEX `idx_student_id` (`student_id`),
    INDEX `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='学业预警表';

-- 13. 成绩申诉表
CREATE TABLE IF NOT EXISTS `score_appeals` (
    `id` INT NOT NULL AUTO_INCREMENT PRIMARY KEY COMMENT '申诉ID',
    `warning_id` INT NOT NULL COMMENT '关联预警ID',
    `reason` TEXT NOT NULL COMMENT '申诉理由',
    `attachments` VARCHAR(255) COMMENT '证明文件路径',
    `status` VARCHAR(20) DEFAULT 'pending' COMMENT '状态：pending/under_appeal/resolved',
    `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    FOREIGN KEY (`warning_id`) REFERENCES `academic_warnings` (`id`),
    INDEX `idx_warning_id` (`warning_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='成绩申诉表';

-- 14. 帮扶计划表
CREATE TABLE IF NOT EXISTS `assistance_plans` (
    `id` INT NOT NULL AUTO_INCREMENT PRIMARY KEY COMMENT '计划ID',
    `student_id` INT NOT NULL COMMENT '学生ID',
    `warning_id` INT COMMENT '关联预警ID',
    `progress_percentage` TINYINT DEFAULT 0 COMMENT '进度百分比',
    `status` VARCHAR(20) DEFAULT 'initiated' COMMENT '状态：initiated/in_progress/completed',
    `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    FOREIGN KEY (`student_id`) REFERENCES `student_profile` (`id`),
    FOREIGN KEY (`warning_id`) REFERENCES `academic_warnings` (`id`),
    INDEX `idx_student_id` (`student_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='帮扶计划表';

-- 15. 沟通记录表
CREATE TABLE IF NOT EXISTS `communication_logs` (
    `id` INT NOT NULL AUTO_INCREMENT PRIMARY KEY COMMENT '记录ID',
    `teacher_id` INT NOT NULL COMMENT '教师ID',
    `student_id` INT NOT NULL COMMENT '学生ID',
    `content` TEXT NOT NULL COMMENT '沟通内容',
    `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    FOREIGN KEY (`teacher_id`) REFERENCES `users` (`id`),
    FOREIGN KEY (`student_id`) REFERENCES `student_profile` (`id`),
    INDEX `idx_teacher_id` (`teacher_id`),
    INDEX `idx_student_id` (`student_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='沟通记录表';

-- 五、系统管理表（审计与安全）

-- 16. 审计日志表
CREATE TABLE IF NOT EXISTS `audit_logs` (
    `id` INT NOT NULL AUTO_INCREMENT PRIMARY KEY COMMENT '日志ID',
    `user_id` INT NOT NULL COMMENT '操作用户ID',
    `action_type` VARCHAR(20) NOT NULL COMMENT '操作类型：create/update/delete',
    `action_details` TEXT NOT NULL COMMENT '操作详情',
    `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '操作时间',
    FOREIGN KEY (`user_id`) REFERENCES `users` (`id`),
    INDEX `idx_action_type` (`action_type`),
    INDEX `idx_created_at` (`created_at`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='审计日志表';

-- 17. 安全日志表
CREATE TABLE IF NOT EXISTS `security_log` (
    `id` INT NOT NULL AUTO_INCREMENT PRIMARY KEY COMMENT '日志ID',
    `user_id` INT NOT NULL COMMENT '用户ID',
    `ip_address` VARCHAR(15) NOT NULL COMMENT '登录IP',
    `login_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '登录时间',
    `is_successful` TINYINT DEFAULT 1 COMMENT '是否成功：1=成功，0=失败',
    `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    FOREIGN KEY (`user_id`) REFERENCES `users` (`id`),
    INDEX `idx_user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='安全日志表';

-- 18. 通知记录表
CREATE TABLE IF NOT EXISTS `notifications` (
    `id` INT NOT NULL AUTO_INCREMENT PRIMARY KEY COMMENT '通知ID',
    `user_id` INT NOT NULL COMMENT '接收用户ID',
    `content` TEXT NOT NULL COMMENT '通知内容',
    `status` VARCHAR(10) DEFAULT 'unread' COMMENT '状态：unread/read',
    `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    FOREIGN KEY (`user_id`) REFERENCES `users` (`id`),
    INDEX `idx_user_id_status` (`user_id`, `status`),
    INDEX `idx_created_at` (`created_at`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='通知记录表';

-- =============================
-- 插入测试数据
-- =============================

-- 1. 插入学院数据
INSERT INTO `colleges` (`name`) VALUES
('信息工程学院'),
('人工智能学院'),
('计算机科学学院'),
('电子工程学院');

-- 2. 插入专业数据
INSERT INTO `majors` (`code`, `name`, `college_id`) VALUES
('01', '计算机科学与技术', 3),
('02', '软件工程', 3),
('03', '网络工程', 1),
('04', '人工智能', 2),
('05', '机器学习', 2);

-- 3. 插入用户数据
INSERT INTO `users` (`username`, `password`, `role`, `email`, `phone`) VALUES
-- 学生
('student001', 'e3b0c44298fc1c149afbf4c8996fb92427ae41e4649b934ca495991b7852b855', 1, 'student001@example.com', '13800138000'),
('2023020616', 'e3b0c44298fc1c149afbf4c8996fb92427ae41e4649b934ca495991b7852b855', 1, 'student002@example.com', '13800138001'),
-- 教师
('teacher001', 'e3b0c44298fc1c149afbf4c8996fb92427ae41e4649b934ca495991b7852b855', 2, 'teacher001@example.com', '13800138002'),
-- 辅导员
('counselor001', 'e3b0c44298fc1c149afbf4c8996fb92427ae41e4649b934ca495991b7852b855', 4, 'counselor001@example.com', '13800138003'),
-- 管理员
('admin001', 'e3b0c44298fc1c149afbf4c8996fb92427ae41e4649b934ca495991b7852b855', 3, 'admin001@example.com', '13800138004');

-- 4. 插入学生档案
INSERT INTO `student_profile` (`user_id`, `college_id`, `major_id`, `privacy_level`) VALUES
(1, 3, 1, 2),
(2, 3, 2, 2);

-- 5. 插入教师档案
INSERT INTO `teacher_profile` (`user_id`, `college_id`, `title`) VALUES
(3, 3, '教授');

-- 6. 插入辅导员档案
INSERT INTO `counselor_profile` (`user_id`, `college_id`) VALUES
(4, 3);

-- 7. 插入管理员档案
INSERT INTO `admin_profile` (`user_id`, `department`) VALUES
(5, '教务处');

-- 8. 插入课程数据
INSERT INTO `courses` (`name`, `type`, `credits`, `score_template`) VALUES
('高等数学', '必修', 4.0, 'exam'),
('线性代数', '必修', 3.0, 'exam'),
('数据库系统', '必修', 3.5, 'exam'),
('计算机网络', '必修', 3.0, 'exam'),
('算法设计', '选修', 2.5, 'exam'),
('机器学习基础', '选修', 3.0, 'exam');

-- 9. 插入成绩数据
INSERT INTO `scores` (`student_id`, `course_id`, `score_total`, `semester`, `grade_point`) VALUES
(1, 1, 78.5, '2023-2024秋', 3.5),
(1, 2, 51.5, '2023-2024秋', 0.0),
(1, 3, 82.0, '2023-2024秋', 3.8),
(1, 4, 75.0, '2023-2024秋', 3.2),
(2, 1, 88.0, '2023-2024秋', 4.0),
(2, 2, 85.5, '2023-2024秋', 3.8),
(2, 3, 92.0, '2023-2024秋', 4.0),
(2, 4, 80.0, '2023-2024秋', 3.5);

-- 10. 插入选课数据
INSERT INTO `enrollments` (`student_id`, `course_id`, `class_id`) VALUES
(1, 1, NULL),
(1, 2, NULL),
(1, 3, NULL),
(1, 4, NULL),
(2, 1, NULL),
(2, 2, NULL),
(2, 3, NULL),
(2, 4, NULL);

-- 11. 插入预警规则
INSERT INTO `warning_rules` (`condition`, `level`, `status`) VALUES
('成绩低于60分', 'high', 1),
('平均绩点低于2.0', 'medium', 1),
('挂科超过3门', 'high', 1),
('学分绩点下降超过30%', 'medium', 1);

-- 12. 插入预警记录
INSERT INTO `academic_warnings` (`student_id`, `warning_level`, `description`, `status`, `appeal_status`) VALUES
(1, 'high', '线性代数成绩51.5分，低于及格线，建议补考或参加辅导', 'pending', 'none'),
(1, 'medium', '本学期平均绩点较低，建议加强学习', 'pending', 'none');

-- 13. 插入成绩申诉
INSERT INTO `score_appeals` (`warning_id`, `reason`, `status`) VALUES
(1, '线性代数考试期间因病缓考，希望申请重考机会', 'pending');

-- 14. 插入帮扶计划
INSERT INTO `assistance_plans` (`student_id`, `warning_id`, `progress_percentage`, `status`) VALUES
(1, 1, 0, 'initiated');

-- 15. 插入沟通记录
INSERT INTO `communication_logs` (`teacher_id`, `student_id`, `content`) VALUES
(3, 1, '关于线性代数成绩，建议参加周末的专项辅导班，同时加强基础知识复习');

-- 16. 插入审计日志
INSERT INTO `audit_logs` (`user_id`, `action_type`, `action_details`) VALUES
(5, 'create', '系统初始化，创建了基础数据'),
(5, 'create', '创建了预警规则'),
(3, 'create', '录入了学生成绩');

-- 17. 插入安全日志
INSERT INTO `security_log` (`user_id`, `ip_address`, `login_time`, `is_successful`) VALUES
(1, '192.168.1.100', NOW(), 1),
(2, '192.168.1.101', NOW(), 1),
(3, '192.168.1.102', NOW(), 1),
(4, '192.168.1.103', NOW(), 1),
(5, '192.168.1.104', NOW(), 1);

-- 18. 插入通知
INSERT INTO `notifications` (`user_id`, `content`, `status`) VALUES
(1, '【红色预警】线性代数成绩51.5分，已低于及格线，请立即联系教师获取帮扶方案', 'unread'),
(1, '帮扶计划已生成，请查看详情并按时参加辅导课程', 'unread'),
(2, '您本学期成绩已发布，请登录查看详细信息', 'read');
