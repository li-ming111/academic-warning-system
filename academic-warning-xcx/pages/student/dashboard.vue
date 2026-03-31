<template>
  <view class="dashboard-page">
    <view class="header">
      <view class="user-info">
        <text class="welcome">欢迎，{{ userName }}</text>
        <text class="role">{{ roleText }}</text>
      </view>
      <view class="avatar">
        <text class="avatar-text">{{ userName.charAt(0) }}</text>
      </view>
    </view>

    <view class="stats-container">
      <view class="stat-card" hover-class="stat-card-hover">
        <view class="stat-icon total-credits">📚</view>
        <text class="stat-title">总学分</text>
        <text class="stat-value">{{ dashboardData.totalCredits || 0 }}</text>
      </view>
      <view class="stat-card" hover-class="stat-card-hover">
        <view class="stat-icon completed-credits">✅</view>
        <text class="stat-title">已修学分</text>
        <text class="stat-value">{{ dashboardData.completedCredits || 0 }}</text>
      </view>
      <view class="stat-card" hover-class="stat-card-hover">
        <view class="stat-icon warning-count">⚠️</view>
        <text class="stat-title">预警数量</text>
        <text class="stat-value warning">{{ dashboardData.warningCount || 0 }}</text>
      </view>
      <view class="stat-card" hover-class="stat-card-hover">
        <view class="stat-icon average-gpa">📈</view>
        <text class="stat-title">平均绩点</text>
        <text class="stat-value">{{ dashboardData.averageGPA || 0 }}</text>
      </view>
    </view>

    <view class="recent-warnings">
      <view class="section-header">
        <text class="section-title">最近预警</text>
        <navigator url="/pages/student/warnings" class="more-link">查看全部</navigator>
      </view>
      <view v-if="dashboardData.recentWarnings && dashboardData.recentWarnings.length > 0" class="warning-list">
        <view v-for="warning in dashboardData.recentWarnings" :key="warning.id" class="warning-item" hover-class="warning-item-hover">
          <view class="warning-content">
            <text class="warning-title">{{ warning.title }}</text>
            <text class="warning-description">{{ warning.description }}</text>
            <text class="warning-date">{{ warning.date }}</text>
          </view>
          <view :class="['warning-level', warning.level]">
            {{ warning.level === 'red' ? '严重' : warning.level === 'yellow' ? '中等' : '轻微' }}
          </view>
        </view>
      </view>
      <view v-else class="empty-state">
        <view class="empty-icon">📭</view>
        <text>暂无预警记录</text>
      </view>
    </view>

    <view class="quick-actions">
      <view class="action-card" @click="navigateToScores" hover-class="action-card-hover">
        <view class="action-icon scores">📊</view>
        <text class="action-text">成绩查询</text>
      </view>
      <view class="action-card" @click="navigateToWarnings" hover-class="action-card-hover">
        <view class="action-icon warnings">⚠️</view>
        <text class="action-text">预警管理</text>
      </view>
      <view class="action-card" @click="navigateToAssistance" hover-class="action-card-hover">
        <view class="action-icon assistance">🤝</view>
        <text class="action-text">帮扶计划</text>
      </view>
      <view class="action-card" @click="handleLogout" hover-class="action-card-hover">
        <view class="action-icon logout">🚪</view>
        <text class="action-text">退出登录</text>
      </view>
    </view>
  </view>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import { apiClient } from '../../services/api'

const dashboardData = ref({
  totalCredits: 0,
  completedCredits: 0,
  warningCount: 0,
  averageGPA: 0,
  recentWarnings: []
})

const userName = computed(() => {
  return uni.getStorageSync('userName') || '学生'
})

const roleText = computed(() => {
  const role = uni.getStorageSync('role')
  switch (role) {
    case '1':
    case 'student':
      return '学生'
    case '2':
    case 'teacher':
      return '教师'
    case '4':
    case 'counselor':
      return '辅导员'
    case '3':
    case 'admin':
      return '管理员'
    default:
      return '用户'
  }
})

const loadDashboardData = async () => {
  try {
    const response = await apiClient.getStudentDashboard()
    if (response && response.data) {
      dashboardData.value = response.data
    }
  } catch (error) {
    console.error('获取仪表板数据失败:', error)
    // 使用模拟数据
    dashboardData.value = {
      totalCredits: 140,
      completedCredits: 85,
      warningCount: 2,
      averageGPA: 3.2,
      recentWarnings: [
        {
          id: 1,
          title: '高数挂科预警',
          description: '高等数学成绩低于60分',
          date: '2026-03-20',
          level: 'red'
        },
        {
          id: 2,
          title: '英语预警',
          description: '大学英语成绩低于70分',
          date: '2026-03-15',
          level: 'yellow'
        }
      ]
    }
  }
}

const navigateToScores = () => {
  uni.navigateTo({ url: '/pages/student/scores' })
}

const navigateToWarnings = () => {
  uni.navigateTo({ url: '/pages/student/warnings' })
}

const navigateToAssistance = () => {
  uni.navigateTo({ url: '/pages/student/assistance' })
}

const handleLogout = async () => {
  try {
    const userId = uni.getStorageSync('userId')
    const role = uni.getStorageSync('role')
    if (userId && role) {
      await apiClient.logout(userId, role)
    }
  } catch (error) {
    console.error('登出失败:', error)
  } finally {
    // 清除本地存储
    uni.removeStorageSync('token')
    uni.removeStorageSync('userId')
    uni.removeStorageSync('username')
    uni.removeStorageSync('role')
    uni.removeStorageSync('userName')
    uni.removeStorageSync('studentId')
    uni.removeStorageSync('teacherId')
    uni.removeStorageSync('counselorId')
    uni.removeStorageSync('adminId')
    
    // 跳转到登录页
    uni.navigateTo({ url: '/pages/auth/login' })
  }
}

onMounted(() => {
  loadDashboardData()
})
</script>

<style scoped>
.dashboard-page {
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

.total-credits {
  color: #4facfe;
}

.completed-credits {
  color: #4caf50;
}

.warning-count {
  color: #ff5252;
}

.average-gpa {
  color: #ff9800;
}

.stat-title {
  font-size: 16rpx;
  color: #666;
  margin-bottom: 8rpx;
  display: block;
}

.stat-value {
  font-size: 28rpx;
  font-weight: 700;
  color: #333;
}

.stat-value.warning {
  color: #ff5252;
}

.recent-warnings {
  background: white;
  border-radius: 20rpx;
  padding: 24rpx;
  margin-bottom: 24rpx;
  box-shadow: 0 2rpx 12rpx rgba(0, 0, 0, 0.08);
  border: 1rpx solid #f0f0f0;
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
  text-decoration: none;
  display: flex;
  align-items: center;
}

.more-link::after {
  content: "→";
  margin-left: 4rpx;
  font-size: 14rpx;
}

.warning-list {
  display: flex;
  flex-direction: column;
  gap: 16rpx;
}

.warning-item {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  padding: 20rpx;
  border: 1rpx solid #f0f0f0;
  border-radius: 16rpx;
  background-color: #f9f9f9;
  transition: all 0.3s ease;
}

.warning-item-hover {
  background-color: #f0f7ff;
  border-color: #4facfe;
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
  margin-bottom: 6rpx;
}

.warning-description {
  font-size: 16rpx;
  color: #666;
  display: block;
  margin-bottom: 6rpx;
  line-height: 24rpx;
}

.warning-date {
  font-size: 14rpx;
  color: #999;
}

.warning-level {
  padding: 6rpx 16rpx;
  border-radius: 16rpx;
  font-size: 14rpx;
  font-weight: 600;
  white-space: nowrap;
}

.warning-level.red {
  background-color: #ffebee;
  color: #d32f2f;
}

.warning-level.yellow {
  background-color: #fff8e1;
  color: #f57c00;
}

.warning-level.blue {
  background-color: #e3f2fd;
  color: #1976d2;
}

.empty-state {
  text-align: center;
  padding: 60rpx 0;
  color: #999;
  font-size: 16rpx;
}

.empty-icon {
  font-size: 60rpx;
  margin-bottom: 16rpx;
  display: block;
}

.quick-actions {
  display: grid;
  grid-template-columns: 1fr 1fr 1fr 1fr;
  gap: 20rpx;
}

.action-card {
  background: white;
  border-radius: 20rpx;
  padding: 24rpx;
  box-shadow: 0 2rpx 12rpx rgba(0, 0, 0, 0.08);
  text-align: center;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  height: 140rpx;
  transition: all 0.3s ease;
  border: 1rpx solid #f0f0f0;
}

.action-card-hover {
  transform: translateY(-4rpx);
  box-shadow: 0 6rpx 20rpx rgba(0, 0, 0, 0.12);
}

.action-icon {
  font-size: 40rpx;
  margin-bottom: 12rpx;
  display: block;
}

.action-icon.scores {
  color: #4facfe;
}

.action-icon.warnings {
  color: #ff5252;
}

.action-icon.assistance {
  color: #4caf50;
}

.action-icon.logout {
  color: #999;
}

.action-text {
  font-size: 16rpx;
  color: #333;
  font-weight: 500;
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

.dashboard-page > * {
  animation: fadeIn 0.5s ease-out forwards;
}

.dashboard-page > *:nth-child(1) {
  animation-delay: 0.1s;
}

.dashboard-page > *:nth-child(2) {
  animation-delay: 0.2s;
}

.dashboard-page > *:nth-child(3) {
  animation-delay: 0.3s;
}

.dashboard-page > *:nth-child(4) {
  animation-delay: 0.4s;
}
</style>