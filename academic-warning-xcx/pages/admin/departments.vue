<template>
  <view class="departments-container">
    <view class="header">
      <text class="title">部门管理</text>
      <text class="subtitle">管理学院和部门信息</text>
    </view>

    <view class="departments-list">
      <view v-for="dept in departments" :key="dept.id" class="dept-item" hover-class="dept-item-hover">
        <view class="dept-icon">🏢</view>
        <view class="dept-info">
          <text class="dept-name">{{ dept.name }}</text>
          <text class="dept-code">代码: {{ dept.code }}</text>
          <view class="dept-stats">
            <text class="stat">教师: {{ dept.teacherCount }}人</text>
            <text class="stat">学生: {{ dept.studentCount }}人</text>
            <text class="stat">专业: {{ dept.majorCount }}个</text>
          </view>
        </view>
        <view class="dept-actions">
          <button class="action-btn" @click="editDept(dept)">编辑</button>
        </view>
      </view>
    </view>
  </view>
</template>

<script setup>
import { ref } from 'vue';

const departments = ref([
  { id: 1, name: '计算机学院', code: 'CS', teacherCount: 80, studentCount: 1200, majorCount: 5 },
  { id: 2, name: '电子信息学院', code: 'EE', teacherCount: 60, studentCount: 900, majorCount: 4 },
  { id: 3, name: '机械工程学院', code: 'ME', teacherCount: 70, studentCount: 800, majorCount: 6 },
  { id: 4, name: '经济管理学院', code: 'EM', teacherCount: 50, studentCount: 1500, majorCount: 8 },
  { id: 5, name: '外国语学院', code: 'FL', teacherCount: 40, studentCount: 600, majorCount: 3 }
]);

const editDept = (dept) => {
  uni.showToast({ title: `编辑部门: ${dept.name}`, icon: 'none' });
};
</script>

<style scoped>
.departments-container { padding: 20rpx; background-color: #f5f7fa; min-height: 100vh; }
.header { margin-bottom: 24rpx; }
.title { font-size: 32rpx; font-weight: 700; color: #333; margin-bottom: 8rpx; display: block; }
.subtitle { font-size: 18rpx; color: #666; display: block; }
.departments-list { display: flex; flex-direction: column; gap: 16rpx; }
.dept-item { background: white; border-radius: 20rpx; padding: 24rpx; box-shadow: 0 2rpx 12rpx rgba(0,0,0,0.08); border: 1rpx solid #f0f0f0; display: flex; align-items: flex-start; transition: all 0.3s ease; }
.dept-item-hover { transform: translateY(-4rpx); box-shadow: 0 6rpx 20rpx rgba(0,0,0,0.12); border-color: #4facfe; }
.dept-icon { font-size: 48rpx; margin-right: 20rpx; }
.dept-info { flex: 1; }
.dept-name { font-size: 20rpx; font-weight: 600; color: #333; display: block; margin-bottom: 4rpx; }
.dept-code { font-size: 14rpx; color: #999; display: block; margin-bottom: 12rpx; }
.dept-stats { display: flex; gap: 12rpx; flex-wrap: wrap; }
.stat { font-size: 14rpx; color: #666; background-color: #f5f5f5; padding: 4rpx 12rpx; border-radius: 8rpx; }
.dept-actions { flex-shrink: 0; }
.action-btn { padding: 12rpx 24rpx; background-color: #4facfe; color: white; border: none; border-radius: 12rpx; font-size: 14rpx; }
</style>