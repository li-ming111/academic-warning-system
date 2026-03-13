<template>
  <div class="class-management-container">
    <!-- 标签页 -->
    <el-tabs v-model="activeTab" class="tabs">
      <!-- 我的班级 -->
      <el-tab-pane label="我的班级" name="my-classes">
        <div class="my-classes-section">
          <el-empty v-if="myClasses.length === 0" description="暂无管理的班级"></el-empty>
          
          <div v-else class="classes-grid">
            <div v-for="clazz in myClasses" :key="clazz.id" class="class-card">
              <div class="class-header">
                <h3>{{ clazz.name }}</h3>
                <el-tag type="success">已管理</el-tag>
              </div>
              <div class="class-info">
                <p><span class="label">学生数：</span> {{ clazz.studentCount }}</p>
                <p><span class="label">班级ID：</span> {{ clazz.id }}</p>
              </div>
              <div class="class-actions">
                <el-button type="primary" size="small" @click="goToStudents(clazz.id)">
                  查看学生
                </el-button>
              </div>
            </div>
          </div>
        </div>
      </el-tab-pane>

      <!-- 添加班级 -->
      <el-tab-pane label="添加班级" name="add-class">
        <div class="add-class-section">
          <div class="search-box">
            <el-input
              v-model="searchKeyword"
              placeholder="输入班级名称搜索"
              clearable
              @input="searchClasses"
              class="search-input"
            >
              <template #prefix>
                <el-icon><Search /></el-icon>
              </template>
            </el-input>
          </div>

          <el-empty v-if="!searchPerformed" description="输入班级名称开始搜索"></el-empty>
          <el-empty v-else-if="searchResults.length === 0" description="未找到匹配的班级"></el-empty>

          <div v-else class="search-results">
            <div v-for="clazz in searchResults" :key="clazz.id" class="result-card">
              <div class="result-header">
                <h4>{{ clazz.name }}</h4>
              </div>
              <div class="result-info">
                <p><span class="label">学生数：</span> {{ clazz.studentCount }}</p>
                <p><span class="label">专业：</span> {{ clazz.majorId || '未指定' }}</p>
                <p v-if="clazz.hasCounselor" class="warning">
                  <el-icon><Warning /></el-icon>
                  已有辅导员管理
                </p>
              </div>
              <div class="result-actions">
                <el-button
                  v-if="!clazz.hasCounselor"
                  type="primary"
                  @click="openApplyDialog(clazz)"
                >
                  申请管理
                </el-button>
                <el-button v-else disabled>已有辅导员</el-button>
              </div>
            </div>
          </div>
        </div>
      </el-tab-pane>

      <!-- 我的申请 -->
      <el-tab-pane label="我的申请" name="my-requests">
        <div class="requests-section">
          <el-empty v-if="myRequests.length === 0" description="暂无申请记录"></el-empty>

          <div v-else class="requests-list">
            <div v-for="req in myRequests" :key="req.id" class="request-item">
              <div class="request-header">
                <h4>{{ req.className }}</h4>
                <el-tag :type="getStatusType(req.status)">{{ getStatusText(req.status) }}</el-tag>
              </div>
              <div class="request-info">
                <p><span class="label">申请原因：</span> {{ req.reason || '无' }}</p>
                <p><span class="label">申请时间：</span> {{ formatDate(req.createdAt) }}</p>
              </div>
            </div>
          </div>
        </div>
      </el-tab-pane>
    </el-tabs>

    <!-- 申请管理班级对话框 -->
    <el-dialog v-model="applyDialogVisible" title="申请管理班级" width="500px">
      <el-form ref="applyFormRef" :model="applyForm" label-width="100px">
        <el-form-item label="班级名称">
          <el-input v-model="applyForm.className" disabled></el-input>
        </el-form-item>
        <el-form-item label="申请原因" prop="reason">
          <el-input
            v-model="applyForm.reason"
            type="textarea"
            rows="4"
            placeholder="请填写申请原因"
          ></el-input>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="applyDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitApply" :loading="applyLoading">
          提交申请
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { Search, Warning } from '@element-plus/icons-vue'
import { studentAPI } from '@/api/index'

const activeTab = ref('my-classes')
const myClasses = ref([])
const searchKeyword = ref('')
const searchResults = ref([])
const searchPerformed = ref(false)
const myRequests = ref([])
const applyDialogVisible = ref(false)
const applyLoading = ref(false)
const applyForm = ref({
  classId: null,
  className: '',
  reason: ''
})

const counselorId = ref(null)

// 获取辅导员ID（从localStorage或store）
const getCounselorId = () => {
  const userId = localStorage.getItem('userId')
  return userId ? parseInt(userId) : null
}

// 加载我的班级
const loadMyClasses = async () => {
  try {
    const userId = getCounselorId()
    if (!userId) {
      ElMessage.error('用户ID不存在')
      return
    }
    
    const response = await studentAPI.request('/counselors/class-management/my-classes', 'GET', {
      counselorId: userId
    })
    
    if (response && response.data) {
      myClasses.value = response.data
    }
  } catch (error) {
    console.error('加载班级失败:', error)
    ElMessage.error('加载班级失败')
  }
}

// 搜索班级
const searchClasses = async () => {
  if (!searchKeyword.value || searchKeyword.value.trim() === '') {
    searchResults.value = []
    searchPerformed.value = false
    return
  }

  try {
    const response = await studentAPI.request('/counselors/class-management/search', 'GET', {
      keyword: searchKeyword.value
    })
    
    if (response && response.data) {
      searchResults.value = response.data
      searchPerformed.value = true
    }
  } catch (error) {
    console.error('搜索班级失败:', error)
    ElMessage.error('搜索班级失败')
  }
}

// 打开申请对话框
const openApplyDialog = (clazz) => {
  applyForm.value = {
    classId: clazz.id,
    className: clazz.name,
    reason: ''
  }
  applyDialogVisible.value = true
}

// 提交申请
const submitApply = async () => {
  if (!applyForm.value.reason || applyForm.value.reason.trim() === '') {
    ElMessage.warning('请填写申请原因')
    return
  }

  applyLoading.value = true
  try {
    const userId = getCounselorId()
    const response = await studentAPI.request('/counselors/class-management/apply', 'POST', {
      counselorId: userId,
      classId: applyForm.value.classId,
      reason: applyForm.value.reason
    })
    
    if (response) {
      ElMessage.success('申请已提交，等待管理员审核')
      applyDialogVisible.value = false
      loadMyRequests()
      searchClasses()
    }
  } catch (error) {
    console.error('提交申请失败:', error)
    ElMessage.error(error.message || '提交申请失败')
  } finally {
    applyLoading.value = false
  }
}

// 加载我的申请
const loadMyRequests = async () => {
  try {
    const userId = getCounselorId()
    const response = await studentAPI.request('/counselors/class-management/requests', 'GET', {
      counselorId: userId
    })
    
    if (response && response.data) {
      myRequests.value = response.data
    }
  } catch (error) {
    console.error('加载申请失败:', error)
  }
}

// 跳转到学生列表页面
const goToStudents = (classId) => {
  // 可以使用 router 跳转到学生列表页面
  // this.$router.push({ name: 'CounselorStudents', params: { classId } })
  ElMessage.info('学生列表功能开发中...')
}

// 获取状态文本
const getStatusText = (status) => {
  const statusMap = {
    'pending': '待审核',
    'approved': '已批准',
    'rejected': '已拒绝'
  }
  return statusMap[status] || status
}

// 获取状态标签类型
const getStatusType = (status) => {
  const typeMap = {
    'pending': 'warning',
    'approved': 'success',
    'rejected': 'danger'
  }
  return typeMap[status] || 'info'
}

// 格式化日期
const formatDate = (date) => {
  if (!date) return ''
  const d = new Date(date)
  return `${d.getFullYear()}-${String(d.getMonth() + 1).padStart(2, '0')}-${String(d.getDate()).padStart(2, '0')}`
}

onMounted(() => {
  counselorId.value = getCounselorId()
  loadMyClasses()
  loadMyRequests()
})
</script>

<style scoped>
.class-management-container {
  padding: 20px;
  background: linear-gradient(135deg, #f5f7fa 0%, #e8ecf1 100%);
  min-height: 100vh;
}

.tabs {
  background: white;
  border-radius: 12px;
  padding: 20px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.08);
}

/* 我的班级 */
.my-classes-section,
.add-class-section,
.requests-section {
  padding: 20px 0;
}

.classes-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
  gap: 20px;
}

.class-card {
  background: linear-gradient(135deg, #f8f9fa 0%, #ffffff 100%);
  border: 1px solid #e8ecf1;
  border-radius: 10px;
  padding: 16px;
  transition: all 0.3s ease;
}

.class-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 8px 20px rgba(0, 0, 0, 0.12);
  border-color: #667eea;
}

.class-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 12px;
}

.class-header h3 {
  margin: 0;
  font-size: 16px;
  color: #2c3e50;
}

.class-info,
.result-info {
  margin: 12px 0;
  font-size: 14px;
  color: #666;
}

.label {
  font-weight: 600;
  color: #2c3e50;
  margin-right: 8px;
}

.class-actions,
.result-actions {
  display: flex;
  gap: 10px;
  margin-top: 12px;
}

/* 搜索框 */
.search-box {
  margin-bottom: 20px;
}

.search-input {
  max-width: 400px;
}

.search-results {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
  gap: 20px;
}

.result-card {
  background: linear-gradient(135deg, #f8f9fa 0%, #ffffff 100%);
  border: 1px solid #e8ecf1;
  border-radius: 10px;
  padding: 16px;
  transition: all 0.3s ease;
}

.result-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 8px 20px rgba(0, 0, 0, 0.12);
  border-color: #667eea;
}

.result-header h4 {
  margin: 0 0 12px 0;
  font-size: 16px;
  color: #2c3e50;
}

.warning {
  color: #f56c6c;
  display: flex;
  align-items: center;
  gap: 6px;
  margin-top: 8px;
}

/* 申请列表 */
.requests-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.request-item {
  background: white;
  border: 1px solid #e8ecf1;
  border-radius: 8px;
  padding: 16px;
  transition: all 0.3s ease;
}

.request-item:hover {
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.08);
}

.request-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 12px;
}

.request-header h4 {
  margin: 0;
  color: #2c3e50;
}

.request-info {
  font-size: 14px;
  color: #666;
}

.request-info p {
  margin: 6px 0;
}
</style>
