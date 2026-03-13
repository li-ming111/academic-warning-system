import java.sql.*;

public class FixDatabase {
    public static void main(String[] args) {
        String url = "jdbc:mysql://localhost:3306/academic_warning_system?useSSL=false&serverTimezone=UTC";
        String user = "root";
        String password = "root";
        
        try (Connection conn = DriverManager.getConnection(url, user, password);
             Statement stmt = conn.createStatement()) {
            
            System.out.println("Connected to database successfully!");
            
            // 1. Create 计科2306班 if not exists
            String createClassSQL = "INSERT IGNORE INTO classes (id, name, college_id, major_id, teacher_id, counselor_id) VALUES (2, '计科2306班', 3, 1, NULL, NULL)";
            int classResult = stmt.executeUpdate(createClassSQL);
            System.out.println("Created 计科2306班: " + (classResult > 0 ? "Success" : "Already exists"));
            
            // 2. Update student's class
            String updateStudentSQL = "UPDATE student_profile SET class_id = 2 WHERE student_id = '2023020616'";
            int studentResult = stmt.executeUpdate(updateStudentSQL);
            System.out.println("Updated student's class: " + (studentResult > 0 ? "Success" : "No change"));
            
            // 3. Update benchmark analysis
            String updateBenchmarkSQL = "UPDATE benchmark_analysis SET class_id = 2 WHERE student_id = 2";
            int benchmarkResult = stmt.executeUpdate(updateBenchmarkSQL);
            System.out.println("Updated benchmark analysis: " + (benchmarkResult > 0 ? "Success" : "No change"));
            
            // 4. Verify result
            String verifySQL = "SELECT sp.student_id, sp.name, c.name AS class_name FROM student_profile sp JOIN classes c ON sp.class_id = c.id WHERE sp.student_id = '2023020616'";
            ResultSet rs = stmt.executeQuery(verifySQL);
            if (rs.next()) {
                System.out.println("Verification result:");
                System.out.println("Student ID: " + rs.getString("student_id"));
                System.out.println("Name: " + rs.getString("name"));
                System.out.println("Class: " + rs.getString("class_name"));
            } else {
                System.out.println("Student not found!");
            }
            
            rs.close();
            System.out.println("Fix completed successfully!");
            
        } catch (SQLException e) {
            System.err.println("Database error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}