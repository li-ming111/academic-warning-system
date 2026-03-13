import mysql.connector

# 数据库连接信息
host = "localhost"
port = 3306
user = "root"
password = "root"
database = "academic_warning_system"

try:
    # 连接数据库
    conn = mysql.connector.connect(
        host=host,
        port=port,
        user=user,
        password=password,
        database=database
    )
    
    # 创建游标
    cursor = conn.cursor()
    
    # 查询用户表
    query = "SELECT username, password, role FROM users"
    cursor.execute(query)
    
    # 获取结果
    results = cursor.fetchall()
    
    # 打印结果
    print("=== 数据库中的用户账号信息 ===")
    print(f"{'用户名':<15} {'密码':<60} {'角色'}")
    print("-" * 100)
    
    role_map = {
        1: "学生",
        2: "教师",
        3: "管理员",
        4: "辅导员"
    }
    
    for row in results:
        username, password_hash, role = row
        role_name = role_map.get(role, "未知")
        print(f"{username:<15} {password_hash:<60} {role_name}")
    
    # 关闭连接
    cursor.close()
    conn.close()
    
    print("\n查询完成！")
    
except mysql.connector.Error as e:
    print(f"数据库连接错误: {e}")
except Exception as e:
    print(f"发生错误: {e}")
