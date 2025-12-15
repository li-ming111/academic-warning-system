CREATE DATABASE IF NOT EXISTS academic_warning_system CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE academic_warning_system;

CREATE TABLE colleges (
    id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL UNIQUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE majors (
    id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    code VARCHAR(10) NOT NULL UNIQUE,
    name VARCHAR(100) NOT NULL,
    college_id BIGINT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (college_id) REFERENCES colleges(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE classes (
    id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(50) NOT NULL,
    college_id BIGINT NOT NULL,
    teacher_id BIGINT,
    counselor_id BIGINT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (college_id) REFERENCES colleges(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE users (
    id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    email VARCHAR(100),
    phone VARCHAR(20),
    role INT NOT NULL,
    status INT DEFAULT 1,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE student_profile (
    id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL UNIQUE,
    student_id VARCHAR(20) NOT NULL UNIQUE,
    name VARCHAR(50) NOT NULL,
    college_id BIGINT,
    major_id BIGINT,
    class_id BIGINT,
    privacy_level INT DEFAULT 0,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id),
    FOREIGN KEY (college_id) REFERENCES colleges(id),
    FOREIGN KEY (major_id) REFERENCES majors(id),
    FOREIGN KEY (class_id) REFERENCES classes(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE teacher_profile (
    id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL UNIQUE,
    college_id BIGINT NOT NULL,
    title VARCHAR(50),
    research_areas VARCHAR(200),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id),
    FOREIGN KEY (college_id) REFERENCES colleges(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE counselor_profile (
    id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL UNIQUE,
    college_id BIGINT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id),
    FOREIGN KEY (college_id) REFERENCES colleges(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE admin_profile (
    id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL UNIQUE,
    department VARCHAR(100),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

INSERT INTO colleges (name) VALUES ('Computer Science College'), ('AI College'), ('Engineering College'), ('Business School');

INSERT INTO majors (code, name, college_id) VALUES 
('01', 'Computer Science', 1),
('02', 'Software Engineering', 1),
('03', 'Network Engineering', 3),
('04', 'Artificial Intelligence', 2),
('05', 'Machine Learning', 2);

INSERT INTO classes (name, college_id) VALUES 
('CS-2023-01', 1),
('CS-2023-02', 1),
('AI-2023-01', 2);

INSERT INTO users (username, password, email, phone, role, status) VALUES
('2023020616', '$2a$10$4BX1wL9yYJ6Y7N2K8Q3R4S5T6U7V8W9X0Y1Z2A3B4C5D6E7F8G9H0', '2023020616@example.com', '13800138010', 1, 1),
('student001', '$2a$10$yKBwVGJjJ0nN.L1K8P1ZluqJ2uN6vK3N.X8P2L5M4N3O2P1Q0R9S8', 'student001@example.com', '13800138000', 1, 1),
('teacher001', '$2a$10$yKBwVGJjJ0nN.L1K8P1ZluqJ2uN6vK3N.X8P2L5M4N3O2P1Q0R9S8', 'teacher001@example.com', '13800138001', 2, 1),
('counselor001', '$2a$10$yKBwVGJjJ0nN.L1K8P1ZluqJ2uN6vK3N.X8P2L5M4N3O2P1Q0R9S8', 'counselor001@example.com', '13800138002', 4, 1),
('admin001', '$2a$10$yKBwVGJjJ0nN.L1K8P1ZluqJ2uN6vK3N.X8P2L5M4N3O2P1Q0R9S8', 'admin001@example.com', '13800138003', 3, 1);

INSERT INTO student_profile (user_id, student_id, name, college_id, major_id, class_id) VALUES
(1, '2023020616', 'Li Si', 1, 1, 1),
(2, '20210001', 'Zhang San', 1, 1, 1);

INSERT INTO teacher_profile (user_id, college_id, title) VALUES
(3, 1, 'Professor');

INSERT INTO counselor_profile (user_id, college_id) VALUES
(4, 1);

INSERT INTO admin_profile (user_id, department) VALUES
(5, 'Academic Affairs Office');
