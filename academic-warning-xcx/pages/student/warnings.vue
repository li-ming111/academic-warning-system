<template>
  <view class="warnings-container">
    <view class="header">
      <text class="title">预警管理</text>
      <text class="subtitle">查看和管理您的学业预警信息</text>
    </view>
    
    <view class="warnings-list">
      <view v-if="warnings.length > 0" class="warnings-content">
        <view v-for="warning in warnings" :key="warning.id" class="warning-item" hover-class="warning-item-hover">
          <view class="warning-icon" :class="getLevelClass(warning.level)">
            {{ getLevelIcon(warning.level) }}
          </view>
          <view class="warning-body">
            <view class="warning-header">
              <text class="warning-title">{{ warning.title }}</text>
              <text :class="['warning-level', getLevelClass(warning.level)]">{{ getLevelText(warning.level) }}</text>
            </view>
            <view class="warning-content">
              <text class="warning-description">{{ warning.description }}</text>
              <view class="warning-meta">
                <text class="warning-date">{{ warning.createdAt }}</text>
              </view>
            </view>
          </view>
          <view class="warning-actions">
            <button class="action-button detail-button" @click="viewDetail(warning)">查看详情</button>
          </view>
        </view>
      </view>
      <view v-else class="empty-state">
        <view class="empty-icon">📭</view>
        <text>暂无预警信息</text>
      </view>
    </view>
    
    <!-- 预警详情弹窗 -->
    <view v-if="showDetail" class="modal-overlay" @click="closeDetail">
      <view class="modal-content" @click.stop>
        <view class="modal-header">
          <text class="modal-title">{{ selectedWarning?.title }}</text>
          <text class="close-btn" @click="closeDetail">×</text>
        </view>
        <view class="modal-body">
          <view class="detail-item">
            <text class="detail-label">预警等级：</text>
            <text :class="['warning-level', getLevelClass(selectedWarning?.level)]">{{ getLevelText(selectedWarning?.level) }}</text>
          </view>
          <view class="detail-item">
            <text class="detail-label">预警描述：</text>
            <text class="detail-value">{{ selectedWarning?.description }}</text>
          </view>
          <view class="detail-item">
            <text class="detail-label">创建时间：</text>
            <text class="detail-value">{{ selectedWarning?.createdAt }}</text>
          </view>
        </view>
        <view class="modal-footer">
          <button class="modal-button" @click="closeDetail">关闭</button>
        </view>
      </view>
    </view>
  </view>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import { apiClient } from '../../services/api';

const warnings = ref([]);
const selectedWarning = ref(null);
const showDetail = ref(false);

onMounted(() => {
  loadWarnings();
});

const loadWarnings = async () => {
  try {
    const response = await apiClient.getStudentWarnings();
    if (response) {
      warnings.value = response;
    }
  } catch (error) {
    console.error('获取预警信息失败:', error);
    // 使用模拟数据
    warnings.value = [
      {
        id: 1,
        title: '高数挂科预警',
        description: '高等数学成绩低于60分',
        level: 'high',
        createdAt: '2026-03-20T10:00:00'
      },
      {
        id: 2,
        title: '英语预警',
        description: '大学英语成绩低于70分',
        level: 'medium',
        createdAt: '2026-03-15T14:30:00'
      },
      {
        id: 3,
        title: '学分预警',
        description: '已修学分不足',
        level: 'low',
        createdAt: '2026-03-10T09:15:00'
      }
    ];
  }
};

const viewDetail = (warning) => {
  selectedWarning.value = warning;
  showDetail.value = true;
};

const closeDetail = () => {
  showDetail.value = false;
};

const getLevelClass = (level) => {
  switch (level) {
    case 'high':
      return 'danger';
    case 'medium':
      return 'warning';
    case 'low':
      return 'info';
    default:
      return 'info';
  }
};

const getLevelText = (level) => {
  switch (level) {
    case 'high':
      return '严重';
    case 'medium':
      return '中等';
    case 'low':
      return '轻微';
    default:
      return '未知';
  }
};

const getLevelIcon = (level) => {
  switch (level) {
    case 'high':
      return '🚨';
    case 'medium':
      return '⚠️';
    case 'low':
      return 'ℹ️';
    default:
      return '📌';
  }
};
</script>

<style scoped>
.warnings-container {
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

.warnings-list {
  display: flex;
  flex-direction: column;
  gap: 20rpx;
}

.warnings-content {
  display: flex;
  flex-direction: column;
  gap: 20rpx;
}

.warning-item {
  background-color: #fff;
  border-radius: 20rpx;
  padding: 24rpx;
  box-shadow: 0 2rpx 12rpx rgba(0, 0, 0, 0.08);
  border: 1rpx solid #f0f0f0;
  display: flex;
  align-items: flex-start;
  transition: all 0.3s ease;
}

.warning-item-hover {
  transform: translateY(-4rpx);
  box-shadow: 0 6rpx 20rpx rgba(0, 0, 0, 0.12);
  border-color: #4facfe;
}

.warning-icon {
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

.warning-icon.danger {
  background-color: #ffebee;
  color: #d32f2f;
}

.warning-icon.warning {
  background-color: #fff8e1;
  color: #f57c00;
}

.warning-icon.info {
  background-color: #e3f2fd;
  color: #1976d2;
}

.warning-body {
  flex: 1;
  margin-right: 16rpx;
}

.warning-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 12rpx;
}

.warning-title {
  font-size: 20rpx;
  font-weight: 600;
  color: #333;
  flex: 1;
  margin-right: 12rpx;
}

.warning-level {
  padding: 6rpx 16rpx;
  border-radius: 16rpx;
  font-size: 14rpx;
  font-weight: 600;
  white-space: nowrap;
}

.warning-level.warning {
  background-color: #fff3cd;
  color: #856404;
}

.warning-level.danger {
  background-color: #f8d7da;
  color: #721c24;
}

.warning-level.info {
  background-color: #d1ecf1;
  color: #0c5460;
}

.warning-content {
  margin-bottom: 16rpx;
  gap: 8rpx;
}

.warning-description {
  font-size: 16rpx;
  color: #666;
  line-height: 24rpx;
  display: block;
}

.warning-meta {
  display: flex;
  justify-content: space-between;
  font-size: 14rpx;
  color: #999;
}

.warning-actions {
  flex-shrink: 0;
  display: flex;
  align-items: flex-start;
}

.action-button {
  padding: 12rpx 24rpx;
  border-radius: 12rpx;
  font-size: 16rpx;
  border: none;
  outline: none;
  transition: all 0.3s ease;
}

.detail-button {
  background-color: #4facfe;
  color: #fff;
}

.detail-button:hover {
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

.modal-footer {
  display: flex;
  justify-content: center;
}

.modal-button {
  padding: 16rpx 48rpx;
  background-color: #4facfe;
  color: #fff;
  border: none;
  border-radius: 12rpx;
  font-size: 18rpx;
  outline: none;
  transition: all 0.3s ease;
}

.modal-button:hover {
  background-color: #3a9bfd;
  transform: scale(1.05);
}

/* 动画效果 */
.warnings-container > * {
  animation: fadeIn 0.5s ease-out forwards;
}

.warnings-container > *:nth-child(1) {
  animation-delay: 0.1s;
}

.warnings-container > *:nth-child(2) {
  animation-delay: 0.2s;
}
</style>