package com.academic.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.ClassPathResource;
import org.springframework.util.StreamUtils;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.Statement;
import javax.sql.DataSource;

/**
 * Database initialization component
 */
@Component
public class DataInit {

    /**
     * Initialize database with SQL scripts on application startup
     */
    @Bean
    public CommandLineRunner initDatabase(DataSource dataSource) {
        return args -> {
            try (
                Connection conn = dataSource.getConnection();
                Statement stmt = conn.createStatement()
            ) {
                // Check if users table has name column
                try {
                    var rs = stmt.executeQuery("SELECT name FROM users LIMIT 1");
                    rs.close();
                } catch (Exception e) {
                    // Column doesn't exist, add it
                    System.out.println("[DataInit] Adding name column to users table...");
                    try {
                        stmt.execute("ALTER TABLE users ADD COLUMN name VARCHAR(100) DEFAULT NULL COMMENT '姓名'");
                        System.out.println("[DataInit] name column added successfully");
                    } catch (Exception ex) {
                        System.err.println("[DataInit] Warning: " + ex.getMessage());
                    }
                }
                
                // Check if academic_warnings table exists
                var rs = conn.getMetaData().getTables(null, null, "academic_warnings", null);
                
                // If tables don't exist, try to initialize
                if (!rs.next()) {
                    System.out.println("[DataInit] Initializing database...");
                    executeInitScript(stmt, "/init_complete_data.sql");
                    System.out.println("[DataInit] Database initialization completed successfully");
                } else {
                    System.out.println("[DataInit] Database already initialized");
                    // 初始化2025-2026-1的课程数据
                    initCourses2025(stmt);
                }
            } catch (Exception e) {
                System.err.println("[DataInit] Error during database initialization: " + e.getMessage());
                e.printStackTrace();
            }
        };
    }

    /**
     * Initialize courses for 2025-2026-1 semester
     */
    private void initCourses2025(Statement stmt) {
        try {
            // Check if courses already exist
            var rs = stmt.executeQuery("SELECT COUNT(*) as cnt FROM courses WHERE id = 147");
            boolean coursesExist = rs.next() && rs.getInt("cnt") > 0;
            
            if (!coursesExist) {
                // Delete old data if exists
                try { stmt.execute("DELETE FROM scores WHERE course_id BETWEEN 147 AND 151"); } catch (Exception e) {}
                try { stmt.execute("DELETE FROM courses WHERE id BETWEEN 147 AND 151"); } catch (Exception e) {}
                
                // Insert courses with Unicode escape sequences
                stmt.execute("INSERT INTO courses (id, code, name, credits, type, created_at, updated_at) VALUES " +
                    "(147, 'INET001', '\u4e92\u8054\u7f51\u7a0b\u5e8f\u8bbe\u8ba1', 4.00, 'required', NOW(), NOW())");
                stmt.execute("INSERT INTO courses (id, code, name, credits, type, created_at, updated_at) VALUES " +
                    "(148, 'OS001', 'Linux\u64cd\u4f5c\u7cfb\u7edf', 4.00, 'required', NOW(), NOW())");
                stmt.execute("INSERT INTO courses (id, code, name, credits, type, created_at, updated_at) VALUES " +
                    "(149, 'PY001', 'Python\u7a0b\u5e8f\u8bbe\u8ba1', 4.00, 'required', NOW(), NOW())");
                stmt.execute("INSERT INTO courses (id, code, name, credits, type, created_at, updated_at) VALUES " +
                    "(150, 'PSY001', '\u751f\u6d3b\u4e2d\u7684\u5fc3\u7406\u5b66', 4.00, 'required', NOW(), NOW())");
                stmt.execute("INSERT INTO courses (id, code, name, credits, type, created_at, updated_at) VALUES " +
                    "(151, 'SE001', '\u8f6f\u4ef6\u5de5\u7a0b', 3.50, 'required', NOW(), NOW())");
            }
            
            // 暂时注释掉插入成绩数据的代码，避免数据库错误
            // System.out.println("[DataInit] Inserting scores data...");
            // 
            // // Delete existing scores for these courses
            // try { stmt.execute("DELETE FROM scores WHERE course_id BETWEEN 147 AND 151"); } catch (Exception e) {}
            // 
            // // Insert scores
            // stmt.execute("INSERT INTO scores (student_id, course_id, semester, regular_score, final_score, score_total, grade_point, created_at, updated_at) VALUES " +
            //     "(1, 147, '2025-2026-1', 85, 80, 82, 3.2, NOW(), NOW())");
            // stmt.execute("INSERT INTO scores (student_id, course_id, semester, regular_score, final_score, score_total, grade_point, created_at, updated_at) VALUES " +
            //     "(1, 148, '2025-2026-1', 75, 70, 72, 2.2, NOW(), NOW())");
            // stmt.execute("INSERT INTO scores (student_id, course_id, semester, regular_score, final_score, score_total, grade_point, created_at, updated_at) VALUES " +
            //     "(1, 149, '2025-2026-1', 95, 90, 92, 4.0, NOW(), NOW())");
            // stmt.execute("INSERT INTO scores (student_id, course_id, semester, regular_score, final_score, score_total, grade_point, created_at, updated_at) VALUES " +
            //     "(1, 150, '2025-2026-1', 85, 85, 85, 4.0, NOW(), NOW())");
            // stmt.execute("INSERT INTO scores (student_id, course_id, semester, regular_score, final_score, score_total, grade_point, created_at, updated_at) VALUES " +
            //     "(1, 151, '2025-2026-1', 80, 75, 78, 2.8, NOW(), NOW())");
            // 
            // // 为其他学生添加成绩数据
            // stmt.execute("INSERT INTO scores (student_id, course_id, semester, regular_score, final_score, score_total, grade_point, created_at, updated_at) VALUES " +
            //     "(2, 147, '2025-2026-1', 75, 70, 72, 2.2, NOW(), NOW())");
            // stmt.execute("INSERT INTO scores (student_id, course_id, semester, regular_score, final_score, score_total, grade_point, created_at, updated_at) VALUES " +
            //     "(2, 148, '2025-2026-1', 85, 80, 82, 3.2, NOW(), NOW())");
            // stmt.execute("INSERT INTO scores (student_id, course_id, semester, regular_score, final_score, score_total, grade_point, created_at, updated_at) VALUES " +
            //     "(3, 147, '2025-2026-1', 90, 85, 87, 3.7, NOW(), NOW())");
            // stmt.execute("INSERT INTO scores (student_id, course_id, semester, regular_score, final_score, score_total, grade_point, created_at, updated_at) VALUES " +
            //     "(3, 149, '2025-2026-1', 65, 60, 62, 2.0, NOW(), NOW())");
            // stmt.execute("INSERT INTO scores (student_id, course_id, semester, regular_score, final_score, score_total, grade_point, created_at, updated_at) VALUES " +
            //     "(4, 148, '2025-2026-1', 55, 50, 52, 0.0, NOW(), NOW())");
            // stmt.execute("INSERT INTO scores (student_id, course_id, semester, regular_score, final_score, score_total, grade_point, created_at, updated_at) VALUES " +
            //     "(4, 150, '2025-2026-1', 80, 75, 78, 2.8, NOW(), NOW())");
            // stmt.execute("INSERT INTO scores (student_id, course_id, semester, regular_score, final_score, score_total, grade_point, created_at, updated_at) VALUES " +
            //     "(5, 147, '2025-2026-1', 95, 90, 92, 4.0, NOW(), NOW())");
            // stmt.execute("INSERT INTO scores (student_id, course_id, semester, regular_score, final_score, score_total, grade_point, created_at, updated_at) VALUES " +
            //     "(5, 151, '2025-2026-1', 70, 65, 68, 2.3, NOW(), NOW())");
            
            System.out.println("[DataInit] 2025-2026-1 courses initialized successfully");
        } catch (Exception e) {
            System.err.println("[DataInit] Error initializing 2025-2026-1 courses: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void executeInitScript(Statement stmt, String scriptPath) throws Exception {
        try {
            ClassPathResource resource = new ClassPathResource(scriptPath);
            String sql = StreamUtils.copyToString(resource.getInputStream(), StandardCharsets.UTF_8);
            
            // Split by semicolon and execute each statement
            String[] statements = sql.split(";");
            for (String statement : statements) {
                statement = statement.trim();
                if (!statement.isEmpty() && !statement.startsWith("--")) {
                    try {
                        stmt.execute(statement);
                    } catch (Exception e) {
                        // Log error but continue with other statements
                        System.err.println("[DataInit] Warning: " + e.getMessage());
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("[DataInit] Failed to execute init script: " + e.getMessage());
            throw e;
        }
    }
}
