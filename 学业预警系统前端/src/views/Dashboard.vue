<template>
  <div class="student-dashboard">
    <!-- 顶部欢迎区域 -->
    <div class="welcome-section">
      <div class="welcome-card">
        <div class="welcome-animation"></div>
        <div class="welcome-header">
          <div class="welcome-text">
            <h1>欢迎回来，{{ username }}！</h1>
            <p>{{ getGreeting() }} | 管理您的学业数据</p>
            <p v-if="classInfo.classIdentifier" class="class-info-text">{{ classInfo.classIdentifier }}</p>
          </div>
          <div class="welcome-avatar">{{ username.charAt(0) }}</div>
        </div>
        <div class="welcome-stats">
          <div class="stat">
            <div class="stat-value">{{ academicData.gpa }}</div>
            <div class="stat-label">本学期GPA</div>
          </div>
          <div class="stat-divider"></div>
          <div class="stat">
            <div class="stat-value">{{ academicData.semesterCourses }}</div>
            <div class="stat-label">课程数</div>
          </div>
          <div class="stat-divider"></div>
          <div class="stat">
            <div class="stat-value">{{ classInfo.rankInClass || '-' }}</div>
            <div class="stat-label">班级排名</div>
          </div>
        </div>
      </div>
    </div>

    <!-- 关键指标卡片 -->
    <div class="metrics-section">
      <div class="metrics-grid">
        <!-- GPA卡片 -->
        <div class="metric-card gpa-metric">
          <div class="metric-icon"></div>
          <div class="metric-content">
            <div class="metric-label">累计GPA</div>
            <div class="metric-value">{{ academicData.gpa }}</div>
            <div v-if="growthRate.hasData" class="metric-trend">较上学期 ↑ {{ growthRate.gpa.toFixed(1) }}</div>
            <div v-else class="metric-trend">暂无数据</div>
          </div>
        </div>

        <!-- 学分卡片 -->
        <div class="metric-card credits-metric">
          <div class="metric-icon"></div>
          <div class="metric-content">
            <div class="metric-label">已修学分</div>
            <div class="metric-value">{{ academicData.totalCourses }}</div>
            <div class="metric-trend">本学期 {{ academicData.semesterCourses }} 门课</div>
          </div>
        </div>

        <!-- 预警卡片 -->
        <div class="metric-card warning-metric">
          <div class="metric-icon"></div>
          <div class="metric-content">
            <div class="metric-label">预警状态</div>
            <div class="metric-value" :class="{ critical: academicData.warningCount > 0 }">
              {{ academicData.warningCount === 0 ? '安全' : '有预警' }}
            </div>
            <div class="metric-trend">{{ academicData.warningLevel }}</div>
          </div>
        </div>

        <!-- 学业建议卡片 -->
        <div class="metric-card advice-metric">
          <div class="metric-icon"></div>
          <div class="metric-content">
            <div class="metric-label">学业建议</div>
            <div class="metric-value">3条</div>
            <div class="metric-trend">查看详情</div>
          </div>
        </div>
      </div>
    </div>

    <!-- 主要内容区域 -->
    <div class="content-section">
      <!-- 左侧：预警信息 -->
      <div class="left-column">
        <div class="card warning-card">
          <div class="card-header">
            <h2>最新预警</h2>
            <router-link to="/student/warnings" class="view-more">查看全部</router-link>
          </div>
          <div class="card-body">
            <div v-if="warnings.length === 0" class="empty-state">
              <div class="empty-icon">✓</div>
              <p>暂无预警信息，学业状态良好</p>
            </div>
            <div v-else v-for="warning in warnings.slice(0, 3)" :key="warning.id" class="warning-item">
              <div class="warning-badge" :class="getLevelClass(warning.warningLevel)"></div>
              <div class="warning-content">
                <h4>{{ warning.description }}</h4>
                <p>{{ warning.detail }}</p>
                <div class="warning-actions">
                  <el-button type="primary" size="small" link>查看详情</el-button>
                </div>
              </div>
            </div>
          </div>
        </div>

        <!-- 最近课程 -->
        <div class="card recent-courses-card">
          <div class="card-header">
            <h2>本学期课程</h2>
            <router-link to="/student/scores" class="view-more">查看成绩</router-link>
          </div>
          <div class="card-body">
            <div v-if="courses.length === 0" class="empty-state">
              <div class="empty-icon"></div>
              <p>暴攧课程数据</p>
            </div>
            <div v-else class="courses-list">
              <div v-for="course in courses.slice(0, 4)" :key="course.id" class="course-item">
                <div class="course-name">{{ course.courseName || course.name }}</div>
                <div class="course-info">
                  <span class="course-credit">{{ course.credit || 0 }}学分</span>
                  <span class="course-grade">{{ course.scoreTotal || course.score || '-' }}分</span>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- 右侧：帮扶计划和其他 -->
      <div class="right-column">
        <!-- 帮扶计划 -->
        <div class="card assistance-card">
          <div class="card-header">
            <h2>帮扶计划</h2>
            <router-link to="/student/assistance" class="view-more">查看全部</router-link>
          </div>
          <div class="card-body">
            <div v-if="assistancePlans.length === 0" class="empty-state">
              <div class="empty-icon"></div>
              <p>暂无帮扶计划</p>
            </div>
            <div v-else v-for="plan in assistancePlans.slice(0, 2)" :key="plan.id" class="plan-item">
              <div class="plan-header">
                <h4>{{ plan.title || plan.teacherName || '帮扶计划' }}</h4>
                <span class="plan-status">{{ plan.status === 'completed' ? '已完成' : '进行中' }}</span>
              </div>
              <p>{{ plan.description || plan.content || plan.measures }}</p>
              <div class="plan-meta">{{ formatDate(plan.startDate || plan.createdAt) }}</div>
            </div>
          </div>
        </div>

        <!-- 学业建议 -->
        <div class="card suggestions-card">
          <div class="card-header">
            <h2>学业建议</h2>
          </div>
          <div class="card-body">
            <div v-if="suggestions.length === 0" class="empty-state">
              <div class="empty-icon">💡</div>
              <p>暂无学业建议</p>
            </div>
            <div v-else v-for="suggestion in suggestions" :key="suggestion.id" class="suggestion-item">
              <div class="suggestion-icon">{{ suggestion.icon || '💡' }}</div>
              <div class="suggestion-content">
                <h4>{{ suggestion.title }}</h4>
                <p>{{ suggestion.content }}</p>
              </div>
            </div>
          </div>
        </div>

        <!-- 快速操作 -->
        <div class="card quick-actions-card">
          <div class="card-header">
            <h2>快速操作</h2>
          </div>
          <div class="card-body">
            <div class="quick-action-grid">
              <router-link to="/student/scores" class="quick-action-btn">
                <div class="action-icon"></div>
                <div>查看成绩</div>
              </router-link>
              <router-link to="/student/appeals" class="quick-action-btn">
                <div class="action-icon"></div>
                <div>成绩申诉</div>
              </router-link>
              <router-link to="/student/messages" class="quick-action-btn">
                <div class="action-icon"></div>
                <div>消息</div>
              </router-link>
              <router-link to="/student/notification-center" class="quick-action-btn">
                <div class="action-icon"></div>
                <div>通知</div>
              </router-link>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { studentAPI } from '@/api/index'
import { getUserId } from '@/utils/userUtils'

const router = useRouter()
const username = ref(localStorage.getItem('userName') || localStorage.getItem('username') || '学生')

// 学业数据
const academicData = ref({
  gpa: 0,
  semesterCourses: 0,
  totalCourses: 0,
  warningCount: 0,
  redWarnings: 0,
  yellowWarnings: 0,
  assistanceCount: 0,
  warningLevel: '正常'
})

// 增长率数据
const growthRate = ref({
  gpa: 0,
  hasData: false
})

// 班级信息
const classInfo = ref({
  classIdentifier: '',
  rankInClass: ''
})

// 预警信息
const warnings = ref([])

// 帮扶计划
const assistancePlans = ref([])

// 本学期课程
const courses = ref([])

// 学业建议
const suggestions = ref([])

// 获取问候语
const getGreeting = () => {
  const hour = new Date().getHours()
  if (hour < 12) return '上午好'
  if (hour < 18) return '下午好'
  return '晚上好'
}

// 获取预警等级样式
const getLevelClass = (level) => {
  const classes = {
    1: 'level-1',
    2: 'level-2',
    3: 'level-3'
  }
  return classes[level] || 'level-1'
}

// 格式化日期
const formatDate = (date) => {
  if (!date) return ''
  const d = new Date(date)
  return `${d.getFullYear()}-${String(d.getMonth() + 1).padStart(2, '0')}-${String(d.getDate()).padStart(2, '0')}`
}

// 加载 Dashboard 数据
const loadDashboard = async () => {
  try {
    const userId = getUserId()
    const studentId = localStorage.getItem('studentId')
    console.log('Dashboard 加载 - userId:', userId, 'studentId:', studentId)
    
    if (!userId) {
      console.error('用户ID不存在，无法加载数据')
      return
    }

    // 加载学业数据
    console.log('正在调用 getDashboard API, userId:', userId)
    const dashboardResponse = await studentAPI.getDashboard(userId)
    console.log('Dashboard API 响应:', dashboardResponse)
    
    // 处理两个下的响应格式：1. 稍便回徤拦截器提取后的 data 对象， 2. 例外或错误后的完整响应
    let data = null
    if (dashboardResponse && typeof dashboardResponse === 'object') {
      // 如果响应子是一个常見的业务数据对象（已经被拦截器提取了 data ）
      if ('courseCount' in dashboardResponse || 'gpa' in dashboardResponse) {
        data = dashboardResponse
      } else if (dashboardResponse.code === 200 && dashboardResponse.data) {
        // 如果是完整的响应对象（未被拦截器处理）
        data = dashboardResponse.data
      }
    }
    
    if (data && typeof data === 'object') {
      academicData.value = {
        gpa: parseFloat(data.gpa) || 0,
        semesterCourses: parseInt(data.courseCount) || 0,
        totalCourses: data.totalCourses || 0,
        warningCount: parseInt(data.warningCount) || 0,
        redWarnings: parseInt(data.redWarnings) || 0,
        yellowWarnings: parseInt(data.yellowWarnings) || 0,
        assistanceCount: parseInt(data.assistanceCount) || 0,
        warningLevel: (parseInt(data.warningCount) || 0) === 0 ? '正常' : '有预警'
      }
      console.log('学业数据已加载:', academicData.value)
      
      // 计算增长率
      // 只有当有数据时才显示增长率
      growthRate.value.hasData = academicData.value.gpa > 0 || academicData.value.totalCourses > 0
      // 这里可以根据实际情况计算增长率
      // 暂时设置为0，实际项目中应该从后端获取上学期数据进行计算
      growthRate.value.gpa = 0
    } else {
      console.error('Dashboard API 响应不正常:', dashboardResponse)
      growthRate.value.hasData = false
    }

    // 加载班级信息
    if (studentId) {
      console.log('正在调用 getClassInfo API, studentId:', studentId)
      try {
        const classResponse = await studentAPI.getClassInfo(studentId)
        console.log('ClassInfo API 响应:', classResponse)
        if (classResponse && typeof classResponse === 'object') {
          // 处理两种可能的响应格式
          let classData = classResponse
          if (classResponse.code === 200 && classResponse.data) {
            classData = classResponse.data
          }
          if (classData && typeof classData === 'object') {
            classInfo.value = {
              classIdentifier: classData.classIdentifier || '',
              rankInClass: classData.rankInClass || ''
            }
          }
        }
      } catch (error) {
        console.warn('加载班级信息失败:', error.message)
        // 班级信息加载失败不会影响整个页面正常使用
      }
    } else {
      console.warn('学号未找到，无法加载班级信息')
    }

    // 加载预警信息
    if (userId) {
      const warningsResponse = await studentAPI.getWarnings(userId)
      if (warningsResponse && Array.isArray(warningsResponse)) {
        warnings.value = warningsResponse.slice(0, 3)
      } else if (warningsResponse && warningsResponse.code === 200 && Array.isArray(warningsResponse.data)) {
        warnings.value = warningsResponse.data.slice(0, 3)
      }
    }

    // 加载帮扶计划
    if (userId) {
      const assistanceResponse = await studentAPI.getAssistancePlans(userId)
      if (assistanceResponse && Array.isArray(assistanceResponse)) {
        assistancePlans.value = assistanceResponse.slice(0, 3)
      } else if (assistanceResponse && assistanceResponse.code === 200 && Array.isArray(assistanceResponse.data)) {
        assistancePlans.value = assistanceResponse.data.slice(0, 3)
      }
    }

    // 加载学业建议
    if (userId) {
      try {
        const suggestionsResponse = await studentAPI.getSuggestions(userId)
        console.log('学业建议API响应:', suggestionsResponse)
        
        if (suggestionsResponse && Array.isArray(suggestionsResponse)) {
          suggestions.value = suggestionsResponse
        } else if (suggestionsResponse && suggestionsResponse.code === 200 && Array.isArray(suggestionsResponse.data)) {
          suggestions.value = suggestionsResponse.data
        }
      } catch (error) {
        console.warn('加载学业建议失败:', error.message)
      }
    }

    // 加载本学期课程
    if (userId) {
      try {
        const scoresResponse = await studentAPI.getScores(userId)
        console.log('成绩API响应:', scoresResponse)
        
        let allScores = []
        if (scoresResponse && Array.isArray(scoresResponse)) {
          // 处理数组响应
          allScores = scoresResponse
        } else if (scoresResponse && scoresResponse.code === 200 && Array.isArray(scoresResponse.data)) {
          // 处理完整响应对象
          allScores = scoresResponse.data
        }
        
        // 筛选当前学期（2025-2026-1）的课程
        const currentSemester = '2025-2026-1'
        const currentSemesterCourses = allScores.filter(score => {
          const semester = score.semester || ''
          // 匹配当前学期的各种格式：2025-2026-1 或 2025-2026
          return semester === currentSemester || semester.startsWith('2025-2026')
        })
        
        console.log('筛选后的本学期课程数:', currentSemesterCourses.length, currentSemesterCourses)
        courses.value = currentSemesterCourses.slice(0, 4)
      } catch (error) {
        console.warn('加载本学期课程失败:', error.message)
      }
    }
  } catch (error) {
    console.error('加载 Dashboard 数据失败:', error)
    // 数据加载失败会使用默认值
  }
}

onMounted(() => {
  loadDashboard()
})
</script>

<style scoped>
.student-dashboard {
  padding: 20px;
  background: linear-gradient(135deg, #f5f7fa 0%, #e8ecf1 100%);
  min-height: 100vh;
}

/* ===== 欢迎区域 ===== */
.welcome-section {
  margin-bottom: 30px;
}

.welcome-card {
  background: white;
  border-radius: 16px;
  padding: 45px;
  color: #333;
  box-shadow: 0 12px 40px rgba(0, 0, 0, 0.1);
  position: relative;
  overflow: hidden;
  animation: slideDown 0.6s ease-out;
  border: 1px solid #e0e0e0;
}

.welcome-animation {
  position: absolute;
  width: 400px;
  height: 400px;
  background: radial-gradient(circle, rgba(79, 172, 254, 0.1) 0%, transparent 70%);
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

.welcome-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 25px;
  position: relative;
  z-index: 1;
}

.welcome-text h1 {
  font-size: 32px;
  margin: 0 0 10px 0;
  font-weight: bold;
  color: #333;
}

.welcome-text p {
  margin: 0;
  font-size: 14px;
  opacity: 0.95;
  color: #666;
}

.welcome-text .class-info-text {
  margin-top: 8px;
  font-size: 13px;
  opacity: 0.85;
  font-weight: 500;
  color: #666;
}

.welcome-avatar {
  width: 60px;
  height: 60px;
  border-radius: 50%;
  background: rgba(79, 172, 254, 0.1);
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 24px;
  font-weight: 600;
  color: #4facfe;
}

.welcome-stats {
  display: flex;
  justify-content: space-around;
  border-top: 1px solid rgba(0, 0, 0, 0.08);
  padding-top: 20px;
  position: relative;
  z-index: 1;
}

.stat {
  text-align: center;
  flex: 1;
}

.stat-value {
  font-size: 24px;
  font-weight: 700;
  margin-bottom: 5px;
  color: #4facfe;
}

.stat-label {
  font-size: 12px;
  opacity: 0.8;
  color: #666;
}

.stat-divider {
  width: 1px;
  background: rgba(0, 0, 0, 0.08);
}

/* 班级信息标识 */
.class-info-display {
  margin-top: 20px;
  padding-top: 20px;
  border-top: 1px solid rgba(255, 255, 255, 0.2);
  text-align: center;
}

.class-identifier-badge {
  display: inline-block;
  background: rgba(255, 255, 255, 0.15);
  padding: 8px 16px;
  border-radius: 20px;
  font-size: 13px;
  backdrop-filter: blur(10px);
  transition: all 0.3s ease;
  border: 1px solid rgba(255, 255, 255, 0.2);
}

.class-identifier-badge:hover {
  background: rgba(255, 255, 255, 0.25);
  transform: scale(1.05);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
}

/* ===== 指标卡片 ===== */
.metrics-section {
  margin-bottom: 30px;
}

.metrics-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
  gap: 16px;
}

.metric-card {
  background: white;
  border-radius: 16px;
  padding: 24px;
  box-shadow: 0 6px 20px rgba(0, 0, 0, 0.08);
  display: flex;
  align-items: center;
  gap: 16px;
  transition: all 0.4s cubic-bezier(0.4, 0, 0.2, 1);
  border: 2px solid transparent;
}

.metric-card:hover {
  box-shadow: 0 12px 32px rgba(0, 0, 0, 0.15);
  transform: translateY(-8px);
  border-color: #e8ecf1;
}

.metric-icon {
  font-size: 36px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #4facfe;
}

.metric-content {
  flex: 1;
}

.metric-label {
  color: #999;
  font-size: 13px;
  margin-bottom: 6px;
  font-weight: 500;
}

.metric-value {
  font-size: 28px;
  font-weight: bold;
  background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
  margin-bottom: 4px;
}

.metric-value.critical {
  color: #ff6b6b;
}

.metric-trend {
  font-size: 12px;
  color: #aaa;
}

/* ===== 主要内容区域 ===== */
.content-section {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 20px;
}

.card {
  background: white;
  border-radius: 16px;
  box-shadow: 0 6px 20px rgba(0, 0, 0, 0.08);
  overflow: hidden;
  border: 2px solid transparent;
  transition: all 0.3s ease;
  margin-bottom: 24px !important;
}

.card:hover {
  border-color: #e8ecf1;
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.12);
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 20px;
  border-bottom: 1px solid #f0f0f0;
}

.card-header h2 {
  margin: 0;
  font-size: 16px;
  color: #333;
  font-weight: 600;
  background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.view-more {
  color: #4facfe;
  text-decoration: none;
  font-size: 12px;
  transition: all 0.3s ease;
}

.view-more:hover {
  color: #00f2fe;
}

.card-body {
  padding: 20px;
}

/* 空状态 */
.empty-state {
  text-align: center;
  padding: 40px 20px;
  color: #999;
}

.empty-icon {
  font-size: 40px;
  margin-bottom: 10px;
  display: block;
}

.empty-state p {
  margin: 0;
  font-size: 14px;
}

/* 预警项目 */
.warning-item {
  display: flex;
  gap: 12px;
  padding: 15px 0;
  border-bottom: 1px solid #f0f0f0;
}

.warning-item:last-child {
  border-bottom: none;
}

.warning-badge {
  width: 4px;
  height: 60px;
  border-radius: 2px;
  background: #ff6b6b;
}

.warning-badge.level-1 {
  background: #ffd93d;
}

.warning-badge.level-2 {
  background: #ff9100;
}

.warning-badge.level-3 {
  background: #ff6b6b;
}

.warning-content h4 {
  margin: 0 0 5px 0;
  font-size: 14px;
  color: #333;
}

.warning-content p {
  margin: 0 0 10px 0;
  font-size: 12px;
  color: #999;
}

.warning-actions {
  display: flex;
  gap: 8px;
}

/* 帮扶项目 */
.plan-item {
  padding: 15px 0;
  border-bottom: 1px solid #f0f0f0;
}

.plan-item:last-child {
  border-bottom: none;
}

.plan-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 5px;
}

.plan-header h4 {
  margin: 0;
  font-size: 14px;
  color: #333;
}

.plan-status {
  font-size: 11px;
  background: #e3f2fd;
  color: #667eea;
  padding: 3px 8px;
  border-radius: 12px;
}

.plan-item p {
  margin: 0 0 8px 0;
  font-size: 12px;
  color: #999;
}

.plan-meta {
  font-size: 11px;
  color: #bbb;
}

/* 最近课程 */
.courses-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.course-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 12px;
  background: #f8f9fa;
  border-radius: 6px;
  border-left: 3px solid #667eea;
}

.course-name {
  font-size: 14px;
  font-weight: 500;
  color: #333;
}

.course-info {
  display: flex;
  gap: 12px;
  align-items: center;
}

.course-credit {
  font-size: 12px;
  color: #999;
}

.course-grade {
  font-size: 14px;
  font-weight: 600;
  color: #667eea;
  padding: 2px 8px;
  background: #f0f2f8;
  border-radius: 4px;
}

/* 学业建议 */
.suggestion-item {
  display: flex;
  gap: 12px;
  padding: 15px 0;
  border-bottom: 1px solid #f0f0f0;
}

.suggestion-item:last-child {
  border-bottom: none;
}

.suggestion-icon {
  font-size: 24px;
  flex-shrink: 0;
}

.suggestion-content h4 {
  margin: 0 0 5px 0;
  font-size: 14px;
  color: #333;
}

.suggestion-content p {
  margin: 0;
  font-size: 12px;
  color: #999;
}

/* 快速操作 */
.quick-actions-card .card-body {
  padding: 20px;
}

.quick-action-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 12px;
}

.quick-action-btn {
  background: white;
  border-radius: 16px;
  padding: 28px;
  text-align: center;
  text-decoration: none;
  color: #333;
  box-shadow: 0 6px 20px rgba(0, 0, 0, 0.08);
  transition: all 0.4s cubic-bezier(0.4, 0, 0.2, 1);
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  gap: 12px;
  border: 2px solid transparent;
}

.quick-action-btn:hover {
  box-shadow: 0 12px 32px rgba(79, 172, 254, 0.25);
  transform: translateY(-8px) scale(1.02);
  border-color: #4facfe;
  background: linear-gradient(135deg, rgba(79, 172, 254, 0.05) 0%, rgba(0, 242, 254, 0.05) 100%);
  color: #4facfe;
}

.action-icon {
  font-size: 32px;
  line-height: 1;
}

.quick-action-btn div:last-child {
  font-size: 14px;
  font-weight: 500;
}

/* 右侧列宽度 */
.right-column {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.left-column {
  display: flex;
  flex-direction: column;
}

/* 响应式 */
@media (max-width: 1400px) {
  .content-section {
    grid-template-columns: 1fr;
  }

  .left-column,
  .right-column {
    display: grid;
    grid-template-columns: 1fr 1fr;
    gap: 20px;
  }

  .left-column > .card:first-child {
    grid-column: 1 / -1;
  }
}

@media (max-width: 1024px) {
  .content-section {
    grid-template-columns: 1fr;
  }

  .metrics-grid {
    grid-template-columns: repeat(2, 1fr);
  }

  .left-column,
  .right-column {
    display: flex;
    flex-direction: column;
    gap: 20px;
  }
}

@media (max-width: 768px) {
  .student-dashboard {
    padding: 12px;
  }

  .welcome-stats {
    flex-direction: column;
    gap: 15px;
  }

  .stat-divider {
    width: 100%;
    height: 1px;
  }

  .metrics-grid {
    grid-template-columns: 1fr;
  }

  .quick-action-grid {
    grid-template-columns: repeat(2, 1fr);
  }
}
</style>
