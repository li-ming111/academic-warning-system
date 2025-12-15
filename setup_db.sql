CREATE DATABASE IF NOT EXISTS `academic_warning_system` CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE `academic_warning_system`;

CREATE TABLE IF NOT EXISTS `colleges` (
    `id` INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    `name` VARCHAR(50) NOT NULL UNIQUE,
    `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    `updated_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX `idx_name` (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS `majors` (
    `id` INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    `code` VARCHAR(2) NOT NULL UNIQUE,
    `name` VARCHAR(30) NOT NULL,
    `college_id` INT NOT NULL,
    `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    `updated_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (`college_id`) REFERENCES `colleges` (`id`),
    INDEX `idx_code` (`code`),
    INDEX `idx_college_id` (`college_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS `users` (
    `id` INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    `username` VARCHAR(20) NOT NULL UNIQUE,
    `password` VARCHAR(64) NOT NULL,
    `role` TINYINT NOT NULL,
    `phone` VARCHAR(11),
    `email` VARCHAR(50),
    `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    `updated_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    UNIQUE INDEX `idx_username` (`username`),
    INDEX `idx_role` (`role`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS `student_profile` (
    `id` INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    `user_id` INT NOT NULL UNIQUE,
    `college_id` INT,
    `major_id` INT,
    `privacy_level` TINYINT DEFAULT 2,
    `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    `updated_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (`user_id`) REFERENCES `users` (`id`),
    FOREIGN KEY (`college_id`) REFERENCES `colleges` (`id`),
    FOREIGN KEY (`major_id`) REFERENCES `majors` (`id`),
    INDEX `idx_college_id` (`college_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS `teacher_profile` (
    `id` INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    `user_id` INT NOT NULL UNIQUE,
    `college_id` INT NOT NULL,
    `title` VARCHAR(30),
    `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    `updated_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (`user_id`) REFERENCES `users` (`id`),
    FOREIGN KEY (`college_id`) REFERENCES `colleges` (`id`),
    INDEX `idx_college_id` (`college_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS `counselor_profile` (
    `id` INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    `user_id` INT NOT NULL UNIQUE,
    `college_id` INT NOT NULL,
    `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    `updated_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (`user_id`) REFERENCES `users` (`id`),
    FOREIGN KEY (`college_id`) REFERENCES `colleges` (`id`),
    INDEX `idx_college_id` (`college_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS `admin_profile` (
    `id` INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    `user_id` INT NOT NULL UNIQUE,
    `department` VARCHAR(30) NOT NULL,
    `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    `updated_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (`user_id`) REFERENCES `users` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS `courses` (
    `id` INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    `name` VARCHAR(100) NOT NULL,
    `type` VARCHAR(10) DEFAULT 'required',
    `credits` DECIMAL(3,1) NOT NULL,
    `score_template` VARCHAR(20) DEFAULT 'exam',
    `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    `updated_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX `idx_name` (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS `scores` (
    `id` INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    `student_id` INT NOT NULL,
    `course_id` INT NOT NULL,
    `score_total` DECIMAL(5,2) DEFAULT 0.00,
    `semester` VARCHAR(20) NOT NULL,
    `grade_point` DECIMAL(3,1) DEFAULT 0.00,
    `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    `updated_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (`student_id`) REFERENCES `student_profile` (`id`),
    FOREIGN KEY (`course_id`) REFERENCES `courses` (`id`),
    INDEX `idx_student_id` (`student_id`),
    INDEX `idx_course_id` (`course_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS `enrollments` (
    `id` INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    `student_id` INT NOT NULL,
    `course_id` INT NOT NULL,
    `class_id` INT,
    `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    `updated_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (`student_id`) REFERENCES `student_profile` (`id`),
    FOREIGN KEY (`course_id`) REFERENCES `courses` (`id`),
    UNIQUE INDEX `idx_student_course` (`student_id`, `course_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS `warning_rules` (
    `id` INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    `condition` TEXT NOT NULL,
    `level` VARCHAR(10) NOT NULL,
    `status` TINYINT DEFAULT 1,
    `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    `updated_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX `idx_level` (`level`),
    INDEX `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS `academic_warnings` (
    `id` INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    `student_id` INT NOT NULL,
    `warning_level` VARCHAR(10) NOT NULL,
    `description` TEXT NOT NULL,
    `status` VARCHAR(20) DEFAULT 'pending',
    `appeal_status` VARCHAR(20) DEFAULT 'none',
    `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    `updated_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (`student_id`) REFERENCES `student_profile` (`id`),
    INDEX `idx_student_id` (`student_id`),
    INDEX `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS `score_appeals` (
    `id` INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    `warning_id` INT NOT NULL,
    `reason` TEXT NOT NULL,
    `attachments` VARCHAR(255),
    `status` VARCHAR(20) DEFAULT 'pending',
    `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    `updated_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (`warning_id`) REFERENCES `academic_warnings` (`id`),
    INDEX `idx_warning_id` (`warning_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS `assistance_plans` (
    `id` INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    `student_id` INT NOT NULL,
    `warning_id` INT,
    `progress_percentage` TINYINT DEFAULT 0,
    `status` VARCHAR(20) DEFAULT 'initiated',
    `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    `updated_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (`student_id`) REFERENCES `student_profile` (`id`),
    FOREIGN KEY (`warning_id`) REFERENCES `academic_warnings` (`id`),
    INDEX `idx_student_id` (`student_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS `communication_logs` (
    `id` INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    `teacher_id` INT NOT NULL,
    `student_id` INT NOT NULL,
    `content` TEXT NOT NULL,
    `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (`teacher_id`) REFERENCES `users` (`id`),
    FOREIGN KEY (`student_id`) REFERENCES `student_profile` (`id`),
    INDEX `idx_teacher_id` (`teacher_id`),
    INDEX `idx_student_id` (`student_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS `audit_logs` (
    `id` INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    `user_id` INT NOT NULL,
    `action_type` VARCHAR(20) NOT NULL,
    `action_details` TEXT NOT NULL,
    `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (`user_id`) REFERENCES `users` (`id`),
    INDEX `idx_action_type` (`action_type`),
    INDEX `idx_created_at` (`created_at`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS `security_log` (
    `id` INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    `user_id` INT NOT NULL,
    `ip_address` VARCHAR(15) NOT NULL,
    `login_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
    `is_successful` TINYINT DEFAULT 1,
    `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (`user_id`) REFERENCES `users` (`id`),
    INDEX `idx_user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS `notifications` (
    `id` INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    `user_id` INT NOT NULL,
    `content` TEXT NOT NULL,
    `status` VARCHAR(10) DEFAULT 'unread',
    `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (`user_id`) REFERENCES `users` (`id`),
    INDEX `idx_user_id_status` (`user_id`, `status`),
    INDEX `idx_created_at` (`created_at`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

INSERT INTO `colleges` (`name`) VALUES ('Infotech'), ('AI'), ('CS'), ('EE');

INSERT INTO `majors` (`code`, `name`, `college_id`) VALUES
('01', 'CS', 3),
('02', 'SE', 3),
('03', 'NE', 1),
('04', 'AI', 2),
('05', 'ML', 2);

INSERT INTO `users` (`username`, `password`, `role`, `email`, `phone`) VALUES
('student001', 'e3b0c44298fc1c149afbf4c8996fb92427ae41e4649b934ca495991b7852b855', 1, 'student001@test.com', '13800138000'),
('2023020616', 'e3b0c44298fc1c149afbf4c8996fb92427ae41e4649b934ca495991b7852b855', 1, 'student002@test.com', '13800138001'),
('teacher001', 'e3b0c44298fc1c149afbf4c8996fb92427ae41e4649b934ca495991b7852b855', 2, 'teacher001@test.com', '13800138002'),
('counselor001', 'e3b0c44298fc1c149afbf4c8996fb92427ae41e4649b934ca495991b7852b855', 4, 'counselor001@test.com', '13800138003'),
('admin001', 'e3b0c44298fc1c149afbf4c8996fb92427ae41e4649b934ca495991b7852b855', 3, 'admin001@test.com', '13800138004');

INSERT INTO `student_profile` (`user_id`, `college_id`, `major_id`, `privacy_level`) VALUES
(1, 3, 1, 2),
(2, 3, 2, 2);

INSERT INTO `teacher_profile` (`user_id`, `college_id`, `title`) VALUES (3, 3, 'Professor');

INSERT INTO `counselor_profile` (`user_id`, `college_id`) VALUES (4, 3);

INSERT INTO `admin_profile` (`user_id`, `department`) VALUES (5, 'Dean');

INSERT INTO `courses` (`name`, `type`, `credits`, `score_template`) VALUES
('Advanced Math', 'required', 4.0, 'exam'),
('Linear Algebra', 'required', 3.0, 'exam'),
('Database System', 'required', 3.5, 'exam'),
('Computer Network', 'required', 3.0, 'exam'),
('Algorithm Design', 'elective', 2.5, 'exam'),
('Machine Learning', 'elective', 3.0, 'exam');

INSERT INTO `scores` (`student_id`, `course_id`, `score_total`, `semester`, `grade_point`) VALUES
(1, 1, 78.5, '2023-2024-fall', 3.5),
(1, 2, 51.5, '2023-2024-fall', 0.0),
(1, 3, 82.0, '2023-2024-fall', 3.8),
(1, 4, 75.0, '2023-2024-fall', 3.2),
(2, 1, 88.0, '2023-2024-fall', 4.0),
(2, 2, 85.5, '2023-2024-fall', 3.8),
(2, 3, 92.0, '2023-2024-fall', 4.0),
(2, 4, 80.0, '2023-2024-fall', 3.5);

INSERT INTO `enrollments` (`student_id`, `course_id`, `class_id`) VALUES
(1, 1, NULL), (1, 2, NULL), (1, 3, NULL), (1, 4, NULL),
(2, 1, NULL), (2, 2, NULL), (2, 3, NULL), (2, 4, NULL);

INSERT INTO `warning_rules` (`condition`, `level`, `status`) VALUES
('score < 60', 'high', 1),
('gpa < 2.0', 'medium', 1),
('failed_count > 3', 'high', 1),
('gpa_decrease > 30%', 'medium', 1);

INSERT INTO `academic_warnings` (`student_id`, `warning_level`, `description`, `status`, `appeal_status`) VALUES
(1, 'high', 'Linear Algebra score 51.5, below 60', 'pending', 'none'),
(1, 'medium', 'GPA below average, need help', 'pending', 'none');

INSERT INTO `score_appeals` (`warning_id`, `reason`, `status`) VALUES
(1, 'Exam delayed due to illness', 'pending');

INSERT INTO `assistance_plans` (`student_id`, `warning_id`, `progress_percentage`, `status`) VALUES
(1, 1, 0, 'initiated');

INSERT INTO `communication_logs` (`teacher_id`, `student_id`, `content`) VALUES
(3, 1, 'Recommend tutoring for Linear Algebra');

INSERT INTO `audit_logs` (`user_id`, `action_type`, `action_details`) VALUES
(5, 'create', 'Initialize system'),
(5, 'create', 'Create warning rules'),
(3, 'create', 'Upload student scores');

INSERT INTO `security_log` (`user_id`, `ip_address`, `login_time`, `is_successful`) VALUES
(1, '192.168.1.100', NOW(), 1),
(2, '192.168.1.101', NOW(), 1),
(3, '192.168.1.102', NOW(), 1),
(4, '192.168.1.103', NOW(), 1),
(5, '192.168.1.104', NOW(), 1);

INSERT INTO `notifications` (`user_id`, `content`, `status`) VALUES
(1, 'Warning: Linear Algebra score 51.5', 'unread'),
(1, 'Assistance plan created', 'unread'),
(2, 'Grades released', 'read');
