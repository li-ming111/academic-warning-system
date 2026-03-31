<template>
  <view class="roles-container">
    <view class="header">
      <text class="title">角色管理</text>
      <text class="subtitle">管理系统角色和权限</text>
    </view>

    <view class="roles-list">
      <view v-for="role in roles" :key="role.id" class="role-item" hover-class="role-item-hover">
        <view class="role-icon">{{ role.icon }}</view>
        <view class="role-info">
          <text class="role-name">{{ role.name }}</text>
          <text class="role-description">{{ role.description }}</text>
          <view class="role-stats">
            <text class="stat">用户: {{ role.userCount }}人</text>
            <text class="stat">权限: {{ role.permissionCount }}项</text>
          </view>
        </view>
        <view class="role-actions">
          <button class="action-btn" @click="editRole(role)">编辑权限</button>
        </view>
      </view>
    </view>
  </view>
</template>

<script setup>
import { ref } from 'vue';

const roles = ref([
  { id: 1, name: '学生', icon: '👨‍🎓', description: '学生用户，可查看成绩、预警和帮扶计划', userCount: 2000, permissionCount: 8 },
  { id: 2, name: '教师', icon: '👨‍🏫', description: '教师用户，可管理学生成绩和预警', userCount: 150, permissionCount: 15 },
  { id: 3, name: '辅导员', icon: '👨‍💼', description: '辅导员用户，可管理学生预警和帮扶', userCount: 30, permissionCount: 20 },
  { id: 4, name: '管理员', icon: '👨‍💻', description: '系统管理员，拥有所有权限', userCount: 5, permissionCount: 50 }
]);

const editRole = (role) => {
  uni.showToast({ title: `编辑角色: ${role.name}`, icon: 'none' });
};
</script>

<style scoped>
.roles-container { padding: 20rpx; background-color: #f5f7fa; min-height: 100vh; }
.header { margin-bottom: 24rpx; }
.title { font-size: 32rpx; font-weight: 700; color: #333; margin-bottom: 8rpx; display: block; }
.subtitle { font-size: 18rpx; color: #666; display: block; }
.roles-list { display: flex; flex-direction: column; gap: 16rpx; }
.role-item { background: white; border-radius: 20rpx; padding: 24rpx; box-shadow: 0 2rpx 12rpx rgba(0,0,0,0.08); border: 1rpx solid #f0f0f0; display: flex; align-items: flex-start; transition: all 0.3s ease; }
.role-item-hover { transform: translateY(-4rpx); box-shadow: 0 6rpx 20rpx rgba(0,0,0,0.12); border-color: #4facfe; }
.role-icon { font-size: 48rpx; margin-right: 20rpx; }
.role-info { flex: 1; }
.role-name { font-size: 20rpx; font-weight: 600; color: #333; display: block; margin-bottom: 8rpx; }
.role-description { font-size: 14rpx; color: #666; display: block; margin-bottom: 12rpx; }
.role-stats { display: flex; gap: 16rpx; }
.stat { font-size: 14rpx; color: #999; background-color: #f5f5f5; padding: 4rpx 12rpx; border-radius: 8rpx; }
.role-actions { flex-shrink: 0; }
.action-btn { padding: 12rpx 24rpx; background-color: #4facfe; color: white; border: none; border-radius: 12rpx; font-size: 14rpx; }
</style>