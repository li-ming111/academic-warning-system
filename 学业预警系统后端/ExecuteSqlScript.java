import java.io.BufferedReader;
import java.io.FileReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

public class ExecuteSqlScript {
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
            
            // 读取并执行 SQL 脚本
            BufferedReader reader = new BufferedReader(new FileReader("delete_extra_classes.sql"));
            String line;
            StringBuilder sql = new StringBuilder();
            
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (!line.isEmpty() && !line.startsWith("--")) {
                    sql.append(line);
                    if (line.endsWith(";")) {
                        String sqlStatement = sql.toString();
                        System.out.println("执行 SQL: " + sqlStatement);
                        stmt.execute(sqlStatement);
                        sql.setLength(0);
                    }
                }
            }
            
            // 关闭资源
            reader.close();
            stmt.close();
            conn.close();
            
            System.out.println("SQL 脚本执行完成");
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}