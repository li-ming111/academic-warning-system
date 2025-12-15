<template>
  <div class="admin-majors">
    <div class="page-header">
      <h1>📚 专业管理</h1>
      <p>专业信息管理</p>
    </div>
    <div class="action-bar">
      <el-button type="primary" @click="addDialogVisible = true">➕ 添加专业</el-button>
    </div>
    <el-card>
      <template #header>
        <div class="card-header">专业列表</div>
      </template>
      <el-table :data="majorList" stripe>
        <el-table-column prop="id" label="专业ID" width="100"></el-table-column>
        <el-table-column prop="code" label="专业代码" width="120"></el-table-column>
        <el-table-column prop="name" label="专业名称" width="150"></el-table-column>
        <el-table-column prop="collegeId" label="学院ID" width="100"></el-table-column>
        <el-table-column label="操作" width="150" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" size="small" link @click="editMajor(row)">编辑</el-button>
            <el-button type="danger" size="small" link @click="deleteMajor(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>
    <el-dialog v-model="addDialogVisible" title="添加专业" width="500px">
      <el-form :model="majorForm" label-width="100px">
        <el-form-item label="专业代码">
          <el-input v-model="majorForm.code" placeholder="输入专业代码"></el-input>
        </el-form-item>
        <el-form-item label="专业名称">
          <el-input v-model="majorForm.name" placeholder="输入专业名称"></el-input>
        </el-form-item>
        <el-form-item label="所属学院">
          <el-input v-model="majorForm.collegeId" placeholder="输入学院ID"></el-input>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="addDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitAdd">确认</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { adminAPI } from '@/api/index'

const addDialogVisible = ref(false)
const majorList = ref([])

const majorForm = ref({ code: '', name: '', collegeId: '' })

onMounted(async () => {
  await loadMajors()
})

const loadMajors = async () => {
  try {
    const response = await adminAPI.getMajors()
    if (response && response.code === 0) {
      majorList.value = response.data || []
    } else if (Array.isArray(response)) {
      majorList.value = response
    }
  } catch (error) {
    console.error('加载专业列表失败:', error)
  }
}

const editMajor = (row) => ElMessage.info(`编辑${row.name}`)

const deleteMajor = async (row) => {
  try {
    await adminAPI.deleteMajor(row.id)
    ElMessage.success('专业已削除')
    await loadMajors()
  } catch (error) {
    console.error('删除专业失败:', error)
    ElMessage.error('删除失败')
  }
}

const submitAdd = async () => {
  if (!majorForm.value.code || !majorForm.value.name) {
    ElMessage.error('请填写专业代码和名称')
    return
  }
  try {
    await adminAPI.createMajor(majorForm.value)
    ElMessage.success('专业已添加')
    addDialogVisible.value = false
    majorForm.value = { code: '', name: '', collegeId: '' }
    await loadMajors()
  } catch (error) {
    console.error('添加专业失败:', error)
    ElMessage.error('添加失败')
  }
}

</script>

<style scoped>
.admin-majors {
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
