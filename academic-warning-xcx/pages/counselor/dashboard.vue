<template>
  <view class="counselor-dashboard">
    <view class="header">
      <view class="user-info">
        <text class="welcome">欢迎，{{ counselorName }}</text>
        <text class="role">辅导员</text>
      </view>
      <view class="avatar">
        <text class="avatar-text">辅</text>
      </view>
    </view>

    <view class="stats-container">
      <view class="stat-card" hover-class="stat-card-hover">
        <view class="stat-icon students">👨‍🎓</view>
        <text class="stat-value">{{ stats.totalStudents }}</text>
        <text class="stat-label">负责学生</text>
      </view>
      <view class="stat-card" hover-class="stat-card-hover">
        <view class="stat-icon warnings">⚠️</view>
        <text class="stat-value">{{ stats.totalWarnings }}</text>
        <text class="stat-label">预警学生</text>
      </view>
      <view class="stat-card" hover-class="stat-card-hover">
        <view class="stat-icon assistance">🤝</view>
        <text class="stat-value">{{ stats.assistancePlans }}</text>
        <text class="stat-label">帮扶计划</text>
      </view>
      <view class="stat-card" hover-class="stat-card-hover">
        <view class="stat-icon classes">🏫</view>
        <text class="stat-value">{{ stats.totalClasses }}</text>
        <text class="stat-label">管理班级</text>
      </view>
    </view>

    <view class="quick-actions">
      <view class="section-header">
        <text class="section-title">学生工作</text>
      </view>
      <view class="action-grid">
        <view class="action-item" @click="manageStudents" hover-class="action-item-hover">
          <view class="action-icon">👨‍🎓</view>
          <text class="action-label">学生管理</text>
        </view>
        <view class="action-item" @click="manageWarnings" hover-class="action-item-hover">
          <view class="action-icon">⚠️</view>
          <text class="action-label">预警管理</text>
        </view>
        <view class="action-item" @click="manageAssistance" hover-class="action-item-hover">
          <view class="action-icon">🤝</view>
          <text class="action-label">帮扶计划</text>
        </view>
        <view class="action-item" @click="viewReports" hover-class="action-item-hover">
          <view class="action-icon">📊</view>
          <text class="action-label">统计报表</text>
        </view>
      </view>
    </view>

    <view class="recent-tasks">
      <view class="section-header">
        <text class="section-title">待办任务</text>
        <text class="more-link" @click="viewAllTasks">查看全部</text>
      </view>
      <view v-if="tasks.length > 0" class="task-list">
        <view v-for="task in tasks" :key="task.id" class="task-item" hover-class="task-item-hover">
          <view class="task-icon" :class="task.type">{{ getTaskIcon(task.type) }}</view>
          <view class="task-content">
            <text class="task-title">{{ task.title }}</text>
            <text class="task-desc">{{ task.description }}</text>
            <text class="task-deadline">截止: {{ task.deadline }}</text>
          </view>
          <view class="task-priority" :class="task.priority">{{ getPriorityText(task.priority) }}</view>
        </view>
      </view>
      <view v-else class="empty-state">
        <view class="empty-icon">📋</view>
        <text>暂无待办任务</text>
      </view>
    </view>
  </view>
</template>

<script setup>
import { ref, onMounted } from 'vue';

const counselorName = ref('李老师');
const stats = ref({
  totalStudents: 180,
  totalWarnings: 25,
  assistancePlans: 12,
  totalClasses: 4
});

const tasks = ref([
  {
    id: 1,
    type: 'warning',
    title: '处理学生预警',
    description: '张三同学成绩预警需要及时处理',
    deadline: '2026-03-31',
    priority: 'high'
  },
  {
    id: 2,
    type: 'assistance',
    title: '跟进帮扶计划',
    description: '李四同学的英语学习计划进度跟进',
    deadline: '2026-04-02',
    priority: 'medium'
  },
  {
    id: 3,
    type: 'meeting',
    title: '家长会准备',
    description: '准备期中家长会材料和PPT',
    deadline: '2026-04-05',
    priority: 'low'
  }
]);

const getTaskIcon = (type) => {
  const icons = { warning: '⚠️', assistance: '🤝', meeting: '📅' };
  return icons[type] || '📋';
};

const getPriorityText = (priority) => {
  const texts = { high: '紧急', medium: '重要', low: '普通' };
  return texts[priority] || '普通';
};

onMounted(() => {
  loadDashboardData();
});

const loadDashboardData = async () => {
  try {
    console.log('Counselor dashboard data loaded');
  } catch (error) {
    console.error('获取辅导员仪表板数据失败:', error);
  }
};

const manageStudents = () => {
  uni.navigateTo({ url: '/pages/counselor/students' });
};

const manageWarnings = () => {
  uni.navigateTo({ url: '/pages/counselor/warnings' });
};

const manageAssistance = () => {
  uni.navigateTo({ url: '/pages/counselor/assistance' });
};

const viewReports = () => {
  uni.showToast({ title: '查看统计报表', icon: 'none' });
};

const viewAllTasks = () => {
  uni.showToast({ title: '查看全部任务', icon: 'none' });
};
</script>

<style scoped>
.counselor-dashboard {
  padding: 20rpx;
  background-color: #f5f7fa;
  min-height: 100vh;
}

.header {
  background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%);
  border-radius: 24rpx;
  padding: 32rpx;
  margin-bottom: 24rpx;
  color: white;
  display: flex;
  justify-content: space-between;
  align-items: center;
  box-shadow: 0 4rpx 16rpx rgba(79, 172, 254, 0.3);
}

.user-info {
  flex: 1;
}

.welcome {
  font-size: 28rpx;
  font-weight: 700;
  display: block;
  margin-bottom: 8rpx;
}

.role {
  font-size: 16rpx;
  opacity: 0.9;
}

.avatar {
  width: 80rpx;
  height: 80rpx;
  border-radius: 50%;
  background-color: rgba(255, 255, 255, 0.2);
  display: flex;
  align-items: center;
  justify-content: center;
  backdrop-filter: blur(10rpx);
}

.avatar-text {
  font-size: 32rpx;
  font-weight: 700;
}

.stats-container {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 20rpx;
  margin-bottom: 24rpx;
}

.stat-card {
  background: white;
  border-radius: 20rpx;
  padding: 24rpx;
  box-shadow: 0 2rpx 12rpx rgba(0, 0, 0, 0.08);
  text-align: center;
  transition: all 0.3s ease;
  border: 1rpx solid #f0f0f0;
}

.stat-card-hover {
  transform: translateY(-4rpx);
  box-shadow: 0 6rpx 20rpx rgba(0, 0, 0, 0.12);
}

.stat-icon {
  font-size: 40rpx;
  margin-bottom: 12rpx;
  display: block;
}

.stat-icon.students {
  color: #4facfe;
}

.stat-icon.warnings {
  color: #ff5252;
}

.stat-icon.assistance {
  color: #4caf50;
}

.stat-icon.classes {
  color: #ff9800;
}

.stat-value {
  font-size: 28rpx;
  font-weight: 700;
  color: #333;
  display: block;
  margin-bottom: 8rpx;
}

.stat-label {
  font-size: 16rpx;
  color: #666;
  display: block;
}

.quick-actions {
  background: white;
  border-radius: 20rpx;
  padding: 24rpx;
  box-shadow: 0 2rpx 12rpx rgba(0, 0, 0, 0.08);
  border: 1rpx solid #f0f0f0;
  margin-bottom: 24rpx;
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20rpx;
}

.section-title {
  font-size: 20rpx;
  font-weight: 600;
  color: #333;
}

.more-link {
  font-size: 16rpx;
  color: #4facfe;
}

.action-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 20rpx;
}

.action-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 24rpx;
  background-color: #f9f9f9;
  border-radius: 16rpx;
  transition: all 0.3s ease;
}

.action-item-hover {
  background-color: #f0f7ff;
  transform: translateY(-4rpx);
  box-shadow: 0 4rpx 12rpx rgba(0, 0, 0, 0.1);
}

.action-icon {
  font-size: 40rpx;
  margin-bottom: 12rpx;
}

.action-label {
  font-size: 14rpx;
  color: #333;
  text-align: center;
  font-weight: 500;
}

.recent-tasks {
  background: white;
  border-radius: 20rpx;
  padding: 24rpx;
  box-shadow: 0 2rpx 12rpx rgba(0, 0, 0, 0.08);
  border: 1rpx solid #f0f0f0;
}

.task-list {
  display: flex;
  flex-direction: column;
  gap: 16rpx;
}

.task-item {
  display: flex;
  align-items: flex-start;
  padding: 20rpx;
  background-color: #f9f9f9;
  border-radius: 16rpx;
  transition: all 0.3s ease;
}

.task-item-hover {
  background-color: #f0f7ff;
  border: 1rpx solid #4facfe;
}

.task-icon {
  width: 60rpx;
  height: 60rpx;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-right: 20rpx;
  flex-shrink: 0;
  font-size: 28rpx;
}

.task-icon.warning {
  background-color: #ffebee;
}

.task-icon.assistance {
  background-color: #e8f5e9;
}

.task-icon.meeting {
  background-color: #e3f2fd;
}

.task-content {
  flex: 1;
  margin-right: 16rpx;
}

.task-title {
  font-size: 18rpx;
  font-weight: 600;
  color: #333;
  display: block;
  margin-bottom: 4rpx;
}

.task-desc {
  font-size: 14rpx;
  color: #666;
  display: block;
  margin-bottom: 4rpx;
}

.task-deadline {
  font-size: 12rpx;
  color: #999;
}

.task-priority {
  padding: 6rpx 16rpx;
  border-radius: 16rpx;
  font-size: 14rpx;
  font-weight: 600;
  white-space: nowrap;
}

.task-priority.high {
  background-color: #ffebee;
  color: #d32f2f;
}

.task-priority.medium {
  background-color: #fff8e1;
  color: #f57c00;
}

.task-priority.low {
  background-color: #e3f2fd;
  color: #1976d2;
}

.empty-state {
  text-align: center;
  padding: 40rpx 0;
  color: #999;
  font-size: 16rpx;
}

.empty-icon {
  font-size: 60rpx;
  margin-bottom: 16rpx;
  display: block;
}

/* 动画效果 */
@keyframes fadeIn {
  from {
    opacity: 0;
    transform: translateY(20rpx);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.counselor-dashboard > * {
  animation: fadeIn 0.5s ease-out forwards;
}

.counselor-dashboard > *:nth-child(1) {
  animation-delay: 0.1s;
}

.counselor-dashboard > *:nth-child(2) {
  animation-delay: 0.2s;
}

.counselor-dashboard > *:nth-child(3) {
  animation-delay: 0.3s;
}

.counselor-dashboard > *:nth-child(4) {
  animation-delay: 0.4s;
}
</style>