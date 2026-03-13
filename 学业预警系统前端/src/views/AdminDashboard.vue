<template>
  <div class="dashboard-wrapper">
    <!-- 顶部欢迎卡片 -->
    <div class="welcome-section">
      <div class="welcome-card">
        <div class="welcome-content">
          <h1>管理员仪表盘</h1>
          <p>全校数据统计、预警分析和系统监控中心</p>
          <div class="welcome-stats">
            <div class="stat-item">
              <span class="stat-value">{{ stats.studentCount || 0 }}</span>
              <span class="stat-label">在籍学生</span>
            </div>
            <div class="stat-item">
              <span class="stat-value">{{ stats.warningCount || 0 }}</span>
              <span class="stat-label">预警总数</span>
            </div>
            <div class="stat-item">
              <span class="stat-value">{{ stats.userCount || 0 }}</span>
              <span class="stat-label">系统用户</span>
            </div>
          </div>
        </div>
        <div class="welcome-time">
          <div class="time">{{ currentTime }}</div>
          <div class="date">{{ currentDate }}</div>
        </div>
      </div>
    </div>

    <!-- 核心指标卡片 -->
    <div class="metrics-section">
      <div class="metric-card student">
        <div class="metric-icon">
          <i class="el-icon-user"></i>
        </div>
        <div class="metric-content">
          <div class="metric-value">{{ stats.studentCount || 0 }}</div>
          <div class="metric-title">全校学生</div>
          <div class="metric-change positive">+2.5%</div>
        </div>
      </div>
      <div class="metric-card course">
        <div class="metric-icon">
          <i class="el-icon-notebook-2"></i>
        </div>
        <div class="metric-content">
          <div class="metric-value">{{ stats.courseCount || 0 }}</div>
          <div class="metric-title">开设课程</div>
          <div class="metric-change positive">+1.8%</div>
        </div>
      </div>
      <div class="metric-card warning">
        <div class="metric-icon">
          <i class="el-icon-warning"></i>
        </div>
        <div class="metric-content">
          <div class="metric-value">{{ stats.redWarnings || 0 }}</div>
          <div class="metric-title">红色预警</div>
          <div class="metric-change negative">+5.2%</div>
        </div>
      </div>
      <div class="metric-card user">
        <div class="metric-icon">
          <i class="el-icon-connection"></i>
        </div>
        <div class="metric-content">
          <div class="metric-value">{{ stats.userCount || 0 }}</div>
          <div class="metric-title">系统用户</div>
          <div class="metric-change positive">+3.1%</div>
        </div>
      </div>
    </div>

    <!-- 数据可视化区域 -->
    <div class="visualization-section">
      <!-- 预警分布 -->
      <div class="chart-card">
        <div class="chart-header">
          <h3>预警等级分布</h3>
          <div class="chart-actions">
            <el-select v-model="warningTimeRange" size="small" @change="loadDashboard">
              <el-option label="本学期" value="current"></el-option>
              <el-option label="上学期" value="last"></el-option>
              <el-option label="全年" value="year"></el-option>
            </el-select>
          </div>
        </div>
        <div class="chart-content">
          <div class="warning-distribution">
            <div class="warning-item">
              <span class="warning-label">红色预警</span>
              <div class="warning-bar red" :style="{ width: getWarningPercentage('red') }"></div>
              <span class="warning-count">{{ stats.redWarnings || 0 }}</span>
            </div>
            <div class="warning-item">
              <span class="warning-label">黄色预警</span>
              <div class="warning-bar yellow" :style="{ width: getWarningPercentage('yellow') }"></div>
              <span class="warning-count">{{ stats.yellowWarnings || 0 }}</span>
            </div>
            <div class="warning-item">
              <span class="warning-label">蓝色预警</span>
              <div class="warning-bar blue" :style="{ width: getWarningPercentage('blue') }"></div>
              <span class="warning-count">{{ stats.blueWarnings || 0 }}</span>
            </div>
          </div>
        </div>
      </div>

      <!-- 今日数据 -->
      <div class="chart-card">
        <div class="chart-header">
          <h3>今日数据</h3>
        </div>
        <div class="today-stats">
          <div class="today-item">
            <div class="today-icon new"><i class="el-icon-circle-plus"></i></div>
            <div class="today-content">
              <div class="today-value">{{ todayStats.newWarnings || 0 }}</div>
              <div class="today-label">新增预警</div>
            </div>
          </div>
          <div class="today-item">
            <div class="today-icon processed"><i class="el-icon-check"></i></div>
            <div class="today-content">
              <div class="today-value">{{ todayStats.processedWarnings || 0 }}</div>
              <div class="today-label">处理预警</div>
            </div>
          </div>
          <div class="today-item">
            <div class="today-icon online"><i class="el-icon-video-camera"></i></div>
            <div class="today-content">
              <div class="today-value">{{ todayStats.onlineTeachers || 0 }}</div>
              <div class="today-label">在线教师</div>
            </div>
          </div>
          <div class="today-item">
            <div class="today-icon student"><i class="el-icon-user"></i></div>
            <div class="today-content">
              <div class="today-value">{{ todayStats.onlineStudents || 0 }}</div>
              <div class="today-label">在线学生</div>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 系统状态监控 -->
    <div class="system-status-section">
      <h2>系统状态监控</h2>
      <div class="status-grid">
        <div class="status-card">
          <div class="status-icon online"><i class="el-icon-check"></i></div>
          <div class="status-content">
            <div class="status-title">数据库连接</div>
            <div class="status-description">正常运行</div>
          </div>
        </div>
        <div class="status-card">
          <div class="status-icon online"><i class="el-icon-check"></i></div>
          <div class="status-content">
            <div class="status-title">API服务</div>
            <div class="status-description">响应正常</div>
          </div>
        </div>
        <div class="status-card">
          <div class="status-icon online"><i class="el-icon-check"></i></div>
          <div class="status-content">
            <div class="status-title">缓存服务</div>
            <div class="status-description">运行良好</div>
          </div>
        </div>
        <div class="status-card">
          <div class="status-icon online"><i class="el-icon-check"></i></div>
          <div class="status-content">
            <div class="status-title">系统负载</div>
            <div class="status-description">正常</div>
          </div>
        </div>
      </div>
    </div>

    <!-- 快速操作 -->
    <div class="actions-section">
      <h2>快速操作</h2>
      <div class="action-grid">
        <router-link to="/admin/colleges" class="action-card">
          <div class="action-icon"><i class="el-icon-office-building"></i></div>
          <div class="action-name">学院管理</div>
        </router-link>
        <router-link to="/admin/majors" class="action-card">
          <div class="action-icon"><i class="el-icon-suitcase"></i></div>
          <div class="action-name">专业管理</div>
        </router-link>
        <router-link to="/admin/users" class="action-card">
          <div class="action-icon"><i class="el-icon-user"></i></div>
          <div class="action-name">用户管理</div>
        </router-link>
        <router-link to="/admin/courses" class="action-card">
          <div class="action-icon"><i class="el-icon-notebook-2"></i></div>
          <div class="action-name">课程管理</div>
        </router-link>
        <router-link to="/admin/rules" class="action-card">
          <div class="action-icon"><i class="el-icon-s-grid"></i></div>
          <div class="action-name">规则管理</div>
        </router-link>
        <router-link to="/admin/statistics" class="action-card">
          <div class="action-icon"><i class="el-icon-data-line"></i></div>
          <div class="action-name">数据分析</div>
        </router-link>
        <router-link to="/admin/class-management/pending-requests" class="action-card">
          <div class="action-icon"><i class="el-icon-document"></i></div>
          <div class="action-name">班级管理申请</div>
        </router-link>
        <router-link to="/admin/messages/broadcast" class="action-card">
          <div class="action-icon"><i class="el-icon-message"></i></div>
          <div class="action-name">发送通知</div>
        </router-link>
      </div>
    </div>

    <!-- 最近动态 -->
    <div class="activity-section">
      <div class="section-header">
        <h2>最近动态</h2>
        <el-button type="primary" size="small" @click="refreshActivities">
          <i class="el-icon-refresh"></i> 刷新
        </el-button>
      </div>
      <div class="activity-list">
        <div v-for="activity in activities" :key="activity.id" class="activity-item">
          <div class="activity-icon">
            <i :class="getActivityIcon(activity.type)"></i>
          </div>
          <div class="activity-content">
            <div class="activity-time">{{ formatTime(activity.createdAt) }}</div>
            <div class="activity-text">
              <span :class="['activity-type', activity.type]">{{ getTypeLabel(activity.type) }}</span>
              <span>{{ activity.content }}</span>
            </div>
          </div>
        </div>
        <div v-if="activities.length === 0" class="activity-item empty">
          <div class="activity-icon"><i class="el-icon-info"></i></div>
          <div class="activity-content">
            <div class="activity-time">--</div>
            <div class="activity-text">
              <span class="activity-type notify">提示</span>
              <span>暂无最近动态</span>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted, computed } from 'vue'
import { adminAPI } from '@/api/index'

const stats = ref({
  studentCount: 0,
  collegeCount: 0,
  majorCount: 0,
  courseCount: 0,
  redWarnings: 0,
  yellowWarnings: 0,
  blueWarnings: 0,
  userCount: 0,
  warningCount: 0
})
const todayStats = ref({
  newWarnings: 5,
  processedWarnings: 3,
  onlineTeachers: 12,
  onlineStudents: 89
})
const currentTime = ref('')
const currentDate = ref('')
const activities = ref([])
const warningTimeRange = ref('current')

let timer = null

onMounted(async () => {
  updateTime()
  timer = setInterval(updateTime, 1000)
  await loadDashboard()
})

onUnmounted(() => {
  if (timer) clearInterval(timer)
})

const updateTime = () => {
  const now = new Date()
  currentTime.value = now.toLocaleTimeString('zh-CN', {
    hour: '2-digit',
    minute: '2-digit',
    second: '2-digit'
  })
  currentDate.value = now.toLocaleDateString('zh-CN', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit',
    weekday: 'long'
  })
}

const loadDashboard = async () => {
  try {
    const response = await adminAPI.getStatistics()
    if (response && typeof response === 'object') {
      stats.value = {
        studentCount: response.totalStudents || 0,
        courseCount: response.totalCourses || 0,
        userCount: response.totalTeachers || 0,
        redWarnings: response.redWarnings || 0,
        yellowWarnings: response.yellowWarnings || 0,
        blueWarnings: response.lowWarnings || 0,
        warningCount: response.totalWarnings || 0,
        collegeCount: response.totalColleges || 0,
        majorCount: 0
      }
    }
    
    // 获取最近动态
    await loadActivities()
  } catch (error) {
    console.error('加载管理员看板失败:', error)
  }
}

const loadActivities = async () => {
  try {
    const activitiesResponse = await adminAPI.getActivities()
    if (activitiesResponse && Array.isArray(activitiesResponse)) {
      activities.value = activitiesResponse
    }
  } catch (error) {
    console.error('加载最近动态失败:', error)
  }
}

const refreshActivities = async () => {
  await loadActivities()
}

const formatTime = (timeString) => {
  if (!timeString) return '--'
  const now = new Date()
  const activityTime = new Date(timeString)
  const diffMs = now - activityTime
  const diffMinutes = Math.floor(diffMs / (1000 * 60))
  
  if (diffMinutes < 1) {
    return '刚刚'
  } else if (diffMinutes < 60) {
    return `${diffMinutes}分钟前`
  } else if (diffMinutes < 1440) {
    return `${Math.floor(diffMinutes / 60)}小时前`
  } else {
    return activityTime.toLocaleDateString('zh-CN')
  }
}

const getTypeLabel = (type) => {
  const typeMap = {
    'warning': '预警',
    'plan_update': '计划更新',
    'system_message': '通知',
    'score_update': '成绩修改',
    'user_register': '用户注册'
  }
  return typeMap[type] || '通知'
}

const getActivityIcon = (type) => {
  const iconMap = {
    'warning': 'el-icon-warning',
    'plan_update': 'el-icon-document',
    'system_message': 'el-icon-message',
    'score_update': 'el-icon-edit',
    'user_register': 'el-icon-user'
  }
  return iconMap[type] || 'el-icon-info'
}

const getWarningPercentage = (level) => {
  const total = (stats.value.redWarnings || 0) + (stats.value.yellowWarnings || 0) + (stats.value.blueWarnings || 0)
  if (total === 0) return '0%'
  
  switch (level) {
    case 'red':
      return `${Math.round((stats.value.redWarnings || 0) / total * 100)}%`
    case 'yellow':
      return `${Math.round((stats.value.yellowWarnings || 0) / total * 100)}%`
    case 'blue':
      return `${Math.round((stats.value.blueWarnings || 0) / total * 100)}%`
    default:
      return '0%'
  }
}
</script>

<style scoped>
.dashboard-wrapper {
  background: linear-gradient(135deg, #f5f7fb 0%, #e8ecf1 100%);
  padding: 24px;
  min-height: 100vh;
  font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, 'Helvetica Neue', Arial, sans-serif;
}

/* 欢迎区域 */
.welcome-section {
  margin-bottom: 32px;
}

.welcome-card {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border-radius: 16px;
  padding: 32px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  color: white;
  box-shadow: 0 12px 40px rgba(102, 126, 234, 0.3);
  position: relative;
  overflow: hidden;
}

.welcome-card::before {
  content: '';
  position: absolute;
  width: 400px;
  height: 400px;
  background: radial-gradient(circle, rgba(255, 255, 255, 0.1) 0%, transparent 70%);
  border-radius: 50%;
  top: -100px;
  right: -100px;
  animation: float 6s ease-in-out infinite;
}

.welcome-content {
  z-index: 1;
  flex: 1;
}

.welcome-content h1 {
  margin: 0 0 8px 0;
  font-size: 28px;
  font-weight: 600;
  letter-spacing: 0.5px;
}

.welcome-content p {
  margin: 0 0 24px 0;
  font-size: 14px;
  opacity: 0.9;
}

.welcome-stats {
  display: flex;
  gap: 32px;
}

.stat-item {
  display: flex;
  flex-direction: column;
  align-items: flex-start;
}

.stat-value {
  font-size: 24px;
  font-weight: 700;
  margin-bottom: 4px;
}

.stat-label {
  font-size: 12px;
  opacity: 0.8;
}

.welcome-time {
  z-index: 1;
  text-align: right;
  margin-left: 40px;
}

.welcome-time .time {
  font-size: 24px;
  font-weight: 600;
  margin-bottom: 4px;
}

.welcome-time .date {
  font-size: 14px;
  opacity: 0.8;
}

@keyframes float {
  0%, 100% { transform: translateY(0px); }
  50% { transform: translateY(-20px); }
}

/* 核心指标 */
.metrics-section {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(240px, 1fr));
  gap: 20px;
  margin-bottom: 32px;
}

.metric-card {
  background: white;
  border-radius: 12px;
  padding: 24px;
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.08);
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  border: 1px solid #e9ecef;
  display: flex;
  align-items: center;
  gap: 20px;
  position: relative;
  overflow: hidden;
}

.metric-card::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  height: 4px;
  background: linear-gradient(90deg, #667eea, #764ba2);
}

.metric-card.student::before {
  background: linear-gradient(90deg, #667eea, #764ba2);
}

.metric-card.course::before {
  background: linear-gradient(90deg, #667eea, #764ba2);
}

.metric-card.warning::before {
  background: linear-gradient(90deg, #f56c6c, #f5a623);
}

.metric-card.user::before {
  background: linear-gradient(90deg, #67c23a, #409eff);
}

.metric-card:hover {
  box-shadow: 0 12px 32px rgba(102, 126, 234, 0.15);
  transform: translateY(-4px);
}

.metric-icon {
  width: 48px;
  height: 48px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 24px;
  color: white;
  background: linear-gradient(135deg, #667eea, #764ba2);
}

.metric-card.warning .metric-icon {
  background: linear-gradient(135deg, #f56c6c, #f5a623);
}

.metric-card.user .metric-icon {
  background: linear-gradient(135deg, #67c23a, #409eff);
}

.metric-content {
  flex: 1;
}

.metric-value {
  font-size: 32px;
  font-weight: 800;
  color: #333;
  margin-bottom: 4px;
}

.metric-title {
  font-size: 14px;
  color: #666;
  margin-bottom: 8px;
}

.metric-change {
  font-size: 12px;
  font-weight: 600;
}

.metric-change.positive {
  color: #67c23a;
}

.metric-change.negative {
  color: #f56c6c;
}

/* 可视化区域 */
.visualization-section {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(400px, 1fr));
  gap: 24px;
  margin-bottom: 32px;
}

.chart-card {
  background: white;
  border-radius: 12px;
  padding: 24px;
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.08);
  border: 1px solid #e9ecef;
}

.chart-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.chart-header h3 {
  margin: 0;
  font-size: 16px;
  font-weight: 600;
  color: #333;
}

.chart-actions {
  display: flex;
  gap: 8px;
}

.chart-content {
  margin-top: 20px;
}

/* 预警分布 */
.warning-distribution {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.warning-item {
  display: flex;
  align-items: center;
  gap: 12px;
}

.warning-label {
  font-size: 14px;
  color: #666;
  min-width: 90px;
}

.warning-bar {
  height: 10px;
  border-radius: 5px;
  flex: 1;
  transition: all 0.3s ease;
}

.warning-bar.red {
  background: linear-gradient(90deg, #f56c6c, #f5a623);
}

.warning-bar.yellow {
  background: linear-gradient(90deg, #e6a23c, #f56c6c);
}

.warning-bar.blue {
  background: linear-gradient(90deg, #409eff, #67c23a);
}

.warning-item:hover .warning-bar {
  height: 12px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.15);
}

.warning-count {
  font-size: 14px;
  font-weight: 600;
  color: #333;
  min-width: 40px;
  text-align: right;
}

/* 今日数据 */
.today-stats {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 16px;
}

.today-item {
  background: #f8f9fa;
  border-radius: 8px;
  padding: 16px;
  display: flex;
  align-items: center;
  gap: 12px;
  transition: all 0.3s ease;
}

.today-item:hover {
  background: #e9ecef;
  transform: translateY(-2px);
}

.today-icon {
  width: 40px;
  height: 40px;
  border-radius: 8px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 20px;
  color: white;
}

.today-icon.new {
  background: linear-gradient(135deg, #667eea, #764ba2);
}

.today-icon.processed {
  background: linear-gradient(135deg, #67c23a, #409eff);
}

.today-icon.online {
  background: linear-gradient(135deg, #409eff, #667eea);
}

.today-icon.student {
  background: linear-gradient(135deg, #e6a23c, #f56c6c);
}

.today-content {
  flex: 1;
}

.today-value {
  font-size: 20px;
  font-weight: 700;
  color: #333;
  margin-bottom: 4px;
}

.today-label {
  font-size: 12px;
  color: #666;
}

/* 系统状态监控 */
.system-status-section {
  margin-bottom: 32px;
}

.system-status-section h2 {
  margin: 0 0 20px 0;
  font-size: 18px;
  font-weight: 600;
  color: #333;
}

.status-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
  gap: 16px;
}

.status-card {
  background: white;
  border-radius: 12px;
  padding: 20px;
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.08);
  border: 1px solid #e9ecef;
  display: flex;
  align-items: center;
  gap: 16px;
  transition: all 0.3s ease;
}

.status-card:hover {
  box-shadow: 0 8px 24px rgba(102, 126, 234, 0.12);
  transform: translateY(-2px);
}

.status-icon {
  width: 48px;
  height: 48px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 24px;
  color: white;
}

.status-icon.online {
  background: linear-gradient(135deg, #67c23a, #409eff);
}

.status-icon.offline {
  background: linear-gradient(135deg, #f56c6c, #e6a23c);
}

.status-content {
  flex: 1;
}

.status-title {
  font-size: 14px;
  font-weight: 600;
  color: #333;
  margin-bottom: 4px;
}

.status-description {
  font-size: 12px;
  color: #666;
}

/* 快速操作区 */
.actions-section {
  margin-bottom: 32px;
}

.actions-section h2 {
  margin: 0 0 20px 0;
  font-size: 18px;
  font-weight: 600;
  color: #333;
}

.action-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(120px, 1fr));
  gap: 16px;
}

.action-card {
  background: white;
  border-radius: 12px;
  padding: 24px 16px;
  text-align: center;
  text-decoration: none;
  color: #333;
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.08);
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  gap: 12px;
  border: 1px solid #e9ecef;
  position: relative;
  overflow: hidden;
}

.action-card::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: linear-gradient(135deg, rgba(102, 126, 234, 0.05) 0%, transparent 100%);
  opacity: 0;
  transition: opacity 0.3s ease;
}

.action-card:hover::before {
  opacity: 1;
}

.action-card:hover {
  box-shadow: 0 12px 32px rgba(102, 126, 234, 0.2);
  transform: translateY(-4px);
  border-color: #667eea;
  color: #667eea;
}

.action-icon {
  font-size: 32px;
  transition: all 0.3s cubic-bezier(0.34, 1.56, 0.64, 1);
  position: relative;
  z-index: 1;
}

.action-card:hover .action-icon {
  transform: scale(1.2) rotateY(360deg);
}

.action-name {
  font-size: 13px;
  font-weight: 600;
  position: relative;
  z-index: 1;
}

/* 最近动态区 */
.activity-section {
  margin-bottom: 24px;
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.section-header h2 {
  margin: 0;
  font-size: 18px;
  font-weight: 600;
  color: #333;
}

.activity-list {
  background: white;
  border-radius: 12px;
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.08);
  overflow: hidden;
  border: 1px solid #e9ecef;
}

.activity-item {
  padding: 20px 24px;
  border-bottom: 1px solid #e9ecef;
  display: flex;
  gap: 16px;
  align-items: flex-start;
  transition: background 0.3s ease;
}

.activity-item:last-child {
  border-bottom: none;
}

.activity-item:hover {
  background: #f8f9fa;
}

.activity-item.empty {
  justify-content: center;
}

.activity-icon {
  width: 40px;
  height: 40px;
  border-radius: 8px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 16px;
  color: white;
  background: linear-gradient(135deg, #667eea, #764ba2);
  flex-shrink: 0;
  margin-top: 2px;
}

.activity-content {
  flex: 1;
}

.activity-time {
  font-size: 12px;
  color: #999;
  margin-bottom: 8px;
}

.activity-text {
  font-size: 14px;
  color: #666;
  display: flex;
  gap: 8px;
  align-items: center;
}

.activity-type {
  display: inline-block;
  padding: 2px 8px;
  border-radius: 4px;
  font-size: 11px;
  font-weight: 600;
  white-space: nowrap;
  background: #f0f2f8;
  color: #667eea;
}

.activity-type.warning {
  background: #fef0f0;
  color: #f56c6c;
}

.activity-type.plan_update {
  background: #f0f9eb;
  color: #67c23a;
}

.activity-type.score_update {
  background: #ecf5ff;
  color: #409eff;
}

.activity-type.user_register {
  background: #fdf6ec;
  color: #e6a23c;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .dashboard-wrapper {
    padding: 16px;
  }
  
  .welcome-card {
    flex-direction: column;
    text-align: center;
    gap: 24px;
  }
  
  .welcome-time {
    margin-left: 0;
  }
  
  .welcome-stats {
    justify-content: center;
  }
  
  .metrics-section {
    grid-template-columns: 1fr;
  }
  
  .visualization-section {
    grid-template-columns: 1fr;
  }
  
  .today-stats {
    grid-template-columns: 1fr;
  }
  
  .status-grid {
    grid-template-columns: 1fr;
  }
  
  .action-grid {
    grid-template-columns: repeat(2, 1fr);
  }
}
</style>