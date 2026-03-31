# 学术预警系统 API 调用文档

## 1. 认证相关 API

### 1.1 用户登录
- **API路径**: `/auth/login`
- **请求方法**: `POST`
- **请求参数**:
  | 参数名 | 数据类型 | 是否必填 | 说明 |
  | --- | --- | --- | --- |
  | username | String | 是 | 用户名/学号/工号 |
  | password | String | 是 | 密码 |
- **响应格式**:
  - 成功响应:
    ```json
    {
      "code": 200,
      "message": "成功",
      "data": {
        "token": "token-1234567890",
        "userId": 1,
        "username": "2023020616",
        "name": "张三",
        "role": 1,
        "email": "zhangsan@example.com",
        "phone": "13800138000"
      }
    }
    ```
  - 错误响应:
    ```json
    {
      "code": 400,
      "message": "用户名或密码错误",
      "data": null
    }
    ```
- **认证方式**: 无（登录接口本身不需要认证）
- **调用限制**: 无
- **使用示例**:
  ```bash
  curl -X POST http://localhost:8080/auth/login \
    -H "Content-Type: application/json" \
    -d '{"username": "2023020616", "password": "123456"}'
  ```

### 1.2 学生注册
- **API路径**: `/auth/register/student`
- **请求方法**: `POST`
- **请求参数**:
  | 参数名 | 数据类型 | 是否必填 | 说明 |
  | --- | --- | --- | --- |
  | studentId | String | 是 | 学号（10位数字或10位数字+Z） |
  | password | String | 是 | 密码 |
  | name | String | 是 | 姓名 |
  | phone | String | 是 | 手机号 |
  | email | String | 是 | 邮箱 |
- **响应格式**:
  - 成功响应:
    ```json
    {
      "code": 200,
      "message": "成功",
      "data": {
        "success": true,
        "message": "注册成功",
        "studentId": "2023020616"
      }
    }
    ```
  - 错误响应:
    ```json
    {
      "code": 400,
      "message": "学号格式错误：本科学生为10位数字，专升本学生为10位数字加Z",
      "data": null
    }
    ```
- **认证方式**: 无
- **调用限制**: 无
- **使用示例**:
  ```bash
  curl -X POST http://localhost:8080/auth/register/student \
    -H "Content-Type: application/json" \
    -d '{"studentId": "2023020616", "password": "123456", "name": "张三", "phone": "13800138000", "email": "zhangsan@example.com"}'
  ```

### 1.3 教师注册
- **API路径**: `/auth/register/teacher`
- **请求方法**: `POST`
- **请求参数**:
  | 参数名 | 数据类型 | 是否必填 | 说明 |
  | --- | --- | --- | --- |
  | username | String | 是 | 工号（5位） |
  | password | String | 是 | 密码 |
  | name | String | 是 | 姓名 |
  | collegeId | Long | 是 | 学院ID |
  | phone | String | 是 | 手机号 |
  | email | String | 是 | 邮箱 |
- **响应格式**:
  - 成功响应:
    ```json
    {
      "code": 200,
      "message": "成功",
      "data": {
        "userId": 1,
        "username": "12345",
        "collegeId": 1
      }
    }
    ```
  - 错误响应:
    ```json
    {
      "code": 400,
      "message": "工号必须是5位",
      "data": null
    }
    ```
- **认证方式**: 无
- **调用限制**: 无
- **使用示例**:
  ```bash
  curl -X POST http://localhost:8080/auth/register/teacher \
    -H "Content-Type: application/json" \
    -d '{"username": "12345", "password": "123456", "name": "李老师", "collegeId": 1, "phone": "13900139000", "email": "teacher@example.com"}'
  ```

### 1.4 辅导员注册
- **API路径**: `/auth/register/counselor`
- **请求方法**: `POST`
- **请求参数**:
  | 参数名 | 数据类型 | 是否必填 | 说明 |
  | --- | --- | --- | --- |
  | username | String | 是 | 工号 |
  | password | String | 是 | 密码 |
  | name | String | 是 | 姓名 |
  | majorCode | String | 是 | 专业编码 |
  | phone | String | 是 | 手机号 |
  | email | String | 是 | 邮箱 |
- **响应格式**:
  - 成功响应:
    ```json
    {
      "code": 200,
      "message": "成功",
      "data": {
        "userId": 1,
        "username": "54321",
        "majorCode": "02",
        "collegeId": 1
      }
    }
    ```
  - 错误响应:
    ```json
    {
      "code": 400,
      "message": "无效的专业编码：00，请联系管理员",
      "data": null
    }
    ```
- **认证方式**: 无
- **调用限制**: 无
- **使用示例**:
  ```bash
  curl -X POST http://localhost:8080/auth/register/counselor \
    -H "Content-Type: application/json" \
    -d '{"username": "54321", "password": "123456", "name": "王辅导员", "majorCode": "02", "phone": "13700137000", "email": "counselor@example.com"}'
  ```

### 1.5 管理员注册
- **API路径**: `/auth/register/admin`
- **请求方法**: `POST`
- **请求参数**:
  | 参数名 | 数据类型 | 是否必填 | 说明 |
  | --- | --- | --- | --- |
  | username | String | 是 | 用户名 |
  | password | String | 是 | 密码 |
  | name | String | 是 | 姓名 |
  | department | String | 是 | 部门 |
  | phone | String | 是 | 手机号 |
  | email | String | 是 | 邮箱 |
- **响应格式**:
  - 成功响应:
    ```json
    {
      "code": 200,
      "message": "成功",
      "data": {
        "userId": 0,
        "username": "admin"
      }
    }
    ```
  - 错误响应:
    ```json
    {
      "code": 400,
      "message": "注册失败",
      "data": null
    }
    ```
- **认证方式**: 无
- **调用限制**: 无
- **使用示例**:
  ```bash
  curl -X POST http://localhost:8080/auth/register/admin \
    -H "Content-Type: application/json" \
    -d '{"username": "admin", "password": "123456", "name": "系统管理员", "department": "信息中心", "phone": "13600136000", "email": "admin@example.com"}'
  ```

### 1.6 用户登出
- **API路径**: `/auth/logout`
- **请求方法**: `POST`
- **请求参数**: 无
- **响应格式**:
  - 成功响应:
    ```json
    {
      "code": 200,
      "message": "登出成功",
      "data": null
    }
    ```
  - 错误响应:
    ```json
    {
      "code": 500,
      "message": "登出失败",
      "data": null
    }
    ```
- **认证方式**: JWT token（通过请求头Authorization传递）
- **调用限制**: 无
- **使用示例**:
  ```bash
  curl -X POST http://localhost:8080/auth/logout \
    -H "Authorization: Bearer token-1234567890"
  ```

## 2. 学生相关 API

### 2.1 学生注册
- **API路径**: `/students/register`
- **请求方法**: `POST`
- **请求参数**:
  | 参数名 | 数据类型 | 是否必填 | 说明 |
  | --- | --- | --- | --- |
  | studentId | String | 是 | 学号 |
  | password | String | 是 | 密码 |
  | name | String | 是 | 姓名 |
  | phone | String | 是 | 手机号 |
  | email | String | 是 | 邮箱 |
- **响应格式**:
  - 成功响应:
    ```json
    {
      "code": 200,
      "message": "注册成功",
      "data": null
    }
    ```
  - 错误响应:
    ```json
    {
      "code": 400,
      "message": "注册失败",
      "data": null
    }
    ```
- **认证方式**: 无
- **调用限制**: 无
- **使用示例**:
  ```bash
  curl -X POST http://localhost:8080/students/register \
    -H "Content-Type: application/json" \
    -d '{"studentId": "2023020616", "password": "123456", "name": "张三", "phone": "13800138000", "email": "zhangsan@example.com"}'
  ```

### 2.2 获取学生信息
- **API路径**: `/students/{studentId}`
- **请求方法**: `GET`
- **请求参数**:
  | 参数名 | 数据类型 | 是否必填 | 说明 |
  | --- | --- | --- | --- |
  | studentId | String | 是 | 学号 |
- **响应格式**:
  - 成功响应:
    ```json
    {
      "code": 200,
      "message": "成功",
      "data": {
        "id": 1,
        "studentId": "2023020616",
        "name": "张三",
        "gender": "男",
        "phone": "13800138000",
        "email": "zhangsan@example.com",
        "collegeId": 1,
        "majorId": 1,
        "classId": 1
      }
    }
    ```
  - 错误响应:
    ```json
    {
      "code": 404,
      "message": "学生不存在",
      "data": null
    }
    ```
- **认证方式**: JWT token
- **调用限制**: 无
- **使用示例**:
  ```bash
  curl -X GET http://localhost:8080/students/2023020616 \
    -H "Authorization: Bearer token-1234567890"
  ```

### 2.3 获取学生GPA
- **API路径**: `/students/{studentId}/gpa`
- **请求方法**: `GET`
- **请求参数**:
  | 参数名 | 数据类型 | 是否必填 | 说明 |
  | --- | --- | --- | --- |
  | studentId | String | 是 | 学号 |
- **响应格式**:
  - 成功响应:
    ```json
    {
      "code": 200,
      "message": "成功",
      "data": 3.5
    }
    ```
  - 错误响应:
    ```json
    {
      "code": 404,
      "message": "学生不存在",
      "data": null
    }
    ```
- **认证方式**: JWT token
- **调用限制**: 无
- **使用示例**:
  ```bash
  curl -X GET http://localhost:8080/students/2023020616/gpa \
    -H "Authorization: Bearer token-1234567890"
  ```

### 2.4 获取学业看板数据
- **API路径**: `/students/dashboard/{userId}`
- **请求方法**: `GET`
- **请求参数**:
  | 参数名 | 数据类型 | 是否必填 | 说明 |
  | --- | --- | --- | --- |
  | userId | Long | 是 | 用户ID |
- **响应格式**:
  - 成功响应:
    ```json
    {
      "code": 200,
      "message": "成功",
      "data": {
        "studentId": 1,
        "studentName": "张三",
        "className": "计算机科学与技术2023级1班",
        "majorName": "计算机科学与技术",
        "gpa": 3.5,
        "warningCount": 0,
        "warningLevel": "正常",
        "failedCoursesCount": 0,
        "totalCoursesCount": 10,
        "completedCredits": 60,
        "requiredCredits": 120
      }
    }
    ```
  - 错误响应:
    ```json
    {
      "code": 404,
      "message": "学生不存在",
      "data": null
    }
    ```
- **认证方式**: JWT token
- **调用限制**: 无
- **使用示例**:
  ```bash
  curl -X GET http://localhost:8080/students/dashboard/1 \
    -H "Authorization: Bearer token-1234567890"
  ```

### 2.5 获取学生成绩列表
- **API路径**: `/students/scores/{userId}`
- **请求方法**: `GET`
- **请求参数**:
  | 参数名 | 数据类型 | 是否必填 | 说明 |
  | --- | --- | --- | --- |
  | userId | Long | 是 | 用户ID |
  | semester | String | 否 | 学期 |
- **响应格式**:
  - 成功响应:
    ```json
    {
      "code": 200,
      "message": "成功",
      "data": [
        {
          "id": 1,
          "scoreTotal": 90,
          "gradePoint": 4.0,
          "semester": "2023-2024-1",
          "createdAt": "2023-12-01T00:00:00",
          "courseName": "高等数学",
          "credits": 4.0
        }
      ]
    }
    ```
  - 错误响应:
    ```json
    {
      "code": 500,
      "message": "获取成绩失败",
      "data": []
    }
    ```
- **认证方式**: JWT token
- **调用限制**: 无
- **使用示例**:
  ```bash
  curl -X GET "http://localhost:8080/students/scores/1?semester=2023-2024-1" \
    -H "Authorization: Bearer token-1234567890"
  ```

### 2.6 获取学生预警列表
- **API路径**: `/students/warnings/{userId}`
- **请求方法**: `GET`
- **请求参数**:
  | 参数名 | 数据类型 | 是否必填 | 说明 |
  | --- | --- | --- | --- |
  | userId | Long | 是 | 用户ID |
- **响应格式**:
  - 成功响应:
    ```json
    {
      "code": 200,
      "message": "成功",
      "data": [
        {
          "id": 1,
          "studentId": 1,
          "warningLevel": "high",
          "warningReason": "挂科超过3门",
          "status": "pending",
          "createdAt": "2023-12-01T00:00:00"
        }
      ]
    }
    ```
  - 错误响应:
    ```json
    {
      "code": 500,
      "message": "获取预警失败",
      "data": []
    }
    ```
- **认证方式**: JWT token
- **调用限制**: 无
- **使用示例**:
  ```bash
  curl -X GET http://localhost:8080/students/warnings/1 \
    -H "Authorization: Bearer token-1234567890"
  ```

### 2.7 获取学生帮扶计划
- **API路径**: `/students/assistance/{userId}`
- **请求方法**: `GET`
- **请求参数**:
  | 参数名 | 数据类型 | 是否必填 | 说明 |
  | --- | --- | --- | --- |
  | userId | Long | 是 | 用户ID |
- **响应格式**:
  - 成功响应:
    ```json
    {
      "code": 200,
      "message": "成功",
      "data": [
        {
          "id": 1,
          "studentId": 1,
          "planName": "数学辅导计划",
          "startDate": "2023-12-01",
          "endDate": "2024-01-31",
          "progress": 50,
          "status": "in_progress"
        }
      ]
    }
    ```
  - 错误响应:
    ```json
    {
      "code": 500,
      "message": "获取帮扶计划失败",
      "data": null
    }
    ```
- **认证方式**: JWT token
- **调用限制**: 无
- **使用示例**:
  ```bash
  curl -X GET http://localhost:8080/students/assistance/1 \
    -H "Authorization: Bearer token-1234567890"
  ```

## 3. 教师相关 API

### 3.1 教师注册
- **API路径**: `/teachers/register`
- **请求方法**: `POST`
- **请求参数**:
  | 参数名 | 数据类型 | 是否必填 | 说明 |
  | --- | --- | --- | --- |
  | username | String | 是 | 用户名 |
  | password | String | 是 | 密码 |
  | college_id | Long | 是 | 学院ID |
- **响应格式**:
  - 成功响应:
    ```json
    {
      "code": 200,
      "message": "成功",
      "data": {
        "userId": 1,
        "username": "12345",
        "college_id": 1
      }
    }
    ```
  - 错误响应:
    ```json
    {
      "code": 400,
      "message": "注册失败",
      "data": null
    }
    ```
- **认证方式**: 无
- **调用限制**: 无
- **使用示例**:
  ```bash
  curl -X POST http://localhost:8080/teachers/register \
    -H "Content-Type: application/json" \
    -d '{"username": "12345", "password": "123456", "college_id": 1}'
  ```

### 3.2 获取教师仪表板数据
- **API路径**: `/teachers/dashboard/{userId}`
- **请求方法**: `GET`
- **请求参数**:
  | 参数名 | 数据类型 | 是否必填 | 说明 |
  | --- | --- | --- | --- |
  | userId | Long | 是 | 用户ID |
- **响应格式**:
  - 成功响应:
    ```json
    {
      "code": 200,
      "message": "成功",
      "data": {
        "studentCount": 50,
        "warningCount": 5,
        "redWarnings": 2,
        "yellowWarnings": 3
      }
    }
    ```
  - 错误响应:
    ```json
    {
      "code": 404,
      "message": "教师不存在",
      "data": null
    }
    ```
- **认证方式**: JWT token
- **调用限制**: 无
- **使用示例**:
  ```bash
  curl -X GET http://localhost:8080/teachers/dashboard/1 \
    -H "Authorization: Bearer token-1234567890"
  ```

### 3.3 查询教师课程列表
- **API路径**: `/teachers/courses`
- **请求方法**: `GET`
- **请求参数**:
  | 参数名 | 数据类型 | 是否必填 | 说明 |
  | --- | --- | --- | --- |
  | teacher_id | Long | 是 | 教师ID |
- **响应格式**:
  - 成功响应:
    ```json
    {
      "code": 200,
      "message": "成功",
      "data": [
        {
          "id": 1,
          "name": "高等数学",
          "credits": 4.0,
          "type": "必修课"
        }
      ]
    }
    ```
  - 错误响应:
    ```json
    {
      "code": 500,
      "message": "查询课程列表失败",
      "data": null
    }
    ```
- **认证方式**: JWT token
- **调用限制**: 无
- **使用示例**:
  ```bash
  curl -X GET "http://localhost:8080/teachers/courses?teacher_id=1" \
    -H "Authorization: Bearer token-1234567890"
  ```

## 4. 辅导员相关 API

### 4.1 辅导员注册
- **API路径**: `/counselors/register`
- **请求方法**: `POST`
- **请求参数**:
  | 参数名 | 数据类型 | 是否必填 | 说明 |
  | --- | --- | --- | --- |
  | username | String | 是 | 用户名 |
  | password | String | 是 | 密码 |
  | name | String | 是 | 姓名 |
  | college_id | Long | 是 | 学院ID |
- **响应格式**:
  - 成功响应:
    ```json
    {
      "code": 200,
      "message": "成功",
      "data": {
        "userId": 1,
        "username": "54321",
        "college_id": 1
      }
    }
    ```
  - 错误响应:
    ```json
    {
      "code": 400,
      "message": "注册失败",
      "data": null
    }
    ```
- **认证方式**: 无
- **调用限制**: 无
- **使用示例**:
  ```bash
  curl -X POST http://localhost:8080/counselors/register \
    -H "Content-Type: application/json" \
    -d '{"username": "54321", "password": "123456", "name": "王辅导员", "college_id": 1}'
  ```

### 4.2 获取辅导员仪表板数据
- **API路径**: `/counselors/dashboard/{userId}`
- **请求方法**: `GET`
- **请求参数**:
  | 参数名 | 数据类型 | 是否必填 | 说明 |
  | --- | --- | --- | --- |
  | userId | Long | 是 | 用户ID |
- **响应格式**:
  - 成功响应:
    ```json
    {
      "code": 200,
      "message": "成功",
      "data": {
        "studentCount": 100,
        "warningCount": 10,
        "redWarnings": 3,
        "yellowWarnings": 7
      }
    }
    ```
  - 错误响应:
    ```json
    {
      "code": 404,
      "message": "辅导员不存在",
      "data": null
    }
    ```
- **认证方式**: JWT token
- **调用限制**: 无
- **使用示例**:
  ```bash
  curl -X GET http://localhost:8080/counselors/dashboard/1 \
    -H "Authorization: Bearer token-1234567890"
  ```

## 5. 管理员相关 API

### 5.1 获取管理员仪表板
- **API路径**: `/admin/dashboard`
- **请求方法**: `GET`
- **请求参数**: 无
- **响应格式**:
  - 成功响应:
    ```json
    {
      "code": 200,
      "message": "成功",
      "data": {
        "totalColleges": 5,
        "totalMajors": 20,
        "totalUsers": 1000,
        "totalStudents": 800,
        "totalTeachers": 150,
        "totalWarnings": 50
      }
    }
    ```
  - 错误响应:
    ```json
    {
      "code": 500,
      "message": "获取管理员仪表板失败",
      "data": null
    }
    ```
- **认证方式**: JWT token
- **调用限制**: 无
- **使用示例**:
  ```bash
  curl -X GET http://localhost:8080/admin/dashboard \
    -H "Authorization: Bearer token-1234567890"
  ```

### 5.2 获取所有学院
- **API路径**: `/admin/colleges`
- **请求方法**: `GET`
- **请求参数**: 无
- **响应格式**:
  - 成功响应:
    ```json
    {
      "code": 200,
      "message": "成功",
      "data": [
        {
          "id": 1,
          "name": "计算机学院",
          "code": "CS",
          "description": "计算机科学与技术学院"
        }
      ]
    }
    ```
  - 错误响应:
    ```json
    {
      "code": 500,
      "message": "获取学院列表失败",
      "data": null
    }
    ```
- **认证方式**: JWT token
- **调用限制**: 无
- **使用示例**:
  ```bash
  curl -X GET http://localhost:8080/admin/colleges \
    -H "Authorization: Bearer token-1234567890"
  ```

## 6. 微服务相关 API

### 6.1 Student Service API

#### 6.1.1 获取学生仪表板数据
- **API路径**: `/students/dashboard/{studentId}`
- **请求方法**: `GET`
- **请求参数**:
  | 参数名 | 数据类型 | 是否必填 | 说明 |
  | --- | --- | --- | --- |
  | studentId | Long | 是 | 学生ID |
- **响应格式**:
  - 成功响应:
    ```json
    {
      "code": 200,
      "message": "成功",
      "data": {
        "studentId": 1,
        "studentName": "张三",
        "className": "计算机科学与技术2023级1班",
        "majorName": "计算机科学与技术",
        "gpa": 3.5,
        "warningCount": 0,
        "warningLevel": "正常",
        "failedCoursesCount": 0,
        "totalCoursesCount": 10,
        "completedCredits": 60,
        "requiredCredits": 120
      }
    }
    ```
  - 错误响应:
    ```json
    {
      "code": 500,
      "message": "获取学生仪表板失败",
      "data": null
    }
    ```
- **认证方式**: 无（微服务内部调用）
- **调用限制**: 无
- **使用示例**:
  ```bash
  curl -X GET http://localhost:8083/students/dashboard/1
  ```

#### 6.1.2 获取学生成绩
- **API路径**: `/students/scores/{studentId}`
- **请求方法**: `GET`
- **请求参数**:
  | 参数名 | 数据类型 | 是否必填 | 说明 |
  | --- | --- | --- | --- |
  | studentId | Long | 是 | 学生ID |
- **响应格式**:
  - 成功响应:
    ```json
    {
      "code": 200,
      "message": "成功",
      "data": [
        {
          "id": 1,
          "studentId": 1,
          "courseId": 1,
          "scoreTotal": 90,
          "gradePoint": 4.0,
          "semester": "2023-2024-1",
          "createdAt": "2023-12-01T00:00:00"
        }
      ]
    }
    ```
  - 错误响应:
    ```json
    {
      "code": 500,
      "message": "获取学生成绩失败",
      "data": null
    }
    ```
- **认证方式**: 无（微服务内部调用）
- **调用限制**: 无
- **使用示例**:
  ```bash
  curl -X GET http://localhost:8083/students/scores/1
  ```

### 6.2 User Service API

#### 6.2.1 用户登录
- **API路径**: `/users/login`
- **请求方法**: `POST`
- **请求参数**:
  | 参数名 | 数据类型 | 是否必填 | 说明 |
  | --- | --- | --- | --- |
  | username | String | 是 | 用户名 |
  | password | String | 是 | 密码 |
- **响应格式**:
  - 成功响应:
    ```json
    {
      "code": 200,
      "message": "成功",
      "data": {
        "token": "token-1234567890",
        "userId": 1,
        "username": "2023020616",
        "name": "张三",
        "role": 1,
        "email": "zhangsan@example.com",
        "phone": "13800138000"
      }
    }
    ```
  - 错误响应:
    ```json
    {
      "code": 500,
      "message": "登录失败",
      "data": null
    }
    ```
- **认证方式**: 无（微服务内部调用）
- **调用限制**: 无
- **使用示例**:
  ```bash
  curl -X POST http://localhost:8082/users/login \
    -H "Content-Type: application/json" \
    -d '{"username": "2023020616", "password": "123456"}'
  ```

## 7. 通用响应格式

所有API响应均使用统一的格式：

```json
{
  "code": 200,
  "message": "成功",
  "data": {...}
}
```

- **code**: 状态码，200表示成功，其他表示错误
- **message**: 响应消息
- **data**: 响应数据

## 8. 认证方式

- **JWT token**: 大多数API需要在请求头中传递Authorization: Bearer {token}
- **无认证**: 登录、注册等接口不需要认证

## 9. 调用限制

- 无特殊限制
- 建议合理控制请求频率，避免系统负载过高

## 10. 错误处理

- 所有错误响应均返回统一格式
- 错误消息清晰明了，便于前端处理
- 服务器内部错误返回500状态码
- 客户端错误返回400系列状态码

## 11. 边界条件处理

- 所有API均处理了边界条件
- 例如：学生不存在、参数为空、格式错误等情况
- 对于分页查询，处理了页号和每页大小的边界值
