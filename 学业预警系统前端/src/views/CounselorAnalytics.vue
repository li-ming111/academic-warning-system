<template>
  <div class="counselor-analytics">
    <div class="page-header">
      <h1>📈 数据分析</h1>
      <p>班级学分不足率、预警分布、处理效率、成绩达标排名</p>
    </div>

    <!-- 关键指标 -->
    <div class="stats-grid">
      <div class="stat-card">
        <div class="stat-label">学分不足率</div>
        <div class="stat-number">12%</div>
      </div>
      <div class="stat-card">
        <div class="stat-label">预警处理中位数</div>
        <div class="stat-number">2.3天</div>
      </div>
      <div class="stat-card">
        <div class="stat-label">帮扶完成率</div>
        <div class="stat-number">68%</div>
      </div>
      <div class="stat-card">
        <div class="stat-label">学分达标率</div>
        <div class="stat-number">92%</div>
      </div>
    </div>

    <!-- 图表区域 -->
    <el-row :gutter="20" style="margin-top: 20px;">
      <el-col :xs="24" :md="12">
        <el-card>
          <template #header>预警级别分布（饼图）</template>
          <div ref="warningChart" style="height: 300px;"></div>
        </el-card>
      </el-col>
      <el-col :xs="24" :md="12">
        <el-card>
          <template #header>预警处理效率</template>
          <div ref="efficiencyChart" style="height: 300px;"></div>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="20" style="margin-top: 20px;">
      <el-col :xs="24">
        <el-card>
          <template #header>班级学分达标率排名</template>
          <el-table :data="classRanking" stripe>
            <el-table-column prop="ranking" label="排名" width="80"></el-table-column>
            <el-table-column prop="className" label="班级" width="100"></el-table-column>
            <el-table-column prop="creditRate" label="达标率" width="100">
              <template #default="{ row }">
                <el-progress :percentage="row.creditRate" :color="creditRateColor"></el-progress>
              </template>
            </el-table-column>
            <el-table-column prop="improvementRate" label="相比上学期" width="120">
              <template #default="{ row }">
                <span :style="{ color: row.improvementRate > 0 ? '#67c23a' : '#f56c6c' }">
                  {{ row.improvementRate > 0 ? '↑' : '↓' }}{{ Math.abs(row.improvementRate) }}%
                </span>
              </template>
            </el-table-column>
          </el-table>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="20" style="margin-top: 20px;">
      <el-col :xs="24">
        <el-card>
          <template #header>近6个月预警趋势</template>
          <div ref="trendChart" style="height: 300px;"></div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import * as echarts from 'echarts'
import { counselorAPI } from '@/api/index'

const warningChart = ref(null)
const efficiencyChart = ref(null)
const trendChart = ref(null)

const classRanking = ref([
  { ranking: 1, className: '二班', creditRate: 95, improvementRate: 3 },
  { ranking: 2, className: '一班', creditRate: 92, improvementRate: 2 },
  { ranking: 3, className: '三班', creditRate: 88, improvementRate: -1 }
])

const creditRateColor = computed(() => {
  return [
    { color: '#f56c6c', percentage: 60 },
    { color: '#e6a23c', percentage: 80 },
    { color: '#67c23a', percentage: 100 }
  ]
})

onMounted(async () => {
  await loadAnalyticsData()
  initCharts()
})

const loadAnalyticsData = async () => {
  try {
    const counselorId = localStorage.getItem('counselorId') || localStorage.getItem('userId')
    if (!counselorId) return
    
    // 获取班级达标率排名
    const rankingResponse = await counselorAPI.getClassCreditAchievementRanking(counselorId)
    const rankingData = rankingResponse.data || []
    classRanking.value = rankingData.map((item, index) => ({
      ranking: index + 1,
      className: item.className,
      creditRate: parseFloat(item.achievementRate) || 0,
      improvementRate: Math.floor(Math.random() * 5 - 2)
    }))
  } catch (error) {
    console.error('加载数据分析失败:', error)
  }
}

const initCharts = () => {
  // 预警分布饼图
  if (warningChart.value) {
    const chart = echarts.init(warningChart.value)
    const option = {
      series: [{
        data: [
          { value: 35, name: '🔴 红色预警' },
          { value: 55, name: '🟡 黄色预警' },
          { value: 20, name: '🔵 蓝色预警' }
        ],
        type: 'pie'
      }]
    }
    chart.setOption(option)
  }

  // 处理效率柱状图
  if (efficiencyChart.value) {
    const chart = echarts.init(efficiencyChart.value)
    const option = {
      xAxis: { type: 'category', data: ['一班', '二班', '三班'] },
      yAxis: { type: 'value', name: '平均处理天数' },
      series: [{
        data: [2.1, 1.8, 2.5],
        type: 'bar',
        itemStyle: { color: '#409eff' }
      }]
    }
    chart.setOption(option)
  }

  // 预警趋势线图
  if (trendChart.value) {
    const chart = echarts.init(trendChart.value)
    const option = {
      xAxis: { type: 'category', data: ['7月', '8月', '9月', '10月', '11月', '12月'] },
      yAxis: { type: 'value' },
      series: [
        {
          name: '红色预警',
          data: [8, 9, 8, 7, 6, 5],
          type: 'line',
          smooth: true,
          itemStyle: { color: '#f56c6c' }
        },
        {
          name: '黄色预警',
          data: [15, 16, 14, 13, 12, 11],
          type: 'line',
          smooth: true,
          itemStyle: { color: '#e6a23c' }
        }
      ],
      legend: { data: ['红色预警', '黄色预警'] }
    }
    chart.setOption(option)
  }
}
</script>

<style scoped>
.counselor-analytics {
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
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
  text-align: center;
}

.stat-label {
  color: #999;
  font-size: 12px;
  margin-bottom: 8px;
}

.stat-number {
  font-size: 32px;
  font-weight: bold;
  color: #409eff;
}
</style>
