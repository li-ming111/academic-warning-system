<template>
  <div class="appeals-container">
    <el-row :gutter="20" style="margin-bottom: 20px;">
      <el-col :xs="24">
        <el-card>
          <template #header>
            <div class="card-header">
              <span>成绩申诉</span>
              <el-button type="primary" @click="openSubmitDialog">提交申诉</el-button>
            </div>
          </template>

          <el-tabs>
            <el-tab-pane label="全部申诉">
              <el-table :data="appeals" stripe max-height="600">
                <el-table-column prop="id" label="申诉ID" width="80"></el-table-column>
                <el-table-column prop="reason" label="申诉原因" width="150"></el-table-column>
                <el-table-column prop="description" label="详细描述" min-width="200"></el-table-column>
                <el-table-column label="状态" width="100">
                  <template #default="{ row }">
                    <el-tag :type="getStatusType(row.status)">{{ getStatusName(row.status) }}</el-tag>
                  </template>
                </el-table-column>
                <el-table-column label="申诉时间" width="180">
                  <template #default="{ row }">
                    {{ formatDate(row.appealTime) }}
                  </template>
                </el-table-column>
                <el-table-column label="操作" width="100">
                  <template #default="{ row }">
                    <el-button link type="primary" size="small" @click="viewDetail(row)">详情</el-button>
                  </template>
                </el-table-column>
              </el-table>
            </el-tab-pane>

            <el-tab-pane label="待处理">
              <el-table :data="pendingAppeals" stripe max-height="600">
                <el-table-column prop="reason" label="申诉原因" width="150"></el-table-column>
                <el-table-column prop="description" label="详细描述" min-width="200"></el-table-column>
                <el-table-column label="申诉时间" width="180">
                  <template #default="{ row }">
                    {{ formatDate(row.appealTime) }}
                  </template>
                </el-table-column>
                <el-table-column label="操作" width="100">
                  <template #default="{ row }">
                    <el-button link type="primary" size="small" @click="viewDetail(row)">详情</el-button>
                  </template>
                </el-table-column>
              </el-table>
            </el-tab-pane>

            <el-tab-pane label="已完成">
              <el-table :data="completedAppeals" stripe max-height="600">
                <el-table-column prop="reason" label="申诉原因" width="150"></el-table-column>
                <el-table-column label="状态" width="100">
                  <template #default="{ row }">
                    <el-tag :type="getStatusType(row.status)">{{ getStatusName(row.status) }}</el-tag>
                  </template>
                </el-table-column>
                <el-table-column label="回复内容" min-width="200">
                  <template #default="{ row }">
                    {{ row.replyMessage || '暂无回复' }}
                  </template>
                </el-table-column>
                <el-table-column label="回复时间" width="180">
                  <template #default="{ row }">
                    {{ formatDate(row.replyTime) }}
                  </template>
                </el-table-column>
              </el-table>
            </el-tab-pane>
          </el-tabs>
        </el-card>
      </el-col>
    </el-row>

    <!-- 提交申诉对话框 -->
    <el-dialog v-model="submitDialogVisible" title="提交成绩申诉" width="600px">
      <el-form :model="appealForm" label-width="100px">
        <el-form-item label="预警ID">
          <el-input-number v-model="appealForm.warningId" :min="1"></el-input-number>
        </el-form-item>
        <el-form-item label="申诉原因">
          <el-select v-model="appealForm.reason" placeholder="选择申诉原因">
            <el-option label="评分不公" value="unfair_grading"></el-option>
            <el-option label="阅卷错误" value="marking_error"></el-option>
            <el-option label="数据输入错误" value="data_error"></el-option>
            <el-option label="其他" value="other"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="附件">
          <el-input 
            v-model="appealForm.attachments" 
            type="textarea" 
            rows="4"
            placeholder="请详细说明申诉理由">
          </el-input>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="submitDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitAppeal">提交</el-button>
      </template>
    </el-dialog>

    <!-- 申诉详情对话框 -->
    <el-dialog v-model="detailDialogVisible" title="申诉详情" width="600px">
      <div v-if="selectedAppeal" class="appeal-detail">
        <div class="detail-item">
          <span class="label">申诉ID：</span>
          <span class="value">{{ selectedAppeal.id }}</span>
        </div>
        <div class="detail-item">
          <span class="label">申诉原因：</span>
          <span class="value">{{ selectedAppeal.reason }}</span>
        </div>
        <div class="detail-item">
          <span class="label">状态：</span>
          <el-tag :type="getStatusType(selectedAppeal.status)">{{ getStatusName(selectedAppeal.status) }}</el-tag>
        </div>
        <div class="detail-item">
          <span class="label">详细描述：</span>
          <div class="value-text">{{ selectedAppeal.description }}</div>
        </div>
        <div class="detail-item">
          <span class="label">申诉时间：</span>
          <span class="value">{{ formatDate(selectedAppeal.appealTime) }}</span>
        </div>
        <div v-if="selectedAppeal.replyMessage" class="detail-item">
          <span class="label">教师回复：</span>
          <div class="value-text">{{ selectedAppeal.replyMessage }}</div>
        </div>
        <div v-if="selectedAppeal.replyTime" class="detail-item">
          <span class="label">回复时间：</span>
          <span class="value">{{ formatDate(selectedAppeal.replyTime) }}</span>
        </div>
      </div>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { studentAPI } from '../api/index'
import { getUserId } from '@/utils/userUtils'

const appeals = ref([])
const submitDialogVisible = ref(false)
const detailDialogVisible = ref(false)
const selectedAppeal = ref(null)

const appealForm = ref({
  warningId: null,
  reason: '',
  attachments: ''
})

const pendingAppeals = computed(() => appeals.value.filter(a => a.status === 'pending'))
const completedAppeals = computed(() => appeals.value.filter(a => a.status !== 'pending'))

onMounted(async () => {
  await loadAppeals()
})

const getStatusType = (status) => {
  const map = {
    'pending': 'warning',
    'approved': 'success',
    'rejected': 'danger'
  }
  return map[status] || 'info'
}

const getStatusName = (status) => {
  const map = {
    'pending': '待处理',
    'approved': '已通过',
    'rejected': '已驳回'
  }
  return map[status] || status
}

const formatDate = (dateString) => {
  if (!dateString) return '-'
  const date = new Date(dateString)
  return date.toLocaleString('zh-CN')
}

const loadAppeals = async () => {
  try {
    const userId = getUserId()
    const studentId = localStorage.getItem('studentId')
    if (!userId || !studentId) {
      ElMessage.error('请先登录并完善学生信息')
      return
    }

    const data = await studentAPI.getAppeals(studentId)
    if (Array.isArray(data)) {
      appeals.value = data
    }
  } catch (error) {
    console.error('加载申诉列表失败:', error)
    ElMessage.error('加载申诉列表失败')
  }
}

const openSubmitDialog = () => {
  appealForm.value = {
    warningId: null,
    reason: '',
    attachments: ''
  }
  submitDialogVisible.value = true
}

const submitAppeal = async () => {
  if (!appealForm.value.warningId) {
    ElMessage.warning('请输入预警ID')
    return
  }
  if (!appealForm.value.reason) {
    ElMessage.warning('请选择申诉原因')
    return
  }
  if (!appealForm.value.attachments) {
    ElMessage.warning('请填写详细说明')
    return
  }

  try {
    const studentId = localStorage.getItem('studentId')
    if (!studentId) {
      ElMessage.error('请先完善学生信息')
      return
    }
    
    const result = await studentAPI.submitAppeal({
      ...appealForm.value,
      studentId: studentId
    })
    ElMessage.success('申诉已提交')
    submitDialogVisible.value = false
    await loadAppeals()
  } catch (error) {
    console.error('提交申诉失败:', error)
    ElMessage.error('提交申诉失败')
  }
}

const viewDetail = async (appeal) => {
  try {
    const result = await studentAPI.getAppealDetail(appeal.id)
    selectedAppeal.value = result || appeal
    detailDialogVisible.value = true
  } catch (error) {
    console.error('获取申诉详情失败:', error)
    ElMessage.error('获取申诉详情失败')
  }
}
</script>

<style scoped>
.appeals-container {
  padding: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.appeal-detail {
  padding: 20px 0;
}

.detail-item {
  display: flex;
  margin-bottom: 15px;
  align-items: flex-start;
}

.detail-item .label {
  font-weight: bold;
  width: 100px;
  flex-shrink: 0;
}

.detail-item .value {
  flex: 1;
  word-break: break-word;
}

.detail-item .value-text {
  flex: 1;
  line-height: 1.5;
  white-space: pre-wrap;
  word-break: break-word;
  background-color: #f5f5f5;
  padding: 10px;
  border-radius: 4px;
}
</style>
