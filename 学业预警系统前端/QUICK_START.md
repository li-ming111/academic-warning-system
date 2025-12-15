# 学业预警系统 - 前端快速启动指南

## 项目概述

学业预警系统前端是一个基于 Vue 3 + Vite 的现代化Web应用，提供学生、教师、辅导员和管理员四个角色的完整功能模块。

## 技术栈

- **框架**: Vue 3.3.4
- **构建工具**: Vite 4.4.9
- **UI库**: Element Plus 2.4.0
- **HTTP客户端**: Axios 1.5.0
- **状态管理**: Pinia 2.1.4
- **路由**: Vue Router 4.2.4
- **数据可视化**: ECharts 5.4.2

## 快速启动

### 1. 前置条件

- Node.js 16+ (推荐18+)
- npm 8+ 或 yarn

### 2. 安装依赖

```bash
cd 学业预警系统前端
npm install
```

### 3. 开发模式启动

```bash
npm run dev
```

应用将在 `http://localhost:5173` 启动

### 4. 构建生产版本

```bash
npm run build
```

## 项目结构

```
src/
├── api/                   # API接口定义
│   ├── index.js          # API方法导出
│   └── client.js         # Axios客户端配置
├── views/                 # 页面组件
│   ├── Login.vue         # 登录页面
│   ├── Register.vue      # 注册页面
│   ├── Dashboard.vue     # 学生仪表板
│   ├── Scores.vue        # 成绩分析
│   ├── Warnings.vue      # 预警管理
│   ├── Assistance.vue    # 帮扶计划
│   ├── TeacherDashboard.vue    # 教师仪表板
│   ├── CounselorDashboard.vue  # 辅导员仪表板
│   └── AdminDashboard.vue      # 管理员仪表板
├── router/                # 路由配置
│   └── index.js
├── store/                 # 状态管理
│   └── index.js
├── App.vue               # 根组件
└── main.js               # 应用入口

public/                    # 静态资源
index.html                 # HTML模板
vite.config.js            # Vite配置
```

## 页面功能说明

### 认证模块

#### 登录页面 (Login.vue)
- 输入用户名和密码
- 自动保存 token 和用户信息到 localStorage
- 登录成功后重定向到各角色首页

#### 注册页面 (Register.vue)
- 学生注册：输入用户名、密码、学号
- 自动识别学号对应的专业

### 学生模块

#### 学业看板 (Dashboard.vue)
- 显示课程总数、当前GPA、预警统计
- 红黄预警分类统计
- 最近预警列表
- 进行中的帮扶计划

#### 成绩分析 (Scores.vue)
- 显示学生所有成绩
- 按学期筛选
- 显示单科成绩、学分、绩点
- 支持成绩申诉

#### 预警管理 (Warnings.vue)
- 显示学业预警列表
- 按预警等级筛选（高/中/低）
- 显示预警描述和状态
- 支持预警详情查看

#### 帮扶计划 (Assistance.vue)
- 显示帮扶计划列表
- 进度条展示完成度
- 实时更新计划进度
- 支持标记完成

### 教师模块

#### 教师仪表板 (TeacherDashboard.vue)
- 统计：学生总数、预警数、高危/中危预警
- 班级学生列表
- 待处理预警列表
- 一键处理预警

### 辅导员模块

#### 辅导员仪表板 (CounselorDashboard.vue)
- 统计：班级学生数、预警人数、高危/中危预警
- 班级学生概览及预警状态
- 待处理预警列表
- 批量处理预警功能

### 管理员模块

#### 管理员仪表板 (AdminDashboard.vue)
- 全局统计：学院数、专业数、用户数、学生数、教师数、预警数
- 学院管理：新增、编辑、删除学院
- 用户管理：查看用户列表、启用/禁用用户

## API 调用流程

### 认证流程

```
1. 用户输入用户名和密码
   ↓
2. 调用 authAPI.login(username, password)
   ↓
3. 服务器验证并返回 { token, userId, username, role }
   ↓
4. 前端保存到 localStorage
   ↓
5. Axios 拦截器自动添加 Authorization header
   ↓
6. 所有后续请求都携带 token
```

### 数据获取流程

```
1. 组件 onMounted 生命周期触发
   ↓
2. 调用相应 API 方法（如 studentAPI.getDashboard(userId)）
   ↓
3. Axios 发送请求
   ↓
4. 响应拦截器检查状态（401 自动登出）
   ↓
5. 组件接收数据并渲染
```

## 配置说明

### Vite 代理配置 (vite.config.js)

```javascript
server: {
  proxy: {
    '/api': {
      target: 'http://localhost:8080',  // 后端服务地址
      changeOrigin: true,
      rewrite: (path) => path.replace(/^\/api/, '/api')
    }
  }
}
```

### Axios 配置 (src/api/client.js)

- 基础URL: `http://localhost:8080/api`
- 请求超时: 10秒
- 自动添加 Authorization header

## 状态管理

使用 Pinia 管理全局状态：

```javascript
// 存储用户信息
const userStore = useUserStore()
userStore.setUser(userData)

// 获取用户信息
const user = userStore.user
```

## 路由保护

所有需要认证的路由都受保护：

```javascript
// 全局路由守卫
router.beforeEach((to, from, next) => {
  const hasToken = localStorage.getItem('token')
  
  if (to.meta.requiresAuth && !hasToken) {
    // 无token则重定向到登录
    next('/login')
  }
})
```

## 常见问题

### Q: 如何修改后端服务地址？
A: 修改 `vite.config.js` 中的 proxy.target

### Q: 如何添加新页面？
A:
1. 在 `views/` 下创建 `.vue` 文件
2. 在 `router/index.js` 中添加路由配置
3. 在导航菜单中添加链接

### Q: 如何添加新的 API 调用？
A:
1. 在 `src/api/index.js` 中定义新方法
2. 在组件中导入并调用
3. 处理响应数据

### Q: localStorage 中存储的数据有哪些？
A:
- `token` - JWT token
- `userId` - 用户ID
- `username` - 用户名
- `role` - 用户角色

## 部署

### 构建为静态文件

```bash
npm run build
```

生成的 `dist/` 目录可部署到Web服务器

### Docker 部署

```dockerfile
FROM node:18-alpine as builder
WORKDIR /app
COPY package*.json ./
RUN npm install
COPY . .
RUN npm run build

FROM nginx:alpine
COPY --from=builder /app/dist /usr/share/nginx/html
EXPOSE 80
CMD ["nginx", "-g", "daemon off;"]
```

## 浏览器兼容性

- Chrome (最新)
- Firefox (最新)
- Safari (最新)
- Edge (最新)

## 性能优化建议

1. 使用 Code Splitting 分割大的 JavaScript 包
2. 启用 Gzip 压缩
3. 使用 CDN 加速静态资源
4. 定期更新依赖包

## 开发建议

1. 使用 Vue DevTools 浏览器扩展调试
2. 在浏览器 Network 标签查看 API 调用
3. 使用 localStorage 临时存储开发数据
4. 定期检查浏览器控制台错误

## 支持

如有问题或建议，请联系技术团队。

---

最后更新: 2025-12-11
