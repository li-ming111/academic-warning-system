<template>
  <div class="scores-container">
    <!-- 筛选器 -->
    <div class="filter-section">
      <el-select v-model="selectedSemester" placeholder="选择学期" style="width: 200px;">
        <el-option v-for="sem in availableSemesters" :key="sem" :label="sem" :value="sem"></el-option>
      </el-select>
      <el-button type="primary" @click="loadScores">查询</el-button>
      <el-button @click="exportScores">导出成绩</el-button>
    </div>

    <!-- 成绩趋势图 -->
    <el-card style="margin-bottom: 20px;">
      <template #header>
        <div class="card-header">成绩趋势分析</div>
      </template>
      <!-- 统计卡片 -->
      <div class="stats-cards" style="margin-bottom: 20px; display: flex; gap: 20px;">
        <div class="stat-card" style="flex: 1; padding: 15px; background: linear-gradient(135deg, #667eea 0%, #764ba2 100%); color: white; border-radius: 8px;">
          <div style="font-size: 12px; opacity: 0.9;">当前学期GPA</div>
          <div style="font-size: 28px; font-weight: bold; margin-top: 10px;">{{ currentGpa }}</div>
          <div style="font-size: 12px; margin-top: 5px;">{{ gpaLevel }}</div>
        </div>
        <div class="stat-card" style="flex: 1; padding: 15px; background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%); color: white; border-radius: 8px;">
          <div style="font-size: 12px; opacity: 0.9;">最高GPA</div>
          <div style="font-size: 28px; font-weight: bold; margin-top: 10px;">{{ maxGpa }}</div>
          <div style="font-size: 12px; margin-top: 5px;">历史最佳</div>
        </div>
        <div class="stat-card" style="flex: 1; padding: 15px; background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%); color: white; border-radius: 8px;">
          <div style="font-size: 12px; opacity: 0.9;">平均GPA</div>
          <div style="font-size: 28px; font-weight: bold; margin-top: 10px;">{{ avgGpa }}</div>
          <div style="font-size: 12px; margin-top: 5px;">全周期平均</div>
        </div>
        <div class="stat-card" style="flex: 1; padding: 15px; background: linear-gradient(135deg, #fa709a 0%, #fee140 100%); color: white; border-radius: 8px;">
          <div style="font-size: 12px; opacity: 0.9;">挂科课程</div>
          <div style="font-size: 28px; font-weight: bold; margin-top: 10px;">{{ failedCourses }}</div>
          <div style="font-size: 12px; margin-top: 5px;" :style="{ color: failedCourses > 5 ? '#ff4444' : failedCourses > 3 ? '#ffaa00' : '#44ff44' }">{{ warnLevel }}</div>
        </div>
      </div>
    </el-card>

    <!-- 成绩列表 -->
    <el-card>
      <template #header>
        <div class="card-header">{{ selectedSemester }} 成绩明细</div>
      </template>
      
      <el-table :data="scoresList" stripe>
        <el-table-column prop="courseName" label="课程名称" width="200"></el-table-column>
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
import { ref, onMounted, watch } from 'vue'
import * as echarts from 'echarts'
import { ElMessage } from 'element-plus'
import { studentAPI } from '@/api/index'
import { getUserId } from '@/utils/userUtils'

const selectedSemester = ref('')
const availableSemesters = ref([])
const scoresList = ref([])
const historyBenchmarks = ref([])
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
  // 根据学号生成可用的学期列表
  generateAvailableSemesters()
  if (availableSemesters.value.length > 0) {
    selectedSemester.value = availableSemesters.value[0]
  }
  // 先加载历史GPA数据，然后再加载成绩
  await loadHistoryBenchmark()
  await loadScores()
})

// 根据学号前四位（入学年份）生成可用的学期
const generateAvailableSemesters = () => {
  const currentYear = new Date().getFullYear()
  const userId = getUserId()
  
  // 从 localStorage 中获取学号（学号为 10 位数字）
  let entranceYear = 2023  // 默认值
  const studentId = localStorage.getItem('studentId') || localStorage.getItem('username')
  if (studentId && studentId.length >= 4) {
    const yearPrefix = parseInt(studentId.substring(0, 4))
    if (!isNaN(yearPrefix) && yearPrefix >= 2000 && yearPrefix <= currentYear) {
      entranceYear = yearPrefix
    }
  }
  
  const semesters = []
  for (let year = entranceYear; year <= currentYear; year++) {
    semesters.push(`${year}-${year + 1}-1`)  // 秋季
    if (year < currentYear) {
      semesters.push(`${year}-${year + 1}-2`)  // 春季（除了当前年度）
    }
  }
  
  availableSemesters.value = semesters.reverse()  // 值倒序，最新学期优先
}

// 监听学期变化，自动重新加载成绩
watch(selectedSemester, async (newVal) => {
  await loadScores()
})

// 加载成绩列表
const loadScores = async () => {
  try {
    const userId = getUserId()
    console.log('userId (number):', userId)
    if (!userId) {
      ElMessage.error('请先登录')
      return
    }
    console.log('selectedSemester:', selectedSemester.value)
    console.log('发送API请求:', {userId, semester: selectedSemester.value})
    // 清空旧数据
    scoresList.value = []
    // 强制添加时间戳避免缓存
    const response = await studentAPI.getScores(userId, selectedSemester.value)
    console.log('成绩响应:', response)
    console.log('响应类型:', typeof response, '是否数组:', Array.isArray(response))
    if (response) {
      console.log('响应长度:', response.length)
      if (response.length > 0) {
        console.log('第一条数据:', JSON.stringify(response[0]))
      }
    }
    if (Array.isArray(response) && response.length > 0) {
      scoresList.value = response.map(score => {
        console.log('处理成绩:', {id: score.id, courseName: score.courseName, credits: score.credits, scoreTotal: score.scoreTotal})
        return {
          ...score,
          status: 'normal',
          grade: calculateGrade(score.scoreTotal || 0)
        }
      })
      console.log('最终scoresList长度:', scoresList.value.length)
    } else {
      scoresList.value = []
    }
    calculateStats()
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

// 加载历史GPA数据
const loadHistoryBenchmark = async () => {
  try {
    const userId = getUserId()
    if (!userId) return
    const response = await studentAPI.getHistoryBenchmark(userId)
    if (response && Array.isArray(response)) {
      // 响应直接是数组
      historyBenchmarks.value = response
    } else if (response?.data?.code === 200) {
      historyBenchmarks.value = response.data.data || []
    } else {
      historyBenchmarks.value = []
    }
    console.log('加载的历史GPA数据:', historyBenchmarks.value)
    calculateStats()
  } catch (error) {
    console.error('加载历史GPA数据失败:', error)
    historyBenchmarks.value = []
  }
}

// 统计数据
const currentGpa = ref('0.00')
const maxGpa = ref('0.00')
const avgGpa = ref('0.00')
const failedCourses = ref(0)
const gpaLevel = ref('--')
const warnLevel = ref('--')

// 计算统计数据
const calculateStats = () => {
  // 计算当前学朞的GPA（使用加权平均）
  if (scoresList.value.length > 0) {
    const totalWeightedGradePoints = scoresList.value.reduce((sum, c) => sum + (c.gradePoint || 0) * (c.credits || 0), 0)
    const totalCredits = scoresList.value.reduce((sum, c) => sum + (c.credits || 0), 0)
    currentGpa.value = totalCredits > 0 ? (totalWeightedGradePoints / totalCredits).toFixed(2) : '0.00'
    gpaLevel.value = currentGpa.value >= 3.5 ? '优秀' : currentGpa.value >= 3.0 ? '良好' : currentGpa.value >= 2.0 ? '中等' : '需改进'
  } else {
    currentGpa.value = '0.00'
    gpaLevel.value = '––'
  }
  
  // 计算最高GPA和平均GPA介绿整个学业
  if (historyBenchmarks.value && historyBenchmarks.value.length > 0) {
    const sorted = [...historyBenchmarks.value].sort((a, b) => b.studentGpa - a.studentGpa)
    maxGpa.value = (sorted[0]?.studentGpa || 0).toFixed(2)
    
    const sum = historyBenchmarks.value.reduce((acc, item) => acc + (item.studentGpa || 0), 0)
    avgGpa.value = (sum / historyBenchmarks.value.length).toFixed(2)
  } else {
    maxGpa.value = currentGpa.value
    avgGpa.value = currentGpa.value
  }
  
  // 计算挂科课程数
  failedCourses.value = scoresList.value.filter(c => c.scoreTotal < 60).length
  if (failedCourses.value < 3) warnLevel.value = '低级预警'
  else if (failedCourses.value <= 5) warnLevel.value = '中级预警'
  else warnLevel.value = '高级预警'
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
