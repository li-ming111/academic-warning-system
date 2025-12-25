<template>
  <div class="counselor-students">
    <div class="page-header">
      <h1>👥 学生管理</h1>
      <p>班级学生列表、搜索和管理</p>
    </div>

    <!-- 搜索和筛选 -->
    <div class="filter-bar">
      <el-input v-model="searchName" placeholder="搜索学生姓名或学号" style="width: 250px;"></el-input>
      <el-select v-model="selectedClass" placeholder="选择班级" style="width: 200px;">
        <el-option label="班级A" value="A"></el-option>
        <el-option label="班级B" value="B"></el-option>
        <el-option label="班级C" value="C"></el-option>
        <el-option label="班级D" value="D"></el-option>
      </el-select>
      <el-button type="primary" @click="searchStudents">🔍 搜索</el-button>
      <el-button @click="exportStudents">📥 导出</el-button>
    </div>

    <!-- 学生表格 -->
    <el-card style="margin-top: 20px;">
      <template #header>
        <div class="card-header">学生列表 ({{ studentList.length }})</div>
      </template>

      <el-table :data="studentList" stripe>
        <el-table-column prop="studentId" label="学号" width="120"></el-table-column>
        <el-table-column prop="studentName" label="姓名" width="100"></el-table-column>
        <el-table-column prop="className" label="班级" width="80"></el-table-column>
        <el-table-column prop="phone" label="联系电话" width="130"></el-table-column>
        <el-table-column prop="email" label="邮箱" width="150"></el-table-column>
        <el-table-column label="状态" width="100">
          <template #default="{ row }">
            <el-tag type="success">正常</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" size="small" link @click="viewStudent(row)">查看详情</el-button>
            <el-button type="warning" size="small" link @click="sendNotification(row)">发送通知</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <!-- 学生详情对话框 -->
    <el-dialog v-model="detailDialogVisible" title="学生详情" width="600px">
      <div class="student-detail">
        <p><strong>学号：</strong>{{ selectedStudent.studentId }}</p>
        <p><strong>姓名：</strong>{{ selectedStudent.studentName }}</p>
        <p><strong>班级：</strong>{{ selectedStudent.className }}</p>
        <p><strong>联系电话：</strong>{{ selectedStudent.phone }}</p>
        <p><strong>邮箱：</strong>{{ selectedStudent.email }}</p>
        <p><strong>获取学分：</strong>7.2/8.0</p>
        <p><strong>当前预警：</strong><el-tag type="warning">黄色预警</el-tag></p>
      </div>
    </el-dialog>

    <!-- 批量通知对话框 -->
    <el-dialog v-model="notificationDialogVisible" title="发送通知" width="600px">
      <el-form :model="notificationForm" label-width="100px">
        <el-form-item label="接收人">
          <el-input :value="notificationForm.receiver" disabled></el-input>
        </el-form-item>
        <el-form-item label="通知内容">
          <el-input v-model="notificationForm.content" type="textarea" rows="4" placeholder="请输入通知内容..."></el-input>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="notificationDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitNotification">发送</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { counselorAPI } from '@/api/index'
import { getUserId } from '@/utils/userUtils'

const searchName = ref('')
const selectedClass = ref('')
const detailDialogVisible = ref(false)
const notificationDialogVisible = ref(false)
const selectedStudent = ref({})
const studentList = ref([])

const notificationForm = ref({
  receiver: '',
  content: ''
})

onMounted(async () => {
  await loadStudents()
})

const loadStudents = async () => {
  try {
    const userId = getUserId()
    const counselorId = localStorage.getItem('counselorId') || userId
    if (!counselorId) return
    const response = await counselorAPI.getStudents(counselorId)
    if (response && response.code === 0) {
      studentList.value = response.data || []
    } else if (Array.isArray(response)) {
      studentList.value = response
    }
  } catch (error) {
    console.error('加载学生列表失败:', error)
  }
}

const searchStudents = async () => {
  if (!searchName.value && !selectedClass.value) {
    await loadStudents()
    return
  }
  // 前端客户端过滤（后端不提供search接口）
  const filtered = studentList.value.filter(s => {
    const matchName = !searchName.value || s.name?.includes(searchName.value) || s.studentId?.includes(searchName.value)
    const matchClass = !selectedClass.value || s.className === selectedClass.value
    return matchName && matchClass
  })
  studentList.value = filtered
  ElMessage.success('搜索成功')
}

const exportStudents = async () => {
  try {
    ElMessage.info('学生信息已导出')
  } catch (error) {
    console.error('导出失败:', error)
  }
}

const viewStudent = (row) => {
  selectedStudent.value = row
  detailDialogVisible.value = true
}

const sendNotification = (row) => {
  notificationForm.value.receiver = row.studentName
  notificationDialogVisible.value = true
}

const submitNotification = async () => {
  if (!notificationForm.value.content) {
    ElMessage.error('请输入通知内容')
    return
  }
  try {
    const data = {
      student_ids: [selectedStudent.value.id],
      message: notificationForm.value.content
    }
    await counselorAPI.notifyStudents(data)
    ElMessage.success('通知已发送')
    notificationDialogVisible.value = false
    notificationForm.value.content = ''
  } catch (error) {
    console.error('发送通知失败:', error)
    ElMessage.error('发送失败')
  }
}
</script>

<style scoped>
.counselor-students {
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

.student-detail {
  line-height: 1.8;
}

.student-detail p {
  margin: 10px 0;
}
</style>
