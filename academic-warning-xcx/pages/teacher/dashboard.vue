<template>
  <view class="teacher-dashboard">
    <view class="header">
      <view class="user-info">
        <text class="welcome">欢迎，{{ teacherName }}</text>
        <text class="role">教师</text>
      </view>
      <view class="avatar">
        <text class="avatar-text">师</text>
      </view>
    </view>

    <view class="stats-container">
      <view class="stat-card" hover-class="stat-card-hover">
        <view class="stat-icon courses">📚</view>
        <text class="stat-value">{{ stats.totalCourses }}</text>
        <text class="stat-label">授课课程</text>
      </view>
      <view class="stat-card" hover-class="stat-card-hover">
        <view class="stat-icon students">👨‍🎓</view>
        <text class="stat-value">{{ stats.totalStudents }}</text>
        <text class="stat-label">学生人数</text>
      </view>
      <view class="stat-card" hover-class="stat-card-hover">
        <view class="stat-icon warnings">⚠️</view>
        <text class="stat-value">{{ stats.totalWarnings }}</text>
        <text class="stat-label">预警学生</text>
      </view>
      <view class="stat-card" hover-class="stat-card-hover">
        <view class="stat-icon avg-score">📊</view>
        <text class="stat-value">{{ stats.averageScore }}</text>
        <text class="stat-label">平均成绩</text>
      </view>
    </view>

    <view class="quick-actions">
      <view class="section-header">
        <text class="section-title">教学管理</text>
      </view>
      <view class="action-grid">
        <view class="action-item" @click="manageScores" hover-class="action-item-hover">
          <view class="action-icon score-icon">📊</view>
          <text class="action-label">成绩管理</text>
        </view>
        <view class="action-item" @click="manageStudents" hover-class="action-item-hover">
          <view class="action-icon student-icon">👨‍🎓</view>
          <text class="action-label">学生管理</text>
        </view>
        <view class="action-item" @click="manageCourses" hover-class="action-item-hover">
          <view class="action-icon course-icon">📚</view>
          <text class="action-label">课程管理</text>
        </view>
        <view class="action-item" @click="viewWarnings" hover-class="action-item-hover">
          <view class="action-icon warning-icon">⚠️</view>
          <text class="action-label">预警管理</text>
        </view>
      </view>
    </view>

    <view class="recent-warnings">
      <view class="section-header">
        <text class="section-title">最近预警</text>
        <text class="more-link" @click="viewAllWarnings">查看全部</text>
      </view>
      <view v-if="recentWarnings.length > 0" class="warning-list">
        <view v-for="warning in recentWarnings" :key="warning.id" class="warning-item" hover-class="warning-item-hover">
          <view class="warning-icon" :class="warning.level">{{ getWarningIcon(warning.level) }}</view>
          <view class="warning-content">
            <text class="warning-title">{{ warning.studentName }}</text>
            <text class="warning-desc">{{ warning.description }}</text>
            <text class="warning-date">{{ warning.date }}</text>
          </view>
          <view class="warning-level" :class="warning.level">{{ getLevelText(warning.level) }}</view>
        </view>
      </view>
      <view v-else class="empty-state">
        <view class="empty-icon">📭</view>
        <text>暂无预警记录</text>
      </view>
    </view>
  </view>
</template>

<script setup>
import { ref, onMounted } from 'vue';

const teacherName = ref('王老师');
const stats = ref({
  totalCourses: 4,
  totalStudents: 170,
  totalWarnings: 12,
  averageScore: 82.5
});

const recentWarnings = ref([
  {
    id: 1,
    studentName: '张三',
    description: '高等数学成绩低于60分',
    level: 'medium',
    date: '2026-03-29'
  },
  {
    id: 2,
    studentName: '李四',
    description: '缺课次数超过3次',
    level: 'low',
    date: '2026-03-28'
  },
  {
    id: 3,
    studentName: '王五',
    description: '作业提交率低于50%',
    level: 'high',
    date: '2026-03-27'
  }
]);

const getWarningIcon = (level) => {
  const icons = { high: '🚨', medium: '⚠️', low: 'ℹ️' };
  return icons[level] || '📌';
};

const getLevelText = (level) => {
  const texts = { high: '严重', medium: '中等', low: '轻微' };
  return texts[level] || '未知';
};

onMounted(() => {
  loadDashboardData();
});

const loadDashboardData = async () => {
  try {
    console.log('Teacher dashboard data loaded');
  } catch (error) {
    console.error('获取教师仪表板数据失败:', error);
  }
};

const manageScores = () => {
  uni.navigateTo({ url: '/pages/teacher/scores' });
};

const manageStudents = () => {
  uni.navigateTo({ url: '/pages/teacher/students' });
};

const manageCourses = () => {
  uni.navigateTo({ url: '/pages/teacher/courses' });
};

const viewWarnings = () => {
  uni.navigateTo({ url: '/pages/teacher/warnings' });
};

const viewAllWarnings = () => {
  uni.navigateTo({ url: '/pages/teacher/warnings' });
};
</script>

<style scoped>
.teacher-dashboard {
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

.stat-icon.courses {
  color: #4facfe;
}

.stat-icon.students {
  color: #4caf50;
}

.stat-icon.warnings {
  color: #ff5252;
}

.stat-icon.avg-score {
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

.recent-warnings {
  background: white;
  border-radius: 20rpx;
  padding: 24rpx;
  box-shadow: 0 2rpx 12rpx rgba(0, 0, 0, 0.08);
  border: 1rpx solid #f0f0f0;
}

.warning-list {
  display: flex;
  flex-direction: column;
  gap: 16rpx;
}

.warning-item {
  display: flex;
  align-items: flex-start;
  padding: 20rpx;
  background-color: #f9f9f9;
  border-radius: 16rpx;
  transition: all 0.3s ease;
}

.warning-item-hover {
  background-color: #f0f7ff;
  border: 1rpx solid #4facfe;
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
  font-size: 28rpx;
}

.warning-icon.high {
  background-color: #ffebee;
  color: #d32f2f;
}

.warning-icon.medium {
  background-color: #fff8e1;
  color: #f57c00;
}

.warning-icon.low {
  background-color: #e3f2fd;
  color: #1976d2;
}

.warning-content {
  flex: 1;
  margin-right: 16rpx;
}

.warning-title {
  font-size: 18rpx;
  font-weight: 600;
  color: #333;
  display: block;
  margin-bottom: 4rpx;
}

.warning-desc {
  font-size: 14rpx;
  color: #666;
  display: block;
  margin-bottom: 4rpx;
}

.warning-date {
  font-size: 12rpx;
  color: #999;
}

.warning-level {
  padding: 6rpx 16rpx;
  border-radius: 16rpx;
  font-size: 14rpx;
  font-weight: 600;
  white-space: nowrap;
}

.warning-level.high {
  background-color: #ffebee;
  color: #d32f2f;
}

.warning-level.medium {
  background-color: #fff8e1;
  color: #f57c00;
}

.warning-level.low {
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

.teacher-dashboard > * {
  animation: fadeIn 0.5s ease-out forwards;
}

.teacher-dashboard > *:nth-child(1) {
  animation-delay: 0.1s;
}

.teacher-dashboard > *:nth-child(2) {
  animation-delay: 0.2s;
}

.teacher-dashboard > *:nth-child(3) {
  animation-delay: 0.3s;
}

.teacher-dashboard > *:nth-child(4) {
  animation-delay: 0.4s;
}
</style>