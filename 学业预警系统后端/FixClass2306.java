import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.sql.ResultSet;

public class FixClass2306 {
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
            
            // 1. 检查计科2306班是否存在
            System.out.println("\n1. 检查计科2306班是否存在:");
            ResultSet rs = stmt.executeQuery("SELECT id, name FROM classes WHERE name = '计科2306班'");
            if (rs.next()) {
                System.out.println("计科2306班已存在，ID: " + rs.getLong("id"));
            } else {
                System.out.println("计科2306班不存在，需要创建");
            }
            
            // 2. 创建计科2306班（如果不存在）
            System.out.println("\n2. 创建计科2306班:");
            int createResult = stmt.executeUpdate(
                "INSERT INTO classes (name, college_id, major_id, teacher_id, counselor_id) " +
                "SELECT '计科2306班', 3, 1, NULL, NULL " +
                "WHERE NOT EXISTS (SELECT 1 FROM classes WHERE name = '计科2306班')"
            );
            System.out.println("创建结果: " + (createResult > 0 ? "成功" : "已存在"));
            
            // 3. 获取计科2306班的ID
            System.out.println("\n3. 获取计科2306班的ID:");
            rs = stmt.executeQuery("SELECT id FROM classes WHERE name = '计科2306班'");
            long classId = 0;
            if (rs.next()) {
                classId = rs.getLong("id");
                System.out.println("计科2306班ID: " + classId);
            }
            
            // 4. 将2023020616学生移动到计科2306班
            System.out.println("\n4. 将2023020616学生移动到计科2306班:");
            int updateResult = stmt.executeUpdate(
                "UPDATE student_profile SET class_id = " + classId + " WHERE student_id = '2023020616'"
            );
            System.out.println("更新结果: " + (updateResult > 0 ? "成功" : "未更新"));
            
            // 5. 更新对标分析表中的班级ID
            System.out.println("\n5. 更新对标分析表中的班级ID:");
            int updateBenchmarkResult = stmt.executeUpdate(
                "UPDATE benchmark_analysis SET class_id = " + classId + " WHERE student_id = (SELECT id FROM student_profile WHERE student_id = '2023020616')"
            );
            System.out.println("更新结果: " + (updateBenchmarkResult > 0 ? "成功" : "未更新"));
            
            // 6. 验证修复结果
            System.out.println("\n6. 验证修复结果:");
            rs = stmt.executeQuery(
                "SELECT sp.student_id, sp.name, c.name AS class_name " +
                "FROM student_profile sp " +
                "LEFT JOIN classes c ON sp.class_id = c.id " +
                "WHERE sp.student_id = '2023020616'"
            );
            if (rs.next()) {
                System.out.println("学号: " + rs.getString("student_id"));
                System.out.println("姓名: " + rs.getString("name"));
                System.out.println("班级: " + rs.getString("class_name"));
            }
            
            // 7. 查看计科2306班的学生数量
            System.out.println("\n7. 查看计科2306班的学生数量:");
            rs = stmt.executeQuery(
                "SELECT c.name AS class_name, COUNT(sp.id) AS student_count " +
                "FROM classes c " +
                "LEFT JOIN student_profile sp ON c.id = sp.class_id " +
                "WHERE c.name = '计科2306班' " +
                "GROUP BY c.name"
            );
            if (rs.next()) {
                System.out.println("班级: " + rs.getString("class_name"));
                System.out.println("学生数量: " + rs.getInt("student_count"));
            }
            
            // 8. 查看所有班级
            System.out.println("\n8. 查看所有班级:");
            rs = stmt.executeQuery("SELECT id, name FROM classes");
            while (rs.next()) {
                System.out.println("ID: " + rs.getLong("id") + ", 名称: " + rs.getString("name"));
            }
            
            // 关闭连接
            rs.close();
            stmt.close();
            conn.close();
            
            System.out.println("\n修复完成！");
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}