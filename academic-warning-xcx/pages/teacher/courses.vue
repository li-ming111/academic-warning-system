<template>
  <view class="courses-container">
    <view class="header">
      <text class="title">课程管理</text>
      <text class="subtitle">管理您的教学课程</text>
    </view>

    <view class="courses-list">
      <view v-for="course in courses" :key="course.id" class="course-item" hover-class="course-item-hover">
        <view class="course-icon">📚</view>
        <view class="course-info">
          <text class="course-name">{{ course.name }}</text>
          <view class="course-meta">
            <text class="meta-item">{{ course.class }}</text>
            <text class="meta-item">{{ course.students }}人</text>
            <text class="meta-item">{{ course.semester }}</text>
          </view>
          <view class="course-schedule">
            <text class="schedule-item">{{ course.schedule }}</text>
            <text class="schedule-item">{{ course.classroom }}</text>
          </view>
        </view>
        <view class="course-actions">
          <button class="action-btn" @click="viewCourse(course)">查看</button>
        </view>
      </view>
    </view>

    <view class="add-section">
      <button class="add-btn" @click="addCourse">+ 添加课程</button>
    </view>
  </view>
</template>

<script setup>
import { ref } from 'vue';

const courses = ref([
  {
    id: 1,
    name: '高等数学',
    class: '计算机1班',
    students: 45,
    semester: '2025-2026学年第一学期',
    schedule: '周一 8:00-9:40',
    classroom: '教学楼A101'
  },
  {
    id: 2,
    name: '高等数学',
    class: '计算机2班',
    students: 42,
    semester: '2025-2026学年第一学期',
    schedule: '周二 8:00-9:40',
    classroom: '教学楼A102'
  },
  {
    id: 3,
    name: '线性代数',
    class: '软件工程1班',
    students: 38,
    semester: '2025-2026学年第一学期',
    schedule: '周三 10:00-11:40',
    classroom: '教学楼B201'
  },
  {
    id: 4,
    name: '概率论与数理统计',
    class: '计算机1班',
    students: 45,
    semester: '2025-2026学年第一学期',
    schedule: '周四 14:00-15:40',
    classroom: '教学楼A103'
  }
]);

const viewCourse = (course) => {
  uni.showToast({ title: `查看课程: ${course.name}`, icon: 'none' });
};

const addCourse = () => {
  uni.showToast({ title: '添加新课程', icon: 'none' });
};
</script>

<style scoped>
.courses-container { padding: 20rpx; background-color: #f5f7fa; min-height: 100vh; }
.header { margin-bottom: 24rpx; }
.title { font-size: 32rpx; font-weight: 700; color: #333; margin-bottom: 8rpx; display: block; }
.subtitle { font-size: 18rpx; color: #666; display: block; }
.courses-list { display: flex; flex-direction: column; gap: 16rpx; margin-bottom: 24rpx; }
.course-item { background: white; border-radius: 20rpx; padding: 24rpx; box-shadow: 0 2rpx 12rpx rgba(0,0,0,0.08); border: 1rpx solid #f0f0f0; display: flex; align-items: flex-start; transition: all 0.3s ease; }
.course-item-hover { transform: translateY(-4rpx); box-shadow: 0 6rpx 20rpx rgba(0,0,0,0.12); border-color: #4facfe; }
.course-icon { font-size: 48rpx; margin-right: 20rpx; flex-shrink: 0; }
.course-info { flex: 1; }
.course-name { font-size: 20rpx; font-weight: 600; color: #333; display: block; margin-bottom: 12rpx; }
.course-meta { display: flex; gap: 12rpx; margin-bottom: 8rpx; flex-wrap: wrap; }
.meta-item { font-size: 14rpx; color: #666; background-color: #f5f5f5; padding: 4rpx 12rpx; border-radius: 8rpx; }
.course-schedule { display: flex; gap: 12rpx; flex-wrap: wrap; }
.schedule-item { font-size: 14rpx; color: #999; }
.course-actions { flex-shrink: 0; }
.action-btn { padding: 12rpx 24rpx; background-color: #4facfe; color: white; border: none; border-radius: 12rpx; font-size: 14rpx; font-weight: 500; }
.add-section { margin-top: 40rpx; }
.add-btn { width: 100%; height: 90rpx; background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%); color: white; border: none; border-radius: 16rpx; font-size: 18rpx; font-weight: 600; }
</style>