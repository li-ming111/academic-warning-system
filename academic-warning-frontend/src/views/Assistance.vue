<template>
  <div class="assistance-container">
    <!-- 页面标题 -->
    <div class="page-header">
      <h1>帮扶计划</h1>
    </div>

    <!-- 统计卡片 -->
    <div class="stats-grid">
      <div class="stat-card">
        <div class="stat-number">{{ assistancePlans.length }}</div>
        <div class="stat-label">总计划</div>
      </div>
      <div class="stat-card">
        <div class="stat-number">{{ inProgressCount }}</div>
        <div class="stat-label">进行中</div>
      </div>
      <div class="stat-card">
        <div class="stat-number">{{ completedCount }}</div>
        <div class="stat-label">已完成</div>
      </div>
      <div class="stat-card">
        <div class="stat-number">{{ averageProgress }}%</div>
        <div class="stat-label">平均进度</div>
      </div>
    </div>

    <!-- 计划列表 -->
    <div class="plans-section">
      <div class="plans-list">
        <el-card 
          v-for="plan in assistancePlans" 
          :key="plan.id"
          :body-style="{ padding: '20px' }"
          shadow="hover"
        >
          <!-- 计划头部 -->
          <div class="plan-header">
            <div>
              <h3 class="plan-title">{{ plan.title }}</h3>
              <div class="plan-meta">
                <el-tag :type="getStatusType(plan.status)" size="small">
                  {{ plan.status }}
                </el-tag>
                <span class="plan-date">{{ plan.createdAt }}</span>
              </div>
            </div>
            <el-button 
              type="primary" 
              size="small"
              @click="viewPlanDetails(plan)"
            >
              详情
            </el-button>
          </div>

          <!-- 计划内容 -->
          <div class="plan-content">
            <div class="plan-info">
              <div class="info-row">
                <span class="info-label">目标课程：</span>
                <span class="info-value">{{ plan.courseName }}</span>
              </div>
              <div class="info-row">
                <span class="info-label">辅导教师：</span>
                <span class="info-value">{{ plan.teacherName }}</span>
              </div>
              <div class="info-row">
                <span class="info-label">辅导时间：</span>
                <span class="info-value">{{ plan.scheduleTime }}</span>
              </div>
            </div>
            <div class="plan-description">
              {{ plan.description }}
            </div>
          </div>

          <!-- 进度部分 -->
          <div class="progress-section">
            <div class="progress-header">
              <span>完成进度</span>
              <span class="progress-value">{{ plan.progressPercentage }}%</span>
            </div>
            <el-progress 
              :percentage="plan.progressPercentage" 
              :color="getProgressColor(plan.progressPercentage)"
            ></el-progress>
            <div class="progress-detail">
              已完成 {{ plan.completedCount || 0 }} 次，共 {{ plan.totalCount || 1 }} 次
            </div>
          </div>

          <!-- 操作按钮 -->
          <div class="action-section">
            <el-button 
              type="primary" 
              @click="updateProgress(plan)"
              :disabled="plan.status === '已完成'"
              size="small"
            >
              更新进度
            </el-button>
            <el-button 
              type="success" 
              @click="markCompleted(plan)"
              :disabled="plan.status === '已完成'"
              size="small"
            >
              标记完成
            </el-button>
          </div>
        </el-card>

        <!-- 空状态 -->
        <div v-if="assistancePlans.length === 0" class="empty-state">
          <el-empty description="暂无帮扶计划">
            <template #description>
              <p>您目前还没有任何帮扶计划</p>
              <p>辅导员可能会根据您的学业情况为您创建计划</p>
            </template>
          </el-empty>
        </div>
      </div>
    </div>

    <!-- 进度更新对话框 -->
    <el-dialog 
      v-model="progressDialogVisible" 
      title="更新帮扶进度" 
      width="600px"
      destroy-on-close
    >
      <el-form :model="progressForm" label-width="100px">
        <el-form-item label="计划名称">
          <el-input v-model="progressForm.planTitle" disabled></el-input>
        </el-form-item>
        <el-form-item label="当前进度">
          <el-slider 
            v-model="progressForm.percentage" 
            :min="0" 
            :max="100" 
            :step="5"
            show-input
          ></el-slider>
        </el-form-item>
        <el-form-item label="完成情况" required>
          <el-input 
            v-model="progressForm.feedback" 
            type="textarea" 
            rows="3" 
            placeholder="请描述本次辅导的完成情况"
          ></el-input>
        </el-form-item>
        <el-form-item label="遇到的问题">
          <el-input 
            v-model="progressForm.issues" 
            type="textarea" 
            rows="2" 
            placeholder="如有问题，请说明"
          ></el-input>
        </el-form-item>
      </el-form>

      <template #footer>
        <el-button @click="progressDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitProgressUpdate">保存</el-button>
      </template>
    </el-dialog>

    <!-- 计划详情对话框 -->
    <el-dialog 
      v-model="detailsDialogVisible" 
      title="计划详情" 
      width="700px"
      destroy-on-close
    >
      <div v-if="selectedPlan" class="plan-details">
        <!-- 计划基本信息 -->
        <el-descriptions :column="2" border>
          <el-descriptions-item label="计划名称">{{ selectedPlan.title }}</el-descriptions-item>
          <el-descriptions-item label="状态">
            <el-tag :type="getStatusType(selectedPlan.status)">{{ selectedPlan.status }}</el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="目标课程">{{ selectedPlan.courseName }}</el-descriptions-item>
          <el-descriptions-item label="辅导教师">{{ selectedPlan.teacherName }}</el-descriptions-item>
          <el-descriptions-item label="计划开始">{{ selectedPlan.startDate }}</el-descriptions-item>
          <el-descriptions-item label="计划结束">{{ selectedPlan.endDate }}</el-descriptions-item>
          <el-descriptions-item label="辅导方式">{{ selectedPlan.assistanceType || '线下辅导' }}</el-descriptions-item>
          <el-descriptions-item label="辅导时间">{{ selectedPlan.scheduleTime }}</el-descriptions-item>
        </el-descriptions>

        <el-divider></el-divider>

        <!-- 计划描述 -->
        <div class="detail-section">
          <h4>计划描述</h4>
          <p>{{ selectedPlan.description }}</p>
        </div>

        <!-- 当前进度 -->
        <div class="detail-section">
          <h4>当前进度</h4>
          <el-progress 
            :percentage="selectedPlan.progressPercentage" 
            :color="getProgressColor(selectedPlan.progressPercentage)"
            :stroke-width="8"
          ></el-progress>
          <div class="progress-stats">
            已完成 {{ selectedPlan.completedCount || 0 }} 次，共 {{ selectedPlan.totalCount || 1 }} 次
          </div>
        </div>

        <!-- 进度记录 -->
        <div class="detail-section">
          <h4>进度记录</h4>
          <el-timeline>
            <el-timeline-item
              v-for="record in selectedPlan.progressRecords"
              :key="record.id"
              :timestamp="record.date"
            >
              <div class="timeline-content">
                <div class="timeline-header">
                  <span class="timeline-progress">{{ record.percentage }}%</span>
                </div>
                <p class="timeline-feedback">{{ record.feedback }}</p>
                <p v-if="record.issues" class="timeline-issues">
                  遇到的问题：{{ record.issues }}
                </p>
              </div>
            </el-timeline-item>
          </el-timeline>
        </div>

        <el-alert
          title="温馨提醒"
          type="info"
          :closable="false"
          show-icon
          style="margin-top: 20px"
        >
          坚持完成帮扶计划，定期与教师沟通，这会显著提升您的学业成绩。
        </el-alert>
      </div>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { studentAPI } from '@/api/index'
import { getUserId } from '@/utils/userUtils'

const assistancePlans = ref([])
const progressDialogVisible = ref(false)
const detailsDialogVisible = ref(false)
const selectedPlan = ref(null)

const progressForm = ref({
  planId: null,
  planTitle: '',
  percentage: 0,
  feedback: '',
  issues: ''
})

// 计算属性
const inProgressCount = computed(() => {
  return assistancePlans.value.filter(plan => plan.status === '进行中').length
})

const completedCount = computed(() => {
  return assistancePlans.value.filter(plan => plan.status === '已完成').length
})

const averageProgress = computed(() => {
  if (assistancePlans.value.length === 0) return 0
  const totalProgress = assistancePlans.value.reduce((sum, plan) => sum + plan.progressPercentage, 0)
  return Math.round(totalProgress / assistancePlans.value.length)
})

onMounted(async () => {
  const userId = getUserId()
  if (userId) {
    await loadAssistancePlans(userId)
  }
})

// 加载帮扶计划
const loadAssistancePlans = async (userId) => {
  try {
    if (!userId) {
      ElMessage.error('请先登录')
      return
    }
    const response = await studentAPI.getAssistancePlans(userId)
    if (Array.isArray(response) && response.length > 0) {
      assistancePlans.value = response.map(plan => ({
        ...plan,
        title: plan.title || plan.description || '帮扶计划',
        status: plan.status === 'in_progress' ? '进行中' : plan.status === 'completed' ? '已完成' : '未开始',
        progressPercentage: plan.progressPercentage || 0,
        progressRecords: plan.progressRecords || []
      }))
    } else {
      assistancePlans.value = []
    }
  } catch (error) {
    console.error('加载帮扶计划失败:', error)
    assistancePlans.value = []
  }
}

// 获取状态标签类型
const getStatusType = (status) => {
  if (status === '已完成') return 'success'
  if (status === '进行中') return 'primary'
  return 'info'
}

// 获取进度条颜色
const getProgressColor = (percentage) => {
  if (percentage >= 80) return '#67c23a'
  if (percentage >= 50) return '#e6a23c'
  return '#f56c6c'
}

// 更新进度
const updateProgress = (plan) => {
  progressForm.value = {
    planId: plan.id,
    planTitle: plan.title,
    percentage: plan.progressPercentage,
    feedback: '',
    issues: ''
  }
  progressDialogVisible.value = true
}

// 提交进度更新
const submitProgressUpdate = async () => {
  if (!progressForm.value.feedback) {
    ElMessage.error('请填写完成情况说明')
    return
  }

  try {
    await studentAPI.updatePlanProgress(progressForm.value.planId, progressForm.value.percentage)
    ElMessage.success('进度已更新')
    progressDialogVisible.value = false
    const userId = getUserId()
    await loadAssistancePlans(userId)
  } catch (error) {
    console.error('更新进度失败:', error)
    ElMessage.error('更新进度失败')
  }
}

// 查看计划详情
const viewPlanDetails = (plan) => {
  selectedPlan.value = plan
  detailsDialogVisible.value = true
}

// 标记完成
const markCompleted = async (plan) => {
  try {
    await studentAPI.updatePlanProgress(plan.id, 100)
    ElMessage.success(`帮扶计划"${plan.title}"已标记为完成`)
    const userId = getUserId()
    await loadAssistancePlans(userId)
  } catch (error) {
    console.error('标记完成失败:', error)
    ElMessage.error('标记完成失败')
  }
}
</script>

<style scoped>
.assistance-container {
  padding: 20px;
  background-color: #f5f7fa;
  min-height: 100vh;
}

/* 页面标题 */
.page-header {
  margin-bottom: 24px;
}

.page-header h1 {
  font-size: 24px;
  font-weight: 600;
  color: #303133;
  margin: 0;
}

/* 统计卡片 */
.stats-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(150px, 1fr));
  gap: 16px;
  margin-bottom: 24px;
}

.stat-card {
  background: white;
  border-radius: 8px;
  padding: 16px;
  text-align: center;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.05);
  border: 1px solid #e4e7ed;
}

.stat-number {
  font-size: 20px;
  font-weight: 600;
  color: #303133;
  margin-bottom: 4px;
}

.stat-label {
  font-size: 14px;
  color: #909399;
}

/* 计划部分 */
.plans-section {
  margin-bottom: 24px;
}

.plans-list {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

/* 计划卡片 */
.plan-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 16px;
  padding-bottom: 12px;
  border-bottom: 1px solid #f0f0f0;
}

.plan-title {
  font-size: 16px;
  font-weight: 600;
  color: #303133;
  margin: 0 0 8px 0;
}

.plan-meta {
  display: flex;
  align-items: center;
  gap: 8px;
}

.plan-date {
  font-size: 12px;
  color: #909399;
}

/* 计划内容 */
.plan-content {
  margin-bottom: 16px;
}

.plan-info {
  margin-bottom: 12px;
}

.info-row {
  display: flex;
  margin-bottom: 8px;
  font-size: 14px;
}

.info-label {
  width: 90px;
  color: #909399;
}

.info-value {
  color: #303133;
  flex: 1;
}

.plan-description {
  font-size: 14px;
  color: #606266;
  line-height: 1.4;
}

/* 进度部分 */
.progress-section {
  margin-bottom: 16px;
}

.progress-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 8px;
  font-size: 14px;
  color: #303133;
}

.progress-value {
  font-weight: 600;
  color: #409eff;
}

.progress-detail {
  margin-top: 8px;
  font-size: 12px;
  color: #909399;
  text-align: right;
}

/* 操作部分 */
.action-section {
  display: flex;
  gap: 10px;
  justify-content: flex-end;
}

/* 空状态 */
.empty-state {
  background: white;
  border-radius: 8px;
  padding: 40px 20px;
  text-align: center;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.05);
  border: 1px solid #e4e7ed;
}

/* 计划详情 */
.plan-details {
  line-height: 1.5;
}

.detail-section {
  margin-bottom: 20px;
}

.detail-section h4 {
  font-size: 14px;
  font-weight: 600;
  color: #303133;
  margin-bottom: 12px;
}

.progress-stats {
  margin-top: 8px;
  font-size: 14px;
  color: #606266;
  text-align: center;
}

.timeline-content {
  font-size: 14px;
}

.timeline-header {
  margin-bottom: 8px;
}

.timeline-progress {
  font-weight: 600;
  color: #409eff;
}

.timeline-feedback {
  color: #606266;
  margin: 0 0 8px 0;
}

.timeline-issues {
  color: #f56c6c;
  margin: 0;
  font-size: 13px;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .assistance-container {
    padding: 16px;
  }

  .stats-grid {
    grid-template-columns: repeat(2, 1fr);
  }

  .info-row {
    flex-direction: column;
  }

  .info-label {
    width: auto;
    margin-bottom: 4px;
  }

  .action-section {
    flex-direction: column;
  }

  .action-section .el-button {
    width: 100%;
  }
}
</style>
