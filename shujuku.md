# 学业预警系统 - 数据库设计文档

## 目录
1. [数据库概述](#数据库概述)
2. [核心表结构](#核心表结构)
3. [表之间的关系](#表之间的关系)
4. [数据流向](#数据流向)
5. [关键业务逻辑](#关键业务逻辑)

---

## 数据库概述

**数据库名**: `academic_warning_system`  
**数据库类型**: MySQL 8.0  
**总表数**: 26 张表

### 主要模块
- 👤 **用户管理**: users, student_profile, teacher_profile, counselor_profile, admin_profile
- 📚 **教学管理**: courses, classes, majors, colleges, enrollments, elective_modules
- 📊 **成绩管理**: scores, score_appeals, benchmark_analysis
- ⚠️ **预警管理**: academic_warnings, warning_rules
- 📢 **帮扶管理**: assistance_plans, assistance_evaluations, feedbacks
- 🔔 **通知管理**: notifications, subscription_preferences
- 📝 **日志管理**: audit_logs, security_logs, communication_logs

---

## 核心表结构

### 1. users（用户表）
**用途**: 存储所有系统用户（学生、教师、辅导员、管理员）的登录信息

| 字段名 | 类型 | 约束 | 说明 |
|--------|------|------|------|
| id | bigint | PK | 主键 |
| username | varchar(50) | UNI, NOT NULL | 用户名/学号 |
| password | varchar(255) | NOT NULL | 密码（加密存储） |
| email | varchar(100) | | 邮箱 |
| phone | varchar(20) | | 电话 |
| role | tinyint(1) | NOT NULL | 用户角色（1=学生、2=教师、3=辅导员、4=管理员） |
| status | tinyint(1) | NOT NULL | 账户状态（0=禁用、1=启用） |
| deleted_at | timestamp | | 软删除时间（NULL=未删除） |
| created_at | timestamp | DEFAULT CURRENT_TIMESTAMP | 创建时间 |
| updated_at | timestamp | DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP | 更新时间 |

**索引设计**:
```sql
CREATE UNIQUE INDEX idx_username ON users(username);
CREATE INDEX idx_role ON users(role);
CREATE INDEX idx_status ON users(status);
```

**角色说明**:
- role=1: 学生
- role=2: 教师
- role=3: 辅导员/班主任
- role=4: 管理员

---

### 2. student_profile（学生档案表）
**用途**: 存储学生的详细信息和学业信息

| 字段名 | 类型 | 约束 | 说明 |
|--------|------|------|------|
| id | bigint | PK | 主键 |
| user_id | bigint | UNI, FK→users.id | 关联的用户ID |
| student_id | varchar(20) | UNI | 学号（10位数字或10位数字+Z，需CHECK约束） |
| name | varchar(50) | NOT NULL | 学生姓名 |
| college_id | bigint | FK→colleges.id | 所属学院ID |
| major_id | bigint | FK→majors.id | 所属专业ID |
| class_id | bigint | FK→classes.id | 所属班级ID |
| privacy_level | tinyint(1) | NOT NULL, DEFAULT 2 | 隐私级别（1=公开, 2=仅师生, 3=仅辅导员, 4=仅管理员, 5=私密） |
| created_at | timestamp | DEFAULT CURRENT_TIMESTAMP | 创建时间 |
| updated_at | timestamp | DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP | 更新时间 |

**学号格式CHECK约束**:
```sql
ALTER TABLE student_profile ADD CONSTRAINT chk_student_id 
  CHECK (LENGTH(student_id)=10 OR (LENGTH(student_id)=11 AND RIGHT(student_id,1)='Z'));
```

**索引设计**:
```sql
CREATE UNIQUE INDEX idx_student_id ON student_profile(student_id);
CREATE INDEX idx_user_id ON student_profile(user_id);
CREATE INDEX idx_class_id ON student_profile(class_id);
```

**学号格式说明**:
- 本科生: 10位纯数字，如 `2023020616`
  - 前4位为入学年份（如2023）
- 专升本: 10位数字+Z，如 `2023020616Z`

**关键映射**:
```
users.id=1 (username='2023020616')
↓
student_profile.user_id=1, student_id='2023020616'
```

---

### 3. scores（成绩表）
**用途**: 存储学生的课程成绩记录

| 字段名 | 类型 | 约束 | 说明 |
|--------|------|------|------|
| id | bigint | PK | 主键 |
| student_id | bigint | NOT NULL, FK→student_profile.id | 学生档案ID |
| course_id | bigint | NOT NULL, FK→courses.id | 课程ID |
| semester | varchar(50) | NOT NULL | 学期（格式：YYYY-YYYY-N，如2023-2024-01） |
| regular_score | decimal(5,2) | | 平时成绩 |
| final_score | decimal(5,2) | | 期末成绩 |
| score_total | decimal(5,2) | NOT NULL | 总分 |
| grade_point | decimal(5,2) | NOT NULL | 绩点（GPA），根据总分自动计算 |
| reason_for_change | varchar(500) | | 成绩变更原因 |
| modified_by | bigint | FK→users.id | 修改人ID |
| created_at | timestamp | DEFAULT CURRENT_TIMESTAMP | 创建时间 |
| updated_at | timestamp | DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP | 更新时间 |

**索引设计**:
```sql
CREATE INDEX idx_student_semester ON scores(student_id, semester);
CREATE INDEX idx_course_id ON scores(course_id);
CREATE INDEX idx_semester ON scores(semester);
```

**学期格式统一规范**:
- **格式**: `YYYY-YYYY+1-N`
- **秋季学期**: `YYYY-YYYY+1-1`（9月-次年1月）
  - 示例: `2023-2024-1`（2023年秋季）
- **春季学期**: `YYYY-YYYY+1-2`（次年3月-8月）
  - 示例: `2023-2024-2`（2024年春季）

⚠️ **重要**：统一采用不含前导零的N值（1或2），避免01/02导致字符串排序错误。

**示例时间轴**:
```
2023-2024-1 (2023.09 - 2024.01) → 秋季学期
2023-2024-2 (2024.03 - 2024.08) → 春季学期
2024-2025-1 (2024.09 - 2025.01) → 秋季学期
2024-2025-2 (2025.03 - 2025.08) → 春季学期
```

**绩点换算规则**:
```
分数 90-100 → GPA 4.0 (A)
分数 80-89  → GPA 3.0-3.9 (A-)
分数 70-79  → GPA 2.0-2.9 (B)
分数 60-69  → GPA 1.0-1.9 (D)
分数 <60    → GPA 0.0-1.0 (F)
```

---

### 4. courses（课程表）
**用途**: 存储所有课程信息

| 字段名 | 类型 | 约束 | 说明 |
|--------|------|------|------|
| id | bigint | PK | 主键 |
| code | varchar(20) | UNI, NOT NULL | 课程代码 |
| name | varchar(100) | NOT NULL | 课程名称 |
| credits | decimal(5,2) | NOT NULL | 学分 |
| type | varchar(20) | NOT NULL | 课程类型（必修/选修）【注：删除is_elective冗余字段】 |
| module_id | bigint | FK→elective_modules.id | 模块ID（若为选修） |
| score_template | varchar(50) | | 成绩模板 |
| created_at | timestamp | DEFAULT CURRENT_TIMESTAMP | 创建时间 |
| updated_at | timestamp | DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP | 更新时间 |

**索引设计**:
```sql
CREATE UNIQUE INDEX idx_course_code ON courses(code);
CREATE INDEX idx_type ON courses(type);
```

---

### 5. classes（班级表）
**用途**: 存储班级信息

| 字段名 | 类型 | 约束 | 说明 |
|--------|------|------|------|
| id | bigint | PK | 主键 |
| name | varchar(50) | NOT NULL | 班级名称 |
| college_id | bigint | FK→colleges | 所属学院ID |
| teacher_id | bigint | FK→users | 班主任ID |
| counselor_id | bigint | FK→users | 辅导员ID |
| created_at | timestamp | | 创建时间 |
| updated_at | timestamp | | 更新时间 |

---

### 6. colleges（学院表）
**用途**: 存储学院/学部信息

| 字段名 | 类型 | 约束 | 说明 |
|--------|------|------|------|
| id | bigint | PK | 主键 |
| name | varchar(100) | UNI, NOT NULL | 学院名称 |
| created_at | timestamp | | 创建时间 |
| updated_at | timestamp | | 更新时间 |

---

### 7. majors（专业表）
**用途**: 存储专业信息

| 字段名 | 类型 | 约束 | 说明 |
|--------|------|------|------|
| id | bigint | PK | 主键 |
| code | varchar(10) | UNI | 专业代码 |
| name | varchar(100) | NOT NULL | 专业名称 |
| college_id | bigint | FK→colleges | 所属学院ID |
| created_at | timestamp | | 创建时间 |
| updated_at | timestamp | | 更新时间 |

---

### 8. academic_warnings（学业预警表）
**用途**: 存储学生的学业预警记录

| 字段名 | 类型 | 约束 | 说明 |
|--------|------|------|------|
| id | bigint | PK | 主键 |
| student_id | bigint | NOT NULL, FK→student_profile.id | 学生档案ID |
| rule_id | bigint | FK→warning_rules.id | 适用的规则ID |
| college_id | bigint | FK→colleges.id | 学院ID |
| class_id | bigint | NOT NULL, FK→classes.id | 班级ID |
| course_id | bigint | FK→courses.id | 课程ID（若为课程相关预警） |
| warning_level | varchar(20) | NOT NULL | 预警等级（low/medium/high） |
| title | varchar(100) | | 预警标题 |
| description | text | | 预警描述 |
| status | varchar(20) | NOT NULL, DEFAULT 'pending' | 预警状态（pending/processing/resolved） |
| appeal_status | varchar(20) | NOT NULL, DEFAULT 'none' | 申诉状态（none/pending/approved/rejected） |
| created_at | timestamp | DEFAULT CURRENT_TIMESTAMP | 创建时间 |
| updated_at | timestamp | DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP | 更新时间 |

**索引设计**:
```sql
CREATE INDEX idx_student_status ON academic_warnings(student_id, status);
CREATE INDEX idx_warning_level ON academic_warnings(warning_level);
```

**预警等级说明**:
```
低级预警 (low):       挂科 < 3 科
中级预警 (medium):    挂科 3-5 科
高级预警 (high):      挂科 > 5 科
```

---

### 9. warning_rules（预警规则表）
**用途**: 存储预警规则定义

| 字段名 | 类型 | 约束 | 说明 |
|--------|------|------|------|
| id | bigint | PK | 主键 |
| name | varchar(100) | NOT NULL | 规则名称 |
| description | text | | 规则描述 |
| rule_condition | text | NOT NULL | 规则条件（JSON格式，见下方示例） |
| warning_level | varchar(20) | NOT NULL | 触发的预警等级【注：改为warning_level保持与academic_warnings一致】 |
| status | tinyint(1) | NOT NULL, DEFAULT 1 | 规则状态（0=禁用、1=启用） |
| created_at | timestamp | DEFAULT CURRENT_TIMESTAMP | 创建时间 |
| updated_at | timestamp | DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP | 更新时间 |

**rule_condition JSON示例**:
```json
{
  "condition_type": "failed_course_count",
  "operator": ">=",
  "value": 3,
  "semester_scope": "current_semester",
  "description": "单学期挂科课程数 >= 3 时触发中级预警"
}
```

**索引设计**:
```sql
CREATE INDEX idx_warning_level ON warning_rules(warning_level);
CREATE INDEX idx_status ON warning_rules(status);
```

---

### 10. benchmark_analysis（对标分析表）
**用途**: 存储学生的对标分析数据（GPA对比）

| 字段名 | 类型 | 约束 | 说明 |
|--------|------|------|------|
| id | bigint | PK | 主键 |
| student_id | bigint | NOT NULL, FK→student_profile.id | 学生档案ID |
| semester | varchar(50) | NOT NULL | 学期（格式：YYYY-YYYY-N） |
| student_gpa | decimal(5,2) | NOT NULL | 学生GPA |
| class_avg_gpa | decimal(5,2) | NOT NULL | 班级平均GPA |
| major_avg_gpa | decimal(5,2) | NOT NULL | 专业平均GPA |
| class_rank | int | NOT NULL | 班级排名 |
| major_rank | int | NOT NULL | 专业排名 |
| class_total | int | NOT NULL | 班级总人数 |
| major_total | int | NOT NULL | 专业总人数 |
| credits_on_track | tinyint(1) | NOT NULL | 学分是否达标（0=否、1=是） |
| courses_passed | int | NOT NULL | 通过课程数 |
| courses_failed | int | NOT NULL | 不及格课程数 |
| created_at | timestamp | DEFAULT CURRENT_TIMESTAMP | 创建时间 |
| updated_at | timestamp | DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP | 更新时间 |

**索引设计**:
```sql
CREATE UNIQUE INDEX idx_student_semester ON benchmark_analysis(student_id, semester);
CREATE INDEX idx_semester ON benchmark_analysis(semester);
```

---

## 表之间的关系

### 关系图

```
users (用户表)
├── PK: id
├── 1:1→ student_profile (user_id)
├── 1:1→ teacher_profile (user_id)
├── 1:1→ counselor_profile (user_id)
└── 1:1→ admin_profile (user_id)

student_profile (学生档案表)
├── FK: user_id → users.id
├── FK: college_id → colleges.id
├── FK: major_id → majors.id
├── FK: class_id → classes.id
├── 1:N→ scores (student_id)
├── 1:N→ academic_warnings (student_id)
└── 1:N→ benchmark_analysis (student_id)

scores (成绩表)
├── FK: student_id → student_profile.id
└── FK: course_id → courses.id

courses (课程表)
├── FK: module_id → elective_modules.id
└── 1:N→ scores (course_id)

classes (班级表)
├── FK: college_id → colleges.id
├── FK: teacher_id → users.id
├── FK: counselor_id → users.id
└── 1:N→ student_profile (class_id)

colleges (学院表)
├── 1:N→ majors (college_id)
├── 1:N→ classes (college_id)
└── 1:N→ student_profile (college_id)

majors (专业表)
└── FK: college_id → colleges.id

academic_warnings (学业预警表)
├── FK: student_id → student_profile.id
├── FK: rule_id → warning_rules.id
├── FK: college_id → colleges.id
└── FK: course_id → courses.id

warning_rules (预警规则表)
└── 1:N→ academic_warnings (rule_id)

benchmark_analysis (对标分析表)
└── FK: student_id → student_profile.id
```

---

## 数据流向

### 1. 学生登录流程

```
1. 用户输入用户名密码 (username='2023020616', password='xxx')
   ↓
2. 查询 users 表 WHERE username='2023020616'
   ↓
3. 获得 users.id=1
   ↓
4. 查询 student_profile WHERE user_id=1
   ↓
5. 获得学生档案信息（student_id, name, college_id, major_id, class_id）
```

### 2. 成绩查询流程

```
1. 学生查看成绩，选择学期 (semester='2023-2024-01')
   ↓
2. 查询 scores WHERE student_id=? AND semester='2023-2024-01'
   ↓
3. 获得成绩列表，通过 course_id 关联 courses 表获取课程名称
   ↓
4. 计算总体 GPA、预警标志等信息
```

### 3. 预警生成流程

```
1. 定时任务或手动触发预警检查
   ↓
2. 查询所有学生的成绩统计（按学期分组）
   ↓
3. 计算每个学生的挂科课程数
   ↓
4. 根据 warning_rules 表的规则判定预警等级
   ↓
5. 如果触发规则，在 academic_warnings 表中插入记录
   ↓
6. 根据学生的 subscription_preferences 推送通知
```

### 4. 对标分析流程

```
1. 查询学生的所有成绩 (scores)
   ↓
2. 计算学生当前学期的 GPA
   ↓
3. 查询班级/专业内所有学生的 GPA，计算平均值
   ↓
4. 计算学生在班级/专业内的排名
   ↓
5. 在 benchmark_analysis 表中插入或更新记录
```

---

## 关键业务逻辑

### 1. 学号与用户名的映射

| 表 | 字段 | 值 | 说明 |
|-----|------|-----|------|
| users | username | 2023020616 | 登录时使用的用户名 |
| users | role | 1 | 角色为学生 |
| student_profile | user_id | 1 | 指向上述用户 |
| student_profile | student_id | 2023020616 | 学号（与username一致） |

**示例**:
```sql
-- 学生 2023020616 的完整信息查询
SELECT 
    u.id, u.username, u.password, sp.student_id, sp.name,
    c.name as college_name, m.name as major_name, cls.name as class_name
FROM users u
JOIN student_profile sp ON u.id = sp.user_id
LEFT JOIN colleges c ON sp.college_id = c.id
LEFT JOIN majors m ON sp.major_id = m.id
LEFT JOIN classes cls ON sp.class_id = cls.id
WHERE u.username = '2023020616';
```

### 2. 学期格式规范

**格式**: `YYYY-YYYY-N`
- `YYYY`: 开始年份
- `YYYY`: 结束年份（通常为开始年份+1）
- `N`: 学期号（1=秋季，2=春季）

**示例**:
```
2023-2024-01  → 2023年秋季学期
2023-2024-02  → 2024年春季学期
2024-2025-01  → 2024年秋季学期
2024-2025-02  → 2025年春季学期
```

**学号与学期的关系**:
- 学号前4位为入学年份
- 学号 2023020616 → 从 2023-2024-01 开始

### 3. 预警规则与等级

**挂科数目判定规则**:
- 预警等级判定基于**单学期累计挂科课程数**
- 累计挂科数统计该学期 `score_total < 60` 的课程数量

```javascript
const failedCount = scores.filter(s => s.semester === currentSemester && s.score_total < 60).length;

if (failedCount < 3) {
    warningLevel = 'low';      // 低级预警
} else if (failedCount >= 3 && failedCount <= 5) {
    warningLevel = 'medium';   // 中级预警
} else if (failedCount > 5) {
    warningLevel = 'high';     // 高级预警
}
```

**预警等级对应标题**:
| 等级 | 标题 | 触发条件 |
|------|------|----------|
| low | 低级预警 | 挂科课程 < 3 科 |
| medium | 中级预警 | 挂科课程 3-5 科 |
| high | 高级预警 | 挂科课程 > 5 科 |

### 4. GPA计算与绩点转换

**单课程绩点转换规则**（精确标准）:
| 分数范围 | 绩点 | 等级 |
|---------|------|------|
| 90-100 | 4.0 | A |
| 85-89 | 3.7 | A- |
| 80-84 | 3.3 | B+ |
| 75-79 | 2.7 | B |
| 70-74 | 2.3 | B- |
| 65-69 | 1.7 | C |
| 60-64 | 1.0 | D |
| <60 | 0.0 | F（不及格） |

**总GPA计算**（加权平均）:
```javascript
// 公式：学期 GPA = Σ(单课程绩点 × 学分) / Σ(学分)
totalGPA = sum(gradePoint[i] × credits[i]) / sum(credits[i])

// 示例
课程1: 3学分, 总分92 → 绩点4.0 → 12.0
课程2: 2学分, 总分78 → 绩点2.7 → 5.4
课程3: 4学分, 总分55 → 绩点0.0 → 0.0

学期GPA = (12.0 + 5.4 + 0.0) / (3 + 2 + 4) = 17.4 / 9 = 1.93
```

**代码实现**:
```javascript
function calculateGPA(scores) {
  let totalPoints = 0;
  let totalCredits = 0;
  
  scores.forEach(score => {
    const gradePoint = getGradePoint(score.score_total);
    totalPoints += gradePoint * score.credits;
    totalCredits += score.credits;
  });
  
  return totalCredits > 0 ? (totalPoints / totalCredits).toFixed(2) : 0;
}

function getGradePoint(score) {
  if (score >= 90) return 4.0;
  if (score >= 85) return 3.7;
  if (score >= 80) return 3.3;
  if (score >= 75) return 2.7;
  if (score >= 70) return 2.3;
  if (score >= 65) return 1.7;
  if (score >= 60) return 1.0;
  return 0.0;
}
```

### 5. 对标分析指标（基于benchmark_analysis表）

**计算班级平均GPA**:
```sql
-- 计算学生该学期所有课程的加权GPA
SELECT 
  sp.id as student_id,
  s.semester,
  ROUND(SUM(s.grade_point * c.credits) / SUM(c.credits), 2) as student_gpa,
  sp.class_id
FROM scores s
JOIN student_profile sp ON s.student_id = sp.id
JOIN courses c ON s.course_id = c.id
WHERE s.semester = ?
GROUP BY sp.id, s.semester;

-- 计算班级平均GPA
SELECT 
  sp.class_id,
  ROUND(AVG(ROUND(SUM(s.grade_point * c.credits) / SUM(c.credits), 2)), 2) as class_avg_gpa
FROM scores s
JOIN student_profile sp ON s.student_id = sp.id
JOIN courses c ON s.course_id = c.id
WHERE s.semester = ? AND sp.class_id = ?
GROUP BY sp.class_id;
```

**计算班级排名**:
```sql
-- 基于学期GPA的班级排名
SELECT 
  sp.id as student_id,
  ba1.student_gpa,
  COUNT(*) + 1 as class_rank
FROM benchmark_analysis ba1
JOIN student_profile sp ON ba1.student_id = sp.id
WHERE ba1.semester = ?
  AND sp.class_id = ?
  AND ba1.student_gpa > (
      SELECT ba2.student_gpa
      FROM benchmark_analysis ba2
      WHERE ba2.student_id = ? AND ba2.semester = ?
  )
GROUP BY sp.id;
```

---

## 附录：完整表结构清单

### 用户相关表（5张）

#### 3. teacher_profile（教师档案表）
| 字段名 | 类型 | 约束 | 说明 |
|--------|------|------|------|
| id | bigint | PK | 主键 |
| user_id | bigint | UNI, FK→users.id | 关联用户ID |
| teacher_no | varchar(20) | UNI, NOT NULL | 教师工号 |
| name | varchar(50) | NOT NULL | 教师姓名 |
| college_id | bigint | FK→colleges.id | 所属学院ID |
| title | varchar(50) | | 职称（教授/副教授/讲师） |
| created_at | timestamp | DEFAULT CURRENT_TIMESTAMP | 创建时间 |
| updated_at | timestamp | DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP | 更新时间 |

#### 4. counselor_profile（辅导员档案表）
| 字段名 | 类型 | 约束 | 说明 |
|--------|------|------|------|
| id | bigint | PK | 主键 |
| user_id | bigint | UNI, FK→users.id | 关联用户ID |
| counselor_no | varchar(20) | UNI, NOT NULL | 辅导员工号 |
| name | varchar(50) | NOT NULL | 辅导员姓名 |
| college_id | bigint | FK→colleges.id | 所属学院ID |
| phone | varchar(20) | | 联系电话 |
| created_at | timestamp | DEFAULT CURRENT_TIMESTAMP | 创建时间 |
| updated_at | timestamp | DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP | 更新时间 |

#### 5. admin_profile（管理员档案表）
| 字段名 | 类型 | 约束 | 说明 |
|--------|------|------|------|
| id | bigint | PK | 主键 |
| user_id | bigint | UNI, FK→users.id | 关联用户ID |
| admin_no | varchar(20) | UNI, NOT NULL | 管理员工号 |
| name | varchar(50) | NOT NULL | 管理员姓名 |
| department | varchar(100) | | 部门 |
| created_at | timestamp | DEFAULT CURRENT_TIMESTAMP | 创建时间 |
| updated_at | timestamp | DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP | 更新时间 |

### 教学管理表（4张）

#### 5. classes（班级表）
| 字段名 | 类型 | 约束 | 说明 |
|--------|------|------|------|
| id | bigint | PK | 主键 |
| name | varchar(50) | NOT NULL | 班级名称 |
| college_id | bigint | NOT NULL, FK→colleges.id | 所属学院ID |
| teacher_id | bigint | FK→users.id | 班主任ID |
| counselor_id | bigint | FK→users.id | 辅导员ID |
| created_at | timestamp | DEFAULT CURRENT_TIMESTAMP | 创建时间 |
| updated_at | timestamp | DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP | 更新时间 |

#### 6. colleges（学院表）
| 字段名 | 类型 | 约束 | 说明 |
|--------|------|------|------|
| id | bigint | PK | 主键 |
| name | varchar(100) | UNI, NOT NULL | 学院名称 |
| code | varchar(20) | UNI | 学院代码 |
| created_at | timestamp | DEFAULT CURRENT_TIMESTAMP | 创建时间 |
| updated_at | timestamp | DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP | 更新时间 |

#### 7. majors（专业表）
| 字段名 | 类型 | 约束 | 说明 |
|--------|------|------|------|
| id | bigint | PK | 主键 |
| code | varchar(10) | UNI, NOT NULL | 专业代码 |
| name | varchar(100) | NOT NULL | 专业名称 |
| college_id | bigint | NOT NULL, FK→colleges.id | 所属学院ID |
| created_at | timestamp | DEFAULT CURRENT_TIMESTAMP | 创建时间 |
| updated_at | timestamp | DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP | 更新时间 |

#### 11. enrollments（选课表）
| 字段名 | 类型 | 约束 | 说明 |
|--------|------|------|------|
| id | bigint | PK | 主键 |
| student_id | bigint | NOT NULL, FK→student_profile.id | 学生档案ID |
| course_id | bigint | NOT NULL, FK→courses.id | 课程ID |
| semester | varchar(50) | NOT NULL | 学期 |
| enrollment_status | varchar(20) | NOT NULL, DEFAULT 'active' | 选课状态（active/dropped/completed） |
| created_at | timestamp | DEFAULT CURRENT_TIMESTAMP | 创建时间 |
| updated_at | timestamp | DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP | 更新时间 |

### 预警与帮扶表（5张）

#### 15. score_appeals（成绩申诉表）
| 字段名 | 类型 | 约束 | 说明 |
|--------|------|------|------|
| id | bigint | PK | 主键 |
| student_id | bigint | NOT NULL, FK→student_profile.id | 学生档案ID |
| score_id | bigint | NOT NULL, FK→scores.id | 成绩ID |
| reason | varchar(100) | NOT NULL | 申诉原因 |
| description | text | | 申诉详情 |
| appeal_status | varchar(20) | NOT NULL, DEFAULT 'pending' | 申诉状态（pending/approved/rejected） |
| teacher_reply | text | | 教师回复 |
| created_at | timestamp | DEFAULT CURRENT_TIMESTAMP | 创建时间 |
| updated_at | timestamp | DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP | 更新时间 |

#### 16. assistance_plans（帮扶计划表）
| 字段名 | 类型 | 约束 | 说明 |
|--------|------|------|------|
| id | bigint | PK | 主键 |
| student_id | bigint | NOT NULL, FK→student_profile.id | 学生档案ID |
| warning_id | bigint | FK→academic_warnings.id | 关联的预警ID |
| plan_title | varchar(100) | NOT NULL | 帮扶计划标题 |
| plan_description | text | | 帮扶计划详情 |
| counselor_id | bigint | FK→users.id | 辅导员ID |
| plan_status | varchar(20) | NOT NULL, DEFAULT 'active' | 计划状态（active/completed/terminated） |
| progress_percentage | int | DEFAULT 0 | 完成进度（0-100） |
| created_at | timestamp | DEFAULT CURRENT_TIMESTAMP | 创建时间 |
| updated_at | timestamp | DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP | 更新时间 |

#### 17. assistance_evaluations（帮扶评估表）
| 字段名 | 类型 | 约束 | 说明 |
|--------|------|------|------|
| id | bigint | PK | 主键 |
| plan_id | bigint | NOT NULL, FK→assistance_plans.id | 帮扶计划ID |
| evaluator_id | bigint | FK→users.id | 评估者ID |
| evaluation_score | int | | 评估分数（0-100） |
| evaluation_content | text | | 评估内容 |
| evaluation_date | timestamp | DEFAULT CURRENT_TIMESTAMP | 评估时间 |
| created_at | timestamp | DEFAULT CURRENT_TIMESTAMP | 创建时间 |
| updated_at | timestamp | DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP | 更新时间 |

#### 20. feedbacks（反馈表）
| 字段名 | 类型 | 约束 | 说明 |
|--------|------|------|------|
| id | bigint | PK | 主键 |
| sender_id | bigint | NOT NULL, FK→users.id | 反馈人ID |
| feedback_type | varchar(50) | NOT NULL | 反馈类型（teaching/service/other） |
| feedback_content | text | NOT NULL | 反馈内容 |
| recipient_type | varchar(50) | | 反馈对象类型（teacher/counselor） |
| recipient_id | bigint | | 反馈对象ID |
| is_anonymous | tinyint(1) | DEFAULT 0 | 是否匿名 |
| status | varchar(20) | NOT NULL, DEFAULT 'pending' | 反馈状态（pending/processed/closed） |
| created_at | timestamp | DEFAULT CURRENT_TIMESTAMP | 创建时间 |
| updated_at | timestamp | DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP | 更新时间 |

### 通知与日志表（6张）

#### 18. notifications（通知消息表）
| 字段名 | 类型 | 约束 | 说明 |
|--------|------|------|------|
| id | bigint | PK | 主键 |
| recipient_id | bigint | NOT NULL, FK→users.id | 接收者ID |
| sender_id | bigint | FK→users.id | 发送者ID |
| notification_title | varchar(100) | NOT NULL | 通知标题 |
| notification_content | text | NOT NULL | 通知内容 |
| notification_type | varchar(50) | NOT NULL | 通知类型（warning/assistance/message） |
| is_read | tinyint(1) | NOT NULL, DEFAULT 0 | 是否已读 |
| read_time | timestamp | | 阅读时间 |
| created_at | timestamp | DEFAULT CURRENT_TIMESTAMP | 创建时间 |
| updated_at | timestamp | DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP | 更新时间 |

#### 19. subscription_preferences（订阅偏好表）
| 字段名 | 类型 | 约束 | 说明 |
|--------|------|------|------|
| id | bigint | PK | 主键 |
| user_id | bigint | NOT NULL, UNI, FK→users.id | 用户ID |
| subscribe_warning_low | tinyint(1) | DEFAULT 1 | 是否订阅低级预警 |
| subscribe_warning_medium | tinyint(1) | DEFAULT 1 | 是否订阅中级预警 |
| subscribe_warning_high | tinyint(1) | DEFAULT 1 | 是否订阅高级预警 |
| subscribe_assistance | tinyint(1) | DEFAULT 1 | 是否订阅帮扶计划 |
| notify_channel_email | tinyint(1) | DEFAULT 0 | 是否通过邮件通知 |
| notify_channel_sms | tinyint(1) | DEFAULT 0 | 是否通过短信通知 |
| notify_channel_system | tinyint(1) | DEFAULT 1 | 是否通过系统消息通知 |
| created_at | timestamp | DEFAULT CURRENT_TIMESTAMP | 创建时间 |
| updated_at | timestamp | DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP | 更新时间 |

#### 22. communication_logs（沟通日志表）
| 字段名 | 类型 | 约束 | 说明 |
|--------|------|------|------|
| id | bigint | PK | 主键 |
| sender_id | bigint | NOT NULL, FK→users.id | 发送者ID |
| recipient_id | bigint | NOT NULL, FK→users.id | 接收者ID |
| communication_type | varchar(50) | NOT NULL | 沟通类型（warning/assistance/advice） |
| content | text | NOT NULL | 沟通内容 |
| created_at | timestamp | DEFAULT CURRENT_TIMESTAMP | 创建时间 |
| updated_at | timestamp | DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP | 更新时间 |

#### 23. audit_logs（审计日志表）
| 字段名 | 类型 | 约束 | 说明 |
|--------|------|------|------|
| id | bigint | PK | 主键 |
| user_id | bigint | NOT NULL, FK→users.id | 操作用户ID |
| action_type | varchar(50) | NOT NULL | 操作类型（create/update/delete） |
| module_name | varchar(50) | NOT NULL | 模块名称 |
| record_id | bigint | | 操作记录ID |
| old_values | text | | 旧值（JSON格式） |
| new_values | text | | 新值（JSON格式） |
| created_at | timestamp | DEFAULT CURRENT_TIMESTAMP | 创建时间 |

#### 24. security_logs（安全日志表）
| 字段名 | 类型 | 约束 | 说明 |
|--------|------|------|------|
| id | bigint | PK | 主键 |
| user_id | bigint | FK→users.id | 用户ID |
| event_type | varchar(50) | NOT NULL | 事件类型（login/logout/access_denied） |
| is_successful | tinyint(1) | NOT NULL | 操作是否成功（0=失败、1=成功） |
| ip_address | varchar(50) | | 登录IP地址 |
| event_time | timestamp | DEFAULT CURRENT_TIMESTAMP | 事件时间 |
| created_at | timestamp | DEFAULT CURRENT_TIMESTAMP | 创建时间 |

### 其他配置表（2张）

#### 21. elective_modules（选修模块表）
| 字段名 | 类型 | 约束 | 说明 |
|--------|------|------|------|
| id | bigint | PK | 主键 |
| module_code | varchar(20) | UNI, NOT NULL | 模块代码 |
| module_name | varchar(100) | NOT NULL | 模块名称 |
| description | text | | 模块描述 |
| required_credits | decimal(5,2) | NOT NULL | 模块要求学分 |
| created_at | timestamp | DEFAULT CURRENT_TIMESTAMP | 创建时间 |
| updated_at | timestamp | DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP | 更新时间 |

#### 25. student_settings（学生设置表）
| 字段名 | 类型 | 约束 | 说明 |
|--------|------|------|------|
| id | bigint | PK | 主键 |
| user_id | bigint | UNI, NOT NULL, FK→users.id | 用户ID |
| theme | varchar(20) | DEFAULT 'light' | 主题设置（light/dark） |
| language | varchar(10) | DEFAULT 'zh_CN' | 语言设置 |
| data_visibility | varchar(20) | DEFAULT 'private' | 数据可见性 |
| notification_enabled | tinyint(1) | DEFAULT 1 | 是否启用通知 |
| created_at | timestamp | DEFAULT CURRENT_TIMESTAMP | 创建时间 |
| updated_at | timestamp | DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP | 更新时间 |

---

## 索引总览

**关键性能索引**:
```sql
-- 用户快速查询
CREATE UNIQUE INDEX idx_username ON users(username);
CREATE UNIQUE INDEX idx_student_id ON student_profile(student_id);

-- 成绩快速查询
CREATE INDEX idx_student_semester ON scores(student_id, semester);
CREATE INDEX idx_course_id ON scores(course_id);

-- 预警快速查询
CREATE INDEX idx_student_status ON academic_warnings(student_id, status);
CREATE INDEX idx_warning_level ON academic_warnings(warning_level);

-- 对标分析快速查询
CREATE UNIQUE INDEX idx_benchmark_student_semester ON benchmark_analysis(student_id, semester);
```

---

## 文档更新记录

| 日期 | 版本 | 更新内容 |
|------|------|----------|
| 2025-12-20 | 2.0 | **重大更新**：补充完整表结构、字段约束、索引设计、精确GPA换算规则、JSON示例、软删除字段、时间默认值等 |
| 2025-12-20 | 1.0 | 初版文档，包含核心表结构和关系 |

---

**文档作者**: AI 助手  
**最后更新**: 2025-12-20  
**数据库名称**: academic_warning_system  
