<template>
  <div class="teacher-dashboard">
    <!-- 欢迎卡片 -->
    <div class="welcome-card">
      <div class="welcome-animation"></div>
      <div class="welcome-content">
        <div class="greeting-icon">👨‍🏫</div>
        <h1>欢迎回来，{{ teacherName }}！</h1>
        <p>{{ currentDate }} | 管理您的教学数据</p>
      </div>
    </div>

    <!-- 教学数据概览 -->
    <div class="stats-section">
      <h2 class="section-title">📊 教学数据概览</h2>
      <div class="stats-grid">
        <div class="stat-card">
          <div class="stat-icon">👥</div>
          <div class="stat-content">
            <div class="stat-label">管理学生</div>
            <div class="stat-number">{{ teacherData.totalStudents || 0 }}</div>
            <div class="stat-detail">人</div>
          </div>
        </div>
        <div class="stat-card">
          <div class="stat-icon">📚</div>
          <div class="stat-content">
            <div class="stat-label">授课课程</div>
            <div class="stat-number">{{ teacherData.totalCourses || 0 }}</div>
            <div class="stat-detail">门</div>
          </div>
        </div>
        <div class="stat-card warning">
          <div class="stat-icon">🔴</div>
          <div class="stat-content">
            <div class="stat-label">红色预警</div>
            <div class="stat-number">{{ teacherData.highWarnings || 0 }}</div>
            <div class="stat-detail">需处理</div>
          </div>
        </div>
        <div class="stat-card">
          <div class="stat-icon">🟡</div>
          <div class="stat-content">
            <div class="stat-label">黄色预警</div>
            <div class="stat-number">{{ teacherData.mediumWarnings || 0 }}</div>
            <div class="stat-detail">需关注</div>
          </div>
        </div>
      </div>
    </div>

    <!-- 待办事项和预警 -->
    <div class="content-grid">
      <!-- 待办事项 -->
      <div class="card-section">
        <div class="section-header">
          <h2 class="section-title">📋 待办事项</h2>
          <el-badge :value="3" class="badge"></el-badge>
        </div>
        <div class="todo-list">
          <div v-if="!teacherData.todos || teacherData.todos.length === 0" class="empty-state">暂无待办事项</div>
          <div v-for="todo in (teacherData.todos || []).slice(0, 3)" :key="todo.id" class="todo-item">
            <el-checkbox :checked="todo.completed"></el-checkbox>
            <div class="todo-content">
              <h4>{{ todo.title }}</h4>
              <p>{{ todo.description }}</p>
            </div>
            <el-button type="primary" size="small" link>处理</el-button>
          </div>
        </div>
      </div>

      <!-- 红色预警学生 -->
      <div class="card-section">
        <div class="section-header">
          <h2 class="section-title">🔴 红色预警</h2>
          <router-link to="/teacher/warnings" class="view-all">查看全部 →</router-link>
        </div>
        <div class="warning-list">
          <div v-if="!teacherData.warnings || teacherData.warnings.length === 0" class="empty-state">暂无红色预警</div>
          <div v-for="warning in (teacherData.warnings || []).slice(0, 2)" :key="warning.id" class="warning-item">
            <div class="warning-header">
              <span class="student-name">{{ warning.studentName }}</span>
              <span class="course-name">{{ warning.courseName }}</span>
            </div>
            <p class="warning-score">成绩：{{ warning.score }}分</p>
            <div class="warning-actions">
              <el-button type="primary" size="small" link>处理</el-button>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 快速操作 -->
    <div class="quick-actions">
      <h2 class="section-title">⚡ 快速操作</h2>
      <div class="action-buttons">
        <router-link to="/teacher/scores" class="action-btn">
          <span>📝</span>
          <span>成绩录入</span>
        </router-link>
        <router-link to="/teacher/warnings" class="action-btn">
          <span>⚠️</span>
          <span>预警管理</span>
        </router-link>
        <router-link to="/teacher/analysis" class="action-btn">
          <span>📊</span>
          <span>性能分析</span>
        </router-link>
        <router-link to="/teacher/credit-prediction" class="action-btn">
          <span>📚</span>
          <span>学分预测</span>
        </router-link>
        <router-link to="/teacher/audit-log" class="action-btn">
          <span>📋</span>
          <span>审计日志</span>
        </router-link>
        <router-link to="/teacher/feedback-management" class="action-btn">
          <span>💬</span>
          <span>反馈管理</span>
        </router-link>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { teacherAPI } from '@/api/index'
import { getUserId } from '@/utils/userUtils'

const teacherName = ref('教师')
const currentDate = ref('')
const teacherData = ref({
  totalStudents: 0,
  totalCourses: 0,
  highWarnings: 0,
  mediumWarnings: 0,
  todos: [],
  warnings: []
})

onMounted(async () => {
  const storedName = localStorage.getItem('username')
  if (storedName) {
    teacherName.value = storedName
  }

  const today = new Date()
  const options = { year: 'numeric', month: 'long', day: 'numeric', weekday: 'long' }
  currentDate.value = today.toLocaleDateString('zh-CN', options)

  await loadTeacherDashboard()
})

// 加载教师看板数据
const loadTeacherDashboard = async () => {
  try {
    const userId = getUserId()
    if (!userId) return
    const response = await teacherAPI.getDashboard(userId)
    if (response) {
      teacherData.value = response
    }
    // 加载待办事项
    const teacherId = localStorage.getItem('teacherId') || userId
    const todosResponse = await teacherAPI.getTodos(teacherId)
    if (todosResponse) {
      // 处理待办事项数据
      const todos = []
      if (todosResponse.pendingWarnings > 0) {
        todos.push({
          id: 1,
          title: '未处理的预警',
          description: `有${todosResponse.pendingWarnings}个学生预警待处理`,
          completed: false
        })
      }
      if (todosResponse.pendingFeedbacks > 0) {
        todos.push({
          id: 2,
          title: '待回复的学生反馈',
          description: `有${todosResponse.pendingFeedbacks}条学生反馈待回复`,
          completed: false
        })
      }
      if (todosResponse.pendingAppeals > 0) {
        todos.push({
          id: 3,
          title: '成绩申诋',
          description: `有${todosResponse.pendingAppeals}条成绩申诋待处理`,
          completed: false
        })
      }
      teacherData.value.todos = todos
    }
  } catch (error) {
    console.error('加载教师看板失败:', error)
  }
}
</script>

<style scoped>
.teacher-dashboard {
  padding: 20px;
  background: linear-gradient(135deg, #f5f7fa 0%, #e8ecf1 100%);
  min-height: 100vh;
}

/* 欢迎卡片 */
.welcome-card {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border-radius: 16px;
  padding: 45px;
  margin-bottom: 30px;
  color: white;
  box-shadow: 0 12px 40px rgba(102, 126, 234, 0.5);
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
  position: relative;
  z-index: 1;
}

.greeting-icon {
  font-size: 40px;
  margin-bottom: 15px;
}

.welcome-content h1 {
  margin: 0;
  font-size: 32px;
  font-weight: bold;
}

.welcome-content p {
  margin: 12px 0 0 0;
  font-size: 14px;
  opacity: 0.95;
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
  grid-template-columns: repeat(auto-fit, minmax(180px, 1fr));
  gap: 20px;
}

.stat-card {
  background: white;
  border-radius: 16px;
  padding: 28px;
  display: flex;
  align-items: center;
  gap: 18px;
  box-shadow: 0 6px 20px rgba(0, 0, 0, 0.08);
  transition: all 0.4s cubic-bezier(0.4, 0, 0.2, 1);
  border: 2px solid transparent;
}

.stat-card:hover {
  box-shadow: 0 12px 32px rgba(0, 0, 0, 0.15);
  transform: translateY(-8px);
  border-color: #667eea;
}

.stat-card.warning {
  border-left: 4px solid #e6a23c;
}

.stat-icon {
  width: 64px;
  height: 64px;
  border-radius: 14px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 32px;
  flex-shrink: 0;
  background: linear-gradient(135deg, rgba(102, 126, 234, 0.1) 0%, rgba(118, 75, 162, 0.1) 100%);
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
  font-size: 32px;
  font-weight: bold;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.stat-detail {
  font-size: 12px;
  color: #999;
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
  border-radius: 16px;
  padding: 28px;
  box-shadow: 0 6px 20px rgba(0, 0, 0, 0.08);
  border: 2px solid transparent;
  transition: all 0.3s ease;
}

.card-section:hover {
  border-color: #e8ecf1;
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.12);
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.badge {
  margin-left: 10px;
}

.view-all {
  color: #409eff;
  text-decoration: none;
  font-size: 13px;
}

/* 待办列表 */
.todo-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.todo-item {
  display: flex;
  align-items: flex-start;
  gap: 12px;
  padding: 12px;
  background: #f9fafb;
  border-radius: 8px;
  border-left: 3px solid #409eff;
}

.todo-content {
  flex: 1;
}

.todo-content h4 {
  margin: 0 0 4px 0;
  color: #333;
}

.todo-content p {
  margin: 0;
  font-size: 13px;
  color: #666;
}

/* 预警列表 */
.warning-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.warning-item {
  padding: 12px;
  background: #fdf6ec;
  border-radius: 8px;
  border-left: 3px solid #e6a23c;
}

.warning-header {
  display: flex;
  gap: 10px;
  margin-bottom: 8px;
}

.student-name {
  font-weight: bold;
  color: #333;
}

.course-name {
  color: #666;
  font-size: 13px;
}

.warning-score {
  margin: 0 0 8px 0;
  font-size: 13px;
  color: #e6a23c;
  font-weight: bold;
}

/* 快速操作 */
.quick-actions {
  margin-top: 30px;
}

.action-buttons {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(120px, 1fr));
  gap: 15px;
}

.action-btn {
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

.action-btn:hover {
  box-shadow: 0 12px 32px rgba(102, 126, 234, 0.25);
  transform: translateY(-8px) scale(1.02);
  border-color: #667eea;
  background: linear-gradient(135deg, rgba(102, 126, 234, 0.05) 0%, rgba(118, 75, 162, 0.05) 100%);
}

.action-btn span:first-child {
  font-size: 32px;
}

.action-btn span:last-child {
  font-size: 14px;
  font-weight: 500;
}
</style>
