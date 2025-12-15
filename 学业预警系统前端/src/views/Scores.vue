<template>
  <div class="scores-container">
    <!-- 筛选器 -->
    <div class="filter-section">
      <el-select v-model="selectedSemester" placeholder="选择学期" style="width: 200px;">
        <el-option label="秋季" value="秋季"></el-option>
        <el-option label="春季" value="春季"></el-option>
      </el-select>
      <el-button type="primary" @click="loadScores">查询</el-button>
      <el-button @click="exportScores">导出成绩</el-button>
    </div>

    <!-- 成绩趋势图 -->
    <el-card style="margin-bottom: 20px;">
      <template #header>
        <div class="card-header">成绩趋势分析</div>
      </template>
      <div id="trendsChart" style="width: 100%; height: 300px;"></div>
    </el-card>

    <!-- 成绩列表 -->
    <el-card>
      <template #header>
        <div class="card-header">{{ selectedSemester }} 成绩明细</div>
      </template>
      
      <el-table :data="scoresList" stripe>
        <el-table-column prop="courseName" label="课程名称" width="200"></el-table-column>
        <el-table-column prop="courseCode" label="课程代码" width="120"></el-table-column>
        <el-table-column prop="credits" label="学分" width="80"></el-table-column>
        <el-table-column prop="scoreTotal" label="总分" width="80">
          <template #default="{ row }">
            <span :style="{ color: row.scoreTotal < 70 ? '#f56c6c' : '#67c23a' }">{{ row.scoreTotal }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="gradePoint" label="绩点" width="80"></el-table-column>
        <el-table-column prop="grade" label="等级" width="80"></el-table-column>
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag v-if="row.status === 'normal'" type="success">正常</el-tag>
            <el-tag v-else-if="row.status === 'warning'" type="warning">预警</el-tag>
            <el-tag v-else type="danger">待处理</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="120" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" size="small" @click="openAppealDialog(row)">申诉</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <!-- 申诉对话框 -->
    <el-dialog v-model="appealDialogVisible" title="成绩申诉" width="500px">
      <el-form :model="appealForm" label-width="100px">
        <el-form-item label="课程">
          <el-input v-model="appealForm.courseName" disabled></el-input>
        </el-form-item>
        <el-form-item label="当前成绩">
          <el-input v-model="appealForm.currentScore" disabled></el-input>
        </el-form-item>
        <el-form-item label="申诉原因">
          <el-select v-model="appealForm.reason" placeholder="请选择申诉原因">
            <el-option label="成绩录入错误" value="entry_error"></el-option>
            <el-option label="缓考未同步" value="makeup_not_sync"></el-option>
            <el-option label="阅卷有问题" value="grading_issue"></el-option>
            <el-option label="其他" value="other"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="申诉说明">
          <el-input v-model="appealForm.description" type="textarea" rows="4" placeholder="请详细说明申诉理由"></el-input>
        </el-form-item>
        <el-form-item label="附件">
          <el-upload
            v-model:file-list="appealForm.attachments"
            action="#"
            :auto-upload="false"
            :limit="3"
          >
            <template #trigger>
              <el-button type="primary">选择文件</el-button>
            </template>
            <template #tip>
              <div class="el-upload__tip">可上传截图或PDF作为证据</div>
            </template>
          </el-upload>
        </el-form-item>
      </el-form>

      <template #footer>
        <el-button @click="appealDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitAppeal">提交申诉</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import * as echarts from 'echarts'
import { ElMessage } from 'element-plus'
import { studentAPI } from '@/api/index'

const selectedSemester = ref('秋季')
const scoresList = ref([])
const appealDialogVisible = ref(false)
const appealForm = ref({
  courseId: null,
  courseName: '',
  currentScore: '',
  reason: '',
  description: '',
  attachments: [],
  warningId: null
})

onMounted(async () => {
  await loadScores()
})

// 加载成绩列表
const loadScores = async () => {
  try {
    const userIdStr = localStorage.getItem('userId')
    const userId = userIdStr ? parseInt(userIdStr) : null
    console.log('userId (string):', userIdStr, 'userId (number):', userId)
    if (!userId) {
      ElMessage.error('请先登录')
      return
    }
    console.log('selectedSemester:', selectedSemester.value)
    // 不传semester参数，获取所有成绩
    const response = await studentAPI.getScores(userId, null)
    console.log('成绩响应:', response)
    if (Array.isArray(response) && response.length > 0) {
      scoresList.value = response.map(score => ({
        ...score,
        status: 'normal',
        grade: calculateGrade(score.scoreTotal || 0)
      }))
    } else {
      scoresList.value = []
    }
    initTrendsChart()
  } catch (error) {
    console.error('加载成绩失败:', error)
    console.error('错误详情:', error.response?.data || error.message)
    scoresList.value = []
  }
}

// 计算等级
const calculateGrade = (score) => {
  if (score >= 90) return 'A'
  if (score >= 80) return 'A-'
  if (score >= 70) return 'B'
  if (score >= 60) return 'C'
  return 'D'
}

// 初始化趋势图
const initTrendsChart = () => {
  const chartDom = document.getElementById('trendsChart')
  if (!chartDom) return

  const myChart = echarts.init(chartDom)
  const option = {
    tooltip: { trigger: 'axis' },
    legend: { data: ['高等数学', '线性代数', '数据结构', '数据库', '计算机网络'] },
    grid: { left: '3%', right: '4%', bottom: '3%', top: '10%', containLabel: true },
    xAxis: {
      type: 'category',
      data: ['第1次', '第2次', '第3次']
    },
    yAxis: {
      type: 'value',
      min: 0,
      max: 100
    },
    series: [
      {
        name: '高等数学',
        data: [72, 73, 75],
        type: 'line',
        smooth: true,
        itemStyle: { color: '#409eff' }
      },
      {
        name: '线性代数',
        data: [65, 68, 68],
        type: 'line',
        smooth: true,
        itemStyle: { color: '#f56c6c' }
      },
      {
        name: '数据结构',
        data: [80, 82, 82],
        type: 'line',
        smooth: true,
        itemStyle: { color: '#67c23a' }
      },
      {
        name: '数据库',
        data: [83, 84, 85],
        type: 'line',
        smooth: true,
        itemStyle: { color: '#e6a23c' }
      },
      {
        name: '计算机网络',
        data: [70, 71, 72],
        type: 'line',
        smooth: true,
        itemStyle: { color: '#909399' }
      }
    ]
  }

  myChart.setOption(option)
  window.addEventListener('resize', () => myChart.resize())
}

// 打开申诉对话框
const openAppealDialog = (row) => {
  appealForm.value = {
    courseId: row.id,
    courseName: row.courseName,
    currentScore: row.scoreTotal,
    reason: '',
    description: '',
    attachments: [],
    warningId: null
  }
  appealDialogVisible.value = true
}

// 提交申诉
const submitAppeal = async () => {
  if (!appealForm.value.reason) {
    ElMessage.error('请选择申诉原因')
    return
  }
  if (!appealForm.value.description) {
    ElMessage.error('请填写申诉说明')
    return
  }

  try {
    const userId = localStorage.getItem('userId')
    const appealData = {
      studentId: userId,
      scoreId: appealForm.value.courseId,
      reason: appealForm.value.reason,
      description: appealForm.value.description
    }
    await studentAPI.submitAppeal(appealData)
    ElMessage.success('申诉已提交，请等待教师处理')
    appealDialogVisible.value = false
  } catch (error) {
    console.error('提交申诉失败:', error)
    ElMessage.error('提交申诉失败')
  }
}

// 导出成绩
const exportScores = async () => {
  try {
    const userId = localStorage.getItem('userId')
    await studentAPI.exportScoresExcel(userId)
    ElMessage.info('成绩导出成功，即将下载')
    // 下载文件
    const blob = await studentAPI.downloadScoresExcel(userId)
    const url = window.URL.createObjectURL(blob)
    const link = document.createElement('a')
    link.href = url
    link.download = `scores_${Date.now()}.xlsx`
    link.click()
    window.URL.revokeObjectURL(url)
  } catch (error) {
    console.error('导出成绩失败:', error)
    ElMessage.error('导出成绩失败')
  }
}
</script>

<style scoped>
.scores-container {
  padding: 20px;
  background-color: #f5f7fa;
  min-height: 100vh;
}

.filter-section {
  display: flex;
  gap: 10px;
  margin-bottom: 20px;
}

.card-header {
  font-size: 16px;
  font-weight: bold;
  color: #333;
}

.el-upload__tip {
  color: #666;
  font-size: 12px;
}
</style>
