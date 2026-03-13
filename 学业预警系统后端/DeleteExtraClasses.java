import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

public class DeleteExtraClasses {
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
            System.out.println("当前班级列表：");
            ResultSet rs = stmt.executeQuery("SELECT id, name FROM classes");
            while (rs.next()) {
                System.out.println("ID: " + rs.getLong("id") + ", 名称: " + rs.getString("name"));
            }
            
            // 删除除了计科2306班以外的所有班级
            int deleted = stmt.executeUpdate("DELETE FROM classes WHERE name != '计科2306班'");
            System.out.println("删除了 " + deleted + " 个班级");
            
            // 查看删除后的班级
            System.out.println("删除后班级列表：");
            rs = stmt.executeQuery("SELECT id, name FROM classes");
            while (rs.next()) {
                System.out.println("ID: " + rs.getLong("id") + ", 名称: " + rs.getString("name"));
            }
            
            // 关闭资源
            rs.close();
            stmt.close();
            conn.close();
            
            System.out.println("操作完成");
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}