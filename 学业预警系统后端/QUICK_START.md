# 学业预警系统 - 后端快速启动指南

## 项目概述

学业预警系统是一个全栈Web应用，包含学生、教师、辅导员和管理员四个主要角色的业务功能。该系统帮助高校实现学业预警的自动化处理和闭环管理。

## 技术栈

- **框架**: Spring Boot 3.2.0
- **ORM**: MyBatis-Plus 3.5.4
- **数据库**: MySQL 8.0
- **缓存**: Redis
- **认证**: Spring Security + JWT
- **API**: RESTful
- **构建**: Maven

## 快速启动

### 1. 前置条件

- Java 17+
- Maven 3.6+
- MySQL 8.0+
- Redis (可选，本地开发可跳过)

### 2. 配置数据库

#### 2.1 创建数据库

```bash
mysql -u root -p < src/main/resources/init_database.sql
```

#### 2.2 更新配置文件

编辑 `src/main/resources/application.yml`：

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/academic_warning_system
    username: root  # 修改为你的MySQL用户名
    password: your_password  # 修改为你的MySQL密码
```

### 3. 启动应用

```bash
mvn clean install
mvn spring-boot:run
```

应用将在 `http://localhost:8080` 启动

## 项目结构

```
src/main/java/com/academic/
├── common/              # 通用工具类
│   └── ApiResponse.java # API响应包装类
├── config/              # 配置类
├── controller/          # REST API 控制器
│   ├── AuthController.java         # 认证
│   ├── StudentController.java       # 学生API
│   ├── TeacherController.java       # 教师API
│   ├── CounselorController.java     # 辅导员API
│   └── AdminController.java         # 管理员API
├── dto/                 # 数据传输对象
├── entity/              # 实体类
├── mapper/              # MyBatis-Plus Mapper
├── service/             # 业务逻辑服务
│   ├── UserService.java
│   ├── StudentService.java
│   ├── TeacherService.java
│   ├── CounselorService.java
│   ├── CollegeService.java
│   ├── MajorService.java
│   ├── ScoreService.java
│   ├── WarningService.java
│   └── AssistancePlanService.java
└── AcademicWarningApplication.java  # Spring Boot 主类
```

## API 端点概览

### 认证模块
- `POST /auth/login` - 用户登录

### 学生模块
- `POST /students/register` - 学生注册
- `GET /students/dashboard/{userId}` - 学业看板
- `GET /students/scores/{userId}` - 成绩列表
- `GET /students/warnings/{userId}` - 预警列表
- `GET /students/assistance/{userId}` - 帮扶计划列表
- `POST /students/assistance/{planId}/progress` - 更新进度

### 教师模块
- `GET /teachers/dashboard/{userId}` - 教师仪表板
- `GET /teachers/students/{teacherId}` - 学生列表
- `GET /teachers/warnings/{teacherId}` - 预警列表
- `POST /teachers/warnings/{warningId}/process` - 处理预警

### 辅导员模块
- `GET /counselors/dashboard/{userId}` - 辅导员仪表板
- `GET /counselors/students/{counselorId}` - 学生列表
- `GET /counselors/warnings/{counselorId}` - 预警列表
- `POST /counselors/warnings/batch-process` - 批量处理预警

### 管理员模块
- `GET /admin/dashboard` - 管理员仪表板
- `GET /admin/colleges` - 学院列表
- `POST /admin/colleges` - 创建学院
- `PUT /admin/colleges/{collegeId}` - 更新学院
- `DELETE /admin/colleges/{collegeId}` - 删除学院
- `GET /admin/colleges/{collegeId}/majors` - 专业列表
- `GET /admin/users` - 用户列表
- `POST /admin/users/{userId}/disable` - 禁用用户
- `POST /admin/users/{userId}/enable` - 启用用户

## 关键功能

### 1. 学生端
- 查看学业看板：课程数、GPA、预警统计
- 查看成绩信息：按学期筛选，显示单科成绩和计算GPA
- 查看学业预警：等级分类，查看预警详情
- 帮扶计划追踪：查看进度和计划内容

### 2. 教师端
- 查看班级学生
- 管理学业预警：查看和处理预警
- 学生表现分析

### 3. 辅导员端
- 班级概览：统计学生数和预警数
- 学生管理：查看班级所有学生及预警状态
- 批量处理预警

### 4. 管理员端
- 学院管理：CRUD操作
- 专业管理：按学院查看和管理
- 用户管理：启用/禁用用户
- 数据统计：整体数据面板

## 数据库设计

### 核心表

| 表名 | 说明 |
|-----|------|
| users | 用户基本信息 |
| student_profile | 学生档案 |
| teacher_profile | 教师档案 |
| counselor_profile | 辅导员档案 |
| admin_profile | 管理员档案 |
| colleges | 学院信息 |
| majors | 专业信息 |
| classes | 班级信息 |
| scores | 成绩记录 |
| courses | 课程信息 |
| academic_warnings | 学业预警 |
| assistance_plans | 帮扶计划 |

## 认证方式

系统使用JWT (JSON Web Token) 进行身份认证：

1. 用户登录时获取token
2. 在请求Header中添加: `Authorization: Bearer {token}`
3. Token有效期: 1小时

## 角色权限

| 角色 | 权限 |
|-----|------|
| 学生 | 查看自己的成绩、预警、帮扶计划 |
| 教师 | 查看班级学生、预警、处理预警 |
| 辅导员 | 查看学院学生、批量处理预警 |
| 管理员 | 管理学院、专业、用户 |

## 常见问题

### Q: 如何修改数据库连接信息？
A: 编辑 `application.yml` 中的 datasource 配置

### Q: 如何添加新的API端点？
A: 
1. 在 controller 中创建新的方法
2. 使用 @GetMapping/@PostMapping/@PutMapping/@DeleteMapping 注解
3. 方法返回 ApiResponse 类型

### Q: 如何添加新的业务逻辑？
A:
1. 创建 Service 接口
2. 创建 ServiceImpl 实现类
3. 在 Controller 中注入使用

## 部署

### 构建可执行JAR

```bash
mvn clean package -DskipTests
```

### 运行JAR

```bash
java -jar target/academic-warning-system-1.0.0.jar
```

## 支持

如有问题或建议，请联系技术团队。

---

最后更新: 2025-12-11
