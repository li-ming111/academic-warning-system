<template>
  <div class="admin-users"><div class="page-header"><h1>👥 用户管理</h1><p>用户信息、角色和权限</p></div>
    <div class="filter-bar">
      <el-select v-model="filterCollege" placeholder="学院筛选" style="width: 200px;"><el-option label="全部" value=""></el-option><el-option label="计算机科学学院" value="101"></el-option></el-select>
      <el-select v-model="filterRole" placeholder="角色筛选" style="width: 200px;"><el-option label="全部" value=""></el-option><el-option label="学生" value="1"></el-option><el-option label="教师" value="2"></el-option></el-select>
      <el-button type="primary" @click="searchUsers">🔍 筛选</el-button>
    </div>
    <el-card><template #header><div class="card-header">用户列表</div></template>
      <el-table :data="userList" stripe>
        <el-table-column prop="userId" label="ID" width="80"></el-table-column>
        <el-table-column prop="username" label="用户名" width="120"></el-table-column>
        <el-table-column prop="name" label="姓名" width="100"></el-table-column>
        <el-table-column prop="role" label="角色" width="80"><template #default="{ row }"><el-tag>{{ row.role === '1' ? '学生' : '教师' }}</el-tag></template></el-table-column>
        <el-table-column prop="collegeName" label="学院" width="150"></el-table-column>
        <el-table-column prop="status" label="状态" width="80"><template #default="{ row }"><el-tag type="success">正常</el-tag></template></el-table-column>
        <el-table-column label="操作" width="150" fixed="right"><template #default="{ row }"><el-button type="primary" size="small" link @click="editUser(row)">编辑</el-button><el-button type="danger" size="small" link>禁用</el-button></template></el-table-column>
      </el-table>
    </el-card></div>
</template>
<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { adminAPI } from '@/api/index'

const filterCollege = ref('')
const filterRole = ref('')
const userList = ref([])
const editDialogVisible = ref(false)
const editingUser = ref({})

onMounted(async () => {
  await loadUsers()
})

const loadUsers = async () => {
  try {
    const response = await adminAPI.getUsers()
    if (response && response.code === 0) {
      userList.value = response.data || []
    } else if (Array.isArray(response)) {
      userList.value = response
    }
  } catch (error) {
    console.error('加载用户列表失败:', error)
  }
}

const editUser = (row) => {
  editingUser.value = { ...row }
  editDialogVisible.value = true
}

const submitEdit = async () => {
  try {
    await adminAPI.updateUser(editingUser.value)
    ElMessage.success('用户已更新')
    editDialogVisible.value = false
    await loadUsers()
  } catch (error) {
    console.error('更新用户失败:', error)
  }
}

const searchUsers = async () => {
  await loadUsers()
  ElMessage.success('筛选成功')
}
</script>
<style scoped>
.admin-users { padding: 20px; }
.page-header { margin-bottom: 20px; }
.page-header h1 { margin: 0 0 8px 0; font-size: 24px; color: #333; }
.page-header p { margin: 0; color: #999; font-size: 14px; }
.filter-bar { display: flex; gap: 10px; margin-bottom: 20px; }
.card-header { font-size: 16px; font-weight: bold; color: #333; }
</style>
