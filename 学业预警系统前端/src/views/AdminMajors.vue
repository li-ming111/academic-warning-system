<template>
  <div class="admin-majors" style="background-color: #f8f9fa !important; min-height: 100vh;">
    <div class="page-header">
      <h1>专业管理</h1>
      <p>专业信息管理</p>
    </div>
    <div class="action-bar">
      <el-select v-model="filterCollegeId" placeholder="按学院筛选" style="width: 200px;" @change="loadMajors">
        <el-option label="全部学院" value=""></el-option>
        <el-option v-for="college in collegeList" :key="college.id" :label="college.name" :value="college.id"></el-option>
      </el-select>
      <el-button type="primary" @click="addDialogVisible = true">添加专业</el-button>
    </div>
    <el-card>
      <template #header>
        <div class="card-header">专业列表</div>
      </template>
      <el-table :data="majorList" stripe>
        <el-table-column prop="id" label="专业ID" width="100"></el-table-column>
        <el-table-column prop="code" label="专业代码" width="120"></el-table-column>
        <el-table-column prop="name" label="专业名称" width="150"></el-table-column>
        <el-table-column label="所属学院" width="150">
          <template #default="{ row }">
            {{ collegeList.find(c => c.id === row.collegeId)?.name || '未中字' }}
          </template>
        </el-table-column>
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
        <el-form-item label="专业名称">
          <el-input v-model="majorForm.name" placeholder="输入专业名称"></el-input>
        </el-form-item>
        <el-form-item label="所属学院">
          <el-select v-model="majorForm.collegeId" placeholder="选择学院">
            <el-option v-for="college in collegeList" :key="college.id" :label="college.name" :value="college.id"></el-option>
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="addDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitAdd">确认</el-button>
      </template>
    </el-dialog>

    <!-- 编辑专业对话框 -->
    <el-dialog v-model="editDialogVisible" title="编辑专业" width="500px">
      <el-form :model="editingMajor" label-width="100px">
        <el-form-item label="专业名称">
          <el-input v-model="editingMajor.name" placeholder="输入专业名称"></el-input>
        </el-form-item>
        <el-form-item label="所属学院">
          <el-select v-model="editingMajor.collegeId" placeholder="选择学院">
            <el-option v-for="college in collegeList" :key="college.id" :label="college.name" :value="college.id"></el-option>
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="editDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitEditMajor">确认</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { adminAPI } from '@/api/index'

const addDialogVisible = ref(false)
const editDialogVisible = ref(false)
const majorList = ref([])
const collegeList = ref([])
const filterCollegeId = ref('')

const majorForm = ref({ code: '', name: '', collegeId: '' })
const editingMajor = ref({ id: null, code: '', name: '', collegeId: '' })

onMounted(async () => {
  await loadColleges()
  await loadMajors()
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

const loadMajors = async () => {
  try {
    const response = await adminAPI.getMajors()
    let allMajors = []
    if (response && response.code === 0) {
      allMajors = response.data || []
    } else if (Array.isArray(response)) {
      allMajors = response
    }
    
    // 根据筛选条件过滚数据
    if (filterCollegeId.value) {
      majorList.value = allMajors.filter(major => major.collegeId === parseInt(filterCollegeId.value))
    } else {
      majorList.value = allMajors
    }
  } catch (error) {
    console.error('加载专业列表失败:', error)
  }
}

const editMajor = (row) => {
  editingMajor.value = { ...row }
  editDialogVisible.value = true
}

const submitEditMajor = async () => {
  if (!editingMajor.value.name) {
    ElMessage.error('请填写专业名称')
    return
  }
  if (!editingMajor.value.collegeId) {
    ElMessage.error('请选择所属学院')
    return
  }
  try {
    const data = {
      name: editingMajor.value.name,
      collegeId: parseInt(editingMajor.value.collegeId)
    }
    await adminAPI.updateMajor(editingMajor.value.id, data)
    ElMessage.success('专业已更新')
    editDialogVisible.value = false
    // 直接更新列表中的数据
    const index = majorList.value.findIndex(item => item.id === editingMajor.value.id)
    if (index !== -1) {
      majorList.value[index].name = editingMajor.value.name
      majorList.value[index].collegeId = editingMajor.value.collegeId
    }
  } catch (error) {
    console.error('更新专业失败:', error)
    ElMessage.error('更新失败')
  }
}

const deleteMajor = async (row) => {
  try {
    await adminAPI.deleteMajor(row.id)
    ElMessage.success('专业已删除')
    // 直接从列表中移除该行
    majorList.value = majorList.value.filter(item => item.id !== row.id)
  } catch (error) {
    console.error('删除专业失败:', error)
    ElMessage.error('删除失败')
  }
}

const submitAdd = async () => {
  if (!majorForm.value.name) {
    ElMessage.error('请填写专业名称')
    return
  }
  if (!majorForm.value.collegeId) {
    ElMessage.error('请选择所属学院')
    return
  }
  try {
    // 自动生成专业代码（基于上一个最大ID）
    const maxId = majorList.value.length > 0 ? Math.max(...majorList.value.map(m => parseInt(m.id))) : 0
    const newCode = String(maxId + 1).padStart(2, '0')
    
    const data = {
      code: newCode,
      name: majorForm.value.name,
      collegeId: parseInt(majorForm.value.collegeId)
    }
    await adminAPI.createMajor(data)
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
  background-color: #f8f9fa !important;
  min-height: 100vh;
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

/* 全局样式覆盖 */
.admin-majors :deep(.el-card) {
  border: 1px solid #e9ecef !important;
}

.admin-majors :deep(.el-button--primary) {
  background-color: #667eea !important;
  border-color: #667eea !important;
}

.admin-majors :deep(.el-button--primary:hover) {
  background-color: #5568d3 !important;
  border-color: #5568d3 !important;
}
</style>
