<template>
  <div class="teacher-credit-prediction">
    <div class="page-header">
      <h1>📚 学分达标预测</h1>
      <p>学生学分预测、达标率分析和课程推荐</p>
    </div>

    <!-- 课程选择 -->
    <div class="action-bar">
      <el-select v-model="selectedCourse" placeholder="选择课程" @change="loadCourseData">
        <el-option v-for="course in courses" :key="course.id" :label="course.name" :value="course.id"></el-option>
      </el-select>
    </div>

    <!-- 课程推荐 - 低分学生 -->
    <el-card style="margin-bottom: 20px;">
      <template #header>
        <div class="card-header">📋 课程推荐（低分学生辅导）</div>
      </template>

      <div class="recommendations-grid" v-if="recommendations.length > 0">
        <el-collapse>
          <el-collapse-item v-for="rec in recommendations" :key="rec.studentId">
            <template #title>
              <div style="display: flex; gap: 20px; width: 100%;">
                <span><strong>{{ rec.studentName }}</strong> (学号: {{ rec.studentId }})</span>
                <el-progress 
                  :percentage="rec.currentScore" 
                  :color="rec.currentScore < 50 ? '#f56c6c' : '#e6a23c'"
                  style="flex: 1; max-width: 200px;"
                ></el-progress>
                <el-tag :type="rec.recommendationType === 'strong' ? 'danger' : 'warning'">
                  {{ rec.recommendationType === 'strong' ? '强烈推荐' : '建议辅导' }}
                </el-tag>
              </div>
            </template>
            <div style="padding: 10px 0;">
              <p><strong>当前成绩：</strong>{{ rec.currentScore }} 分</p>
              <p><strong>推荐方案：</strong></p>
              <p style="margin-left: 20px; color: #666;">{{ rec.suggestions }}</p>
            </div>
          </el-collapse-item>
        </el-collapse>
      </div>
      <el-empty v-else description="暂无推荐记录"></el-empty>
    </el-card>

    <!-- 学分预测详情表格 -->
    <el-card style="margin-bottom: 20px;">
      <template #header>
        <div class="card-header">📊 学生学分预测详情</div>
      </template>

      <el-table :data="studentPredictions" stripe v-if="studentPredictions.length > 0">
        <el-table-column prop="studentName" label="学生名称" width="120"></el-table-column>
        <el-table-column prop="creditAchieved" label="已修学分" width="100"></el-table-column>
        <el-table-column prop="totalRequired" label="总要求" width="100"></el-table-column>
        <el-table-column label="达标率" width="150">
          <template #default="{ row }">
            <el-progress 
              :percentage="parseInt(row.currentRate)" 
              :color="row.riskLevel === 'high' ? '#f56c6c' : (row.riskLevel === 'medium' ? '#e6a23c' : '#67c23a')"
              style="width: 100%;"
            ></el-progress>
          </template>
        </el-table-column>
        <el-table-column label="预测达标率" width="150">
          <template #default="{ row }">
            <el-progress 
              :percentage="parseInt(row.predictedFinalRate)" 
              :color="row.predictedFinalRate < 80 ? '#e6a23c' : '#67c23a'"
              style="width: 100%;"
            ></el-progress>
          </template>
        </el-table-column>
        <el-table-column label="风险等级" width="100">
          <template #default="{ row }">
            <el-tag :type="row.riskLevel === 'high' ? 'danger' : (row.riskLevel === 'medium' ? 'warning' : 'success')">
              {{ row.riskLevel === 'high' ? '高风险' : (row.riskLevel === 'medium' ? '中风险' : '低风险') }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="100">
          <template #default="{ row }">
            <el-button type="primary" size="small" link @click="viewPrediction(row)">详情</el-button>
          </template>
        </el-table-column>
      </el-table>
      <el-empty v-else description="暂无预测数据"></el-empty>
    </el-card>

    <!-- 学分达标率统计 -->
    <el-card>
      <template #header>
        <div class="card-header">📈 班级学分达标率统计</div>
      </template>

      <div class="stats-grid">
        <div class="stat-box">
          <div class="stat-title">学生总数</div>
          <div class="stat-value">{{ studentPredictions.length }}</div>
          <div class="stat-hint">人</div>
        </div>
        <div class="stat-box">
          <div class="stat-title">高风险学生</div>
          <div class="stat-value" style="color: #f56c6c;">{{ highRiskCount }}</div>
          <div class="stat-hint">人</div>
        </div>
        <div class="stat-box">
          <div class="stat-title">中风险学生</div>
          <div class="stat-value" style="color: #e6a23c;">{{ mediumRiskCount }}</div>
          <div class="stat-hint">人</div>
        </div>
        <div class="stat-box">
          <div class="stat-title">低风险学生</div>
          <div class="stat-value" style="color: #67c23a;">{{ lowRiskCount }}</div>
          <div class="stat-hint">人</div>
        </div>
      </div>

      <div ref="chartContainer" style="height: 300px; margin-top: 20px;"></div>
    </el-card>

    <!-- 学生详情对话框 -->
    <el-dialog v-model="dialogVisible" title="学分预测详情" width="600px">
      <div v-if="selectedPrediction" class="prediction-detail">
        <p><strong>学生名称：</strong>{{ selectedPrediction.studentName || '未知' }}</p>
        <p><strong>已修学分：</strong>{{ selectedPrediction.creditAchieved }} / {{ selectedPrediction.totalRequired }}</p>
        <p><strong>当前达标率：</strong>{{ selectedPrediction.currentRate }}%</p>
        <p><strong>预测达标率：</strong>{{ selectedPrediction.predictedFinalRate }}%</p>
        <p><strong>优秀课程数：</strong>{{ selectedPrediction.excellentCount }}</p>
        <p><strong>及格课程数：</strong>{{ selectedPrediction.passCount }}</p>
        <p><strong>总课程数：</strong>{{ selectedPrediction.totalCourses }}</p>
        <p><strong>风险等级：</strong>
          <el-tag :type="selectedPrediction.riskLevel === 'high' ? 'danger' : (selectedPrediction.riskLevel === 'medium' ? 'warning' : 'success')">
            {{ selectedPrediction.riskLevel === 'high' ? '高风险' : (selectedPrediction.riskLevel === 'medium' ? '中风险' : '低风险') }}
          </el-tag>
        </p>
        <p v-if="selectedPrediction.riskLevel !== 'low'" style="color: #f56c6c; margin-top: 10px;">
          <strong>⚠️ 建议：</strong>
          <span v-if="selectedPrediction.riskLevel === 'high'">学生学分达标风险很高，建议立即进行个性化辅导和课程规划。</span>
          <span v-else>学生学分达标有风险，建议加强学习指导和监督。</span>
        </p>
      </div>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import * as echarts from 'echarts'
import { teacherAPI } from '@/api/index'
import { getUserId } from '@/utils/userUtils'
import { ElMessage } from 'element-plus'

const selectedCourse = ref('')
const courses = ref([])
const recommendations = ref([])
const studentPredictions = ref([])
const dialogVisible = ref(false)
const selectedPrediction = ref(null)
const chartContainer = ref(null)

// 计算风险等级
const highRiskCount = computed(() => 
  studentPredictions.value.filter(p => p.riskLevel === 'high').length
)
const mediumRiskCount = computed(() => 
  studentPredictions.value.filter(p => p.riskLevel === 'medium').length
)
const lowRiskCount = computed(() => 
  studentPredictions.value.filter(p => p.riskLevel === 'low').length
)

onMounted(async () => {
  await loadCourses()
})

// 加载课程列表
const loadCourses = async () => {
  try {
    const userId = getUserId()
    const teacherId = localStorage.getItem('teacherId') || userId
    if (!teacherId) return
    const response = await teacherAPI.getCourses(teacherId)
    if (Array.isArray(response) && response.length > 0) {
      courses.value = response
      selectedCourse.value = response[0].id
      await loadCourseData()
    }
  } catch (error) {
    console.error('加载课程失败:', error)
  }
}

// 加载课程数据
const loadCourseData = async () => {
  if (!selectedCourse.value) return
  try {
    const [recs, students] = await Promise.all([
      teacherAPI.getCourseRecommendations(selectedCourse.value, 10),
      teacherAPI.getCourseStudents(selectedCourse.value, 1, 100)
    ])
    
    recommendations.value = recs || []
    
    // 获取所有学生的学分预测
    const predictions = []
    for (const student of (students || [])) {
      try {
        const prediction = await teacherAPI.getCreditPrediction(student.student_id)
        if (prediction) {
          prediction.studentName = student.student_name
          prediction.studentId = student.student_id
          predictions.push(prediction)
        }
      } catch (e) {
        console.warn('获取学生预测失败:', e)
      }
    }
    
    studentPredictions.value = predictions
    
    // 延迟初始化图表
    setTimeout(() => {
      initChart()
    }, 100)
  } catch (error) {
    console.error('加载课程数据失败:', error)
    ElMessage.error('加载数据失败，请重试')
  }
}

// 初始化图表
const initChart = () => {
  if (!chartContainer.value) return
  const chart = echarts.init(chartContainer.value)
  
  const data = [
    { name: '高风险', value: highRiskCount.value },
    { name: '中风险', value: mediumRiskCount.value },
    { name: '低风险', value: lowRiskCount.value }
  ]
  
  const option = {
    tooltip: { trigger: 'item', formatter: '{b}: {c} ({d}%)' },
    legend: { orient: 'vertical', left: 'left' },
    series: [{
      name: '学分风险分布',
      type: 'pie',
      radius: '50%',
      data: data,
      itemStyle: {
        color: (params) => {
          const colors = ['#f56c6c', '#e6a23c', '#67c23a']
          return colors[params.dataIndex]
        }
      }
    }]
  }
  
  chart.setOption(option)
}

// 查看预测详情
const viewPrediction = (row) => {
  selectedPrediction.value = row
  dialogVisible.value = true
}
</script>

<style scoped>
.teacher-credit-prediction {
  padding: 24px;
  background: #f5f7fa;
  min-height: 100vh;
}

.page-header {
  margin-bottom: 28px;
  padding: 32px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border-radius: 16px;
  color: white;
  box-shadow: 0 8px 24px rgba(102, 126, 234, 0.4);
  animation: slideDown 0.6s ease-out;
}

@keyframes slideDown {
  from { opacity: 0; transform: translateY(-20px); }
  to { opacity: 1; transform: translateY(0); }
}

.page-header h1 {
  margin: 0 0 10px 0;
  font-size: 28px;
  font-weight: bold;
  color: white;
}

.page-header p {
  margin: 0;
  font-size: 14px;
  opacity: 0.95;
  color: white;
}

.action-bar {
  display: flex;
  gap: 16px;
  margin-bottom: 24px;
  padding: 20px;
  background: white;
  border-radius: 16px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.08);
  align-items: center;
}

.action-bar .el-select {
  width: 220px;
}

.recommendations-grid {
  width: 100%;
}

.stats-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(140px, 1fr));
  gap: 16px;
  margin-bottom: 20px;
}

.stat-box {
  background: white;
  padding: 24px;
  border-radius: 16px;
  text-align: center;
  box-shadow: 0 6px 20px rgba(0, 0, 0, 0.08);
  transition: all 0.4s cubic-bezier(0.4, 0, 0.2, 1);
  border-top: 4px solid #667eea;
}

.stat-box:hover {
  box-shadow: 0 12px 32px rgba(0, 0, 0, 0.15);
  transform: translateY(-8px);
}

.stat-title {
  color: #999;
  font-size: 13px;
  margin-bottom: 10px;
  font-weight: 500;
}

.stat-value {
  font-size: 32px;
  font-weight: bold;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
  margin-bottom: 6px;
}

.stat-hint {
  color: #999;
  font-size: 12px;
}

.card-header {
  font-size: 18px;
  font-weight: bold;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.prediction-detail {
  line-height: 1.8;
}

.prediction-detail p {
  margin: 12px 0;
  color: #333;
  font-size: 15px;
}

:deep(.el-card) {
  border-radius: 16px;
  box-shadow: 0 6px 20px rgba(0, 0, 0, 0.08);
  border: 2px solid transparent;
  transition: all 0.3s ease;
  margin-bottom: 24px !important;
}

:deep(.el-card:hover) {
  border-color: #e8ecf1;
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.12);
}

:deep(.el-table) {
  border-radius: 12px;
  overflow: hidden;
}

:deep(.el-table th) {
  background: linear-gradient(135deg, #f8f9fa 0%, #e8ecf1 100%);
  color: #333;
  font-weight: 600;
}

:deep(.el-button) {
  border-radius: 8px;
  transition: all 0.3s ease;
}

:deep(.el-button:hover) {
  transform: translateY(-2px);
}

@media (max-width: 1200px) {
  .stats-grid {
    grid-template-columns: repeat(2, 1fr);
  }
}
</style>
