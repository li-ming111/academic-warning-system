<template>
  <div class="admin-statistics">
    <div class="stats-cards">
      <el-card class="stat-card">
        <div class="stat-value">{{ stats.totalStudents || 0 }}</div>
        <div class="stat-label">总学生数</div>
      </el-card>
      <el-card class="stat-card">
        <div class="stat-value">{{ stats.totalWarnings || 0 }}</div>
        <div class="stat-label">总预警数</div>
      </el-card>
      <el-card class="stat-card">
        <div class="stat-value">{{ stats.totalTeachers || 0 }}</div>
        <div class="stat-label">教师总数</div>
      </el-card>
      <el-card class="stat-card">
        <div class="stat-value">{{ stats.totalCourses || 0 }}</div>
        <div class="stat-label">课程总数</div>
      </el-card>
    </div>

    <el-card>
      <template #header>
        <div class="card-header">
          <span>预警数据统计</span>
          <div>
            <el-button @click="exportStats('excel')">Excel导出</el-button>
            <el-button @click="exportStats('pdf')">PDF导出</el-button>
          </div>
        </div>
      </template>
      <div ref="chartContainer" style="height: 400px;"></div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import * as echarts from 'echarts'
import { adminAPI } from '@/api/index'
import { ElMessage } from 'element-plus'

const chartContainer = ref(null)
const stats = ref({
  totalStudents: 0,
  totalWarnings: 0,
  redWarnings: 0,
  yellowWarnings: 0,
  totalTeachers: 0,
  totalColleges: 0,
  highWarnings: 0,
  mediumWarnings: 0,
  lowWarnings: 0
})

onMounted(async () => {
  await loadStatistics()
})

const loadStatistics = async () => {
  try {
    const response = await adminAPI.getStatistics()
    console.log('统计数据响应:', response)
    console.log('响应的字段:', Object.keys(response || {}))
    // 启湇断器已-解包，响应已是data对象
    if (response && typeof response === 'object') {
      stats.value = {
        totalStudents: response.totalStudents || 0,
        totalWarnings: response.totalWarnings || 0,
        redWarnings: response.redWarnings || 0,
        yellowWarnings: response.yellowWarnings || 0,
        totalTeachers: response.totalTeachers || 0,
        totalColleges: response.totalColleges || 0,
        highWarnings: response.highWarnings || response.redWarnings || 0,
        mediumWarnings: response.mediumWarnings || response.yellowWarnings || 0,
        lowWarnings: response.lowWarnings || 0
      }
    }
    initChart()
  } catch (error) {
    console.error('加载统计数据失败:', error)
    initChart()
  }
}

const initChart = () => {
  if (chartContainer.value) {
    const chart = echarts.init(chartContainer.value)
    const option = {
      series: [{
        data: [
          { value: stats.value.highWarnings || 45, name: '🔴 红色预警' },
          { value: stats.value.mediumWarnings || 32, name: '🟡 黄色预警' },
          { value: stats.value.lowWarnings || 21, name: '🔵 蓝色预警' }
        ],
        type: 'pie'
      }]
    }
    chart.setOption(option)
  }
}

const exportStats = async (type) => {
  try {
    await adminAPI.exportStatistics(type)
    ElMessage.success('报表已导出')
  } catch (error) {
    console.error('导出失败:', error)
  }
}
</script>

<style scoped>
.admin-statistics {
  padding: 20px;
}
.stats-cards {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 20px;
  margin-bottom: 20px;
}
.stat-card {
  text-align: center;
  border: 1px solid #e9ecef !important;
}
.stat-value {
  font-size: 32px;
  font-weight: bold;
  color: #667eea;
}
.stat-label {
  font-size: 14px;
  color: #909399;
  margin-top: 8px;
}
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
</style>
