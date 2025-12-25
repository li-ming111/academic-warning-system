<template>
  <div class="counselor-scores">
    <div class="page-header">
      <h1>📊 学生成绩跟踪</h1>
      <p>按课程查看班级成绩分布、识别低分学生、跟踪成绩变化</p>
    </div>

    <!-- 课程选择和统计 -->
    <div class="filter-bar">
      <el-select v-model="selectedCourse" placeholder="选择课程" style="width: 200px;">
        <el-option label="高等数学" value="1"></el-option>
        <el-option label="线性代数" value="2"></el-option>
        <el-option label="数据库系统" value="3"></el-option>
      </el-select>
      <el-button type="primary" @click="analyzeScores">分析</el-button>
    </div>

    <!-- 成绩分布统计 -->
    <el-row :gutter="20" style="margin-bottom: 20px;">
      <el-col :xs="24" :sm="12" :md="6">
        <el-card>
          <template #header>平均成绩</template>
          <div style="font-size: 32px; color: #409eff; font-weight: bold;">{{ scoreStats.average }}分</div>
        </el-card>
      </el-col>
      <el-col :xs="24" :sm="12" :md="6">
        <el-card>
          <template #header>及格率</template>
          <div style="font-size: 32px; color: #67c23a; font-weight: bold;">{{ scoreStats.passRate }}%</div>
        </el-card>
      </el-col>
      <el-col :xs="24" :sm="12" :md="6">
        <el-card>
          <template #header>低分学生</template>
          <div style="font-size: 32px; color: #f56c6c; font-weight: bold;">{{ scoreStats.lowScoreCount }}人</div>
        </el-card>
      </el-col>
      <el-col :xs="24" :sm="12" :md="6">
        <el-card>
          <template #header>学分达标</template>
          <div style="font-size: 32px; color: #e6a23c; font-weight: bold;">{{ scoreStats.creditRate }}%</div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 成绩分布图表 -->
    <el-card style="margin-bottom: 20px;">
      <template #header>
        <div class="card-header">成绩分布（柱状图）</div>
      </template>
      <div ref="chartContainer" style="height: 300px;"></div>
    </el-card>

    <!-- 低分学生列表 -->
    <el-card style="margin-bottom: 20px;">
      <template #header>
        <div class="card-header">低分学生预警</div>
      </template>
      <el-table :data="lowScoreStudents" stripe>
        <el-table-column prop="studentName" label="学生" width="100"></el-table-column>
        <el-table-column prop="studentId" label="学号" width="120"></el-table-column>
        <el-table-column prop="score" label="成绩" width="80"></el-table-column>
        <el-table-column prop="className" label="班级" width="100"></el-table-column>
        <el-table-column label="状态" width="100">
          <template #default="{ row }">
            <el-tag type="danger" v-if="row.score < 60">高风险</el-tag>
            <el-tag type="warning" v-else-if="row.score < 70">中风险</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="150">
          <template #default="{ row }">
            <el-button type="primary" size="small" link @click="viewTrend(row)">查看趋势</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <!-- 成绩趋势对话框 -->
    <el-dialog v-model="trendDialogVisible" title="成绩变化趋势" width="600px">
      <div ref="trendChartContainer" style="height: 300px;"></div>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { counselorAPI } from '@/api/index'
import { getUserId } from '@/utils/userUtils'

const selectedCourse = ref('1')
const scoreStats = ref({
  average: 82,
  passRate: 88,
  lowScoreCount: 3,
  creditRate: 92
})
const lowScoreStudents = ref([])
const chartContainer = ref(null)
const trendChartContainer = ref(null)
const trendDialogVisible = ref(false)

onMounted(async () => {
  await loadScores()
  initCharts()
})

const loadScores = async () => {
  try {
    const counselorId = localStorage.getItem('counselorId') || getUserId()

    // 获取低分学生列表
    const response = await counselorAPI.getLowScoreStudents(counselorId)
    lowScoreStudents.value = response.data || []
    
    // 更新统计数据
    if (selectedCourse.value) {
      const statsResponse = await counselorAPI.getCourseScoreDistribution(selectedCourse.value)
      if (statsResponse.data) {
        const data = statsResponse.data
        scoreStats.value = {
          average: Math.round((data.excellent * 95 + data.good * 85 + data.normal * 75 + data.pass * 65) / (data.total || 1)),
          passRate: data.total > 0 ? Math.round(((data.excellent + data.good + data.normal + data.pass) / data.total) * 100) : 0,
          lowScoreCount: data.fail || 0,
          creditRate: 92
        }
      }
    }
  } catch (error) {
    console.error('加载成绩数据失败:', error)
  }
}

const analyzeScores = () => {
  ElMessage.success('成绩已分析')
  initCharts()
}

const initCharts = () => {
  if (chartContainer.value) {
    const chart = echarts.init(chartContainer.value)
    const option = {
      xAxis: { type: 'category', data: ['50-60', '60-70', '70-80', '80-90', '90-100'] },
      yAxis: { type: 'value' },
      series: [{
        data: [],
        type: 'bar',
        itemStyle: { color: '#409eff' }
      }]
    }
    chart.setOption(option)
  }
}

const viewTrend = (row) => {
  trendDialogVisible.value = true
  setTimeout(() => {
    if (trendChartContainer.value) {
      const chart = echarts.init(trendChartContainer.value)
      const option = {
        xAxis: { type: 'category', data: ['秋季', '春季', '秋季', '春季'] },
        yAxis: { type: 'value' },
        series: [{
          data: [],
          type: 'line',
          smooth: true,
          itemStyle: { color: '#67c23a' }
        }]
      }
      chart.setOption(option)
    }
  }, 100)
}
</script>

<style scoped>
.counselor-scores {
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

.filter-bar {
  display: flex;
  gap: 10px;
  margin-bottom: 20px;
}

.card-header {
  font-size: 16px;
  font-weight: bold;
  color: #333;
}
</style>
