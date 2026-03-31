# 学业预警系统文档

## 1. 系统架构

### 1.1 整体架构
- **前端**：Vue 3 + Element Plus
- **后端**：Spring Boot 3.2.0
- **微服务**：Spring Cloud
- **服务注册**：Eureka Server
- **数据库**：MySQL
- **缓存**：Redis
- **认证**：JWT

### 1.2 微服务架构
- **eureka-server**：服务注册中心
- **student-service**：学生服务，提供学生相关功能
- **user-service**：用户服务，提供认证和用户管理功能

## 2. 功能模块

### 2.1 学生模块
- 学业看板：展示学生的GPA、挂科情况、预警信息等
- 成绩管理：查看课程成绩、学分情况
- 预警管理：查看学业预警信息
- 帮扶计划：查看和更新帮扶计划进度
- 申诉管理：提交和管理成绩申诉
- 对标分析：与班级、专业的成绩对比
- 通知中心：接收系统通知

### 2.2 教师模块
- 仪表盘：查看教学统计数据
- 课程管理：管理所授课程
- 成绩管理：录入和管理学生成绩
- 预警管理：查看和处理学生预警
- 反馈管理：处理学生反馈
- 班级管理：申请和管理班级

### 2.3 辅导员模块
- 仪表盘：查看所管学生的统计数据
- 学生管理：管理所管学生信息
- 预警管理：处理学生预警
- 帮扶计划：制定和管理帮扶计划
- 成绩跟踪：跟踪学生成绩情况
- 班级管理：管理班级信息
- 数据分析：分析学生和班级数据

### 2.4 管理员模块
- 仪表盘：查看系统整体统计数据
- 用户管理：管理系统用户
- 学院管理：管理学院信息
- 专业管理：管理专业信息
- 预警规则：配置预警规则
- 权限管理：管理用户权限
- 数据导出：导出系统数据

## 3. API接口

### 3.1 认证接口
- `POST /api/auth/login`：用户登录
- `POST /api/auth/register/student`：学生注册
- `POST /api/auth/register/teacher`：教师注册
- `POST /api/auth/register/counselor`：辅导员注册
- `POST /api/auth/register/admin`：管理员注册

### 3.2 学生接口
- `GET /api/students/dashboard/{userId}`：获取学生仪表盘数据
- `GET /api/students/scores/{userId}`：获取学生成绩
- `GET /api/students/warnings/{userId}`：获取学生预警
- `GET /api/students/assistance/{userId}`：获取帮扶计划
- `POST /api/students/appeals/submit`：提交成绩申诉
- `GET /api/students/benchmark/{studentId}/latest`：获取对标分析数据

### 3.3 教师接口
- `GET /api/teachers/dashboard/{userId}`：获取教师仪表盘
- `GET /api/teachers/courses`：获取教师课程
- `POST /api/teachers/scores`：保存学生成绩
- `GET /api/teachers/feedbacks`：获取学生反馈
- `GET /api/teachers/warnings/{teacherId}`：获取学生预警

### 3.4 辅导员接口
- `GET /api/counselors/dashboard/{userId}`：获取辅导员仪表盘
- `GET /api/counselors/students`：获取学生列表
- `POST /api/counselors/warnings/{warningId}/process`：处理预警
- `POST /api/counselors/assistance/create`：创建帮扶计划
- `GET /api/counselors/analytics/credit-insufficient`：获取学分不足率

### 3.5 管理员接口
- `GET /api/admin/dashboard`：获取管理员仪表盘
- `POST /api/admin/colleges`：创建学院
- `POST /api/admin/majors`：创建专业
- `GET /api/admin/users`：获取用户列表
- `POST /api/admin/rules`：创建预警规则

## 4. 数据库设计

### 4.1 核心表结构
- `user`：用户表
- `student_profile`：学生档案表
- `teacher_profile`：教师档案表
- `counselor_profile`：辅导员档案表
- `admin_profile`：管理员档案表
- `score`：成绩表
- `academic_warning`：学业预警表
- `assistance_plan`：帮扶计划表
- `score_appeal`：成绩申诉表
- `college`：学院表
- `major`：专业表
- `course`：课程表

## 5. 部署指南

### 5.1 环境要求
- JDK 17+
- MySQL 8.0+
- Redis 6.0+
- Maven 3.8+
- Node.js 16+

### 5.2 部署步骤
1. **数据库初始化**：
   - 创建数据库 `academic_warning_system`
   - 执行 `schema.sql` 创建表结构
   - 执行 `data.sql` 初始化基础数据

2. **微服务部署**：
   - 启动 eureka-server：`mvn spring-boot:run`
   - 启动 user-service：`mvn spring-boot:run`
   - 启动 student-service：`mvn spring-boot:run`

3. **主后端部署**：
   - 构建：`mvn clean package`
   - 运行：`java -jar target/academic-warning-system-1.0.0.jar`

4. **前端部署**：
   - 安装依赖：`npm install`
   - 构建：`npm run build`
   - 部署到 web 服务器

### 5.3 配置文件
- **微服务配置**：`application.yml` 中的数据库连接、Redis 连接、Eureka 服务地址
- **主后端配置**：`application.yml` 中的数据库连接、Redis 连接
- **前端配置**：`src/api/client.js` 中的 API 基础 URL

## 6. 系统维护

### 6.1 日志管理
- 系统日志：`logs` 目录
- 数据库日志：MySQL 日志
- 微服务日志：各服务的控制台输出

### 6.2 性能优化
- 数据库索引优化
- 缓存策略调整
- 连接池配置优化
- 代码优化

### 6.3 安全维护
- 定期更新密码
- 监控系统访问日志
- 定期备份数据库
- 及时更新依赖包

## 7. 技术栈

### 7.1 后端技术
- Spring Boot 3.2.0
- Spring Cloud
- MyBatis-Plus
- MySQL
- Redis
- JWT

### 7.2 前端技术
- Vue 3
- Element Plus
- Axios
- Vue Router
- Pinia

## 8. 故障排查

### 8.1 常见问题
- 服务注册失败：检查 Eureka 服务是否启动
- 数据库连接失败：检查数据库服务是否启动，连接配置是否正确
- Redis 连接失败：检查 Redis 服务是否启动
- 前端 API 调用失败：检查后端服务是否启动，API 地址是否正确

### 8.2 排查步骤
1. 检查服务状态
2. 查看日志信息
3. 检查网络连接
4. 验证配置文件
5. 测试 API 接口

## 9. 版本历史

- **v1.0.0**：系统初始化，实现核心功能
- **v1.1.0**：添加微服务架构
- **v1.2.0**：优化性能，添加缓存机制
- **v1.3.0**：完善前端界面，添加更多功能模块

## 10. 联系方式

- 系统管理员：admin@academic.edu
- 技术支持：tech@academic.edu
- 服务热线：400-123-4567