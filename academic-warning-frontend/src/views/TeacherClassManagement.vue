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
                <el-button type="primary" size="small" @click="goToScoreImport(clazz.id)">
                  导入成绩
                </el-button>
                <el-button type="success" size="small" @click="openStudentImportDialog(clazz)">
                  导入学生
                </el-button>
                <el-button type="info" size="small" @click="viewStudents(clazz)">
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
            <el-button type="primary" @click="loadClasses(false)" style="margin-left: 10px" :loading="loading">
              查看无教师班级
            </el-button>
            <el-button type="primary" @click="loadClasses(true)" style="margin-left: 10px" :loading="loading">
              查看所有班级
            </el-button>
          </div>

          <el-empty v-if="!searchPerformed" description="点击按钮查看班级"></el-empty>
          <el-empty v-else-if="searchResults.length === 0" description="未找到匹配的班级"></el-empty>

          <div v-else class="search-results">
            <div v-for="clazz in searchResults" :key="clazz.id" class="result-card">
              <div class="result-header">
                <h4>{{ clazz.name }}</h4>
              </div>
              <div class="result-info">
                <p><span class="label">学生数：</span> {{ clazz.studentCount }}</p>
                <p><span class="label">专业：</span> {{ clazz.majorId || '未指定' }}</p>
                <p v-if="clazz.hasTeacher" class="warning">
                  <el-icon><Warning /></el-icon>
                  已有教师管理
                </p>
              </div>
              <div class="result-actions">
                <el-button
                  type="primary"
                  @click="openApplyDialog(clazz)"
                >
                  申请管理
                </el-button>
                <el-button v-if="clazz.hasTeacher" type="info" size="small" @click="viewClassDetails(clazz)" style="margin-left: 8px">
                  查看详情
                </el-button>
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

    <!-- 导入学生对话框 -->
    <el-dialog v-model="studentImportDialogVisible" title="导入学生信息" width="600px">
      <div class="student-import-content">
        <el-form ref="importFormRef" :model="importForm" label-width="100px">
          <el-form-item label="班级名称">
            <el-input v-model="importForm.className" disabled></el-input>
          </el-form-item>
          <el-form-item label="上传Excel">
            <el-upload
              ref="uploadStudentRef"
              :auto-upload="false"
              :on-change="handleStudentFileSelect"
              accept=".xlsx,.xls"
              class="upload-demo"
            >
              <el-button type="primary">选择文件</el-button>
              <template #tip>
                <div class="el-upload__tip">
                  请上传包含学生信息的Excel文件，格式要求：学号、姓名、性别、手机号、邮箱
                </div>
              </template>
            </el-upload>
          </el-form-item>
        </el-form>
      </div>
      <template #footer>
        <el-button @click="studentImportDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="importStudents" :loading="importLoading">
          开始导入
        </el-button>
      </template>
    </el-dialog>

    <!-- 学生列表对话框 -->
    <el-dialog v-model="studentsDialogVisible" :title="`${currentClass?.name} - 学生列表`" width="800px">
      <div v-if="loadingStudents" class="loading-container">
        <el-skeleton :rows="10" animated />
      </div>
      <div v-else>
        <el-input
          v-model="studentSearch"
          placeholder="搜索学生姓名或学号"
          clearable
          class="mb-4"
        >
          <template #prefix>
            <el-icon><Search /></el-icon>
          </template>
        </el-input>
        <el-table :data="filteredStudents" style="width: 100%">
          <el-table-column prop="studentId" label="学号" width="180"></el-table-column>
          <el-table-column prop="studentName" label="姓名" width="120"></el-table-column>
          <el-table-column label="操作" width="120">
            <template #default="{ row }">
              <el-button size="small" @click="viewStudentDetail(row)">
                详情
              </el-button>
            </template>
          </el-table-column>
        </el-table>
        <div v-if="classStudents.length === 0" class="empty-students">
          <el-empty description="该班级暂无学生"></el-empty>
        </div>
      </div>
      <template #footer>
        <el-button @click="studentsDialogVisible = false">关闭</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { Search, Warning } from '@element-plus/icons-vue'
import { teacherAPI } from '@/api/index'

const activeTab = ref('my-classes')
const myClasses = ref([])
const searchKeyword = ref('')
const searchResults = ref([])
const searchPerformed = ref(false)
const myRequests = ref([])
const applyDialogVisible = ref(false)
const applyLoading = ref(false)
const loading = ref(false)
const applyForm = ref({
  classId: null,
  className: '',
  reason: ''
})

// 学生导入相关
const studentImportDialogVisible = ref(false)
const importLoading = ref(false)
const importForm = ref({
  classId: null,
  className: '',
  file: null
})
const uploadStudentRef = ref(null)

// 学生列表相关
const studentsDialogVisible = ref(false)
const currentClass = ref(null)
const classStudents = ref([])
const loadingStudents = ref(false)
const studentSearch = ref('')

const teacherId = ref(null)

// 获取教师ID（从localStorage或store）
const getTeacherId = () => {
  const userId = localStorage.getItem('userId')
  return userId ? parseInt(userId) : null
}

// 加载我的班级
const loadMyClasses = async () => {
  try {
    const userId = getTeacherId()
    if (!userId) {
      ElMessage.error('用户ID不存在')
      return
    }
    
    const response = await teacherAPI.getMyClasses(userId)
    
    if (response) {
      myClasses.value = response
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
    const response = await teacherAPI.searchClasses(searchKeyword.value)
    
    if (response) {
      searchResults.value = response
      searchPerformed.value = true
    }
  } catch (error) {
    console.error('搜索班级失败:', error)
    ElMessage.error('搜索班级失败')
  }
}

// 加载班级
const loadClasses = async (showAll = false) => {
  loading.value = true
  try {
    // 传递空字符串获取所有班级
    const response = await teacherAPI.searchClasses('')
    
    if (response) {
      if (showAll) {
        // 显示所有班级
        searchResults.value = response
      } else {
        // 只显示没有教师的班级
        searchResults.value = response.filter(clazz => !clazz.hasTeacher)
      }
      searchPerformed.value = true
    }
  } catch (error) {
    console.error('加载班级失败:', error)
    ElMessage.error('加载班级失败')
  } finally {
    loading.value = false
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
    const userId = getTeacherId()
    const response = await teacherAPI.submitClassManagementRequest({
      teacherId: userId,
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
    const userId = getTeacherId()
    const response = await teacherAPI.getMyClassManagementRequests(userId)
    
    if (response) {
      myRequests.value = response
    }
  } catch (error) {
    console.error('加载申请失败:', error)
  }
}

// 跳转到成绩导入页面
const goToScoreImport = (classId) => {
  // 跳转到成绩管理页面，使用已实现的成绩导入功能
  window.location.href = '/teacher/scores'
}

// 打开学生导入对话框
const openStudentImportDialog = (clazz) => {
  importForm.value = {
    classId: clazz.id,
    className: clazz.name,
    file: null
  }
  studentImportDialogVisible.value = true
}

// 查看班级详情
const viewClassDetails = (clazz) => {
  ElMessage.info(`班级详情：${clazz.name}，学生数：${clazz.studentCount}`)
  console.log('班级详情:', clazz)
}

// 查看学生列表
const viewStudents = async (clazz) => {
  currentClass.value = clazz
  loadingStudents.value = true
  try {
    const response = await teacherAPI.getClassStudents(clazz.id)
    if (response) {
      classStudents.value = response
    } else {
      classStudents.value = []
    }
  } catch (error) {
    console.error('加载学生列表失败:', error)
    ElMessage.error('加载学生列表失败')
    classStudents.value = []
  } finally {
    loadingStudents.value = false
    studentsDialogVisible.value = true
  }
}

// 查看学生详情
const viewStudentDetail = (student) => {
  ElMessage.info(`查看学生 ${student.studentName} 的详情`)
  // 这里可以跳转到学生详情页面或显示详情对话框
}

// 筛选学生
const filteredStudents = computed(() => {
  if (!studentSearch.value) {
    return classStudents.value
  }
  const query = studentSearch.value.toLowerCase()
  return classStudents.value.filter(student => 
    student.studentName.toLowerCase().includes(query) || 
    student.studentId.includes(query)
  )
})

// 处理学生文件选择
const handleStudentFileSelect = (uploadFile) => {
  importForm.value.file = uploadFile.raw
}

// 导入学生
const importStudents = async () => {
  if (!importForm.value.file) {
    ElMessage.error('请选择Excel文件')
    return
  }

  importLoading.value = true
  try {
    const formData = new FormData()
    formData.append('file', importForm.value.file)
    formData.append('classId', importForm.value.classId)
    
    const response = await teacherAPI.importStudents(formData)
    
    if (response) {
      ElMessage.success('学生导入成功')
      studentImportDialogVisible.value = false
      loadMyClasses()
      uploadStudentRef.value.clearFiles()
    }
  } catch (error) {
    console.error('导入学生失败:', error)
    ElMessage.error(error.message || '导入学生失败')
  } finally {
    importLoading.value = false
  }
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
  teacherId.value = getTeacherId()
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
  flex-wrap: wrap;
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

/* 学生列表相关样式 */
.loading-container {
  padding: 20px 0;
}

.mb-4 {
  margin-bottom: 16px;
}

.empty-students {
  padding: 40px 0;
  text-align: center;
}
</style>
