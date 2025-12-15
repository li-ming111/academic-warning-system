<template>
  <div class="dashboard-wrapper">
    <!-- 欢迎卡片 -->
    <div class="welcome-card">
      <div class="welcome-content">
        <div class="greeting-icon">🎯</div>
        <h1>管理员仪表盘</h1>
        <p>全校数据统计、预警分析和系统监控中心</p>
      </div>
      <div class="welcome-time">{{ currentTime }}</div>
    </div>

    <!-- 核心指标卡片 -->
    <div class="metrics-section">
      <div class="metric-card blue">
        <div class="metric-header">
          <span class="metric-icon">👥</span>
          <span class="metric-title">全校学生</span>
        </div>
        <div class="metric-value">{{ stats.studentCount || '-' }}</div>
        <div class="metric-detail">在籍学生总数</div>
      </div>
      <div class="metric-card green">
        <div class="metric-header">
          <span class="metric-icon">📚</span>
          <span class="metric-title">开设课程</span>
        </div>
        <div class="metric-value">{{ stats.courseCount || '-' }}</div>
        <div class="metric-detail">本学期课程数</div>
      </div>
      <div class="metric-card red">
        <div class="metric-header">
          <span class="metric-icon">🔴</span>
          <span class="metric-title">红色预警</span>
        </div>
        <div class="metric-value">{{ stats.redWarnings || 0 }}</div>
        <div class="metric-detail">需立即干预学生</div>
      </div>
      <div class="metric-card purple">
        <div class="metric-header">
          <span class="metric-icon">✅</span>
          <span class="metric-title">系统用户</span>
        </div>
        <div class="metric-value">{{ stats.userCount || '-' }}</div>
        <div class="metric-detail">注册用户总数</div>
      </div>
    </div>

    <!-- 预警分布统计 -->
    <div class="charts-section">
      <div class="chart-card">
        <h3>预警等级分布</h3>
        <div class="warning-distribution">
          <div class="warning-item">
            <span class="warning-label">🔴 红色预警</span>
            <div class="warning-bar" style="background: #f56c6c; width: 45%"></div>
            <span class="warning-count">{{ stats.redWarnings || 0 }}</span>
          </div>
          <div class="warning-item">
            <span class="warning-label">🟡 黄色预警</span>
            <div class="warning-bar" style="background: #e6a23c; width: 30%"></div>
            <span class="warning-count">{{ stats.yellowWarnings || 0 }}</span>
          </div>
          <div class="warning-item">
            <span class="warning-label">🔵 蓝色预警</span>
            <div class="warning-bar" style="background: #409eff; width: 25%"></div>
            <span class="warning-count">{{ stats.blueWarnings || 0 }}</span>
          </div>
        </div>
      </div>

      <div class="chart-card">
        <h3>实时在线统计</h3>
        <div class="online-stats">
          <div class="online-item">
            <div class="online-icon">👨‍🏫</div>
            <div class="online-info">
              <div class="online-label">在线教师</div>
              <div class="online-number">{{ todayStats.onlineTeachers || 32 }}</div>
            </div>
          </div>
          <div class="online-item">
            <div class="online-icon">👤</div>
            <div class="online-info">
              <div class="online-label">在线学生</div>
              <div class="online-number">{{ todayStats.onlineStudents || 180 }}</div>
            </div>
          </div>
          <div class="online-item">
            <div class="online-icon">🌐</div>
            <div class="online-info">
              <div class="online-label">系统在线</div>
              <div class="online-number">{{ (todayStats.onlineTeachers || 0) + (todayStats.onlineStudents || 0) }}</div>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 今日数据 -->
    <div class="today-section">
      <h2>⚡ 今日数据概览</h2>
      <div class="today-cards">
        <div class="today-card new">
          <div class="today-icon">🆕</div>
          <div class="today-content">
            <div class="today-label">新增预警</div>
            <div class="today-number">{{ todayStats.newWarnings || 5 }}</div>
          </div>
        </div>
        <div class="today-card processed">
          <div class="today-icon">✅</div>
          <div class="today-content">
            <div class="today-label">已处理</div>
            <div class="today-number">{{ todayStats.processedWarnings || 3 }}</div>
          </div>
        </div>
        <div class="today-card pending">
          <div class="today-icon">⏳</div>
          <div class="today-content">
            <div class="today-label">待处理</div>
            <div class="today-number">{{ (todayStats.newWarnings || 5) - (todayStats.processedWarnings || 3) }}</div>
          </div>
        </div>
      </div>
    </div>

    <!-- 快速操作 -->
    <div class="actions-section">
      <h2>⚙️ 快速操作</h2>
      <div class="action-grid">
        <router-link to="/admin/colleges" class="action-card">
          <div class="action-icon">🏫</div>
          <div class="action-name">学院管理</div>
        </router-link>
        <router-link to="/admin/majors" class="action-card">
          <div class="action-icon">📚</div>
          <div class="action-name">专业管理</div>
        </router-link>
        <router-link to="/admin/users" class="action-card">
          <div class="action-icon">👥</div>
          <div class="action-name">用户管理</div>
        </router-link>
        <router-link to="/admin/courses" class="action-card">
          <div class="action-icon">📖</div>
          <div class="action-name">课程管理</div>
        </router-link>
        <router-link to="/admin/rules" class="action-card">
          <div class="action-icon">⚙️</div>
          <div class="action-name">规则管理</div>
        </router-link>
        <router-link to="/admin/statistics" class="action-card">
          <div class="action-icon">📊</div>
          <div class="action-name">数据分析</div>
        </router-link>
      </div>
    </div>

    <!-- 系统动态 -->
    <div class="activity-section">
      <h2>📢 最近动态</h2>
      <div class="activity-list">
        <div class="activity-item">
          <div class="activity-time">刚刚</div>
          <div class="activity-content">
            <span class="activity-type alert">预警</span>
            <span>学生张三新增红色预警</span>
          </div>
        </div>
        <div class="activity-item">
          <div class="activity-time">2分钟前</div>
          <div class="activity-content">
            <span class="activity-type modify">修改</span>
            <span>王老师修改了班级B的成绩</span>
          </div>
        </div>
        <div class="activity-item">
          <div class="activity-time">5分钟前</div>
          <div class="activity-content">
            <span class="activity-type notify">通知</span>
            <span>系统发送选修课未达标预警</span>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted } from 'vue'
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
  newWarnings: 0,
  processedWarnings: 0,
  onlineTeachers: 0,
  onlineStudents: 0
})
const currentTime = ref('')

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
  currentTime.value = now.toLocaleString('zh-CN', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit',
    hour: '2-digit',
    minute: '2-digit',
    second: '2-digit'
  })
}

const loadDashboard = async () => {
  try {
    const response = await adminAPI.getDashboard()
    if (response && response.code === 0) {
      stats.value = response.data || {}
    } else if (response && response.stats) {
      stats.value = response.stats
    }
  } catch (error) {
    console.error('加载管理员看板失败:', error)
  }
}
</script>

<style scoped>
.dashboard-wrapper {
  background: linear-gradient(135deg, #f0f2f5 0%, #e8ecf1 100%);
  padding: 30px;
  min-height: 100vh;
  font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, 'Helvetica Neue', Arial, sans-serif;
}

/* 欢迎卡片 */
.welcome-card {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border-radius: 16px;
  padding: 40px;
  margin-bottom: 40px;
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

.greeting-icon {
  font-size: 48px;
  margin-bottom: 16px;
  display: block;
}

.welcome-content h1 {
  margin: 0 0 10px 0;
  font-size: 32px;
  font-weight: 600;
  letter-spacing: 0.5px;
}

.welcome-content p {
  margin: 0;
  font-size: 14px;
  opacity: 0.9;
}

.welcome-time {
  font-size: 13px;
  opacity: 0.8;
  z-index: 1;
  margin-left: 30px;
  white-space: nowrap;
}

@keyframes float {
  0%, 100% { transform: translateY(0px); }
  50% { transform: translateY(-20px); }
}

/* 核心指标 */
.metrics-section {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
  gap: 20px;
  margin-bottom: 40px;
}

.metric-card {
  background: white;
  border-radius: 12px;
  padding: 24px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.08);
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  border-top: 4px solid #409eff;
  position: relative;
}

.metric-card.blue { border-top-color: #409eff; }
.metric-card.green { border-top-color: #67c23a; }
.metric-card.red { border-top-color: #f56c6c; }
.metric-card.purple { border-top-color: #9254de; }

.metric-card:hover {
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.12);
  transform: translateY(-4px);
}

.metric-header {
  display: flex;
  align-items: center;
  gap: 10px;
  margin-bottom: 16px;
}

.metric-icon {
  font-size: 24px;
}

.metric-title {
  font-size: 14px;
  color: #666;
  font-weight: 500;
}

.metric-value {
  font-size: 28px;
  font-weight: 700;
  color: #333;
  margin-bottom: 8px;
}

.metric-detail {
  font-size: 12px;
  color: #999;
}

/* 图表区 */
.charts-section {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(300px, 1fr));
  gap: 20px;
  margin-bottom: 40px;
}

.chart-card {
  background: white;
  border-radius: 12px;
  padding: 24px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.08);
}

.chart-card h3 {
  margin: 0 0 20px 0;
  font-size: 16px;
  font-weight: 600;
  color: #333;
}

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
  font-size: 13px;
  color: #666;
  min-width: 90px;
}

.warning-bar {
  height: 8px;
  border-radius: 4px;
  flex: 1;
  transition: all 0.3s ease;
}

.warning-item:hover .warning-bar {
  height: 10px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.15);
}

.warning-count {
  font-size: 14px;
  font-weight: 600;
  color: #333;
  min-width: 40px;
  text-align: right;
}

.online-stats {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(100px, 1fr));
  gap: 15px;
}

.online-item {
  background: linear-gradient(135deg, #f5f7fa 0%, #f0f3f8 100%);
  border-radius: 8px;
  padding: 16px;
  text-align: center;
  transition: all 0.3s ease;
}

.online-item:hover {
  background: linear-gradient(135deg, #e8ecf1 0%, #e0e6f1 100%);
  transform: translateY(-2px);
}

.online-icon {
  font-size: 28px;
  margin-bottom: 8px;
  display: block;
}

.online-label {
  font-size: 12px;
  color: #999;
  margin-bottom: 4px;
}

.online-number {
  font-size: 20px;
  font-weight: 700;
  color: #409eff;
}

/* 今日数据区 */
.today-section {
  margin-bottom: 40px;
}

.today-section h2 {
  margin: 0 0 20px 0;
  font-size: 18px;
  font-weight: 600;
  color: #333;
}

.today-cards {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(150px, 1fr));
  gap: 15px;
}

.today-card {
  background: white;
  border-radius: 12px;
  padding: 20px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.08);
  display: flex;
  align-items: center;
  gap: 15px;
  transition: all 0.3s ease;
  border-left: 4px solid #409eff;
}

.today-card.new { border-left-color: #67c23a; }
.today-card.processed { border-left-color: #409eff; }
.today-card.pending { border-left-color: #e6a23c; }

.today-card:hover {
  box-shadow: 0 6px 20px rgba(0, 0, 0, 0.12);
  transform: translateY(-3px);
}

.today-icon {
  font-size: 32px;
  min-width: 45px;
}

.today-content {
  flex: 1;
}

.today-label {
  font-size: 12px;
  color: #999;
  margin-bottom: 4px;
}

.today-number {
  font-size: 24px;
  font-weight: 700;
  color: #333;
}

/* 快速操作区 */
.actions-section {
  margin-bottom: 40px;
}

.actions-section h2 {
  margin: 0 0 20px 0;
  font-size: 18px;
  font-weight: 600;
  color: #333;
}

.action-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(100px, 1fr));
  gap: 15px;
}

.action-card {
  background: white;
  border-radius: 12px;
  padding: 20px;
  text-align: center;
  text-decoration: none;
  color: #333;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.08);
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 10px;
}

.action-card:hover {
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.12);
  transform: translateY(-6px);
  color: #409eff;
}

.action-icon {
  font-size: 32px;
  transition: transform 0.3s ease;
}

.action-card:hover .action-icon {
  transform: scale(1.2) rotate(5deg);
}

.action-name {
  font-size: 12px;
  font-weight: 500;
}

/* 最近动态区 */
.activity-section {
  margin-bottom: 20px;
}

.activity-section h2 {
  margin: 0 0 20px 0;
  font-size: 18px;
  font-weight: 600;
  color: #333;
}

.activity-list {
  background: white;
  border-radius: 12px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.08);
  overflow: hidden;
}

.activity-item {
  padding: 18px 24px;
  border-bottom: 1px solid #f0f0f0;
  display: flex;
  gap: 16px;
  align-items: flex-start;
  transition: background 0.3s ease;
}

.activity-item:last-child {
  border-bottom: none;
}

.activity-item:hover {
  background: #fafafa;
}

.activity-time {
  font-size: 12px;
  color: #999;
  min-width: 60px;
  margin-top: 2px;
}

.activity-content {
  flex: 1;
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
}

.activity-type.alert {
  background: #fef0f0;
  color: #f56c6c;
}

.activity-type.modify {
  background: #f0f9ff;
  color: #409eff;
}

.activity-type.notify {
  background: #fef7e6;
  color: #e6a23c;
}
</style>
