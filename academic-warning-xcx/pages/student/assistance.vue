<template>
  <view class="assistance-container">
    <view class="header">
      <text class="title">帮扶计划</text>
      <text class="subtitle">查看和管理您的学业帮扶计划</text>
    </view>
    
    <view class="assistance-list">
      <view v-if="assistancePlans.length > 0" class="plans-content">
        <view v-for="plan in assistancePlans" :key="plan.id" class="plan-item" hover-class="plan-item-hover">
          <view class="plan-icon" :class="getStatusClass(plan.status)">
            {{ getPlanIcon(plan.status) }}
          </view>
          <view class="plan-body">
            <view class="plan-header">
              <text class="plan-title">{{ plan.title }}</text>
              <text :class="['plan-status', getStatusClass(plan.status)]">{{ getStatusText(plan.status) }}</text>
            </view>
            <view class="plan-content">
              <text class="plan-description">{{ plan.description }}</text>
              <view class="plan-meta">
                <text class="plan-start-date">开始时间：{{ plan.startDate }}</text>
                <text class="plan-end-date">结束时间：{{ plan.endDate }}</text>
              </view>
              <view class="plan-progress">
                <text class="progress-label">当前进度：</text>
                <view class="progress-container">
                  <view class="progress-bar" :style="{ width: plan.progress + '%' }"></view>
                </view>
                <text class="progress-text">{{ plan.progress }}%</text>
              </view>
            </view>
            <view class="plan-actions">
              <button class="action-button detail-button" @click="viewDetail(plan)">查看详情</button>
              <button v-if="plan.status === 'active'" class="action-button report-button" @click="submitReport(plan.id)">更新进度</button>
            </view>
          </view>
        </view>
      </view>
      <view v-else class="empty-state">
        <view class="empty-icon">🤝</view>
        <text>暂无帮扶计划</text>
      </view>
    </view>
    
    <!-- 帮扶计划详情弹窗 -->
    <view v-if="showDetail" class="modal-overlay" @click="closeDetail">
      <view class="modal-content" @click.stop>
        <view class="modal-header">
          <text class="modal-title">{{ selectedPlan?.title }}</text>
          <text class="close-btn" @click="closeDetail">×</text>
        </view>
        <view class="modal-body">
          <view class="detail-item">
            <text class="detail-label">计划状态：</text>
            <text :class="['plan-status', getStatusClass(selectedPlan?.status)]">{{ getStatusText(selectedPlan?.status) }}</text>
          </view>
          <view class="detail-item">
            <text class="detail-label">帮扶目标：</text>
            <text class="detail-value">{{ selectedPlan?.goal }}</text>
          </view>
          <view class="detail-item">
            <text class="detail-label">开始时间：</text>
            <text class="detail-value">{{ selectedPlan?.startDate }}</text>
          </view>
          <view class="detail-item">
            <text class="detail-label">结束时间：</text>
            <text class="detail-value">{{ selectedPlan?.endDate }}</text>
          </view>
          <view class="detail-item">
            <text class="detail-label">当前进度：</text>
            <view class="progress-container">
              <view class="progress-bar" :style="{ width: selectedPlan?.progress + '%' }"></view>
            </view>
            <text class="progress-text">{{ selectedPlan?.progress }}%</text>
          </view>
        </view>
        <view class="modal-footer">
          <button class="modal-button" @click="closeDetail">关闭</button>
        </view>
      </view>
    </view>
    
    <!-- 提交报告弹窗 -->
    <view v-if="showReport" class="modal-overlay" @click="closeReport">
      <view class="modal-content" @click.stop>
        <view class="modal-header">
          <text class="modal-title">更新帮扶进度</text>
          <text class="close-btn" @click="closeReport">×</text>
        </view>
        <view class="modal-body">
          <view class="form-item">
            <text class="form-label">当前进度：</text>
            <view class="slider-container">
              <slider :value="reportProgress" min="0" max="100" show-value @change="handleProgressChange" />
            </view>
          </view>
        </view>
        <view class="modal-footer">
          <button class="modal-button cancel-button" @click="closeReport">取消</button>
          <button class="modal-button submit-button" @click="confirmReport">提交</button>
        </view>
      </view>
    </view>
  </view>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import { apiClient } from '../../services/api';

const assistancePlans = ref([]);
const selectedPlan = ref(null);
const showDetail = ref(false);
const showReport = ref(false);
const reportProgress = ref(0);
const currentPlanId = ref(null);

onMounted(() => {
  loadAssistancePlans();
});

const loadAssistancePlans = async () => {
  try {
    const response = await apiClient.getStudentAssistancePlans();
    if (response) {
      assistancePlans.value = response;
    }
  } catch (error) {
    console.error('获取帮扶计划失败:', error);
    // 使用模拟数据
    assistancePlans.value = [
      {
        id: 1,
        title: '高等数学辅导计划',
        description: '针对高等数学课程的专项辅导，包括知识点讲解、习题练习和答疑解惑。',
        goal: '提高高等数学成绩，确保期末考试及格',
        startDate: '2026-03-01',
        endDate: '2026-06-30',
        status: 'active',
        progress: 60
      },
      {
        id: 2,
        title: '学习方法改进计划',
        description: '通过学习方法指导，帮助学生建立科学的学习习惯和方法。',
        goal: '改进学习方法，提高学习效率',
        startDate: '2026-02-15',
        endDate: '2026-05-15',
        status: 'completed',
        progress: 100
      },
      {
        id: 3,
        title: '英语听力提升计划',
        description: '针对英语听力的专项训练，提高听力理解能力。',
        goal: '提高英语听力水平，通过英语四级考试',
        startDate: '2026-03-10',
        endDate: '2026-09-10',
        status: 'active',
        progress: 30
      }
    ];
  }
};

const viewDetail = (plan) => {
  selectedPlan.value = plan;
  showDetail.value = true;
};

const closeDetail = () => {
  showDetail.value = false;
};

const submitReport = (planId) => {
  currentPlanId.value = planId;
  const plan = assistancePlans.value.find(p => p.id === planId);
  if (plan) {
    reportProgress.value = plan.progress;
  }
  showReport.value = true;
};

const closeReport = () => {
  showReport.value = false;
};

const handleProgressChange = (e) => {
  reportProgress.value = e.detail.value;
};

const confirmReport = async () => {
  try {
    await apiClient.updatePlanProgress(currentPlanId.value, reportProgress.value);
    
    // 更新本地数据
    const plan = assistancePlans.value.find(p => p.id === currentPlanId.value);
    if (plan) {
      plan.progress = reportProgress.value;
      if (reportProgress.value >= 100) {
        plan.status = 'completed';
      }
    }
    
    showReport.value = false;
    
    uni.showToast({
      title: '进度更新成功',
      icon: 'success'
    });
  } catch (error) {
    console.error('更新进度失败:', error);
    uni.showToast({
      title: '更新失败',
      icon: 'error'
    });
  }
};

const getStatusClass = (status) => {
  switch (status) {
    case 'active':
      return 'active';
    case 'completed':
      return 'completed';
    case 'pending':
      return 'pending';
    default:
      return 'pending';
  }
};

const getStatusText = (status) => {
  switch (status) {
    case 'active':
      return '进行中';
    case 'completed':
      return '已完成';
    case 'pending':
      return '待开始';
    default:
      return '未知';
  }
};

const getPlanIcon = (status) => {
  switch (status) {
    case 'active':
      return '🔄';
    case 'completed':
      return '✅';
    case 'pending':
      return '⏳';
    default:
      return '📋';
  }
};
</script>

<style scoped>
.assistance-container {
  padding: 20rpx;
  background-color: #f5f7fa;
  min-height: 100vh;
}

.header {
  margin-bottom: 24rpx;
}

.title {
  font-size: 32rpx;
  font-weight: 700;
  color: #333;
  margin-bottom: 8rpx;
  display: block;
}

.subtitle {
  font-size: 18rpx;
  color: #666;
  display: block;
}

.assistance-list {
  display: flex;
  flex-direction: column;
  gap: 20rpx;
}

.plans-content {
  display: flex;
  flex-direction: column;
  gap: 20rpx;
}

.plan-item {
  background-color: #fff;
  border-radius: 20rpx;
  padding: 24rpx;
  box-shadow: 0 2rpx 12rpx rgba(0, 0, 0, 0.08);
  border: 1rpx solid #f0f0f0;
  display: flex;
  align-items: flex-start;
  transition: all 0.3s ease;
}

.plan-item-hover {
  transform: translateY(-4rpx);
  box-shadow: 0 6rpx 20rpx rgba(0, 0, 0, 0.12);
  border-color: #4facfe;
}

.plan-icon {
  width: 60rpx;
  height: 60rpx;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-right: 20rpx;
  flex-shrink: 0;
  font-size: 32rpx;
}

.plan-icon.active {
  background-color: #e8f5e9;
  color: #4caf50;
}

.plan-icon.completed {
  background-color: #e3f2fd;
  color: #1976d2;
}

.plan-icon.pending {
  background-color: #fff8e1;
  color: #f57c00;
}

.plan-body {
  flex: 1;
}

.plan-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 12rpx;
}

.plan-title {
  font-size: 20rpx;
  font-weight: 600;
  color: #333;
  flex: 1;
  margin-right: 12rpx;
}

.plan-status {
  padding: 6rpx 16rpx;
  border-radius: 16rpx;
  font-size: 14rpx;
  font-weight: 600;
  white-space: nowrap;
}

.plan-status.active {
  background-color: #d4edda;
  color: #155724;
}

.plan-status.completed {
  background-color: #d1ecf1;
  color: #0c5460;
}

.plan-status.pending {
  background-color: #fff3cd;
  color: #856404;
}

.plan-content {
  margin-bottom: 16rpx;
  gap: 8rpx;
}

.plan-description {
  font-size: 16rpx;
  color: #666;
  line-height: 24rpx;
  display: block;
  margin-bottom: 12rpx;
}

.plan-meta {
  display: flex;
  justify-content: space-between;
  font-size: 14rpx;
  color: #999;
  margin-bottom: 12rpx;
}

.plan-progress {
  display: flex;
  align-items: center;
  margin-bottom: 16rpx;
}

.progress-label {
  font-size: 14rpx;
  color: #666;
  margin-right: 12rpx;
  white-space: nowrap;
}

.progress-container {
  flex: 1;
  height: 12rpx;
  background-color: #f0f0f0;
  border-radius: 6rpx;
  overflow: hidden;
  margin-right: 12rpx;
}

.progress-bar {
  height: 100%;
  background-color: #4facfe;
  border-radius: 6rpx;
  transition: width 0.3s ease;
}

.progress-text {
  font-size: 14rpx;
  color: #333;
  font-weight: 600;
  min-width: 50rpx;
  text-align: right;
}

.plan-actions {
  display: flex;
  gap: 12rpx;
  justify-content: flex-end;
}

.action-button {
  padding: 10rpx 20rpx;
  border-radius: 12rpx;
  font-size: 16rpx;
  border: none;
  outline: none;
  transition: all 0.3s ease;
}

.detail-button {
  background-color: #f0f0f0;
  color: #333;
}

.detail-button:hover {
  background-color: #e0e0e0;
  transform: scale(1.05);
}

.report-button {
  background-color: #4facfe;
  color: #fff;
}

.report-button:hover {
  background-color: #3a9bfd;
  transform: scale(1.05);
}

.empty-state {
  text-align: center;
  padding: 60rpx 0;
  color: #999;
  font-size: 16rpx;
  background-color: #fff;
  border-radius: 20rpx;
  box-shadow: 0 2rpx 12rpx rgba(0, 0, 0, 0.08);
  border: 1rpx solid #f0f0f0;
}

.empty-icon {
  font-size: 60rpx;
  margin-bottom: 16rpx;
  display: block;
}

/* 模态框样式 */
.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background-color: rgba(0, 0, 0, 0.5);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 9999;
  animation: fadeIn 0.3s ease-out;
}

.modal-content {
  width: 90%;
  max-width: 600rpx;
  background-color: #fff;
  border-radius: 20rpx;
  padding: 32rpx;
  box-shadow: 0 8rpx 32rpx rgba(0, 0, 0, 0.2);
  animation: slideIn 0.3s ease-out;
}

@keyframes fadeIn {
  from {
    opacity: 0;
  }
  to {
    opacity: 1;
  }
}

@keyframes slideIn {
  from {
    opacity: 0;
    transform: translateY(-20rpx);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.modal-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24rpx;
  padding-bottom: 16rpx;
  border-bottom: 1rpx solid #f0f0f0;
}

.modal-title {
  font-size: 24rpx;
  font-weight: 700;
  color: #333;
}

.close-btn {
  font-size: 32rpx;
  color: #999;
  cursor: pointer;
  transition: all 0.3s ease;
}

.close-btn:hover {
  color: #333;
  transform: rotate(90deg);
}

.modal-body {
  margin-bottom: 32rpx;
}

.detail-item {
  display: flex;
  margin-bottom: 20rpx;
  align-items: flex-start;
}

.detail-label {
  font-size: 16rpx;
  color: #666;
  width: 120rpx;
  flex-shrink: 0;
}

.detail-value {
  font-size: 16rpx;
  color: #333;
  flex: 1;
  line-height: 24rpx;
}

.progress-container {
  flex: 1;
  height: 12rpx;
  background-color: #f0f0f0;
  border-radius: 6rpx;
  overflow: hidden;
  margin-right: 12rpx;
}

.progress-bar {
  height: 100%;
  background-color: #4facfe;
  border-radius: 6rpx;
  transition: width 0.3s ease;
}

.progress-text {
  font-size: 16rpx;
  color: #333;
  font-weight: 600;
  min-width: 50rpx;
  text-align: right;
}

/* 表单样式 */
.form-item {
  margin-bottom: 24rpx;
}

.form-label {
  font-size: 16rpx;
  color: #666;
  display: block;
  margin-bottom: 12rpx;
}

.slider-container {
  padding: 0 20rpx;
}

.modal-footer {
  display: flex;
  gap: 20rpx;
  justify-content: center;
}

.modal-button {
  padding: 16rpx 48rpx;
  border: none;
  border-radius: 12rpx;
  font-size: 18rpx;
  outline: none;
  transition: all 0.3s ease;
}

.cancel-button {
  background-color: #f0f0f0;
  color: #333;
}

.cancel-button:hover {
  background-color: #e0e0e0;
  transform: scale(1.05);
}

.submit-button {
  background-color: #4facfe;
  color: #fff;
}

.submit-button:hover {
  background-color: #3a9bfd;
  transform: scale(1.05);
}

/* 动画效果 */
.assistance-container > * {
  animation: fadeIn 0.5s ease-out forwards;
}

.assistance-container > *:nth-child(1) {
  animation-delay: 0.1s;
}

.assistance-container > *:nth-child(2) {
  animation-delay: 0.2s;
}
</style>