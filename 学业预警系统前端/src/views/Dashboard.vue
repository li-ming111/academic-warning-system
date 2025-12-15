<template>
  <div class="dashboard-wrapper">
    <!-- 顶部欢迎卡片 -->
    <div class="welcome-card">
      <div class="welcome-animation"></div>
      <div class="welcome-content">
        <div class="greeting-icon">👋</div>
        <h1>欢迎回来，{{ username }}！</h1>
        <p>{{ currentDate }} | 您的学业一切顺利</p>
      </div>
      <div class="user-avatar-wrapper">
        <div class="avatar-bg"></div>
        <el-avatar size="large" :src="avatarUrl" class="user-avatar"></el-avatar>
      </div>
    </div>

    <!-- 统计卡片 -->
    <div class="stats-section">
      <h2 class="section-title">📊 学业概览</h2>
      <div class="stats-grid">
        <div class="stat-card gpa-card" data-aos="fade-up">
          <div class="stat-icon gpa-icon">📚</div>
          <div class="stat-content">
            <div class="stat-label">累计GPA</div>
            <div class="stat-number">{{ academicData.gpa }}</div>
            <div class="stat-detail">📈 较上学期提升0.2</div>
          </div>
        </div>
        <div class="stat-card courses-card" data-aos="fade-up" data-aos-delay="100">
          <div class="stat-icon courses-icon">📖</div>
          <div class="stat-content">
            <div class="stat-label">本学期课程</div>
            <div class="stat-number">{{ academicData.semesterCourses }}</div>
            <div class="stat-detail">共{{ academicData.totalCourses }}门已修</div>
          </div>
        </div>
        <div class="stat-card warning-card" data-aos="fade-up" data-aos-delay="200">
          <div class="stat-icon warning-icon">⚠️</div>
          <div class="stat-content">
            <div class="stat-label">预警等级</div>
            <div class="stat-number warning-num">{{ academicData.warningCount }}</div>
            <div class="stat-detail">{{ academicData.warningLevel }}</div>
          </div>
        </div>
        <div class="stat-card attendance-card" data-aos="fade-up" data-aos-delay="300">
          <div class="stat-icon attendance-icon">✅</div>
          <div class="stat-content">
            <div class="stat-label">出勤率</div>
            <div class="stat-number">{{ academicData.attendanceRate }}%</div>
            <div class="stat-detail">🌟 优秀</div>
          </div>
        </div>
      </div>
    </div>

    <!-- 成绩趋势 -->
    <div class="chart-section">
      <h2 class="section-title">📈 成绩趋势分析</h2>
      <div class="chart-wrapper">
        <div class="chart-container">
          <div id="scoreChart" style="width: 100%; height: 350px;"></div>
        </div>
      </div>
    </div>

    <!-- 内容分布：预警和帮扶 -->
    <div class="content-grid">
      <!-- 预警信息 -->
      <div class="card-section warning-section">
        <div class="section-header">
          <h2 class="section-title">⚠️ 最新预警</h2>
          <router-link to="/warnings" class="view-all">查看全部 →</router-link>
        </div>
        <div class="warning-list">
          <div v-if="warnings.length === 0" class="empty-state">暂无预警信息</div>
          <div v-for="warning in warnings.slice(0, 2)" :key="warning.id" class="warning-item" :class="getLevelClass(warning.warningLevel)">
            <div class="warning-icon">{{ getLevelIcon(warning.warningLevel) }}</div>
            <div class="warning-content">
              <div class="warning-header">
                <el-tag :type="getTagType(warning.warningLevel)" effect="light">{{ getLevelText(warning.warningLevel) }}</el-tag>
                <span class="warning-title">{{ warning.description }}</span>
              </div>
              <p class="warning-desc">{{ warning.description }}</p>
              <div class="warning-meta">
                <span>📅 {{ formatDate(warning.createdAt) }}</span>
              </div>
              <div class="warning-actions">
                <el-button type="primary" size="small" link>查看详情</el-button>
                <el-button type="warning" size="small" link>申请帮扶</el-button>
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- 帮扶计划 -->
      <div class="card-section assistance-section">
        <div class="section-header">
          <h2 class="section-title">🤝 帮扶计划</h2>
          <router-link to="/assistance" class="view-all">查看全部 →</router-link>
        </div>
        <div class="assistance-list">
          <div v-if="assistancePlans.length === 0" class="empty-state">暂无帮扶计划</div>
          <div v-for="plan in assistancePlans.slice(0, 2)" :key="plan.id" class="assistance-item">
            <div class="assist-icon">👨‍🏫</div>
            <div class="assist-wrapper">
              <div class="assist-header">
                <div class="assist-title">{{ plan.title }}</div>
                <el-tag :type="getAssistanceTagType(plan.status)" effect="light">{{ getAssistanceStatusText(plan.status) }}</el-tag>
              </div>
              <div class="assist-meta">
                <span>{{ plan.description }}</span>
              </div>
              <div class="progress-wrapper">
                <el-progress :percentage="Math.round(plan.progressPercentage || 0)"></el-progress>
              </div>
              <div class="assist-status">进度：{{ Math.round(plan.progressPercentage || 0) }}%</div>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 通知中心 -->
    <div class="card-section notification-section">
      <div class="section-header">
        <h2 class="section-title">🔔 最新通知</h2>
        <router-link to="/notifications" class="view-all">查看全部 →</router-link>
      </div>
      <div class="notification-list">
        <div class="notification-item" v-for="notif in notifications" :key="notif.id">
          <div class="notif-badge">{{ notif.icon }}</div>
          <div class="notif-content">
            <h4>{{ notif.title }}</h4>
            <p>{{ notif.content }}</p>
          </div>
          <span class="notif-time">{{ notif.time }}</span>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, nextTick } from 'vue'
import * as echarts from 'echarts'
import { studentAPI } from '@/api/index'

const username = ref('学生')
const currentDate = ref('')
const avatarUrl = ref('https://cube.elemecdn.com/0/88/03b0f58596f3478fa5659716a36f6jpeg.jpeg')

// 学业数据
const academicData = ref({
  gpa: 0,
  totalCourses: 0,
  semesterCourses: 0,
  warningCount: 0,
  attendanceRate: 0,
  warningLevel: '',
  credits: 0,
  totalCredits: 0
})

// 预警数据
const warnings = ref([])

// 帮扶计划数据
const assistancePlans = ref([])

// 通知数据
const notifications = ref([])

onMounted(async () => {
  // 获取用户名
  const storedUsername = localStorage.getItem('username')
  if (storedUsername) {
    username.value = storedUsername
  }

  // 设置当前日期
  const today = new Date()
  const options = { year: 'numeric', month: 'long', day: 'numeric', weekday: 'long' }
  currentDate.value = today.toLocaleDateString('zh-CN', options)

  const userId = localStorage.getItem('userId')
  console.log('Dashboard userId:', userId)
  if (userId) {
    // 加载学业看板数据
    await loadAcademicData(userId)
    // 加载预警信息
    await loadWarnings(userId)
    // 加载帮扶计划
    await loadAssistancePlans(userId)
    // 加载通知
    await loadNotifications(userId)
  }

  // 初始化图表
  initChart()
})

// 加载学业看板数据
const loadAcademicData = async (userId) => {
  try {
    const response = await studentAPI.getDashboard(userId)
    console.log('看板数据响应:', response)
    if (response) {
      academicData.value = {
        gpa: response.gpa || 0,
        totalCourses: response.courseCount || 0,
        semesterCourses: response.courseCount || 0,
        warningCount: response.warningCount || 0,
        attendanceRate: 95,
        warningLevel: response.redWarnings > 0 ? '高预警' : ('中预警'),
        credits: 0,
        totalCredits: 0
      }
    }
  } catch (error) {
    console.error('加载学业数据失败:', error)
  }
}

// 加载预警信息
const loadWarnings = async (userId) => {
  try {
    const response = await studentAPI.getWarnings(userId)
    console.log('预警响应:', response)
    if (Array.isArray(response)) {
      warnings.value = response
    } else {
      warnings.value = []
    }
  } catch (error) {
    console.error('加载预警信息失败:', error)
    warnings.value = []
  }
}

// 加载帮扶计划
const loadAssistancePlans = async (userId) => {
  try {
    const response = await studentAPI.getAssistancePlans(userId)
    console.log('帮扶计划响应:', response)
    if (Array.isArray(response) && response.length > 0) {
      assistancePlans.value = response.map(plan => ({
        ...plan,
        title: plan.description || '帮扶计划',
        description: plan.progressPercentage ? `进度: ${plan.progressPercentage}%` : '',
        status: plan.status || 'initiated'
      }))
    } else {
      assistancePlans.value = []
    }
  } catch (error) {
    console.error('加载帮扶计划失败:', error)
    assistancePlans.value = []
  }
}

// 加载通知 - 使用新版API /notification-center
const loadNotifications = async (userId) => {
  try {
    const response = await studentAPI.getNotificationsPageable(userId, 1, 3)
    console.log('加载通知响应:', response)
    let notifList = []
    // 处理不同的响应结构
    if (response) {
      if (Array.isArray(response)) {
        notifList = response
      } else if (response.list && Array.isArray(response.list)) {
        notifList = response.list
      } else if (response.data && Array.isArray(response.data)) {
        notifList = response.data
      }
      if (notifList.length > 0) {
        notifications.value = notifList.map(notif => ({
          id: notif.id,
          icon: '📢',
          title: notif.title || notif.content || '系统通知',
          content: notif.content || '',
          time: formatTimeAgo(notif.createdAt)
        }))
      }
    }
  } catch (error) {
    console.error('加载通知失败:', error)
    notifications.value = []
  }
}

const initChart = () => {
  nextTick(() => {
    const chartDom = document.getElementById('scoreChart')
    if (!chartDom || chartDom.offsetWidth === 0) {
      setTimeout(initChart, 300)
      return
    }

    const myChart = echarts.init(chartDom)
    const option = {
      tooltip: { trigger: 'axis' },
      grid: { left: '3%', right: '4%', bottom: '3%', top: '5%', containLabel: true },
      xAxis: { type: 'category', data: ['9月', '10月', '11月', '12月'], boundaryGap: false },
      yAxis: { type: 'value', min: 0, max: 100 },
      series: [
        { name: '平均分', data: [78, 81, 75, 82], type: 'line', smooth: true },
        { name: '目标分', data: [80, 80, 80, 80], type: 'line', smooth: true }
      ]
    }
    myChart.setOption(option)
    window.addEventListener('resize', () => myChart.resize())
  })
}

// 添加空状态样式
const style = `
.empty-state {
  text-align: center;
  padding: 40px 20px;
  color: #999;
  font-size: 14px;
}
`

// 格式化日期
const formatDate = (dateStr) => {
  if (!dateStr) return ''
  const date = new Date(dateStr)
  return date.toLocaleDateString('zh-CN')
}

// 格式化时间（几分钟/小时/天前）
const formatTimeAgo = (dateStr) => {
  if (!dateStr) return ''
  const date = new Date(dateStr)
  const now = new Date()
  const diff = Math.floor((now - date) / 1000)
  
  if (diff < 60) return '刚刚'
  if (diff < 3600) return Math.floor(diff / 60) + '分钟前'
  if (diff < 86400) return Math.floor(diff / 3600) + '小时前'
  return Math.floor(diff / 86400) + '天前'
}

// 获取预警级别标签类型
const getTagType = (level) => {
  if (level === 'high' || level === 'red') return 'danger'
  if (level === 'medium' || level === 'yellow') return 'warning'
  return 'success'
}

// 获取预警级别文本
const getLevelText = (level) => {
  if (level === 'high' || level === 'red') return '高级'
  if (level === 'medium' || level === 'yellow') return '中级'
  return '低级'
}

// 获取预警级别样式
const getLevelClass = (level) => {
  if (level === 'high' || level === 'red') return 'high'
  if (level === 'medium' || level === 'yellow') return 'medium'
  return 'low'
}

// 获取预警图标
const getLevelIcon = (level) => {
  if (level === 'high' || level === 'red') return '🔴'
  if (level === 'medium' || level === 'yellow') return '🟡'
  return '🟢'
}

// 获取帮扶计划标签类型
const getAssistanceTagType = (status) => {
  if (status === 'in_progress') return 'warning'
  if (status === 'completed') return 'success'
  return 'info'
}

// 获取帮扶计划状态文本
const getAssistanceStatusText = (status) => {
  if (status === 'in_progress') return '进行中'
  if (status === 'completed') return '已完成'
  return '未开始'
}

// 计算GPA: SUM(grade_point * credits) / SUM(credits)
const calculateGPA = (scores) => {
  if (!scores || scores.length === 0) return 0
  let totalPoints = 0
  let totalCredits = 0
  scores.forEach(score => {
    totalPoints += (score.gradePoint || 0) * (score.credits || 0)
    totalCredits += score.credits || 0
  })
  return totalCredits === 0 ? 0 : (totalPoints / totalCredits).toFixed(2)
}
</script>

<style scoped>
.dashboard-wrapper {
  background: linear-gradient(135deg, #f5f7fa 0%, #e3f2fd 100%);
  padding: 20px;
  min-height: 100vh;
}

/* 欢迎卡片 */
.welcome-card {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border-radius: 12px;
  padding: 40px;
  margin-bottom: 30px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  color: white;
  box-shadow: 0 8px 32px rgba(102, 126, 234, 0.4);
  position: relative;
  overflow: hidden;
  animation: slideDown 0.6s ease-out;
}

.welcome-animation {
  position: absolute;
  width: 400px;
  height: 400px;
  background: radial-gradient(circle, rgba(255, 255, 255, 0.1) 0%, transparent 70%);
  border-radius: 50%;
  top: -100px;
  right: -100px;
  animation: float 6s ease-in-out infinite;
}

@keyframes slideDown {
  from { opacity: 0; transform: translateY(-20px); }
  to { opacity: 1; transform: translateY(0); }
}

@keyframes float {
  0%, 100% { transform: translateY(0px); }
  50% { transform: translateY(-20px); }
}

.welcome-content {
  z-index: 1;
  flex: 1;
}

.greeting-icon {
  display: inline-block;
  font-size: 40px;
  margin-bottom: 15px;
  animation: wave 0.6s ease-in-out;
}

@keyframes wave {
  0%, 100% { transform: rotate(0deg); }
  10%, 30% { transform: rotate(14deg); }
  20% { transform: rotate(-8deg); }
  40% { transform: rotate(10deg); }
}

.welcome-content h1 {
  margin: 0;
  font-size: 32px;
  font-weight: bold;
  letter-spacing: 0.5px;
}

.welcome-content p {
  margin: 12px 0 0 0;
  font-size: 14px;
  opacity: 0.95;
}

.user-avatar-wrapper {
  position: relative;
  z-index: 1;
  margin-left: 40px;
}

.avatar-bg {
  position: absolute;
  width: 120px;
  height: 120px;
  background: rgba(255, 255, 255, 0.2);
  border-radius: 50%;
  animation: pulse 2s ease-in-out infinite;
}

@keyframes pulse {
  0%, 100% { transform: scale(1); opacity: 0.2; }
  50% { transform: scale(1.1); opacity: 0.1; }
}

.user-avatar {
  border: 3px solid rgba(255, 255, 255, 0.5);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.2);
  position: relative;
  z-index: 2;
}

/* 统计部分 */
.stats-section {
  margin-bottom: 30px;
}

.section-title {
  margin: 0 0 20px 0;
  font-size: 20px;
  font-weight: bold;
  color: #333;
}

.stats-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
  gap: 20px;
}

.stat-card {
  background: white;
  border-radius: 12px;
  padding: 24px;
  display: flex;
  align-items: center;
  gap: 15px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.08);
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  position: relative;
  overflow: hidden;
}

.stat-card::before {
  content: '';
  position: absolute;
  top: 0; left: 0; right: 0;
  height: 4px;
  background: linear-gradient(90deg, #409eff, #67c23a);
  transform: scaleX(0);
  transform-origin: left;
  transition: transform 0.3s ease;
}

.stat-card:hover {
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.15);
  transform: translateY(-5px);
}

.stat-card:hover::before {
  transform: scaleX(1);
}

.stat-icon {
  font-size: 36px;
  min-width: 50px;
}

.stat-content {
  flex: 1;
}

.stat-label {
  font-size: 12px;
  color: #999;
  margin-bottom: 5px;
}

.stat-number {
  font-size: 28px;
  font-weight: bold;
  color: #409eff;
  margin-bottom: 5px;
}

.gpa-card .stat-number { color: #409eff; }
.courses-card .stat-number { color: #67c23a; }
.warning-card .stat-number { color: #e6a23c; }
.warning-num { color: #e6a23c !important; }
.attendance-card .stat-number { color: #f56c6c; }

.stat-detail {
  font-size: 12px;
  color: #999;
}

/* 图表部分 */
.chart-section {
  background: white;
  border-radius: 12px;
  padding: 28px;
  margin-bottom: 30px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.08);
  animation: fadeIn 0.6s ease-out 0.2s backwards;
}

@keyframes fadeIn {
  from { opacity: 0; transform: translateY(20px); }
  to { opacity: 1; transform: translateY(0); }
}

.chart-wrapper {
  border-radius: 8px;
  background: linear-gradient(135deg, #f9fafb 0%, #f3f4f6 100%);
  padding: 20px;
}

.chart-container {
  min-height: 350px;
  border-radius: 6px;
  background: white;
  padding: 20px;
}

/* 内容网格 */
.content-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 24px;
  margin-bottom: 30px;
}

@media (max-width: 1200px) {
  .content-grid { grid-template-columns: 1fr; }
}

.card-section {
  background: white;
  border-radius: 12px;
  padding: 28px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.08);
  transition: all 0.3s ease;
  animation: fadeIn 0.6s ease-out 0.3s backwards;
}

.card-section:hover {
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.12);
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.view-all {
  color: #409eff;
  text-decoration: none;
  font-size: 13px;
  transition: all 0.3s ease;
}

.view-all:hover {
  color: #66b1ff;
  transform: translateX(3px);
}

/* 预警列表 */
.warning-list {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.warning-item {
  padding: 16px;
  border-radius: 8px;
  display: flex;
  gap: 12px;
  background-color: #f9fafb;
  border: 1px solid #f0f0f0;
  transition: all 0.3s ease;
}

.warning-item:hover {
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.08);
  transform: translateX(4px);
}

.warning-item.medium {
  border-left: 4px solid #e6a23c;
}

.warning-item.low {
  border-left: 4px solid #67c23a;
}

.warning-item.high {
  border-left: 4px solid #f56c6c;
}

.warning-icon {
  font-size: 24px;
  min-width: 30px;
}

.warning-content {
  flex: 1;
}

.warning-header {
  display: flex;
  align-items: center;
  gap: 10px;
  margin-bottom: 8px;
}

.warning-title {
  font-weight: bold;
  color: #333;
  font-size: 14px;
}

.warning-desc {
  margin: 0;
  font-size: 13px;
  color: #666;
  line-height: 1.6;
}

.warning-meta {
  margin: 8px 0;
  font-size: 12px;
  color: #999;
}

.warning-actions {
  display: flex;
  gap: 8px;
  margin-top: 10px;
}

/* 帮扶列表 */
.assistance-list {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.assistance-item {
  padding: 16px;
  border-radius: 8px;
  background: linear-gradient(135deg, #f9fafb 0%, #f3f4f6 100%);
  border: 1px solid #e8e8e8;
  display: flex;
  gap: 12px;
  transition: all 0.3s ease;
}

.assistance-item:hover {
  background: linear-gradient(135deg, #f0f4ff 0%, #f0f7ff 100%);
  box-shadow: 0 4px 12px rgba(64, 158, 255, 0.1);
  border-color: #b3d8ff;
}

.assist-icon {
  font-size: 32px;
  min-width: 40px;
  display: flex;
  align-items: center;
}

.assist-wrapper {
  flex: 1;
}

.assist-header {
  display: flex;
  align-items: center;
  gap: 10px;
  margin-bottom: 8px;
}

.assist-title {
  font-weight: 600;
  color: #333;
  font-size: 14px;
}

.assist-meta {
  font-size: 12px;
  color: #999;
  margin: 8px 0;
  line-height: 1.5;
}

.progress-wrapper {
  margin: 12px 0;
}

.assist-status {
  font-size: 12px;
  color: #999;
}

/* 通知列表 */
.notification-section {
  grid-column: 1 / -1;
}

.notification-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.notification-item {
  display: flex;
  align-items: center;
  gap: 16px;
  padding: 16px;
  background: linear-gradient(135deg, #f9fafb 0%, #f3f4f6 100%);
  border-radius: 8px;
  border: 1px solid #e8e8e8;
  transition: all 0.3s ease;
}

.notification-item:hover {
  background: linear-gradient(135deg, #fff8f0 0%, #fff3e0 100%);
  border-color: #ffe4c0;
  transform: translateX(4px);
}

.notif-badge {
  font-size: 24px;
  min-width: 32px;
  text-align: center;
}

.notif-content {
  flex: 1;
}

.notif-content h4 {
  margin: 0 0 4px 0;
  font-size: 14px;
  font-weight: 600;
  color: #333;
}

.notif-content p {
  margin: 0;
  font-size: 13px;
  color: #666;
}

.notif-time {
  font-size: 12px;
  color: #999;
  white-space: nowrap;
  flex-shrink: 0;
}
</style>
