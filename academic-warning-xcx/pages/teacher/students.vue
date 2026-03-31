<template>
  <view class="students-container">
    <view class="header">
      <text class="title">学生管理</text>
      <text class="subtitle">管理班级学生信息</text>
    </view>

    <view class="filter-section">
      <view class="search-box">
        <input class="search-input" type="text" v-model="searchKeyword" placeholder="搜索学生姓名或学号" @confirm="handleSearch" />
        <button class="search-btn" @click="handleSearch">搜索</button>
      </view>
      <picker :range="classOptions" :value="selectedClassIndex" @change="handleClassChange" class="class-picker">
        <view class="picker-label">
          <text class="picker-text">{{ selectedClass }}</text>
          <text class="picker-arrow">▼</text>
        </view>
      </picker>
    </view>

    <view class="students-list">
      <view v-if="filteredStudents.length > 0" class="students-content">
        <view v-for="student in filteredStudents" :key="student.id" class="student-item" hover-class="student-item-hover">
          <view class="student-avatar">
            <text class="avatar-text">{{ student.name.charAt(0) }}</text>
          </view>
          <view class="student-info">
            <text class="student-name">{{ student.name }}</text>
            <text class="student-id">学号: {{ student.id }}</text>
            <view class="student-meta">
              <text class="meta-item">班级: {{ student.class }}</text>
              <text class="meta-item">专业: {{ student.major }}</text>
            </view>
          </view>
          <view class="student-actions">
            <button class="action-btn view-btn" @click="viewStudent(student)">查看</button>
            <button class="action-btn warning-btn" @click="addWarning(student)">预警</button>
          </view>
        </view>
      </view>
      <view v-else class="empty-state">
        <view class="empty-icon">👨‍🎓</view>
        <text>暂无学生数据</text>
      </view>
    </view>
  </view>
</template>

<script setup>
import { ref, computed } from 'vue';

const searchKeyword = ref('');
const selectedClass = ref('全部班级');
const selectedClassIndex = ref(0);
const classOptions = ['全部班级', '计算机1班', '计算机2班', '软件工程1班', '软件工程2班'];

const students = ref([
  { id: '2021001', name: '张三', class: '计算机1班', major: '计算机科学与技术' },
  { id: '2021002', name: '李四', class: '计算机1班', major: '计算机科学与技术' },
  { id: '2021003', name: '王五', class: '计算机2班', major: '计算机科学与技术' },
  { id: '2021004', name: '赵六', class: '软件工程1班', major: '软件工程' },
  { id: '2021005', name: '钱七', class: '软件工程2班', major: '软件工程' }
]);

const filteredStudents = computed(() => {
  let result = students.value;
  if (selectedClass.value !== '全部班级') {
    result = result.filter(s => s.class === selectedClass.value);
  }
  if (searchKeyword.value) {
    const keyword = searchKeyword.value.toLowerCase();
    result = result.filter(s => s.name.toLowerCase().includes(keyword) || s.id.toLowerCase().includes(keyword));
  }
  return result;
});

const handleSearch = () => {
  console.log('搜索:', searchKeyword.value);
};

const handleClassChange = (e) => {
  selectedClassIndex.value = e.detail.value;
  selectedClass.value = classOptions[e.detail.value];
};

const viewStudent = (student) => {
  uni.showToast({ title: `查看学生: ${student.name}`, icon: 'none' });
};

const addWarning = (student) => {
  uni.showToast({ title: `为学生 ${student.name} 添加预警`, icon: 'none' });
};
</script>

<style scoped>
.students-container { padding: 20rpx; background-color: #f5f7fa; min-height: 100vh; }
.header { margin-bottom: 24rpx; }
.title { font-size: 32rpx; font-weight: 700; color: #333; margin-bottom: 8rpx; display: block; }
.subtitle { font-size: 18rpx; color: #666; display: block; }
.filter-section { background: white; border-radius: 20rpx; padding: 24rpx; margin-bottom: 24rpx; box-shadow: 0 2rpx 12rpx rgba(0,0,0,0.08); border: 1rpx solid #f0f0f0; }
.search-box { display: flex; gap: 16rpx; margin-bottom: 20rpx; }
.search-input { flex: 1; height: 80rpx; padding: 0 24rpx; border: 1rpx solid #e0e0e0; border-radius: 12rpx; font-size: 16rpx; background-color: #f9f9f9; }
.search-btn { width: 120rpx; height: 80rpx; background-color: #4facfe; color: white; border: none; border-radius: 12rpx; font-size: 16rpx; font-weight: 500; }
.class-picker { width: 100%; }
.picker-label { display: flex; justify-content: space-between; align-items: center; padding: 20rpx 24rpx; background-color: #f9f9f9; border: 1rpx solid #e0e0e0; border-radius: 12rpx; }
.picker-text { font-size: 16rpx; color: #333; }
.picker-arrow { font-size: 14rpx; color: #999; }
.students-list { margin-bottom: 24rpx; }
.students-content { display: flex; flex-direction: column; gap: 16rpx; }
.student-item { background: white; border-radius: 20rpx; padding: 24rpx; box-shadow: 0 2rpx 12rpx rgba(0,0,0,0.08); border: 1rpx solid #f0f0f0; display: flex; align-items: center; transition: all 0.3s ease; }
.student-item-hover { transform: translateY(-4rpx); box-shadow: 0 6rpx 20rpx rgba(0,0,0,0.12); border-color: #4facfe; }
.student-avatar { width: 80rpx; height: 80rpx; border-radius: 50%; background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%); display: flex; align-items: center; justify-content: center; margin-right: 20rpx; flex-shrink: 0; }
.avatar-text { font-size: 32rpx; font-weight: 700; color: white; }
.student-info { flex: 1; }
.student-name { font-size: 20rpx; font-weight: 600; color: #333; display: block; margin-bottom: 4rpx; }
.student-id { font-size: 14rpx; color: #666; display: block; margin-bottom: 8rpx; }
.student-meta { display: flex; gap: 16rpx; }
.meta-item { font-size: 14rpx; color: #999; background-color: #f5f5f5; padding: 4rpx 12rpx; border-radius: 8rpx; }
.student-actions { display: flex; flex-direction: column; gap: 8rpx; flex-shrink: 0; }
.action-btn { padding: 8rpx 16rpx; border: none; border-radius: 8rpx; font-size: 14rpx; font-weight: 500; }
.view-btn { background-color: #e3f2fd; color: #1976d2; }
.warning-btn { background-color: #fff3cd; color: #856404; }
.empty-state { text-align: center; padding: 60rpx 0; color: #999; font-size: 16rpx; background-color: #fff; border-radius: 20rpx; box-shadow: 0 2rpx 12rpx rgba(0,0,0,0.08); border: 1rpx solid #f0f0f0; }
.empty-icon { font-size: 60rpx; margin-bottom: 16rpx; display: block; }
</style>