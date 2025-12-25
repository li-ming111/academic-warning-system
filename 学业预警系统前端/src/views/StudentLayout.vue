<template>
  <div class="student-layout">
    <!-- 顶部导航栏 -->
    <div class="top-navbar">
      <div class="navbar-left">
        <div class="logo">
          <img :src="logoImg" alt="Logo" class="logo-image">
          <span class="logo-text">学生工作台</span>
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
            <div class="user-avatar">{{ studentName.charAt(0) }}</div>
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
          <nav class="menu-list">
            <router-link 
              to="/student/dashboard" 
              class="menu-item"
              :class="{ active: isActive('/student/dashboard') }"
            >
              <el-icon><HomeFilled /></el-icon>
              <span>数据概览</span>
            </router-link>
            <router-link 
              to="/student/scores" 
              class="menu-item"
              :class="{ active: isActive('/student/scores') }"
            >
              <el-icon><DocumentCopy /></el-icon>
              <span>成绩查询</span>
            </router-link>
            <router-link 
              to="/student/warnings" 
              class="menu-item"
              :class="{ active: isActive('/student/warnings') }"
            >
              <el-icon><Warning /></el-icon>
              <span>预警信息</span>
            </router-link>
            <router-link 
              to="/student/assistance" 
              class="menu-item"
              :class="{ active: isActive('/student/assistance') }"
            >
              <el-icon><Help /></el-icon>
              <span>帮扶信息</span>
            </router-link>
            <router-link 
              to="/student/appeals" 
              class="menu-item"
              :class="{ active: isActive('/student/appeals') }"
            >
              <el-icon><EditPen /></el-icon>
              <span>成绩申诉</span>
            </router-link>
            <router-link 
              to="/student/messages" 
              class="menu-item"
              :class="{ active: isActive('/student/messages') }"
            >
              <el-icon><ChatDotRound /></el-icon>
              <span>消息中心</span>
            </router-link>
            <router-link 
              to="/student/notification-center" 
              class="menu-item"
              :class="{ active: isActive('/student/notification-center') }"
            >
              <el-icon><Bell /></el-icon>
              <span>通知公告</span>
            </router-link>
            <router-link 
              to="/student/benchmark-analysis" 
              class="menu-item"
              :class="{ active: isActive('/student/benchmark-analysis') }"
            >
              <el-icon><DataAnalysis /></el-icon>
              <span>对标分析</span>
            </router-link>
            <router-link 
              to="/student/assistance-feedback" 
              class="menu-item"
              :class="{ active: isActive('/student/assistance-feedback') }"
            >
              <el-icon><Edit /></el-icon>
              <span>帮扶反馈</span>
            </router-link>
            <router-link 
              to="/student/settings" 
              class="menu-item"
              :class="{ active: isActive('/student/settings') }"
            >
              <el-icon><Setting /></el-icon>
              <span>个人设置</span>
            </router-link>
          </nav>
        </div>

        <div class="menu-footer">
          <div class="user-card">
            <div class="user-avatar-large">{{ studentName.charAt(0) }}</div>
            <div class="user-details">
              <div class="user-name">{{ studentName }}</div>
              <div class="user-role">学生</div>
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
  Help,
  EditPen,
  ChatDotRound,
  Bell,
  SwitchButton,
  DataAnalysis,
  Edit,
  Setting
} from '@element-plus/icons-vue'

const route = useRoute()
const router = useRouter()
const studentName = ref(localStorage.getItem('username') || '学生')

const pageMap = {
  '/student/dashboard': '学业数据概览',
  '/student/scores': '成绩查询',
  '/student/warnings': '预警信息',
  '/student/assistance': '帮扶信息',
  '/student/appeals': '成绩申诉',
  '/student/messages': '消息中心',
  '/student/notification-center': '通知公告',
  '/student/benchmark-analysis': '对标分析',
  '/student/assistance-feedback': '帮扶反馈',
  '/student/settings': '个人设置'
}

const currentPageTitle = computed(() => {
  return pageMap[route.path] || '学生工作台'
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

.student-layout {
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
  padding: 0 8px;
  gap: 8px;
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
  gap: 8px;
}

.user-info {
  display: flex;
  align-items: center;
  gap: 8px;
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
  padding: 12px 0;
  border-bottom: 1px solid rgba(255, 255, 255, 0.08);
}

.menu-section:last-of-type {
  border-bottom: none;
}

.menu-list {
  display: flex;
  flex-direction: column;
  gap: 0;
}

.menu-item {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 10px;
  padding: 14px 12px;
  border-radius: 0;
  color: rgba(184, 199, 206, 0.85);
  text-decoration: none;
  transition: all 0.2s ease;
  cursor: pointer;
  font-size: 13px;
  font-weight: 500;
  line-height: 1.2;
  position: relative;
  overflow: visible;
  border-left: 3px solid transparent;
  margin: 0;
}

.menu-item:hover {
  background: transparent;
  color: #ffffff;
  transform: none;
  border-left-color: #667eea;
}

.menu-item.active {
  background: rgba(102, 126, 234, 0.15);
  color: #ffffff;
  box-shadow: none;
  transform: none;
  border-left-color: #667eea;
}

.menu-item :deep(.el-icon) {
  font-size: 18px;
  flex-shrink: 0;
}

/* 菜单底部用户卡片 */
.menu-footer {
  margin-top: auto;
  padding: 12px;
  border-top: 1px solid rgba(255, 255, 255, 0.1);
}

.user-card {
  background: rgba(102, 126, 234, 0.15);
  border-radius: 8px;
  padding: 10px;
  display: flex;
  align-items: center;
  gap: 10px;
  border: 1px solid rgba(102, 126, 234, 0.3);
  transition: all 0.3s ease;
}

.user-card:hover {
  background: rgba(102, 126, 234, 0.25);
  border-color: rgba(102, 126, 234, 0.5);
}

.user-avatar-large {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  display: flex;
  align-items: center;
  justify-content: center;
  font-weight: 600;
  flex-shrink: 0;
  font-size: 14px;
}

.user-details {
  flex: 1;
  min-width: 0;
}

.user-name {
  font-size: 12px;
  font-weight: 600;
  color: #ffffff;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.user-role {
  font-size: 10px;
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
  
  .menu-item {
    font-size: 12px;
    gap: 8px;
    padding: 10px 10px;
  }

  .menu-item :deep(.el-icon) {
    font-size: 16px;
  }

  .user-card {
    padding: 8px;
    gap: 8px;
  }

  .user-avatar-large {
    width: 36px;
    height: 36px;
    font-size: 12px;
  }

  .user-name {
    font-size: 11px;
  }

  .user-role {
    font-size: 9px;
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
