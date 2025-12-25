<template>
  <div class="assistance-container">
    <el-card>
      <template #header>
        <div class="card-header">帮扶计划</div>
      </template>

      <div class="plans-list">
        <div v-for="plan in assistancePlans" :key="plan.id" class="plan-card">
          <div class="plan-header">
            <div class="plan-title">
              <h3>{{ plan.title }}</h3>
              <el-tag :type="getStatusType(plan.status)">{{ plan.status }}</el-tag>
            </div>
            <div class="plan-date">{{ plan.createdAt }}</div>
          </div>

          <div class="plan-content">
            <p><strong>目标课程：</strong>{{ plan.courseName }}</p>
            <p><strong>辅导教师：</strong>{{ plan.teacherName }}</p>
            <p><strong>计划描述：</strong>{{ plan.description }}</p>
            <p><strong>辅导时间：</strong>{{ plan.scheduleTime }}</p>
          </div>

          <div class="progress-section">
            <div class="progress-header">
              <span>完成进度</span>
              <span class="progress-value">{{ plan.progressPercentage }}%</span>
            </div>
            <el-progress :percentage="plan.progressPercentage" :color="getProgressColor(plan.progressPercentage)"></el-progress>
            <p class="progress-detail">已完成 {{ plan.completedCount }} 次，共 {{ plan.totalCount }} 次</p>
          </div>

          <div class="action-section">
            <el-button type="primary" @click="updateProgress(plan)">更新进度</el-button>
            <el-button @click="viewPlanDetails(plan)">查看详情</el-button>
            <el-button type="success" @click="markCompleted(plan)">标记完成</el-button>
          </div>
        </div>
      </div>
    </el-card>

    <!-- 进度更新对话框 -->
    <el-dialog v-model="progressDialogVisible" title="更新帮扶进度" width="600px">
      <el-form :model="progressForm" label-width="100px">
        <el-form-item label="计划">
          <el-input v-model="progressForm.planTitle" disabled></el-input>
        </el-form-item>
        <el-form-item label="进度百分比">
          <el-slider v-model="progressForm.percentage" :min="0" :max="100" :step="5"></el-slider>
        </el-form-item>
        <el-form-item label="完成情况">
          <el-input v-model="progressForm.feedback" type="textarea" rows="4" placeholder="请描述本次辅导的完成情况和学习进度"></el-input>
        </el-form-item>
        <el-form-item label="遇到的问题">
          <el-input v-model="progressForm.issues" type="textarea" rows="3" placeholder="如有问题，请详细说明"></el-input>
        </el-form-item>
      </el-form>

      <template #footer>
        <el-button @click="progressDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitProgressUpdate">保存更新</el-button>
      </template>
    </el-dialog>

    <!-- 计划详情对话框 -->
    <el-dialog v-model="detailsDialogVisible" title="帮扶计划详情" width="600px">
      <div v-if="selectedPlan" class="plan-details">
        <p><strong>计划名称：</strong>{{ selectedPlan.title }}</p>
        <p><strong>目标课程：</strong>{{ selectedPlan.courseName }}</p>
        <p><strong>辅导教师：</strong>{{ selectedPlan.teacherName }}</p>
        <p><strong>计划开始：</strong>{{ selectedPlan.startDate }}</p>
        <p><strong>计划结束：</strong>{{ selectedPlan.endDate }}</p>
        <p><strong>计划描述：</strong>{{ selectedPlan.description }}</p>
        <p><strong>辅导方式：</strong>{{ selectedPlan.assistanceType }}</p>
        <p><strong>辅导时间：</strong>{{ selectedPlan.scheduleTime }}</p>
        <p><strong>当前进度：</strong>{{ selectedPlan.progressPercentage }}%</p>

        <el-divider></el-divider>

        <h4>进度记录</h4>
        <el-timeline>
          <el-timeline-item
            v-for="record in selectedPlan.progressRecords"
            :key="record.id"
            :timestamp="record.date"
            placement="top"
          >
            <p><strong>进度：</strong>{{ record.percentage }}%</p>
            <p>{{ record.feedback }}</p>
          </el-timeline-item>
        </el-timeline>

        <el-divider></el-divider>

        <p><strong>💡 提醒：</strong>坚持完成帮扶计划，定期与教师沟通，这会显著提升您的学业成绩。</p>
      </div>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
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

onMounted(async () => {
  const userId = getUserId()
  if (userId) {
    await loadAssistancePlans(userId)
  }
})

// 加载帮扶计划
const loadAssistancePlans = async (userId) => {
  try {
    console.log('帮扶userId:', userId, 'type:', typeof userId)
    if (!userId) {
      ElMessage.error('请先登录')
      return
    }
    const response = await studentAPI.getAssistancePlans(userId)
    console.log('帮扶计划响应:', response)
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
    ElMessage.success(`帮扶计划“${plan.title}”已标记为完成`)
    await loadAssistancePlans()
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

.card-header {
  font-size: 18px;
  font-weight: bold;
  color: #333;
}

.plans-list {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.plan-card {
  background: white;
  border-radius: 8px;
  padding: 20px;
  border-left: 4px solid #409eff;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
}

.plan-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 15px;
}

.plan-title {
  display: flex;
  align-items: center;
  gap: 10px;
}

.plan-title h3 {
  margin: 0;
  font-size: 16px;
  color: #333;
}

.plan-date {
  font-size: 12px;
  color: #999;
}

.plan-content {
  margin-bottom: 15px;
  padding: 15px;
  background-color: #f9f9f9;
  border-radius: 6px;
}

.plan-content p {
  margin: 8px 0;
  color: #666;
  font-size: 14px;
}

.progress-section {
  margin-bottom: 15px;
}

.progress-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 8px;
  font-size: 14px;
  font-weight: bold;
  color: #333;
}

.progress-value {
  color: #409eff;
  font-size: 16px;
}

.progress-detail {
  margin-top: 8px;
  font-size: 12px;
  color: #999;
}

.action-section {
  display: flex;
  gap: 10px;
}

.plan-details {
  line-height: 1.8;
}

.plan-details p {
  margin: 10px 0;
  color: #666;
}

.plan-details h4 {
  margin-top: 20px;
  color: #333;
}
</style>
