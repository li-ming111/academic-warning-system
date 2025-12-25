<template>
  <div class="student-dashboard">
    <!-- 顶部欢迎区域 -->
    <div class="welcome-section">
      <div class="welcome-card">
        <div class="welcome-header">
          <div class="welcome-text">
            <h1>欢迎回来</h1>
            <p>{{ username }}，{{ getGreeting() }}</p>
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
          <div class="metric-icon">📊</div>
          <div class="metric-content">
            <div class="metric-label">累计GPA</div>
            <div class="metric-value">{{ academicData.gpa }}</div>
            <div class="metric-trend">较上学期 ↑ 0.2</div>
          </div>
        </div>

        <!-- 学分卡片 -->
        <div class="metric-card credits-metric">
          <div class="metric-icon">🎓</div>
          <div class="metric-content">
            <div class="metric-label">已修学分</div>
            <div class="metric-value">{{ academicData.totalCourses }}</div>
            <div class="metric-trend">本学期 {{ academicData.semesterCourses }} 门课</div>
          </div>
        </div>

        <!-- 预警卡片 -->
        <div class="metric-card warning-metric">
          <div class="metric-icon">⚠️</div>
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
          <div class="metric-icon">💡</div>
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
            <div class="courses-list">
              <div class="course-item">
                <div class="course-name">高等数学</div>
                <div class="course-info">
                  <span class="course-credit">4学分</span>
                  <span class="course-grade">87分</span>
                </div>
              </div>
              <div class="course-item">
                <div class="course-name">线性代数</div>
                <div class="course-info">
                  <span class="course-credit">3学分</span>
                  <span class="course-grade">92分</span>
                </div>
              </div>
              <div class="course-item">
                <div class="course-name">离散数学</div>
                <div class="course-info">
                  <span class="course-credit">3学分</span>
                  <span class="course-grade">88分</span>
                </div>
              </div>
              <div class="course-item">
                <div class="course-name">数据结构</div>
                <div class="course-info">
                  <span class="course-credit">4学分</span>
                  <span class="course-grade">95分</span>
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
              <div class="empty-icon">🌟</div>
              <p>暂无帮扶计划</p>
            </div>
            <div v-else v-for="plan in assistancePlans.slice(0, 2)" :key="plan.id" class="plan-item">
              <div class="plan-header">
                <h4>{{ plan.teacherName }}的帮扶</h4>
                <span class="plan-status">进行中</span>
              </div>
              <p>{{ plan.content }}</p>
              <div class="plan-meta">{{ formatDate(plan.startDate) }}</div>
            </div>
          </div>
        </div>

        <!-- 学业建议 -->
        <div class="card suggestions-card">
          <div class="card-header">
            <h2>学业建议</h2>
          </div>
          <div class="card-body">
            <div class="suggestion-item">
              <div class="suggestion-icon">📚</div>
              <div class="suggestion-content">
                <h4>加强基础课程学习</h4>
                <p>高等数学成绩有提升空间，建议参加辅导课程</p>
              </div>
            </div>
            <div class="suggestion-item">
              <div class="suggestion-icon">⏰</div>
              <div class="suggestion-content">
                <h4>制定学习计划</h4>
                <p>建议制定周学习计划，提高学习效率</p>
              </div>
            </div>
            <div class="suggestion-item">
              <div class="suggestion-icon">👥</div>
              <div class="suggestion-content">
                <h4>参加学习小组</h4>
                <p>与同学组建学习小组，互相帮助共同进步</p>
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
                <div class="action-icon">📈</div>
                <div>查看成绩</div>
              </router-link>
              <router-link to="/student/appeals" class="quick-action-btn">
                <div class="action-icon">📝</div>
                <div>成绩申诉</div>
              </router-link>
              <router-link to="/student/messages" class="quick-action-btn">
                <div class="action-icon">💬</div>
                <div>消息</div>
              </router-link>
              <router-link to="/student/notification-center" class="quick-action-btn">
                <div class="action-icon">📢</div>
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

const router = useRouter()
const username = ref(localStorage.getItem('username') || '学生')

// 学业数据
const academicData = ref({
  gpa: 3.5,
  semesterCourses: 6,
  totalCourses: 45,
  warningCount: 0,
  warningLevel: '正常'
})

// 班级信息
const classInfo = ref({
  classIdentifier: '2023级电子信息工程1班',
  rankInClass: 12
})

// 预警信息
const warnings = ref([
  {
    id: 1,
    description: '高等数学有挂科风险',
    detail: '累计GPA偏低，建议加强学习',
    warningLevel: 2
  },
  {
    id: 2,
    description: '英语成绩不稳定',
    detail: '最近两次考试成绩波动较大',
    warningLevel: 1
  }
])

// 帮扶计划
const assistancePlans = ref([
  {
    id: 1,
    teacherName: '王老师',
    content: '数学辅导计划',
    startDate: '2025-12-20',
    status: 'active'
  },
  {
    id: 2,
    teacherName: '李老师',
    content: '英语强化训练',
    startDate: '2025-12-15',
    status: 'active'
  }
])

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

onMounted(() => {
  // 初始化数据
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
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border-radius: 12px;
  padding: 30px;
  color: white;
  box-shadow: 0 10px 30px rgba(102, 126, 234, 0.2);
}

.welcome-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 25px;
}

.welcome-text h1 {
  font-size: 28px;
  margin: 0 0 8px 0;
  font-weight: 600;
}

.welcome-text p {
  margin: 0;
  font-size: 14px;
  opacity: 0.9;
}

.welcome-avatar {
  width: 60px;
  height: 60px;
  border-radius: 50%;
  background: rgba(255, 255, 255, 0.2);
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 24px;
  font-weight: 600;
}

.welcome-stats {
  display: flex;
  justify-content: space-around;
  border-top: 1px solid rgba(255, 255, 255, 0.2);
  padding-top: 20px;
}

.stat {
  text-align: center;
  flex: 1;
}

.stat-value {
  font-size: 24px;
  font-weight: 700;
  margin-bottom: 5px;
}

.stat-label {
  font-size: 12px;
  opacity: 0.8;
}

.stat-divider {
  width: 1px;
  background: rgba(255, 255, 255, 0.2);
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
  border-radius: 10px;
  padding: 20px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
  display: flex;
  align-items: center;
  gap: 15px;
  transition: all 0.3s ease;
}

.metric-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 8px 16px rgba(0, 0, 0, 0.12);
}

.metric-icon {
  font-size: 32px;
  line-height: 1;
}

.metric-content {
  flex: 1;
}

.metric-label {
  font-size: 12px;
  color: #999;
  margin-bottom: 6px;
}

.metric-value {
  font-size: 24px;
  font-weight: 700;
  color: #667eea;
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
  border-radius: 10px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
  overflow: hidden;
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
}

.view-more {
  color: #667eea;
  text-decoration: none;
  font-size: 12px;
  transition: all 0.3s ease;
}

.view-more:hover {
  color: #764ba2;
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
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  gap: 8px;
  padding: 20px;
  background: #f8f9fa;
  border-radius: 8px;
  text-decoration: none;
  color: #333;
  transition: all 0.3s ease;
  border: 1px solid #e8ecf1;
}

.quick-action-btn:hover {
  background: #667eea;
  color: white;
  border-color: #667eea;
}

.action-icon {
  font-size: 28px;
  line-height: 1;
}

.quick-action-btn div:last-child {
  font-size: 12px;
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
