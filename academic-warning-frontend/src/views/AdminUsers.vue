<template>
  <div class="admin-users"><div class="page-header"><h1>用户管理</h1><p>用户信息、角色和权限</p></div>
    <div class="filter-bar">
      <el-select v-model="filterCollege" placeholder="学院筛选" style="width: 200px;">
        <el-option label="全部" value=""></el-option>
        <el-option v-for="college in colleges" :key="college.id" :label="college.name" :value="college.id">
          {{ college.name }}
        </el-option>
      </el-select>
      <el-select v-model="filterRole" placeholder="角色筛选" style="width: 200px;">
        <el-option label="全部" value=""></el-option>
        <el-option label="学生" value="1"></el-option>
        <el-option label="教师" value="2"></el-option>
        <el-option label="辅导员" value="4"></el-option>
      </el-select>
      <el-button type="primary" @click="searchUsers">筛选</el-button>
      <el-button type="danger" @click="batchDeleteUsers" :disabled="selectedUsers.length === 0">批量删除</el-button>
    </div>
    <el-card><template #header><div class="card-header">用户列表</div></template>
      <el-table :data="userList" stripe style="width: 100%" row-key="id" @selection-change="handleSelectionChange">
        <el-table-column type="selection" width="55"></el-table-column>
        <el-table-column label="ID" min-width="100">
          <template #default="{ $index }">
            {{ $index + 1 }}
          </template>
        </el-table-column>
        <!-- 根据角色显示学号或工号 -->
        <el-table-column label="学号" min-width="120">
          <template #default="{ row }">
            {{ row.role === '1' || row.role === 1 ? (row.studentId || row.username) : (row.jobNumber || row.username) }}
          </template>
        </el-table-column>
        <el-table-column prop="name" label="姓名" min-width="120"></el-table-column>
        <el-table-column prop="role" label="角色" min-width="100"><template #default="{ row }"><el-tag>{{ row.role === '1' ? '学生' : row.role === '2' ? '教师' : row.role === '4' ? '辅导员' : '管理员' }}</el-tag></template></el-table-column>
        <el-table-column prop="collegeName" label="学院" min-width="200"></el-table-column>
        <el-table-column prop="status" label="状态" min-width="100"><template #default="{ row }"><el-tag :type="row.status === 1 ? 'success' : 'danger'">{{ row.status === 1 ? '正常' : '禁用' }}</el-tag></template></el-table-column>
        <el-table-column label="操作" min-width="280" fixed="right"><template #default="{ row }"><el-button type="primary" size="small" link @click="editUser(row)">编辑</el-button><el-button type="info" size="small" link @click="viewPassword(row)">查看密码</el-button><el-button :type="row.status === 1 ? 'danger' : 'success'" size="small" link @click="toggleStatus(row)">{{ row.status === 1 ? '禁用' : '启用' }}</el-button><el-button type="danger" size="small" link @click="deleteUser(row)">删除</el-button></template></el-table-column>
      </el-table>
      <div class="pagination-container" style="margin-top: 20px; display: flex; justify-content: flex-start;">
        <el-pagination
          v-model:current-page="currentPage"
          v-model:page-size="pageSize"
          :page-sizes="[10, 20, 50, 100]"
          layout="total, sizes, prev, pager, next, jumper"
          :total="total"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
        />
      </div>
    </el-card>
    <el-dialog v-model="editDialogVisible" title="编辑用户" width="400px">
      <el-form :model="editingUser" label-width="80px">
        <!-- 用户名显示为只读（派生自学号/工号） -->
        <el-form-item label="用户名">
          <el-input v-model="editingUser.username" disabled placeholder="自动由学号/工号生成"></el-input>
        </el-form-item>
        
        <!-- 根据用户角色显示学号或工号 -->
        <el-form-item v-if="editingUser.role === '1' || editingUser.role === 1" label="学号">
          <el-input v-model="editingUser.studentId" placeholder="请输入学号"></el-input>
          <small style="color: #999; margin-top: 5px; display: block;">修改学号会自动更新用户名</small>
        </el-form-item>
        <el-form-item v-else-if="editingUser.role === '2' || editingUser.role === 2" label="工号">
          <el-input v-model="editingUser.jobNumber" placeholder="请输入教师工号"></el-input>
          <small style="color: #999; margin-top: 5px; display: block;">修改工号会自动更新用户名</small>
        </el-form-item>
        <el-form-item v-else-if="editingUser.role === '4' || editingUser.role === 4" label="工号">
          <el-input v-model="editingUser.jobNumber" placeholder="请输入辅导员工号"></el-input>
          <small style="color: #999; margin-top: 5px; display: block;">修改工号会自动更新用户名</small>
        </el-form-item>
        
        <el-form-item label="姓名">
          <el-input v-model="editingUser.name"></el-input>
        </el-form-item>
        <el-form-item label="邮箱">
          <el-input v-model="editingUser.email"></el-input>
        </el-form-item>
        <el-form-item label="密码">
          <el-input v-model="editingUser.password" placeholder="留空表示不修改密码" show-password></el-input>
          <small style="color: #999; margin-top: 5px; display: block;">留空表示不修改密码，输入新密码将自动加密</small>
        </el-form-item>
        <el-form-item>
          <el-button type="info" size="small" @click="resetPassword(editingUser.id)">重置为默认密码</el-button>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="editDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitEdit">保存</el-button>
      </template>
    </el-dialog></div>
</template>
<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { adminAPI } from '@/api/index'

const filterCollege = ref('')
const filterRole = ref('')
const userList = ref([])
const editDialogVisible = ref(false)
const editingUser = ref({})
const colleges = ref([])
const currentPage = ref(1)
const pageSize = ref(20)
const total = ref(0)
const selectedUsers = ref([])

onMounted(async () => {
  await loadColleges()
  await loadUsers()
})

const loadColleges = async () => {
  try {
    console.log('开始加载学院列表...')
    console.log('调用adminAPI.getColleges...')
    const response = await adminAPI.getColleges()
    console.log('学院API响应:', response)
    console.log('响应类型:', typeof response)
    console.log('是否为数组:', Array.isArray(response))
    if (Array.isArray(response)) {
      colleges.value = response
      console.log('学院列表（直接数组）:', colleges.value)
      console.log('学院数量:', colleges.value.length)
      colleges.value.forEach((college, index) => {
        console.log(`学院${index}:`, college)
      })
    } else {
      console.log('学院API响应格式异常:', response)
      colleges.value = []
    }
  } catch (error) {
    console.error('加载学院列表失败:', error)
  }
}

const loadUsers = async () => {
  try {
    console.log('当前filterCollege.value:', filterCollege.value)
    console.log('当前filterRole.value:', filterRole.value)
    console.log('当前分页参数:', { currentPage: currentPage.value, pageSize: pageSize.value })
    const collegeId = filterCollege.value && filterCollege.value !== '' ? parseInt(filterCollege.value) : null
    const role = filterRole.value && filterRole.value !== '' ? parseInt(filterRole.value) : null
    console.log('处理后的筛选参数:', { collegeId, role })
    console.log('调用adminAPI.getUsers...')
    const response = await adminAPI.getUsers(currentPage.value, pageSize.value, collegeId, role)
    console.log('用户API响应:', response)
    console.log('响应类型:', typeof response)
    console.log('是否为数组:', Array.isArray(response))
    let users = []
    if (Array.isArray(response)) {
      users = response
      console.log('响应是数组，长度:', users.length)
      // 使用当前页数据长度作为总数，这样至少能显示正确的当前页数据量
      total.value = users.length
      console.log('设置总记录数为:', total.value)
    } else if (response && response.data && Array.isArray(response.data.data)) {
      // 新的响应格式：{ data: { total: 40, data: [...] } }
      users = response.data.data
      total.value = response.data.total || users.length
      console.log('响应是对象，数据长度:', users.length, '总数:', total.value)
    } else if (response && response.data && Array.isArray(response.data)) {
      // 旧的响应格式：{ data: [...], total: 40 }
      users = response.data
      total.value = response.total || users.length
      console.log('响应是对象（旧格式），数据长度:', users.length, '总数:', total.value)
    } else {
      console.log('响应不是数组:', response)
      users = []
      total.value = 0
      console.log('设置总记录数为0')
    }
    // 确保每个用户都有status字段
    users = users.map(user => ({
      ...user,
      status: user.status !== undefined ? user.status : 1
    }))
    userList.value = users
    console.log('处理后的用户列表:', userList.value)
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
    const userId = editingUser.value.id
    console.log('提交编辑，用户ID:', userId, '数据:', editingUser.value)
    if (!userId) {
      ElMessage.error('用户ID不存在')
      return
    }
    const response = await adminAPI.updateUser(userId, editingUser.value)
    console.log('更新响应:', response)
    ElMessage.success('用户已更新')
    
    // 如果修改的是当前登录用户（管理员），更新localStorage
    const currentUserId = parseInt(localStorage.getItem('userId'))
    if (currentUserId === userId) {
      // 更新本地存储的用户名
      if (editingUser.value.name) {
        localStorage.setItem('userName', editingUser.value.name)
      }
      if (editingUser.value.email) {
        localStorage.setItem('email', editingUser.value.email)
      }
    }
    
    editDialogVisible.value = false
    await loadUsers()
  } catch (error) {
    console.error('更新用户失败:', error)
    ElMessage.error('更新失败')
  }
}

const resetPassword = async (userId) => {
  try {
    if (!userId) {
      ElMessage.error('用户ID不存在')
      return
    }
    const response = await adminAPI.resetPassword(userId)
    ElMessage.success('密码已重置为默认密码: 123456')
    editingUser.value.password = '123456'
  } catch (error) {
    console.error('重置密码失败:', error)
    ElMessage.error('重置密码失败')
  }
}

const toggleStatus = async (row) => {
  try {
    const userId = row.id
    console.log('切换状态，用户ID:', userId, '当前状态:', row.status)
    if (!userId) {
      ElMessage.error('用户ID不存在')
      return
    }
    if (row.status === 1) {
      await adminAPI.disableUser(userId)
      ElMessage.success('用户已禁用')
      // 直接更新前端数据
      row.status = 0
    } else {
      await adminAPI.enableUser(userId)
      ElMessage.success('用户已启用')
      // 直接更新前端数据
      row.status = 1
    }
    console.log('直接更新后的程序方式，炸上loadUsers的调用')
  } catch (error) {
    console.error('状态更新失败:', error)
    ElMessage.error('操作失败')
  }
}

const deleteUser = async (row) => {
  try {
    const userId = row.id
    if (!userId) {
      ElMessage.error('用户ID不存在')
      return
    }
    // 确认删除
    await ElMessageBox.confirm(
      `确定删除用户 "${row.username}" 吗？删除后该用户将无法使用系统，且数据库中将直接删除。`,
      '警告',
      {
        confirmButtonText: '确定删除',
        cancelButtonText: '取消',
        type: 'warning',
      }
    )
    // 调用后端删除接口
    await adminAPI.deleteUser(userId)
    ElMessage.success('用户已删除')
    await loadUsers()
  } catch (error) {
    if (error === 'cancel') {
      // 用户点击了取消
      return
    }
    console.error('删除用户失败:', error)
    ElMessage.error('删除失败')
  }
}

const viewPassword = async (row) => {
  try {
    const userId = row.id
    if (!userId) {
      ElMessage.error('用户ID不存在')
      return
    }
    const response = await adminAPI.viewPassword(userId)
    ElMessage.success(`用户密码: ${response}`)
  } catch (error) {
    console.error('查看密码失败:', error)
    ElMessage.error('查看密码失败')
  }
}

const searchUsers = async () => {
  try {
    console.log('点击筛选按钮，当前筛选值:', { filterCollege: filterCollege.value, filterRole: filterRole.value })
    currentPage.value = 1 // 筛选时重置到第一页
    await loadUsers()
  } catch (error) {
    console.error('筛选用户失败:', error)
    ElMessage.error('筛选失败')
  }
}

const handleCurrentChange = (val) => {
  console.log('当前页码变更为:', val)
  currentPage.value = val
  loadUsers()
}

const handleSizeChange = (val) => {
  console.log('每页条数变更为:', val)
  pageSize.value = val
  currentPage.value = 1 // 每页条数变化时重置到第一页
  loadUsers()
}

const handleSelectionChange = (val) => {
  console.log('选择的用户:', val)
  selectedUsers.value = val
}

const batchDeleteUsers = async () => {
  if (selectedUsers.value.length === 0) {
    ElMessage.warning('请先选择要删除的用户')
    return
  }
  
  try {
    // 确认删除
    await ElMessageBox.confirm(
      `确定删除选中的 ${selectedUsers.value.length} 个用户吗？删除后这些用户将无法使用系统，且数据库中将直接删除。`,
      '警告',
      {
        confirmButtonText: '确定删除',
        cancelButtonText: '取消',
        type: 'warning',
      }
    )
    
    // 调用后端批量删除接口
    const userIds = selectedUsers.value.map(user => user.id)
    console.log('批量删除用户ID:', userIds)
    
    // 由于没有批量删除接口，我们一个一个删除
    for (const userId of userIds) {
      await adminAPI.deleteUser(userId)
    }
    
    ElMessage.success(`成功删除 ${selectedUsers.value.length} 个用户`)
    
    // 清空选择
    selectedUsers.value = []
    // 重新加载用户列表
    await loadUsers()
  } catch (error) {
    if (error === 'cancel') {
      // 用户点击了取消
      return
    }
    console.error('批量删除用户失败:', error)
    ElMessage.error('批量删除失败')
  }
}

</script>
<style scoped>
.admin-users { 
  padding: 20px; 
  background-color: #f8f9fa !important;
  min-height: 100%;
}
.page-header { margin-bottom: 20px; }
.page-header h1 { margin: 0 0 8px 0; font-size: 24px; color: #333; }
.page-header p { margin: 0; color: #999; font-size: 14px; }
.filter-bar { display: flex; gap: 10px; margin-bottom: 20px; }
.card-header { font-size: 16px; font-weight: bold; color: #333; }
/* 全局样式覆盖 */
.admin-users :deep(.el-card) {
  border: 1px solid #e9ecef !important;
}
.admin-users :deep(.el-button--primary) {
  background-color: #667eea !important;
  border-color: #667eea !important;
}
.admin-users :deep(.el-button--primary:hover) {
  background-color: #5568d3 !important;
  border-color: #5568d3 !important;
}
</style>
