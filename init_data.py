#!/usr/bin/env python3
# -*- coding: utf-8 -*-

import mysql.connector
from mysql.connector import Error
import sys

def execute_sql_script(sql_file, user='root', password='root', host='localhost'):
    """Execute SQL script from file"""
    try:
        conn = mysql.connector.connect(
            host=host,
            user=user,
            password=password,
            database='academic_warning_system',
            charset='utf8mb4'
        )
        cursor = conn.cursor()
        
        with open(sql_file, 'r', encoding='utf-8') as f:
            sql_content = f.read()
            
        # Split SQL statements by semicolon
        statements = sql_content.split(';')
        
        for statement in statements:
            statement = statement.strip()
            if statement:
                try:
                    cursor.execute(statement)
                except Error as e:
                    print(f"Error executing: {statement[:100]}...")
                    print(f"Error: {e}")
                    
        conn.commit()
        print("✓ 数据初始化成功")
        
    except Error as e:
        print(f"Database error: {e}")
        sys.exit(1)
    finally:
        if conn.is_connected():
            cursor.close()
            conn.close()

if __name__ == '__main__':
    execute_sql_script('init_complete_data.sql')
