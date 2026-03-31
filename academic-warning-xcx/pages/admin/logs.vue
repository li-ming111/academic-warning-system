<template>
  <view class="logs-container">
    <view class="header">
      <text class="title">系统日志</text>
      <text class="subtitle">查看系统操作日志</text>
    </view>

    <view class="filter-section">
      <picker :range="logTypeOptions" :value="selectedTypeIndex" @change="handleTypeChange" class="filter-picker">
        <view class="picker-label">
          <text class="picker-text">{{ selectedType }}</text>
          <text class="picker-arrow">▼</text>
        </view>
      </picker>
    </view>

    <view class="logs-list">
      <view v-for="log in filteredLogs" :key="log.id" class="log-item" hover-class="log-item-hover">
        <view class="log-icon" :class="log.type">{{ getLogIcon(log.type) }}</view>
        <view class="log-content">
          <view class="log-header">
            <text class="log-title">{{ log.title }}</text>
            <text class="log-time">{{ log.time }}</text>
          </view>
          <text class="log-message">{{ log.message }}</text>
          <text class="log-user">操作人: {{ log.user }}</text>
        </view>
      </view>
    </view>
  </view>
</template>

<script setup>
import { ref, computed } from 'vue';

const selectedType = ref('全部类型');
const selectedTypeIndex = ref(0);
const logTypeOptions = ['全部类型', '登录', '操作', '错误', '系统'];

const logs = ref([
  { id: 1, type: 'login', title: '用户登录', message: '管理员 admin 登录系统', user: 'admin', time: '2026-03-30 10:30:00' },
  { id: 2, type: 'operation', title: '用户管理', message: '修改用户张三的信息', user: 'admin', time: '2026-03-30 10:25:00' },
  { id: 3, type: 'error', title: '系统错误', message: '数据库连接超时', user: 'system', time: '2026-03-30 10:20:00' },
  { id: 4, type: 'system', title: '系统启动', message: '系统服务正常启动', user: 'system', time: '2026-03-30 08:00:00' },
  { id: 5, type: 'operation', title: '数据备份', message: '完成每日数据备份', user: 'admin', time: '2026-03-30 02:00:00' },
  { id: 6, type: 'login', title: '用户登录', message: '教师 王老师 登录系统', user: '王老师', time: '2026-03-29 16:30:00' },
  { id: 7, type: 'error', title: '预警生成', message: '批量生成学生预警信息失败', user: 'system', time: '2026-03-29 15:45:00' },
  { id: 8, type: 'operation', title: '角色管理', message: '修改辅导员角色权限', user: 'admin', time: '2026-03-29 14:20:00' }
]);

const filteredLogs = computed(() => {
  if (selectedType.value === '全部类型') return logs.value;
  const typeMap = { '登录': 'login', '操作': 'operation', '错误': 'error', '系统': 'system' };
  return logs.value.filter(log => log.type === typeMap[selectedType.value]);
});

const handleTypeChange = (e) => {
  selectedTypeIndex.value = e.detail.value;
  selectedType.value = logTypeOptions[e.detail.value];
};

const getLogIcon = (type) => {
  const iconMap = { login: '🔑', operation: '⚙️', error: '❌', system: '⚡' };
  return iconMap[type] || '📝';
};
</script>

<style scoped>
.logs-container { padding: 20rpx; background-color: #f5f7fa; min-height: 100vh; }
.header { margin-bottom: 24rpx; }
.title { font-size: 32rpx; font-weight: 700; color: #333; margin-bottom: 8rpx; display: block; }
.subtitle { font-size: 18rpx; color: #666; display: block; }
.filter-section { background: white; border-radius: 20rpx; padding: 24rpx; margin-bottom: 24rpx; box-shadow: 0 2rpx 12rpx rgba(0,0,0,0.08); border: 1rpx solid #f0f0f0; }
.filter-picker { width: 100%; }
.picker-label { display: flex; justify-content: space-between; align-items: center; padding: 20rpx 24rpx; background-color: #f9f9f9; border: 1rpx solid #e0e0e0; border-radius: 12rpx; }
.picker-text { font-size: 16rpx; color: #333; }
.picker-arrow { font-size: 14rpx; color: #999; }
.logs-list { display: flex; flex-direction: column; gap: 16rpx; }
.log-item { background: white; border-radius: 20rpx; padding: 24rpx; box-shadow: 0 2rpx 12rpx rgba(0,0,0,0.08); border: 1rpx solid #f0f0f0; display: flex; align-items: flex-start; transition: all 0.3s ease; }
.log-item-hover { transform: translateY(-4rpx); box-shadow: 0 6rpx 20rpx rgba(0,0,0,0.12); border-color: #4facfe; }
.log-icon { width: 60rpx; height: 60rpx; border-radius: 50%; display: flex; align-items: center; justify-content: center; margin-right: 20rpx; flex-shrink: 0; font-size: 28rpx; }
.log-icon.login { background-color: #e3f2fd; }
.log-icon.operation { background-color: #e8f5e9; }
.log-icon.error { background-color: #ffebee; }
.log-icon.system { background-color: #fff8e1; }
.log-content { flex: 1; }
.log-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 8rpx; }
.log-title { font-size: 18rpx; font-weight: 600; color: #333; }
.log-time { font-size: 14rpx; color: #999; }
.log-message { font-size: 16rpx; color: #666; display: block; margin-bottom: 8rpx; }
.log-user { font-size: 14rpx; color: #4facfe; }
</style>