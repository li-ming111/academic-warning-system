<template>
  <div class="app-wrapper">
    <!-- 固定侧边栏 - 仅在已登录+非登录/注册页显示 -->
    <aside 
      v-if="isLoggedIn && !isPureAuthPage" 
      class="sidebar"
      :class="{ 'sidebar-collapsed': isCollapsed }"
    >
      <div class="sidebar-header">
        <h2>📚 {{ isAdmin ? '管理系统' : '学业预警' }}</h2>
        <button v-if="!isAdmin" class="collapse-btn" @click="isCollapsed = !isCollapsed">
          {{ isCollapsed ? '▶' : '◀' }}
        </button>
      </div>
      <nav class="sidebar-nav">
        <!-- 管理员端导航 -->
        <template v-if="isAdmin">
          <router-link to="/admin/dashboard" class="nav-item" active-class="active">
            <span>📊 仪表盘</span>
          </router-link>
          <router-link to="/admin/colleges" class="nav-item" active-class="active">
            <span>🏫 学院管理</span>
          </router-link>
          <router-link to="/admin/majors" class="nav-item" active-class="active">
            <span>📚 专业管理</span>
          </router-link>
          <router-link to="/admin/courses" class="nav-item" active-class="active">
            <span>📖 课程管理</span>
          </router-link>
          <router-link to="/admin/users" class="nav-item" active-class="active">
            <span>👥 用户与权限管理</span>
          </router-link>
          <router-link to="/admin/rules" class="nav-item" active-class="active">
            <span>⚙️ 规则管理</span>
          </router-link>
          <router-link to="/admin/statistics" class="nav-item" active-class="active">
            <span>📊 数据分析</span>
          </router-link>
          <router-link to="/admin/messages" class="nav-item" active-class="active">
            <span>💬 消息通知</span>
          </router-link>
          <router-link to="/admin/data-export" class="nav-item" active-class="active">
            <span>📥 数据导出</span>
          </router-link>
        </template>

        <!-- 学生端导航 -->
        <template v-else-if="isStudent">
          <router-link 
            to="/dashboard" 
            class="nav-item" 
            active-class="active"
            exact-active-class="active"
          >
            <span>📊 智能看板</span>
          </router-link>
          <router-link to="/scores" class="nav-item" active-class="active">
            <span>📈 成绩与统计</span>
          </router-link>
          <router-link to="/warnings" class="nav-item" active-class="active">
            <span>⚠️ 智能预警提醒</span>
          </router-link>
          <router-link to="/assistance" class="nav-item" active-class="active">
            <span>🤝 帮扶计划追踪</span>
          </router-link>
          <router-link to="/notification-center" class="nav-item" active-class="active">
            <span>🔔 预警通知中心</span>
          </router-link>
          <router-link to="/benchmark-analysis" class="nav-item" active-class="active">
            <span>📊 个人对标分析</span>
          </router-link>
          <router-link to="/appeal-management" class="nav-item" active-class="active">
            <span>📋 成绩申诉管理</span>
          </router-link>
          <router-link to="/assistance-feedback" class="nav-item" active-class="active">
            <span>⭐ 帮扶反馈评价</span>
          </router-link>
          <router-link to="/settings" class="nav-item" active-class="active">
            <span>⚙️ 个人设置</span>
          </router-link>
        </template>

        <!-- 教师端导航 -->
        <template v-else-if="isTeacher">
          <router-link to="/teacher-dashboard" class="nav-item" active-class="active">
            <span>📊 教师看板</span>
          </router-link>
          <router-link to="/teacher-scores" class="nav-item" active-class="active">
            <span>📝 成绩管理</span>
          </router-link>
          <router-link to="/teacher-warnings" class="nav-item" active-class="active">
            <span>⚠️ 预警管理</span>
          </router-link>
          <router-link to="/teacher-analysis" class="nav-item" active-class="active">
            <span>📊 班级分析</span>
          </router-link>
          <router-link to="/teacher-feedback" class="nav-item" active-class="active">
            <span>💬 反馈管理</span>
          </router-link>
          <router-link to="/teacher-courses" class="nav-item" active-class="active">
            <span>📚 选修课管理</span>
          </router-link>
        </template>

        <!-- 辅导员端导航 -->
        <template v-else-if="isCounselor">
          <router-link to="/counselor-dashboard" class="nav-item" active-class="active">
            <span>👨‍🎓 学生情况概览</span>
          </router-link>
          <router-link to="/counselor/students" class="nav-item" active-class="active">
            <span>👥 学生管理</span>
          </router-link>
          <router-link to="/counselor/warnings" class="nav-item" active-class="active">
            <span>⚠️ 学生预警管理</span>
          </router-link>
          <router-link to="/counselor/courses" class="nav-item" active-class="active">
            <span>📚 选修课管理</span>
          </router-link>
          <router-link to="/counselor/credit-monitor" class="nav-item" active-class="active">
            <span>📊 学生数据分析</span>
          </router-link>
          <router-link to="/counselor/notifications" class="nav-item" active-class="active">
            <span>📢 批量通知</span>
          </router-link>
          <router-link to="/counselor/class-management" class="nav-item" active-class="active">
            <span>🏫 班级管理</span>
          </router-link>
          <router-link to="/counselor/settings" class="nav-item" active-class="active">
            <span>⚙️ 个人设置</span>
          </router-link>
        </template>

        <div class="nav-divider"></div>
        <button @click="handleLogout" class="nav-item logout-btn">
          <span>🚪 退出登录</span>
        </button>
      </nav>
    </aside>

    <!-- 主内容区 -->
    <div 
      :class="[
        'main-wrapper', 
        { 'full-wrapper': !isLoggedIn || isPureAuthPage },
        { 'main-wrapper-collapsed': isLoggedIn && !isPureAuthPage && isCollapsed }
      ]"
    >
      <!-- 深部 -->
      <header v-if="isLoggedIn && !isPureAuthPage" class="app-header" :class="{ 'admin-header': isAdmin }">
        <div class="header-content">{{ isAdmin ? '📊 学业预警系统 - 管理员端' : '学业预警与帮扶系统' }}</div>
        <div v-if="!isAdmin" class="header-user">
          {{ userName }} | {{ roleLabel }}
        </div>
        <div v-else class="header-user">
          <el-dropdown @command="handleCommand">
            <span class="el-dropdown-link">
              {{ userName }} <el-icon class="el-icon--right"><arrow-down /></el-icon>
            </span>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item command="settings">系统设置</el-dropdown-item>
                <el-dropdown-item command="logout">退出登录</el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>
      </header>

      <!-- 内容区 -->
      <main class="app-main">
        <router-view></router-view>
      </main>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, computed, onUnmounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { useUserStore } from './store'
import { ElMessage, ElMessageBox } from 'element-plus'
import { ArrowDown } from '@element-plus/icons-vue'
import apiClient from './api/client'

// 初始化
const router = useRouter()
const route = useRoute()
const userStore = useUserStore()
const isLoggedIn = ref(false)
const userRole = ref('1') // 默认学生角色
const isCollapsed = ref(false) // 侧边栏折叠状态

// 统一角色判断（转为数字后判断，避免类型问题）
const roleNum = computed(() => Number(userRole.value) || 1)
const isStudent = computed(() => roleNum.value === 1)
const isTeacher = computed(() => roleNum.value === 2)
const isAdmin = computed(() => roleNum.value === 3)
const isCounselor = computed(() => roleNum.value === 4)

// 验证区域判定（仅登录/注册页）
const isPureAuthPage = computed(() => {
  const path = route.path
  const authPaths = ['/login', '/register', '/teacher-register', '/counselor-register', '/admin-register']
  return authPaths.includes(path)
})

// 用户信息
const userName = computed(() => localStorage.getItem('username') || '未知用户')
const roleLabel = computed(() => {
  const labels = { 1: '学生', 2: '教师', 3: '管理员', 4: '辅导员' }
  return labels[roleNum.value] || '学生'
})

// 验证Token有效性（简化）
const validateToken = async () => {
  const token = localStorage.getItem('token')
  if (!token) {
    isLoggedIn.value = false
    return false
  }
  // 在有token的情况下，仅根据 localStorage 中的role信息设置使用户状态
  isLoggedIn.value = true
  userRole.value = localStorage.getItem('role') || '1'
  return true
}

// 退出登录（增加确认弹窗）
const handleLogout = async () => {
  try {
    await ElMessageBox.confirm('确定要退出登录吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    // 调用后端退出接口（可选）
    try {
      await apiClient.post('/auth/logout', {})
    } catch (e) {
      // 无论后端是否响应，都下面清理本地存储
    }
  } catch (e) {
    ElMessage.info('已取消退出')
    return
  }
  // 清除本地存储
  localStorage.removeItem('token')
  localStorage.removeItem('user')
  localStorage.removeItem('userId')
  localStorage.removeItem('username')
  localStorage.removeItem('role')
  userStore.logout()
  isLoggedIn.value = false
  router.push('/login')
  ElMessage.success('已退出登录')
}

// 管理员下拉菜单处理
const handleCommand = (command) => {
  if (command === 'logout') {
    handleLogout()
  } else if (command === 'settings') {
    router.push('/settings')
  }
}
const routeWatcher = router.afterEach(async (to) => {
  if (isPureAuthPage.value) return
  await validateToken()
})

onMounted(async () => {
  // 初始化验证Token
  await validateToken()
  console.log('用户role:', userRole.value, 'type:', typeof userRole.value)
})

onUnmounted(() => {
  // 移除路由监听
  routeWatcher()
})
</script>

<style scoped>
* {
  margin: 0;
  padding: 0;
  box-sizing: border-box;
}

.app-wrapper {
  display: flex;
  height: 100vh;
  background: #f5f7fa;
  transition: all 0.3s ease;
}

/* 侧边栏 */
.sidebar {
  width: 200px;
  background: linear-gradient(180deg, #f8f9fc 0%, #f0f3f8 100%);
  box-shadow: 2px 0 16px rgba(0, 0, 0, 0.08);
  display: flex;
  flex-direction: column;
  position: fixed;
  left: 0;
  top: 0;
  bottom: 0;
  z-index: 1000;
  overflow-y: auto;
  transition: width 0.3s ease;
}

.sidebar-collapsed {
  width: 60px;
}

.sidebar-collapsed .nav-item span {
  display: none;
}

.sidebar-collapsed .sidebar-header h2 {
  justify-content: center;
}

.sidebar-header {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  padding: 24px 16px;
  text-align: center;
  border-bottom: 1px solid rgba(255, 255, 255, 0.15);
  box-shadow: 0 4px 12px rgba(102, 126, 234, 0.25);
  flex-shrink: 0;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.collapse-btn {
  background: transparent;
  border: none;
  color: white;
  cursor: pointer;
  font-size: 14px;
  width: 24px;
  height: 24px;
  border-radius: 4px;
  transition: background 0.2s;
}

.collapse-btn:hover {
  background: rgba(255, 255, 255, 0.2);
}

.sidebar-header h2 {
  font-size: 16px;
  font-weight: 600;
  letter-spacing: 0.8px;
  display: flex;
  align-items: center;
  gap: 8px;
}

.sidebar-nav {
  flex: 1;
  display: flex;
  flex-direction: column;
  padding: 16px 8px;
  gap: 6px;
}

.nav-item {
  display: flex;
  align-items: center;
  justify-content: flex-start;
  padding: 12px 16px;
  margin: 0 4px;
  border-radius: 8px;
  color: #404854;
  font-size: 14px;
  font-weight: 500;
  text-decoration: none;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  position: relative;
  cursor: pointer;
  border: none;
  background: transparent;
}

.nav-item::before {
  content: '';
  position: absolute;
  left: 0;
  top: 50%;
  transform: translateY(-50%);
  width: 3px;
  height: 0;
  background: linear-gradient(180deg, #409eff, #66b1ff);
  border-radius: 0 2px 2px 0;
  transition: height 0.3s ease;
}

.nav-item:hover {
  background: linear-gradient(90deg, #e6f4ff 0%, #f0f7ff 100%);
  color: #409eff;
  transform: translateX(4px);
}

.nav-item:hover::before {
  height: 24px;
}

.nav-item.active {
  background: linear-gradient(90deg, #409eff 0%, #66b1ff 100%);
  color: white;
  box-shadow: 0 4px 12px rgba(64, 158, 255, 0.3);
  font-weight: 600;
}

.nav-item.active::before {
  height: 32px;
  width: 4px;
}

.nav-divider {
  height: 1px;
  background: linear-gradient(90deg, transparent 0%, rgba(102, 126, 234, 0.2) 50%, transparent 100%);
  margin: 12px 0;
}

.logout-btn {
  margin-top: auto;
}

/* 主内容区 */
.main-wrapper {
  flex: 1;
  margin-left: 200px;
  display: flex;
  flex-direction: column;
  overflow-y: auto;
  overflow-x: hidden;
  transition: margin-left 0.3s ease;
}

.main-wrapper-collapsed {
  margin-left: 60px;
}

/* 头部 */
.app-header {
  background: linear-gradient(90deg, #667eea 0%, #764ba2 50%, #667eea 100%);
  color: white;
  padding: 0 28px;
  height: 64px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  box-shadow: 0 4px 20px rgba(102, 126, 234, 0.3);
  flex-shrink: 0;
  position: relative;
  overflow: hidden;
}

.app-header::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  height: 1px;
  background: linear-gradient(90deg, transparent, rgba(255, 255, 255, 0.5), transparent);
}

.app-header::after {
  content: '';
  position: absolute;
  bottom: 0;
  left: 0;
  right: 0;
  height: 1px;
  background: linear-gradient(90deg, transparent, rgba(0, 0, 0, 0.1), transparent);
}

.header-content {
  font-size: 18px;
  font-weight: bold;
  letter-spacing: 0.5px;
}

.header-user {
  font-size: 14px;
  opacity: 0.9;
}

.app-main {
  flex: 1;
  padding: 16px;
  overflow-y: auto;
  overflow-x: hidden;
}

.full-wrapper {
  flex: 1;
  display: flex;
  flex-direction: column;
  overflow: hidden;
  margin-left: 0;
}

.full-wrapper .app-main {
  flex: 1;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  padding: 0;
}
</style>
