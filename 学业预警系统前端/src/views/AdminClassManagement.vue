<template>
  <div class="admin-class-management">
    

    <!-- 待审批申请列表 -->
    <el-card>
      <template #header>
        <div class="card-header">
          ⏳ 待审批申请
          <el-tag style="margin-left: 10px;">{{ pendingCount }}</el-tag>
        </div>
      </template>

      <el-table :data="pendingRequests" stripe style="width: 100%">
        <el-table-column prop="id" label="申请ID" width="80"></el-table-column>
        <el-table-column prop="teacherName" label="申请教师" width="120"></el-table-column>
        <el-table-column prop="teacherUsername" label="工号" width="100"></el-table-column>
        <el-table-column prop="className" label="申请班级" width="150"></el-table-column>
        <el-table-column prop="reason" label="申请理由" min-width="200"></el-table-column>
        <el-table-column label="申请时间" width="180">
          <template #default="{ row }">
            {{ formatDate(row.createdAt) }}
          </template>
        </el-table-column>
        <el-table-column label="操作" width="180" fixed="right">
          <template #default="{ row }">
            <el-button 
              type="success" 
              size="small" 
              @click="approveRequest(row.id)"
              :loading="loadingIds.includes(row.id)"
            >
              批准
            </el-button>
            <el-button 
              type="danger" 
              size="small" 
              @click="showRejectDialog(row)"
            >
              拒绝
            </el-button>
          </template>
        </el-table-column>
      </el-table>

      <div v-if="pendingRequests.length === 0" style="text-align: center; padding: 40px; color: #999;">
        暂无待审批申请
      </div>
    </el-card>

    <!-- 历史申请 -->
    <el-card style="margin-top: 20px;">
      <template #header>
        <div class="card-header">所有申请记录</div>
      </template>

      <el-table :data="allRequests" stripe style="width: 100%">
        <el-table-column prop="id" label="申请ID" width="80"></el-table-column>
        <el-table-column prop="teacherName" label="申请教师" width="120"></el-table-column>
        <el-table-column prop="className" label="申请班级" width="150"></el-table-column>
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
        <el-table-column label="处理时间" width="180">
          <template #default="{ row }">
            {{ formatDate(row.updatedAt) }}
          </template>
        </el-table-column>
      </el-table>

      <div v-if="allRequests.length === 0" style="text-align: center; padding: 40px; color: #999;">
        暂无申请记录
      </div>
    </el-card>

    <!-- 拒绝理由对话框 -->
    <el-dialog v-model="rejectDialogVisible" title="拒绝申请" width="500px">
      <el-form :model="rejectForm" label-width="100px">
        <el-form-item label="教师">
          <el-input :value="rejectForm.teacherName" disabled></el-input>
        </el-form-item>
        <el-form-item label="班级">
          <el-input :value="rejectForm.className" disabled></el-input>
        </el-form-item>
        <el-form-item label="拒绝理由">
          <el-input 
            v-model="rejectForm.reason" 
            type="textarea" 
            rows="4"
            placeholder="请输入拒绝理由..."
            maxlength="500"
          ></el-input>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="rejectDialogVisible = false">取消</el-button>
        <el-button type="danger" @click="confirmReject" :loading="rejectLoading">
          确认拒绝
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { adminAPI } from '@/api/index'

const pendingRequests = ref([])
const allRequests = ref([])
const rejectDialogVisible = ref(false)
const loadingIds = ref([])
const rejectLoading = ref(false)

const rejectForm = ref({
  requestId: '',
  teacherName: '',
  className: '',
  reason: ''
})

const pendingCount = computed(() => pendingRequests.value.length)

onMounted(async () => {
  await loadRequests()
})

// 加载申请列表
const loadRequests = async () => {
  try {
    const response = await adminAPI.getPendingClassManagementRequests()
    console.log('后端返回的数据:', response)
    
    let requestsData = []
    if (response && Array.isArray(response)) {
      requestsData = response
    } else if (response && response.data && Array.isArray(response.data)) {
      requestsData = response.data
    }
    
    // 确保每个请求都有teacherName和teacherUsername字段
    const processedRequests = requestsData.map(req => {
      // 如果没有teacherName字段，尝试使用userName字段，如果都没有则使用默认值
      if (!req.teacherName) {
        if (req.userName) {
          req.teacherName = req.userName
        } else {
          req.teacherName = '未知教师'
        }
      }
      // 如果没有teacherUsername字段，尝试使用userUsername字段，如果都没有则使用默认值
      if (!req.teacherUsername) {
        if (req.userUsername) {
          req.teacherUsername = req.userUsername
        } else {
          req.teacherUsername = '未知工号'
        }
      }
      return req
    })
    
    allRequests.value = processedRequests
    pendingRequests.value = processedRequests.filter(req => req.status === 'pending')
  } catch (error) {
    console.error('加载申请列表失败:', error)
    ElMessage.error('加载申请列表失败')
  }
}

// 批准申请
const approveRequest = (requestId) => {
  ElMessageBox.confirm(
    '确定要批准这个申请吗？批准后该教师将获得管理指定班级的权限。',
    '确认批准',
    {
      confirmButtonText: '确认',
      cancelButtonText: '取消',
      type: 'success'
    }
  ).then(async () => {
    // 先刷新申请列表，确保获取最新的状态
    try {
      await loadRequests()
      
      // 检查申请是否仍然是待审批状态
      const request = pendingRequests.value.find(req => req.id === requestId)
      if (!request) {
        ElMessage.warning('申请不存在或状态已变更')
        return
      }
      
      loadingIds.value.push(requestId)
      adminAPI.approveClassManagementRequest(requestId)
        .then(() => {
          ElMessage.success('申请已批准')
          loadRequests()
        })
        .catch((error) => {
          console.error('批准申请失败:', error)
          ElMessage.error(error.message || '批准申请失败')
        })
        .finally(() => {
          loadingIds.value = loadingIds.value.filter(id => id !== requestId)
        })
    } catch (error) {
      console.error('刷新申请列表失败:', error)
      ElMessage.error('刷新申请列表失败')
    }
  }).catch(() => {
    // 用户取消
  })
}

// 显示拒绝对话框
const showRejectDialog = (request) => {
  rejectForm.value = {
    requestId: request.id,
    teacherName: request.teacherName,
    className: request.className,
    reason: ''
  }
  rejectDialogVisible.value = true
}

// 确认拒绝
const confirmReject = async () => {
  if (!rejectForm.value.reason.trim()) {
    ElMessage.error('请输入拒绝理由')
    return
  }

  rejectLoading.value = true
  try {
    await adminAPI.rejectClassManagementRequest(rejectForm.value.requestId, rejectForm.value.reason)
    ElMessage.success('申请已拒绝')
    rejectDialogVisible.value = false
    await loadRequests()
  } catch (error) {
    console.error('拒绝申请失败:', error)
    ElMessage.error(error.message || '拒绝申请失败')
  } finally {
    rejectLoading.value = false
  }
}

// 格式化日期
const formatDate = (date) => {
  if (!date) return '-'
  const d = new Date(date)
  return d.toLocaleString('zh-CN', { year: 'numeric', month: '2-digit', day: '2-digit', hour: '2-digit', minute: '2-digit' })
}
</script>

<style scoped>
.admin-class-management {
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
  display: flex;
  align-items: center;
}

:deep(.el-card) {
  border-radius: 12px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
}

:deep(.el-table) {
  border-radius: 8px;
  overflow: hidden;
}

:deep(.el-button) {
  border-radius: 6px;
}

:deep(.el-tag) {
  border-radius: 12px;
}
</style>
