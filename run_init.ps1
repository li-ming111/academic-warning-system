# 重新初始化数据库成绩表
$mysqlPath = "D:\JAVA\mysql\bin\mysql.exe"
$sqlFile = "d:\ABS\学业预警系统后端\src\main\resources\init_business_data.sql"

# 执行清空
& $mysqlPath -u root -proot academic_warning_system -e "SET FOREIGN_KEY_CHECKS=0; TRUNCATE TABLE scores; SET FOREIGN_KEY_CHECKS=1;"

# 获取文件内容并执行
$sqlContent = Get-Content $sqlFile -Raw
& $mysqlPath -u root -proot academic_warning_system -e $sqlContent
