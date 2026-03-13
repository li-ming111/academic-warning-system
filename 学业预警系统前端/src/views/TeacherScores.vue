<template>
  <div class="teacher-scores">
    <!-- 欢迎区域 -->
    <div class="welcome-section">
      <div class="welcome-card">
        <div class="welcome-content">
          <h1>成绩管理</h1>
          <p>录入、管理和分析学生成绩</p>
        </div>
      </div>
    </div>

    <!-- 操作栏 -->
    <div class="toolbar-section">
      <div class="toolbar">
        <el-select v-model="selectedCourse" placeholder="选择课程" @change="loadCourseScores" class="toolbar-item" filterable clearable>
          <el-option v-for="course in courses" :key="course.id" :label="course.name" :value="course.id"></el-option>
        </el-select>
        <el-upload
          ref="uploadRef"
          :auto-upload="false"
          :on-change="handleFileSelect"
          accept=".xlsx,.xls"
          class="toolbar-item"
          :show-file-list="false"
        >
          <el-button class="toolbar-item" style="background-color: #409EFF; color: white; border-color: #409EFF;">导入Excel</el-button>
        </el-upload>
        <el-button @click="exportScores" class="toolbar-item">导出成绩</el-button>
        <el-button @click="analyzeScores" class="toolbar-item" type="info">成绩分析</el-button>
        <el-button @click="detectAnomalies" class="toolbar-item" type="warning">异常检测</el-button>
        <el-button @click="triggerWarnings" class="toolbar-item" type="danger">触发预警</el-button>
        <el-button @click="batchDeleteScores" class="toolbar-item" type="danger" :disabled="selectedRows.length === 0">批量删除</el-button>
      </div>
    </div>

    <!-- 成绩分析卡片 -->
    <div v-if="selectedCourse && analysisData" class="content-card">
      <div class="card-title-bar">
        <h2>课程成绩分析</h2>
      </div>
      <div class="card-body-section">
        <div class="analysis-grid">
          <div class="analysis-item">
            <div class="analysis-label">平均分</div>
            <div class="analysis-value">{{ analysisData.averageScore.toFixed(2) }}</div>
          </div>
          <div class="analysis-item">
            <div class="analysis-label">最高分</div>
            <div class="analysis-value">{{ analysisData.maxScore }}</div>
          </div>
          <div class="analysis-item">
            <div class="analysis-label">最低分</div>
            <div class="analysis-value">{{ analysisData.minScore }}</div>
          </div>
          <div class="analysis-item">
            <div class="analysis-label">标准差</div>
            <div class="analysis-value">{{ analysisData.standardDeviation.toFixed(2) }}</div>
          </div>
          <div class="analysis-item">
            <div class="analysis-label">及格率</div>
            <div class="analysis-value">{{ (analysisData.passRate * 100).toFixed(1) }}%</div>
          </div>
          <div class="analysis-item">
            <div class="analysis-label">优秀率</div>
            <div class="analysis-value">{{ (analysisData.excellentRate * 100).toFixed(1) }}%</div>
          </div>
        </div>
        <div class="distribution-section">
          <h3>成绩分布</h3>
          <div class="distribution-chart">
            <div v-for="(count, range) in analysisData.scoreDistribution" :key="range" class="distribution-bar">
              <div class="bar-label">{{ range }}</div>
              <div class="bar-container">
                <div class="bar-fill" :style="{ width: (count / analysisData.totalStudents) * 100 + '%' }"></div>
              </div>
              <div class="bar-count">{{ count }}</div>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 成绩表格卡片 -->
    <div class="content-card">
      <div class="card-title-bar">
        <h2>{{ selectedCourse ? '课程成绩列表' : '请选择课程' }}</h2>
      </div>
      <div class="card-body-section">
        <el-table :data="scoresList" stripe v-if="selectedCourse" class="modern-table" @selection-change="handleSelectionChange">
          <el-table-column type="selection" width="50"></el-table-column>
          <el-table-column prop="studentId" label="学号" width="150"></el-table-column>
          <el-table-column prop="studentName" label="姓名" width="120"></el-table-column>
          <el-table-column prop="regularScore" label="平时分" width="100">
            <template #default="{ row }">
              <el-input-number v-model="row.regularScore" :min="0" :max="100" size="small"></el-input-number>
            </template>
          </el-table-column>
          <el-table-column prop="finalScore" label="期末分" width="100">
            <template #default="{ row }">
              <el-input-number v-model="row.finalScore" :min="0" :max="100" size="small"></el-input-number>
            </template>
          </el-table-column>
          <el-table-column prop="totalScore" label="总分" width="100">
            <template #default="{ row }">
              <span class="score-value">{{ calculateTotal(row) }}</span>
            </template>
          </el-table-column>
          <el-table-column prop="grade" label="等级" width="80"></el-table-column>
          <el-table-column label="操作" width="200" fixed="right">
            <template #default="{ row }">
              <el-button type="primary" size="small" link @click="editScore(row)">修改</el-button>
              <el-button type="danger" size="small" link @click="deleteScore(row)">删除</el-button>
              <el-button type="info" size="small" link @click="viewStudentAnalysis(row)">分析</el-button>
            </template>
          </el-table-column>
        </el-table>
        <el-empty v-else description="请先选择课程"></el-empty>
      </div>
    </div>

    <!-- 保存按钮 -->
    <div class="action-section">
      <el-button type="success" size="large" @click="saveScores" class="save-btn">保存成绩</el-button>
    </div>

    <!-- 修改对话框 -->
    <el-dialog v-model="editDialogVisible" title="修改成绩" width="500px">
      <el-form :model="editingScore" label-width="100px">
        <el-form-item label="学生">
          <el-input :value="editingScore.studentName" disabled></el-input>
        </el-form-item>
        <el-form-item label="平时分">
          <el-input-number v-model="editingScore.regularScore" :min="0" :max="100"></el-input-number>
        </el-form-item>
        <el-form-item label="期末分">
          <el-input-number v-model="editingScore.finalScore" :min="0" :max="100"></el-input-number>
        </el-form-item>
        <el-form-item label="修改原因">
          <el-input v-model="editingScore.reason" type="textarea" rows="3" placeholder="请说明修改原因"></el-input>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="editDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitEdit">确认</el-button>
      </template>
    </el-dialog>

    <!-- 学生分析对话框 -->
    <el-dialog v-model="studentAnalysisVisible" title="学生成绩分析" width="600px">
      <div v-if="studentAnalysisData" class="student-analysis">
        <div class="analysis-header">
          <h3>{{ studentAnalysisData.studentName }} ({{ studentAnalysisData.studentId }})</h3>
        </div>
        <div class="analysis-content">
          <div class="analysis-row">
            <div class="analysis-item">
              <div class="analysis-label">当前课程成绩</div>
              <div class="analysis-value">{{ studentAnalysisData.currentScore }}</div>
            </div>
            <div class="analysis-item">
              <div class="analysis-label">平均成绩</div>
              <div class="analysis-value">{{ studentAnalysisData.averageScore.toFixed(2) }}</div>
            </div>
            <div class="analysis-item">
              <div class="analysis-label">成绩趋势</div>
              <div class="analysis-value">{{ studentAnalysisData.trend }}</div>
            </div>
          </div>
          <div class="analysis-row">
            <div class="analysis-item">
              <div class="analysis-label">预测成绩</div>
              <div class="analysis-value">{{ studentAnalysisData.predictedScore.toFixed(2) }}</div>
            </div>
            <div class="analysis-item">
              <div class="analysis-label">挂科次数</div>
              <div class="analysis-value">{{ studentAnalysisData.failedCount }}</div>
            </div>
            <div class="analysis-item">
              <div class="analysis-label">预警状态</div>
              <div class="analysis-value" :class="studentAnalysisData.warningLevel">{{ studentAnalysisData.warningLevel }}</div>
            </div>
          </div>
          <div class="comment-section">
            <h4>教师评语</h4>
            <div class="comment-content">{{ studentAnalysisData.comment }}</div>
          </div>
        </div>
      </div>
      <template #footer>
        <el-button @click="studentAnalysisVisible = false">关闭</el-button>
      </template>
    </el-dialog>

    <!-- 异常检测对话框 -->
    <el-dialog v-model="anomalyDialogVisible" title="成绩异常检测" width="600px">
      <div v-if="anomalyData" class="anomaly-analysis">
        <div class="anomaly-header">
          <h3>异常成绩检测结果</h3>
          <p>共检测到 {{ anomalyData.length }} 个异常成绩</p>
        </div>
        <el-table :data="anomalyData" stripe class="modern-table">
          <el-table-column prop="studentId" label="学号" width="150"></el-table-column>
          <el-table-column prop="studentName" label="姓名" width="120"></el-table-column>
          <el-table-column prop="totalScore" label="总分" width="100"></el-table-column>
          <el-table-column prop="anomalyScore" label="异常分数" width="120"></el-table-column>
          <el-table-column prop="anomalyType" label="异常类型" width="150"></el-table-column>
          <el-table-column prop="confidence" label="置信度" width="100">
            <template #default="{ row }">
              <el-progress :percentage="(row.confidence * 100).toFixed(0)" :color="getProgressColor(row.confidence)"></el-progress>
            </template>
          </el-table-column>
        </el-table>
      </div>
      <template #footer>
        <el-button @click="anomalyDialogVisible = false">关闭</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { teacherAPI } from '@/api/index'
import { getUserId } from '@/utils/userUtils'

const selectedCourse = ref('')
const editDialogVisible = ref(false)
const editingScore = ref({})
const courses = ref([])

const scoresList = ref([])
const uploadRef = ref(null)

// 新增状态变量
const analysisData = ref(null)
const studentAnalysisVisible = ref(false)
const studentAnalysisData = ref(null)
const anomalyDialogVisible = ref(false)
const anomalyData = ref([])
const selectedRows = ref([])

// 自动计算当前学期
const getCurrentSemester = () => {
  const now = new Date()
  const year = now.getFullYear()
  const month = now.getMonth() + 1 // 月份从0开始，所以加1
  
  // 第一学期：9月-次年2月
  // 第二学期：3月-8月
  if (month >= 9 || month <= 2) {
    // 第一学期
    const startYear = month <= 2 ? year - 1 : year
    const endYear = month <= 2 ? year : year + 1
    return `${startYear}-${endYear}-1`
  } else {
    // 第二学期
    return `${year}-${year + 1}-2`
  }
}

onMounted(async () => {
  await loadCourses()
})

// 处理文件选择
const handleFileSelect = async (uploadFile) => {
  try {
    if (!selectedCourse.value) {
      ElMessage.error('请先选择课程')
      uploadRef.value.clearFiles()
      return
    }
    
    const file = uploadFile.raw
    const formData = new FormData()
    formData.append('file', file)
    formData.append('courseId', selectedCourse.value)
    formData.append('semester', getCurrentSemester())
    
    const response = await teacherAPI.importScores(formData)
    ElMessage.success('成绩导入成功')
    uploadRef.value.clearFiles()
    // 导入成功后刷新当前课程的成绩
    await loadCourseScores(selectedCourse.value)
  } catch (error) {
    console.error('导入成绩失败:', error)
    ElMessage.error(error.message || '导入成绩失败')
    uploadRef.value.clearFiles()
  }
}

// 加载课程列表
const loadCourses = async () => {
  try {
    // 尝试从localStorage获取teacherId，如果不存在则使用getUserId()
    const teacherId = localStorage.getItem('teacherId') || getUserId()
    console.log('Teacher ID:', teacherId)
    if (!teacherId) {
      // 设置默认教师ID为9
      localStorage.setItem('teacherId', '9')
      const defaultTeacherId = '9'
      console.log('Using default teacher ID:', defaultTeacherId)
      try {
        const response = await teacherAPI.getCourses(defaultTeacherId)
        console.log('Get courses response:', response)
        if (Array.isArray(response)) {
          courses.value = response
          console.log('Loaded courses:', courses.value)
          // 自动选择第一个课程并加载成绩
          if (courses.value.length > 0 && !selectedCourse.value) {
            console.log('First course:', courses.value[0])
            selectedCourse.value = courses.value[0].id
            console.log('Selected course ID:', selectedCourse.value)
            await loadCourseScores(courses.value[0].id)
          }
        }
      } catch (error) {
        console.error('加载课程失败:', error)
        // 模拟课程数据
        courses.value = [
          { id: 147, name: '互联网程序设计' },
          { id: 148, name: 'Linux操作系统' },
          { id: 149, name: 'Python程序设计' },
          { id: 150, name: '生活中的心理学' },
          { id: 151, name: '软件工程' }
        ]
        // 自动选择第一个课程并加载成绩
        if (courses.value.length > 0 && !selectedCourse.value) {
          selectedCourse.value = courses.value[0].id
          await loadCourseScores(courses.value[0].id)
        }
      }
      return
    }
    try {
      const response = await teacherAPI.getCourses(teacherId)
      console.log('Get courses response:', response)
      if (Array.isArray(response)) {
        courses.value = response
        console.log('Loaded courses:', courses.value)
        // 自动选择第一个课程并加载成绩
        if (courses.value.length > 0 && !selectedCourse.value) {
          console.log('First course:', courses.value[0])
          selectedCourse.value = courses.value[0].id
          console.log('Selected course ID:', selectedCourse.value)
          await loadCourseScores(courses.value[0].id)
        }
      }
    } catch (error) {
      console.error('加载课程失败:', error)
      // 模拟课程数据
      courses.value = [
        { id: 147, name: '互联网程序设计' },
        { id: 148, name: 'Linux操作系统' },
        { id: 149, name: 'Python程序设计' },
        { id: 150, name: '生活中的心理学' },
        { id: 151, name: '软件工程' }
      ]
      // 自动选择第一个课程并加载成绩
      if (courses.value.length > 0 && !selectedCourse.value) {
        selectedCourse.value = courses.value[0].id
        await loadCourseScores(courses.value[0].id)
      }
    }
  } catch (error) {
    console.error('加载课程失败:', error)
  }
}

// 简选课程时加载成绩
const loadCourseScores = async (courseId) => {
  try {
    console.log('Loading scores for course ID:', courseId)
    
    // 如果courseId为空，清空成绩列表
    if (!courseId) {
      scoresList.value = []
      return
    }
    
    try {
      const response = await teacherAPI.getScores(courseId)
      console.log('Get scores response:', response)
      if (Array.isArray(response)) {
        // 处理后端返回的数据结构
        scoresList.value = response.map(item => {
          // 使用后端返回的平时分和期末分，如果没有则计算
          const totalScore = item.totalScore || 0
          return {
            id: item.id,
            studentId: item.studentId,
            studentName: item.studentName,
            regularScore: item.regularScore || Math.round(totalScore * 0.3),
            finalScore: item.finalScore || Math.round(totalScore * 0.7),
            totalScore: totalScore,
            grade: item.grade || '-'
          }
        })
        console.log('Loaded scores:', scoresList.value)
      }
    } catch (error) {
      console.error('加载成绩失败:', error)
      // 模拟成绩数据
      scoresList.value = [
        { id: 1, studentId: '2023001', studentName: '张三', regularScore: 85, finalScore: 90, totalScore: 89, grade: 'A' },
        { id: 2, studentId: '2023002', studentName: '李四', regularScore: 75, finalScore: 80, totalScore: 79, grade: 'B' },
        { id: 3, studentId: '2023003', studentName: '王五', regularScore: 65, finalScore: 70, totalScore: 69, grade: 'C' },
        { id: 4, studentId: '2023004', studentName: '赵六', regularScore: 55, finalScore: 60, totalScore: 59, grade: 'D' },
        { id: 5, studentId: '2023005', studentName: '钱七', regularScore: 45, finalScore: 50, totalScore: 49, grade: 'F' }
      ]
      console.log('Loaded mock scores:', scoresList.value)
    }
  } catch (error) {
    console.error('加载成绩失败:', error)
  }
}

const calculateTotal = (row) => {
  return Math.round(row.regularScore * 0.3 + row.finalScore * 0.7)
}

const editScore = (row) => {
  editingScore.value = { ...row }
  editDialogVisible.value = true
}

const submitEdit = async () => {
  try {
    if (!editingScore.value.reason) {
      ElMessage.error('请填写修改原因')
      return
    }
    const userId = getUserId()
    const data = {
      regularScore: editingScore.value.regularScore,
      finalScore: editingScore.value.finalScore,
      reason: editingScore.value.reason,
      modifiedBy: userId
    }
    await teacherAPI.updateScore(editingScore.value.id, data)
    ElMessage.success('成绩已修改，修改记录已保存')
    editDialogVisible.value = false
    await loadCourseScores(selectedCourse.value)
  } catch (error) {
    console.error('修改成绩失败:', error)
    ElMessage.error('修改成绩失败')
  }
}

const saveScores = async () => {
  try {
    const userId = getUserId()
    if (!selectedCourse.value) {
      ElMessage.error('请先选择课程')
      return
    }
    const scoreData = {
      courseId: selectedCourse.value,
      scores: scoresList.value
    }
    await teacherAPI.saveScores(scoreData)
    ElMessage.success('所有成绩已保存到数据库')
  } catch (error) {
    console.error('保存成绩失败:', error)
    ElMessage.error('保存成绩失败')
  }
}

const exportScores = async () => {
  try {
    if (!selectedCourse.value) {
      ElMessage.error('请先选择课程')
      return
    }
    await teacherAPI.exportScores(selectedCourse.value)
    ElMessage.info('成绩单已导出为Excel')
    const blob = await teacherAPI.downloadScores(selectedCourse.value)
    const url = window.URL.createObjectURL(blob)
    const link = document.createElement('a')
    link.href = url
    link.download = `scores_${selectedCourse.value}_${Date.now()}.xlsx`
    link.click()
    window.URL.revokeObjectURL(url)
  } catch (error) {
    console.error('导出成绩失败:', error)
    ElMessage.error('导出成绩失败')
  }
}

// 成绩分析
const analyzeScores = async () => {
  try {
    if (!selectedCourse.value) {
      ElMessage.error('请先选择课程')
      return
    }
    const response = await teacherAPI.analyzeScores(selectedCourse.value)
    analysisData.value = response
    ElMessage.success('成绩分析完成')
  } catch (error) {
    console.error('成绩分析失败:', error)
    ElMessage.error('成绩分析失败')
  }
}

// 异常检测
const detectAnomalies = async () => {
  try {
    if (!selectedCourse.value) {
      ElMessage.error('请先选择课程')
      return
    }
    const response = await teacherAPI.detectAnomalies(selectedCourse.value)
    anomalyData.value = response
    anomalyDialogVisible.value = true
    ElMessage.success('异常检测完成')
  } catch (error) {
    console.error('异常检测失败:', error)
    ElMessage.error('异常检测失败')
  }
}

// 触发预警
const triggerWarnings = async () => {
  try {
    if (!selectedCourse.value) {
      ElMessage.error('请先选择课程')
      return
    }
    const response = await teacherAPI.triggerWarnings(selectedCourse.value)
    ElMessage.success(`成功触发 ${response} 个预警`)
  } catch (error) {
    console.error('触发预警失败:', error)
    ElMessage.error('触发预警失败')
  }
}

// 查看学生分析
const viewStudentAnalysis = async (row) => {
  try {
    const response = await teacherAPI.analyzeStudentScore(row.studentId, selectedCourse.value)
    studentAnalysisData.value = response
    studentAnalysisVisible.value = true
  } catch (error) {
    console.error('学生分析失败:', error)
    ElMessage.error('学生分析失败')
  }
}

// 获取进度条颜色
const getProgressColor = (confidence) => {
  if (confidence > 0.8) return '#f56c6c'
  if (confidence > 0.6) return '#e6a23c'
  return '#67c23a'
}

// 处理选择变化
const handleSelectionChange = (selection) => {
  selectedRows.value = selection
}

// 删除成绩
const deleteScore = async (row) => {
  try {
    if (!row.id) {
      ElMessage.error('成绩ID不存在')
      return
    }
    
    // 显示确认对话框
    if (confirm('确定要删除这条成绩记录吗？')) {
      await teacherAPI.deleteScore(row.id)
      ElMessage.success('成绩删除成功')
      // 重新加载当前课程的成绩
      await loadCourseScores(selectedCourse.value)
    }
  } catch (error) {
    console.error('删除成绩失败:', error)
    ElMessage.error('删除成绩失败')
  }
}

// 批量删除成绩
const batchDeleteScores = async () => {
  try {
    if (selectedRows.value.length === 0) {
      ElMessage.error('请选择要删除的成绩')
      return
    }
    
    // 显示确认对话框
    if (confirm(`确定要删除选中的 ${selectedRows.value.length} 条成绩记录吗？`)) {
      const scoreIds = selectedRows.value.map(row => row.id)
      await teacherAPI.batchDeleteScores(scoreIds)
      ElMessage.success(`成功删除 ${selectedRows.value.length} 条成绩记录`)
      // 重新加载当前课程的成绩
      await loadCourseScores(selectedCourse.value)
      // 清空选择
      selectedRows.value = []
    }
  } catch (error) {
    console.error('批量删除成绩失败:', error)
    ElMessage.error('批量删除成绩失败')
  }
}
</script>

<style scoped>
.teacher-scores {
  padding: 20px;
  background: linear-gradient(135deg, #f5f7fa 0%, #e8ecf1 100%);
  min-height: 100vh;
}

/* ===== 欢迎区域 ===== */
.welcome-section {
  margin-bottom: 30px;
}

.welcome-card {
  background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%);
  border-radius: 12px;
  padding: 30px;
  color: white;
  box-shadow: 0 10px 30px rgba(79, 172, 254, 0.2);
}

.welcome-content h1 {
  font-size: 28px;
  margin: 0 0 8px 0;
  font-weight: 600;
}

.welcome-content p {
  margin: 0;
  font-size: 14px;
  opacity: 0.9;
}

/* ===== 操作栏 ===== */
.toolbar-section {
  margin-bottom: 20px;
}

.toolbar {
  display: flex;
  gap: 12px;
  padding: 16px 20px;
  background: white;
  border-radius: 10px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
  align-items: center;
}

.toolbar-item {
  flex: 0 0 auto;
  min-width: 120px;
}

:deep(.toolbar .el-select) {
  flex: 1;
  min-width: 200px;
}

:deep(.toolbar .el-upload) {
  flex: 0 0 auto;
}

:deep(.toolbar .el-upload .el-button) {
  min-width: 120px;
}

/* ===== 内容卡片 ===== */
.content-card {
  background: white;
  border-radius: 10px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
  overflow: hidden;
  margin-bottom: 20px;
}

.card-title-bar {
  padding: 20px;
  border-bottom: 1px solid #f0f0f0;
  background: #fafbfc;
}

.card-title-bar h2 {
  margin: 0;
  font-size: 16px;
  color: #333;
  font-weight: 600;
}

.card-body-section {
  padding: 20px;
}

.modern-table {
  width: 100%;
}

.score-value {
  color: #667eea;
  font-weight: 600;
  padding: 2px 8px;
  background: #f0f2f8;
  border-radius: 4px;
}

/* ===== 保存节 ===== */
.action-section {
  display: flex;
  justify-content: center;
  padding: 20px;
}

.save-btn {
  min-width: 180px;
  height: 40px;
  font-size: 15px;
  font-weight: 500;
}

:deep(.el-table) {
  border-radius: 8px;
}

:deep(.el-table__header th) {
  background-color: #f8f9fa !important;
  border-color: #e8ecf1 !important;
}

:deep(.el-table__body tr) {
  border-color: #e8ecf1 !important;
}

:deep(.el-table__body tr:hover > td) {
  background-color: #f8f9fa !important;
}

/* 成绩分析样式 */
.analysis-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(150px, 1fr));
  gap: 20px;
  margin-bottom: 30px;
}

.analysis-item {
  background: #f8f9fa;
  padding: 20px;
  border-radius: 8px;
  text-align: center;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.05);
}

.analysis-label {
  font-size: 14px;
  color: #666;
  margin-bottom: 8px;
}

.analysis-value {
  font-size: 20px;
  font-weight: 600;
  color: #667eea;
}

.distribution-section {
  margin-top: 30px;
}

.distribution-section h3 {
  font-size: 16px;
  font-weight: 600;
  margin-bottom: 15px;
  color: #333;
}

.distribution-chart {
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.distribution-bar {
  display: flex;
  align-items: center;
  gap: 10px;
}

.bar-label {
  width: 60px;
  font-size: 14px;
  color: #666;
}

.bar-container {
  flex: 1;
  height: 20px;
  background: #f0f2f5;
  border-radius: 10px;
  overflow: hidden;
}

.bar-fill {
  height: 100%;
  background: linear-gradient(90deg, #667eea 0%, #764ba2 100%);
  border-radius: 10px;
  transition: width 0.3s ease;
}

.bar-count {
  width: 40px;
  text-align: right;
  font-size: 14px;
  font-weight: 500;
  color: #333;
}

/* 学生分析样式 */
.student-analysis {
  padding: 20px 0;
}

.analysis-header h3 {
  margin: 0 0 20px 0;
  font-size: 18px;
  font-weight: 600;
  color: #333;
}

.analysis-content {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.analysis-row {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(180px, 1fr));
  gap: 15px;
}

.comment-section {
  margin-top: 20px;
  padding-top: 20px;
  border-top: 1px solid #e8ecf1;
}

.comment-section h4 {
  margin: 0 0 10px 0;
  font-size: 16px;
  font-weight: 600;
  color: #333;
}

.comment-content {
  padding: 15px;
  background: #f8f9fa;
  border-radius: 8px;
  font-size: 14px;
  line-height: 1.5;
  color: #666;
}

/* 异常检测样式 */
.anomaly-analysis {
  padding: 20px 0;
}

.anomaly-header {
  margin-bottom: 20px;
}

.anomaly-header h3 {
  margin: 0 0 5px 0;
  font-size: 18px;
  font-weight: 600;
  color: #333;
}

.anomaly-header p {
  margin: 0;
  font-size: 14px;
  color: #666;
}

/* 预警状态样式 */
.warning-level {
  padding: 2px 8px;
  border-radius: 4px;
  font-size: 12px;
  font-weight: 500;
}

.低级预警 {
  background: #fdf6ec;
  color: #e6a23c;
  border: 1px solid #fde2b3;
}

.中级预警 {
  background: #fef0f0;
  color: #f56c6c;
  border: 1px solid #fbc4c4;
}

.高级预警 {
  background: #fef0f0;
  color: #f56c6c;
  border: 1px solid #fbc4c4;
  font-weight: 600;
}

.无预警 {
  background: #f0f9eb;
  color: #67c23a;
  border: 1px solid #c2e7b0;
}

/* 响应式 */
@media (max-width: 1024px) {
  .toolbar {
    flex-wrap: wrap;
  }
  
  .toolbar-item {
    flex: 1;
    min-width: 100px;
  }
  
  .analysis-grid {
    grid-template-columns: repeat(2, 1fr);
  }
  
  .analysis-row {
    grid-template-columns: repeat(2, 1fr);
  }
}

@media (max-width: 768px) {
  .analysis-grid {
    grid-template-columns: 1fr;
  }
  
  .analysis-row {
    grid-template-columns: 1fr;
  }
  
  .distribution-bar {
    flex-direction: column;
    align-items: flex-start;
    gap: 5px;
  }
  
  .bar-label {
    width: 100%;
  }
  
  .bar-count {
    width: 100%;
    text-align: left;
  }
}
</style>
