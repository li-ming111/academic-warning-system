#!/usr/bin/env python3
# -*- coding: utf-8 -*-

import mysql.connector
from mysql.connector import Error

def execute_init():
    conn = None
    try:
        # Connect to MySQL server
        print("Connecting to MySQL...")
        conn = mysql.connector.connect(
            host='localhost',
            user='root',
            password='root',
            database='academic_warning_system',
            charset='utf8mb4',
            use_unicode=True
        )
        print("✓ Connected to database")
        cursor = conn.cursor()
        
        # Read and execute SQL file
        print("Reading SQL file...")
        with open('final_init_data.sql', 'r', encoding='utf-8') as f:
            sql_content = f.read()
        print(f"✓ Read {len(sql_content)} characters from SQL file")
        
        # Split and execute statements
        statements = sql_content.split(';')
        count = 0
        for statement in statements:
            stmt = statement.strip()
            if stmt and not stmt.startswith('--'):
                try:
                    cursor.execute(stmt)
                    count += 1
                except Error as e:
                    print(f"✗ Error: {e}")
                    print(f"  Statement: {stmt[:100]}...")
        
        conn.commit()
        print(f"\n✓ Database initialization completed successfully!")
        print(f"✓ Executed {count} SQL statements")
        
    except Error as e:
        print(f"Database error: {e}")
        import traceback
        traceback.print_exc()
    finally:
        if conn and conn.is_connected():
            cursor.close()
            conn.close()

if __name__ == '__main__':
    execute_init()
