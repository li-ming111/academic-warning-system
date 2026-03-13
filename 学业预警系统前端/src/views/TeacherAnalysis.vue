<template>
  <div class="teacher-analysis">
    <div class="page-header">
      <h1>学生表现分析</h1>
      <p>成绩分布、异常学生识别和详细成绩排名</p>
    </div>

    <!-- 班级选择 -->
    <div class="action-bar">
      <el-select v-model="selectedClass" placeholder="选择班级" @change="loadAnalysisData">
        <el-option v-for="cls in classes" :key="cls.id" :label="cls.name" :value="cls.id"></el-option>
      </el-select>
    </div>

    <!-- 统计信息 -->
    <div class="stats-grid" v-if="distribution">
      <div class="stat-box">
        <div class="stat-title">总人数</div>
        <div class="stat-value">{{ distribution.total }}</div>
        <div class="stat-hint">人</div>
      </div>
      <div class="stat-box">
        <div class="stat-title">平均分</div>
        <div class="stat-value">{{ distribution.averageScore }}</div>
        <div class="stat-hint">分</div>
      </div>
      <div class="stat-box">
        <div class="stat-title">及格率</div>
        <div class="stat-value">{{ passRate }}%</div>
        <div class="stat-hint">百分比</div>
      </div>
      <div class="stat-box">
        <div class="stat-title">风险学生</div>
        <div class="stat-value" style="color: #f56c6c;">{{ anomalies.length }}</div>
        <div class="stat-hint">人</div>
      </div>
    </div>

    <!-- 图表区域 -->
    <div class="charts-grid" v-if="distribution">
      <!-- 成绩分布柱状图 -->
      <el-card>
        <template #header>
          <div class="card-header">成绩分布统计</div>
        </template>
        <div ref="chartContainer1" style="height: 300px;"></div>
      </el-card>

      <!-- 成绩段比例饼图 -->
      <el-card>
        <template #header>
          <div class="card-header">成绩段占比</div>
        </template>
        <div ref="chartContainer2" style="height: 300px;"></div>
      </el-card>
    </div>

    <!-- 异常学生表格 -->
    <el-card style="margin-top: 20px;" v-if="anomalies.length > 0">
      <template #header>
        <div class="card-header">异常学生预警 (成绩<60分)</div>
      </template>
      <el-table :data="anomalies" stripe>
        <el-table-column prop="studentId" label="学号" width="120"></el-table-column>
        <el-table-column prop="studentName" label="姓名" width="100"></el-table-column>
        <el-table-column prop="score" label="成绩" width="80">
          <template #default="{ row }">
            <span :style="{ color: row.riskLevel === 'high' ? '#f56c6c' : '#e6a23c', fontWeight: 'bold' }">
              {{ row.score }}
            </span>
          </template>
        </el-table-column>
        <el-table-column prop="riskLevel" label="风险等级" width="100">
          <template #default="{ row }">
            <el-tag :type="row.riskLevel === 'high' ? 'danger' : 'warning'">{{ row.riskLevel === 'high' ? '高风险' : '中风险' }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="120">
          <template #default="{ row }">
            <el-button type="primary" size="small" link @click="viewStudent(row)">查看</el-button>
            <el-button type="warning" size="small" link>预警</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <!-- 学生成绩排名表格 -->
    <el-card style="margin-top: 20px;" v-if="studentList.length > 0">
      <template #header>
        <div class="card-header">学生成绩详情排名</div>
      </template>
      <el-table :data="studentList" stripe>
        <el-table-column prop="rank" label="排名" width="60"></el-table-column>
        <el-table-column prop="student_id" label="学号" width="120"></el-table-column>
        <el-table-column prop="student_name" label="姓名" width="100"></el-table-column>
        <el-table-column prop="score_total" label="成绩" width="80">
          <template #default="{ row }">
            <span :style="{ color: row.score_total >= 70 ? '#67c23a' : '#f56c6c', fontWeight: 'bold' }">
              {{ row.score_total || '-' }}
            </span>
          </template>
        </el-table-column>
        <el-table-column label="状态" width="100">
          <template #default="{ row }">
            <el-tag v-if="row.score_total >= 80" type="success">优秀</el-tag>
            <el-tag v-else-if="row.score_total >= 70" type="info">良好</el-tag>
            <el-tag v-else-if="row.score_total >= 60" type="warning">及格</el-tag>
            <el-tag v-else type="danger">不及格</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="120" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" size="small" link @click="viewStudent(row)">查看</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <!-- 学生详情对话框 -->
    <el-dialog v-model="studentDialogVisible" title="学生详情" width="600px">
      <div class="student-detail">
        <p><strong>学号：</strong>{{ selectedStudent.student_id || selectedStudent.studentId }}</p>
        <p><strong>姓名：</strong>{{ selectedStudent.student_name || selectedStudent.studentName }}</p>
        <p><strong>成绩：</strong>{{ selectedStudent.score_total || selectedStudent.score }}分</p>
        <p><strong>排名：</strong>{{ selectedStudent.rank || '-' }}</p>
        <p><strong>状态：</strong>
          <el-tag v-if="(selectedStudent.score_total || selectedStudent.score) >= 80" type="success">优秀</el-tag>
          <el-tag v-else-if="(selectedStudent.score_total || selectedStudent.score) >= 70" type="info">良好</el-tag>
          <el-tag v-else-if="(selectedStudent.score_total || selectedStudent.score) >= 60" type="warning">及格</el-tag>
          <el-tag v-else type="danger">不及格</el-tag>
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

const selectedClass = ref('')
const studentDialogVisible = ref(false)
const chartContainer1 = ref(null)
const chartContainer2 = ref(null)
const selectedStudent = ref({})
const classes = ref([])
const studentList = ref([])
const distribution = ref(null)
const anomalies = ref([])

// 计算及格率
const passRate = computed(() => {
  if (!distribution.value) return 0
  const pass = distribution.value.excellent + distribution.value.good + distribution.value.normal + distribution.value.pass
  const rate = distribution.value.total > 0 ? (pass / distribution.value.total * 100).toFixed(1) : 0
  return rate
})

onMounted(async () => {
  await loadClasses()
})

// 加载班级列表
const loadClasses = async () => {
  try {
    const userId = getUserId()
    const teacherId = localStorage.getItem('teacherId') || userId
    if (!teacherId) return
    // 从API获取班级列表
    const response = await teacherAPI.getMyClasses(teacherId)
    if (response && response.data) {
      classes.value = response.data.map(cls => ({
        id: cls.id || cls.classId,
        name: cls.name || cls.className
      }))
      if (classes.value.length > 0) {
        selectedClass.value = classes.value[0].id
        await loadAnalysisData()
      }
    }
  } catch (error) {
    console.error('加载班级失败:', error)
    // 加载失败时使用模拟数据
    classes.value = [
      { id: 1, name: '软件1班' },
      { id: 2, name: '软件2班' },
      { id: 3, name: '电子1班' },
      { id: 4, name: '电子2班' }
    ]
    if (classes.value.length > 0) {
      selectedClass.value = classes.value[0].id
      await loadAnalysisData()
    }
  }
}

// 加载分析数据
const loadAnalysisData = async () => {
  if (!selectedClass.value) return
  try {
    // 加载班级成绩分析数据
    const response = await teacherAPI.getClassScoreAnalysis(selectedClass.value)
    
    if (response && response.data) {
      const data = response.data
      // 转换数据格式以适应现有图表
      distribution.value = {
        total: data.totalStudents || 0,
        averageScore: data.averageScore || 0,
        excellent: data.scoreDistribution?.excellent || 0,
        good: data.scoreDistribution?.good || 0,
        normal: data.scoreDistribution?.average || 0,
        pass: data.scoreDistribution?.pass || 0,
        fail: data.scoreDistribution?.fail || 0
      }
      
      // 模拟异常学生数据
      anomalies.value = [
        { studentId: '2023001', studentName: '张三', score: 55, riskLevel: 'high' },
        { studentId: '2023002', studentName: '李四', score: 58, riskLevel: 'medium' }
      ]
      
      // 模拟学生列表数据
      studentList.value = [
        { rank: 1, student_id: '2023003', student_name: '王五', score_total: 95 },
        { rank: 2, student_id: '2023004', student_name: '赵六', score_total: 88 },
        { rank: 3, student_id: '2023005', student_name: '钱七', score_total: 76 },
        { rank: 4, student_id: '2023006', student_name: '孙八', score_total: 65 },
        { rank: 5, student_id: '2023001', student_name: '张三', score_total: 55 },
        { rank: 6, student_id: '2023002', student_name: '李四', score_total: 58 }
      ]
      
      // 延迟后初始化图表
      setTimeout(() => {
        initChart1()
        initChart2()
      }, 100)
    }
  } catch (error) {
    console.error('加载分析数据失败:', error)
    ElMessage.error('加载数据失败，请重试')
  }
}

const initChart1 = () => {
  if (!chartContainer1.value || !distribution.value) return
  const chart = echarts.init(chartContainer1.value)
  const data = [
    { name: '90-100分', value: distribution.value.excellent },
    { name: '80-89分', value: distribution.value.good },
    { name: '70-79分', value: distribution.value.normal },
    { name: '60-69分', value: distribution.value.pass },
    { name: '0-59分', value: distribution.value.fail }
  ]
  const option = {
    xAxis: { 
      type: 'category', 
      data: data.map(d => d.name)
    },
    yAxis: { type: 'value' },
    series: [{
      data: data.map(d => d.value),
      type: 'bar',
      itemStyle: { 
        color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
          { offset: 0, color: '#409eff' },
          { offset: 1, color: '#66b1ff' }
        ])
      }
    }]
  }
  chart.setOption(option)
}

const initChart2 = () => {
  if (!chartContainer2.value || !distribution.value) return
  const chart = echarts.init(chartContainer2.value)
  const option = {
    tooltip: { trigger: 'item', formatter: '{b}: {c} ({d}%)' },
    legend: { orient: 'vertical', left: 'left' },
    series: [{
      name: '成绩分布',
      type: 'pie',
      radius: '50%',
      data: [
        { value: distribution.value.excellent, name: '优秀(90-100)' },
        { value: distribution.value.good, name: '良好(80-89)' },
        { value: distribution.value.normal, name: '中等(70-79)' },
        { value: distribution.value.pass, name: '及格(60-69)' },
        { value: distribution.value.fail, name: '不及格(0-59)' }
      ]
    }]
  }
  chart.setOption(option)
}

const viewStudent = (row) => {
  selectedStudent.value = row
  studentDialogVisible.value = true
}
</script>

<style scoped>
.teacher-analysis {
  padding: 24px;
  background: #f5f7fa;
  min-height: 100vh;
}

.page-header {
  margin-bottom: 28px;
  padding: 32px;
  background: linear-gradient(135deg, #667eea 0%, #66b1ff 100%);
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

.stats-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(140px, 1fr));
  gap: 16px;
  margin-bottom: 24px;
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
  background: linear-gradient(135deg, #667eea 0%, #66b1ff 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
  margin-bottom: 6px;
}

.stat-hint {
  color: #999;
  font-size: 12px;
}

.charts-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 24px;
  margin-bottom: 24px;
}

.card-header {
  font-size: 18px;
  font-weight: bold;
  background: linear-gradient(135deg, #667eea 0%, #66b1ff 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.student-detail {
  line-height: 1.8;
}

.student-detail p {
  margin: 12px 0;
  color: #333;
  font-size: 15px;
}

:deep(.el-card) {
  border-radius: 16px;
  box-shadow: 0 6px 20px rgba(0, 0, 0, 0.08);
  border: 2px solid transparent;
  transition: all 0.3s ease;
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
  .charts-grid { grid-template-columns: 1fr; }
  .stats-grid { grid-template-columns: repeat(2, 1fr); }
}

@media (max-width: 768px) {
  .page-header {
    padding: 20px;
  }
  .page-header h1 {
    font-size: 22px;
  }
  .stats-grid { grid-template-columns: 1fr; }
  .action-bar {
    flex-direction: column;
    align-items: flex-start;
  }
  .action-bar :deep(.el-select) {
    width: 100%;
  }
}
</style>
