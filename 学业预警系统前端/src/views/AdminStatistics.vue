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
        <div class="stat-value">{{ stats.redWarnings || 0 }}</div>
        <div class="stat-label">红色预警</div>
      </el-card>
      <el-card class="stat-card">
        <div class="stat-value">{{ stats.yellowWarnings || 0 }}</div>
        <div class="stat-label">黄色预警</div>
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
  totalStudents: 1200,
  totalWarnings: 98,
  processedWarnings: 45,
  assistancePlans: 67
})

onMounted(async () => {
  await loadStatistics()
})

const loadStatistics = async () => {
  try {
    const response = await adminAPI.getStatistics()
    if (response && response.code === 0) {
      stats.value = response.data || {}
    } else if (response && response.stats) {
      stats.value = response.stats
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
}
.stat-value {
  font-size: 32px;
  font-weight: bold;
  color: #409eff;
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
