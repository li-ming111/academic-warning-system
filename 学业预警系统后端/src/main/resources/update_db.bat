@echo off
mysql -u root -p academic_warning_system -e "ALTER TABLE communication_logs ADD COLUMN status INT DEFAULT 0 COMMENT '状态：0=未读，1=已读', ADD COLUMN reply TEXT COMMENT '回复内容';"
echo Update completed!
pause
