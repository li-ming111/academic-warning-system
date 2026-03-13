<template>
  <div class="counselor-dashboard">
    <div class="page-header">
      <h1>学生情况概览</h1>
      <p>班级预警统计和学生管理中心</p>
    </div>

    <!-- 统计卡片 -->
    <div class="stats-grid">
      <div class="stat-card">
        <div class="stat-icon"></div>
        <div class="stat-content">
          <div class="stat-label">管理学生</div>
          <div class="stat-number">{{ counselorStats.studentCount || 0 }}</div>
        </div>
      </div>
      <div class="stat-card">
        <div class="stat-icon"></div>
        <div class="stat-content">
          <div class="stat-label">预警总数</div>
          <div class="stat-number">{{ counselorStats.warningCount || 0 }}</div>
        </div>
      </div>
      <div class="stat-card warning">
        <div class="stat-icon"></div>
        <div class="stat-content">
          <div class="stat-label">红色预警</div>
          <div class="stat-number">{{ counselorStats.redWarnings || 0 }}</div>
        </div>
      </div>
      <div class="stat-card">
        <div class="stat-icon"></div>
        <div class="stat-content">
          <div class="stat-label">黄色预警</div>
          <div class="stat-number">{{ counselorStats.yellowWarnings || 0 }}</div>
        </div>
      </div>
    </div>

    <!-- 预警趋势图 -->
    <el-card style="margin-bottom: 20px;">
      <template #header>
        <div class="card-header">预警趋势（迖6个月）</div>
      </template>
      <div ref="trendChart" style="height: 300px;"></div>
    </el-card>

    <!-- 快速操作 -->
    <div class="quick-actions">
      <h2 class="section-title">快速操作</h2>
      <div class="action-buttons">
        <router-link to="/counselor/students" class="action-btn">
          <span></span>
          <span>学生管理</span>
        </router-link>
        <router-link to="/counselor/warnings" class="action-btn">
          <span></span>
          <span>预警管理</span>
        </router-link>
        <router-link to="/counselor/courses" class="action-btn">
          <span></span>
          <span>选修课管理</span>
        </router-link>
        <router-link to="/counselor/credit-monitor" class="action-btn">
          <span></span>
          <span>学分监控</span>
        </router-link>
        <router-link to="/counselor/notifications" class="action-btn">
          <span></span>
          <span>批量通知</span>
        </router-link>
      </div>
    </div>

    <!-- 待办事项 -->
    <el-card style="margin-top: 20px;">
      <template #header>
        <div class="card-header">待办事项 ({{ todos.length }})</div>
      </template>
      <div class="todo-list" v-loading="loading">
        <div v-if="todos.length === 0" class="empty-todos">
          <el-empty description="暂无待办事项"></el-empty>
        </div>
        <div class="todo-item" v-for="todo in todos" :key="todo.id">
          <el-checkbox :checked="false"></el-checkbox>
          <div class="todo-content">
            <h4>{{ todo.title }}</h4>
            <p>{{ todo.description }}</p>
          </div>
          <el-button type="primary" size="small" link>处理</el-button>
        </div>
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import * as echarts from 'echarts'
import { counselorAPI } from '@/api/index'
import { getUserId } from '@/utils/userUtils'

const trendChart = ref(null)
const counselorStats = ref({
  studentCount: 0,
  warningCount: 0,
  redWarnings: 0,
  yellowWarnings: 0,
  blueWarnings: 0
})
const todos = ref([])
const loading = ref(false)

onMounted(async () => {
  await loadDashboard()
  await loadTodos()
})

const loadDashboard = async () => {
  try {
    const userId = getUserId()
    if (!userId) return
    const response = await counselorAPI.getDashboard(userId)
    if (response.code === 200) {
      const data = response.data
      counselorStats.value = {
        studentCount: data.studentCount || 0,
        warningCount: data.warningCount || 0,
        redWarnings: data.redWarnings || 0,
        yellowWarnings: data.yellowWarnings || 0
      }
    }
    // 绘制图表
    setTimeout(() => {
      if (trendChart.value) {
        const chart = echarts.init(trendChart.value)
        const option = {
          xAxis: { type: 'category', data: ['1月', '2月', '3月', '4月', '5月', '6月'] },
          yAxis: { type: 'value' },
          series: [
            {
              name: '红色预警',
              data: [],
              type: 'line',
              smooth: true,
              itemStyle: { color: '#f56c6c' }
            },
            {
              name: '黄色预警',
              data: [],
              type: 'line',
              smooth: true,
              itemStyle: { color: '#e6a23c' }
            }
          ],
          legend: { data: ['红色预警', '黄色预警'] }
        }
        chart.setOption(option)
      }
    }, 200)
  } catch (error) {
    console.error('加载看板失败:', error)
  }
}

const loadTodos = async () => {
  loading.value = true
  try {
    const userId = getUserId()
    if (!userId) return
    // 这里可以添加获取待办事项的API调用
    // 暂时设置为空数组
    todos.value = []
  } catch (error) {
    console.error('加载待办事项失败:', error)
  } finally {
    loading.value = false
  }
}

</script>

<style scoped>
.counselor-dashboard {
  padding: 20px;
}

.page-header {
  margin-bottom: 20px;
}

.page-header h1 {
  margin: 0 0 8px 0;
  font-size: 24px;
  color: #333;
}

.page-header p {
  margin: 0;
  color: #999;
  font-size: 14px;
}

.stats-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
  gap: 15px;
  margin-bottom: 20px;
}

.stat-card {
  background: white;
  border-radius: 8px;
  padding: 20px;
  display: flex;
  align-items: center;
  gap: 15px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
  transition: all 0.3s ease;
}

.stat-card:hover {
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.12);
}

.stat-card.warning {
  border-left: 4px solid #f56c6c;
}

.stat-icon {
  font-size: 32px;
}

.stat-content {
  flex: 1;
}

.stat-label {
  color: #999;
  font-size: 12px;
  margin-bottom: 4px;
}

.stat-number {
  font-size: 24px;
  font-weight: bold;
  color: #409eff;
}

.section-title {
  margin: 0 0 15px 0;
  font-size: 16px;
  font-weight: bold;
  color: #333;
}

.quick-actions {
  margin-bottom: 20px;
}

.action-buttons {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(120px, 1fr));
  gap: 15px;
}

.action-btn {
  background: white;
  border-radius: 8px;
  padding: 20px;
  text-align: center;
  text-decoration: none;
  color: #333;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
  transition: all 0.3s ease;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 8px;
}

.action-btn:hover {
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.12);
  transform: translateY(-5px);
  color: #409eff;
}

.action-btn span:first-child {
  font-size: 28px;
}

.card-header {
  font-size: 16px;
  font-weight: bold;
  color: #333;
}

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
</style>
