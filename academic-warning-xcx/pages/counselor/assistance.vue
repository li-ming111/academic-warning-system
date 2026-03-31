<template>
  <view class="assistance-container">
    <view class="header">
      <text class="title">帮扶计划</text>
      <text class="subtitle">管理学生学业帮扶计划</text>
    </view>

    <view class="stats-cards">
      <view class="stat-card active">
        <text class="stat-number">{{ stats.active }}</text>
        <text class="stat-label">进行中</text>
      </view>
      <view class="stat-card completed">
        <text class="stat-number">{{ stats.completed }}</text>
        <text class="stat-label">已完成</text>
      </view>
      <view class="stat-card pending">
        <text class="stat-number">{{ stats.pending }}</text>
        <text class="stat-label">待开始</text>
      </view>
    </view>

    <view class="plans-list">
      <view v-for="plan in plans" :key="plan.id" class="plan-item" hover-class="plan-item-hover">
        <view class="plan-icon" :class="plan.status">{{ getIcon(plan.status) }}</view>
        <view class="plan-content">
          <text class="plan-title">{{ plan.studentName }} - {{ plan.title }}</text>
          <text class="plan-desc">{{ plan.description }}</text>
          <view class="plan-progress">
            <view class="progress-bar">
              <view class="progress-fill" :style="{ width: plan.progress + '%' }"></view>
            </view>
            <text class="progress-text">{{ plan.progress }}%</text>
          </view>
          <text class="plan-date">{{ plan.startDate }} 至 {{ plan.endDate }}</text>
        </view>
        <view class="plan-actions">
          <button class="action-btn" @click="viewPlan(plan)">查看</button>
        </view>
      </view>
    </view>

    <view class="add-section">
      <button class="add-btn" @click="createPlan">+ 创建帮扶计划</button>
    </view>
  </view>
</template>

<script setup>
import { ref } from 'vue';

const stats = ref({ active: 8, completed: 15, pending: 3 });

const plans = ref([
  { id: 1, studentName: '张三', title: '数学辅导计划', description: '针对高等数学成绩不理想的学生进行一对一辅导', status: 'active', progress: 60, startDate: '2026-03-01', endDate: '2026-06-01' },
  { id: 2, studentName: '李四', title: '英语学习计划', description: '提高英语听说读写能力，每周三次辅导', status: 'active', progress: 40, startDate: '2026-03-15', endDate: '2026-05-15' },
  { id: 3, studentName: '王五', title: '专业课辅导', description: '计算机基础课程辅导，帮助理解核心概念', status: 'pending', progress: 0, startDate: '2026-04-01', endDate: '2026-06-30' },
  { id: 4, studentName: '赵六', title: '综合素质提升', description: '全面提升学习能力和综合素质', status: 'completed', progress: 100, startDate: '2026-01-01', endDate: '2026-03-01' }
]);

const getIcon = (status) => {
  const icons = { active: '🔄', completed: '✅', pending: '⏳' };
  return icons[status];
};

const viewPlan = (plan) => {
  uni.showToast({ title: `查看计划: ${plan.title}`, icon: 'none' });
};

const createPlan = () => {
  uni.showToast({ title: '创建新帮扶计划', icon: 'none' });
};
</script>

<style scoped>
.assistance-container { padding: 20rpx; background-color: #f5f7fa; min-height: 100vh; }
.header { margin-bottom: 24rpx; }
.title { font-size: 32rpx; font-weight: 700; color: #333; margin-bottom: 8rpx; display: block; }
.subtitle { font-size: 18rpx; color: #666; display: block; }
.stats-cards { display: flex; gap: 16rpx; margin-bottom: 24rpx; }
.stat-card { flex: 1; background: white; border-radius: 16rpx; padding: 20rpx; text-align: center; box-shadow: 0 2rpx 12rpx rgba(0,0,0,0.08); }
.stat-card.active { border-left: 4rpx solid #4caf50; }
.stat-card.completed { border-left: 4rpx solid #1976d2; }
.stat-card.pending { border-left: 4rpx solid #f57c00; }
.stat-number { font-size: 28rpx; font-weight: 700; display: block; margin-bottom: 4rpx; }
.stat-card.active .stat-number { color: #4caf50; }
.stat-card.completed .stat-number { color: #1976d2; }
.stat-card.pending .stat-number { color: #f57c00; }
.stat-label { font-size: 14rpx; color: #666; }
.plans-list { display: flex; flex-direction: column; gap: 16rpx; margin-bottom: 24rpx; }
.plan-item { background: white; border-radius: 20rpx; padding: 24rpx; box-shadow: 0 2rpx 12rpx rgba(0,0,0,0.08); border: 1rpx solid #f0f0f0; display: flex; align-items: flex-start; }
.plan-item-hover { border-color: #4facfe; }
.plan-icon { width: 60rpx; height: 60rpx; border-radius: 50%; display: flex; align-items: center; justify-content: center; margin-right: 20rpx; flex-shrink: 0; font-size: 28rpx; }
.plan-icon.active { background-color: #e8f5e9; }
.plan-icon.completed { background-color: #e3f2fd; }
.plan-icon.pending { background-color: #fff8e1; }
.plan-content { flex: 1; margin-right: 16rpx; }
.plan-title { font-size: 18rpx; font-weight: 600; color: #333; display: block; margin-bottom: 4rpx; }
.plan-desc { font-size: 14rpx; color: #666; display: block; margin-bottom: 12rpx; }
.plan-progress { display: flex; align-items: center; margin-bottom: 8rpx; }
.progress-bar { flex: 1; height: 8rpx; background-color: #f0f0f0; border-radius: 4rpx; overflow: hidden; margin-right: 12rpx; }
.progress-fill { height: 100%; background-color: #4facfe; border-radius: 4rpx; transition: width 0.3s ease; }
.progress-text { font-size: 14rpx; color: #4facfe; font-weight: 600; min-width: 50rpx; }
.plan-date { font-size: 12rpx; color: #999; }
.plan-actions { flex-shrink: 0; }
.action-btn { padding: 12rpx 24rpx; background-color: #4facfe; color: white; border: none; border-radius: 12rpx; font-size: 14rpx; }
.add-section { margin-top: 40rpx; }
.add-btn { width: 100%; height: 90rpx; background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%); color: white; border: none; border-radius: 16rpx; font-size: 18rpx; font-weight: 600; }
</style>