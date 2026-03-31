<template>
  <div class="admin-layout">
    <!-- 顶部导航栏 -->
    <div class="top-navbar">
      <div class="navbar-left">
        <div class="logo">
          <img src="@/assets/logo.png" alt="哈尔滨信息工程学院" class="logo-image">
        </div>
        <nav class="main-nav">
          <router-link 
            to="/admin/dashboard" 
            class="nav-item"
            :class="{ active: isActive('/admin/dashboard') }"
          >
            <span>首页</span>
          </router-link>
          <router-link 
            to="/admin/colleges" 
            class="nav-item"
            :class="{ active: isActive('/admin/colleges') }"
          >
            <span>学院管理</span>
          </router-link>
          <router-link 
            to="/admin/majors" 
            class="nav-item"
            :class="{ active: isActive('/admin/majors') }"
          >
            <span>专业管理</span>
          </router-link>
          <router-link 
            to="/admin/courses" 
            class="nav-item"
            :class="{ active: isActive('/admin/courses') }"
          >
            <span>课程管理</span>
          </router-link>
          <router-link 
            to="/admin/users" 
            class="nav-item"
            :class="{ active: isActive('/admin/users') }"
          >
            <span>用户管理</span>
          </router-link>
          <router-link 
            to="/admin/rules" 
            class="nav-item"
            :class="{ active: isActive('/admin/rules') }"
          >
            <span>规则管理</span>
          </router-link>
          <router-link 
            to="/admin/statistics" 
            class="nav-item"
            :class="{ active: isActive('/admin/statistics') }"
          >
            <span>数据分析</span>
          </router-link>
          <router-link 
            to="/admin/messages" 
            class="nav-item"
            :class="{ active: isActive('/admin/messages') }"
          >
            <span>消息通知</span>
          </router-link>
          <router-link 
            to="/admin/data-export" 
            class="nav-item"
            :class="{ active: isActive('/admin/data-export') }"
          >
            <span>数据导出</span>
          </router-link>
          <router-link 
            to="/admin/class-management" 
            class="nav-item"
            :class="{ active: isActive('/admin/class-management') }"
          >
            <span>班级管理申请</span>
          </router-link>
          <router-link 
            to="/admin/settings" 
            class="nav-item"
            :class="{ active: isActive('/admin/settings') }"
          >
            <span>个人设置</span>
          </router-link>
        </nav>
      </div>
      <div class="navbar-right">
        <div class="user-info">
          <el-dropdown trigger="click">
            <div class="user-profile">
              <div class="user-avatar">{{ adminName.charAt(0) }}</div>
              <span class="user-name">{{ adminName }}</span>
            </div>
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
      <!-- 内容区 -->
      <div class="content-area">
        <router-view />
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { 
  SwitchButton
} from '@element-plus/icons-vue'
import { adminAPI } from '@/api/index'

const route = useRoute()
const router = useRouter()
const storedName = localStorage.getItem('userName')
const storedRole = localStorage.getItem('role')
let defaultName = '管理员'
// 检查storedName是否存在且不是纯数字（避免显示用户ID）
if (storedName && !/^\d+$/.test(storedName)) {
  defaultName = storedName
} else {
  // 如果没有userName或userName是纯数字，根据角色显示默认名称
  if (storedRole === '4' || storedRole === 'admin') {
    defaultName = '管理员'
  } else {
    defaultName = '用户'
  }
}
const adminName = ref(defaultName)

// 获取管理员信息
const loadAdminInfo = async () => {
  try {
    const response = await adminAPI.getDashboard()
    if (response && (response.adminName || response.name)) {
      const adminNameFromApi = response.adminName || response.name
      adminName.value = adminNameFromApi
      // 更新localStorage中的userName
      localStorage.setItem('userName', adminNameFromApi)
    }
  } catch (error) {
    console.error('加载管理员信息失败:', error)
  }
}

// 组件挂载时加载管理员信息
onMounted(() => {
  loadAdminInfo()
})

const pageMap = {
  '/admin/dashboard': '数据统计',
  '/admin/colleges': '学院管理',
  '/admin/majors': '专业管理',
  '/admin/courses': '课程管理',
  '/admin/users': '用户管理',
  '/admin/rules': '规则管理',
  '/admin/statistics': '数据分析',
  '/admin/messages': '消息通知',
  '/admin/data-export': '数据导出',
  '/admin/class-management': '班级管理申请',
  '/admin/settings': '个人设置'
}

const currentPageTitle = computed(() => {
  return pageMap[route.path] || '管理工作台'
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

.admin-layout {
  height: 100vh;
  display: flex;
  flex-direction: column;
  background: #f5f7fa;
}

/* 顶部导航栏 */
.top-navbar {
  height: 60px;
  background: #ffffff;
  border-bottom: 1px solid #e0e0e0;
  display: flex;
  align-items: center;
  padding: 0 20px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.05);
  justify-content: space-between;
  flex-wrap: wrap;
  gap: 20px;
}

.navbar-left {
  display: flex;
  align-items: center;
  gap: 30px;
  flex-wrap: wrap;
}

.navbar-right {
  display: flex;
  align-items: center;
}

.logo-image {
  height: 40px;
  object-fit: contain;
}

.main-nav {
  display: flex;
  gap: 15px;
  align-items: center;
  flex-wrap: wrap;
}

.nav-item {
  color: #333;
  text-decoration: none;
  font-size: 14px;
  font-weight: 500;
  padding: 8px 0;
  position: relative;
  transition: all 0.3s ease;
  white-space: nowrap;
}

.nav-item:hover {
  color: #4facfe;
}

.nav-item.active {
  color: #4facfe;
  font-weight: 600;
}

.nav-item.active::after {
  content: '';
  position: absolute;
  bottom: 0;
  left: 0;
  width: 100%;
  height: 2px;
  background: #4facfe;
}

.navbar-right {
  flex-shrink: 0;
  display: flex;
  align-items: center;
}

.user-info {
  display: flex;
  align-items: center;
}

.user-profile {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 6px 12px;
  border-radius: 20px;
  transition: all 0.3s ease;
  cursor: pointer;
}

.user-profile:hover {
  background: #f0f0f0;
}

.user-avatar {
  width: 32px;
  height: 32px;
  border-radius: 50%;
  background: #f0f0f0;
  color: #333;
  display: flex;
  align-items: center;
  justify-content: center;
  font-weight: 600;
}

.user-name {
  font-size: 14px;
  font-weight: 500;
  color: #333;
  white-space: nowrap;
}

/* 主体区域 */
.main-wrapper {
  flex: 1;
  display: flex;
  overflow: hidden;
}

/* 内容区 */
.content-area {
  flex: 1;
  overflow-y: auto;
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
@media (max-width: 1400px) {
  .main-nav {
    gap: 10px;
  }
  
  .nav-item {
    font-size: 13px;
  }
}

@media (max-width: 1200px) {
  .top-navbar {
    padding: 0 15px;
    gap: 15px;
  }
  
  .navbar-left {
    gap: 20px;
  }
  
  .main-nav {
    gap: 10px;
  }
  
  .nav-item {
    font-size: 12px;
  }
}

@media (max-width: 768px) {
  .top-navbar {
    padding: 0 10px;
    gap: 10px;
  }
  
  .navbar-left {
    gap: 15px;
  }
  
  .main-nav {
    gap: 8px;
  }
  
  .nav-item {
    font-size: 11px;
  }
  
  .user-name {
    display: none;
  }
}
</style>
