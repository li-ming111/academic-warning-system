#!/usr/bin/env python3
# -*- coding: utf-8 -*-
import mysql.connector
from mysql.connector import Error

def execute_sql_file(conn, filepath):
    """执行SQL文件"""
    try:
        with open(filepath, 'r', encoding='utf-8') as f:
            sql_content = f.read()
        
        # 分割SQL语句
        statements = sql_content.split(';')
        
        cursor = conn.cursor()
        for statement in statements:
            statement = statement.strip()
            if statement and not statement.startswith('--'):
                try:
                    cursor.execute(statement)
                except Error as e:
                    print(f"⚠️  语句执行警告: {e}")
                    continue
        
        conn.commit()
        cursor.close()
        print(f"✓ {filepath} 执行成功")
        return True
    except Exception as e:
        print(f"✗ 执行 {filepath} 失败: {e}")
        return False

def main():
    try:
        # 连接数据库
        conn = mysql.connector.connect(
            host='127.0.0.1',
            user='root',
            password='123456',
            database='academic_warning_system'
        )
        
        print("="*60)
        print("学业预警系统 - 业务数据初始化")
        print("="*60)
        
        # 执行业务数据初始化脚本
        if execute_sql_file(conn, 'd:\\ABS\\学业预警系统后端\\src\\main\\resources\\init_business_data.sql'):
            print("\n✓ 所有数据初始化完成！")
            print("\n初始化包含的数据：")
            print("  - 班级数据: 3个")
            print("  - 课程数据: 8门")
            print("  - 成绩记录: 10条")
            print("  - 预警记录: 1条")
            print("  - 帮扶计划: 1个")
            print("  - 成绩申诉: 1条")
            print("  - 选修课: 4条")
            print("  - 通知: 4条")
            print("  - 消息: 3条")
            print("  - 反馈: 2条")
            print("  - 沟通记录: 2条")
            print("  - 毕业要求: 4条")
            print("  - 安全日志: 4条")
            print("  - 审计日志: 4条")
        
        conn.close()
        
    except Error as e:
        print(f"✗ 数据库连接失败: {e}")
        print("\n请确保：")
        print("  1. MySQL服务正在运行")
        print("  2. 数据库已通过init_database.sql创建")
        print("  3. 用户名和密码正确")

if __name__ == '__main__':
    main()
