<template>
  <div class="teacher-layout">
    <!-- 顶部导航栏 -->
    <div class="top-navbar">
      <div class="navbar-left">
        <div class="logo">
          <img :src="logoImg" alt="Logo" class="logo-image">
          <span class="logo-text">教师工作台</span>
        </div>
      </div>
      <div class="navbar-center">
        <div class="breadcrumb">
          <span class="breadcrumb-item">{{ currentPageTitle }}</span>
        </div>
      </div>
      <div class="navbar-right">
        <div class="user-info">
          <el-icon class="notification-icon" style="cursor: pointer;"><Bell /></el-icon>
          <el-dropdown trigger="click">
            <div class="user-avatar">{{ teacherName.charAt(0) }}</div>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item @click="handleLogout">
                  <el-icon><SwitchButton /></el-icon>
                  <span style="margin-left: 8px;">退出登录</span>
                </el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>
      </div>
    </div>

    <div class="main-wrapper">
      <!-- 左侧菜单栏 -->
      <div class="sidebar">
        <div class="menu-section">
          <div class="menu-title">主要功能</div>
          <nav class="menu-list">
            <router-link 
              to="/teacher/dashboard" 
              class="menu-item"
              :class="{ active: isActive('/teacher/dashboard') }"
            >
              <el-icon><HomeFilled /></el-icon>
              <span>数据概览</span>
            </router-link>
            <router-link 
              to="/teacher/scores" 
              class="menu-item"
              :class="{ active: isActive('/teacher/scores') }"
            >
              <el-icon><DocumentCopy /></el-icon>
              <span>成绩管理</span>
            </router-link>
            <router-link 
              to="/teacher/warnings" 
              class="menu-item"
              :class="{ active: isActive('/teacher/warnings') }"
            >
              <el-icon><Warning /></el-icon>
              <span>预警管理</span>
            </router-link>
            <router-link 
              to="/teacher/analysis" 
              class="menu-item"
              :class="{ active: isActive('/teacher/analysis') }"
            >
              <el-icon><DataAnalysis /></el-icon>
              <span>班级分析</span>
            </router-link>
          </nav>
        </div>

        <div class="menu-section">
          <div class="menu-title">其他管理</div>
          <nav class="menu-list">
            <router-link 
              to="/teacher/feedback" 
              class="menu-item"
              :class="{ active: isActive('/teacher/feedback') }"
            >
              <el-icon><ChatDotRound /></el-icon>
              <span>反馈管理</span>
            </router-link>
            <router-link 
              to="/teacher/courses" 
              class="menu-item"
              :class="{ active: isActive('/teacher/courses') }"
            >
              <el-icon><Reading /></el-icon>
              <span>选修课</span>
            </router-link>
            <router-link 
              to="/teacher/class-management" 
              class="menu-item"
              :class="{ active: isActive('/teacher/class-management') }"
            >
              <el-icon><Management /></el-icon>
              <span>班级管理</span>
            </router-link>
          </nav>
        </div>

        <div class="menu-footer">
          <div class="user-card">
            <div class="user-avatar-large">{{ teacherName.charAt(0) }}</div>
            <div class="user-details">
              <div class="user-name">{{ teacherName }}</div>
              <div class="user-role">授课教师</div>
            </div>
          </div>
        </div>
      </div>

      <!-- 右侧内容区 -->
      <div class="content-area">
        <router-view />
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import logoImg from '../assets/xiaohui.jpg'
import { 
  HomeFilled,
  DocumentCopy,
  Warning,
  DataAnalysis,
  ChatDotRound,
  Reading,
  Management,
  SwitchButton,
  Bell
} from '@element-plus/icons-vue'

const route = useRoute()
const router = useRouter()
const teacherName = ref(localStorage.getItem('username') || '教师')

const pageMap = {
  '/teacher/dashboard': '教学数据概览',
  '/teacher/scores': '学生成绩管理',
  '/teacher/warnings': '学业预警管理',
  '/teacher/analysis': '班级性能分析',
  '/teacher/feedback': '学生反馈管理',
  '/teacher/courses': '选修课信息',
  '/teacher/credit-prediction': '学分预测',
  '/teacher/audit-log': '审计日志',
  '/teacher/feedback-management': '反馈处理',
  '/teacher/class-management': '班级管理'
}

const currentPageTitle = computed(() => {
  return pageMap[route.path] || '教师工作台'
})

const isActive = (path) => {
  return route.path === path
}

const handleLogout = () => {
  localStorage.removeItem('token')
  localStorage.removeItem('userId')
  localStorage.removeItem('username')
  localStorage.removeItem('role')
  router.push('/login')
}
</script>

<style scoped>
* {
  box-sizing: border-box;
}

.teacher-layout {
  height: 100vh;
  display: flex;
  flex-direction: column;
  background: #f5f7fa;
}

/* 顶部导航栏 */
.top-navbar {
  height: 64px;
  background: linear-gradient(90deg, #ffffff 0%, #f8f9fa 100%);
  border-bottom: 1px solid #e8ecf1;
  display: flex;
  align-items: center;
  padding: 0 16px;
  gap: 20px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
}

.navbar-left {
  flex-shrink: 0;
}

.logo {
  display: flex;
  align-items: center;
  gap: 12px;
  font-size: 18px;
  font-weight: 600;
  color: #2c3e50;
}

.logo-image {
  height: 40px;
  width: 40px;
  border-radius: 50%;
  object-fit: cover;
}

.navbar-center {
  flex: 1;
  text-align: left;
}

.breadcrumb {
  font-size: 16px;
  font-weight: 500;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.breadcrumb-item {
  cursor: pointer;
}

.navbar-right {
  flex-shrink: 0;
  display: flex;
  align-items: center;
  gap: 16px;
}

.user-info {
  display: flex;
  align-items: center;
  gap: 12px;
}

.notification-icon {
  font-size: 22px;
  color: #666;
  transition: all 0.3s ease;
}

.notification-icon:hover {
  color: #667eea;
  transform: scale(1.1);
}

.user-avatar {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  display: flex;
  align-items: center;
  justify-content: center;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s ease;
  box-shadow: 0 2px 8px rgba(102, 126, 234, 0.3);
}

.user-avatar:hover {
  transform: scale(1.05);
  box-shadow: 0 4px 12px rgba(102, 126, 234, 0.4);
}

/* 主体区域 */
.main-wrapper {
  flex: 1;
  display: flex;
  overflow: hidden;
}

/* 左侧菜单 */
.sidebar {
  width: 220px;
  background: linear-gradient(180deg, #2c3e50 0%, #34495e 100%);
  display: flex;
  flex-direction: column;
  box-shadow: none;
  overflow-y: auto;
  padding: 0;
  margin: 0;
}

.menu-section {
  padding: 16px 8px;
}

.menu-section:not(:last-child) {
  border-bottom: 1px solid rgba(255, 255, 255, 0.1);
}

.menu-title {
  font-size: 12px;
  font-weight: 600;
  color: rgba(255, 255, 255, 0.5);
  text-transform: uppercase;
  letter-spacing: 1px;
  margin-bottom: 12px;
  padding: 0 8px;
}

.menu-list {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.menu-item {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 10px 12px;
  border-radius: 8px;
  color: #b8c7ce;
  text-decoration: none;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  cursor: pointer;
  font-size: 14px;
  font-weight: 500;
  position: relative;
  overflow: hidden;
}

.menu-item::before {
  content: '';
  position: absolute;
  left: 0;
  top: 0;
  width: 3px;
  height: 0;
  background: linear-gradient(180deg, #667eea 0%, #764ba2 100%);
  transition: height 0.3s ease;
}

.menu-item:hover {
  background: rgba(102, 126, 234, 0.15);
  color: #ffffff;
  transform: translateX(4px);
}

.menu-item:hover::before {
  height: 100%;
}

.menu-item.active {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: #ffffff;
  box-shadow: 0 4px 12px rgba(102, 126, 234, 0.4);
  transform: translateX(4px);
}

.menu-item.active::before {
  height: 100%;
}

.menu-item :deep(.el-icon) {
  font-size: 18px;
  flex-shrink: 0;
}

/* 菜单底部用户卡片 */
.menu-footer {
  margin-top: auto;
  padding: 8px;
  border-top: 1px solid rgba(255, 255, 255, 0.1);
}

.user-card {
  background: rgba(102, 126, 234, 0.15);
  border-radius: 8px;
  padding: 12px;
  display: flex;
  align-items: center;
  gap: 12px;
  border: 1px solid rgba(102, 126, 234, 0.3);
  transition: all 0.3s ease;
}

.user-card:hover {
  background: rgba(102, 126, 234, 0.25);
  border-color: rgba(102, 126, 234, 0.5);
}

.user-avatar-large {
  width: 44px;
  height: 44px;
  border-radius: 50%;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  display: flex;
  align-items: center;
  justify-content: center;
  font-weight: 600;
  flex-shrink: 0;
}

.user-details {
  flex: 1;
  min-width: 0;
}

.user-name {
  font-size: 13px;
  font-weight: 600;
  color: #ffffff;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.user-role {
  font-size: 11px;
  color: rgba(255, 255, 255, 0.7);
  margin-top: 2px;
}

/* 右侧内容区 */
.content-area {
  flex: 1;
  overflow-y: auto;
  display: flex;
  flex-direction: column;
  background: linear-gradient(135deg, #f5f7fa 0%, #e8ecf1 100%);
  padding: 0;
  margin: 0;
}

.content-area::-webkit-scrollbar {
  width: 0 !important;
  background: transparent !important;
}

.content-area::-webkit-scrollbar-track {
  background: transparent !important;
  display: none !important;
}

.content-area::-webkit-scrollbar-thumb {
  background: transparent !important;
  display: none !important;
}

.content-area::-webkit-scrollbar-thumb:hover {
  background: transparent !important;
}

/* 响应式设计 */
@media (max-width: 1200px) {
  .sidebar {
    width: 180px;
  }
  
  .menu-title {
    font-size: 11px;
  }
  
  .menu-item {
    font-size: 13px;
    padding: 10px 12px;
  }
}

@media (max-width: 768px) {
  .sidebar {
    width: 160px;
  }
  
  .logo-text {
    display: none;
  }
  
  .breadcrumb {
    font-size: 14px;
  }
}
</style>
