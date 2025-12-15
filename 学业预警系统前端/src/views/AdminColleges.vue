<template>
  <div class="admin-colleges">
    <div class="page-header">
      <h1>🏫 学院管理</h1>
      <p>学院CRUD操作和数据管理</p>
    </div>

    <!-- 操作按钮 -->
    <div class="action-bar">
      <el-button type="primary" @click="addCollegDialogVisible = true">➕ 添加学院</el-button>
      <el-button @click="exportColleges">📥 导出</el-button>
    </div>

    <!-- 学院列表 -->
    <el-card>
      <template #header>
        <div class="card-header">学院列表</div>
      </template>

      <el-table :data="collegeList" stripe>
        <el-table-column prop="id" label="学院ID" width="100"></el-table-column>
        <el-table-column prop="name" label="学院名称" width="200"></el-table-column>
        <el-table-column prop="studentCount" label="学生数" width="100"></el-table-column>
        <el-table-column prop="teacherCount" label="教师数" width="100"></el-table-column>
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" size="small" link @click="editCollege(row)">编辑</el-button>
            <el-button type="danger" size="small" link @click="deleteCollege(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <!-- 添加对话框 -->
    <el-dialog v-model="addCollegDialogVisible" title="添加学院" width="500px">
      <el-form :model="collegeForm" label-width="100px">
        <el-form-item label="学院名称">
          <el-input v-model="collegeForm.name" placeholder="请输入学院名称"></el-input>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="addCollegDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitAddCollege">确认</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { adminAPI } from '@/api/index'

const addCollegDialogVisible = ref(false)
const collegeList = ref([])

const collegeForm = ref({
  name: ''
})

onMounted(async () => {
  await loadColleges()
})

const loadColleges = async () => {
  try {
    const response = await adminAPI.getColleges()
    if (response && response.code === 0) {
      collegeList.value = response.data || []
    } else if (Array.isArray(response)) {
      collegeList.value = response
    }
  } catch (error) {
    console.error('加载学院列表失败:', error)
  }
}

const editCollege = (row) => {
  ElMessage.info(`编辑${row.name}`)
}

const deleteCollege = async (row) => {
  try {
    await adminAPI.deleteCollege(row.id)
    ElMessage.success('学院已删除')
    await loadColleges()
  } catch (error) {
    console.error('删除学院失败:', error)
  }
}

const submitAddCollege = async () => {
  if (!collegeForm.value.name) {
    ElMessage.error('请输入学院名称')
    return
  }
  try {
    const data = { name: collegeForm.value.name }
    await adminAPI.createCollege(data)
    ElMessage.success('学院已添加')
    addCollegDialogVisible.value = false
    collegeForm.value.name = ''
    await loadColleges()
  } catch (error) {
    console.error('添加学院失败:', error)
    ElMessage.error('添加失败')
  }
}

const exportColleges = async () => {
  try {
    await adminAPI.exportColleges()
    ElMessage.success('学院列表已导出')
  } catch (error) {
    console.error('导出失败:', error)
  }
}
</script>

<style scoped>
.admin-colleges {
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

.action-bar {
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
