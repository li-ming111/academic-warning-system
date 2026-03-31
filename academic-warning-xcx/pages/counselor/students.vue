<template>
  <view class="students-container">
    <view class="header">
      <text class="title">学生管理</text>
      <text class="subtitle">管理辅导员负责的学生</text>
    </view>

    <view class="filter-section">
      <view class="search-box">
        <input class="search-input" type="text" v-model="searchKeyword" placeholder="搜索学生姓名或学号" @confirm="handleSearch" />
        <button class="search-btn" @click="handleSearch">搜索</button>
      </view>
      <view class="filter-row">
        <picker :range="classOptions" :value="selectedClassIndex" @change="handleClassChange" class="filter-picker">
          <view class="picker-label">
            <text class="picker-text">{{ selectedClass }}</text>
            <text class="picker-arrow">▼</text>
          </view>
        </picker>
        <picker :range="statusOptions" :value="selectedStatusIndex" @change="handleStatusChange" class="filter-picker">
          <view class="picker-label">
            <text class="picker-text">{{ selectedStatus }}</text>
            <text class="picker-arrow">▼</text>
          </view>
        </picker>
      </view>
    </view>

    <view class="students-list">
      <view v-if="filteredStudents.length > 0" class="students-content">
        <view v-for="student in filteredStudents" :key="student.id" class="student-item" hover-class="student-item-hover">
          <view class="student-avatar">
            <text class="avatar-text">{{ student.name.charAt(0) }}</text>
          </view>
          <view class="student-info">
            <view class="student-header">
              <text class="student-name">{{ student.name }}</text>
              <text :class="['student-status', student.status]">{{ getStatusText(student.status) }}</text>
            </view>
            <text class="student-id">学号: {{ student.id }}</text>
            <view class="student-meta">
              <text class="meta-item">班级: {{ student.class }}</text>
              <text class="meta-item">专业: {{ student.major }}</text>
            </view>
            <view class="student-tags">
              <text v-if="student.hasWarning" class="tag warning-tag">有预警</text>
              <text v-if="student.hasAssistance" class="tag assistance-tag">帮扶中</text>
            </view>
          </view>
          <view class="student-actions">
            <button class="action-btn view-btn" @click="viewStudent(student)">查看</button>
            <button class="action-btn contact-btn" @click="contactStudent(student)">联系</button>
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
const selectedStatus = ref('全部状态');
const selectedStatusIndex = ref(0);

const classOptions = ['全部班级', '计算机1班', '计算机2班', '软件工程1班', '软件工程2班'];
const statusOptions = ['全部状态', '正常', '预警', '帮扶中'];

const students = ref([
  { id: '2021001', name: '张三', class: '计算机1班', major: '计算机科学与技术', status: 'warning', hasWarning: true, hasAssistance: false },
  { id: '2021002', name: '李四', class: '计算机1班', major: '计算机科学与技术', status: 'normal', hasWarning: false, hasAssistance: false },
  { id: '2021003', name: '王五', class: '计算机2班', major: '计算机科学与技术', status: 'assistance', hasWarning: true, hasAssistance: true },
  { id: '2021004', name: '赵六', class: '软件工程1班', major: '软件工程', status: 'normal', hasWarning: false, hasAssistance: false },
  { id: '2021005', name: '钱七', class: '软件工程2班', major: '软件工程', status: 'warning', hasWarning: true, hasAssistance: false }
]);

const filteredStudents = computed(() => {
  let result = students.value;
  if (selectedClass.value !== '全部班级') {
    result = result.filter(s => s.class === selectedClass.value);
  }
  if (selectedStatus.value !== '全部状态') {
    const statusMap = { '正常': 'normal', '预警': 'warning', '帮扶中': 'assistance' };
    result = result.filter(s => s.status === statusMap[selectedStatus.value]);
  }
  if (searchKeyword.value) {
    const keyword = searchKeyword.value.toLowerCase();
    result = result.filter(s => s.name.toLowerCase().includes(keyword) || s.id.toLowerCase().includes(keyword));
  }
  return result;
});

const getStatusText = (status) => {
  const map = { normal: '正常', warning: '预警', assistance: '帮扶中' };
  return map[status] || status;
};

const handleSearch = () => {
  console.log('搜索:', searchKeyword.value);
};

const handleClassChange = (e) => {
  selectedClassIndex.value = e.detail.value;
  selectedClass.value = classOptions[e.detail.value];
};

const handleStatusChange = (e) => {
  selectedStatusIndex.value = e.detail.value;
  selectedStatus.value = statusOptions[e.detail.value];
};

const viewStudent = (student) => {
  uni.showToast({ title: `查看学生: ${student.name}`, icon: 'none' });
};

const contactStudent = (student) => {
  uni.showToast({ title: `联系学生: ${student.name}`, icon: 'none' });
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
.filter-row { display: flex; gap: 16rpx; }
.filter-picker { flex: 1; }
.picker-label { display: flex; justify-content: space-between; align-items: center; padding: 20rpx 24rpx; background-color: #f9f9f9; border: 1rpx solid #e0e0e0; border-radius: 12rpx; }
.picker-text { font-size: 16rpx; color: #333; }
.picker-arrow { font-size: 14rpx; color: #999; }
.students-list { margin-bottom: 24rpx; }
.students-content { display: flex; flex-direction: column; gap: 16rpx; }
.student-item { background: white; border-radius: 20rpx; padding: 24rpx; box-shadow: 0 2rpx 12rpx rgba(0,0,0,0.08); border: 1rpx solid #f0f0f0; display: flex; align-items: flex-start; transition: all 0.3s ease; }
.student-item-hover { transform: translateY(-4rpx); box-shadow: 0 6rpx 20rpx rgba(0,0,0,0.12); border-color: #4facfe; }
.student-avatar { width: 80rpx; height: 80rpx; border-radius: 50%; background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%); display: flex; align-items: center; justify-content: center; margin-right: 20rpx; flex-shrink: 0; }
.avatar-text { font-size: 32rpx; font-weight: 700; color: white; }
.student-info { flex: 1; margin-right: 16rpx; }
.student-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 4rpx; }
.student-name { font-size: 20rpx; font-weight: 600; color: #333; }
.student-status { padding: 4rpx 12rpx; border-radius: 12rpx; font-size: 12rpx; font-weight: 500; }
.student-status.normal { background-color: #d4edda; color: #155724; }
.student-status.warning { background-color: #fff3cd; color: #856404; }
.student-status.assistance { background-color: #f8d7da; color: #721c24; }
.student-id { font-size: 14rpx; color: #666; display: block; margin-bottom: 8rpx; }
.student-meta { display: flex; gap: 16rpx; margin-bottom: 12rpx; }
.meta-item { font-size: 14rpx; color: #999; }
.student-tags { display: flex; gap: 8rpx; }
.tag { padding: 4rpx 12rpx; border-radius: 8rpx; font-size: 12rpx; font-weight: 500; }
.warning-tag { background-color: #fff3cd; color: #856404; }
.assistance-tag { background-color: #d4edda; color: #155724; }
.student-actions { display: flex; flex-direction: column; gap: 8rpx; flex-shrink: 0; }
.action-btn { padding: 8rpx 16rpx; border: none; border-radius: 8rpx; font-size: 14rpx; font-weight: 500; }
.view-btn { background-color: #e3f2fd; color: #1976d2; }
.contact-btn { background-color: #e8f5e9; color: #4caf50; }
.empty-state { text-align: center; padding: 60rpx 0; color: #999; font-size: 16rpx; background-color: #fff; border-radius: 20rpx; box-shadow: 0 2rpx 12rpx rgba(0,0,0,0.08); border: 1rpx solid #f0f0f0; }
.empty-icon { font-size: 60rpx; margin-bottom: 16rpx; display: block; }
</style>