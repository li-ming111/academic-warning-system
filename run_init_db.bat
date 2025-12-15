@echo off
cd /d d:\ABS
"C:\Program Files\MySQL\MySQL Server 8.0\bin\mysql.exe" -u root -p123456 < "学业预警系统后端\src\main\resources\init_database.sql"
echo Database initialization completed
