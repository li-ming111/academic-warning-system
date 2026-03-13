import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

public class CheckClassStatus {
    public static void main(String[] args) {
        try {
            // 加载驱动
            Class.forName("com.mysql.cj.jdbc.Driver");
            
            // 连接数据库
            String url = "jdbc:mysql://localhost:3306/academic_warning_system?useSSL=false&serverTimezone=UTC";
            String username = "root";
            String password = "root";
            Connection conn = DriverManager.getConnection(url, username, password);
            
            System.out.println("数据库连接成功");
            
            // 创建 Statement
            Statement stmt = conn.createStatement();
            
            // 查看所有班级
            System.out.println("\n=== 所有班级列表 ===");
            ResultSet rs = stmt.executeQuery("SELECT id, name, major_id, college_id FROM classes");
            while (rs.next()) {
                System.out.println("ID: " + rs.getLong("id") + ", 名称: " + rs.getString("name") + ", 专业ID: " + rs.getLong("major_id") + ", 学院ID: " + rs.getLong("college_id"));
            }
            
            // 查看计科2306班的学生
            System.out.println("\n=== 计科2306班学生 ===");
            rs = stmt.executeQuery("SELECT c.id as class_id, c.name as class_name, sp.id as student_id, sp.student_id as student_number, sp.name as student_name FROM classes c LEFT JOIN student_profile sp ON c.id = sp.class_id WHERE c.name = '计科2306班'");
            boolean hasStudents = false;
            while (rs.next()) {
                hasStudents = true;
                System.out.println("班级: " + rs.getString("class_name") + ", 学生学号: " + rs.getString("student_number") + ", 学生姓名: " + rs.getString("student_name"));
            }
            if (!hasStudents) {
                System.out.println("计科2306班没有学生");
            }
            
            // 查看所有学生及其班级
            System.out.println("\n=== 所有学生及其班级 ===");
            rs = stmt.executeQuery("SELECT sp.id as student_id, sp.student_id as student_number, sp.name as student_name, c.name as class_name FROM student_profile sp LEFT JOIN classes c ON sp.class_id = c.id");
            while (rs.next()) {
                System.out.println("学号: " + rs.getString("student_number") + ", 姓名: " + rs.getString("student_name") + ", 班级: " + (rs.getString("class_name") != null ? rs.getString("class_name") : "未分配"));
            }
            
            // 关闭连接
            rs.close();
            stmt.close();
            conn.close();
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}