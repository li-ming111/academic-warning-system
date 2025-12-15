<template>
  <div class="teacher-layout">
    <el-container>
      <!-- 侧边栏 -->
      <el-aside width="260px" class="sidebar">
        <div class="logo-section">
          <div class="logo-icon">👨‍🏫</div>
          <h2>教师工作台</h2>
          <div class="teacher-info">
            <el-avatar :size="45" style="background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);">
              {{ teacherName.charAt(0) }}
            </el-avatar>
            <div class="info-text">
              <p class="name">{{ teacherName }}</p>
              <p class="role">授课教师</p>
            </div>
          </div>
        </div>
        
        <el-menu
          :default-active="activeMenu"
          router
          :unique-opened="true"
          background-color="#2c3e50"
          text-color="#b8c7ce"
          active-text-color="#ffffff"
          class="teacher-menu"
        >
          <el-menu-item index="/teacher/dashboard">
            <el-icon><HomeFilled /></el-icon>
            <span>📊 教学数据概览</span>
          </el-menu-item>
          
          <el-menu-item index="/teacher/scores">
            <el-icon><DocumentCopy /></el-icon>
            <span>📝 学生成绩管理</span>
          </el-menu-item>
          
          <el-menu-item index="/teacher/warnings">
            <el-icon><Warning /></el-icon>
            <span>⚠️ 学业预警管理</span>
          </el-menu-item>
          
          <el-menu-item index="/teacher/analysis">
            <el-icon><DataAnalysis /></el-icon>
            <span>📈 学生表现分析</span>
          </el-menu-item>
          
          <el-menu-item index="/teacher/feedback">
            <el-icon><ChatDotRound /></el-icon>
            <span>💬 学生反馈管理</span>
          </el-menu-item>
          
          <el-menu-item index="/teacher/courses">
            <el-icon><Reading /></el-icon>
            <span>📚 选修课信息</span>
          </el-menu-item>
        </el-menu>
      </el-aside>

      <!-- 主内容区 -->
      <el-container>
        <!-- 顶部栏 -->
        <el-header class="header">
          <div class="header-left">
            <h1 class="page-title">{{ currentPageTitle }}</h1>
            <el-breadcrumb separator="/" class="breadcrumb">
              <el-breadcrumb-item :to="{ path: '/teacher/dashboard' }">首页</el-breadcrumb-item>
              <el-breadcrumb-item>{{ currentPageTitle }}</el-breadcrumb-item>
            </el-breadcrumb>
          </div>
          <div class="header-right">
            <el-badge :value="3" class="notification-badge">
              <el-icon :size="22" style="cursor: pointer;"><Bell /></el-icon>
            </el-badge>
            <el-dropdown>
              <el-avatar :size="40" style="cursor: pointer; background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);">
                {{ teacherName.charAt(0) }}
              </el-avatar>
              <template #dropdown>
                <el-dropdown-menu>
                  <el-dropdown-item>个人设置</el-dropdown-item>
                  <el-dropdown-item divided @click="handleLogout">
                    <el-icon><SwitchButton /></el-icon>
                    退出登录
                  </el-dropdown-item>
                </el-dropdown-menu>
              </template>
            </el-dropdown>
          </div>
        </el-header>

        <!-- 内容区域 -->
        <el-main class="main-content">
          <router-view />
        </el-main>
      </el-container>
    </el-container>
  </div>
</template>

<script setup>
import { ref, computed, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { 
  HomeFilled, 
  DocumentCopy, 
  Warning, 
  DataAnalysis, 
  ChatDotRound, 
  Reading,
  SwitchButton,
  Bell
} from '@element-plus/icons-vue'

const route = useRoute()
const router = useRouter()
const teacherName = ref(localStorage.getItem('username') || '教师')
const activeMenu = ref(route.path)

const pageMap = {
  '/teacher/dashboard': '教学数据概览',
  '/teacher/scores': '学生成绩管理',
  '/teacher/warnings': '学业预警管理',
  '/teacher/analysis': '学生表现分析',
  '/teacher/feedback': '学生反馈管理',
  '/teacher/courses': '选修课信息'
}

const currentPageTitle = computed(() => {
  return pageMap[route.path] || '教师端'
})

watch(() => route.path, (newPath) => {
  activeMenu.value = newPath
})

const handleLogout = () => {
  // 清除localStorage中的所有用户信息
  localStorage.removeItem('token')
  localStorage.removeItem('userId')
  localStorage.removeItem('username')
  localStorage.removeItem('role')
  // 跳转到登录页面
  router.push('/login')
}
</script>

<style scoped>
.teacher-layout {
  height: 100vh;
  overflow: hidden;
}

.el-container {
  height: 100%;
}

.sidebar {
  background: linear-gradient(180deg, #2c3e50 0%, #34495e 100%);
  height: 100vh;
  overflow-y: auto;
  box-shadow: 4px 0 16px rgba(0, 0, 0, 0.15);
  position: relative;
  transition: all 0.3s ease;
}

.sidebar::after {
  content: '';
  position: absolute;
  right: 0;
  top: 0;
  width: 1px;
  height: 100%;
  background: linear-gradient(180deg, rgba(255,255,255,0.1) 0%, transparent 50%, rgba(255,255,255,0.1) 100%);
}

.logo-section {
  padding: 30px 20px 25px;
  text-align: center;
  background: linear-gradient(135deg, rgba(102, 126, 234, 0.2) 0%, rgba(118, 75, 162, 0.2) 100%);
  border-bottom: 2px solid rgba(102, 126, 234, 0.3);
  animation: fadeInDown 0.8s ease-out;
}

@keyframes fadeInDown {
  from {
    opacity: 0;
    transform: translateY(-20px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.logo-icon {
  font-size: 48px;
  margin-bottom: 12px;
  animation: float 3s ease-in-out infinite, pulse 2s ease-in-out infinite;
  display: inline-block;
  transition: transform 0.3s ease;
}

.logo-icon:hover {
  transform: scale(1.1) translateY(-8px);
}

@keyframes float {
  0%, 100% { transform: translateY(0); }
  50% { transform: translateY(-8px); }
}

@keyframes pulse {
  0%, 100% { filter: drop-shadow(0 0 0 rgba(102, 126, 234, 0.4)); }
  50% { filter: drop-shadow(0 0 8px rgba(102, 126, 234, 0.8)); }
}

.logo-section h2 {
  margin: 0 0 20px 0;
  color: #fff;
  font-size: 22px;
  font-weight: bold;
  letter-spacing: 1px;
  background: linear-gradient(135deg, #fff 0%, #e0e7ff 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.teacher-info {
  display: flex;
  align-items: center;
  gap: 12px;
  background: rgba(255, 255, 255, 0.12);
  padding: 12px;
  border-radius: 12px;
  backdrop-filter: blur(10px);
  border: 1px solid rgba(102, 126, 234, 0.3);
  transition: all 0.3s ease;
  animation: slideInRight 0.6s ease-out;
}

.teacher-info:hover {
  background: rgba(255, 255, 255, 0.18);
  border-color: rgba(102, 126, 234, 0.6);
  box-shadow: 0 4px 12px rgba(102, 126, 234, 0.2);
}

@keyframes slideInRight {
  from {
    opacity: 0;
    transform: translateX(-15px);
  }
  to {
    opacity: 1;
    transform: translateX(0);
  }
}

.info-text {
  flex: 1;
  text-align: left;
}

.info-text .name {
  margin: 0;
  font-size: 15px;
  font-weight: 600;
  color: #fff;
}

.info-text .role {
  margin: 4px 0 0 0;
  font-size: 12px;
  color: #b8c7ce;
}

.el-menu {
  border: none;
  padding: 12px 8px;
}

:deep(.el-menu-item) {
  margin: 6px 8px;
  border-radius: 10px;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  font-weight: 500;
  position: relative;
  overflow: hidden;
}

:deep(.el-menu-item::before) {
  content: '';
  position: absolute;
  left: 0;
  top: 0;
  width: 4px;
  height: 0;
  background: linear-gradient(180deg, #667eea 0%, #764ba2 100%);
  transition: height 0.3s ease;
}

:deep(.el-menu-item:hover) {
  background: rgba(102, 126, 234, 0.15) !important;
  transform: translateX(4px);
  box-shadow: 0 2px 8px rgba(102, 126, 234, 0.2);
}

:deep(.el-menu-item:hover::before) {
  height: 100%;
}

:deep(.el-menu-item.is-active) {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%) !important;
  box-shadow: 0 4px 16px rgba(102, 126, 234, 0.4);
  font-weight: 600;
  transform: translateX(4px);
}

:deep(.el-menu-item.is-active::before) {
  height: 100%;
}

:deep(.el-menu-item .el-icon) {
  font-size: 18px;
  transition: transform 0.3s ease;
}

:deep(.el-menu-item:hover .el-icon) {
  transform: scale(1.1);
}

:deep(.el-menu-item.is-active .el-icon) {
  animation: iconPulse 0.6s ease-out;
}

@keyframes iconPulse {
  0% {
    transform: scale(1);
  }
  50% {
    transform: scale(1.2);
  }
  100% {
    transform: scale(1);
  }
}

.header {
  background: linear-gradient(90deg, #ffffff 0%, #f8f9fa 100%);
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.08);
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 0 32px;
  border-bottom: 2px solid #e8ecf1;
  position: relative;
}

.header::after {
  content: '';
  position: absolute;
  bottom: -2px;
  left: 0;
  right: 0;
  height: 2px;
  background: linear-gradient(90deg, #667eea 0%, #764ba2 100%, transparent 100%);
  opacity: 0.3;
}

.header-left {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.page-title {
  margin: 0;
  font-size: 22px;
  font-weight: bold;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
  animation: slideDown 0.6s ease-out;
}

@keyframes slideDown {
  from {
    opacity: 0;
    transform: translateY(-10px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.breadcrumb {
  font-size: 13px;
}

.header-right {
  display: flex;
  gap: 24px;
  align-items: center;
}

.notification-badge {
  cursor: pointer;
  transition: all 0.3s ease;
  animation: fadeIn 0.6s ease-out;
}

.notification-badge:hover {
  transform: scale(1.1) rotate(10deg);
  filter: drop-shadow(0 2px 8px rgba(102, 126, 234, 0.4));
}

@keyframes fadeIn {
  from {
    opacity: 0;
  }
  to {
    opacity: 1;
  }
}

.main-content {
  background: linear-gradient(135deg, #f5f7fa 0%, #e8ecf1 100%);
  overflow-y: auto;
  height: calc(100vh - 60px);
  position: relative;
}

.main-content::before {
  content: '';
  position: fixed;
  top: 0;
  left: 260px;
  right: 0;
  bottom: 0;
  background: radial-gradient(circle at top right, rgba(102, 126, 234, 0.05) 0%, transparent 50%);
  pointer-events: none;
  z-index: 0;
}
</style>
