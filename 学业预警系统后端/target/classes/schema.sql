USE `academic_warning_system`;

-- Create courses table
CREATE TABLE IF NOT EXISTS `courses` (
    `id` BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    `code` VARCHAR(20) NOT NULL UNIQUE,
    `name` VARCHAR(100) NOT NULL,
    `credits` DECIMAL(5, 2) NOT NULL,
    `type` VARCHAR(20),
    `score_template` VARCHAR(50),
    `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    `updated_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    KEY `idx_code` (`code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Create scores table
CREATE TABLE IF NOT EXISTS `scores` (
    `id` BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    `student_id` BIGINT NOT NULL,
    `course_id` BIGINT NOT NULL,
    `semester` VARCHAR(50),
    `regular_score` DECIMAL(5, 2),
    `final_score` DECIMAL(5, 2),
    `score_total` DECIMAL(5, 2) NOT NULL,
    `grade_point` DECIMAL(5, 2),
    `reason_for_change` VARCHAR(500),
    `modified_by` BIGINT,
    `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    `updated_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (`student_id`) REFERENCES `student_profile`(`id`),
    FOREIGN KEY (`course_id`) REFERENCES `courses`(`id`),
    KEY `idx_student_id` (`student_id`),
    KEY `idx_course_id` (`course_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Create warning_rules table
CREATE TABLE IF NOT EXISTS `warning_rules` (
    `id` BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    `name` VARCHAR(100) NOT NULL,
    `description` TEXT,
    `rule_condition` TEXT NOT NULL,
    `level` VARCHAR(20) NOT NULL,
    `status` INT DEFAULT 1,
    `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    `updated_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Create academic_warnings table
CREATE TABLE IF NOT EXISTS `academic_warnings` (
    `id` BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    `student_id` BIGINT NOT NULL,
    `rule_id` BIGINT,
    `college_id` BIGINT,
    `warning_level` VARCHAR(20) NOT NULL,
    `description` TEXT,
    `status` VARCHAR(50) DEFAULT 'pending',
    `appeal_status` VARCHAR(50) DEFAULT 'none',
    `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    `updated_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (`student_id`) REFERENCES `student_profile`(`id`),
    FOREIGN KEY (`rule_id`) REFERENCES `warning_rules`(`id`),
    FOREIGN KEY (`college_id`) REFERENCES `colleges`(`id`),
    KEY `idx_student_id` (`student_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Create assistance_plans table
CREATE TABLE IF NOT EXISTS `assistance_plans` (
    `id` BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    `student_id` BIGINT NOT NULL,
    `warning_id` BIGINT,
    `title` VARCHAR(100),
    `description` TEXT,
    `target` TEXT,
    `measures` TEXT,
    `progress_percentage` DECIMAL(5, 2) DEFAULT 0,
    `status` VARCHAR(50) DEFAULT 'initiated',
    `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    `updated_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    `completed_at` TIMESTAMP,
    FOREIGN KEY (`student_id`) REFERENCES `student_profile`(`id`),
    FOREIGN KEY (`warning_id`) REFERENCES `academic_warnings`(`id`),
    KEY `idx_student_id` (`student_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Create score_appeals table
CREATE TABLE IF NOT EXISTS `score_appeals` (
    `id` BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    `warning_id` BIGINT NOT NULL,
    `student_id` BIGINT NOT NULL,
    `reason` TEXT,
    `attachments` VARCHAR(500),
    `status` VARCHAR(50) DEFAULT 'pending',
    `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    `updated_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (`warning_id`) REFERENCES `academic_warnings`(`id`),
    FOREIGN KEY (`student_id`) REFERENCES `student_profile`(`id`),
    KEY `idx_student_id` (`student_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Create notifications table
CREATE TABLE IF NOT EXISTS `notifications` (
    `id` BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    `user_id` BIGINT NOT NULL,
    `title` VARCHAR(100),
    `content` TEXT NOT NULL,
    `type` VARCHAR(50),
    `warning_id` BIGINT,
    `push_channel` VARCHAR(50) DEFAULT 'app',
    `is_read` INT DEFAULT 0,
    `is_deleted` INT DEFAULT 0,
    `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    `updated_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (`user_id`) REFERENCES `users`(`id`),
    FOREIGN KEY (`warning_id`) REFERENCES `academic_warnings`(`id`),
    KEY `idx_user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Create enrollments table
CREATE TABLE IF NOT EXISTS `enrollments` (
    `id` BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    `student_id` BIGINT NOT NULL,
    `course_id` BIGINT NOT NULL,
    `class_id` BIGINT,
    `score` DECIMAL(5, 2),
    `status` VARCHAR(50) DEFAULT 'ongoing',
    `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    `updated_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (`student_id`) REFERENCES `student_profile`(`id`),
    FOREIGN KEY (`course_id`) REFERENCES `courses`(`id`),
    FOREIGN KEY (`class_id`) REFERENCES `classes`(`id`),
    KEY `idx_student_id` (`student_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Create communication_logs table
CREATE TABLE IF NOT EXISTS `communication_logs` (
    `id` BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    `teacher_id` BIGINT NOT NULL,
    `student_id` BIGINT,
    `warning_id` BIGINT,
    `content` TEXT,
    `category` VARCHAR(50),
    `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (`teacher_id`) REFERENCES `users`(`id`),
    FOREIGN KEY (`student_id`) REFERENCES `student_profile`(`id`),
    FOREIGN KEY (`warning_id`) REFERENCES `academic_warnings`(`id`),
    KEY `idx_teacher_id` (`teacher_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Create feedbacks table
CREATE TABLE IF NOT EXISTS `feedbacks` (
    `id` BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    `teacher_id` BIGINT NOT NULL,
    `student_id` BIGINT NOT NULL,
    `content` TEXT NOT NULL,
    `category` VARCHAR(50),
    `rating` INT,
    `reply_content` TEXT,
    `replied_at` TIMESTAMP,
    `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    `updated_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (`teacher_id`) REFERENCES `users`(`id`),
    FOREIGN KEY (`student_id`) REFERENCES `student_profile`(`id`),
    KEY `idx_teacher_id` (`teacher_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Create security_log table
CREATE TABLE IF NOT EXISTS `security_log` (
    `id` BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    `user_id` BIGINT NOT NULL,
    `ip_address` VARCHAR(50),
    `login_time` TIMESTAMP,
    `action` VARCHAR(100),
    `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (`user_id`) REFERENCES `users`(`id`),
    KEY `idx_user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Create audit_logs table
CREATE TABLE IF NOT EXISTS `audit_logs` (
    `id` BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    `user_id` BIGINT,
    `action_type` VARCHAR(50),
    `entity_type` VARCHAR(50),
    `entity_id` BIGINT,
    `details` TEXT,
    `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (`user_id`) REFERENCES `users`(`id`),
    KEY `idx_action_type` (`action_type`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Create benchmark_analysis table
CREATE TABLE IF NOT EXISTS `benchmark_analysis` (
    `id` BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    `student_id` BIGINT NOT NULL,
    `class_id` BIGINT,
    `major_id` BIGINT,
    `semester` VARCHAR(50),
    `student_gpa` DECIMAL(5, 3),
    `class_avg_gpa` DECIMAL(5, 3),
    `major_avg_gpa` DECIMAL(5, 3),
    `class_rank` INT,
    `class_total` INT,
    `major_rank` INT,
    `major_total` INT,
    `courses_passed` INT DEFAULT 0,
    `courses_failed` INT DEFAULT 0,
    `required_credits` DECIMAL(5, 2),
    `credits_on_track` INT DEFAULT 1,
    `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    `updated_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (`student_id`) REFERENCES `student_profile`(`id`),
    FOREIGN KEY (`class_id`) REFERENCES `classes`(`id`),
    FOREIGN KEY (`major_id`) REFERENCES `majors`(`id`),
    KEY `idx_student_id` (`student_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
