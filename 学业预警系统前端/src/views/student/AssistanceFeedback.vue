<template>
  <div class="feedback-container">
    <!-- 评价统计 -->
    <el-row :gutter="20" class="stats-row">
      <el-col :xs="24" :sm="12" :md="6">
        <el-card class="stat-card" shadow="hover">
          <div class="stat-number">{{ statistics.total || 0 }}</div>
          <div class="stat-label">已评价计划</div>
        </el-card>
      </el-col>
      <el-col :xs="24" :sm="12" :md="6">
        <el-card class="stat-card" shadow="hover">
          <div class="stat-number">{{ (statistics.avgRating || 0).toFixed(1) }}</div>
          <div class="stat-label">平均评分</div>
        </el-card>
      </el-col>
      <el-col :xs="24" :sm="12" :md="6">
        <el-card class="stat-card" shadow="hover">
          <div class="stat-number">{{ (statistics.avgEffectiveness || 0).toFixed(1) }}</div>
          <div class="stat-label">有效性评分</div>
        </el-card>
      </el-col>
      <el-col :xs="24" :sm="12" :md="6">
        <el-card class="stat-card" shadow="hover">
          <div class="stat-number">{{ activePlans.length }}</div>
          <div class="stat-label">待评价计划</div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 评分分布 + 评价列表 -->
    <el-row :gutter="20" class="content-row">
      <!-- 左侧：评分分布 -->
      <el-col :xs="24" :md="6">
        <el-card class="distribution-card" shadow="hover">
          <template #header>
            <div class="card-header">
              <span class="title">评分分布</span>
            </div>
          </template>
          <div v-if="statistics.ratingDistribution" class="distribution-list">
            <div v-for="(count, rating) in statistics.ratingDistribution" :key="rating" class="distribution-item">
              <span class="rating-star">
                <el-rate :model-value="parseInt(rating)" disabled size="small" />
              </span>
              <el-progress :percentage="getRatingPercentage(count)" :color="getProgressColor(rating)" />
              <span class="count">{{ count }}次</span>
            </div>
          </div>
          <div v-else class="no-data">暂无数据</div>
        </el-card>
      </el-col>

      <!-- 右侧：评价列表 -->
      <el-col :xs="24" :md="18">
        <!-- 工具栏 -->
        <el-card class="toolbar-card" shadow="hover" style="margin-bottom: 20px">
          <el-row :gutter="15">
            <el-col :xs="24" :sm="12" :md="8">
              <el-button type="primary" @click="showEvaluationDialog = true" style="width: 100%">
                评价帮扶计划
              </el-button>
            </el-col>
            <el-col :xs="24" :sm="12" :md="8">
              <el-select v-model="filterStatus" @change="loadEvaluations" placeholder="筛选状态" style="width: 100%">
                <el-option label="全部" value=""></el-option>
                <el-option label="待评价" value="pending"></el-option>
                <el-option label="已评价" value="evaluated"></el-option>
              </el-select>
            </el-col>
          </el-row>
        </el-card>

        <!-- 评价列表表格 -->
        <el-card class="table-card" shadow="hover">
          <template #header v-if="evaluations.length > 0">
            <div class="card-header">
              <span class="title">我的评价</span>
            </div>
          </template>
          <el-table v-if="evaluations.length > 0" :data="evaluations" stripe style="width: 100%">
            <el-table-column prop="id" label="ID" width="60"></el-table-column>
            <el-table-column prop="planId" label="计划ID" width="80"></el-table-column>
            <el-table-column label="评分" width="80">
              <template #default="{ row }">
                <el-rate v-model="row.rating" disabled size="small" />
              </template>
            </el-table-column>
            <el-table-column label="有效性" width="80">
              <template #default="{ row }">
                <el-tag :type="getEffectivenessType(row.effectiveness)">
                  {{ row.effectiveness || 0 }} / 5
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="feedback" label="反馈" min-width="200">
              <template #default="{ row }">
                <span class="feedback-text">{{ row.feedback }}</span>
              </template>
            </el-table-column>
            <el-table-column prop="createdAt" label="评价时间" width="150">
              <template #default="{ row }">
                {{ formatTime(row.createdAt) }}
              </template>
            </el-table-column>
            <el-table-column label="操作" width="100" fixed="right">
              <template #default="{ row }">
                <el-button link type="primary" size="small" @click="handleEdit(row)">编辑</el-button>
              </template>
            </el-table-column>
          </el-table>
          <div v-else class="no-data-placeholder">
            <el-empty description="暂无评价" />
          </div>
        </el-card>

        <!-- 待评价计划列表 -->
        <el-card class="pending-card" shadow="hover" style="margin-top: 20px" v-if="activePlans.length > 0">
          <template #header>
            <div class="card-header">
              <span class="title">待评价计划</span>
            </div>
          </template>
          <el-table :data="activePlans" stripe style="width: 100%">
            <el-table-column prop="id" label="计划ID" width="80"></el-table-column>
            <el-table-column prop="title" label="计划名称" min-width="150"></el-table-column>
            <el-table-column prop="status" label="状态" width="100">
              <template #default="{ row }">
                <el-tag :type="getStatusType(row.status)">{{ getStatusLabel(row.status) }}</el-tag>
              </template>
            </el-table-column>
            <el-table-column label="进度" width="150">
              <template #default="{ row }">
                <el-progress :percentage="row.progressPercentage || 0" />
              </template>
            </el-table-column>
            <el-table-column label="操作" width="120" fixed="right">
              <template #default="{ row }">
                <el-button link type="primary" size="small" @click="handleEvaluate(row)">
                  评价
                </el-button>
              </template>
            </el-table-column>
          </el-table>
        </el-card>
      </el-col>
    </el-row>

    <!-- 评价提交对话框 -->
    <el-dialog v-model="showEvaluationDialog" title="评价帮扶计划" width="600px" @close="resetEvaluationForm">
      <el-form :model="evaluationForm" label-width="100px">
        <el-form-item label="选择计划" required>
          <el-select v-model="evaluationForm.planId" placeholder="请选择要评价的计划" style="width: 100%">
            <el-option
              v-for="plan in activePlans"
              :key="plan.id"
              :label="`${plan.title} (${plan.id})`"
              :value="plan.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="计划评分" required>
          <el-rate v-model="evaluationForm.rating" allow-half show-score />
        </el-form-item>
        <el-form-item label="有效性评分" required>
          <el-rate v-model="evaluationForm.effectiveness" allow-half show-score />
        </el-form-item>
        <el-form-item label="反馈内容">
          <el-input
            v-model="evaluationForm.feedback"
            type="textarea"
            rows="4"
            placeholder="请输入您对帮扶计划的评价和建议"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="showEvaluationDialog = false">取消</el-button>
          <el-button type="primary" @click="handleSubmit">提交评价</el-button>
        </span>
      </template>
    </el-dialog>

    <!-- 编辑评价对话框 -->
    <el-dialog v-model="showEditDialog" title="编辑评价" width="600px" @close="resetEvaluationForm">
      <el-form :model="evaluationForm" label-width="100px">
        <el-form-item label="计划ID" disabled>
          <el-input v-model.number="evaluationForm.planId" disabled />
        </el-form-item>
        <el-form-item label="计划评分">
          <el-rate v-model="evaluationForm.rating" allow-half show-score />
        </el-form-item>
        <el-form-item label="有效性评分">
          <el-rate v-model="evaluationForm.effectiveness" allow-half show-score />
        </el-form-item>
        <el-form-item label="反馈内容">
          <el-input
            v-model="evaluationForm.feedback"
            type="textarea"
            rows="4"
            placeholder="请输入您对帮扶计划的评价和建议"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="showEditDialog = false">取消</el-button>
          <el-button type="primary" @click="handleUpdate">更新评价</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, computed } from 'vue'
import { studentAPI } from '@/api'
import { ElMessage } from 'element-plus'

const userId = ref(null)
const studentId = ref(null)

const statistics = ref({
  total: 0,
  avgRating: 0,
  avgEffectiveness: 0,
  ratingDistribution: {}
})

const evaluations = ref([])
const assistancePlans = ref([])
const filterStatus = ref('')

const showEvaluationDialog = ref(false)
const showEditDialog = ref(false)

const evaluationForm = reactive({
  planId: null,
  rating: 0,
  effectiveness: 0,
  feedback: '',
  studentId: null
})

// 计算待评价计划
const activePlans = computed(() => {
  if (filterStatus.value === 'pending') {
    return assistancePlans.value.filter(p => {
      const hasEvaluation = evaluations.value.some(e => e.planId === p.id)
      return !hasEvaluation && p.status !== 'completed'
    })
  } else if (filterStatus.value === 'evaluated') {
    return assistancePlans.value.filter(p => {
      return evaluations.value.some(e => e.planId === p.id)
    })
  }
  return assistancePlans.value.filter(p => p.status !== 'completed')
})

onMounted(async () => {
  const userInfo = JSON.parse(localStorage.getItem('user') || '{}')
  userId.value = localStorage.getItem('userId') || userInfo.userId || userInfo.id
  studentId.value = localStorage.getItem('studentId') || userInfo.studentId

  await loadAssistancePlans()
  await loadEvaluations()
  await loadStatistics()
})

async function loadAssistancePlans() {
  try {
    const response = await studentAPI.getAssistancePlans(userId.value)
    if (response.data?.code === 200) {
      assistancePlans.value = response.data.data || []
    }
  } catch (error) {
    console.error('加载帮扶计划失败', error)
  }
}

async function loadEvaluations() {
  try {
    const response = await studentAPI.getStudentEvaluations(studentId.value)
    if (response.data?.code === 200) {
      evaluations.value = response.data.data || []
    }
  } catch (error) {
    ElMessage.error('加载评价列表失败')
    console.error(error)
  }
}

async function loadStatistics() {
  try {
    const response = await studentAPI.getEvaluationStatistics(studentId.value)
    if (response.data?.code === 200) {
      statistics.value = response.data.data || {}
    }
  } catch (error) {
    console.error('加载统计数据失败', error)
  }
}

function handleEvaluate(plan) {
  evaluationForm.planId = plan.id
  evaluationForm.rating = 0
  evaluationForm.effectiveness = 0
  evaluationForm.feedback = ''
  evaluationForm.studentId = studentId.value
  showEvaluationDialog.value = true
}

function handleEdit(evaluation) {
  evaluationForm.id = evaluation.id
  evaluationForm.planId = evaluation.planId
  evaluationForm.rating = evaluation.rating || 0
  evaluationForm.effectiveness = evaluation.effectiveness || 0
  evaluationForm.feedback = evaluation.feedback || ''
  evaluationForm.studentId = studentId.value
  showEditDialog.value = true
}

async function handleSubmit() {
  if (!evaluationForm.planId) {
    ElMessage.warning('请选择要评价的计划')
    return
  }

  if (evaluationForm.rating === 0) {
    ElMessage.warning('请评分')
    return
  }

  try {
    const evaluation = {
      planId: evaluationForm.planId,
      studentId: studentId.value,
      rating: evaluationForm.rating,
      effectiveness: evaluationForm.effectiveness,
      feedback: evaluationForm.feedback
    }

    const response = await studentAPI.submitEvaluation(evaluation)
    if (response.data.success) {
      ElMessage.success('评价已提交')
      showEvaluationDialog.value = false
      resetEvaluationForm()
      await loadEvaluations()
      await loadStatistics()
    }
  } catch (error) {
    ElMessage.error('提交评价失败')
    console.error(error)
  }
}

async function handleUpdate() {
  if (!evaluationForm.rating) {
    ElMessage.warning('请评分')
    return
  }

  try {
    const evaluation = {
      id: evaluationForm.id,
      planId: evaluationForm.planId,
      studentId: studentId.value,
      rating: evaluationForm.rating,
      effectiveness: evaluationForm.effectiveness,
      feedback: evaluationForm.feedback
    }

    const response = await studentAPI.submitEvaluation(evaluation)
    if (response.data.success) {
      ElMessage.success('评价已更新')
      showEditDialog.value = false
      resetEvaluationForm()
      await loadEvaluations()
      await loadStatistics()
    }
  } catch (error) {
    ElMessage.error('更新评价失败')
    console.error(error)
  }
}

function resetEvaluationForm() {
  evaluationForm.id = null
  evaluationForm.planId = null
  evaluationForm.rating = 0
  evaluationForm.effectiveness = 0
  evaluationForm.feedback = ''
  evaluationForm.studentId = null
}

function getRatingPercentage(count) {
  const maxCount = Math.max(...Object.values(statistics.value.ratingDistribution || {}))
  return maxCount > 0 ? (count / maxCount) * 100 : 0
}

function getProgressColor(rating) {
  const colors = {
    '1': '#f56c6c',
    '2': '#e6a23c',
    '3': '#e6a23c',
    '4': '#67c23a',
    '5': '#67c23a'
  }
  return colors[rating] || '#67c23a'
}

function getEffectivenessType(effectiveness) {
  if (!effectiveness) return 'info'
  if (effectiveness >= 4) return 'success'
  if (effectiveness >= 3) return 'warning'
  return 'danger'
}

function getStatusLabel(status) {
  const labels = {
    initiated: '已启动',
    in_progress: '进行中',
    completed: '已完成',
    archived: '已归档'
  }
  return labels[status] || status
}

function getStatusType(status) {
  const types = {
    initiated: 'info',
    in_progress: 'warning',
    completed: 'success',
    archived: 'info'
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
.feedback-container {
  padding: 24px;
  background: linear-gradient(135deg, #f5f7fa 0%, #e8ecf1 100%);
  min-height: 100vh;

  .stats-row {
    margin-bottom: 24px;

    .stat-card {
      text-align: center;
      padding: 24px;
      border-radius: 16px;
      box-shadow: 0 6px 20px rgba(0, 0, 0, 0.08);
      transition: all 0.4s cubic-bezier(0.4, 0, 0.2, 1);
      border: 2px solid transparent;

      &:hover {
        box-shadow: 0 12px 32px rgba(0, 0, 0, 0.15);
        transform: translateY(-8px);
        border-color: #e8ecf1;
      }

      .stat-number {
        font-size: 32px;
        font-weight: bold;
        background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%);
        -webkit-background-clip: text;
        -webkit-text-fill-color: transparent;
        background-clip: text;
        margin-bottom: 10px;
      }

      .stat-label {
        font-size: 13px;
        color: #606266;
        font-weight: 500;
      }
    }
  }

  .content-row {
    .distribution-card {
      border-radius: 16px;
      box-shadow: 0 6px 20px rgba(0, 0, 0, 0.08);
      transition: all 0.3s ease;
      border: 2px solid transparent;

      &:hover {
        border-color: #e8ecf1;
        box-shadow: 0 8px 24px rgba(0, 0, 0, 0.12);
      }

      .card-header {
        .title {
          font-weight: 600;
          font-size: 16px;
          background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%);
          -webkit-background-clip: text;
          -webkit-text-fill-color: transparent;
          background-clip: text;
        }
      }

      .distribution-list {
        .distribution-item {
          display: flex;
          align-items: center;
          gap: 10px;
          padding: 10px 0;
          border-bottom: 1px solid #ebeef5;

          &:last-child {
            border-bottom: none;
          }

          .rating-star {
            min-width: 100px;
          }

          .el-progress {
            flex: 1;
          }

          .count {
            min-width: 50px;
            text-align: right;
            color: #606266;
          }
        }
      }
    }

    .toolbar-card {
      margin-bottom: 24px;
      border-radius: 16px;
      box-shadow: 0 6px 20px rgba(0, 0, 0, 0.08);
      transition: all 0.3s ease;
      border: 2px solid transparent;

      &:hover {
        border-color: #e8ecf1;
        box-shadow: 0 8px 24px rgba(0, 0, 0, 0.12);
      }
    }

    .table-card {
      border-radius: 16px;
      box-shadow: 0 6px 20px rgba(0, 0, 0, 0.08);
      transition: all 0.3s ease;
      border: 2px solid transparent;

      &:hover {
        border-color: #e8ecf1;
        box-shadow: 0 8px 24px rgba(0, 0, 0, 0.12);
      }

      .card-header {
        .title {
          font-weight: 600;
          font-size: 16px;
          background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%);
          -webkit-background-clip: text;
          -webkit-text-fill-color: transparent;
          background-clip: text;
        }
      }

      .feedback-text {
        color: #606266;
        word-break: break-word;
        line-height: 1.5;
      }

      .no-data-placeholder {
        padding: 40px 20px;
      }
    }

    .pending-card {
      margin-top: 24px;
      border-radius: 16px;
      box-shadow: 0 6px 20px rgba(0, 0, 0, 0.08);
      transition: all 0.3s ease;
      border: 2px solid transparent;

      &:hover {
        border-color: #e8ecf1;
        box-shadow: 0 8px 24px rgba(0, 0, 0, 0.12);
      }

      .card-header {
        .title {
          font-weight: 600;
          font-size: 16px;
          background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%);
          -webkit-background-clip: text;
          -webkit-text-fill-color: transparent;
          background-clip: text;
        }
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

  .card-header {
    .title {
      font-weight: 600;
      font-size: 16px;
    }
  }

  .communication-content {
    line-height: 1.8;

    p {
      margin: 12px 0;
      color: #333;
      font-size: 15px;
    }
  }

  :deep(.el-card) {
    border-radius: 16px;
    box-shadow: 0 6px 20px rgba(0, 0, 0, 0.08);
    border: 2px solid transparent;
    transition: all 0.3s ease;
    margin-bottom: 24px !important;
  }

  :deep(.el-card:hover) {
    border-color: #e8ecf1;
    box-shadow: 0 8px 24px rgba(0, 0, 0, 0.12);
  }

  :deep(.el-button) {
    border-radius: 8px;
    transition: all 0.3s ease;
  }

  :deep(.el-button:hover) {
    transform: translateY(-2px);
  }

  :deep(.el-table) {
    border-radius: 12px;
    overflow: hidden;
  }

  :deep(.el-table th) {
    background: linear-gradient(135deg, #f8f9fa 0%, #e8ecf1 100%);
    color: #333;
    font-weight: 600;
  }
}

</style>
