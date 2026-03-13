import mysql.connector

# 数据库连接信息
host = "localhost"
port = 3306
user = "root"
password = "root"

try:
    # 连接数据库服务器
    conn = mysql.connector.connect(
        host=host,
        port=port,
        user=user,
        password=password
    )
    
    # 创建游标
    cursor = conn.cursor()
    
    # 检查数据库是否存在
    cursor.execute("SHOW DATABASES LIKE 'academic_warning_system'")
    db_exists = cursor.fetchone()
    
    if db_exists:
        print("数据库 academic_warning_system 存在")
        
        # 切换到该数据库
        cursor.execute("USE academic_warning_system")
        
        # 检查 users 表是否存在
        cursor.execute("SHOW TABLES LIKE 'users'")
        table_exists = cursor.fetchone()
        
        if table_exists:
            print("users 表存在")
            
            # 查看表结构
            cursor.execute("DESCRIBE users")
            print("\nusers 表结构:")
            for row in cursor.fetchall():
                print(row)
            
            # 查看表中的数据
            cursor.execute("SELECT COUNT(*) FROM users")
            count = cursor.fetchone()[0]
            print(f"\nusers 表中有 {count} 条记录")
            
            if count > 0:
                print("\n用户数据:")
                cursor.execute("SELECT username, password, role FROM users")
                for row in cursor.fetchall():
                    print(row)
        else:
            print("users 表不存在")
    else:
        print("数据库 academic_warning_system 不存在")
    
    # 关闭连接
    cursor.close()
    conn.close()
    
except mysql.connector.Error as e:
    print(f"数据库连接错误: {e}")
except Exception as e:
    print(f"发生错误: {e}")
