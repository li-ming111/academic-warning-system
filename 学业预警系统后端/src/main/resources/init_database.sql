-- 创建数据库
CREATE DATABASE IF NOT EXISTS `academic_warning_system` CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE `academic_warning_system`;

-- 学院表
CREATE TABLE `colleges` (
    `id` BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    `name` VARCHAR(100) NOT NULL UNIQUE COMMENT '学院名称',
    `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    `updated_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX `idx_name` (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='学院表';

-- 专业表
CREATE TABLE `majors` (
    `id` BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    `code` VARCHAR(10) NOT NULL UNIQUE COMMENT '专业编码（学号第5-6位）',
    `name` VARCHAR(100) NOT NULL COMMENT '专业名称',
    `college_id` BIGINT NOT NULL COMMENT '学院ID',
    `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    `updated_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (`college_id`) REFERENCES `colleges` (`id`),
    INDEX `idx_code` (`code`),
    INDEX `idx_college_id` (`college_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='专业表';

-- 班级表
CREATE TABLE `classes` (
    `id` BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    `name` VARCHAR(50) NOT NULL COMMENT '班级名称',
    `college_id` BIGINT NOT NULL COMMENT '学院ID',
    `teacher_id` BIGINT COMMENT '授课教师ID',
    `counselor_id` BIGINT COMMENT '辅导员ID',
    `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    `updated_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (`college_id`) REFERENCES `colleges` (`id`),
    INDEX `idx_teacher_id` (`teacher_id`),
    INDEX `idx_counselor_id` (`counselor_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='班级表';

-- 用户表
CREATE TABLE `users` (
    `id` BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    `username` VARCHAR(50) NOT NULL UNIQUE COMMENT '用户名',
    `name` VARCHAR(100) COMMENT '姓名',
    `password` VARCHAR(255) NOT NULL COMMENT '密码（SHA-256加密）',
    `email` VARCHAR(100) COMMENT '邮箱',
    `phone` VARCHAR(20) COMMENT '电话号码',
    `role` INT NOT NULL COMMENT '角色：1=学生，2=教师，3=管理员，4=辅导员',
    `status` INT DEFAULT 1 COMMENT '状态：1=正常，0=禁用',
    `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    `updated_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    UNIQUE INDEX `idx_username` (`username`),
    INDEX `idx_role` (`role`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户表';

-- 学生档案表
CREATE TABLE `student_profile` (
    `id` BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    `user_id` BIGINT NOT NULL UNIQUE COMMENT '用户ID',
    `student_id` VARCHAR(20) NOT NULL UNIQUE COMMENT '学号',
    `name` VARCHAR(50) NOT NULL COMMENT '学生名称',
    `college_id` BIGINT COMMENT '学院ID',
    `major_id` BIGINT COMMENT '专业ID',
    `class_id` BIGINT COMMENT '班级ID',
    `privacy_level` INT DEFAULT 0 COMMENT '隐私级别：0=公开，1=学院内可见，2=仅教师可见，3=仅本人与管理员',
    `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    `updated_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (`user_id`) REFERENCES `users` (`id`),
    FOREIGN KEY (`college_id`) REFERENCES `colleges` (`id`),
    FOREIGN KEY (`major_id`) REFERENCES `majors` (`id`),
    FOREIGN KEY (`class_id`) REFERENCES `classes` (`id`),
    UNIQUE INDEX `idx_student_id` (`student_id`),
    INDEX `idx_college_id` (`college_id`),
    INDEX `idx_class_id` (`class_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='学生档案表';

-- 教师档案表
CREATE TABLE `teacher_profile` (
    `id` BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    `user_id` BIGINT NOT NULL UNIQUE COMMENT '用户ID',
    `college_id` BIGINT NOT NULL COMMENT '学院ID',
    `title` VARCHAR(50) COMMENT '职称',
    `research_areas` VARCHAR(200) COMMENT '研究方向',
    `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    `updated_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (`user_id`) REFERENCES `users` (`id`),
    FOREIGN KEY (`college_id`) REFERENCES `colleges` (`id`),
    INDEX `idx_college_id` (`college_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='教师档案表';

-- 辅导员档案表
CREATE TABLE `counselor_profile` (
    `id` BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    `user_id` BIGINT NOT NULL UNIQUE COMMENT '用户ID',
    `college_id` BIGINT NOT NULL COMMENT '学院ID',
    `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    `updated_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (`user_id`) REFERENCES `users` (`id`),
    FOREIGN KEY (`college_id`) REFERENCES `colleges` (`id`),
    INDEX `idx_college_id` (`college_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='辅导员档案表';

-- 管理员档案表
CREATE TABLE `admin_profile` (
    `id` BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    `user_id` BIGINT NOT NULL UNIQUE COMMENT '用户ID',
    `department` VARCHAR(100) COMMENT '部门',
    `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    `updated_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (`user_id`) REFERENCES `users` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='管理员档案表';

-- 课程表
CREATE TABLE `courses` (
    `id` BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    `code` VARCHAR(20) NOT NULL UNIQUE COMMENT '课程编号',
    `name` VARCHAR(100) NOT NULL COMMENT '课程名称',
    `credits` DECIMAL(5, 2) NOT NULL COMMENT '学分',
    `type` VARCHAR(20) COMMENT '课程类型：必修，选修',
    `score_template` VARCHAR(50) COMMENT '考核模板：exam（平时30%+期末70%）等',
    `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    `updated_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX `idx_code` (`code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='课程表';

-- 成绩表
CREATE TABLE `scores` (
    `id` BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    `student_id` BIGINT NOT NULL COMMENT '学生ID（student_profile.id）',
    `course_id` BIGINT NOT NULL COMMENT '课程ID',
    `semester` VARCHAR(50) COMMENT '学期（如2023-2024秋）',
    `regular_score` DECIMAL(5, 2) COMMENT '平时分',
    `final_score` DECIMAL(5, 2) COMMENT '期末分',
    `score_total` DECIMAL(5, 2) NOT NULL COMMENT '总分',
    `grade_point` DECIMAL(5, 2) COMMENT '绩点',
    `reason_for_change` VARCHAR(500) COMMENT '修改原因（仅在修改时使用）',
    `modified_by` BIGINT COMMENT '修改人ID（仅在修改时使用）',
    `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    `updated_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (`student_id`) REFERENCES `student_profile` (`id`),
    FOREIGN KEY (`course_id`) REFERENCES `courses` (`id`),
    UNIQUE INDEX `idx_student_course_semester` (`student_id`, `course_id`, `semester`),
    INDEX `idx_student_id_semester` (`student_id`, `semester`),
    INDEX `idx_course_id` (`course_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='成绩表';

-- 成绩修改日志表
CREATE TABLE `score_logs` (
    `id` BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    `score_id` BIGINT NOT NULL COMMENT '成绩ID',
    `modified_by` BIGINT NOT NULL COMMENT '修改人ID',
    `old_score` DECIMAL(5, 2) COMMENT '原分数',
    `new_score` DECIMAL(5, 2) COMMENT '新分数',
    `reason` TEXT COMMENT '修改原因',
    `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (`score_id`) REFERENCES `scores` (`id`),
    FOREIGN KEY (`modified_by`) REFERENCES `users` (`id`),
    INDEX `idx_score_id` (`score_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='成绩修改日志表';

-- 预警规则表
CREATE TABLE `warning_rules` (
    `id` BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    `name` VARCHAR(100) NOT NULL COMMENT '规则名称',
    `description` TEXT COMMENT '规则描述',
    `condition` TEXT NOT NULL COMMENT '规则逻辑条件',
    `level` VARCHAR(20) NOT NULL COMMENT '预警等级：low，medium，high',
    `status` INT DEFAULT 1 COMMENT '状态：1=启用，0=禁用',
    `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    `updated_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='预警规则表';

-- 学业预警表
CREATE TABLE `academic_warnings` (
    `id` BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    `student_id` BIGINT NOT NULL COMMENT '学生ID',
    `rule_id` BIGINT COMMENT '触发规则ID',
    `college_id` BIGINT COMMENT '学院ID',
    `warning_level` VARCHAR(20) NOT NULL COMMENT '预警等级：low，medium，high',
    `description` TEXT COMMENT '预警描述',
    `status` VARCHAR(50) DEFAULT 'pending' COMMENT '状态：pending，confirmed，processed，closed',
    `appeal_status` VARCHAR(50) DEFAULT 'none' COMMENT '申诉状态：none，pending，under_appeal，resolved，rejected',
    `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    `updated_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (`student_id`) REFERENCES `student_profile` (`id`),
    FOREIGN KEY (`rule_id`) REFERENCES `warning_rules` (`id`),
    FOREIGN KEY (`college_id`) REFERENCES `colleges` (`id`),
    INDEX `idx_student_id_status` (`student_id`, `status`),
    INDEX `idx_warning_level` (`warning_level`),
    INDEX `idx_created_at` (`created_at`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='学业预警表';

-- 成绩申诉表
CREATE TABLE `score_appeals` (
    `id` BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    `warning_id` BIGINT NOT NULL COMMENT '关联预警ID',
    `student_id` BIGINT NOT NULL COMMENT '学生ID',
    `reason` TEXT COMMENT '申诉原因',
    `attachments` VARCHAR(500) COMMENT '申诉附件（JSON数组）',
    `status` VARCHAR(50) DEFAULT 'pending' COMMENT '状态：pending，approved，rejected',
    `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    `updated_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (`warning_id`) REFERENCES `academic_warnings` (`id`),
    FOREIGN KEY (`student_id`) REFERENCES `student_profile` (`id`),
    INDEX `idx_student_id` (`student_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='成绩申诉表';

-- 帮扶计划表
CREATE TABLE `assistance_plans` (
    `id` BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    `student_id` BIGINT NOT NULL COMMENT '学生ID',
    `warning_id` BIGINT COMMENT '关联预警ID',
    `title` VARCHAR(100) COMMENT '计划标题',
    `description` TEXT COMMENT '计划描述',
    `target` TEXT COMMENT '目标',
    `measures` TEXT COMMENT '措施',
    `progress_percentage` DECIMAL(5, 2) DEFAULT 0 COMMENT '进度百分比',
    `status` VARCHAR(50) DEFAULT 'initiated' COMMENT '状态：initiated，in_progress，completed，archived',
    `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    `updated_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    `completed_at` TIMESTAMP COMMENT '完成时间',
    FOREIGN KEY (`student_id`) REFERENCES `student_profile` (`id`),
    FOREIGN KEY (`warning_id`) REFERENCES `academic_warnings` (`id`),
    INDEX `idx_student_id` (`student_id`),
    INDEX `idx_warning_id` (`warning_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='帮扶计划表';

-- 通知表
CREATE TABLE `notifications` (
    `id` BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    `user_id` BIGINT NOT NULL COMMENT '接收用户ID',
    `title` VARCHAR(100) COMMENT '通知标题',
    `content` TEXT NOT NULL COMMENT '通知内容',
    `type` VARCHAR(50) COMMENT '通知类型：warning，plan_update，system_message等',
    `warning_id` BIGINT COMMENT '关联预警ID',
    `push_channel` VARCHAR(50) DEFAULT 'app' COMMENT '推送渠道：app(应用内),email(邮件),sms(短信)',
    `is_read` INT DEFAULT 0 COMMENT '是否已读：0=未读，1=已读',
    `is_deleted` INT DEFAULT 0 COMMENT '是否删除：0=正常，1=已删除',
    `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    `updated_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (`user_id`) REFERENCES `users` (`id`),
    FOREIGN KEY (`warning_id`) REFERENCES `academic_warnings` (`id`),
    INDEX `idx_user_id_is_read` (`user_id`, `is_read`),
    INDEX `idx_created_at` (`created_at`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='通知表';

-- 沟通日志表
CREATE TABLE `communication_logs` (
    `id` BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    `teacher_id` BIGINT NOT NULL COMMENT '教师ID',
    `student_id` BIGINT COMMENT '学生ID',
    `warning_id` BIGINT COMMENT '关联预警ID',
    `content` TEXT COMMENT '沟通内容',
    `category` VARCHAR(50) COMMENT '分类：预警处理，选修建议等',
    `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    `updated_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (`teacher_id`) REFERENCES `users` (`id`),
    FOREIGN KEY (`student_id`) REFERENCES `student_profile` (`id`),
    FOREIGN KEY (`warning_id`) REFERENCES `academic_warnings` (`id`),
    INDEX `idx_teacher_id` (`teacher_id`),
    INDEX `idx_student_id` (`student_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='沟通日志表';

-- 学生反馈表
CREATE TABLE `feedbacks` (
    `id` BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    `teacher_id` BIGINT NOT NULL COMMENT '教师ID',
    `student_id` BIGINT NOT NULL COMMENT '学生ID',
    `content` TEXT NOT NULL COMMENT '反馈内容',
    `category` VARCHAR(50) COMMENT '分类：教学方法，课程内容，其他',
    `rating` INT COMMENT '满意度评分（1-5）',
    `reply_content` TEXT COMMENT '回复内容',
    `replied_at` TIMESTAMP COMMENT '回复时间',
    `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    `updated_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (`teacher_id`) REFERENCES `users` (`id`),
    FOREIGN KEY (`student_id`) REFERENCES `student_profile` (`id`),
    INDEX `idx_teacher_id_category` (`teacher_id`, `category`),
    INDEX `idx_student_id` (`student_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='学生反馈表';

-- 选修课信息表
CREATE TABLE `enrollments` (
    `id` BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    `student_id` BIGINT NOT NULL COMMENT '学生ID',
    `course_id` BIGINT NOT NULL COMMENT '课程ID',
    `class_id` BIGINT COMMENT '班级ID',
    `score` DECIMAL(5, 2) COMMENT '成绩',
    `status` VARCHAR(50) DEFAULT 'ongoing' COMMENT '状态：ongoing，completed',
    `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    `updated_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (`student_id`) REFERENCES `student_profile` (`id`),
    FOREIGN KEY (`course_id`) REFERENCES `courses` (`id`),
    FOREIGN KEY (`class_id`) REFERENCES `classes` (`id`),
    UNIQUE INDEX `idx_student_course` (`student_id`, `course_id`),
    INDEX `idx_class_id` (`class_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='选修课信息表';

-- 订阅偏好表
CREATE TABLE `subscription_preferences` (
    `id` BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    `student_id` BIGINT NOT NULL COMMENT '学生ID',
    `subscribe_warnings` INT DEFAULT 1 COMMENT '订阅预警通知：1=开启，0=关闭',
    `subscribe_low` INT DEFAULT 1 COMMENT '订阅低级预警：1=开启，0=关闭',
    `subscribe_medium` INT DEFAULT 1 COMMENT '订阅中级预警：1=开启，0=关闭',
    `subscribe_high` INT DEFAULT 1 COMMENT '订阅高级预警：1=开启，0=关闭',
    `subscribe_assistance` INT DEFAULT 1 COMMENT '订阅帮扶更新：1=开启，0=关闭',
    `subscribe_system` INT DEFAULT 1 COMMENT '订阅系统消息：1=开启，0=关闭',
    `push_email` INT DEFAULT 1 COMMENT '邮件推送：1=开启，0=关闭',
    `push_sms` INT DEFAULT 0 COMMENT '短信推送：1=开启，0=关闭',
    `push_app` INT DEFAULT 1 COMMENT '应用内推送：1=开启，0=关闭',
    `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    `updated_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (`student_id`) REFERENCES `student_profile` (`id`),
    UNIQUE INDEX `idx_student_id` (`student_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='订阅偏好表';

-- 帮扶评价表
CREATE TABLE `assistance_evaluations` (
    `id` BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    `plan_id` BIGINT NOT NULL COMMENT '帮扶计划ID',
    `student_id` BIGINT NOT NULL COMMENT '学生ID',
    `rating` INT COMMENT '评分：1-5',
    `effectiveness` INT COMMENT '有效性评分：1-5',
    `feedback` TEXT COMMENT '评价反馈',
    `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    `updated_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (`plan_id`) REFERENCES `assistance_plans` (`id`),
    FOREIGN KEY (`student_id`) REFERENCES `student_profile` (`id`),
    UNIQUE INDEX `idx_plan_id` (`plan_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='帮扶评价表';

-- 反馈模板表
CREATE TABLE `feedback_templates` (
    `id` BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    `teacher_id` BIGINT NOT NULL COMMENT '教师ID',
    `category` VARCHAR(50) NOT NULL COMMENT '分类：教学方法，课程内容，其他',
    `template_name` VARCHAR(100) COMMENT '模板名称',
    `content` TEXT NOT NULL COMMENT '模板内容',
    `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    `updated_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (`teacher_id`) REFERENCES `users` (`id`),
    INDEX `idx_teacher_id_category` (`teacher_id`, `category`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='反馈模板表';

-- 系统消息表
CREATE TABLE `system_messages` (
    `id` BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    `title` VARCHAR(100) NOT NULL COMMENT '消息标题',
    `content` TEXT NOT NULL COMMENT '消息内容',
    `message_type` VARCHAR(50) COMMENT '消息类型：announcement(公告)，task(任务)，system(系统)',
    `target_type` VARCHAR(50) COMMENT '目标类型：all(全校)，college(学院)，class(班级)',
    `target_id` BIGINT COMMENT '目标ID(学院ID或班级ID)',
    `created_by` BIGINT NOT NULL COMMENT '创建人ID',
    `is_published` INT DEFAULT 0 COMMENT '是否已发布：0=未发布，1=已发布',
    `published_at` TIMESTAMP COMMENT '发布时间',
    `expires_at` TIMESTAMP COMMENT '过期时间',
    `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    `updated_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (`created_by`) REFERENCES `users` (`id`),
    INDEX `idx_is_published` (`is_published`),
    INDEX `idx_published_at` (`published_at`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='系统消息表';

-- 用户权限表
CREATE TABLE `user_role_permissions` (
    `id` BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    `user_id` BIGINT NOT NULL COMMENT '用户ID',
    `permission_code` VARCHAR(100) NOT NULL COMMENT '权限代码',
    `permission_name` VARCHAR(100) COMMENT '权限名称',
    `scope_type` VARCHAR(50) COMMENT '权限范围：self(自己)，college(学院)，all(全校)',
    `scope_id` BIGINT COMMENT '权限范围ID',
    `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (`user_id`) REFERENCES `users` (`id`),
    UNIQUE INDEX `idx_user_permission_scope` (`user_id`, `permission_code`, `scope_type`, `scope_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户权限表';

-- 毕业要求表
CREATE TABLE `elective_requirements` (
    `id` BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    `grade` INT COMMENT '年级（如2021）',
    `required_credits` DECIMAL(5, 2) COMMENT '要求学分',
    `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    `updated_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='毕业要求表';

-- 安全日志表
CREATE TABLE `security_log` (
    `id` BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    `user_id` BIGINT NOT NULL COMMENT '用户ID',
    `ip_address` VARCHAR(50) COMMENT 'IP地址',
    `login_time` TIMESTAMP COMMENT '登录时间',
    `action` VARCHAR(100) COMMENT '操作',
    `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (`user_id`) REFERENCES `users` (`id`),
    INDEX `idx_user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='安全日志表';

-- 对标分析表
CREATE TABLE `benchmark_analysis` (
    `id` BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    `student_id` BIGINT NOT NULL COMMENT '学生ID',
    `class_id` BIGINT COMMENT '班级ID',
    `major_id` BIGINT COMMENT '专业ID',
    `semester` VARCHAR(50) COMMENT '学期（如2023-2024秋）',
    `student_gpa` DECIMAL(5, 3) COMMENT '学生GPA',
    `class_avg_gpa` DECIMAL(5, 3) COMMENT '班级平均GPA',
    `major_avg_gpa` DECIMAL(5, 3) COMMENT '专业平均GPA',
    `class_rank` INT COMMENT '班级排名',
    `class_total` INT COMMENT '班级总人数',
    `major_rank` INT COMMENT '专业排名',
    `major_total` INT COMMENT '专业总人数',
    `courses_passed` INT DEFAULT 0 COMMENT '通过课程数',
    `courses_failed` INT DEFAULT 0 COMMENT '不及格课程数',
    `required_credits` DECIMAL(5, 2) COMMENT '修习学分',
    `credits_on_track` INT DEFAULT 1 COMMENT '学分是否达标：1=达标，0=未达标',
    `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    `updated_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (`student_id`) REFERENCES `student_profile` (`id`),
    FOREIGN KEY (`class_id`) REFERENCES `classes` (`id`),
    FOREIGN KEY (`major_id`) REFERENCES `majors` (`id`),
    UNIQUE INDEX `idx_student_semester` (`student_id`, `semester`),
    INDEX `idx_class_id` (`class_id`),
    INDEX `idx_major_id` (`major_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='对标分析表';

-- 审计日志表
CREATE TABLE `audit_logs` (
    `id` BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    `user_id` BIGINT COMMENT '操作用户ID',
    `action_type` VARCHAR(50) COMMENT '操作类型：login，update，delete等',
    `entity_type` VARCHAR(50) COMMENT '实体类型',
    `entity_id` BIGINT COMMENT '实体ID',
    `details` TEXT COMMENT '操作详情',
    `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (`user_id`) REFERENCES `users` (`id`),
    INDEX `idx_action_type` (`action_type`),
    INDEX `idx_created_at` (`created_at`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='审计日志表';

-- 插入初始学院数据
INSERT INTO `colleges` (`name`) VALUES
('信息工程学院'),
('人工智能学院'),
('计算机科学学院'),
('电子工程学院');

-- 初始化订阅偏好（学生001）
INSERT INTO `subscription_preferences` (`student_id`) VALUES (1), (2);

-- 插入初始专业数据
INSERT INTO `majors` (`code`, `name`, `college_id`) VALUES
('01', '计算机科学与技术', 3),
('02', '软件工程', 3),
('03', '网络工程', 1),
('04', '人工智能', 2),
('05', '机器学习', 2);

-- 插入初始班级数据
INSERT INTO `classes` (`name`, `college_id`, `teacher_id`, `counselor_id`) VALUES
('2021级计算机1班', 3, NULL, NULL);

-- 插入测试用户数据
-- 注意：密码都是 BCrypt 加密的
-- 解密后：password123 或 123456
INSERT INTO `users` (`username`, `password`, `email`, `phone`, `role`, `status`) VALUES
-- 学生账号1
('student001', '$2a$10$yKBwVGJjJ0nN.L1K8P1ZluqJ2uN6vK3N.X8P2L5M4N3O2P1Q0R9S8', 'student001@example.com', '13800138000', 1, 1),
-- 学生账号2 - 用户指定账号
('2023020616', '$2a$10$4BX1wL9yYJ6Y7N2K8Q3R4S5T6U7V8W9X0Y1Z2A3B4C5D6E7F8G9H0', '2023020616@example.com', '13800138010', 1, 1),
-- 教师账号  
('teacher001', '$2a$10$yKBwVGJjJ0nN.L1K8P1ZluqJ2uN6vK3N.X8P2L5M4N3O2P1Q0R9S8', 'teacher001@example.com', '13800138001', 2, 1),
-- 辅导员账号
('counselor001', '$2a$10$yKBwVGJjJ0nN.L1K8P1ZluqJ2uN6vK3N.X8P2L5M4N3O2P1Q0R9S8', 'counselor001@example.com', '13800138002', 4, 1),
-- 管理员账号
('admin001', '$2a$10$yKBwVGJjJ0nN.L1K8P1ZluqJ2uN6vK3N.X8P2L5M4N3O2P1Q0R9S8', 'admin001@example.com', '13800138003', 3, 1);

-- 插入学生档案
INSERT INTO `student_profile` (`user_id`, `student_id`, `name`, `college_id`, `major_id`, `class_id`) VALUES
(1, '20210001', '张三', 3, 1, 1),
(2, '2023020616', '李四', 3, 1, 1);

-- 插入教师档案
INSERT INTO `teacher_profile` (`user_id`, `college_id`, `title`) VALUES
(3, 3, '教授');

-- 插入辅导员档案
INSERT INTO `counselor_profile` (`user_id`, `college_id`) VALUES
(4, 3);

-- 插入管理员档案
INSERT INTO `admin_profile` (`user_id`, `department`) VALUES
(5, '教务处');

-- 初始化管理员权限
INSERT INTO `user_role_permissions` (`user_id`, `permission_code`, `permission_name`, `scope_type`) VALUES
(5, 'manage_colleges', '学院管理', 'all'),
(5, 'manage_majors', '专业管理', 'all'),
(5, 'manage_users', '用户管理', 'all'),
(5, 'manage_rules', '规则管理', 'all'),
(5, 'manage_messages', '消息管理', 'all');

-- 初始化教师权限
INSERT INTO `user_role_permissions` (`user_id`, `permission_code`, `permission_name`, `scope_type`, `scope_id`) VALUES
(3, 'view_scores', '查看成绩', 'college', 3),
(3, 'edit_scores', '修改成绩', 'college', 3),
(3, 'manage_warnings', '管理预警', 'college', 3);

-- 初始化计等权限
INSERT INTO `user_role_permissions` (`user_id`, `permission_code`, `permission_name`, `scope_type`, `scope_id`) VALUES
(4, 'view_students', '查看学生', 'college', 3),
(4, 'manage_assistance', '管理帮扶', 'college', 3),
(4, 'manage_warnings', '管理预警', 'college', 3);

-- 处标分析初始化数据、2023-2024秋学年
INSERT INTO `benchmark_analysis` (`student_id`, `class_id`, `major_id`, `semester`, `student_gpa`, `class_avg_gpa`, `major_avg_gpa`, `class_rank`, `class_total`, `major_rank`, `major_total`, `courses_passed`, `courses_failed`, `required_credits`, `credits_on_track`) VALUES
(1, 1, 1, '2023-2024秋', 3.52, 3.15, 3.18, 8, 30, 42, 120, 12, 0, 32.00, 1),
(2, 1, 1, '2023-2024秋', 3.25, 3.15, 3.18, 12, 30, 65, 120, 12, 0, 30.00, 1);

-- 成绩申诉初始化数据
INSERT INTO `score_appeals` (`warning_id`, `student_id`, `reason`, `status`) VALUES
(1, 1, '认为该科目成绩评分不公正，对考试成绩有异议', 'pending'),
(2, 2, '希望复查成绩，觉得计算有误', 'approved');
