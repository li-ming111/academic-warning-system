@echo off
chcp 65001
cd /d d:\ABS
echo 开始初始化数据库业务数据...
"C:\Program Files\MySQL\MySQL Server 8.0\bin\mysql.exe" -u root -p123456 academic_warning_system < "学业预警系统后端\src\main\resources\init_business_data.sql"
echo 数据初始化完成！
pause
