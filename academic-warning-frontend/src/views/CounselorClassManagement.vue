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
                <el-button type="primary" size="small" @click="viewStudents(clazz)">
                  查看学生
                </el-button>
                <el-button size="small" @click="importStudents(clazz)">
                  导入学生
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

    <!-- 导入学生对话框 -->
    <el-dialog v-model="importDialogVisible" :title="`导入学生 - ${currentClass?.name}`" width="600px">
      <el-form :model="importForm">
        <el-form-item label="导入方式">
          <el-radio-group v-model="importForm.method">
            <el-radio label="manual">手动输入</el-radio>
            <el-radio label="excel">Excel导入</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item v-if="importForm.method === 'manual'" label="学生信息">
          <el-button type="primary" @click="addStudentRow">
            添加学生
          </el-button>
          <el-table :data="importForm.students" style="width: 100%; margin-top: 10px">
            <el-table-column prop="studentId" label="学号">
              <template #default="{ row, $index }">
                <el-input v-model="row.studentId" placeholder="请输入学号"></el-input>
              </template>
            </el-table-column>
            <el-table-column prop="name" label="姓名">
              <template #default="{ row, $index }">
                <el-input v-model="row.name" placeholder="请输入姓名"></el-input>
              </template>
            </el-table-column>
            <el-table-column label="操作" width="80">
              <template #default="{ $index }">
                <el-button type="danger" size="small" @click="removeStudentRow($index)">
                  删除
                </el-button>
              </template>
            </el-table-column>
          </el-table>
        </el-form-item>
        <el-form-item v-else label="Excel文件">
          <el-upload
            class="upload-demo"
            action="#"
            :auto-upload="false"
            :on-change="handleFileChange"
            :show-file-list="true"
          >
            <el-button type="primary">
              <el-icon><Upload /></el-icon>
              选择文件
            </el-button>
            <template #tip>
              <div class="el-upload__tip">
                请上传Excel文件，包含学号和姓名列
              </div>
            </template>
          </el-upload>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="importDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitImport" :loading="importLoading">
          导入
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { Search, Warning, Upload } from '@element-plus/icons-vue'
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

// 学生相关状态
const studentsDialogVisible = ref(false)
const importDialogVisible = ref(false)
const currentClass = ref(null)
const classStudents = ref([])
const loadingStudents = ref(false)
const studentSearch = ref('')
const importForm = ref({
  method: 'manual',
  students: []
})
const importLoading = ref(false)

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

// 查看学生列表
const viewStudents = async (clazz) => {
  currentClass.value = clazz
  loadingStudents.value = true
  try {
    const response = await studentAPI.request(`/counselors/classes/${clazz.id}/students`, 'GET')
    if (response && response.data) {
      classStudents.value = response.data
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

// 导入学生
const importStudents = (clazz) => {
  currentClass.value = clazz
  importForm.value = {
    method: 'manual',
    students: []
  }
  importDialogVisible.value = true
}

// 添加学生行
const addStudentRow = () => {
  importForm.value.students.push({ studentId: '', name: '' })
}

// 删除学生行
const removeStudentRow = (index) => {
  importForm.value.students.splice(index, 1)
}

// 处理文件上传
const handleFileChange = (file) => {
  console.log('文件上传:', file)
  // 这里可以处理Excel文件上传和解析
  ElMessage.info('Excel导入功能开发中...')
}

// 提交导入
const submitImport = async () => {
  if (importForm.value.method === 'manual' && importForm.value.students.length === 0) {
    ElMessage.warning('请添加学生信息')
    return
  }

  importLoading.value = true
  try {
    const response = await studentAPI.request(`/teachers/students/import`, 'POST', {
      classId: currentClass.value.id,
      students: importForm.value.students
    })
    
    if (response) {
      ElMessage.success('学生导入成功')
      importDialogVisible.value = false
      loadMyClasses()
    }
  } catch (error) {
    console.error('导入学生失败:', error)
    ElMessage.error('导入学生失败')
  } finally {
    importLoading.value = false
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

/* 导入学生相关样式 */
.upload-demo {
  margin-top: 10px;
}

.el-upload__tip {
  margin-top: 8px;
  font-size: 12px;
  color: #909399;
}
</style>
