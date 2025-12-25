<template>
  <div class="teacher-courses">
    <div class="page-header">
      <h1>📚 学生选修课管理</h1>
      <p>查看学生选修课、推荐选课、学分核查和沟通记录</p>
    </div>

    <!-- 统计数据 -->
    <div class="stats-grid">
      <div class="stat-card">
        <div class="stat-icon">📚</div>
        <div class="stat-content">
          <div class="stat-label">选修课总数</div>
          <div class="stat-number">12</div>
        </div>
      </div>
      <div class="stat-card">
        <div class="stat-icon">👥</div>
        <div class="stat-content">
          <div class="stat-label">选修学生</div>
          <div class="stat-number">120</div>
        </div>
      </div>
      <div class="stat-card">
        <div class="stat-icon">⭐</div>
        <div class="stat-content">
          <div class="stat-label">平均评分</div>
          <div class="stat-number">4.5</div>
        </div>
      </div>
      <div class="stat-card">
        <div class="stat-icon">✅</div>
        <div class="stat-content">
          <div class="stat-label">及格率</div>
          <div class="stat-number">95%</div>
        </div>
      </div>
    </div>

    <!-- 选修课列表 -->
    <el-card style="margin-bottom: 20px;">
      <template #header>
        <div class="card-header">📖 选修课列表</div>
      </template>

      <el-table :data="courseList" stripe>
        <el-table-column prop="courseName" label="课程名称" width="150"></el-table-column>
        <el-table-column prop="credits" label="学分" width="80"></el-table-column>
        <el-table-column prop="type" label="课程类型" width="100">
          <template #default="{ row }">
            <el-tag>{{ row.type }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="enrollmentCount" label="选修人数" width="100"></el-table-column>
        <el-table-column prop="passRate" label="及格率" width="100">
          <template #default="{ row }">
            <span style="color: #67c23a; font-weight: bold;">{{ row.passRate }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="rating" label="评分" width="100">
          <template #default="{ row }">
            <el-rate v-model="row.rating" :max="5" size="small" disabled></el-rate>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="150" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" size="small" link @click="viewStudents(row)">学生</el-button>
            <el-button type="info" size="small" link @click="recommendCourse(row)">推荐</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <!-- 学生选课情况 -->
    <el-card style="margin-bottom: 20px;">
      <template #header>
        <div class="card-header">👤 学生选修情况</div>
      </template>

      <el-table :data="studentCourses" stripe>
        <el-table-column prop="studentId" label="学号" width="120"></el-table-column>
        <el-table-column prop="studentName" label="姓名" width="100"></el-table-column>
        <el-table-column prop="courseName" label="选修课" width="150"></el-table-column>
        <el-table-column prop="score" label="成绩" width="80">
          <template #default="{ row }">
            <span :style="{ color: row.score >= 60 ? '#67c23a' : '#f56c6c' }">{{ row.score }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="credits" label="获得学分" width="100"></el-table-column>
        <el-table-column label="操作" width="120" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" size="small" link @click="viewCommunication(row)">沟通</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <!-- 学分统计 -->
    <el-card>
      <template #header>
        <div class="card-header">📊 学分核查</div>
      </template>

      <el-table :data="creditStats" stripe>
        <el-table-column prop="studentId" label="学号" width="120"></el-table-column>
        <el-table-column prop="studentName" label="姓名" width="100"></el-table-column>
        <el-table-column prop="completedCredits" label="已修学分" width="100"></el-table-column>
        <el-table-column prop="requiredCredits" label="需修学分" width="100"></el-table-column>
        <el-table-column label="状态" width="100">
          <template #default="{ row }">
            <el-tag v-if="row.completedCredits >= row.requiredCredits" type="success">达标</el-tag>
            <el-tag v-else type="warning">不足{{ row.requiredCredits - row.completedCredits }}学分</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="推荐课程" width="150">
          <template #default="{ row }">
            <el-button type="primary" size="small" @click="recommendForStudent(row)">推荐选课</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <!-- 推荐课程对话框 -->
    <el-dialog v-model="recommendDialogVisible" title="推荐选修课" width="600px">
      <el-form :model="recommendForm" label-width="120px">
        <el-form-item label="学生">
          <el-input :value="recommendForm.studentName" disabled></el-input>
        </el-form-item>
        <el-form-item label="推荐课程">
          <el-select v-model="recommendForm.courses" multiple placeholder="选择推荐课程">
            <el-option label="人工智能基础" value="ai"></el-option>
            <el-option label="机器学习" value="ml"></el-option>
            <el-option label="自然语言处理" value="nlp"></el-option>
            <el-option label="计算机视觉" value="cv"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="推荐理由">
          <el-input v-model="recommendForm.reason" type="textarea" rows="4" placeholder="输入推荐理由..."></el-input>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="recommendDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitRecommend">发送推荐</el-button>
      </template>
    </el-dialog>

    <!-- 沟通记录对话框 -->
    <el-dialog v-model="communicationDialogVisible" title="沟通记录" width="600px">
      <div class="communication-content">
        <p><strong>学生：</strong>{{ selectedStudent.studentName }}</p>
        <p><strong>课程：</strong>{{ selectedStudent.courseName }}</p>
        <p><strong>沟通记录：</strong></p>
        <el-timeline>
          <el-timeline-item v-for="i in 2" :key="i" :timestamp="`2024-12-0${i}`">
            <p v-if="i === 1">学生对NLP感兴趣，推荐其选修《自然语言处理》课程</p>
            <p v-else>学生已确认选修，待注册</p>
          </el-timeline-item>
        </el-timeline>
        <el-input v-model="newCommunication" type="textarea" rows="3" placeholder="输入新的沟通内容..."></el-input>
        <el-button type="primary" style="margin-top: 10px;" @click="addCommunication">保存记录</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { teacherAPI } from '@/api/index'
import { getUserId } from '@/utils/userUtils'

const recommendDialogVisible = ref(false)
const communicationDialogVisible = ref(false)
const newCommunication = ref('')

const courseList = ref([])
const studentCourses = ref([])
const creditStats = ref([])
const courseStats = ref({
  totalCourses: 0,
  totalStudents: 0,
  averageRating: 0,
  passRate: 0
})

const recommendForm = ref({
  studentName: '',
  courses: [],
  reason: ''
})

const selectedStudent = ref({
  studentName: '',
  courseName: ''
})

onMounted(async () => {
  await loadCourseData()
})

// 加载课程数据
const loadCourseData = async () => {
  try {
    const userId = localStorage.getItem('userId') || getUserId()
    const teacherId = localStorage.getItem('teacherId') || userId
    if (!teacherId) return
    
    // 加载教师的选修课数据
    const enrollments = await teacherAPI.getEnrollments(teacherId)
    if (enrollments && Array.isArray(enrollments)) {
      // 根据选修课数据计算统计信息
      const coursesMap = new Map()
      let totalStudents = 0
      
      for (const enrollment of enrollments) {
        if (enrollment.courseId) {
          if (!coursesMap.has(enrollment.courseId)) {
            coursesMap.set(enrollment.courseId, {
              courseName: enrollment.courseName || `课程${enrollment.courseId}`,
              courseId: enrollment.courseId,
              credits: enrollment.credits || 0,
              type: enrollment.type || '选修',
              students: []
            })
          }
          coursesMap.get(enrollment.courseId).students.push(enrollment)
          totalStudents++
        }
      }
      
      // 构建课程列表
      courseList.value = Array.from(coursesMap.values()).map(course => ({
        ...course,
        enrollmentCount: course.students.length,
        passRate: '95%',
        rating: 4.5
      }))
      
      // 更新统计信息
      courseStats.value.totalCourses = courseList.value.length
      courseStats.value.totalStudents = totalStudents
      courseStats.value.averageRating = 4.5
      courseStats.value.passRate = '95%'
    }
  } catch (error) {
    console.error('加载课程数据失败:', error)
  }
}

const viewStudents = (row) => {
  ElMessage.info(`查看《${row.courseName}》选修学生`)
}

const recommendCourse = (row) => {
  recommendForm.value.studentName = '请选择学生'
  recommendDialogVisible.value = true
}

const recommendForStudent = (row) => {
  recommendForm.value.studentName = row.studentName
  recommendDialogVisible.value = true
}

const submitRecommend = async () => {
  if (!recommendForm.value.courses.length) {
    ElMessage.error('请选择推荐课程')
    return
  }
  try {
    const data = {
      studentName: recommendForm.value.studentName,
      courses: recommendForm.value.courses,
      reason: recommendForm.value.reason
    }
    await teacherAPI.recommendCourse(data)
    ElMessage.success('推荐已发送')
    recommendDialogVisible.value = false
    await loadCourseData()
  } catch (error) {
    console.error('发送推荐失败:', error)
    ElMessage.error('发送推荐失败')
  }
}

const viewCommunication = (row) => {
  selectedStudent.value = row
  communicationDialogVisible.value = true
}

const addCommunication = async () => {
  if (!newCommunication.value) {
    ElMessage.error('请输入沟通内容')
    return
  }
  try {
    const userId = getUserId()
    const data = {
      teacherId: userId,
      studentId: selectedStudent.value.studentId,
      content: newCommunication.value
    }
    await teacherAPI.saveCommunication(data)
    ElMessage.success('沟通记录已保存')
    newCommunication.value = ''
    await loadCourseData()
  } catch (error) {
    console.error('保存沟通记录失败:', error)
    ElMessage.error('保存沟通记录失败')
  }
}
</script>

<style scoped>
.teacher-courses {
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

.stats-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(160px, 1fr));
  gap: 20px;
  margin-bottom: 24px;
}

.stat-card {
  background: white;
  border-radius: 16px;
  padding: 24px;
  display: flex;
  align-items: center;
  gap: 16px;
  box-shadow: 0 6px 20px rgba(0, 0, 0, 0.08);
  transition: all 0.4s cubic-bezier(0.4, 0, 0.2, 1);
  border: 2px solid transparent;
}

.stat-card:hover {
  box-shadow: 0 12px 32px rgba(0, 0, 0, 0.15);
  transform: translateY(-8px);
  border-color: #667eea;
}

.stat-icon {
  font-size: 36px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.stat-content {
  flex: 1;
}

.stat-label {
  color: #999;
  font-size: 13px;
  margin-bottom: 6px;
  font-weight: 500;
}

.stat-number {
  font-size: 28px;
  font-weight: bold;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.card-header {
  font-size: 18px;
  font-weight: bold;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.communication-content {
  line-height: 1.8;
}

.communication-content p {
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
</style>
