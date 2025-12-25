<template>
  <div class="dashboard-wrapper">
    <!-- 欢迎卡片 -->
    <div class="welcome-card">
      <div class="welcome-content">
        <h1>管理员仪表盘</h1>
        <p>全校数据统计、预警分析和系统监控中心</p>
      </div>
      <div class="welcome-time">{{ currentTime }}</div>
    </div>

    <!-- 核心指标卡片 -->
    <div class="metrics-section">
      <div class="metric-card blue">
        <div class="metric-header">
          <span class="metric-title">全校学生</span>
        </div>
        <div class="metric-value">{{ stats.studentCount || '-' }}</div>
        <div class="metric-detail">在籍学生总数</div>
      </div>
      <div class="metric-card green">
        <div class="metric-header">
          <span class="metric-title">开设课程</span>
        </div>
        <div class="metric-value">{{ stats.courseCount || '-' }}</div>
        <div class="metric-detail">本学期课程数</div>
      </div>
      <div class="metric-card red">
        <div class="metric-header">
          <span class="metric-title">红色预警</span>
        </div>
        <div class="metric-value">{{ stats.redWarnings || 0 }}</div>
        <div class="metric-detail">需立即干预学生</div>
      </div>
      <div class="metric-card purple">
        <div class="metric-header">
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
            <span class="warning-label">红色预警</span>
            <div class="warning-bar" style="background: #667eea; width: 45%"></div>
            <span class="warning-count">{{ stats.redWarnings !== null ? stats.redWarnings : '-' }}</span>
          </div>
          <div class="warning-item">
            <span class="warning-label">黄色预警</span>
            <div class="warning-bar" style="background: #667eea; width: 30%"></div>
            <span class="warning-count">{{ stats.yellowWarnings !== null ? stats.yellowWarnings : '-' }}</span>
          </div>
          <div class="warning-item">
            <span class="warning-label">蓝色预警</span>
            <div class="warning-bar" style="background: #667eea; width: 25%"></div>
            <span class="warning-count">{{ stats.blueWarnings !== null ? stats.blueWarnings : '-' }}</span>
          </div>
        </div>
      </div>

      <div class="chart-card">
        <h3>最近动态</h3>
        <div class="activity-list">
          <div class="activity-item">
            <div class="activity-time">实时数据</div>
            <div class="activity-content">
              <span class="activity-type notify">提示</span>
              <span>仪表盘核心数据已关联真实数据库</span>
            </div>
          </div>
        </div>
      </div>
    </div>



    <!-- 快速操作 -->
    <div class="actions-section">
      <h2>快速操作</h2>
      <div class="action-grid">
        <router-link to="/admin/colleges" class="action-card">
          <div class="action-name">学院管理</div>
        </router-link>
        <router-link to="/admin/majors" class="action-card">
          <div class="action-name">专业管理</div>
        </router-link>
        <router-link to="/admin/users" class="action-card">
          <div class="action-name">用户管理</div>
        </router-link>
        <router-link to="/admin/courses" class="action-card">
          <div class="action-name">课程管理</div>
        </router-link>
        <router-link to="/admin/rules" class="action-card">
          <div class="action-name">规则管理</div>
        </router-link>
        <router-link to="/admin/statistics" class="action-card">
          <div class="action-name">数据分析</div>
        </router-link>
      </div>
    </div>

    <!-- 系统动态 -->
    <div class="activity-section">
      <h2>最近动态</h2>
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
  newWarnings: null,
  processedWarnings: null,
  onlineTeachers: null,
  onlineStudents: null
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
  } catch (error) {
    console.error('加载管理员看板失败:', error)
  }
}
</script>

<style scoped>
.dashboard-wrapper {
  background: linear-gradient(135deg, #f5f7fb 0%, #e8ecf1 100%);
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
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.06);
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  border: 1px solid #e9ecef;
  border-top: 4px solid #667eea;
  position: relative;
  overflow: hidden;
}

.metric-card::before {
  content: '';
  position: absolute;
  top: 0;
  right: -50px;
  width: 120px;
  height: 120px;
  background: radial-gradient(circle, rgba(102, 126, 234, 0.08) 0%, transparent 70%);
  border-radius: 50%;
}

.metric-card.blue { border-top-color: #667eea; background: linear-gradient(135deg, rgba(255,255,255,1) 0%, rgba(246,248,253,0.5) 100%); }
.metric-card.green { border-top-color: #667eea; background: linear-gradient(135deg, rgba(255,255,255,1) 0%, rgba(248,251,248,0.5) 100%); }
.metric-card.red { border-top-color: #667eea; background: linear-gradient(135deg, rgba(255,255,255,1) 0%, rgba(253,246,246,0.5) 100%); }
.metric-card.purple { border-top-color: #667eea; background: linear-gradient(135deg, rgba(255,255,255,1) 0%, rgba(250,247,253,0.5) 100%); }

.metric-card:hover {
  box-shadow: 0 12px 32px rgba(102, 126, 234, 0.15);
  transform: translateY(-8px);
  border-top: 4px solid #5568d3;
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
  font-size: 36px;
  font-weight: 800;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  background-clip: text;
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  margin-bottom: 12px;
  letter-spacing: -1px;
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
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.06);
  border: 1px solid #e9ecef;
  transition: all 0.3s ease;
}

.chart-card:hover {
  box-shadow: 0 8px 24px rgba(102, 126, 234, 0.12);
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
  color: #667eea;
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
  border-left: 4px solid #667eea;
}

.today-card.new { border-left-color: #667eea; }
.today-card.processed { border-left-color: #667eea; }
.today-card.pending { border-left-color: #667eea; }

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
  padding: 24px;
  text-align: center;
  text-decoration: none;
  color: #333;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.06);
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
  transform: translateY(-8px);
  border-color: #667eea;
  color: #667eea;
}

.action-icon {
  font-size: 36px;
  transition: all 0.3s cubic-bezier(0.34, 1.56, 0.64, 1);
  position: relative;
  z-index: 1;
}

.action-card:hover .action-icon {
  transform: scale(1.3) rotateY(360deg);
}

.action-name {
  font-size: 13px;
  font-weight: 600;
  position: relative;
  z-index: 1;
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
  border: 1px solid #e9ecef;
}

.activity-item {
  padding: 18px 24px;
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
  background: #f0f2f8;
  color: #667eea;
}

.activity-type.modify {
  background: #f0f2f8;
  color: #667eea;
}

.activity-type.notify {
  background: #f0f2f8;
  color: #667eea;
}
</style>
