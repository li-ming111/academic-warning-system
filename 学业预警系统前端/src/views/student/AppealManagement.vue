<template>
  <div class="appeal-container">
    <!-- 统计卡片 -->
    <el-row :gutter="20" class="stats-row">
      <el-col :xs="24" :sm="12" :md="6">
        <el-card class="stat-card" shadow="hover">
          <div class="stat-number">{{ statistics.total || 0 }}</div>
          <div class="stat-label">累计申诉</div>
        </el-card>
      </el-col>
      <el-col :xs="24" :sm="12" :md="6">
        <el-card class="stat-card" shadow="hover">
          <div class="stat-number">{{ statistics.approved || 0 }}</div>
          <div class="stat-label">申诉通过</div>
        </el-card>
      </el-col>
      <el-col :xs="24" :sm="12" :md="6">
        <el-card class="stat-card" shadow="hover">
          <div class="stat-number">{{ statistics.pending || 0 }}</div>
          <div class="stat-label">待处理</div>
        </el-card>
      </el-col>
      <el-col :xs="24" :sm="12" :md="6">
        <el-card class="stat-card" shadow="hover">
          <div class="stat-number">{{ successRate.toFixed(1) }}%</div>
          <div class="stat-label">成功率</div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 申诉提交表单和列表 -->
    <el-row :gutter="20" class="content-row">
      <!-- 左侧：申诉原因分类统计 -->
      <el-col :xs="24" :md="6">
        <el-card class="reason-card" shadow="hover">
          <template #header>
            <div class="card-header">
              <span class="title">申诉原因分布</span>
            </div>
          </template>
          <div v-if="reasonStats && Object.keys(reasonStats).length > 0" class="reason-list">
            <div v-for="(count, reason) in reasonStats" :key="reason" class="reason-item">
              <span class="reason-name">{{ reason }}</span>
              <el-tag>{{ count }}</el-tag>
            </div>
          </div>
          <div v-else class="no-data">暂无数据</div>
        </el-card>
      </el-col>

      <!-- 右侧：申诉列表 -->
      <el-col :xs="24" :md="18">
        <!-- 工具栏 -->
        <el-card class="toolbar-card" shadow="hover" style="margin-bottom: 20px">
          <el-row :gutter="15">
            <el-col :xs="24" :sm="12" :md="8">
              <el-select v-model="filterStatus" @change="loadAppeals" placeholder="筛选状态" style="width: 100%">
                <el-option label="全部" value=""></el-option>
                <el-option label="待处理" value="pending"></el-option>
                <el-option label="已通过" value="approved"></el-option>
                <el-option label="已驳回" value="rejected"></el-option>
              </el-select>
            </el-col>
            <el-col :xs="24" :sm="12" :md="8">
              <el-button type="primary" @click="showSubmitDialog = true" style="width: 100%">发起申诉</el-button>
            </el-col>
          </el-row>
        </el-card>

        <!-- 申诉列表表格 -->
        <el-card class="table-card" shadow="hover">
          <el-table :data="appeals" stripe style="width: 100%">
            <el-table-column prop="id" label="ID" width="60"></el-table-column>
            <el-table-column prop="reason" label="申诉原因" min-width="200">
              <template #default="{ row }">
                <span class="reason-text">{{ row.reason }}</span>
              </template>
            </el-table-column>
            <el-table-column prop="status" label="状态" width="100">
              <template #default="{ row }">
                <el-tag :type="getStatusType(row.status)">{{ getStatusLabel(row.status) }}</el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="createdAt" label="提交时间" width="150">
              <template #default="{ row }">
                {{ formatTime(row.createdAt) }}
              </template>
            </el-table-column>
            <el-table-column label="操作" width="120" fixed="right">
              <template #default="{ row }">
                <el-button link type="primary" size="small" @click="handleDetail(row.id)">查看</el-button>
                <el-button v-if="row.status === 'pending'" link type="danger" size="small" @click="handleWithdraw(row.id)">撤销</el-button>
              </template>
            </el-table-column>
          </el-table>
          <!-- 分页 -->
          <div class="pagination">
            <el-pagination
              v-model:current-page="currentPage"
              v-model:page-size="pageSize"
              :page-sizes="[5, 10, 15, 20]"
              :total="total"
              layout="total, sizes, prev, pager, next, jumper"
              @change="loadAppeals"
            />
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 申诉提交对话框 -->
    <el-dialog v-model="showSubmitDialog" title="发起申诉" width="600px" @close="resetSubmitForm">
      <el-form :model="submitForm" label-width="80px">
        <el-form-item label="关联预警">
          <el-input v-model="submitForm.warningId" type="number" placeholder="请输入预警ID"></el-input>
        </el-form-item>
        <el-form-item label="申诉原因">
          <el-input v-model="submitForm.reason" type="textarea" rows="4" placeholder="请详细描述申诉原因"></el-input>
        </el-form-item>
        <el-form-item label="附件">
          <el-input v-model="submitForm.attachments" placeholder="可选：输入附件URL或相关链接"></el-input>
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="showSubmitDialog = false">取消</el-button>
          <el-button type="primary" @click="handleSubmit">提交申诉</el-button>
        </span>
      </template>
    </el-dialog>

    <!-- 申诉详情对话框 -->
    <el-dialog v-model="showDetailDialog" title="申诉详情" width="600px" @close="currentDetail = null">
      <div v-if="currentDetail" class="detail-content">
        <div class="detail-item">
          <span class="detail-label">申诉ID：</span>
          <span>{{ currentDetail.id }}</span>
        </div>
        <div class="detail-item">
          <span class="detail-label">状态：</span>
          <el-tag :type="getStatusType(currentDetail.status)">{{ getStatusLabel(currentDetail.status) }}</el-tag>
        </div>
        <div class="detail-item">
          <span class="detail-label">申诉原因：</span>
          <p>{{ currentDetail.reason }}</p>
        </div>
        <div class="detail-item">
          <span class="detail-label">提交时间：</span>
          <span>{{ formatTime(currentDetail.createdAt) }}</span>
        </div>
        <div v-if="currentDetail.attachments" class="detail-item">
          <span class="detail-label">附件：</span>
          <el-link type="primary" href="currentDetail.attachments" target="_blank">{{ currentDetail.attachments }}</el-link>
        </div>
      </div>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { studentAPI } from '@/api'
import { ElMessage, ElMessageBox } from 'element-plus'

const userId = ref(null)
const studentId = ref(null)

const statistics = ref({
  total: 0,
  pending: 0,
  approved: 0,
  rejected: 0,
  withdrawn: 0
})
const successRate = ref(0)
const reasonStats = ref({})

const appeals = ref([])
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)
const filterStatus = ref('')

const showSubmitDialog = ref(false)
const showDetailDialog = ref(false)
const currentDetail = ref(null)

const submitForm = reactive({
  warningId: null,
  reason: '',
  attachments: ''
})

onMounted(async () => {
  const userInfo = JSON.parse(localStorage.getItem('user') || '{}')
  userId.value = userInfo.userId || userInfo.id
  studentId.value = userInfo.studentId

  await loadStatistics()
  await loadAppeals()
  await loadReasonStats()
})

async function loadStatistics() {
  try {
    const response = await studentAPI.getAppealStatistics(studentId.value)
    if (response.data.success) {
      statistics.value = response.data.data || {}
    }
    
    const successResp = await studentAPI.getAppealSuccessRate(studentId.value)
    if (successResp.data.success) {
      successRate.value = successResp.data.data.successRate || 0
    }
  } catch (error) {
    ElMessage.error('加载统计数据失败')
    console.error(error)
  }
}

async function loadAppeals() {
  try {
    let response
    if (filterStatus.value) {
      response = await studentAPI.getAppealsByStatus(studentId.value, filterStatus.value)
      appeals.value = response.data.data || []
    } else {
      response = await studentAPI.getAppealHistory(studentId.value, currentPage.value, pageSize.value)
      if (response.data.success) {
        const data = response.data.data || {}
        appeals.value = data.data || []
        total.value = data.total || 0
      }
    }
  } catch (error) {
    ElMessage.error('加载申诉列表失败')
    console.error(error)
  }
}

async function loadReasonStats() {
  try {
    const response = await studentAPI.getAppealReasonStatistics(studentId.value)
    if (response.data.success) {
      reasonStats.value = response.data.data || {}
    }
  } catch (error) {
    console.error('加载原因统计失败', error)
  }
}

async function handleSubmit() {
  if (!submitForm.reason) {
    ElMessage.warning('请输入申诉原因')
    return
  }

  try {
    const appeal = {
      studentId: studentId.value,
      warningId: submitForm.warningId,
      reason: submitForm.reason,
      attachments: submitForm.attachments
    }
    
    const response = await studentAPI.submitAppeal(appeal)
    if (response.data.success) {
      ElMessage.success('申诉已提交')
      showSubmitDialog.value = false
      resetSubmitForm()
      await loadAppeals()
      await loadStatistics()
    }
  } catch (error) {
    ElMessage.error('提交申诉失败')
    console.error(error)
  }
}

async function handleDetail(appealId) {
  try {
    const response = await studentAPI.getAppealDetail(appealId)
    if (response.data.success) {
      currentDetail.value = response.data.data
      showDetailDialog.value = true
    }
  } catch (error) {
    ElMessage.error('加载详情失败')
    console.error(error)
  }
}

async function handleWithdraw(appealId) {
  ElMessageBox.confirm('确定要撤销该申诉吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      const response = await studentAPI.withdrawAppeal(appealId)
      if (response.data.success) {
        ElMessage.success('申诉已撤销')
        await loadAppeals()
        await loadStatistics()
      }
    } catch (error) {
      ElMessage.error('撤销申诉失败')
      console.error(error)
    }
  }).catch(() => {})
}

function resetSubmitForm() {
  submitForm.warningId = null
  submitForm.reason = ''
  submitForm.attachments = ''
}

function getStatusLabel(status) {
  const labels = {
    pending: '待处理',
    approved: '已通过',
    rejected: '已驳回',
    withdrawn: '已撤销'
  }
  return labels[status] || status
}

function getStatusType(status) {
  const types = {
    pending: 'warning',
    approved: 'success',
    rejected: 'danger',
    withdrawn: 'info'
  }
  return types[status] || 'info'
}

function formatTime(time) {
  if (!time) return '--'
  return new Date(time).toLocaleDateString('zh-CN', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit',
    hour: '2-digit',
    minute: '2-digit'
  })
}
</script>

<style scoped lang="scss">
.appeal-container {
  padding: 20px;
  background: #f5f7fa;

  .stats-row {
    margin-bottom: 20px;

    .stat-card {
      text-align: center;
      padding: 20px;

      .stat-number {
        font-size: 32px;
        font-weight: bold;
        color: #409eff;
        margin-bottom: 8px;
      }

      .stat-label {
        font-size: 14px;
        color: #606266;
      }
    }
  }

  .content-row {
    .reason-card {
      .reason-list {
        .reason-item {
          display: flex;
          justify-content: space-between;
          align-items: center;
          padding: 10px 0;
          border-bottom: 1px solid #ebeef5;

          &:last-child {
            border-bottom: none;
          }

          .reason-name {
            color: #606266;
            font-size: 14px;
          }
        }
      }
    }

    .toolbar-card {
      margin-bottom: 20px;
    }

    .table-card {
      .reason-text {
        color: #606266;
        word-break: break-word;
      }

      .pagination {
        text-align: right;
        margin-top: 20px;
      }
    }
  }

  .detail-content {
    .detail-item {
      margin-bottom: 20px;
      padding-bottom: 15px;
      border-bottom: 1px solid #ebeef5;

      &:last-child {
        border-bottom: none;
      }

      .detail-label {
        font-weight: 600;
        color: #303133;
        display: inline-block;
        min-width: 80px;
      }

      p {
        margin: 10px 0 0 80px;
        color: #606266;
        line-height: 1.5;
      }
    }
  }

  .no-data {
    text-align: center;
    padding: 40px 20px;
    color: #909399;
  }

  .dialog-footer {
    display: flex;
    justify-content: flex-end;
    gap: 10px;
  }
}
</style>
