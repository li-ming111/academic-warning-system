<template>
  <view class="warnings-container">
    <view class="header">
      <text class="title">预警管理</text>
      <text class="subtitle">管理学生学业预警</text>
    </view>

    <view class="stats-cards">
      <view class="stat-card high">
        <text class="stat-number">{{ stats.high }}</text>
        <text class="stat-label">严重预警</text>
      </view>
      <view class="stat-card medium">
        <text class="stat-number">{{ stats.medium }}</text>
        <text class="stat-label">中等预警</text>
      </view>
      <view class="stat-card low">
        <text class="stat-number">{{ stats.low }}</text>
        <text class="stat-label">轻微预警</text>
      </view>
    </view>

    <view class="warnings-list">
      <view v-for="warning in warnings" :key="warning.id" class="warning-item" hover-class="warning-item-hover">
        <view class="warning-icon" :class="warning.level">{{ getIcon(warning.level) }}</view>
        <view class="warning-content">
          <text class="warning-title">{{ warning.studentName }} - {{ warning.title }}</text>
          <text class="warning-desc">{{ warning.description }}</text>
          <text class="warning-date">{{ warning.date }}</text>
        </view>
        <view class="warning-actions">
          <button class="action-btn" @click="handleWarning(warning)">处理</button>
        </view>
      </view>
    </view>
  </view>
</template>

<script setup>
import { ref } from 'vue';

const stats = ref({ high: 5, medium: 12, low: 20 });

const warnings = ref([
  { id: 1, studentName: '张三', title: '成绩预警', description: '高等数学成绩低于60分', level: 'high', date: '2026-03-30' },
  { id: 2, studentName: '李四', title: '出勤预警', description: '缺课次数超过3次', level: 'medium', date: '2026-03-29' },
  { id: 3, studentName: '王五', title: '作业预警', description: '作业提交率低于50%', level: 'high', date: '2026-03-28' },
  { id: 4, studentName: '赵六', title: '成绩预警', description: '英语成绩低于70分', level: 'low', date: '2026-03-27' }
]);

const getIcon = (level) => {
  const icons = { high: '🚨', medium: '⚠️', low: 'ℹ️' };
  return icons[level];
};

const handleWarning = (warning) => {
  uni.showToast({ title: `处理预警: ${warning.studentName}`, icon: 'none' });
};
</script>

<style scoped>
.warnings-container { padding: 20rpx; background-color: #f5f7fa; min-height: 100vh; }
.header { margin-bottom: 24rpx; }
.title { font-size: 32rpx; font-weight: 700; color: #333; margin-bottom: 8rpx; display: block; }
.subtitle { font-size: 18rpx; color: #666; display: block; }
.stats-cards { display: flex; gap: 16rpx; margin-bottom: 24rpx; }
.stat-card { flex: 1; background: white; border-radius: 16rpx; padding: 20rpx; text-align: center; box-shadow: 0 2rpx 12rpx rgba(0,0,0,0.08); }
.stat-card.high { border-left: 4rpx solid #d32f2f; }
.stat-card.medium { border-left: 4rpx solid #f57c00; }
.stat-card.low { border-left: 4rpx solid #1976d2; }
.stat-number { font-size: 28rpx; font-weight: 700; display: block; margin-bottom: 4rpx; }
.stat-card.high .stat-number { color: #d32f2f; }
.stat-card.medium .stat-number { color: #f57c00; }
.stat-card.low .stat-number { color: #1976d2; }
.stat-label { font-size: 14rpx; color: #666; }
.warnings-list { display: flex; flex-direction: column; gap: 16rpx; }
.warning-item { background: white; border-radius: 20rpx; padding: 24rpx; box-shadow: 0 2rpx 12rpx rgba(0,0,0,0.08); border: 1rpx solid #f0f0f0; display: flex; align-items: flex-start; }
.warning-item-hover { border-color: #4facfe; }
.warning-icon { width: 60rpx; height: 60rpx; border-radius: 50%; display: flex; align-items: center; justify-content: center; margin-right: 20rpx; flex-shrink: 0; font-size: 28rpx; }
.warning-icon.high { background-color: #ffebee; }
.warning-icon.medium { background-color: #fff8e1; }
.warning-icon.low { background-color: #e3f2fd; }
.warning-content { flex: 1; margin-right: 16rpx; }
.warning-title { font-size: 18rpx; font-weight: 600; color: #333; display: block; margin-bottom: 4rpx; }
.warning-desc { font-size: 14rpx; color: #666; display: block; margin-bottom: 4rpx; }
.warning-date { font-size: 12rpx; color: #999; }
.warning-actions { flex-shrink: 0; }
.action-btn { padding: 12rpx 24rpx; background-color: #4facfe; color: white; border: none; border-radius: 12rpx; font-size: 14rpx; }
</style>