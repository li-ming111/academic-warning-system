SET NAMES utf8mb4;
SET CHARACTER SET utf8mb4;

UPDATE majors SET short_name = '软工' WHERE code = '01';
UPDATE majors SET short_name = '计科' WHERE code = '02';
UPDATE majors SET short_name = '数据' WHERE code = '03';
UPDATE majors SET short_name = '智能' WHERE code = '04';
UPDATE majors SET short_name = '虚拟' WHERE code = '05';
UPDATE majors SET short_name = '电信' WHERE code = '06';
UPDATE majors SET short_name = '自动' WHERE code = '07';
UPDATE majors SET short_name = '人工' WHERE code = '08';
UPDATE majors SET short_name = '电商' WHERE code = '09';
UPDATE majors SET short_name = '物流' WHERE code = '10';
UPDATE majors SET short_name = '财管' WHERE code = '11';
UPDATE majors SET short_name = '网媒' WHERE code = '12';
UPDATE majors SET short_name = '设计' WHERE code = '13';
UPDATE majors SET short_name = '视传' WHERE code = '14';

-- 更新班级名称为新格式
UPDATE classes SET name = '计科2306班' WHERE id = 1 AND major_id = 2;

-- 验证
SELECT id, code, short_name FROM majors WHERE short_name IS NOT NULL;
SELECT id, name, major_id FROM classes;
