#!/usr/bin/env python3
import mysql.connector

try:
    # 连接数据库
    conn = mysql.connector.connect(
        host='127.0.0.1',
        user='root',
        password='root',  # 根据application.yml，密码是root
        database='academic_warning_system'
    )
    cursor = conn.cursor()

    # 删除现有的演示账号
    cursor.execute("DELETE FROM users WHERE username IN ('teacher001', 'counselor001', 'admin001')")
    
    # 插入新的演示账号
    users_data = [
        ('2023020616', '123456', 'student001@example.com', '13800138000', 1, 1),
        ('teacher001', '123456', 'teacher001@example.com', '13800138001', 2, 1),
        ('counselor001', '123456', 'counselor001@example.com', '13800138002', 4, 1),
        ('admin001', '123456', 'admin001@example.com', '13800138003', 3, 1)
    ]
    
    sql = "INSERT INTO users (username, password, email, phone, role, status) VALUES (%s, %s, %s, %s, %s, %s)"
    cursor.executemany(sql, users_data)
    
    conn.commit()
    print("✓ 演示账号添加成功")
    print("\n演示账号列表：")
    print("=" * 60)
    print("学生账号:   username=2023020616  password=123456")
    print("教师账号:   username=teacher001  password=teacher123")
    print("辅导员账号: username=counselor001 password=counselor123")
    print("管理员账号: username=admin001    password=admin123")
    print("=" * 60)
    
    cursor.close()
    conn.close()
except Exception as e:
    print(f"✗ 错误: {e}")
