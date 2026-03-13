import java.sql.*;

public class CheckUser {
    public static void main(String[] args) {
        String url = "jdbc:mysql://localhost:3306/academic_warning_system?useSSL=false&serverTimezone=UTC";
        String user = "root";
        String password = "root";
        
        try (Connection conn = DriverManager.getConnection(url, user, password);
             Statement stmt = conn.createStatement()) {
            
            System.out.println("Connected to database successfully!");
            
            // 1. 检查users表中是否存在2023020616用户
            System.out.println("\n1. 检查users表中的2023020616用户:");
            String userQuery = "SELECT id, username, name, role, status FROM users WHERE username = '2023020616'";
            ResultSet rs = stmt.executeQuery(userQuery);
            if (rs.next()) {
                System.out.println("用户存在:");
                System.out.println("ID: " + rs.getLong("id"));
                System.out.println("Username: " + rs.getString("username"));
                System.out.println("Name: " + rs.getString("name"));
                System.out.println("Role: " + rs.getInt("role"));
                System.out.println("Status: " + rs.getInt("status"));
            } else {
                System.out.println("用户不存在!");
            }
            
            // 2. 检查student_profile表中的相关记录
            System.out.println("\n2. 检查student_profile表中的记录:");
            String profileQuery = "SELECT id, user_id, student_id, name, class_id FROM student_profile WHERE student_id = '2023020616'";
            rs = stmt.executeQuery(profileQuery);
            if (rs.next()) {
                System.out.println("学生档案存在:");
                System.out.println("ID: " + rs.getLong("id"));
                System.out.println("User ID: " + rs.getLong("user_id"));
                System.out.println("Student ID: " + rs.getString("student_id"));
                System.out.println("Name: " + rs.getString("name"));
                System.out.println("Class ID: " + rs.getLong("class_id"));
            } else {
                System.out.println("学生档案不存在!");
            }
            
            // 3. 查看所有用户
            System.out.println("\n3. 查看所有用户:");
            String allUsersQuery = "SELECT id, username, name, role, status FROM users ORDER BY created_at DESC LIMIT 25";
            rs = stmt.executeQuery(allUsersQuery);
            int count = 0;
            while (rs.next()) {
                count++;
                System.out.println(count + ". ID: " + rs.getLong("id") + ", Username: " + rs.getString("username") + ", Name: " + rs.getString("name") + ", Role: " + rs.getInt("role") + ", Status: " + rs.getInt("status"));
            }
            System.out.println("\n总用户数: " + count);
            
            // 4. 检查班级信息
            System.out.println("\n4. 查看班级信息:");
            String classesQuery = "SELECT id, name FROM classes";
            rs = stmt.executeQuery(classesQuery);
            while (rs.next()) {
                System.out.println("ID: " + rs.getLong("id") + ", Name: " + rs.getString("name"));
            }
            
            rs.close();
            System.out.println("\n检查完成!");
            
        } catch (SQLException e) {
            System.err.println("Database error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}