<template>
  <view class="admin-dashboard">
    <view class="header">
      <view class="user-info">
        <text class="title">管理员首页</text>
        <text class="subtitle">欢迎回来，{{ adminName }}</text>
      </view>
      <view class="avatar">
        <text class="avatar-text">管</text>
      </view>
    </view>

    <view class="stats-container">
      <view class="stat-card" hover-class="stat-card-hover">
        <view class="stat-icon students">👨‍🎓</view>
        <text class="stat-number">{{ stats.totalStudents }}</text>
        <text class="stat-label">总学生数</text>
      </view>
      <view class="stat-card" hover-class="stat-card-hover">
        <view class="stat-icon teachers">👨‍🏫</view>
        <text class="stat-number">{{ stats.totalTeachers }}</text>
        <text class="stat-label">教师数</text>
      </view>
      <view class="stat-card" hover-class="stat-card-hover">
        <view class="stat-icon counselors">👨‍💼</view>
        <text class="stat-number">{{ stats.totalCounselors }}</text>
        <text class="stat-label">辅导员数</text>
      </view>
      <view class="stat-card" hover-class="stat-card-hover">
        <view class="stat-icon warnings">⚠️</view>
        <text class="stat-number">{{ stats.totalWarnings }}</text>
        <text class="stat-label">预警总数</text>
      </view>
    </view>

    <view class="system-stats">
      <view class="section-header">
        <text class="section-title">系统统计</text>
      </view>
      <view class="stats-grid">
        <view class="stat-item" hover-class="stat-item-hover">
          <view class="stat-icon-bg assistance">🤝</view>
          <text class="stat-value">{{ stats.assistancePlans }}</text>
          <text class="stat-desc">帮扶计划数</text>
        </view>
        <view class="stat-item" hover-class="stat-item-hover">
          <view class="stat-icon-bg active">🔥</view>
          <text class="stat-value">{{ stats.activeWarnings }}</text>
          <text class="stat-desc">活跃预警数</text>
        </view>
        <view class="stat-item" hover-class="stat-item-hover">
          <view class="stat-icon-bg completed">✅</view>
          <text class="stat-value">{{ stats.completedPlans }}</text>
          <text class="stat-desc">已完成计划</text>
        </view>
        <view class="stat-item" hover-class="stat-item-hover">
          <view class="stat-icon-bg users">👥</view>
          <text class="stat-value">{{ stats.systemUsers }}</text>
          <text class="stat-desc">系统用户数</text>
        </view>
      </view>
    </view>

    <view class="quick-actions">
      <view class="section-header">
        <text class="section-title">系统管理</text>
      </view>
      <view class="action-grid">
        <view class="action-item" @click="manageUsers" hover-class="action-item-hover">
          <view class="action-icon user-icon">👥</view>
          <text class="action-label">用户管理</text>
        </view>
        <view class="action-item" @click="manageRoles" hover-class="action-item-hover">
          <view class="action-icon role-icon">🔒</view>
          <text class="action-label">角色管理</text>
        </view>
        <view class="action-item" @click="manageDepartments" hover-class="action-item-hover">
          <view class="action-icon dept-icon">🏢</view>
          <text class="action-label">部门管理</text>
        </view>
        <view class="action-item" @click="systemSettings" hover-class="action-item-hover">
          <view class="action-icon settings-icon">⚙️</view>
          <text class="action-label">系统设置</text>
        </view>
        <view class="action-item" @click="viewReports" hover-class="action-item-hover">
          <view class="action-icon report-icon">📊</view>
          <text class="action-label">报表统计</text>
        </view>
        <view class="action-item" @click="systemLogs" hover-class="action-item-hover">
          <view class="action-icon log-icon">📋</view>
          <text class="action-label">系统日志</text>
        </view>
      </view>
    </view>
  </view>
</template>

<script setup>
import { ref, onMounted } from 'vue';

const adminName = ref('系统管理员');
const stats = ref({
  totalStudents: 2000,
  totalTeachers: 150,
  totalCounselors: 30,
  totalWarnings: 500,
  assistancePlans: 200,
  activeWarnings: 150,
  completedPlans: 180,
  systemUsers: 1850
});

onMounted(() => {
  loadDashboardData();
});

const loadDashboardData = async () => {
  try {
    console.log('Admin dashboard data loaded');
  } catch (error) {
    console.error('获取管理员仪表板数据失败:', error);
  }
};

const manageUsers = () => {
  uni.navigateTo({ url: '/pages/admin/users' });
};

const manageRoles = () => {
  uni.navigateTo({ url: '/pages/admin/roles' });
};

const manageDepartments = () => {
  uni.navigateTo({ url: '/pages/admin/departments' });
};

const systemSettings = () => {
  uni.navigateTo({ url: '/pages/admin/settings' });
};

const viewReports = () => {
  uni.navigateTo({ url: '/pages/admin/reports' });
};

const systemLogs = () => {
  uni.navigateTo({ url: '/pages/admin/logs' });
};
</script>

<style scoped>
.admin-dashboard {
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

.title {
  font-size: 28rpx;
  font-weight: 700;
  display: block;
  margin-bottom: 8rpx;
}

.subtitle {
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

.stat-icon.teachers {
  color: #4caf50;
}

.stat-icon.counselors {
  color: #ff9800;
}

.stat-icon.warnings {
  color: #ff5252;
}

.stat-number {
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

.system-stats {
  background: white;
  border-radius: 20rpx;
  padding: 24rpx;
  box-shadow: 0 2rpx 12rpx rgba(0, 0, 0, 0.08);
  border: 1rpx solid #f0f0f0;
  margin-bottom: 24rpx;
}

.section-header {
  margin-bottom: 20rpx;
}

.section-title {
  font-size: 20rpx;
  font-weight: 600;
  color: #333;
  display: block;
}

.stats-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 20rpx;
}

.stat-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 24rpx;
  background-color: #f9f9f9;
  border-radius: 16rpx;
  transition: all 0.3s ease;
}

.stat-item-hover {
  background-color: #f0f7ff;
  transform: translateY(-2rpx);
}

.stat-icon-bg {
  width: 60rpx;
  height: 60rpx;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 28rpx;
  margin-bottom: 12rpx;
}

.stat-icon-bg.assistance {
  background-color: #e8f5e9;
  color: #4caf50;
}

.stat-icon-bg.active {
  background-color: #fff3e0;
  color: #ff9800;
}

.stat-icon-bg.completed {
  background-color: #e3f2fd;
  color: #1976d2;
}

.stat-icon-bg.users {
  background-color: #f3e5f5;
  color: #7b1fa2;
}

.stat-value {
  font-size: 24rpx;
  font-weight: 700;
  color: #333;
  margin-bottom: 4rpx;
}

.stat-desc {
  font-size: 14rpx;
  color: #666;
  text-align: center;
}

.quick-actions {
  background: white;
  border-radius: 20rpx;
  padding: 24rpx;
  box-shadow: 0 2rpx 12rpx rgba(0, 0, 0, 0.08);
  border: 1rpx solid #f0f0f0;
}

.action-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
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

.admin-dashboard > * {
  animation: fadeIn 0.5s ease-out forwards;
}

.admin-dashboard > *:nth-child(1) {
  animation-delay: 0.1s;
}

.admin-dashboard > *:nth-child(2) {
  animation-delay: 0.2s;
}

.admin-dashboard > *:nth-child(3) {
  animation-delay: 0.3s;
}

.admin-dashboard > *:nth-child(4) {
  animation-delay: 0.4s;
}
</style>