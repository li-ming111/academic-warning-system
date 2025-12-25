<template>
  <div class="teacher-scores">
    <div class="page-header">
      <h1>📝 学生成绩管理</h1>
      <p>批量录入、查看和修改学生成绩</p>
    </div>

    <!-- 课程选择和操作 -->
    <div class="action-bar">
      <el-select v-model="selectedCourse" placeholder="选择课程" @change="loadCourseScores">
        <el-option v-for="course in courses" :key="course.id" :label="course.courseName" :value="course.id"></el-option>
      </el-select>
      <el-button type="primary">📤 导入Excel</el-button>
      <el-button @click="exportScores">📥 导出成绩</el-button>
    </div>

    <!-- 成绩表格 -->
    <el-card>
      <template #header>
        <div class="card-header">{{ selectedCourse ? '课程成绩列表' : '请选择课程' }}</div>
      </template>

      <el-table :data="scoresList" stripe v-if="selectedCourse">
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
            <span style="color: #409eff; font-weight: bold;">{{ calculateTotal(row) }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="grade" label="等级" width="80"></el-table-column>
        <el-table-column label="操作" width="120" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" size="small" link @click="editScore(row)">修改</el-button>
            <el-button type="danger" size="small" link>删除</el-button>
          </template>
        </el-table-column>
      </el-table>
      <el-empty v-else description="请先选择课程"></el-empty>
    </el-card>

    <!-- 保存按钮 -->
    <div class="action-footer">
      <el-button type="success" size="large" @click="saveScores">💾 保存成绩</el-button>
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

onMounted(async () => {
  await loadCourses()
})

// 加载课程列表
const loadCourses = async () => {
  try {
    const teacherId = localStorage.getItem('teacherId') || getUserId()
    if (!teacherId) return
    const response = await teacherAPI.getCourses(teacherId)
    if (Array.isArray(response)) {
      courses.value = response
    }
  } catch (error) {
    console.error('加载课程失败:', error)
  }
}

// 简选课程时加载成绩
const loadCourseScores = async (courseId) => {
  try {
    const response = await teacherAPI.getScores(courseId)
    if (Array.isArray(response)) {
      scoresList.value = response
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
</script>

<style scoped>
.teacher-scores {
  padding: 24px;
}

.page-header {
  margin-bottom: 28px;
  padding: 32px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border-radius: 16px;
  color: white;
  box-shadow: 0 8px 24px rgba(102, 126, 234, 0.4);
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

.card-header {
  font-size: 18px;
  font-weight: bold;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.action-footer {
  display: flex;
  justify-content: center;
  gap: 16px;
  margin-top: 24px;
  padding: 20px;
  background: white;
  border-radius: 16px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.08);
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
</style>
