#!/usr/bin/env python3
# -*- coding: utf-8 -*-
import mysql.connector
from datetime import datetime

# 连接数据库
conn = mysql.connector.connect(
    host='127.0.0.1',
    user='root',
    password='root',
    database='academic_warning_system',
    charset='utf8mb4',
    use_unicode=True
)

cursor = conn.cursor()

try:
    # 删除旧课程
    cursor.execute("SET FOREIGN_KEY_CHECKS = 0")
    cursor.execute("DELETE FROM scores WHERE course_id IN (574, 575, 576, 577, 578, 569, 570, 571, 572, 573)")
    cursor.execute("DELETE FROM courses WHERE id IN (574, 575, 576, 577, 578, 569, 570, 571, 572, 573)")
    cursor.execute("SET FOREIGN_KEY_CHECKS = 1")
    
    # 插入新课程
    courses = [
        ('INET001', '互联网程序设计', 4.00, 'required'),
        ('OS001', 'Linux操作系统', 4.00, 'required'),
        ('PYTHON001', 'Python程序设计', 4.00, 'required'),
        ('PSY001', '生活中的心理学', 4.00, 'required'),
        ('SE001', '软件工程', 3.50, 'required'),
    ]
    
    now = datetime.now()
    for code, name, credits, course_type in courses:
        sql = "INSERT INTO courses (code, name, credits, type, created_at, updated_at) VALUES (%s, %s, %s, %s, %s, %s)"
        cursor.execute(sql, (code, name, credits, course_type, now, now))
    
    conn.commit()
    
    # 查询新插入的课程
    cursor.execute("SELECT id, name, credits FROM courses ORDER BY id DESC LIMIT 5")
    results = cursor.fetchall()
    
    print("新插入的课程:")
    for row in results:
        print(f"ID: {row[0]}, 名称: {row[1]}, 学分: {row[2]}")
    
    # 获取最新5个课程的ID
    cursor.execute("SELECT id FROM courses ORDER BY id DESC LIMIT 5")
    course_ids = [row[0] for row in reversed(cursor.fetchall())]
    print(f"\n新课程ID列表: {course_ids}")
    
except Exception as e:
    print(f"错误: {e}")
    conn.rollback()
finally:
    cursor.close()
    conn.close()
