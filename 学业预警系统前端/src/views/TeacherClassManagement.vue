<template>
  <div class="teacher-class-management">
    <div class="page-header">
      <h1>📚 班级管理</h1>
      <p>申请管理班级、查看班级学生、统计班级成绩、编辑班级信息</p>
    </div>

    <!-- Tab页签 -->
    <el-tabs v-model="activeTab" style="margin-bottom: 20px;">
      <el-tab-pane label="📋 班级申请管理" name="request">
        <!-- 申请表单 -->
        <el-card style="margin-bottom: 20px;">
          <template #header>
            <div class="card-header">✏️ 提交新申请</div>
          </template>

          <el-form :model="form" :rules="rules" ref="formRef" label-width="100px">
            <el-form-item label="选择班级" prop="classId">
              <el-select 
                v-model="form.classId" 
                placeholder="请选择班级"
                clearable
                @change="onClassSelected"
              >
                <el-option 
                  v-for="clazz in classList" 
                  :key="clazz.id" 
                  :label="`${clazz.name}`" 
                  :value="clazz.id"
                />
              </el-select>
            </el-form-item>

            <el-form-item label="申请理由" prop="reason">
              <el-input 
                v-model="form.reason" 
                type="textarea" 
                rows="4" 
                placeholder="请输入申请理由，说明为什么需要管理该班级"
                maxlength="500"
              ></el-input>
            </el-form-item>

            <el-form-item>
              <el-button type="primary" @click="submitRequest" :loading="submitting">
                提交申请
              </el-button>
              <el-button @click="resetForm">重置</el-button>
            </el-form-item>
          </el-form>
        </el-card>

        <!-- 申请历史 -->
        <el-card>
          <template #header>
            <div class="card-header">📋 我的申请记录</div>
          </template>

          <el-table :data="requests" stripe style="width: 100%">
            <el-table-column prop="id" label="申请ID" width="80"></el-table-column>
            <el-table-column label="班级" width="150">
              <template #default="{ row }">
                {{ getClassNameById(row.classId) }}
              </template>
            </el-table-column>
            <el-table-column prop="reason" label="申请理由" min-width="200"></el-table-column>
            <el-table-column prop="status" label="状态" width="100">
              <template #default="{ row }">
                <el-tag 
                  v-if="row.status === 'pending'" 
                  type="warning"
                >
                  待审批
                </el-tag>
                <el-tag 
                  v-else-if="row.status === 'approved'" 
                  type="success"
                >
                  已批准
                </el-tag>
                <el-tag 
                  v-else-if="row.status === 'rejected'" 
                  type="danger"
                >
                  已拒绝
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column label="拒绝理由" width="200">
              <template #default="{ row }">
                <span v-if="row.rejectReason" style="color: #f56c6c;">
                  {{ row.rejectReason }}
                </span>
                <span v-else style="color: #999;">-</span>
              </template>
            </el-table-column>
            <el-table-column label="申请时间" width="180">
              <template #default="{ row }">
                {{ formatDate(row.createdAt) }}
              </template>
            </el-table-column>
          </el-table>

          <div v-if="requests.length === 0" style="text-align: center; padding: 40px; color: #999;">
            暂无申请记录
          </div>
        </el-card>
      </el-tab-pane>

      <!-- 班级学生名单 -->
      <el-tab-pane label="👥 班级学生名单" name="students">
        <el-card>
          <template #header>
            <div class="card-header">👥 班级学生列表</div>
          </template>

          <div style="margin-bottom: 20px;">
            <el-select 
              v-model="selectedClassForStudents" 
              placeholder="请选择班级"
              clearable
              @change="loadClassStudents"
              style="width: 300px;"
            >
              <el-option 
                v-for="clazz in classList" 
                :key="clazz.id" 
                :label="`${clazz.name}`" 
                :value="clazz.id"
              />
            </el-select>
          </div>

          <el-table :data="classStudents" stripe style="width: 100%;">
            <el-table-column prop="studentId" label="学号" width="120"></el-table-column>
            <el-table-column prop="name" label="姓名" width="100"></el-table-column>
            <el-table-column prop="gender" label="性别" width="80">
              <template #default="{ row }">
                {{ row.gender === 'M' ? '男' : '女' }}
              </template>
            </el-table-column>
            <el-table-column prop="email" label="邮箱" width="200"></el-table-column>
            <el-table-column prop="status" label="状态" width="100">
              <template #default="{ row }">
                <el-tag :type="row.status === 'active' ? 'success' : 'info'">
                  {{ row.status === 'active' ? '在读' : '其他' }}
                </el-tag>
              </template>
            </el-table-column>
          </el-table>

          <div v-if="classStudents.length === 0" style="text-align: center; padding: 40px; color: #999;">
            请先选择班级查看学生名单
          </div>
        </el-card>
      </el-tab-pane>

      <!-- 班级成绩统计 -->
      <el-tab-pane label="📊 班级成绩统计" name="scores">
        <el-card>
          <template #header>
            <div class="card-header">📊 班级成绩统计</div>
          </template>

          <div style="margin-bottom: 20px;">
            <el-select 
              v-model="selectedClassForScores" 
              placeholder="请选择班级"
              clearable
              @change="loadClassScoresStatistics"
              style="width: 300px;"
            >
              <el-option 
                v-for="clazz in classList" 
                :key="clazz.id" 
                :label="`${clazz.name}`" 
                :value="clazz.id"
              />
            </el-select>
          </div>

          <div v-if="classScoresStats" class="stats-grid" style="margin-bottom: 20px;">
            <div class="stat-card">
              <div class="stat-label">平均分</div>
              <div class="stat-number">{{ classScoresStats.avgScore.toFixed(2) }}</div>
            </div>
            <div class="stat-card">
              <div class="stat-label">最高分</div>
              <div class="stat-number">{{ classScoresStats.maxScore }}</div>
            </div>
            <div class="stat-card">
              <div class="stat-label">最低分</div>
              <div class="stat-number">{{ classScoresStats.minScore }}</div>
            </div>
            <div class="stat-card">
              <div class="stat-label">及格率</div>
              <div class="stat-number">{{ classScoresStats.passRate }}%</div>
            </div>
          </div>

          <el-table :data="classScoresList" stripe style="width: 100%;">
            <el-table-column prop="studentName" label="学生名称" width="120"></el-table-column>
            <el-table-column prop="studentId" label="学号" width="120"></el-table-column>
            <el-table-column prop="courseName" label="课程" width="150"></el-table-column>
            <el-table-column prop="score" label="成绩" width="80"></el-table-column>
            <el-table-column prop="gradePoint" label="绩点" width="80"></el-table-column>
            <el-table-column prop="semester" label="学期" width="100"></el-table-column>
          </el-table>

          <div v-if="classScoresList.length === 0" style="text-align: center; padding: 40px; color: #999;">
            请先选择班级查看成绩统计
          </div>
        </el-card>
      </el-tab-pane>

      <!-- 班级信息编辑 -->
      <el-tab-pane label="⚙️ 班级信息编辑" name="info">
        <el-card>
          <template #header>
            <div class="card-header">⚙️ 编辑班级信息</div>
          </template>

          <div style="margin-bottom: 20px;">
            <el-select 
              v-model="selectedClassForEdit" 
              placeholder="请选择班级"
              clearable
              @change="loadClassInfoForEdit"
              style="width: 300px;"
            >
              <el-option 
                v-for="clazz in classList" 
                :key="clazz.id" 
                :label="`${clazz.name}`" 
                :value="clazz.id"
              />
            </el-select>
          </div>

          <el-form 
            v-if="editForm.id" 
            :model="editForm" 
            :rules="editRules" 
            ref="editFormRef" 
            label-width="120px"
          >
            <el-form-item label="班级名称" prop="name">
              <el-input v-model="editForm.name" placeholder="输入班级名称"></el-input>
            </el-form-item>

            <el-form-item label="班级代码" prop="code">
              <el-input v-model="editForm.code" placeholder="输入班级代码"></el-input>
            </el-form-item>

            <el-form-item label="班级简介" prop="description">
              <el-input 
                v-model="editForm.description" 
                type="textarea" 
                rows="4"
                placeholder="输入班级简介"
              ></el-input>
            </el-form-item>

            <el-form-item>
              <el-button type="primary" @click="saveClassInfo" :loading="savingClass">
                保存修改
              </el-button>
              <el-button @click="resetEditForm">重置</el-button>
            </el-form-item>
          </el-form>

          <div v-else style="text-align: center; padding: 40px; color: #999;">
            请先选择班级
          </div>
        </el-card>
      </el-tab-pane>
    </el-tabs>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { teacherAPI } from '@/api/index'
import { getUserId } from '@/utils/userUtils'

// Tab相关
const activeTab = ref('request')

// 班级申请相关
const formRef = ref()
const submitting = ref(false)

const form = ref({
  classId: '',
  reason: ''
})

const rules = {
  classId: [{ required: true, message: '请选择班级', trigger: 'change' }],
  reason: [{ required: true, message: '请输入申请理由', trigger: 'blur' }]
}

// 班级信息编辑相关
const editFormRef = ref()
const savingClass = ref(false)
const selectedClassForEdit = ref('')

const editForm = ref({
  id: '',
  name: '',
  code: '',
  description: ''
})

const editRules = {
  name: [{ required: true, message: '请输入班级名称', trigger: 'blur' }],
  code: [{ required: true, message: '请输入班级代码', trigger: 'blur' }]
}

// 班级列表
const classList = ref([])
const requests = ref([])

// 班级学生列表相关
const selectedClassForStudents = ref('')
const classStudents = ref([])

// 班级成绩统计相关
const selectedClassForScores = ref('')
const classScoresStats = ref(null)
const classScoresList = ref([])

onMounted(async () => {
  await loadClasses()
  await loadMyRequests()
})

// 加载班级列表
const loadClasses = async () => {
  try {
    classList.value = [
      { id: 1, name: '2023级计算机科学与技术班一' },
      { id: 2, name: '2023级计算机科学与技术班二' },
      { id: 3, name: '2023级软件工程班一' },
      { id: 4, name: '2024级计算机科学与技术班一' },
      { id: 5, name: '2024级软件工程班一' }
    ]
  } catch (error) {
    console.error('加载班级列表失败:', error)
  }
}

// 加载我的申请
const loadMyRequests = async () => {
  try {
    const userId = localStorage.getItem('userId') || getUserId()
    if (!userId) return
    
    const response = await teacherAPI.getMyClassManagementRequests(userId)
    if (response && Array.isArray(response)) {
      requests.value = response
    } else if (response && response.data && Array.isArray(response.data)) {
      requests.value = response.data
    }
  } catch (error) {
    console.error('加载申请记录失败:', error)
  }
}

// 加载班级学生
const loadClassStudents = async () => {
  try {
    if (!selectedClassForStudents.value) {
      classStudents.value = []
      return
    }
    
    // 模拟数据，实际应从API获取
    const mockStudents = {
      1: [
        { studentId: '2023010001', name: '张三', gender: 'M', email: 'zhangsan@example.com', status: 'active' },
        { studentId: '2023010002', name: '李四', gender: 'F', email: 'lisi@example.com', status: 'active' },
        { studentId: '2023010003', name: '王五', gender: 'M', email: 'wangwu@example.com', status: 'active' }
      ],
      2: [
        { studentId: '2023020001', name: '赵六', gender: 'F', email: 'zhaoliu@example.com', status: 'active' },
        { studentId: '2023020002', name: '孙七', gender: 'M', email: 'sunqi@example.com', status: 'active' }
      ],
      3: [
        { studentId: '2023030001', name: '周八', gender: 'M', email: 'zhouba@example.com', status: 'active' },
        { studentId: '2023030002', name: '吴九', gender: 'F', email: 'wujiu@example.com', status: 'active' }
      ]
    }
    
    classStudents.value = mockStudents[selectedClassForStudents.value] || []
  } catch (error) {
    console.error('加载班级学生失败:', error)
    ElMessage.error('加载班级学生失败')
  }
}

// 加载班级成绩统计
const loadClassScoresStatistics = async () => {
  try {
    if (!selectedClassForScores.value) {
      classScoresStats.value = null
      classScoresList.value = []
      return
    }
    
    // 模拟成绩数据
    const mockScores = {
      1: {
        stats: { avgScore: 78.5, maxScore: 95, minScore: 60, passRate: 92 },
        list: [
          { studentName: '张三', studentId: '2023010001', courseName: '高等数学', score: 85, gradePoint: 3.5, semester: '2023-2024-1' },
          { studentName: '张三', studentId: '2023010001', courseName: '程序设计基础', score: 90, gradePoint: 4.0, semester: '2023-2024-1' },
          { studentName: '李四', studentId: '2023010002', courseName: '高等数学', score: 78, gradePoint: 3.0, semester: '2023-2024-1' },
          { studentName: '李四', studentId: '2023010002', courseName: '程序设计基础', score: 85, gradePoint: 3.5, semester: '2023-2024-1' }
        ]
      },
      2: {
        stats: { avgScore: 75.2, maxScore: 92, minScore: 65, passRate: 88 },
        list: [
          { studentName: '赵六', studentId: '2023020001', courseName: '高等数学', score: 80, gradePoint: 3.2, semester: '2023-2024-1' },
          { studentName: '孙七', studentId: '2023020002', courseName: '程序设计基础', score: 88, gradePoint: 3.6, semester: '2023-2024-1' }
        ]
      }
    }
    
    const data = mockScores[selectedClassForScores.value]
    if (data) {
      classScoresStats.value = data.stats
      classScoresList.value = data.list
    }
  } catch (error) {
    console.error('加载班级成绩统计失败:', error)
    ElMessage.error('加载班级成绩统计失败')
  }
}

// 加载班级信息编辑
const loadClassInfoForEdit = async () => {
  try {
    if (!selectedClassForEdit.value) {
      editForm.value = { id: '', name: '', code: '', description: '' }
      return
    }
    
    const clazz = classList.value.find(c => c.id === selectedClassForEdit.value)
    if (clazz) {
      editForm.value = {
        id: clazz.id,
        name: clazz.name,
        code: `CLASS${clazz.id}`,
        description: '班级简介信息'
      }
    }
  } catch (error) {
    console.error('加载班级信息失败:', error)
    ElMessage.error('加载班级信息失败')
  }
}

// 获取班级名称
const getClassNameById = (classId) => {
  const clazz = classList.value.find(c => c.id === classId)
  return clazz ? clazz.name : `班级${classId}`
}

// 班级选择变化
const onClassSelected = () => {
  // 可以在这里添加额外逻辑
}

// 提交申请
const submitRequest = async () => {
  formRef.value.validate(async (valid) => {
    if (!valid) return

    submitting.value = true
    try {
      const userId = localStorage.getItem('userId') || getUserId()
      const data = {
        teacherId: userId,
        classId: form.value.classId,
        reason: form.value.reason
      }

      await teacherAPI.submitClassManagementRequest(data)
      ElMessage.success('申请已提交，请等待管理员审批')
      
      // 重置表单并重新加载申请列表
      resetForm()
      await loadMyRequests()
    } catch (error) {
      console.error('提交申请失败:', error)
      ElMessage.error(error.message || '提交申请失败')
    } finally {
      submitting.value = false
    }
  })
}

// 保存班级信息
const saveClassInfo = async () => {
  editFormRef.value.validate(async (valid) => {
    if (!valid) return

    savingClass.value = true
    try {
      // 实际应调用API保存
      ElMessage.success('班级信息已保存')
      resetEditForm()
    } catch (error) {
      console.error('保存班级信息失败:', error)
      ElMessage.error(error.message || '保存班级信息失败')
    } finally {
      savingClass.value = false
    }
  })
}

// 重置表单
const resetForm = () => {
  formRef.value.resetFields()
}

// 重置编辑表单
const resetEditForm = () => {
  editFormRef.value?.resetFields()
}

// 格式化日期
const formatDate = (date) => {
  if (!date) return '-'
  const d = new Date(date)
  return d.toLocaleString('zh-CN', { year: 'numeric', month: '2-digit', day: '2-digit', hour: '2-digit', minute: '2-digit' })
}
</script>

<style scoped>
.teacher-class-management {
  padding: 24px;
  background: #f5f7fa;
  min-height: 100vh;
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
}

.page-header p {
  margin: 0;
  font-size: 14px;
  opacity: 0.95;
}

.card-header {
  font-size: 18px;
  font-weight: bold;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.stats-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
  gap: 16px;
}

.stat-card {
  padding: 20px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border-radius: 8px;
  color: white;
  text-align: center;
}

.stat-label {
  font-size: 14px;
  opacity: 0.9;
  margin-bottom: 8px;
}

.stat-number {
  font-size: 28px;
  font-weight: bold;
}

:deep(.el-card) {
  border-radius: 12px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
  margin-bottom: 20px !important;
}

:deep(.el-table) {
  border-radius: 8px;
  overflow: hidden;
}

:deep(.el-button) {
  border-radius: 6px;
}

:deep(.el-tabs__nav-wrap) {
  background: white;
  border-radius: 12px;
  padding: 0 16px;
  margin-bottom: 20px;
}

:deep(.el-tabs__content) {
  padding: 0;
}
</style>
