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
                // Check if academic_warnings table exists
                var rs = conn.getMetaData().getTables(null, null, "academic_warnings", null);
                
                // If tables don't exist, try to initialize
                if (!rs.next()) {
                    System.out.println("[DataInit] Initializing database...");
                    executeInitScript(stmt, "/init_complete_data.sql");
                    System.out.println("[DataInit] Database initialization completed successfully");
                } else {
                    System.out.println("[DataInit] Database already initialized");
                }
            } catch (Exception e) {
                System.err.println("[DataInit] Error during database initialization: " + e.getMessage());
                e.printStackTrace();
            }
        };
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
